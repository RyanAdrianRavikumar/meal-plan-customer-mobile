package com.mealplanmobile.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText customerEmail;
    private EditText customerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        customerEmail = findViewById(R.id.customerEmail);
        customerPassword = findViewById(R.id.customerPassword);
    }

    public void onLoginClick(View view){
        sendPostRequest();
    }

    public void sendPostRequest(){
        String email = customerEmail.getText().toString().trim();
        String pass = customerPassword.getText().toString().trim();

        if(email.isEmpty() || pass.isEmpty()){
            Log.e("ERROR", "Email or Password is empty");
            return;
        }

        String url = "http://192.168.1.15:8080/customers/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("customerEmail", email);
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
                        // You can now handle "Customer", "Admin", etc.
                        if (response.equalsIgnoreCase("Customer")) {
                            // Navigate to customer dashboard, etc.
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
