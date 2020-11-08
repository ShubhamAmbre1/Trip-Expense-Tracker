package com.example.tripexpensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class Login extends AppCompatActivity {

    private TextInputEditText username_, password_;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private TextView signUp;

    public static int status = 0;

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        //To check if user is already logged in.
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            //TO DO: Redirect to startTripACtivity if trip is not start else
            //if(tripActive()){

             //   Log.d(TAG, "tripActive(): Working");
              //  Toast.makeText(this, "TripActiveWorks", Toast.LENGTH_SHORT).show();
           //j     startActivity(new Intent(this, StartTripActivity.class));
            //} else {
                startActivity(new Intent(this, MainActivity.class));
            //}
            return;
        }

        username_ = findViewById(R.id.username);
        password_ = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        clickableSignUpText();
    }

    private boolean tripActive(){
        final String username = SharedPrefManager.getInstance(this).getUsername();

        Log.d(TAG, "tripActive(): is running");
        //Check trip status
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_TRIP_ACTIVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();


                        Log.d(TAG, "tripActive(): getting response");
                        try{
                            JSONObject obj = new JSONObject(response);
                            Log.d(TAG, "tripActive(): Working");
                            Toast.makeText(getApplicationContext(), obj.getInt("activeStatus"), Toast.LENGTH_SHORT).show();
                            if(!obj.getBoolean("error")){
                                status = obj.getInt("activeStatus");
                                Toast.makeText(Login.this, obj.getInt("activeStatus"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e){
                            Toast.makeText(Login.this, "Not Getting requests Working", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "tripActive(): Try not working");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
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
        if (status == 0){
            return false;
        }
        return true;
    }

    private void userLogin(){
        final String username = username_.getText().toString().trim();
        final String password = password_.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("username"),
                                                obj.getString("email")
                                        );
                                //finish();

                                Log.d(TAG, "tripActive(): Working login working");
                                //if(tripActive()){
                                  //  startActivity(new Intent(getApplicationContext(), StartTripActivity.class));
                                   // Toast.makeText(getApplicationContext(), obj.getInt("activeStatus"), Toast.LENGTH_SHORT).show();
                               // } else {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                //}
                            }else{
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
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
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void clickableSignUpText(){
        signUp = findViewById(R.id.signUpText);

        String text = "Don't have an account? Sign Up";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        };

        ss.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUp.setText(ss);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}