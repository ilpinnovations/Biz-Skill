package com.example.tcs.bskill.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.tcs.bskill.Adapters.LeaderBoardRecycleAdapter;
import com.example.tcs.bskill.Beans.EmployeeBean;
import com.example.tcs.bskill.R;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.loopj.android.http.HttpGet;

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


public class LeaderBoardActivity extends AppCompatActivity {
     DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    List<EmployeeBean> employeeBeanList = new ArrayList<>();
//    ArrayList<EmployeeBean> filteredList;
    RecyclerView recycler1;
//    LeaderBoardRecycleAdapter leaderBoardRecycleAdapter;
    CircularProgressView progressView;
    LinearLayout progress_status;
    AppCompatDialog mDialog;
    AppCompatTextView progressName;
    String course_id, name, points, leaderboard_id/*, imageurl*/;
    String current_course_id, current_name, current_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Leader Board");
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Leader Board");
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        progress_status = (LinearLayout) findViewById(R.id.progress_status);



        recycler1 = (RecyclerView) findViewById(R.id.leaderBoardList);
        recycler1.setLayoutManager(new LinearLayoutManager(this));


        mDialog = new AppCompatDialog(LeaderBoardActivity.this, R.style.AppCompatProgressDialogStyle);
        mDialog.setContentView(R.layout.progress_dialog);
        progressName = (AppCompatTextView) mDialog.findViewById(R.id.progress_name);
        progressName.setText("Loading");
        mDialog.setCancelable(false);
        mDialog.show();
        Log.d("tag","1");
        Intent intent = getIntent();
        current_course_id = intent.getStringExtra("courseID");
        current_name = intent.getStringExtra("currentEmpName");
        current_points = intent.getStringExtra("currentQuizPoints");
        new GetLeaderBoardAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leaderboard, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);

        }
    }
/*
    @Override
    public void onServiceResponse(String s) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        Log.d("tag_s2", s);
        Log.d("tag", "finish");
    }
*/


     class GetLeaderBoardAsync extends AsyncTask<Void, Void, Void> {
        private String jsonStr;

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.d("tag","2");

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://theinspirer.in/BizSkills/getLeaderboard.php?course_id="+current_course_id+"&points="+current_points+"&name="+current_name);
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
                        leaderboard_id = c.getString("leaderboard_id");
                        course_id = c.getString("course_id");
                        points = c.getString("points");
//                        imageurl = c.getString("imageurl");
                        name = c.getString("name");
                        EmployeeBean eb1 = new EmployeeBean(name, leaderboard_id, points, "", course_id);
                        employeeBeanList.add(eb1);

                    }
                } catch (JSONException e) {
                    //problem_loading_courses.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //super.onPostExecute(result);
            //progress_status.setVisibility(View.GONE);
            //swipeRefreshLayout.setEnabled(true);
            LeaderBoardRecycleAdapter leaderBoardRecycleAdapter = new LeaderBoardRecycleAdapter(LeaderBoardActivity.this, employeeBeanList);
            recycler1.setAdapter(leaderBoardRecycleAdapter);
            if (mDialog != null)
                mDialog.dismiss();

        }
    }


}