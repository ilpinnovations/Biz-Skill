package com.example.tcs.bskill.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tcs.bskill.Adapters.CourseRecyclerAdapter;
import com.example.tcs.bskill.Adapters.NavigationItemsAdapter;
import com.example.tcs.bskill.Beans.CourseBean;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.Interfaces.Communicator;
import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.ConnectionDetector;
import com.example.tcs.bskill.Utilities.PreferenceUtil;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.loopj.android.http.HttpGet;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements Communicator {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static int permissionCheck;
    final int REQUEST_CODE_RECORD_AUDIO = 1;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    ArrayList<CourseBean> courseBeanList, filteredList;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> navItemsList;
    List<Integer> navItemsIconsList;
    RecyclerView recyclerView, recyclerViewItems;
    String jsonStr = "", courseID, courseName, courseDesc, courseAudioURL, courseVideoURL;
    CircularProgressView progressView;
    NavigationItemsAdapter navigationItemsAdapter;
    LinearLayout progress_status;
    NavigationView navigationView;
    ProgressBar overallProgress;
    MaterialSearchView searchView;
    TextView internet, problem_loading_courses, overallProgressPercent;
    DatabaseHandlerCourseStatus db;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        progress_status = (LinearLayout) findViewById(R.id.progress_status);
        internet = (TextView) findViewById(R.id.internet);
        problem_loading_courses = (TextView) findViewById(R.id.problem_loading_courses);
        overallProgress = (ProgressBar) findViewById(R.id.overallProgress);
        overallProgressPercent = (TextView) findViewById(R.id.overallProgressPercent);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        TextView navName = (TextView) navigationView.findViewById(R.id.navName);
        TextView navId = (TextView) navigationView.findViewById(R.id.navId);

        navName.setText(PreferenceUtil.getEmpName(this));
        navId.setText(PreferenceUtil.getEmpID(this));

        recyclerView = (RecyclerView) findViewById(R.id.courseList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        recyclerViewItems = (RecyclerView) drawerLayout.findViewById(R.id.navigation_items);

        /**READ_PHONE_STATE PERMISSION FOR MARSHMALLOW**/
        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_RECORD_AUDIO);
        }

        navItemsList = new ArrayList<>();

        navItemsList.add("My Courses");
        navItemsList.add("Leader Board");
        navItemsList.add("My Profile");

        navItemsIconsList = new ArrayList<>();

        navItemsIconsList.add(R.drawable.item_course_icon);
        navItemsIconsList.add(R.drawable.item_leaderboard_icon);
        navItemsIconsList.add(R.drawable.item_profile_icon);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewItems.setLayoutManager(linearLayoutManager);
        navigationItemsAdapter = new NavigationItemsAdapter(navItemsList, navItemsIconsList, MainActivity.this);
        recyclerViewItems.setAdapter(navigationItemsAdapter);


        /**Setting Up Search View**/
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setVoiceSearch(true);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filteredList = new ArrayList<>();
                if (newText.length() == 0) {
                    filteredList.addAll(courseBeanList);
                } else {
                    final String filterPattern = newText.toLowerCase().trim();

                    for (final CourseBean courseBean : courseBeanList) {
                        if (courseBean.getCourseName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(courseBean);
                        }
                    }
                }
                CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter(MainActivity.this, filteredList);
                recyclerView.setAdapter(courseRecyclerAdapter);

                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

        db = new DatabaseHandlerCourseStatus(this);
        if (db.getOverallProgressCount() > 0) {
            overallProgress.setMax(100);
            overallProgress.setProgress(Integer.parseInt(db.getOverallProgress()));
            overallProgressPercent.setText(db.getOverallProgress() + "%");
        } else {
            overallProgress.setMax(100);
            overallProgress.setProgress(0);
            overallProgressPercent.setText("0%");
            db.addOverallProgress("0");
        }

        initSwipeRefresher();
        checkInternetConnection(true);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }


            @Override
            public void onDrawerOpened(View drawerView) {

                if (db.getOverallProgressCount() > 0) {
                    overallProgress.setMax(100);
                    overallProgress.setProgress(Integer.parseInt(db.getOverallProgress()));
                    overallProgressPercent.setText(db.getOverallProgress() + "%");
                } else {
                    overallProgress.setMax(100);
                    overallProgress.setProgress(0);
                    overallProgressPercent.setText("0%");
                    db.addOverallProgress("0");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    public void onNavigationItemSelected(int position) {
        checkInternetConnection(false);
        if (position == 0) {
//            checkInternetConnection(false);
        } else if (position == 1) {
            if (ConnectionDetector.isConnected(this)){
                Intent i = new Intent(this, LeaderBoardNavBarActivity.class);
                startActivity(i);
            }else {
//                Log.i(TAG, "No internet Connection!");
            }
        } else if (position == 2) {
            Log.i(TAG, "position = 2");
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            if (ConnectionDetector.isConnected(this)){
                //            Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
            }else {
//                Log.i(TAG, "No internet Connection!");
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void checkInternetConnection(boolean isProgress) {
        if (ConnectionDetector.isConnected(this)) {
            internet.setVisibility(View.INVISIBLE);
            problem_loading_courses.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            if (isProgress) {
                progress_status.setVisibility(View.VISIBLE);
            } else {
                progress_status.setVisibility(View.INVISIBLE);
            }
            new GetCourses().execute();
        } else {
            problem_loading_courses.setVisibility(View.INVISIBLE);
            progress_status.setVisibility(View.INVISIBLE);
            internet.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            snackbar = Snackbar
                    .make(recyclerView, "No internet connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkInternetConnection(false);
                        }
                    });
            snackbar.show();
        }
    }

    private void initSwipeRefresher() {
        swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDarker, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress_status.setVisibility(View.INVISIBLE);
                checkInternetConnection(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callBack(CourseBean courseBean) {
        Intent i = new Intent(this, ContentActivity.class);
        i.putExtra("courseID", courseBean.getCourseID());
        i.putExtra("courseName", courseBean.getCourseName());
        i.putExtra("courseDesc", courseBean.getCourseDesc());
        i.putExtra("courseAudioURL", courseBean.getCourseAudioURL());
        i.putExtra("courseVideoURL", courseBean.getCourseVideoURL());

        startActivity(i);
    }

    @Override
    public void onBackPressed() {

        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    private class GetCourses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            courseBeanList = new ArrayList<CourseBean>();
            progressView.startAnimation();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://theinspirer.in/BSkill/getBCourses.php");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                jsonStr = EntityUtils.toString(httpEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("JSONString", jsonStr);

            if (jsonStr != null && !jsonStr.equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray result = jsonObj.getJSONArray("result");

                    Log.d("JSONArray", String.valueOf(result));

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject c = result.getJSONObject(i);
                        courseID = c.getString("course_id");
                        courseName = c.getString("course_name");
                        courseDesc = c.getString("course_description");
                        courseAudioURL = c.getString("course_audio_url");
                        courseVideoURL = c.getString("course_video_url");
                        CourseBean cb1 = new CourseBean(courseID, courseName, courseDesc, courseAudioURL, courseVideoURL);
                        courseBeanList.add(cb1);

                        if (db.getCourseDetailsCountByID(courseID) == 0)
                            db.addCourseDetails(new CourseDetailsBean(courseID, "0", "0", "0", "0"));

                    }
                } catch (JSONException e) {
                    //problem_loading_courses.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            } else {
//                problem_loading_courses.setVisibility(View.VISIBLE);
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress_status.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(true);
            CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter(MainActivity.this, courseBeanList);
            recyclerView.setAdapter(courseRecyclerAdapter);

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}