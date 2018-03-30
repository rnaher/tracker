package team4.packagetrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Created by Subasuseela on 30-03-2018.
 */

public class ScanBarcode extends AppCompatActivity implements View.OnClickListener {
        private Button scanBtn;
        private TextView formatTxt, contentTxt, contentTxt1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            scanBtn = (Button)findViewById(R.id.barcodeButton);
            contentTxt = (TextView)findViewById(R.id.pkgIDField);
            scanBtn.setOnClickListener(this);
        }
        public void onClick(View v){
//respond to clicks
            if(v.getId()==R.id.barcodeButton){
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
//scan
            }
        }
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
//we have a result
                String scanContent = scanningResult.getContents();
                contentTxt.setText("CONTENT: " + scanContent);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


