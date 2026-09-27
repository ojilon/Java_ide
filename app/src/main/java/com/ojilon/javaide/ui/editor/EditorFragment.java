package com.ojilon.javaide.ui.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ojilon.javaide.R;
import com.ojilon.javaide.core.CoreBridge;
import com.ojilon.javaide.core.compile.Diagnostic;
import com.ojilon.javaide.core.model.SourceFile;
import com.ojilon.javaide.core.run.RunRequest;
import com.ojilon.javaide.core.run.RunResult;
import com.ojilon.javaide.core.run.RunStage;
import com.ojilon.javaide.run.BuildAndRunCoordinator;

/**
 * Host for the tabbed editor + Run action (task 13).
 */
public final class EditorFragment extends Fragment {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private EditorPagerAdapter adapter;
    private BuildAndRunCoordinator runCoordinator;

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
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        runCoordinator = new BuildAndRunCoordinator();

        adapter = new EditorPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String fileId = adapter.getFileId(position);
            SourceFile file = CoreBridge.getDocument(fileId);
            tab.setText(file != null ? file.getName() : "?");
        }).attach();

        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        openNew();
    }

    private boolean onMenuItemClick(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            openNew();
            return true;
        } else if (id == R.id.action_open) {
            openSample();
            return true;
        } else if (id == R.id.action_save) {
            saveCurrent();
            return true;
        } else if (id == R.id.action_run) {
            runCurrent();
            return true;
        } else if (id == R.id.action_close) {
            closeCurrent();
            return true;
        }
        return false;
    }

    private void openNew() {
        SourceFile file = CoreBridge.openNewDocument();
        adapter.addTab(file.getId());
        viewPager.setCurrentItem(adapter.getTabCount() - 1, true);
        Toast.makeText(requireContext(), "New tab: " + file.getName(), Toast.LENGTH_SHORT).show();
    }

    private void openSample() {
        String sample = ""
                + "package demo;\n\n"
                + "/** Sample class for syntax highlighting */\n"
                + "public class Hello {\n"
                + "    public static void main(String[] args) {\n"
                + "        // Print a message\n"
                + "        System.out.println(\"Hello from Java IDE\");\n"
                + "        int answer = 42;\n"
                + "    }\n"
                + "}\n";
        SourceFile file = CoreBridge.openDocument("Hello.java", sample);
        adapter.addTab(file.getId());
        viewPager.setCurrentItem(adapter.getTabCount() - 1, true);
        Toast.makeText(requireContext(), "Opened: Hello.java", Toast.LENGTH_SHORT).show();
    }

    private void saveCurrent() {
        int pos = viewPager.getCurrentItem();
        if (pos < 0 || pos >= adapter.getTabCount()) {
            Toast.makeText(requireContext(), "Nothing to save", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileId = adapter.getFileId(pos);
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof EditorPageFragment && f.isVisible()) {
                ((EditorPageFragment) f).save();
                SourceFile updated = CoreBridge.getDocument(fileId);
                String name = updated != null ? updated.getName() : fileId;
                Toast.makeText(requireContext(), "Saved: " + name, Toast.LENGTH_SHORT).show();
                TabLayout.Tab tab = tabLayout.getTabAt(pos);
                if (tab != null && updated != null) {
                    tab.setText(updated.getName());
                }
                return;
            }
        }
        Toast.makeText(requireContext(), "Could not find editor page", Toast.LENGTH_SHORT).show();
    }

    private void runCurrent() {
        int pos = viewPager.getCurrentItem();
        if (pos < 0 || pos >= adapter.getTabCount()) {
            Toast.makeText(requireContext(), "Nothing to run", Toast.LENGTH_SHORT).show();
            return;
        }

        // Persist current editor text first
        String fileId = adapter.getFileId(pos);
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof EditorPageFragment && f.isVisible()) {
                ((EditorPageFragment) f).save();
                break;
            }
        }

        SourceFile source = CoreBridge.getDocument(fileId);
        if (source == null) {
            Toast.makeText(requireContext(), "Document not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Running…", Toast.LENGTH_SHORT).show();
        toolbar.setSubtitle("Running…");

        runCoordinator.run(requireContext(), RunRequest.of(source), new BuildAndRunCoordinator.Callback() {
            @Override
            public void onStage(@NonNull RunStage stage, @NonNull String detail) {
                if (!isAdded()) return;
                toolbar.setSubtitle(stage + ": " + detail);
            }

            @Override
            public void onFinished(@NonNull RunResult result) {
                if (!isAdded()) return;
                toolbar.setSubtitle(null);
                StringBuilder sb = new StringBuilder(result.getMessage());
                if (!result.getDiagnostics().isEmpty()) {
                    sb.append("\n");
                    for (Diagnostic d : result.getDiagnostics()) {
                        sb.append("\n").append(d.toString());
                    }
                }
                Toast.makeText(requireContext(), sb.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeCurrent() {
        int pos = viewPager.getCurrentItem();
        if (pos < 0 || pos >= adapter.getTabCount()) return;

        String fileId = adapter.getFileId(pos);
        CoreBridge.closeDocument(fileId);
        adapter.removeTab(pos);

        if (adapter.getTabCount() == 0) {
            openNew();
        }
    }
}
