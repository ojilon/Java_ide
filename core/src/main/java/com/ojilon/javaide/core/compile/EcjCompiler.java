package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;

import com.ojilon.javaide.core.model.SourceFile;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compiler backend backed by the Eclipse Compiler for Java (ECJ).
 *
 * Strategy:
 * 1. Write sources to a temporary directory
 * 2. Invoke ECJ BatchCompiler
 * 3. Parse diagnostics from the error stream
 * 4. Collect .class bytes from the output directory
 * 5. Clean up temps
 *
 * Pure-Java sources work best. Compiling against Android APIs requires
 * supplying android.jar on the classpath (future enhancement).
 */
public final class EcjCompiler implements Compiler {

    // ECJ diagnostic line examples:
    //   1. ERROR in /tmp/.../Hello.java (at line 3)
    //   2. WARNING in ...
    private static final Pattern DIAG_HEADER = Pattern.compile(
            "^\\d+\\.\\s+(ERROR|WARNING|INFO|PROBLEM)\\s+in\\s+(.+?)\\s+\\(at line\\s+(\\d+)\\)",
            Pattern.CASE_INSENSITIVE
    );

    @NonNull
    @Override
    public CompileResult compile(@NonNull CompileRequest request) {
        File workDir = null;
        try {
            workDir = Files.createTempDirectory("javaide-ecj-").toFile();
            File srcDir = new File(workDir, "src");
            File outDir = new File(workDir, "out");
            if (!srcDir.mkdirs() || !outDir.mkdirs()) {
                return CompileResult.failure(List.of(
                        Diagnostic.error("Failed to create temp directories", null, -1, -1)
                ));
            }

            List<String> sourcePaths = new ArrayList<>();
            for (SourceFile sf : request.getSources()) {
                File javaFile = new File(srcDir, sf.getName());
                // Ensure parent dirs for package-style names if ever used
                File parent = javaFile.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
                try (FileOutputStream fos = new FileOutputStream(javaFile)) {
                    fos.write(sf.getContent().getBytes(StandardCharsets.UTF_8));
                }
                sourcePaths.add(javaFile.getAbsolutePath());
            }

            CompileOptions opts = request.getOptions();
            StringBuilder cmd = new StringBuilder();
            cmd.append("-d ").append(quote(outDir.getAbsolutePath())).append(' ');
            cmd.append("-source ").append(opts.getSourceVersion()).append(' ');
            cmd.append("-target ").append(opts.getTargetVersion()).append(' ');
            cmd.append("-encoding UTF-8 ");
            cmd.append("-proceedOnError ");
            if (opts.isVerbose()) {
                cmd.append("-verbose ");
            }
            if (!opts.getClasspath().isEmpty()) {
                cmd.append("-classpath ");
                cmd.append(quote(String.join(File.pathSeparator, opts.getClasspath())));
                cmd.append(' ');
            }
            for (String path : sourcePaths) {
                cmd.append(quote(path)).append(' ');
            }

            StringWriter errBuffer = new StringWriter();
            StringWriter outBuffer = new StringWriter();
            PrintWriter errWriter = new PrintWriter(errBuffer);
            PrintWriter outWriter = new PrintWriter(outBuffer);

            boolean ok = BatchCompiler.compile(
                    cmd.toString().trim(),
                    outWriter,
                    errWriter,
                    new CompilationProgress() {
                        @Override public void begin(int remainingWork) {}
                        @Override public void done() {}
                        @Override public boolean isCanceled() { return false; }
                        @Override public void setTaskName(String name) {}
                        @Override public void worked(int workIncrement, int remainingWork) {}
                    }
            );

            errWriter.flush();
            outWriter.flush();

            List<Diagnostic> diagnostics = parseDiagnostics(errBuffer.toString());
            if (opts.isVerbose() && outBuffer.getBuffer().length() > 0) {
                diagnostics.add(Diagnostic.info(outBuffer.toString().trim()));
            }

            List<CompiledClass> classes = collectClasses(outDir, "");

            if (ok && !hasErrors(diagnostics)) {
                return CompileResult.success(classes, diagnostics);
            }
            // Even on failure we may have partial classes; still report failure if errors exist
            if (hasErrors(diagnostics)) {
                return CompileResult.failure(diagnostics);
            }
            // ECJ returned false but no ERROR diagnostics — treat as failure with info
            if (!ok) {
                diagnostics.add(Diagnostic.info("ECJ reported compilation failure"));
                return CompileResult.failure(diagnostics);
            }
            return CompileResult.success(classes, diagnostics);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return CompileResult.failure(List.of(
                    Diagnostic.error("Compiler exception: " + e.getMessage(), null, -1, -1),
                    Diagnostic.info(sw.toString())
            ));
        } finally {
            if (workDir != null) {
                deleteRecursively(workDir);
            }
        }
    }

