package com.example.tcs.bskill.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.PreferenceUtil;
import com.loopj.android.http.HttpGet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import cz.msebera.android.httpclient.util.TextUtils;

public class LoginActivity extends AppCompatActivity {

    EditText empID, pass, empName;
    AppCompatButton login;
    ScrollView scrollView;
    private TextInputLayout tilID, tilEmail, tilName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        empID = (EditText) findViewById(R.id.input_empID);
        pass = (EditText) findViewById(R.id.input_email);
        empName = (EditText) findViewById(R.id.input_empName);
        login = (AppCompatButton) findViewById(R.id.btn_login);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        tilID = (TextInputLayout) findViewById(R.id.tilID);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilName = (TextInputLayout) findViewById(R.id.tilName);

        /*empID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        sleep(500);
                                        scrollView.scrollTo(0, login.getBottom());
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        thread.start();
                    }
                });
            }
        });*/

        Typeface type1 = Typeface.createFromAsset(getAssets(), "segoeprb.ttf");

        empID.setTypeface(type1);
        pass.setTypeface(type1);
        empName.setTypeface(type1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {

                    SharedPreferences preferences = PreferenceUtil.getPreference(LoginActivity.this);
                    preferences.edit()
                            .putString(PreferenceUtil.EMP_ID, empID.getText().toString().trim())
                            .putString(PreferenceUtil.EMP_NAME, empName.getText().toString().trim())
                            .putString(PreferenceUtil.EMP_EMAIL, pass.getText().toString().trim())
                            .apply();
                    LoginTask task = new LoginTask(LoginActivity.this, empID.getText().toString().trim(), pass.getText().toString().trim(), empName.getText().toString().trim());
                    task.execute();
                }
            }
        });
    }

    private boolean isValidInput() {
        if (empID.getText().toString().trim().isEmpty()) {
            tilID.setError("Please enter Employee id");
            return false;
        } else if (empName.getText().toString().trim().isEmpty()) {
            tilID.setErrorEnabled(false);
            tilName.setError("Please enter Employee name");
            return false;
        } else if (pass.getText().toString().trim().isEmpty()) {
            tilName.setErrorEnabled(false);
            tilEmail.setError("Please enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(pass.getText().toString().trim()).matches()) {
            tilEmail.setError("Invalid email address");
            return false;
        }
        tilID.setErrorEnabled(false);
        tilEmail.setErrorEnabled(false);
        tilName.setErrorEnabled(false);
        return true;
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        private String jsonStr;
        private ProgressDialog dialog;
        private Context context;

        private String id, pass, name;

        public LoginTask(Context context, String id, String email, String name) {
            this.context = context;
            this.id = id;
            this.pass = email;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setTitle("Authenticating");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String nameWithoutSpace = name.replaceAll(" ", "%20");

                HttpGet httpGet = new HttpGet("http://theinspirer.in/BizSkills/employee_registration.php?employee_id=" + id + "&employee_email=" + pass + "&employee_name=" + nameWithoutSpace);
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
            dialog.dismiss();
            if (!TextUtils.isEmpty(jsonStr) && !jsonStr.equals("error")) {
                Toast.makeText(context, jsonStr, Toast.LENGTH_SHORT).show();

                PreferenceUtil.getPreference(context).edit().putBoolean(PreferenceUtil.IS_FIRST_TIME, false).apply();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(context, "Internal Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}