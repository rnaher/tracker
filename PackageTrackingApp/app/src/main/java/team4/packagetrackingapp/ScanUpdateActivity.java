package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class ScanUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_update);

        Spinner status_options = findViewById(R.id.statusSpinner);

        status_options.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_options.setAdapter(adapter);
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

    /** Called when the user taps the Submit button */
    public void manualSubmit(View view) {
        EditText pkg_ID = findViewById(R.id.pkgIDField);

        TextView pkgIDView = findViewById(R.id.pkgIDView);
        pkgIDView.setText(pkg_ID.getText().toString());
    }

    /** Called when the user taps the Update Package Status button */
    public void updateStatus(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        TextView pkgIDView = findViewById(R.id.pkgIDView);
        String pkgID = pkgIDView.getText().toString();

        TextView pkgStatusView = findViewById(R.id.pkgStatusView);
        String status = pkgStatusView.getText().toString();

        String URL= "http://10.0.2.2:8080/app/update/";
        URL = URL + pkgID;
        Log.e("new URL", URL);

        JSONObject updateRequest = new JSONObject();

        try {
            updateRequest.put("username", username);
            updateRequest.put("status", status);
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown while trying to add to JSONObject");
        }

        try {
            Log.e("update request", updateRequest.toString());
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown while trying to print JSONObject");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                updateRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject response_jsonObj;
                        String code = null, msg = null;
                        Log.e("Rest Response", response.toString());

                        try {
                            response_jsonObj = response.getJSONObject("Response");
                            code = response_jsonObj.getString("error_code");
                            msg = response_jsonObj.getString("message");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_LONG).show();

                            Intent back_to_dash = new Intent(ScanUpdateActivity.this,
                                                             DeliveryDashboard.class);
                            startActivity(back_to_dash);
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
