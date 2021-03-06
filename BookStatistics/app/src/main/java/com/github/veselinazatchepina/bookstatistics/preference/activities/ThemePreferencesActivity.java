package com.github.veselinazatchepina.bookstatistics.preference.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.veselinazatchepina.bookstatistics.MyApplication;
import com.github.veselinazatchepina.bookstatistics.R;
import com.github.veselinazatchepina.bookstatistics.abstracts.NavigationAbstractActivity;
import com.github.veselinazatchepina.bookstatistics.preference.fragments.ThemePreferencesFragment;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;

import static com.github.veselinazatchepina.bookstatistics.R.id.fab;


public class ThemePreferencesActivity extends NavigationAbstractActivity {

    private static final String BOOK_SETTINGS_TITLE = "book_settings_title";

    @BindView(fab)
    FloatingActionButton mFloatingActionButton;

    public static Intent newIntent(Context context, String title) {
        Intent intent = new Intent(context, ThemePreferencesActivity.class);
        intent.putExtra(BOOK_SETTINGS_TITLE, title);
        return intent;
    }

    @Override
    public void defineInputData(Bundle savedInstanceState) {
        setTitleToActivity();
    }

    private void setTitleToActivity() {
        String title = getIntent().getStringExtra(BOOK_SETTINGS_TITLE);
        if (title != null) {
            setTitle(title);
        }
    }

    @Override
    public void defineAppBarLayoutExpandableValue() {
        setAppBarNotExpandable();
    }

    @Override
    public String getScreenOrientation(Context context) {
        return super.getScreenOrientation(context);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_navigation_drawer;
    }

    @Override
    public void defineFab() {
        mFloatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void defineFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        android.app.Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            currentFragment = ThemePreferencesFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, currentFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
