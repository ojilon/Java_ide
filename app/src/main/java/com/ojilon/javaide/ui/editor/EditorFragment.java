package com.ojilon.javaide.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Placeholder for the Java source editor UI (OOP Android layer).
 * Will later host a modern editor view and talk to core for parsing / formatting.
 */
public final class EditorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Layout will be added in a later iteration
        return new View(requireContext());
    }
}
