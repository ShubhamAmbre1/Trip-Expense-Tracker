package com.example.tripexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ExpenseTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Drawer variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView textViewUsername, textViewUserEmail;


    //Testing Recycler View
    private static final String TAG = "Expense Tracker";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);

        createDrawerLayout();
        initImageBitmap();
    }

    private void initImageBitmap(){
        Log.d(TAG, "initImageBitmap: prepareing bitmap.");

//        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("Shubham Ambre");

//        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Aditya Sarwankar");

//        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Nitesh Chawan");

//        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Mandar noob");


//        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Chotu");

//        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Sarvesh");


//

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