package team4.packagetrackingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;

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

        EditText pkg_ID = findViewById(R.id.newpkgIDField);

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
    /** Called when the user taps the Track button */
    public void Track(View view) {
        Intent intent = new Intent(this, TrackResultActivity.class);
        intent.putExtra("packageID",contentTxt.getText().toString());
        startActivity(intent);
    }

}
