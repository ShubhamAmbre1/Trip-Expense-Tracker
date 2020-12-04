package com.example.tripexpensetracker;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Drawer variables

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView textViewUsername, textViewUserEmail;
    //
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    CardView locationButton, expenseButton, imageButton, profileButton;
    Button logoutButton;
    String isActive;

    //display info variables
    TextView current_expense, total_people, budget, destination_location;

    //To send the images
    Bitmap photo;
    String imgstring;
    private ImageView imageView;
    View bottomSheetView;

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mAmounts = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();

    RecyclerViewAdapter adapter;
    EditText amount, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initRecyclerView();
        initImageBitmap();
        buttonClick();

        isServicesOk();
        createDrawerLayout();

        //check Trip if exists
        checkTrip();

        //display trip info
        info();

    }
    private void info(){
        current_expense = findViewById(R.id.main_activity_current_expense);
        total_people = findViewById(R.id.main_activity_total_people);
        budget = findViewById(R.id.main_activity_budget);
        destination_location = findViewById(R.id.main_activity_location);

        final String username = SharedPrefManager.getInstance(this).getUsername();

        Log.d(TAG, "endTrip(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DASHBOARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "info(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), "WORKING", Toast.LENGTH_SHORT).show();
                            if(!obj.getBoolean("error")){
                                if(obj.getString("current_total_expense").equals("null")){
                                    current_expense.setText("Rs. 0");
                                } else {
                                    current_expense.setText("Rs. " + obj.getString("current_total_expense"));
                                }
                                total_people.setText(obj.getString("total_people"));
                                budget.setText("Rs. " + obj.getString("total_expense"));
                                destination_location.setText(obj.getString("location"));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "info(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    public void buttonClick(){
        locationButton = findViewById(R.id.location_button_dashboard);
        expenseButton = findViewById(R.id.expenses_button_dashboard);
        logoutButton = findViewById(R.id.logout_button_dashboard);
        imageButton = findViewById(R.id.image_button_dashboard);
        profileButton = findViewById(R.id.profile_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            MainActivity.this, R.style.BottomSheetDialogTheme);
                    bottomSheetView = LayoutInflater.from(getApplicationContext())
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
                                Toast.makeText(MainActivity.this, "Taking Photo", Toast.LENGTH_SHORT).show();
                            } catch (ActivityNotFoundException e) {
                                // display error state to the user
                                Toast.makeText(MainActivity.this, "Error Taking Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    bottomSheetView.findViewById(R.id.add_expense).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TO DO: add expense and images in the database
                            amount = bottomSheetView.findViewById(R.id.amountPaid);
                            name = bottomSheetView.findViewById(R.id.bottomdrawername);

                            addExpense();
//                            mNames.add(name.getText().toString());
//                            mAmounts.add(amount.getText().toString());
//                            mImages.add(imgstring);

                            adapter = new RecyclerViewAdapter(MainActivity.this, mNames, mAmounts, mImages);
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                    Toast.makeText(MainActivity.this, "Taking Photo", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                    Toast.makeText(MainActivity.this, "Error Taking Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        photo = (Bitmap) data.getExtras().get("data");
        imageView = bottomSheetView.findViewById(R.id.show_image);
        imageView.setImageBitmap(photo);
    }

    private void addExpense(){
        final String username = SharedPrefManager.getInstance(this).getUsername();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        imgstring = android.util.Base64.encodeToString(bytes, Base64.DEFAULT);

        Log.d(TAG, "endTrip(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_EXPENSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "addExpense(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                Toast.makeText(MainActivity.this, "Expense Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "tripActive(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("image", imgstring);
                params.put("title", Integer.toString(adapter.getItemCount() + 1));
                params.put("name", name.getText().toString());
                params.put("cost", amount.getText().toString());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
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
    private void initImageBitmap(){
        Log.d(TAG, "initImageBitmap: prepareing bitmap.");

        final String username = SharedPrefManager.getInstance(this).getUsername();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_RECYCLER_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "gettingExpense(): getting response");
                        try{
                            JSONArray result = new JSONArray(response);
                            for(int i=0; i<result.length(); i++){
                                JSONObject obj = result.getJSONObject(i);
                                String name = obj.getString("name");
                                String amount = obj.getString("cost");
                                String image = obj.getString("image");
                                mNames.add(name);
                                mAmounts.add(amount);
                                mImages.add(image);
                            }

                            adapter = new RecyclerViewAdapter(MainActivity.this, mNames, mAmounts, mImages);

                        } catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "getExpense(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        //initRecyclerView();
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

            case R.id.endTrip:
                //endTrip();
                startActivity(new Intent(this, SummaryPage.class));
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

    private void endTrip(){
        final String username = SharedPrefManager.getInstance(this).getUsername();

        Log.d(TAG, "endTrip(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_END_TRIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "tripActive(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                startActivity(new Intent(getApplicationContext(), StartTripActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "tripActive(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void checkTrip(){
        final String username = SharedPrefManager.getInstance(this).getUsername();

        Log.d(TAG, "checkTrip(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_TRIP_ACTIVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "tripActive(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                isActive = obj.getString("activeStatus");
                                if (isActive.equals("0")){
                                    startActivity(new Intent(getApplicationContext(), StartTripActivity.class));
                                } else {
                                    return;
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "tripActive(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}