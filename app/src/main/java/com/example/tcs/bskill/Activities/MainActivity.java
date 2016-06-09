package com.example.tcs.bskill.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.Toast;

import com.example.tcs.bskill.Adapters.CourseRecyclerAdapter;
import com.example.tcs.bskill.Beans.CourseBean;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.Interfaces.Communicator;
import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.ConnectionDetector;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.loopj.android.http.HttpGet;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements Communicator {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    ArrayList<CourseBean> courseBeanList, filteredList;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    String jsonStr = "", courseID, courseName, courseDesc, courseAudioURL, courseVideoURL;
    CircularProgressView progressView;
    LinearLayout progress_status;
    NavigationView navigationView;
    ProgressBar overallProgress;
    ConnectionDetector cd;
    MaterialSearchView searchView;
    TextView internet, problem_loading_courses, overallProgressPercent;
    DatabaseHandlerCourseStatus db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectionDetector(getApplicationContext());
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
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        recyclerView = (RecyclerView) findViewById(R.id.courseList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

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

    public void myProgress(View v) {
        Toast.makeText(getApplicationContext(), "Progress", Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void myCourses(View v) {
        checkInternetConnection(false);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void leaderBoard(View v) {
        Toast.makeText(getApplicationContext(), "Leader Board", Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void myProfile(View v) {
        Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void checkInternetConnection(boolean isProgress) {
        if (cd.isConnectingToInternet()) {
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

            Snackbar snackbar = Snackbar
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

            if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
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
                problem_loading_courses.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_inbox:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_starred:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_sent_mail:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_drafts:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.item_navigation_drawer_help_and_feedback:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
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
}