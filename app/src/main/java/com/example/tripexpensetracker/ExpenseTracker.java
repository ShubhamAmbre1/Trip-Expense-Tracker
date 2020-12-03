package com.example.tripexpensetracker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<String> mAmounts = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();

    //Floating action button
    FloatingActionButton fab;

    //Camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    View bottomSheetView;

    //Edit created list item
    private Button edit;

    //To send the images
    Bitmap photo;
    String imgstring;

    //for bottom drawer
    EditText amount, name;

    //Recycler View Adapter
    RecyclerViewAdapter adapter;
    //display info variables
    TextView current_expense, total_people, budget, per_person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);

        getCameraPermission();
        bottomSheetDrawer();
        createDrawerLayout();
        initImageBitmap();

        info();


    }

    private void info(){
        current_expense = findViewById(R.id.expense_current_expense);
        total_people = findViewById(R.id.expense_total_people);
        budget = findViewById(R.id.expense_budget);
        per_person = findViewById(R.id.expense_price_per_member);

        final String username = SharedPrefManager.getInstance(this).getUsername();
        Toast.makeText(this, "Updating dash", Toast.LENGTH_SHORT).show();
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
                                budget.setText("Rs. " +obj.getString("total_expense"));
                                int total_people = Integer.parseInt(obj.getString("total_people"));
                                int expense;
                                if (obj.getString("current_total_expense").equals("null")) {
                                    expense = 0;
                                } else {
                                    expense = Integer.parseInt(obj.getString("current_total_expense"));
                                }
                                per_person.setText("Rs. " +Double.toString((double)expense/total_people));
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
                            Toast.makeText(ExpenseTracker.this, "Taking Photo", Toast.LENGTH_SHORT).show();
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
                        amount = bottomSheetView.findViewById(R.id.amountPaid);
                        name = bottomSheetView.findViewById(R.id.bottomdrawername);

                        addExpense();
                        mNames.add(name.getText().toString());
                        mAmounts.add(amount.getText().toString());
                        mImages.add(imgstring);
                        initRecyclerView();

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
                                info();
                                Toast.makeText(ExpenseTracker.this, "Expense Added", Toast.LENGTH_SHORT).show();
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
                                initRecyclerView();
                            }

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

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecylerView: init recyclerview.");

        //Get Recycler view from xml
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //Create RecyclerViewAdapter fetching data from ArrayLists. Using RecyclerViewAdapter class we created
        adapter = new RecyclerViewAdapter(this, mNames, mAmounts, mImages);

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
                startActivity(new Intent(this, ShowLocation.class));
                break;
            case R.id.nav_expenses:
                startActivity(new Intent(this, ExpenseTracker.class));
                break;
            case R.id.nav_dashboard:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.endTrip:
                endTrip();
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
}