package com.github.veselinazatchepina.bookstatistics.books.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.github.veselinazatchepina.bookstatistics.MyApplication;
import com.github.veselinazatchepina.bookstatistics.R;
import com.github.veselinazatchepina.bookstatistics.abstracts.SingleFragmentAbstractActivity;
import com.github.veselinazatchepina.bookstatistics.books.fragments.CurrentBookFragment;
import com.github.veselinazatchepina.bookstatistics.database.BooksRealmRepository;
import com.github.veselinazatchepina.bookstatistics.database.model.Book;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CurrentBookActivity extends SingleFragmentAbstractActivity {

    private static final String CURRENT_BOOK_ID_INTENT = "current_book_id_intent";
    private static final String CURRENT_SECTION_TYPE_INTENT = "current_section_type_intent";
    private static final String CURRENT_BOOK_CATEGORY_INTENT = "current_book_category_intent";

    @BindView(R.id.current_book_view_pager)
    ViewPager mViewPager;

    private long mCurrentBookId;
    private String mCurrentSectionType;
    private String mCurrentCategory;
    private long mChoosenBookIdForIntent;
    private RealmResults<Book> mBooksInCurrentSection;


    public static Intent newIntent(Context context, long currentBookId, String currentSectionType, String currentCategory) {
        Intent intent = new Intent(context, CurrentBookActivity.class);
        intent.putExtra(CURRENT_BOOK_ID_INTENT, currentBookId);
        intent.putExtra(CURRENT_SECTION_TYPE_INTENT, currentSectionType);
        intent.putExtra(CURRENT_BOOK_CATEGORY_INTENT, currentCategory);
        return intent;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_current_book_view_pager;
    }

    @Override
    public void defineInputData(Bundle saveInstanceState) {
        mCurrentBookId = getIntent().getLongExtra(CURRENT_BOOK_ID_INTENT, -1);
        mCurrentSectionType = getIntent().getStringExtra(CURRENT_SECTION_TYPE_INTENT);
        mCurrentCategory = getIntent().getStringExtra(CURRENT_BOOK_CATEGORY_INTENT);
        defineBooksForViewPager();
        mBooksInCurrentSection.addChangeListener(new RealmChangeListener<RealmResults<Book>>() {
            @Override
            public void onChange(RealmResults<Book> element) {
                defineViewPager(element);
            }
        });
    }

    private void defineViewPager(final RealmResults<Book> element) {
        if (element.isEmpty()) {
            this.finish();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                mCurrentFragment = CurrentBookFragment.newInstance(element.get(position).getId());
                return mCurrentFragment;
            }

            @Override
            public int getCount() {
                return element.size();
            }

            @Override
            public void finishUpdate(ViewGroup container) {
                try {
                    super.finishUpdate(container);
                } catch (NullPointerException nullPointerException) {
                    System.out.println(getString(R.string.view_pager_exception));
                }
            }
        });
        setViewPagerOnClickedBookPosition(mViewPager, element);
        setViewPagerOnPageChangeListener(mViewPager, element);
    }

    @Override
    public void defineFragment() {

    }

    @Override
    public Fragment createFragment() {
        return null;
    }

    private void setViewPagerOnClickedBookPosition(ViewPager viewPager, RealmResults<Book> element) {
        for (int i = 0; i < element.size(); i++) {
            if (element.get(i).getId() == mCurrentBookId) {
                viewPager.setCurrentItem(i);
                mChoosenBookIdForIntent = element.get(viewPager.getCurrentItem()).getId();
            }
        }
    }

    private void setViewPagerOnPageChangeListener(ViewPager viewPager, final RealmResults<Book> element) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mChoosenBookIdForIntent = element.get(position).getId();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void defineBooksForViewPager() {
        BooksRealmRepository booksRealmRepository = new BooksRealmRepository();
        if (mCurrentCategory != null) {
            mBooksInCurrentSection = booksRealmRepository.getAllBooksInCurrentSectionByCategory(mCurrentSectionType, mCurrentCategory);
        } else {
            mBooksInCurrentSection = booksRealmRepository.getAllBooksInCurrentSection(mCurrentSectionType);
        }
    }

    @Override
    public int setFabImageResourceId() {
        return R.drawable.ic_create_white_24dp;
    }

    @Override
    public void defineActionWhenFabIsPressed() {
        Intent intent = AddBookActivity.newIntent(this, mChoosenBookIdForIntent, getString(R.string.edit_book_title));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
