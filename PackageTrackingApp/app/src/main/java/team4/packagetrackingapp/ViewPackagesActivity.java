package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class ViewPackagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_packages);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        String URL= "http://10.0.2.2:8080/app/packages/";
        URL = URL + username;

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
                        }

                        if(Objects.equals(code, "000")) {
                            Toast.makeText(getApplicationContext(), "Records Retrieved",
                                    Toast.LENGTH_LONG).show();

                            JSONArray packages = null;
                            try {
                                packages = response_jsonObj.getJSONArray("packages");
                                Log.e("packages jsonarray", packages.toString());
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown while getting packages");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }

                            TableLayout pkgTable = findViewById(R.id.pkgsTable);

                            try {
                                for (int i = 0; i < packages.length(); i++) {
                                    JSONObject pkg = packages.getJSONObject(i);
                                    Log.e("pkg", pkg.toString());

                                    HorizontalScrollView hsView = new
                                            HorizontalScrollView(ViewPackagesActivity.this);

                                    TableRow trow = new TableRow(ViewPackagesActivity.this);

                                    TextView tv = new TextView(ViewPackagesActivity.this);
                                    tv.setText(pkg.toString());
                                    trow.addView(tv);

                                    hsView.addView(trow);
                                    pkgTable.addView(hsView);
                                }

                                for (int i = 0; i < 100; i++) {
                                    JSONObject pkg = packages.getJSONObject(0);
                                    Log.e("pkg", pkg.toString());

                                    HorizontalScrollView hsView = new
                                            HorizontalScrollView(ViewPackagesActivity.this);

                                    TableRow trow = new TableRow(ViewPackagesActivity.this);

                                    TextView tv = new TextView(ViewPackagesActivity.this);
                                    tv.setText(pkg.toString());
                                    trow.addView(tv);

                                    hsView.addView(trow);
                                    pkgTable.addView(hsView);
                                }
                            } catch (org.json.JSONException e) {
                                Log.e("json exception", "thrown while looping array");
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
}
