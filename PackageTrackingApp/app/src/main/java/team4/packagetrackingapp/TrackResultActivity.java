package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import android.widget.ProgressBar;

public class TrackResultActivity extends AppCompatActivity {

    public void populate_current_status() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String hostPort= sP.getString("HOST_PORT",null);
        String packageID= getIntent().getExtras().getString("packageID");
        String URL = "http://"+hostIP+":"+hostPort+"/app/status/"+packageID+"/"+username;
        System.out.println(URL);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status = null;

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
                                status = response_jsonObj.getString("status");
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }

                            TextView statusView = findViewById(R.id.statusField);
                            statusView.setText(status);
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

    private int progressStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_result);
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar3);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String hostPort= sP.getString("HOST_PORT",null);
        String packageID= getIntent().getExtras().getString("packageID");
        String URL = "http://"+hostIP+":"+hostPort+"/app/track/"+packageID+"/"+username;
        System.out.println(URL);

        populate_current_status();

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
                            progressStatus = response_jsonObj.getInt("count");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            Toast.makeText(getApplicationContext(), "track details Retrieved",
                                    Toast.LENGTH_LONG).show();

                            JSONArray details = null;
                            try {
                                details = response_jsonObj.getJSONArray("details");
                                Log.e("track jsonarray", details.toString());
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown while getting packages");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown");
                            }

                            String data_to_show="";
                            try {
                                for (int i = 0; i < details.length(); i++) {
                                    JSONObject detail = details.getJSONObject(i);
                                    Log.e("detail", detail.toString());
                                    data_to_show +=detail.toString()+"\n";
                                }
                                pb.setProgress(progressStatus);
                                TextView pkgIDView = findViewById(R.id.details);
                                pkgIDView.setText(data_to_show);
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

    /** Called when user taps View Location button */
    public void viewLocation(View view) {
        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String packageID= getIntent().getExtras().getString("packageID");
        String URL = "http://"+"192.168.43.42"+":"+"8088"+"/AppServer/requests?pkgid="+packageID;
        Log.e("url used for view loc", URL);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(URL));
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
        String packageID= getIntent().getExtras().getString("packageID");
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
