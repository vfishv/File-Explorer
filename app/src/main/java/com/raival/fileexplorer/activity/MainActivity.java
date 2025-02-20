package com.raival.fileexplorer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.raival.fileexplorer.App;
import com.raival.fileexplorer.R;
import com.raival.fileexplorer.activity.model.MainViewModel;
import com.raival.fileexplorer.common.view.BottomBarView;
import com.raival.fileexplorer.common.view.TabView;
import com.raival.fileexplorer.tab.BaseDataHolder;
import com.raival.fileexplorer.tab.BaseTabFragment;
import com.raival.fileexplorer.tab.checklist.ChecklistTabDataHolder;
import com.raival.fileexplorer.tab.checklist.ChecklistTabFragment;
import com.raival.fileexplorer.tab.file.FileExplorerTabDataHolder;
import com.raival.fileexplorer.tab.file.FileExplorerTabFragment;
import com.raival.fileexplorer.util.FileUtils;
import com.raival.fileexplorer.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private boolean confirmExit = false;
    private TabView tabView;
    private FragmentContainerView fragmentContainerView;
    private MaterialToolbar toolbar;
    private BottomBarView bottomBarView;
    private MainViewModel mainViewModel;

    /**
     * Called after read & write permissions are granted
     */
    @Override
    public void init() {
        if (getTabFragments().isEmpty()) {
            loadDefaultTab();
        } else {
            fragmentContainerView.post(this::restoreTabs);
        }
    }

    private List<BaseTabFragment> getTabFragments() {
        List<BaseTabFragment> list = new ArrayList<>();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseTabFragment) {
                list.add((BaseTabFragment) fragment);
            }
        }
        return list;
    }

    private MainViewModel getMainViewModel() {
        if (mainViewModel == null) {
            mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        }
        return mainViewModel;
    }

    private void restoreTabs() {
        final String activeFragmentTag = getActiveFragment().getTag();

        for (int i = 0; i < getMainViewModel().getDataHolders().size(); i++) {
            BaseDataHolder dataHolder = getMainViewModel().getDataHolders().get(i);
            // The active fragment will create its own TabView, so we skip it
            if (!dataHolder.getTag().equals(activeFragmentTag)) {
                if (dataHolder instanceof FileExplorerTabDataHolder) {
                    tabView.insertNewTabAt(i, dataHolder.getTag(), false)
                            .setName(FileUtils.getShortLabel(((FileExplorerTabDataHolder) dataHolder).activeDirectory, FileExplorerTabFragment.MAX_NAME_LENGTH));
                } else if (dataHolder instanceof ChecklistTabDataHolder) {
                    tabView.insertNewTabAt(i, dataHolder.getTag(), false)
                            .setName(FileUtils.getShortLabel(((ChecklistTabDataHolder) dataHolder).file, FileExplorerTabFragment.MAX_NAME_LENGTH));
                }
                // handle other types of DataHolders here
            }
        }
    }

    private void loadDefaultTab() {
        getSupportFragmentManager().beginTransaction()
                // This fragment cannot be deleted, and its tag is unique (starts with "0_")
                .replace(R.id.fragment_container, new FileExplorerTabFragment(), "0_FileExplorerTabFragment_" + generateRandomTag())
                .setReorderingAllowed(true)
                .commit();
    }

    public String generateRandomTag() {
        return TextUtils.getRandomString(16);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tabView = findViewById(R.id.tabs);
        fragmentContainerView = findViewById(R.id.fragment_container);
        toolbar = findViewById(R.id.toolbar);
        bottomBarView = findViewById(R.id.bottom_bar_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_menu_24);
        toolbar.setNavigationOnClickListener(null);

        tabView.setOnUpdateTabViewListener((tab, event) -> {
            if (event == TabView.ON_SELECT) {
                if (tab.tag.startsWith("FileExplorerTabFragment_") || tab.tag.startsWith("0_FileExplorerTabFragment_")) {
                    if (!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getTag().equals(tab.tag)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FileExplorerTabFragment(), tab.tag)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                }
                if (tab.tag.startsWith("ChecklistTabFragment_")) {
                    if (!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getTag().equals(tab.tag)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ChecklistTabFragment(), tab.tag)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                }
                // Handle other types of tabs here...
            } else if (event == TabView.ON_LONG_CLICK) {
                PopupMenu popupMenu = new PopupMenu(this, tab.view);
                popupMenu.inflate(R.menu.tab_menu);
                // Default tab is unclosable
                if (tab.tag.startsWith("0_")) {
                    popupMenu.getMenu().findItem(R.id.close).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.close_all).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.close) {
                        BaseTabFragment activeFragment = getActiveFragment();
                        if (tab.tag.equals(activeFragment.getTag())) {
                            activeFragment.closeTab();
                        } else {
                            getMainViewModel().getDataHolders().removeIf(dataHolder1 -> dataHolder1.getTag().equals(tab.tag));
                            closeTab(tab.tag);
                        }
                        return true;
                    } else if (item.getItemId() == R.id.close_all) {
                        BaseTabFragment activeFragment = getActiveFragment();
                        // Remove unselected tabs
                        for (String tag : tabView.getTags()) {
                            if (!tag.startsWith("0_") && !tag.equals(activeFragment.getTag())) {
                                getMainViewModel().getDataHolders().removeIf(dataHolder1 -> dataHolder1.getTag().equals(tag));
                                closeTab(tag);
                            }
                        }
                        // Remove the active tab
                        activeFragment.closeTab();
                        return true;
                    } else if (item.getItemId() == R.id.close_others) {
                        BaseTabFragment activeFragment = getActiveFragment();
                        for (String tag : tabView.getTags()) {
                            if (!tag.startsWith("0_") && !tag.equals(activeFragment.getTag()) && !tag.equals(tab.tag)) {
                                getMainViewModel().getDataHolders().removeIf(dataHolder1 -> dataHolder1.getTag().equals(tag));
                                closeTab(tag);
                            }
                        }
                        if (!activeFragment.getTag().equals(tab.tag)) activeFragment.closeTab();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        findViewById(R.id.tabs_options).setOnClickListener(view -> {
            addNewTab(new FileExplorerTabFragment(), "FileExplorerTabFragment_" + generateRandomTag());
        });

        checkPermissions();
    }

    private BaseTabFragment getActiveFragment() {
        return (BaseTabFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    public void addNewTab(BaseTabFragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final String title = item.getTitle().toString();
        if (title.equals("Logs")) {
            showLogFile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogFile() {
        final File logFile = new File(getExternalFilesDir(null).getAbsolutePath() + "/debug/log.txt");
        if (logFile.exists() && logFile.isFile()) {
            Intent intent = new Intent();
            intent.setClass(this, TextEditorActivity.class);
            intent.putExtra("file", logFile.getAbsolutePath());
            startActivity(intent);
            return;
        }
        App.showMsg("No logs found");
    }

    @Override
    public void onBackPressed() {
        if (getActiveFragment().onBackPressed()) {
            return;
        }
        if (!confirmExit) {
            confirmExit = true;
            App.showMsg("Press again to exit");
            App.appHandler.postDelayed(() -> confirmExit = false, 2000);
            return;
        }
        super.onBackPressed();
    }

    public MaterialToolbar getToolbar() {
        return toolbar;
    }

    public BottomBarView getBottomBarView() {
        return bottomBarView;
    }

    public TabView getTabView() {
        return tabView;
    }

    public void closeTab(String tag) {
        // Remove the tab from TabView. TabView will select another tab which will replace the corresponding fragment.
        // The DataHolder must be removed by the fragment itself, as deletion process differs for each tab.

        // Default fragment (the one added when the app is opened) won't be closed.
        if (tag.startsWith("0_")) return;
        tabView.removeTab(tag);
    }
}