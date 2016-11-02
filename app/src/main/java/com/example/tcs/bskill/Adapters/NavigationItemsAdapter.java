package com.example.tcs.bskill.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tcs.bskill.Activities.MainActivity;
import com.example.tcs.bskill.R;

import java.util.List;



public class NavigationItemsAdapter extends RecyclerView.Adapter<NavigationItemsAdapter.MyViewHolder> {

    public Context context;
    public List<String> navItemsList;
    public List<Integer> navItemsIconsList;
    MainActivity mainActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView navItemName;
        public RelativeLayout cardView;
        public ImageView navItemIcon;

        public MyViewHolder(View view) {
            super(view);
            cardView = (RelativeLayout) view.findViewById(R.id.cardView);
            navItemName = (TextView) view.findViewById(R.id.nav_item_name);
            navItemIcon = (ImageView) view.findViewById(R.id.nav_item_icon);
        }
    }

    public NavigationItemsAdapter(List<String> navItemsList, List<Integer> navItemsIconsList, Context context) {
        this.navItemsList = navItemsList;
        this.navItemsIconsList = navItemsIconsList;
        this.context = context;
        this.mainActivity = (MainActivity)context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String itemName = navItemsList.get(position);
        final int itemIcon = navItemsIconsList.get(position);
        holder.navItemName.setText(itemName);
        holder.navItemIcon.setImageResource(itemIcon);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onNavigationItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return navItemsList.size();
    }
}