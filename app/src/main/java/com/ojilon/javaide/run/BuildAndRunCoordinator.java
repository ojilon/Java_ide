package com.ojilon.javaide.run;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ojilon.javaide.core.CoreBridge;
import com.ojilon.javaide.core.compile.CompileResult;
import com.ojilon.javaide.core.compile.Diagnostic;
import com.ojilon.javaide.core.dex.DexRequest;
import com.ojilon.javaide.core.dex.DexResult;
import com.ojilon.javaide.core.model.SourceFile;
import com.ojilon.javaide.core.packaging.PackageRequest;
import com.ojilon.javaide.core.packaging.PackageResult;
import com.ojilon.javaide.core.run.RunRequest;
import com.ojilon.javaide.core.run.RunResult;
import com.ojilon.javaide.core.run.RunStage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Orchestrates: compile → dex → package → install → launch.
 * Heavy work runs off the main thread; callbacks are delivered on the main thread.
 */
public final class BuildAndRunCoordinator {

    public interface Callback {
        void onStage(@NonNull RunStage stage, @NonNull String detail);
        void onFinished(@NonNull RunResult result);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void run(@NonNull Context context,
                    @NonNull RunRequest request,
                    @NonNull Callback callback) {
        Context appContext = context.getApplicationContext();

        executor.execute(() -> {
            List<Diagnostic> allDiagnostics = new ArrayList<>();

            // 1. Compile
            postStage(callback, RunStage.COMPILE, "Compiling " + request.getSource().getName());
            CompileResult compileResult = CoreBridge.compile(request.getSource());
            allDiagnostics.addAll(compileResult.getDiagnostics());
            if (!compileResult.isSuccess()) {
                postResult(callback, RunResult.fail(
                        RunStage.COMPILE,
                        "Compilation failed",
                        allDiagnostics
                ));
                return;
            }
            if (compileResult.getClasses().isEmpty()) {
                postResult(callback, RunResult.fail(
                        RunStage.COMPILE,
                        "Compilation produced no class files",
                        allDiagnostics
                ));
                return;
            }

            // 2. Dex
            postStage(callback, RunStage.DEX, "Dexing " + compileResult.getClasses().size() + " class(es)");
            DexResult dexResult = CoreBridge.dex(DexRequest.of(compileResult.getClasses()));
            allDiagnostics.addAll(dexResult.getDiagnostics());
            if (!dexResult.isSuccess() || dexResult.getDexBytes() == null) {
                postResult(callback, RunResult.fail(
                        RunStage.DEX,
                        "Dexing not available yet (stub). Compile succeeded with "
                                + compileResult.getClasses().size() + " class(es).",
                        allDiagnostics
                ));
                return;
            }

            // 3. Package
            postStage(callback, RunStage.PACKAGE, "Packaging APK");
            PackageResult pkgResult = CoreBridge.packageApk(
                    PackageRequest.of(dexResult.getDexBytes(), request.getPackageOptions())
            );
            allDiagnostics.addAll(pkgResult.getDiagnostics());
            if (!pkgResult.isSuccess() || pkgResult.getApkBytes() == null) {
                postResult(callback, RunResult.fail(
                        RunStage.PACKAGE,
                        "Packaging not available yet (stub).",
                        allDiagnostics
                ));
                return;
            }

            // 4. Install
            postStage(callback, RunStage.INSTALL, "Installing APK");
            try {
                InstallResultReceiver.setListener((success, message, packageName) -> {
                    if (!success) {
                        postResult(callback, RunResult.fail(RunStage.INSTALL, message, allDiagnostics));
                        return;
                    }
                    // 5. Launch
                    postStage(callback, RunStage.LAUNCH, "Launching " + packageName);
                    boolean launched = packageName != null
                            && ApkInstaller.launch(appContext, packageName);
                    if (launched) {
                        postResult(callback, RunResult.ok("Running " + packageName));
                    } else {
                        postResult(callback, RunResult.fail(
                                RunStage.LAUNCH,
                                "Installed but could not launch " + packageName,
                                allDiagnostics
                        ));
                    }
                });

                ApkInstaller.install(
                        appContext,
                        pkgResult.getApkBytes(),
                        "generated.apk"
                );
            } catch (Exception e) {
                postResult(callback, RunResult.fail(
                        RunStage.INSTALL,
                        "Install error: " + e.getMessage(),
                        allDiagnostics
                ));
            }
        });
    }

    private void postStage(Callback cb, RunStage stage, String detail) {
        mainHandler.post(() -> cb.onStage(stage, detail));
    }

    private void postResult(Callback cb, RunResult result) {
        mainHandler.post(() -> cb.onFinished(result));
    }
}
