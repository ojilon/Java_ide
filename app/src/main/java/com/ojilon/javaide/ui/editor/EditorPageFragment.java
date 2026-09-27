package com.ojilon.javaide.ui.editor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ojilon.javaide.R;
import com.ojilon.javaide.core.CoreBridge;
import com.ojilon.javaide.core.model.SourceFile;

/**
 * One tab page: an independent editor bound to a single SourceFile.
 */
public final class EditorPageFragment extends Fragment {

    private static final String ARG_FILE_ID = "file_id";

    private EditText codeEditor;
    private String fileId;
    private boolean suppressHighlight;

    @NonNull
    public static EditorPageFragment newInstance(@NonNull String fileId) {
        EditorPageFragment f = new EditorPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_ID, fileId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileId = getArguments().getString(ARG_FILE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        codeEditor = view.findViewById(R.id.codeEditor);

        SourceFile file = fileId != null ? CoreBridge.getDocument(fileId) : null;
        if (file != null) {
            applyHighlighted(file.getContent());
        }

        codeEditor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (suppressHighlight) return;
                // Re-apply highlighting after a short quiet period would be better,
                // but for simplicity we re-highlight on every change for small files.
                String text = s.toString();
                int sel = codeEditor.getSelectionStart();
                applyHighlighted(text);
                if (sel >= 0 && sel <= codeEditor.getText().length()) {
                    codeEditor.setSelection(sel);
                }
            }
        });
    }

    private void applyHighlighted(@NonNull String text) {
        suppressHighlight = true;
        codeEditor.setText(SyntaxHighlighter.highlight(text));
        suppressHighlight = false;
    }

    /** Called by the parent when the user hits Save. */
    public void save() {
        if (fileId == null || codeEditor == null) return;
        String content = codeEditor.getText() != null ? codeEditor.getText().toString() : "";
        CoreBridge.saveDocument(fileId, content);
    }

    @Nullable
    public String getFileId() {
        return fileId;
    }

    @NonNull
    public String getCurrentText() {
        if (codeEditor == null || codeEditor.getText() == null) return "";
        return codeEditor.getText().toString();
    }
}
