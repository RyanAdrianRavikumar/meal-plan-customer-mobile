package com.mealplanmobile.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private EditText fullName;
    private EditText customerEmail;
    private EditText phone;
    private EditText customerPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullName = findViewById(R.id.fullName);
        customerEmail = findViewById(R.id.customerEmail);
        phone = findViewById(R.id.phone);
        customerPassword = findViewById(R.id.customerPassword);
    }

    public void navigateToLogin(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onRegisterClick(View view){
        sendPostRequest();
    }

    public void sendPostRequest(){
        String name = fullName.getText().toString().trim();
        String email = customerEmail.getText().toString().trim();
        String customerPhone = phone.getText().toString().trim();
        String pass = customerPassword.getText().toString().trim();


        if(email.isEmpty() || pass.isEmpty()){
            Log.e("ERROR", "Email or Password is empty");
            return;
        }

        String url = "http://192.168.1.13:8080/customers/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("customerName", name);
            jsonBody.put("customerEmail", email);
            jsonBody.put("customerPhone", customerPhone);
            jsonBody.put("customerPassword", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response); // Will print: Customer
                        if (response.equalsIgnoreCase("Customer registered successfully.")) {
                            Intent intent = new Intent(RegistrationActivity.this, AddressActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        Log.e("ERROR", error.toString());
                    }
                }) {
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(request);
    }

}