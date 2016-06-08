package com.example.tcs.bskill.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.tcs.bskill.R;

public class LoginActivity extends AppCompatActivity {

    EditText empID, pass;
    AppCompatButton login;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        empID = (EditText) findViewById(R.id.input_empID);
        pass = (EditText) findViewById(R.id.input_password);
        login = (AppCompatButton) findViewById(R.id.btn_login);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        empID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });

        Typeface type1 = Typeface.createFromAsset(getAssets(), "segoeprb.ttf");

        empID.setTypeface(type1);
        pass.setTypeface(type1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}