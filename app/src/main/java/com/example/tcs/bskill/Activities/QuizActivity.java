package com.example.tcs.bskill.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.tcs.bskill.R;

public class QuizActivity extends AppCompatActivity {


    CardView option1, option2, option3, option4;
    Animation shakeAnim = null, spinAnim = null;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quiz Activity");

        spinAnim = AnimationUtils.loadAnimation(this, R.anim.spin);
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);


        option1 = (CardView) findViewById(R.id.option1);
        option2 = (CardView) findViewById(R.id.option2);
        option3 = (CardView) findViewById(R.id.option3);
        option4 = (CardView) findViewById(R.id.option4);

        option3.setOnClickListener(showRightAnim(option3));
        option1.setOnClickListener(showWrongAnim(option1));
        option2.setOnClickListener(showWrongAnim(option2));
        option4.setOnClickListener(showWrongAnim(option4));

    }

    private View.OnClickListener showRightAnim(final CardView cardView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setCardBackgroundColor(Color.parseColor("#dc00c164"));
                cardView.startAnimation(spinAnim);
            }
        };
    }

    private View.OnClickListener showWrongAnim(final CardView cardView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setCardBackgroundColor(R.color.colorPrimaryDark);
                cardView.startAnimation(shakeAnim);

            }
        };
    }


}
