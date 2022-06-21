package com.raival.fileexplorer.tabs.file.observer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.raival.fileexplorer.tabs.file.FileExplorerTabFragment;
import com.raival.fileexplorer.tabs.file.adapter.FileListAdapter;

public class FileListObserver extends RecyclerView.AdapterDataObserver {
    private FileExplorerTabFragment parentFragment;
    private FileListAdapter fileListAdapter;

    public FileListObserver(FileExplorerTabFragment fragment, FileListAdapter adapter) {
        parentFragment = fragment;
        fileListAdapter = adapter;
        checkIfEmpty();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        parentFragment.showPlaceholder(fileListAdapter != null && fileListAdapter.getItemCount() == 0);
    }
}