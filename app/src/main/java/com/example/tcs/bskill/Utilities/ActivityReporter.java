package com.example.tcs.bskill.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tcs.bskill.Interfaces.ReporterCallback;
import com.loopj.android.http.HttpGet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by 1241575(Azim Ansari) on 7/26/2016.
 * Marks user activity on the server
 */
public class ActivityReporter extends AsyncTask<Void, Void, String> {

    private String jsonStr;
    //    private ProgressDialog dialog;
    private Context context;

    private String id, courseId;
    private int activity;
    private ReporterCallback callback;
    private int points;

    public ActivityReporter(Context context, ReporterCallback callback, String id, String courseId, int activity, int points) {
        this.context = context;
        this.id = id;
        this.courseId = courseId;
        this.activity = activity;
        this.callback = callback;
        this.points = points;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        dialog = new ProgressDialog(context);
//        dialog.setTitle("Authenticating");
//        dialog.setMessage("Please wait...");
//        dialog.setCancelable(false);
//        dialog.show();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String url = "http://theinspirer.in/BizSkills/manage_activity.php?employee_id=" + id + "&course_id=" + courseId;

            if (activity == 1) {
                url = url + "&audio_activity=1";
            } else if (activity == 2) {
                url = url + "&video_activity=1";
            } else if (activity == 3) {
                url = url + "&quizz_activity=1&points=" + points;
            }

            HttpGet httpGet = new HttpGet(url);
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
        return jsonStr;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        dialog.dismiss();
        if (!TextUtils.isEmpty(jsonStr) && !jsonStr.equals("error")) {
            callback.onResult(jsonStr);

        } else {
            Toast.makeText(context, "Internal Error", Toast.LENGTH_SHORT).show();
        }
    }
}
