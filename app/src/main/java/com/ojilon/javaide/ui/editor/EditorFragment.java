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
import com.ojilon.javaide.core.model.SourceFile;

/**
 * Host for the tabbed editor (Task 8).
 * Each tab is an EditorPageFragment bound to a SourceFile.
 * Syntax highlighting is applied inside each page (Task 9).
 */
public final class EditorFragment extends Fragment {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private EditorPagerAdapter adapter;

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

        adapter = new EditorPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String fileId = adapter.getFileId(position);
            SourceFile file = CoreBridge.getDocument(fileId);
            tab.setText(file != null ? file.getName() : "?");
        }).attach();

        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        // Start with one empty document
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
        // Ask the current page fragment to save
        Fragment page = getChildFragmentManager()
                .findFragmentByTag("f" + adapter.getItemId(pos));
        // ViewPager2 tag is not always reliable; fall back to saving via CoreBridge
        String fileId = adapter.getFileId(pos);
        // We need the text from the visible page. Simplest reliable way:
        // iterate and find the active EditorPageFragment.
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof EditorPageFragment && f.isVisible()) {
                ((EditorPageFragment) f).save();
                SourceFile updated = CoreBridge.getDocument(fileId);
                String name = updated != null ? updated.getName() : fileId;
                Toast.makeText(requireContext(), "Saved: " + name, Toast.LENGTH_SHORT).show();
                // Refresh tab title
                TabLayout.Tab tab = tabLayout.getTabAt(pos);
                if (tab != null && updated != null) {
                    tab.setText(updated.getName());
                }
                return;
            }
        }
        Toast.makeText(requireContext(), "Could not find editor page", Toast.LENGTH_SHORT).show();
    }

    private void closeCurrent() {
        int pos = viewPager.getCurrentItem();
        if (pos < 0 || pos >= adapter.getTabCount()) return;

        String fileId = adapter.getFileId(pos);
        CoreBridge.closeDocument(fileId);
        adapter.removeTab(pos);

        if (adapter.getTabCount() == 0) {
            openNew(); // always keep at least one tab
        }
    }
}
