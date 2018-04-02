package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;

import org.json.JSONObject;

import java.util.Objects;

public class AddPackageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);
        contentTxt = (EditText)findViewById(R.id.packageField);
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
    //Called when the user taps the Submit button
    /*
    public void Submit(View view) {
        EditText pkg_ID = findViewById(R.id.newpkgIDField);

        TextView pkgIDView = findViewById(R.id.pkgIDView);
        pkgIDView.setText(pkg_ID.getText().toString());
    }
    */

    /** Called when user taps submit button */
    public void submitPackage(View view) {
        // need to ensure constraints on email ID, pw etc.
        Log.e("checking "," function started");

        EditText package_name = findViewById(R.id.packageField);
        EditText destination = findViewById(R.id.destinationField);
        EditText buyer = findViewById(R.id.buyerField);
        EditText contact = findViewById(R.id.contactField);
        Log.e("checking "," function started");

//        Package newPackage = new Package(Integer.parseInt(package_name.getText().toString()), destination.getText().toString());
        Log.e("checking "," function started");

        //        newUser.show();

        Gson gson = new Gson();
//        String newPackage_jsonString = gson.toJson(newPackage);

        JSONObject newPackage_json = new JSONObject();
        JSONObject buyer_json = new JSONObject();
        /*
        try {
            newPackage_json = new JSONObject(newPackage_jsonString);
        }catch(org.json.JSONException e) {
            Log.e("JSON object creation", "failed");
        }
        */
        try {
            newPackage_json.put("packageID", Double.parseDouble(package_name.getText().toString()));
            buyer_json.put("name", buyer.getText().toString());
            buyer_json.put("contactNo", contact.getText().toString());
            newPackage_json.put("Buyer_details", buyer_json);
            newPackage_json.put("destination", destination.getText().toString());
            Log.e("request",newPackage_json.toString());
        }catch(org.json.JSONException e) {
            Log.e("JSON Object creation", "failed");
        }






        SharedPreferences sharedPreferences= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sharedPreferences.getString("HOST_IP",null);
        String hostPort= sharedPreferences.getString("HOST_PORT",null);

        sharedPreferences= getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("username", null);
       // Log.e("username (sharedprefs)", username);

        String URL = "http://"+hostIP+":"+hostPort+"/app/package/"+username;
        System.out.println(URL) ;
//        String URL= "http://10.3.0.147:8080/app/registration";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                newPackage_json,
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
                        } else {
                            Toast.makeText(getApplicationContext(), "failed",
                                    Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(AddPackageActivity.this, SellerDashboard.class);
                        startActivity(intent);
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


    /** Called when the user taps the scan barcode button */

    public void scan_Barcode(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();

        EditText pkg_ID = findViewById(R.id.packageField);

        TextView pkgIDView = findViewById(R.id.pkgIDView);
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


}
