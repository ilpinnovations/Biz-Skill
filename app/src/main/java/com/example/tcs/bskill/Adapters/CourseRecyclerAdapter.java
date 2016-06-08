package com.example.tcs.bskill.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tcs.bskill.Beans.CourseBean;
import com.example.tcs.bskill.Beans.CourseDetailsBean;
import com.example.tcs.bskill.Databases.DatabaseHandlerCourseStatus;
import com.example.tcs.bskill.Interfaces.Communicator;
import com.example.tcs.bskill.R;

import java.util.ArrayList;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.RecycleViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<CourseBean> courseBeanList = new ArrayList<CourseBean>();
    Communicator communicator;
    DatabaseHandlerCourseStatus db;
    Context context;

    public CourseRecyclerAdapter(Context context, ArrayList<CourseBean> courseBeanList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.courseBeanList = courseBeanList;
        this.communicator = (Communicator) context;
        this.context = context;
    }

    @Override
    public CourseRecyclerAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView;
        int cardNumber = viewType + 1;

        if (cardNumber > 6) {
            cardNumber = cardNumber % 6;
        }

        switch (cardNumber) {
            case 1:
                cardView = layoutInflater.inflate(R.layout.course_card1, parent, false);
                break;
            case 2:
                cardView = layoutInflater.inflate(R.layout.course_card2, parent, false);
                break;
            case 3:
                cardView = layoutInflater.inflate(R.layout.course_card3, parent, false);
                break;
            case 4:
                cardView = layoutInflater.inflate(R.layout.course_card4, parent, false);
                break;
            case 5:
                cardView = layoutInflater.inflate(R.layout.course_card5, parent, false);
                break;
            case 6:
                cardView = layoutInflater.inflate(R.layout.course_card6, parent, false);
                break;
            default:
                cardView = layoutInflater.inflate(R.layout.course_card1, parent, false);
        }

        RecycleViewHolder recycleViewHolder = new RecycleViewHolder(cardView);

        Log.d("cardNumber", String.valueOf(cardNumber));

        return recycleViewHolder;
    }

    @Override
    public void onBindViewHolder(CourseRecyclerAdapter.RecycleViewHolder holder, int position) {
        final CourseBean currentData = courseBeanList.get(position);
        holder.courseName.setText(currentData.getCourseName());

        db = new DatabaseHandlerCourseStatus(context);
        CourseDetailsBean courseDetailsBean = db.getCourseDetailsByID(currentData.getCourseID());

        holder.courseProgress.setText(courseDetailsBean.getCourseProgress() + "%");
        holder.courseProgressBar.setSecondaryProgress(100);
        holder.courseProgressBar.setProgress(Integer.parseInt(courseDetailsBean.getCourseProgress()));
        //holder.courseProgressBar.setProgress(0);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.callBack(currentData);

                Log.d("courseID", currentData.getCourseID());
                Log.d("courseName", currentData.getCourseName());
                Log.d("courseDesc", currentData.getCourseDesc());
                Log.d("courseAudioURL", currentData.getCourseAudioURL());
                Log.d("courseVideoURL", currentData.getCourseVideoURL());
            }
        });

    }

    @Override
    public int getItemCount() {
        return courseBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {

        TextView courseName, courseProgress;
        CardView cardView;
        ProgressBar courseProgressBar;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            courseName = (TextView) itemView.findViewById(R.id.courseName);
            cardView = (CardView) itemView.findViewById(R.id.CV);
            courseProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            courseProgress = (TextView) itemView.findViewById(R.id.courseProgress);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}