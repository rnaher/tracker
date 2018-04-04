package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class TrackPackage extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final int packageId=0;

    private TextView contentTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_package);
        contentTxt = (TextView)findViewById(R.id.trckpkgIDField);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        String status = (String) parent.getItemAtPosition(pos);
        Log.e("selected option", status);

        TextView pkgStatusView = findViewById(R.id.pkgStatusView);
        pkgStatusView.setText(status);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    /** Called when the user taps the scan barcode button */
    public void scan_Barcode(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();

        EditText pkg_ID = findViewById(R.id.trckpkgIDField);

//        TextView pkgIDView = findViewById(R.id.pkgIDView);
//        pkgIDView.setText(pkg_ID.getText().toString());
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            contentTxt.setText(scanContent);

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /** Called when the user taps the Track button */
    public void Track(View view) {
        if( TextUtils.isEmpty(contentTxt.getText())){

            Toast.makeText(getApplicationContext(), "Package Id is empty", Toast.LENGTH_SHORT).show();

        }
        else {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                    Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", null);

            SharedPreferences sP= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

            String hostIP= sP.getString("HOST_IP",null);
            String hostPort= sP.getString("HOST_PORT",null);
            final String packageID= contentTxt.getText().toString();
            String URL = "http://"+hostIP+":"+hostPort+"/app/track/"+packageID+"/"+username;
            System.out.println(URL);

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
                                Intent intent = new Intent(TrackPackage.this, TrackResultActivity.class);
                                intent.putExtra("packageID", packageID);
                                startActivity(intent);
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
}
