package com.example.tripexpensetracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

            View bottomSheetView;
    //Initialize variables for imagesNames and imagesUrls
    private ArrayList<String> mImageNames;
    private ArrayList<String> mAmounts;
    private ArrayList<String> mMessage;

    private Context mContext;

    //***************************************************************************************************************************************
    //RecyclerViewAdapter : Constructor
    public SummaryAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mAmounts, ArrayList<String> mMessage){
            this.mImageNames = mImageNames;
            this.mMessage = mMessage;
            this.mAmounts = mAmounts;
            this.mContext = mContext;
            }


    //Create a view holder, which means it creates the box in which it fits in the main activity
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_recycler_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Sets Images and ImageNames to the adapter
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //Add ImageNames alongside the images
        holder.imageName.setText(mImageNames.get(position));
        holder.amount.setText(mAmounts.get(position));
        holder.message.setText(mMessage.get(position));
    }

    //returns the size of the Adapter
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    //***************************************************************************************************************************************

    //Class ViewHolder to
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView imageName, amount, message;
        RelativeLayout parentLayout;

        //ViewHolder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageName = itemView.findViewById(R.id.name_of_member);
            message = itemView.findViewById(R.id.recycler_view_message);
            amount = itemView.findViewById(R.id.summary_expense);
            parentLayout = itemView.findViewById(R.id.summary_parent_layout);
        }
    }
    //***************************************************************************************************************************************
}