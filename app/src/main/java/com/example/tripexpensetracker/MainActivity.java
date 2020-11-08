package com.example.tripexpensetracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Drawer variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView textViewUsername, textViewUserEmail;

    //
    private static final String TAG = "Location";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //
    CardView locationButton, expenseButton;
    Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonClick();

        isServicesOk();
        createDrawerLayout();
    }

    public void buttonClick(){
        locationButton = findViewById(R.id.location_button_dashboard);
        expenseButton = findViewById(R.id.expenses_button_dashboard);
        logoutButton = findViewById(R.id.logout_button_dashboard);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShowLocation.class));
            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ExpenseTracker.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
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
    //Checking google services
    public void isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
//            return true;
            //Toast.makeText(this, "Services Running", Toast.LENGTH_SHORT).show();

        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOk: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
//        return false;
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