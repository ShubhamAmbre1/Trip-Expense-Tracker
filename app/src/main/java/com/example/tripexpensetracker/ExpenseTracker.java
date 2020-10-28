package com.example.tripexpensetracker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ExpenseTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    //Drawer variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView textViewUsername, textViewUserEmail;

    //Checkpermission
    private static final String CAMERA = Manifest.permission.CAMERA;


    //Testing Recycler View
    private static final String TAG = "Expense Tracker";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    //Floating action button
    FloatingActionButton fab;

    //Camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);

        getCameraPermission();
        bottomSheetDrawer();
        createDrawerLayout();
        initImageBitmap();
    }

    private void getCameraPermission() {
        String[] permissions = {CAMERA};

        if (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_IMAGE_CAPTURE);
        }

    }


    void bottomSheetDrawer(){
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        ExpenseTracker.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_drawer_layout,
                                (LinearLayout)findViewById(R.id.bottom_sheet)
                        );
                bottomSheetView.findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            // display error state to the user
                            Toast.makeText(ExpenseTracker.this, "Error Taking Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bottomSheetView.findViewById(R.id.add_expense).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TO DO: add expense and images in the database
                        Toast.makeText(ExpenseTracker.this, "Expense Added", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    //to generate random file name
                    String fileName = "tempimg.jpg";


                    try {
                        Bundle extras = data.getExtras();
                        Bitmap photo = (Bitmap) extras.get("data");
                        //captured image set in imageview
                        imageView = findViewById(R.id.show_image);
                        imageView.setImageBitmap(photo);
                    } catch (Exception e) {
                        Toast.makeText(this, "Not displaying image", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
                    }
                }
        }
    }

    private void initImageBitmap(){
        Log.d(TAG, "initImageBitmap: prepareing bitmap.");

        mNames.add("Shubham Ambre");
        mNames.add("Aditya Sarwankar");
        mNames.add("Nitesh Chawan");
        mNames.add("Mandar noob");
        mNames.add("Chotu");
        mNames.add("Sarvesh");
        mNames.add("Shubham Ambre");
        mNames.add("Aditya Sarwankar");
        mNames.add("Nitesh Chawan");
        mNames.add("Mandar noob");
        mNames.add("Chotu");


        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecylerView: init recyclerview.");

        //Get Recycler view from xml
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //Create RecyclerViewAdapter fetching data from ArrayLists. Using RecyclerViewAdapter class we created
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames);

        //Output ArrayAdapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        //Display recyclerView in linear horizontal format
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }











    //For Drawer
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // To display options menu on the display
    // And also display the items for the menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.nav_location:

//                if(isServicesOk()){
                startActivity(new Intent(this, ShowLocation.class));
//                }
                break;
            case R.id.nav_expenses:
                startActivity(new Intent(this, ExpenseTracker.class));
                break;
            case R.id.nav_dashboard:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }

    //Creates Drawer
    public void createDrawerLayout(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        TextView textViewUserEmail = findViewById(R.id.display_user_email);
        TextView textViewUsername = findViewById(R.id.display_user_name);

        //textViewUserEmail.setText("abcd");
        //textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        View headerView = navigationView.getHeaderView(0);
        textViewUsername = headerView.findViewById(R.id.display_user_name);
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

        textViewUserEmail = headerView.findViewById(R.id.display_user_email);
        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
    }

}