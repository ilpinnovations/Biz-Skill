package com.example.tcs.bskill.Adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tcs.bskill.Beans.EmployeeBean;
import com.example.tcs.bskill.R;

import java.util.ArrayList;
import java.util.List;


public class LeaderBoardRecycleAdapter extends RecyclerView.Adapter<LeaderBoardRecycleAdapter.ViewHolder> {

    List<EmployeeBean> employeeBeanList = new ArrayList<>();
    AlertDialog.Builder alertDialogBuilder;
    boolean flagwinner = false;

    public LeaderBoardRecycleAdapter(Context context, List<EmployeeBean> employeeBeanList) {
        this.employeeBeanList = employeeBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout_leaderboard, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final EmployeeBean employeeBean;
        employeeBean = employeeBeanList.get(i);
        if (i < 1) {
            if (!flagwinner) {
                viewHolder.topper.setImageResource(R.drawable.winner);
                flagwinner = true;
            }
        }
        viewHolder.lbname.setText(employeeBean.getEmployeeName());
        viewHolder.lbscore.setText(employeeBean.getEmployeeScore());
        viewHolder.lbposition.setText(String.valueOf(i + 1));
    }

    @Override
    public int getItemCount() {
        return employeeBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lbname, lbposition, lbscore;
        ImageView imageurl, topper;

        public ViewHolder(View view) {
            super(view);
            lbscore = (TextView) view.findViewById(R.id.lb_score);
            lbposition = (TextView) view.findViewById(R.id.lb_position);
            lbname = (TextView) view.findViewById(R.id.lb_name);
            imageurl = (ImageView) view.findViewById(R.id.lb_image);
            topper = (ImageView) view.findViewById(R.id.lb_image_topper);
        }
    }
}