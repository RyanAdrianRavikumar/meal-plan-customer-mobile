package com.mealplanmobile.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class AddressActivity extends AppCompatActivity {

    private EditText addressLine;
    private EditText city;
    private EditText state;
    private EditText postalCode;
    private Spinner countrySpinner;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");
        countrySpinner = findViewById(R.id.country);
        addressLine = findViewById(R.id.addressLine);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        postalCode = findViewById(R.id.postalCode);
    }

    public void onAddressBtnClick(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getRequestUrl = "http://192.168.1.13:8080/customers/email?email=" + email;
        Log.d("AddressActivity", "Received email: " + email);

        StringRequest getRequest = new StringRequest(Request.Method.GET, getRequestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int customerId = jsonObject.getInt("customerId");

                            // Start POST request with customerId
                            sendPostRequest(queue, customerId);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GET Error", error.toString());
                    }
                });

        queue.add(getRequest);
    }

    public void sendPostRequest(RequestQueue queue, int customerId) {
            String custAddLine = addressLine.getText().toString().trim();
            String custCity = city.getText().toString().trim();
            String custState = state.getText().toString().trim();
            String custPostalCode = postalCode.getText().toString().trim();
            String custCountry = countrySpinner.getSelectedItem().toString().trim();

            // Validations
            if (custAddLine.isEmpty()) {
                Log.e("ERROR", "Please enter an address.");
                return;
            }
            if (custCity.isEmpty()) {
                Log.e("ERROR", "Please enter a city.");
                return;
            }
            if (custState.isEmpty()) {
                Log.e("ERROR", "Please enter a State or Province.");
                return;
            }
            if (custPostalCode.isEmpty()) {
                Log.e("ERROR", "Please enter postal code.");
                return;
            }

            String url = "http://192.168.1.13:8080/customers/address";

            // Construct POST body
            JSONObject jsonBody = new JSONObject();
            try {
                JSONObject customerObj = new JSONObject();
                customerObj.put("customerId", customerId);

                jsonBody.put("customer", customerObj);
                jsonBody.put("addressLine", custAddLine);
                jsonBody.put("city", custCity);
                jsonBody.put("state", custState);
                jsonBody.put("postalCode", custPostalCode);
                jsonBody.put("country", custCountry);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("POST RESPONSE", "Response: " + response);
                            try {
                                // Try to parse if response becomes JSON in future
                                JSONObject jsonResponse = new JSONObject(response);
                                // Handle JSON response
                            } catch (JSONException e) {
                                // Handle plain text response
                                if (response.equals("Customer")) {
                                    // Success case for current plain text response
                                    Log.d("POST SUCCESS", "Address registered successfully");
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String responseBody = new String(error.networkResponse.data);
                                Log.e("POST ERROR", "Status: " + error.networkResponse.statusCode +
                                        ", Body: " + responseBody);
                            } else {
                                Log.e("POST ERROR", error.toString());
                            }
                        }
                    }) {
                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            queue.add(request);
        }
    }
