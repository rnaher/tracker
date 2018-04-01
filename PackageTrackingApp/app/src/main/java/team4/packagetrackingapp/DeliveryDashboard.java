package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DeliveryDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

/*        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES,
                                                                   Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.e("username (sharedprefs)", username);*/
    }

    /** Called when the user taps the Scan & Update button */
    public void scanUpdate(View view) {
        Intent intent = new Intent(this, ScanUpdateActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the View Packages button */
    public void viewPackages(View view) {
        Intent intent = new Intent(this, ViewPackagesActivity.class);
        startActivity(intent);
    }

    public void shareLocation(View view) {
        Intent intent = new Intent(this, DeliveryExecutive.class);
        startActivity(intent);
    }
}
