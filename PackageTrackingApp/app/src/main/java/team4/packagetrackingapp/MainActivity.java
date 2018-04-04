package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "team4.packagetrackingapp.MESSAGE";
    public static final String HOST_Config = "hostconfigs";
    public static final String HOST_IP = "192.168.43.13";
//    public static final String HOST_IP = "10.0.2.2";

    public static final String HOST_PORT = "8080";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(HOST_Config, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HOST_IP", HOST_IP);
        editor.putString("HOST_PORT", HOST_PORT);
        editor.apply();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        String URL= "://10.0.2.2:8080/app/users";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Error", error.toString());
                    }
                }
        )*//*{
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Connection", "keep-alive");
                return headers;
            }
        }*/;

//        requestQueue.add(objectRequest);
    }

    /** Called when the user taps the Send button */
    /*
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }*/

    /** Called when the user taps the Register button */
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Sign-In button */
    public void login(View view) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
    }
}
