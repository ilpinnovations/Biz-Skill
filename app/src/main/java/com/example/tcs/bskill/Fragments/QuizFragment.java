package com.example.tcs.bskill.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tcs.bskill.Activities.ContentActivity;
import com.example.tcs.bskill.Activities.MainActivity;
import com.example.tcs.bskill.Activities.QuizActivity;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.R;

public class QuizFragment extends android.support.v4.app.Fragment {

    TextView quizStatus, quizLockStatus, startQuiz;
    ImageView quizLock;
    String courseID;
    AppCompatDialog aboutQuiz;
    AppCompatButton ok;
    DatabaseHandlerCourseStatus db;
    ContentActivity contentActivity;

    public QuizFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contentActivity = (ContentActivity) getActivity();
        db = new DatabaseHandlerCourseStatus(contentActivity);
        courseID = contentActivity.getCourseID();

        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizLock = (ImageView) view.findViewById(R.id.quizLock);
        quizStatus = (TextView) view.findViewById(R.id.quizStatus);
        quizLockStatus = (TextView) view.findViewById(R.id.quizLockStatus);
        startQuiz = (TextView) view.findViewById(R.id.startQuiz);
        CourseDetailsBean courseDetailsBean = db.getCourseDetailsByID(courseID);

        if (courseDetailsBean.getAudioStatus().equals("1") && courseDetailsBean.getVideoStatus().equals("1")) {
            quizLock.setImageResource(R.drawable.unlocked);
            quizLockStatus.setText(R.string.a4);
            startQuiz.setVisibility(View.VISIBLE);
            quizStatus.setVisibility(View.INVISIBLE);
        } else {
            quizLock.setImageResource(R.drawable.locked);
            startQuiz.setVisibility(View.INVISIBLE);
            quizLockStatus.setText(R.string.a3);
        }


        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aboutQuiz = new AppCompatDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
                aboutQuiz.setContentView(R.layout.about_quiz);
                aboutQuiz.setCancelable(true);
                aboutQuiz.setTitle(R.string.heading2);
                ok = (AppCompatButton) aboutQuiz.findViewById(R.id.ok);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(contentActivity, QuizActivity.class);
                        i.putExtra("CourseID", courseID);
                        startActivity(i);
                        aboutQuiz.dismiss();
                        contentActivity.finish();

                    }
                });
                aboutQuiz.show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
