package com.example.tripexpensetracker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    View bottomSheetView;
    //Initialize variables for imagesNames and imagesUrls
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mAmounts = new ArrayList<>();

    private Context mContext;
    private static final String TAG = "Recycler View";

    private ImageView dial_image;
    //***************************************************************************************************************************************
    //RecyclerViewAdapter : Constructor
    //It takes in 3 arguments Context: calling class name, imageNames, imageUrl
    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mAmounts, ArrayList<String> mImages){
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mAmounts = mAmounts;
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



        //Add ImageNames alongside the images
        holder.imageName.setText(mImageNames.get(position));
        holder.amount.setText(mAmounts.get(position));
        byte[] decodeString = Base64.decode(mImages.get(position), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        holder.image.setImageBitmap(bitmap);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final Dialog myDialog = new Dialog(mContext);
               myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
               myDialog.setContentView(R.layout.dialog_image);
               dial_image = myDialog.findViewById(R.id.dial_image);
                byte[] decodeString = Base64.decode(mImages.get(position), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                dial_image.setImageBitmap(bitmap);
               myDialog.show();
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

        ImageView image;
        TextView imageName, amount;
        RelativeLayout parentLayout;
        Button editExpense;

        //ViewHolder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.show_image_test);
            imageName = itemView.findViewById(R.id.image_name);
            amount = itemView.findViewById(R.id.recyclerViewAmountt);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    //***************************************************************************************************************************************
}







































//useless code in onBindViewHolder()
//onClickListener to tell user that it has been clicked
//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked on: "+ mImageNames.get(position));
//
//                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
//            }
//        });

//        holder.editExpense.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //For editing the expenses
//                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
//                        ExpenseTracker.this, R.style.BottomSheetDialogTheme);
//                bottomSheetView = LayoutInflater.from(getApplicationContext())
//                        .inflate(
//                                R.layout.bottom_drawer_layout,
//                                (LinearLayout)findViewById(R.id.bottom_sheet)
//                        );
//                bottomSheetView.findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        try {
//                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                            Toast.makeText(ExpenseTracker.this, "Taking Photo", Toast.LENGTH_SHORT).show();
//                        } catch (ActivityNotFoundException e) {
//                            // display error state to the user
//                            Toast.makeText(ExpenseTracker.this, "Error Taking Image", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                bottomSheetView.findViewById(R.id.add_expense).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //TO DO: add expense and images in the database
//                        Toast.makeText(ExpenseTracker.this, "Expense Added", Toast.LENGTH_SHORT).show();
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();
//            }
//        });
