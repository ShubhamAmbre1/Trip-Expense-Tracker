package com.example.tripexpensetracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    //Initialize variables for imagesNames and imagesUrls
    private ArrayList<String> mImageNames = new ArrayList<>();
//    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    //***************************************************************************************************************************************
    //RecyclerViewAdapter : Constructor
    //It takes in 3 arguments Context: calling class name, imageNames, imageUrl
    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames){
        this.mImageNames = mImageNames;
//        this.mImages = mImages;
        this.mContext = mContext;
    }

    //***************************************************This block len(ArrayList) times*****************************************************************

    //Create a view holder, which means it creates the box in which it fits in the main activity
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_expense_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Sets Images and ImageNames to the adapter
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //Glide is used to convert imageUrls into bitmap
//        Glide.with(mContext)
//                .asBitmap()
//                .load(mImages.get(position))
//                .into(holder.image);//this line adds the image and its name to the view

        //Add ImageNames alongside the images
        holder.imageName.setText(mImageNames.get(position));

        //onClickListener to tell user that it has been clicked
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: "+ mImageNames.get(position));

                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //returns the size of the Adapter
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    //***************************************************************************************************************************************

    //Class ViewHolder to
    public class ViewHolder extends RecyclerView.ViewHolder{

//        ImageView image;
        TextView imageName;
        RelativeLayout parentLayout;

        //ViewHolder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    //***************************************************************************************************************************************
}