    private static boolean hasErrors(@NonNull List<Diagnostic> diagnostics) {
        for (Diagnostic d : diagnostics) {
            if (d.getSeverity() == DiagnosticSeverity.ERROR) return true;
        }
        return false;
    }

    @NonNull
    private static String quote(@NonNull String path) {
        if (path.indexOf(' ') >= 0) {
            return '"' + path + '"';
        }
        return path;
    }

    @NonNull
    private static List<Diagnostic> parseDiagnostics(@NonNull String errText) {
        List<Diagnostic> result = new ArrayList<>();
        String[] lines = errText.split("\r?\n");
        DiagnosticSeverity currentSeverity = null;
        String currentFile = null;
        int currentLine = -1;
        StringBuilder message = new StringBuilder();

        for (String line : lines) {
            Matcher m = DIAG_HEADER.matcher(line.trim());
            if (m.find()) {
                // flush previous
                if (currentSeverity != null && message.length() > 0) {
                    result.add(new Diagnostic(
                            currentSeverity,
                            message.toString().trim(),
                            currentFile,
                            currentLine,
                            -1
                    ));
                }
                String sev = m.group(1).toUpperCase(Locale.US);
                currentSeverity = switch (sev) {
                    case "ERROR" -> DiagnosticSeverity.ERROR;
                    case "WARNING" -> DiagnosticSeverity.WARNING;
                    case "INFO" -> DiagnosticSeverity.INFO;
                    default -> DiagnosticSeverity.OTHER;
                };
                currentFile = new File(m.group(2)).getName();
                currentLine = Integer.parseInt(m.group(3));
                message.setLength(0);
            } else if (currentSeverity != null) {
                // continuation / message body (skip source-echo lines that start with digits + | or similar)
                String trimmed = line.trim();
                if (!trimmed.isEmpty()
                        && !trimmed.matches("^\\d+\\s*\\|.*")
                        && !trimmed.matches("^\\s*\\^+")
                        && !trimmed.equalsIgnoreCase("----------")) {
                    if (message.length() > 0) message.append(' ');
                    message.append(trimmed);
                }
            } else if (!line.trim().isEmpty()) {
                // Unparsed noise — keep as info so nothing is lost
                result.add(Diagnostic.info(line.trim()));
            }
        }
        if (currentSeverity != null && message.length() > 0) {
            result.add(new Diagnostic(
                    currentSeverity,
                    message.toString().trim(),
                    currentFile,
                    currentLine,
                    -1
            ));
        }
        return result;
    }

    @NonNull
    private static List<CompiledClass> collectClasses(@NonNull File dir, @NonNull String packagePrefix)
            throws IOException {
        List<CompiledClass> list = new ArrayList<>();
        File[] children = dir.listFiles();
        if (children == null) return list;

        for (File child : children) {
            if (child.isDirectory()) {
                String next = packagePrefix.isEmpty()
                        ? child.getName()
                        : packagePrefix + "." + child.getName();
                list.addAll(collectClasses(child, next));
            } else if (child.getName().endsWith(".class")) {
                String simple = child.getName().substring(0, child.getName().length() - 6);
                String binaryName = packagePrefix.isEmpty() ? simple : packagePrefix + "." + simple;
                byte[] bytes = Files.readAllBytes(child.toPath());
                list.add(new CompiledClass(binaryName, bytes));
            }
        }
        return list;
    }

    private static void deleteRecursively(@NonNull File f) {
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteRecursively(c);
            }
        }
        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }
}
