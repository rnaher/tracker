package team4.packagetrackingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SellerDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
    }
    /** Called when the user taps the Add Package button */
    public void addPackage(View view) {
        Intent intent = new Intent(this, AddPackageActivity.class);
        startActivity(intent);
    }
    /** Called when the user taps the Add Package button */
    public void trackPackage(View view) {
        Intent intent = new Intent(this, TrackPackage.class);
        startActivity(intent);
    }

    /** Called when the user taps the View All Packages button */
    public void viewAllPackages(View view) {
        Intent intent = new Intent(this, ViewPackagesActivity.class);
        startActivity(intent);
    }
    public void viewNotifications(View view) {
        Intent intent = new Intent(this, viewNotifications.class);
        startActivity(intent);
    }
}
