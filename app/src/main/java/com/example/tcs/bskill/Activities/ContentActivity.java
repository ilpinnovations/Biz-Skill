package com.example.tcs.bskill.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Fragments.AudioFragment;
import com.example.tcs.bskill.Fragments.QuizFragment;
import com.example.tcs.bskill.Fragments.VideoFragment;
import com.example.tcs.bskill.R;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {

    public TabLayout tabLayout;

    public String getCourseVideoURL() {
        return courseVideoURL;
    }

    public String getCourseAudioURL() {
        return courseAudioURL;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseID() {
        return courseID;
    }

    public ViewPager viewPager;
    ActionBar actionBar;
    String courseID, courseName, courseDesc, courseAudioURL, courseVideoURL;
    CourseDetailsBean courseDetailsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        courseID = intent.getStringExtra("courseID");
        courseName = intent.getStringExtra("courseName");
        courseDesc = intent.getStringExtra("courseDesc");
        courseAudioURL = intent.getStringExtra("courseAudioURL");
        courseVideoURL = intent.getStringExtra("courseVideoURL");

        Log.d("course", "Showing details");

        Log.d("courseID", courseID);
        Log.d("courseName", courseName);
        Log.d("courseDesc", courseDesc);
        Log.d("courseAudioURL", courseAudioURL);
        Log.d("courseVideoURL", courseVideoURL);

        actionBar.setTitle(courseName);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        int[] ic = new int[]{
                R.drawable.tab_icon_audio,
                R.drawable.tab_icon_video,
                R.drawable.tab_icon_quiz};

        Log.d("tag", "ic= " + ic[0]);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(ic[i]);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
                Fragment f = viewPagerAdapter.getItem(position);
                f.onResume();
                Fragment c = viewPagerAdapter.getItem(currentPosition);
                c.onPause();
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new AudioFragment(), "");
        adapter.addFragment(new VideoFragment(), "");
        adapter.addFragment(new QuizFragment(), "");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}