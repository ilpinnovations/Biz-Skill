package com.example.tcs.bskill.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.textservice.TextInfo;
import android.widget.TextView;

import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.PreferenceUtil;

public class ProfileActivity extends AppCompatActivity {

    private TextView empName, empId, empEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        empName = (TextView) findViewById(R.id.name);
        empId = (TextView) findViewById(R.id.id);
        empEmail = (TextView) findViewById(R.id.email);

        empName.setText(PreferenceUtil.getEmpName(this));
        empId.setText(PreferenceUtil.getEmpID(this));
        empEmail.setText(PreferenceUtil.getEmpEmail(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
