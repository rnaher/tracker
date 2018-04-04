package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Objects;

public class PackageDetailsActivity extends AppCompatActivity {
    String packageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        String pkgDetails_string = getIntent().getStringExtra("pkgDetails");

        JSONObject pkgDetails = null, buyerDetails = null;
        try {
            pkgDetails = new JSONObject(pkgDetails_string);
            buyerDetails = pkgDetails.getJSONObject("Buyer_details");
            Log.e("string received", pkgDetails.toString());
            Log.e("buyer details", buyerDetails.toString());
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        }

        TextView pkgID = findViewById(R.id.idField);
        TextView status = findViewById(R.id.statusField);
        TextView seller = findViewById(R.id.sellerField);
        TextView de = findViewById(R.id.deField);
        TextView dest = findViewById(R.id.destField);
        TextView name = findViewById(R.id.nameField);
        TextView contact = findViewById(R.id.contactField);

        try {
            pkgID.setText(pkgDetails.getString("packageID"));
            status.setText(pkgDetails.getString("status"));
            seller.setText(pkgDetails.getString("seller"));
            dest.setText(pkgDetails.getString("destination"));
            name.setText(buyerDetails.getString("name"));
            contact.setText(buyerDetails.getString("contactNo"));

            this.packageID = pkgDetails.getString("packageID");
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown");
        }

        try {
            de.setText(pkgDetails.getString("DE"));
        } catch(org.json.JSONException e) {
            de.setText("Not Assigned");
            Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown");
    }
    }

    public void trackPackage(View view) {
        String pkgDetails_string = getIntent().getStringExtra("pkgDetails");

        JSONObject pkgDetails = null;
        try {
            pkgDetails = new JSONObject(pkgDetails_string);
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        }

        Intent intent = new Intent(this, TrackResultActivity.class);

        try {
            intent.putExtra("packageID", pkgDetails.getString("packageID"));
            Log.e("tracking pkgID", pkgDetails.getString("packageID"));
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown");
        }

        startActivity(intent);
    }

    public void contactDE(View view) {
        // all numbers are prefixed with 91 for India

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String hostPort= sP.getString("HOST_PORT",null);
        String packageID= this.packageID;
        String URL = "http://"+hostIP+":"+hostPort+"/app/de/"+packageID+"/"+username;
        System.out.println(URL);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String contact = null;
                        String number = "tel:+91-";
                        JSONObject response_jsonObj = null;
                        String code = null, msg = null;
                        Log.e("Rest Response", response.toString());

                        try {
                            response_jsonObj = response.getJSONObject("Response");
                            code = response_jsonObj.getString("error_code");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            try {
                                contact = response_jsonObj.getString("contact_no");
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown");
                            }

                            number = number+contact;
                            Log.e("number", number);

                            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                            phoneIntent.setData(Uri.parse(number));

                            startActivity(phoneIntent);
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
}
