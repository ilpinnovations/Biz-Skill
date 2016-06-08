package com.example.tcs.bskill.Fragments;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tcs.bskill.Activities.ContentActivity;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.R;
import com.example.tcs.bskill.Utilities.Vizualizer.VisualizerView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

public class AudioFragment extends android.support.v4.app.Fragment implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private CircularProgressView progressView;
    private MediaPlayer mediaPlayer = null;
    private VisualizerView mVisualiserView = null;
    private Visualizer mVisualizer = null;
    private FloatingActionButton floatingActionButton;
    private String courseID, courseAudioURL, descriptionContent;
    private ProgressBar progressBar;
    private final Handler handler = new Handler();
    private boolean musicStarted = false;
    private int mediaFileLengthInMilliseconds;
    DatabaseHandlerCourseStatus db;
    boolean gettingDestroyed = false;

    public AudioFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ContentActivity contentActivity = (ContentActivity) getActivity();
        db = new DatabaseHandlerCourseStatus(contentActivity);

        courseID = contentActivity.getCourseID();
        courseAudioURL = contentActivity.getCourseAudioURL();
        descriptionContent = contentActivity.getCourseDesc();

        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mVisualiserView = (VisualizerView) view.findViewById(R.id.myVisualizerView);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        TextView description = (TextView) view.findViewById(R.id.description);

        description.setText(descriptionContent);

        initView();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadAudio().execute();
            }
        });
    }

    private void initView() {
        progressBar.setMax(99); // It means 100% .0-99

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void primarySeekBarProgressUpdater() {
        progressBar.setSecondaryProgress(100);
        int percent = (int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100);
        progressBar.setProgress(percent);
        Log.d("ProgressPercent", String.valueOf(percent));

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onDestroyView() {
        mediaPlayer.stop();
        gettingDestroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    class LoadAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mediaPlayer.setDataSource(courseAudioURL);
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(0);
        }

        @Override
        protected void onPostExecute(Void result) {
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                if (!musicStarted)
                    setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                floatingActionButton.setImageResource(android.R.drawable.ic_media_pause);
                musicStarted = true;

            } else {
                mediaPlayer.pause();
                mVisualizer.setEnabled(false);
                floatingActionButton.setImageResource(android.R.drawable.ic_media_play);
            }
            progressView.setVisibility(View.INVISIBLE);
            progressView.clearAnimation();
            primarySeekBarProgressUpdater();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (!gettingDestroyed) {
            floatingActionButton.setImageResource(android.R.drawable.ic_media_play);

            CourseDetailsBean courseDetailsBean1 = db.getCourseDetailsByID(courseID);
            int courseProgress = ((1 + Integer.parseInt(courseDetailsBean1.getVideoStatus()) + Integer.parseInt(courseDetailsBean1.getQuizStatus())) * 100) / 3;

            CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
            courseDetailsBean.setCourseID(courseID);
            courseDetailsBean.setAudioStatus("1");
            courseDetailsBean.setVideoStatus(courseDetailsBean1.getVideoStatus());
            courseDetailsBean.setQuizStatus(courseDetailsBean1.getQuizStatus());
            courseDetailsBean.setCourseProgress(String.valueOf(courseProgress));
            db.updateCourseDetails(courseDetailsBean);

            int overallProgressPercent = ((db.getAudioCount() + db.getVideoCount() + db.getQuizCount()) * 100) / (3 * db.getCourseDetailsCount());

            db.updateOverallProgress(String.valueOf(overallProgressPercent));
        }
    }

    private void setupVisualizerFxAndUI() {
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        mVisualiserView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }
}