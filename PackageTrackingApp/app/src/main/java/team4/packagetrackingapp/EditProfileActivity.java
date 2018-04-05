package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class EditProfileActivity extends AppCompatActivity {
    String user_type;
    String newUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences sharedPreferences= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sharedPreferences.getString("HOST_IP",null);
        String hostPort= sharedPreferences.getString("HOST_PORT",null);

        SharedPreferences sharedPreferences2 = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences2.getString("username", null);

        String URL = "http://"+hostIP+":"+hostPort+"/app/profile/"+username;
        System.out.println(URL) ;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject response_jsonObj = null;
                        String code = null, msg = null;
                        Log.e("Rest Response", response.toString());

                        try {
                            response_jsonObj = response.getJSONObject("Response");
                            code = response_jsonObj.getString("error_code");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        } catch(java.lang.NullPointerException e) {
                            Log.e("NullPointerException", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            JSONObject profile;
                            String username, password, name, email, contact;
                            try {
                                profile = response_jsonObj.getJSONObject("profile");

                                username = profile.getString("username");
                                name = profile.getString("name");
                                email = profile.getString("email_id");
                                contact = profile.getString("contact_no");

                                // check if this works or only user_type
                                EditProfileActivity.this.user_type = profile.getString("user_type");

                                EditText username_et = findViewById(R.id.userField);
                                EditText name_et = findViewById(R.id.nameField);
                                EditText email_et = findViewById(R.id.emailField);
                                EditText contact_et = findViewById(R.id.contactField);

                                username_et.setText(username);
                                name_et.setText(name);
                                email_et.setText(email);
                                contact_et.setText(contact);
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }
                        } else {
                            try {
                                msg = response_jsonObj.getString("message");
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }

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

    /** Called when user taps Submit button */
    public void submit(View view) {
        SharedPreferences sharedPreferences= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sharedPreferences.getString("HOST_IP",null);
        String hostPort= sharedPreferences.getString("HOST_PORT",null);

        SharedPreferences sharedPreferences2 = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences2.getString("username", null);

        String URL = "http://"+hostIP+":"+hostPort+"/app/profile/"+username;
        System.out.println(URL) ;

        EditText username_et = findViewById(R.id.userField);
        EditText password_et = findViewById(R.id.passField);
        EditText name_et = findViewById(R.id.nameField);
        EditText email_et = findViewById(R.id.emailField);
        EditText contact_et = findViewById(R.id.contactField);


        String email2 = email_et.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if( TextUtils.isEmpty(username_et.getText())){

            Toast.makeText(getApplicationContext(), "User Name is empty", Toast.LENGTH_SHORT).show();

        }
        else if( TextUtils.isEmpty(name_et.getText())){

            Toast.makeText(getApplicationContext(), "Name is empty", Toast.LENGTH_SHORT).show();

        }
        else if( TextUtils.isEmpty(contact_et.getText())){

            Toast.makeText(getApplicationContext(), "Contact is empty", Toast.LENGTH_SHORT).show();

        }
        else if (!email2.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }
        else {


            JSONObject updateRequest = new JSONObject();

            try {


                updateRequest.put("username", username_et.getText().toString());
                this.newUsername = username_et.getText().toString();
                updateRequest.put("password", password_et.getText().toString());
                updateRequest.put("name", name_et.getText().toString());
                updateRequest.put("email_id", email_et.getText().toString());
                updateRequest.put("contact_no", contact_et.getText().toString());
            } catch (org.json.JSONException e) {
                Log.e("JSONException", "thrown");
            } catch (java.lang.NullPointerException e) {
                Log.e("NullPointerException", "thrown while trying to add to JSONObject");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);


            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    URL,
                    updateRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject response_jsonObj = null;
                            String code = null, msg = null;
                            Log.e("Rest Response", response.toString());

                            try {
                                response_jsonObj = response.getJSONObject("Response");
                                code = response_jsonObj.getString("error_code");
                                msg = response_jsonObj.getString("message");
                            } catch (org.json.JSONException e) {
                                Log.e("json exception", "thrown");
                            } catch (java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }

                            if (Objects.equals(code, "000")) {
                                SharedPreferences sP1 = getSharedPreferences(LoginActivity.MyPREFERENCES,
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = sP1.edit();
                                editor.putString("username", EditProfileActivity.this.newUsername);
                                editor.apply();

                                Toast.makeText(getApplicationContext(), msg,
                                        Toast.LENGTH_LONG).show();

                                if (Objects.equals(EditProfileActivity.this.user_type, "seller")) {
                                    Intent back_to_dash = new Intent(EditProfileActivity.this,
                                            SellerDashboard.class);
                                    startActivity(back_to_dash);
                                } else if (Objects.equals(EditProfileActivity.this.user_type, "de")) {
                                    Intent back_to_dash = new Intent(EditProfileActivity.this,
                                            DeliveryDashboard.class);
                                    startActivity(back_to_dash);
                                } else {
                                    Log.e("error", "no user_type");
                                }
                            } else {
                                try {
                                    msg = response_jsonObj.getString("message");
                                } catch (org.json.JSONException e) {
                                    Log.e("json exception", "thrown");
                                } catch (java.lang.NullPointerException e) {
                                    Log.e("NullPointerException", "thrown");
                                }

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
}
