package com.example.tcs.bskill.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tcs.bskill.Activities.ContentActivity;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.Interfaces.ReporterCallback;
import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.ActivityReporter;
import com.example.tcs.bskill.Utilities.PreferenceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoFragment extends android.support.v4.app.Fragment implements ReporterCallback {
    private static final String API_KEY = "AIzaSyALFWDKEVLY4UQMjU5b5qCkWmS1ArE0hgc";
    String courseVideoURL, courseID;
    YouTubePlayer YouPlayer;
    DatabaseHandlerCourseStatus db;

    public VideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ContentActivity contentActivity = (ContentActivity) getActivity();
        db = new DatabaseHandlerCourseStatus(contentActivity);

        courseID = contentActivity.getCourseID();
        courseVideoURL = contentActivity.getCourseVideoURL();

        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {

                    @Override
                    public void onLoading() {
                        Log.d("VideoStatus", "Loading...");
                    }

                    @Override
                    public void onLoaded(String s) {
                        Log.d("VideoStatus", "Loaded!");
                    }

                    @Override
                    public void onAdStarted() {
                    }

                    @Override
                    public void onVideoStarted() {
                        Log.d("VideoStatus", "Started!");
                    }

                    @Override
                    public void onVideoEnded() {

                        CourseDetailsBean courseDetailsBean1 = db.getCourseDetailsByID(courseID);
                        int courseProgress = ((Integer.parseInt(courseDetailsBean1.getAudioStatus()) + 1 + Integer.parseInt(courseDetailsBean1.getQuizStatus())) * 100) / 3;

                        CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
                        courseDetailsBean.setCourseID(courseID);
                        courseDetailsBean.setAudioStatus(courseDetailsBean1.getAudioStatus());
                        courseDetailsBean.setVideoStatus("1");
                        courseDetailsBean.setQuizStatus(courseDetailsBean1.getQuizStatus());
                        courseDetailsBean.setCourseProgress(String.valueOf(courseProgress));
                        db.updateCourseDetails(courseDetailsBean);

                        int overallProgressPercent = ((db.getAudioCount() + db.getVideoCount() + db.getQuizCount()) / (3 * db.getCourseDetailsCount())) * 100;
                        db.updateOverallProgress(String.valueOf(overallProgressPercent));

                        ActivityReporter reporter = new ActivityReporter(getActivity(), VideoFragment.this, PreferenceUtil.getEmpID(getActivity()), courseID, 2, 0);
                        reporter.execute();
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d("VideoStatus", "Error");
                    }
                });

                player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {

                    @Override
                    public void onPlaying() {

                    }

                    @Override
                    public void onPaused() {

                    }

                    @Override
                    public void onStopped() {

                    }

                    @Override
                    public void onBuffering(boolean b) {

                    }

                    @Override
                    public void onSeekTo(int i) {

                    }
                });

                if (!wasRestored) {
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    player.loadVideo(courseVideoURL);

                    if (player.isPlaying())
                        player.pause();
                }
                YouPlayer = player;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                String errorMessage = youTubeInitializationResult.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (YouPlayer != null && YouPlayer.isPlaying())
            YouPlayer.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResult(String result) {
        if (!isAdded())
            return;
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }
}
