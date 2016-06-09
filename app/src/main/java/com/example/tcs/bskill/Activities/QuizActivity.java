package com.example.tcs.bskill.Activities;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tcs.bskill.Beans.QuestionBean;
import com.example.tcs.bskill.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizActivity extends AppCompatActivity implements Runnable {

    int getQuestionBeanListSize, score = 0, min = 0, sec = 30, mn = 0, sc = 30, questionBeanListIndex = 0;
    static String ans = null;
    String op_a, op_b, op_c, op_d, m, s;
    MediaPlayer player_p, player_n, player_next, player_back;
    TextView timer, player_score, next, cult_q, button_a, button_b, button_c, button_d, player_name, counter;
    ProgressBar progressBar;
    Thread t;
    boolean sf;
    ArrayList<QuestionBean> questionBeanList;
    QuestionBean questionBean;
    private Handler setTime = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz);

        player_p = MediaPlayer.create(getApplicationContext(), R.raw.click_possitive);
        player_n = MediaPlayer.create(getApplicationContext(), R.raw.click_error);
        player_next = MediaPlayer.create(getApplicationContext(), R.raw.next_music);
        player_back = MediaPlayer.create(getApplicationContext(), R.raw.back_music3);

        player_n.setVolume(100, 100);
        player_p.setVolume(100, 100);
        player_next.setVolume(100, 100);

        cult_q = (TextView) findViewById(R.id.textView1);
        button_a = (TextView) findViewById(R.id.textView2);
        button_b = (TextView) findViewById(R.id.textView3);
        button_c = (TextView) findViewById(R.id.textView4);
        button_d = (TextView) findViewById(R.id.textView5);
        player_name = (TextView) findViewById(R.id.textView6);
        player_score = (TextView) findViewById(R.id.textView8);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        timer = (TextView) findViewById(R.id.textView10);
        next = (TextView) findViewById(R.id.textView9);
        counter = (TextView) findViewById(R.id.textView7);

        player_name.setText("Pushpal");

        new GetQuestions().execute();

        button_a.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Seconds", String.valueOf(sec));

                next.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                button_a.setClickable(false);
                button_b.setClickable(false);
                button_c.setClickable(false);
                button_d.setClickable(false);

                if (ans.equals(op_a)) {
                    button_a.setBackgroundResource(R.drawable.correct);
                    button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    player_p.start();
                    score = score + 1000 + (sec * 100);
                } else {
                    button_a.setBackgroundResource(R.drawable.incorrect);
                    button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_highlight_off_white_24dp, 0);
                    player_n.start();

                    if (ans.equals(op_b)) {
                        button_b.setBackgroundResource(R.drawable.correct);
                        button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_c)) {
                        button_c.setBackgroundResource(R.drawable.correct);
                        button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_d)) {
                        button_d.setBackgroundResource(R.drawable.correct);
                        button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    }
                }
                player_score.setText(String.valueOf(score));
                mySuspend();
            }
        });

        button_b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Seconds", String.valueOf(sec));

                next.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                button_a.setClickable(false);
                button_b.setClickable(false);
                button_c.setClickable(false);
                button_d.setClickable(false);

                if (ans.equals(op_b)) {
                    button_b.setBackgroundResource(R.drawable.correct);
                    button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    score = score + 1000 + (sec * 100);
                    player_p.start();
                } else {
                    button_b.setBackgroundResource(R.drawable.incorrect);
                    button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_highlight_off_white_24dp, 0);
                    player_n.start();
                    if (ans.equals(op_a)) {
                        button_a.setBackgroundResource(R.drawable.correct);
                        button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_c)) {
                        button_c.setBackgroundResource(R.drawable.correct);
                        button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_d)) {
                        button_d.setBackgroundResource(R.drawable.correct);
                        button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    }
                }
                player_score.setText(String.valueOf(score));
                mySuspend();
            }
        });


        button_c.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Seconds", String.valueOf(sec));

                next.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                button_a.setClickable(false);
                button_b.setClickable(false);
                button_c.setClickable(false);
                button_d.setClickable(false);

                if (ans.equals(op_c)) {
                    button_c.setBackgroundResource(R.drawable.correct);
                    button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    score = score + 1000 + (sec * 100);
                    player_p.start();
                } else {
                    button_c.setBackgroundResource(R.drawable.incorrect);
                    button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_highlight_off_white_24dp, 0);
                    player_n.start();
                    if (ans.equals(op_a)) {
                        button_a.setBackgroundResource(R.drawable.correct);
                        button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_b)) {
                        button_b.setBackgroundResource(R.drawable.correct);
                        button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_d)) {
                        button_d.setBackgroundResource(R.drawable.correct);
                        button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    }
                }
                player_score.setText(String.valueOf(score));
                mySuspend();
            }
        });


        button_d.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("Seconds", String.valueOf(sec));

                next.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                button_a.setClickable(false);
                button_b.setClickable(false);
                button_c.setClickable(false);
                button_d.setClickable(false);

                if (ans.equals(op_d)) {
                    button_d.setBackgroundResource(R.drawable.correct);
                    button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    player_p.start();
                    score = score + 1000 + (sec * 100);
                } else {
                    button_d.setBackgroundResource(R.drawable.incorrect);
                    button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_highlight_off_white_24dp, 0);
                    player_n.start();
                    if (ans.equals(op_a)) {
                        button_a.setBackgroundResource(R.drawable.correct);
                        button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_c)) {
                        button_c.setBackgroundResource(R.drawable.correct);
                        button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    } else if (ans.equals(op_b)) {
                        button_b.setBackgroundResource(R.drawable.correct);
                        button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                    }
                }
                player_score.setText(String.valueOf(score));
                mySuspend();
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                player_next.start();
                player_n.start();
                player_n.seekTo(0);
                player_n.pause();
                player_p.start();
                player_p.seekTo(0);
                player_p.pause();

                setQuestion();
            }
        });
    }

    public void setQuestion() {

        next.setVisibility(View.INVISIBLE);

        if ((questionBeanListIndex + 1) > getQuestionBeanListSize) {
            finish();
        } else {
            counter.setText(String.valueOf(questionBeanListIndex + 1) + "/" + getQuestionBeanListSize);

            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar2));
            timer.setTextColor(Color.parseColor("#ffffff"));

            next.setEnabled(false);
            progressBar.setProgress(100);

            NewThread();

            button_a.setBackgroundResource(R.drawable.options);
            button_b.setBackgroundResource(R.drawable.options);
            button_c.setBackgroundResource(R.drawable.options);
            button_d.setBackgroundResource(R.drawable.options);
            button_a.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button_b.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button_c.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button_a.setClickable(true);
            button_b.setClickable(true);
            button_c.setClickable(true);
            button_d.setClickable(true);

            if ((questionBeanListIndex + 1) >= getQuestionBeanListSize) {
                next.setText("FINISH");
            }

            questionBean = questionBeanList.get(questionBeanListIndex);

            cult_q.setText(questionBean.getQuestion());
            op_a = questionBean.getOptionA();
            button_a.setText(op_a);
            op_b = questionBean.getOptionB();
            button_b.setText(op_b);
            op_c = questionBean.getOptionC();
            button_c.setText(op_c);
            op_d = questionBean.getOptionD();
            button_d.setText(op_d);
            ans = questionBean.getAnswer();

            questionBeanListIndex++;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        player_back.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player_back.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player_back.stop();
        player_back.release();
    }

    void NewThread() {
        t = new Thread(this, "my watch");
        sf = false;
        t.start();
    }

    @Override
    public void run() {
        while (sec <= 60 && min >= 0) {
            setTime.post(new Runnable() {
                public void run() {
                    if (min == -1) {
                        timer.setText("00");
                        progressBar.setProgress(0);
                        mySuspend();

                        next.setVisibility(View.VISIBLE);
                        next.setEnabled(true);
                        button_a.setClickable(false);
                        button_b.setClickable(false);
                        button_c.setClickable(false);
                        button_d.setClickable(false);

                        player_n.start();

                        score = score - 1000;
                        player_score.setText(String.valueOf(score));
                    } else {
                        if (sec <= 5) {
                            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar5));
                            timer.setTextColor(Color.parseColor("#ff5400"));
                        } else if (sec <= 15) {
                            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar4));
                            timer.setTextColor(Color.parseColor("#f6ff00"));
                        }

                        m = String.valueOf(min);
                        s = String.valueOf(sec);

                        if (s.length() == 1)
                            s = "0" + s;
                        if (m.length() == 1)
                            m = "0" + m;

                        timer.setText(s);

                        float progress = (((min * 60) + sec) * 100) / ((mn * 60) + sc);
                        progressBar.setProgress((int) progress);
                    }
                }
            });

            if (sec == 0) {
                min--;
                sec = 60;
            }

            try {

                Thread.sleep(1000);

                synchronized (this) {
                    while (sf) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            sec--;
        }
    }

    synchronized void mySuspend() {
        sf = true;

        min = 0;
        sec = 30;
        mn = 0;
        sc = 30;
    }

    synchronized void myResume() {
        sf = false;
        notify();
    }

    private class GetQuestions extends AsyncTask<Void, Void, Void> {

        HttpURLConnection conn;
        String jsonStr = "", quizID, question, optionA, optionB, optionC, optionD, answer;
        AppCompatDialog dialog;
        AppCompatTextView progressName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            questionBeanList = new ArrayList<>();
            dialog = new AppCompatDialog(QuizActivity.this, R.style.AppCompatProgressDialogStyle);
            dialog.setContentView(R.layout.progress_dialog);
            progressName = (AppCompatTextView) dialog.findViewById(R.id.progress_name);
            progressName.setText(R.string.message1);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String link = "http://theinspirer.in/BSkill/getQuestion.php?course_id=" + 1;
                URL url = new URL(link);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(35000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                switch (conn.getResponseCode()) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        jsonStr = sb.toString();
                        break;
                    default:
                        jsonStr = "Error";
                }

            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            Log.d("JSONString", jsonStr);

            if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray result = jsonObj.getJSONArray("result");

                    Log.d("JSONArray", String.valueOf(result));

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject c = result.getJSONObject(i);
                        quizID = c.getString("quiz_id");
                        question = c.getString("question");
                        optionA = c.getString("option_a");
                        optionB = c.getString("option_b");
                        optionC = c.getString("option_c");
                        optionD = c.getString("option_d");
                        answer = c.getString("answer");
                        QuestionBean questionBean = new QuestionBean(quizID, question, optionA, optionB, optionC, optionD, answer);
                        questionBeanList.add(questionBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getQuestionBeanListSize = questionBeanList.size();
            setQuestion();
            player_back.setLooping(true);
            player_back.start();
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }
}
