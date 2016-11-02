package com.example.tcs.bskill.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.PreferenceUtil;

public class SplashScreen extends Activity {

    ImageView splash;
    TextView title, sub_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = (ImageView) findViewById(R.id.splash);
        title = (TextView) findViewById(R.id.title);
        sub_title = (TextView) findViewById(R.id.sub_title);

        Typeface title_type = Typeface.createFromAsset(getAssets(), "segoeprb.ttf");
        title.setTypeface(title_type);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtil.getPreference(SplashScreen.this).getBoolean(PreferenceUtil.IS_FIRST_TIME, true)) {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 500);
    }
}
