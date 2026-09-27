package com.ojilon.javaide.ui.editor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of open editor tabs (each bound to a SourceFile id).
 */
public final class EditorPagerAdapter extends FragmentStateAdapter {

    private final List<String> fileIds = new ArrayList<>();

    public EditorPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    public void addTab(@NonNull String fileId) {
        fileIds.add(fileId);
        notifyItemInserted(fileIds.size() - 1);
    }

    public void removeTab(int position) {
        if (position < 0 || position >= fileIds.size()) return;
        fileIds.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    public String getFileId(int position) {
        return fileIds.get(position);
    }

    public int indexOf(@NonNull String fileId) {
        return fileIds.indexOf(fileId);
    }

    public int getTabCount() {
        return fileIds.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return EditorPageFragment.newInstance(fileIds.get(position));
    }

    @Override
    public int getItemCount() {
        return fileIds.size();
    }

    @Override
    public long getItemId(int position) {
        return fileIds.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (String id : fileIds) {
            if (id.hashCode() == itemId) return true;
        }
        return false;
    }
}
