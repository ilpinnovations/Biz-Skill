package com.example.tcs.bskill.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tcs.bskill.Activities.ContentActivity;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.R;

public class QuizFragment extends android.support.v4.app.Fragment {

    TextView quizStatus, quizLockStatus;
    ImageView quizLock;
    String courseID;
    DatabaseHandlerCourseStatus db;

    public QuizFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ContentActivity contentActivity = (ContentActivity) getActivity();
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
        CourseDetailsBean courseDetailsBean = db.getCourseDetailsByID(courseID);

        if (courseDetailsBean.getAudioStatus().equals("1") && courseDetailsBean.getVideoStatus().equals("1")) {
            quizLock.setImageResource(R.drawable.unlocked);
            quizLockStatus.setText(R.string.a4);
            quizStatus.setVisibility(View.INVISIBLE);
        } else {
            quizLock.setImageResource(R.drawable.locked);
            quizLockStatus.setText(R.string.a3);
        }
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
