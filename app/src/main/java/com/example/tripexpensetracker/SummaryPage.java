package com.example.tripexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SummaryPage extends AppCompatActivity {

    //Testing Recycler View
    private static final String TAG = "Summary";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mAmounts = new ArrayList<>();
    private ArrayList<String> mMessages = new ArrayList<>();

    //Recycler View Adapter
    SummaryAdapter adapter;

    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_page);

        initImageBitmap();

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTrip();
            }
        });
    }

    private void initImageBitmap(){
        Log.d(TAG, "initImageBitmap: prepareing bitmap.");

        final String username = SharedPrefManager.getInstance(this).getUsername();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SUMMARY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "gettingExpense(): getting response");
                        try{

                            JSONObject obj = new JSONObject(response);
                            int total_people = Integer.parseInt(obj.getString("total_people"));
                            int expense;
                            if (obj.getString("current_total_expense").equals("null")) {
                                expense = 0;
                            } else {
                                expense = Integer.parseInt(obj.getString("current_total_expense"));
                            }
                            Double per_person = (double)expense/total_people;

                            JSONArray arr = obj.getJSONArray("names");

                            for(int i=0; i<arr.length(); i++){
                                JSONObject obj1 = arr.getJSONObject(i);
                                String name = obj1.getString("name");
                                String amount = obj1.getString("cost");

                                Toast.makeText(SummaryPage.this, "Success", Toast.LENGTH_SHORT).show();
                                mNames.add(name);
                                int amt = Integer.parseInt(amount);

                                if (Integer.parseInt(amount) < per_person){
                                    mAmounts.add(Double.toString(per_person - (double)amt));
                                    mMessages.add("Should pay");
                                } else {
                                    mAmounts.add(Double.toString((double)amt-per_person));
                                    mMessages.add("Should receive");
                                }
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
        RecyclerView recyclerView = findViewById(R.id.summary_recycler_view);

        //Create RecyclerViewAdapter fetching data from ArrayLists. Using RecyclerViewAdapter class we created
        adapter = new SummaryAdapter(this, mNames, mAmounts, mMessages);

        //Output ArrayAdapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        //Display recyclerView in linear horizontal format
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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