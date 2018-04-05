package team4.packagetrackingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DeliveryExecutive extends AppCompatActivity {


    private Button btn_start, btn_stop;
    private TextView set_coordinates_tv;
    private BroadcastReceiver broadcastReceiver;
    public String Latitude="",Longitude="";

    @Override
    protected void onResume() {
        Log.e("on resume", "called");
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("Received","entered");
                    String cordinates = (String)intent.getExtras().get("coordinates");
                    Longitude=cordinates.split("\\s+")[0];
                    Latitude=cordinates.split("\\s+")[1];

//                    make call similar to http://localhost:8080/AppServer/requests?pkgid=1&&lat=17.44798&&lng=78.34830
                    set_coordinates_tv.append("Long and Lat " +Longitude + " "+Latitude+"\n");
                    DeliveryExecutive.this.connect_server();
                    Log.e("Here Received","entered");
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    private void connect_server() {
        Log.e("connect_server", "started");
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.e("username (sharedprefs)", username);

        Log.e("function", "entered");
//        final TextView mTextView = (TextView) findViewById(R.id.coordinates_tv);
// ...

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String ip="192.168.43.42";
        String port="8088";
        String pkgid="1";
        String lat=Latitude;//"17.44798";
        String lng=Longitude;//"78.34830";
        String url ="http://"+ip+":"+port+"/AppServer/requests?deid="+username+"&&lat="+lat+"&&lng="+lng;
        Log.e("URL created", url);

        Log.i(lat,"lat info");

//        String url="http://google.com";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response:", response);
                        // Display the first 500 characters of the response string.
//                        mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mTextView.setText("That didn't work!");
                Log.e("error", error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        set_coordinates_tv.append("Long and Lat " +Longitude + " "+Latitude);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e("created","yes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_executive);
        btn_start = (Button) findViewById(R.id.start_delivery_btn);
        btn_stop = (Button) findViewById(R.id.stop_delivery_btn);
        set_coordinates_tv = (TextView) findViewById(R.id.coordinates_tv);

        if(!runtime_permissions())
            enable_buttons();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);

            }
        });

    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

}
