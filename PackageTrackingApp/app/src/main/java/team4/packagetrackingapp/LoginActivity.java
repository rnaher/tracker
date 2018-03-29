package team4.packagetrackingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /** Called when user taps Submit button */
    public void submitLogin(View view) {
        EditText username = findViewById(R.id.userField);
        EditText password = findViewById(R.id.passField);

        Log.e("username", username.getText().toString());
        Log.e("password", password.getText().toString());

        JSONObject loginDetails = new JSONObject();

        try {
            loginDetails.put("username", username.getText().toString());
            loginDetails.put("password", password.getText().toString());
        } catch(org.json.JSONException e) {
                Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
                Log.e("NullPointerException", "thrown while trying to add to JSONObject");
        }
/*
        try {
            Log.e("login details", loginDetails.toString());
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown while trying to print JSONObject");
        }*/

        String URL= "http://10.0.2.2:8080/app/login";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                loginDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject response_jsonObj;
                        String code = null, msg = null, user_type = null;
                        Log.e("Rest Response", response.toString());

                        try {
                            response_jsonObj = response.getJSONObject("Response");
                            code = response_jsonObj.getString("error_code");
                            msg = response_jsonObj.getString("message");
                            user_type = response_jsonObj.getString("user_type");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_LONG).show();

                            Log.e("user_type", user_type);
                        } else {
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Error", error.toString());
                    }
                });

        requestQueue.add(objectRequest);
    }
}