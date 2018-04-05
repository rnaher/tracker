package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import android.widget.TableRow;

public class viewNotifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);


        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sP.getString("HOST_IP",null);
        String hostPort= sP.getString("HOST_PORT",null);


        String URL = "http://"+hostIP+":"+hostPort+"/app/notification/";

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
                            Toast.makeText(getApplicationContext(), "Notification Retrieved",
                                    Toast.LENGTH_LONG).show();

                            JSONArray notifications = null;
                            try {
                                notifications = response_jsonObj.getJSONArray("notifications");
                                Log.e("notifications jsonarray", notifications.toString());
                            } catch(org.json.JSONException e) {
                                Log.e("json exception", "thrown while getting notifications");
                            } catch(java.lang.NullPointerException e) {
                                Log.e("NullPointerException", "thrown while getting notifications");
                            }

                            TableLayout notTable = findViewById(R.id.notifTable);
                            android.widget.TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
                            p.leftMargin = 20;

                            TableRow initial = new TableRow(viewNotifications.this);
                            TextView intv = new TextView(viewNotifications.this);
                            intv.setLayoutParams(p);
                            //intv.setWidth(50);
                            intv.setText("Notification Data ");
                            initial.addView(intv);

                            TextView intv2 = new TextView(viewNotifications.this);

                            intv2.setText("Package ");
                            intv2.setLayoutParams(p);
                            //intv2.setWidth(50);
                            initial.addView(intv2);

                            TextView intv3 = new TextView(viewNotifications.this);
                            intv3.setText("Seen ");
                            intv3.setLayoutParams(p);
                            initial.addView(intv3);

                            //intv3.setWidth(50);
                            notTable.addView(initial);

                            String data_to_show="";
                            try {
                                for (int i = 0; i < notifications.length(); i++) {
                                    JSONObject detail = notifications.getJSONObject(i);
                                    Log.e("detail", detail.toString());

                                    TableRow newRow = new TableRow(viewNotifications.this);
                                    TextView tv = new TextView(viewNotifications.this);
                                    tv.setText(detail.getString("notification_data")+" ");
                                    tv.setLayoutParams(p);
                                    //  tv.setWidth(50);
                                    Log.e("detail3", detail.toString());
                                    newRow.addView(tv);

                                    TextView tv2 = new TextView(viewNotifications.this);
                                    tv2.setText(detail.getString("packageID")+" ");
                                    tv2.setLayoutParams(p);
                                    //tv2.setWidth(50);
                                    Log.e("detail4", detail.toString());
                                    newRow.addView(tv2);

                                    TextView tv3 = new TextView(viewNotifications.this);
                                    tv3.setText(detail.getString("seen"));
                                    tv3.setLayoutParams(p);
                                    //tv3.setWidth(50);
                                    Log.e("detail5", detail.toString());
                                    newRow.addView(tv3);
                                    notTable.addView(newRow);


                                    data_to_show +=detail.toString()+"\n";
                                }
                                //TextView notificationView = findViewById(R.id.notification_data);
                                //notificationView.setText(data_to_show);
                            }catch (org.json.JSONException e) {
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
//
//    TableLayout t1;
//
//    TableLayout tl = (TableLayout) findViewById(R.id.main_table);
}
