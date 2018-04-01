package team4.packagetrackingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class PackageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        String pkgDetails_string = getIntent().getStringExtra("pkgDetails");

        JSONObject pkgDetails = null, buyerDetails = null;
        try {
            pkgDetails = new JSONObject(pkgDetails_string);
            buyerDetails = pkgDetails.getJSONObject("Buyer_details");
            Log.e("string received", pkgDetails.toString());
            Log.e("buyer details", buyerDetails.toString());
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        }

        TextView pkgID = findViewById(R.id.idField);
        TextView status = findViewById(R.id.statusField);
        TextView seller = findViewById(R.id.sellerField);
        TextView de = findViewById(R.id.deField);
        TextView dest = findViewById(R.id.destField);
        TextView name = findViewById(R.id.nameField);
        TextView contact = findViewById(R.id.contactField);

        try {
            pkgID.setText(pkgDetails.getString("packageID"));
            status.setText(pkgDetails.getString("status"));
            seller.setText(pkgDetails.getString("seller"));
            dest.setText(pkgDetails.getString("destination"));
            de.setText(pkgDetails.getString("DE"));
            name.setText(buyerDetails.getString("name"));
            contact.setText(buyerDetails.getString("contactNo"));
        } catch(org.json.JSONException e) {
            Log.e("JSONException", "thrown");
        } catch(java.lang.NullPointerException e) {
            Log.e("NullPointerException", "thrown");
        }
    }
}
