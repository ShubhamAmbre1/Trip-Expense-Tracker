package com.example.tripexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StartTripActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextInputEditText total_people, expected_expense, names, loc;
    private Button start, logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);

        start = findViewById(R.id.buttonstarttrip);
        logout = findViewById(R.id.buttonLogout);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_people = findViewById(R.id.totalPeople);
                expected_expense = findViewById(R.id.expectedExpense);
                names = findViewById(R.id.names);
                loc = findViewById(R.id.loc);
                startTrip();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void startTrip(){

        final String username = SharedPrefManager.getInstance(this).getUsername();
        final String total_people_ = total_people.getText().toString().trim();
        final String expected_expense_ = expected_expense.getText().toString().trim();
        final String names_ = names.getText().toString().trim();
        final String loc_ = loc.getText().toString().trim();

        Log.d(TAG, "startTrip(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SET_TRIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "startTrip(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                params.put("total_people", total_people_);
                params.put("total_expense", expected_expense_);
                params.put("loc", loc_);
                params.put("names", names_);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}