package com.example.tripexpensetracker;

import android.content.Intent;
import android.os.Bundle;
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

public class Profile extends AppCompatActivity {

    TextInputEditText username, password, repassword, email;
    Button save;
    final String username_ = SharedPrefManager.getInstance(this).getUsername();
    final String email_ = SharedPrefManager.getInstance(this).getUserEmail();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.profile_username);
        username.setText(username_);

        password = findViewById(R.id.profile_password);
        repassword = findViewById(R.id.profile_re_password);

        email = findViewById(R.id.profile_email);
        email.setText(email_);

        save = findViewById(R.id.buttonSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pass = password.getText().toString().trim();
                final String repass = repassword.getText().toString().trim();

                if(pass.equals(repass)){
                    saveInfo();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Profile.this, "Check Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveInfo(){
        final String updated_email = email.getText().toString().trim();
        final String updated_username = username.getText().toString().trim();
        final String updated_password = password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(getApplicationContext())
                                    .userLogin(
                                            SharedPrefManager.getInstance(getApplicationContext()).getUserId(),
                                            updated_username,
                                            updated_email
                                    );

                        } catch (JSONException e) {
                            Toast.makeText(Profile.this, "Not Working", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username_);
                params.put("updated_user", updated_username);
                params.put("email", updated_email);
                params.put("password", updated_password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}