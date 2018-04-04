package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewPackagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_packages);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String hostPort= sP.getString("HOST_PORT",null);

        String URL = "http://"+hostIP+":"+hostPort+"/app/packages/";

//        String URL= "http://10.0.2.2:8080/app/packages/";
        URL = URL + username;
        Log.e("sending GET to URL", URL);

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
                                Log.e("NullPointerException", "thrown while getting packages");
                            }

                            TableLayout pkgTable = findViewById(R.id.pkgsTable);

                            String pkgID = null;
                            final List<JSONObject> pkgs = new ArrayList<JSONObject>();

                            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                                    Context.MODE_PRIVATE);
                            final String user_type = sharedPreferences.getString("usertype", null);
                            try {
                                for (int i = 0; i < packages.length(); i++) {
                                    Log.e("loop", Integer.toString(i));

                                    JSONObject pkg = packages.getJSONObject(i);
                                    Log.e("package", "extracted from JSONArray");
                                    pkgs.add(pkg);
                                    Log.e("package", "added to list");

                                    Log.e("pkg", pkg.toString());

                                    try {
                                        pkgID = pkg.getString("packageID");
                                    } catch(org.json.JSONException e) {
                                        Log.e("json exception", "thrown getting pkgID");
                                    } catch(java.lang.NullPointerException e) {
                                        Log.e("NullPointerException", "thrown while getting pkgID");
                                    }

                                    TableRow trow = new TableRow(ViewPackagesActivity.this);

/*                                    TextView tv = new TextView(ViewPackagesActivity.this);
                                    tv.setText(pkg.toString());*/

                                    Button pkgDetails = new Button(ViewPackagesActivity.this);
                                    pkgDetails.setId(i);
                                    pkgDetails.setText(pkgID);

                                    final int btn_id = pkgDetails.getId();

                                    trow.addView(pkgDetails);

                                    pkgTable.addView(trow);

                                    Button btn = findViewById(btn_id);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            Intent showDeets = null;
                                            if (Objects.equals(user_type, "seller")) {
                                                showDeets = new Intent(
                                                        ViewPackagesActivity.this,
                                                        PackageDetailsActivity.class);
                                            } else if (Objects.equals(user_type, "de")) {
                                                showDeets = new Intent(
                                                        ViewPackagesActivity.this,
                                                        PackageDetailsAlternate.class);
                                            }
                                            showDeets.putExtra("pkgDetails", pkgs.get(btn_id).toString());
                                            Log.e("sending pkg", pkgs.get(btn_id).toString());

                                            startActivity(showDeets);
                                        }
                                    });
                                }
                            } catch (org.json.JSONException e) {
                                Log.e("json exception", "thrown while looping array");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown while looping array");
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
