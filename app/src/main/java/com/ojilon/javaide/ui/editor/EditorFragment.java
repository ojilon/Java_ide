package com.ojilon.javaide.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.ojilon.javaide.R;
import com.ojilon.javaide.core.CoreBridge;
import com.ojilon.javaide.core.model.SourceFile;

/**
 * Basic code editor UI (Task 6) + open/save stubs (Task 7).
 * Classic OOP Android layer; all document state lives in :core.
 */
public final class EditorFragment extends Fragment {

    private static final String ARG_FILE_ID = "file_id";

    private EditText codeEditor;
    private TextView fileNameLabel;
    private MaterialToolbar toolbar;

    @Nullable
    private String currentFileId;

    @NonNull
    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.editorToolbar);
        fileNameLabel = view.findViewById(R.id.fileNameLabel);
        codeEditor = view.findViewById(R.id.codeEditor);

        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        // Start with a fresh document
        openNew();
    }

    private boolean onMenuItemClick(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            openNew();
            return true;
        } else if (id == R.id.action_open) {
            // Stub: open a sample file
            openSample();
            return true;
        } else if (id == R.id.action_save) {
            saveCurrent();
            return true;
        }
        return false;
    }

    private void openNew() {
        SourceFile file = CoreBridge.openNewDocument();
        bindFile(file);
        Toast.makeText(requireContext(), "New file created", Toast.LENGTH_SHORT).show();
    }

    private void openSample() {
        String sample = ""
                + "public class Hello {\n"
                + "    public static void main(String[] args) {\n"
                + "        System.out.println(\"Hello from Java IDE\");\n"
                + "    }\n"
                + "}\n";
        SourceFile file = CoreBridge.openDocument("Hello.java", sample);
        bindFile(file);
        Toast.makeText(requireContext(), "Opened sample: Hello.java", Toast.LENGTH_SHORT).show();
    }

    private void saveCurrent() {
        if (currentFileId == null) {
            Toast.makeText(requireContext(), "Nothing to save", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = codeEditor.getText() != null ? codeEditor.getText().toString() : "";
        SourceFile updated = CoreBridge.saveDocument(currentFileId, content);
        if (updated != null) {
            fileNameLabel.setText(updated.getName() + " (saved)");
            Toast.makeText(requireContext(), "Saved: " + updated.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Save failed – unknown document", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindFile(@NonNull SourceFile file) {
        currentFileId = file.getId();
        fileNameLabel.setText(file.getName());
        codeEditor.setText(file.getContent());
        toolbar.setTitle(file.getName());
    }
}
