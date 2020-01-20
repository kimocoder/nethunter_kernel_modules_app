package com.faust93.kocontrol;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class KoControl extends FragmentActivity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.viewpager);
        PagerTabStrip pagerTabStrip = findViewById(R.id.pagerTabStrip);
            pagerTabStrip.setDrawFullUnderline(true);
            pagerTabStrip.setTabIndicatorColor(Color.parseColor("#33B5E5"));

        TitleAdapter titleAdapter = new TitleAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(titleAdapter);
        mViewPager.setCurrentItem(0);
    }

    public class TitleAdapter extends FragmentPagerAdapter {
        private final String[] titles = new String[] {getResources().getString(R.string.modules), getResources().getString(R.string.help) };
        private final Fragment[] frags = new Fragment[titles.length];

        public TitleAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new ModulesFragment();
            frags[1] = new HelpFragment();
        }

        @SuppressLint("LongLogTag")
        @Override
        public CharSequence getPageTitle(int position) {
            Log.v("TitleAdapter - getPageTitle=", titles[position]);
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            Log.v("TitleAdapter - getItem=", String.valueOf(position));
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle SavedInstanceState){

    }
    @Override
    public void onSaveInstanceState(Bundle SavedInstanceState) {
        super.onSaveInstanceState(SavedInstanceState);
    }
}
