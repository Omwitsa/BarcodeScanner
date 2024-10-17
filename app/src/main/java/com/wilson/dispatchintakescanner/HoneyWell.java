package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import com.wilson.dispatchintakescanner.Utilities.DBHelper;

public class HoneyWell extends AppCompatActivity {
    private static final String TAG = "IntentApiSample";
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    /**
     * Honeywell DataCollection Intent API
     * Claim scanner
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Release scanner claim
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the scanner to claim. If scanner is not available or if extra is not used,
     * DataCollection will choose an available scanner.
     * Values : String
     * "dcs.scanner.imager" : Uses the internal scanner
     * "dcs.scanner.ring" : Uses the external ring scanner
     */
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the profile to use. If profile is not available or if extra is not used,
     * the scanner will use factory default properties (not "DEFAULT" profile properties).
     * Values : String
     */
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Overrides the profile properties (non-persistent) until the next scanner claim.
     * Values : Bundle
     */
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private TextView textView;
    private BroadcastReceiver barcodeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BARCODE_DATA.equals(intent.getAction())) {
/*
These extras are available:
"version" (int) = Data Intent Api version
"aimId" (String) = The AIM Identifier
}
"charset" (String) = The charset used to convert "dataBytes" to "data" string
"codeId" (String) = The Honeywell Symbology Identifier
"data" (String) = The barcode data as a string
"dataBytes" (byte[]) = The barcode data as a byte array
"timestamp" (String) = The barcode timestamp
*/
                int version = intent.getIntExtra("version", 0);
                if (version >= 1) {
                    String aimId = intent.getStringExtra("aimId");
                    String charset = intent.getStringExtra("charset");
                    String codeId = intent.getStringExtra("codeId");
                    String data = intent.getStringExtra("data");
                    byte[] dataBytes = intent.getByteArrayExtra("dataBytes");
                    String dataBytesStr = bytesToHexString(dataBytes);
                    String timestamp = intent.getStringExtra("timestamp");
                    String text = String.format(
                            "Data:%s\n" +
                                    "Charset:%s\n" +
                                    "Bytes:%s\n" +
                                    "AimId:%s\n" +
                                    "CodeId:%s\n" +
                                    "Timestamp:%s\n",
                            data, charset, dataBytesStr, aimId, codeId, timestamp);

                    setText(data);
                }
            }
        }
    };
    public String pallet_no;
    public DBHelper mydb;
    public String datecreated;
    public TextView palletCount;
    public AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honey_well);
        mydb = new DBHelper(HoneyWell.this);
        textView = (TextView) findViewById(R.id.textViewh);
        TextView pallet_view = (TextView) findViewById(R.id.palletNumber);
        palletCount = (TextView) findViewById(R.id.palletCount);
        Bundle extras = getIntent().getExtras();
        pallet_no = extras.getString("pallet_no");
        pallet_view.setText(pallet_no);
        datecreated = mydb.getDateTime();
        int cc=mydb.getCountPalletInitial(datecreated, pallet_no);
        palletCount.setText(Integer.toString(cc));
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();
    }
    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER)
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
                .putExtra(EXTRA_PROFILE, "MyProfile1")
                .putExtra(EXTRA_PROPERTIES, properties)
        );
    }
    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }
    private void setText(final String text) {
        if (textView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(text);
                    mydb = new DBHelper(HoneyWell.this);
                    datecreated = mydb.getDateTime();



                    String finalcode=text.trim();
                    int codel=finalcode.length();
                    String len = String.valueOf(codel);

                    int rc = mydb.getCountPallet(finalcode, datecreated, pallet_no);
                    Toast.makeText(HoneyWell.this, len,
                            Toast.LENGTH_SHORT).show();
//                    textView.setText(finalcode);
                    if(codel>12){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(HoneyWell.this);
                        builder1.setMessage("Whoops!! Invalid Barcode Number");
                        builder1.setCancelable(true);
                        Vibrator vibrator;
                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(700);

                        builder1.setPositiveButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

//                       builder1.setNegativeButton(
//                               "No",
//                               new DialogInterface.OnClickListener() {
//                                   public void onClick(DialogInterface dialog, int id) {
//                                       dialog.cancel();
//                                   }
//                               });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }
                    else{
                        if(rc==0){
                            int c2 = Integer.parseInt((String) palletCount.getText())+1;
                            palletCount.setText(String.valueOf(c2));
                            mydb.insertPallet(finalcode, pallet_no);
                            Toast.makeText(HoneyWell.this, "New",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HoneyWell.this, "Exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
    private String bytesToHexString(byte[] arr) {
        String s = "[]";
        if (arr != null) {
            s = "[";
            for (int i = 0; i < arr.length; i++) {
                s += "0x" + Integer.toHexString(arr[i]) + ", ";
            }
            s = s.substring(0, s.length() - 2) + "]";
        }
        return s;
    }
}