package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import java.util.ArrayList;

public class TruckThreeHw extends AppCompatActivity {
    TextView txtStatus = null;
    TextView txtTemplate = null;
    LinearLayout layoutRegeions = null;
    TextView txtCount;
    public String labelType = "";
    public String dataString = "";
    public DBHelper mydb;
    Button Etoday;
    SharedPreferences prf;
    Intent intent;
    public String datecreated;
    public  TextView truckCount;
    Button extoday;
    public String type = "2";
    public String farm;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trucks_activity);
        txtCount = findViewById(R.id.txtCount);
        extoday = (Button) findViewById(R.id.exporttoday);
        truckCount = (TextView) findViewById(R.id.truck_count);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(!prf.contains("username") || !prf.contains("password")) {
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }

        String user = prf.getString("username", "");
        farm = prf.getString("farm", "");
        intent = new Intent(this, MainActivity.class);
        mydb = new DBHelper(this);
        String sdate = mydb.getDateTime();
        String ctr1 = Integer.toString(mydb.getTruckCount("2", sdate, "3", farm));
        truckCount.setText(ctr1);
//        txtTemplate  = findViewById(R.id.txtTemplate);
//        txtTemplate.setText("Template \"" + TEMPLATE_NAME + "\", must be available in the device to run this application. Please use DataWedgeMGR via StageNow to push the templates to device");
        layoutRegeions = findViewById(R.id.layoutRegeions);

        Button today = (Button) findViewById(R.id.viewToday);
        Etoday = findViewById(R.id.btn_download);
        //view todays records
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb = new DBHelper(TruckThreeHw.this);
                setContentView(R.layout.show_product);
                Button btnDownload = (Button) findViewById(R.id.btn_download);
                ArrayList<String> product_code_list = mydb.getSpecificDateProductsTrucknoFarm("2", mydb.getDateTime(), "2", farm);
                // ct = (TextView) findViewById(R.id.textView);

                ArrayAdapter arrayAdapter = new ArrayAdapter(TruckThreeHw.this, android.R.layout.simple_list_item_1, product_code_list);

                ListView obj = (ListView) findViewById(R.id.listView1);
                obj.setAdapter(arrayAdapter);
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Toast.makeText(MainActivity.this, "downloaded", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("Type", type);
                        bundle.putString("Truckno", "3");

                        Intent ex= new Intent(TruckThreeHw.this, FileExport.class);
                        ex.putExtras(bundle);
                        startActivity(ex);
                    }
                });


            }
        });


        extoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(TruckThreeHw.this, "downloaded", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("Type", type);
                bundle.putString("Truckno", "3");
                bundle.putString("Date", null);

                Intent ex = new Intent(TruckThreeHw.this, FileExport.class);
                ex.putExtras(bundle);
                startActivity(ex);
            }
        });

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
//        Toast.makeText(TruckOneHw.this, "Inside set text " +text,
//                Toast.LENGTH_SHORT).show();
        if (text != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    textView.setText(text);
                    mydb = new DBHelper(TruckThreeHw.this);
                    datecreated = mydb.getDateTime();



                    String finalcode=text.trim();
                    int codel=finalcode.length();
                    String len = String.valueOf(codel);

//                    int rc = mydb.getCount(myfinal, datecreated, "2");
//                    Toast.makeText(TruckOneHw.this, len,
//                            Toast.LENGTH_SHORT).show();
//                    textView.setText(finalcode);
                    if(codel>12){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TruckThreeHw.this);
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
                        String farm = prf.getString("farm", "");
                        datecreated = mydb.getDateTime();
                        int rc = mydb.getCount(finalcode, datecreated, "2");
                        if(rc==0){
                            mydb.insertProduct(finalcode, "2", "3", farm);
                            Toast.makeText(TruckThreeHw.this, "New",
                                    Toast.LENGTH_SHORT).show();
                            String ctr2 = Integer.toString(mydb.getTruckCount("2", datecreated, "3", farm));
                            truckCount.setText(ctr2);
                        }
                        else{
                            Toast.makeText(TruckThreeHw.this, "Exists",
                                    Toast.LENGTH_SHORT).show();
                            Vibrator vibrator;
                            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(700);
                        }
                    }
                }
            });
        }
        else{

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
    //menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.truckthree, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {


            case R.id.tselect:
                Intent ire=new Intent(this, Trucks.class);
                startActivity(ire);
                break;
            case R.id.tmenu2:
                Intent ir=new Intent(this, TruckTwoHw.class);
                startActivity(ir);
                break;
            case R.id.tmenu1:
                Intent ii=new Intent(this, TruckOneHw.class);
                startActivity(ii);
                break;
            case R.id.exit:
                finish();
                moveTaskToBack(true);
                System.exit(0);
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = prf.edit();
                editor.clear();
                editor.commit();
                startActivity(intent);
//                Intent ii=new Intent(HomePage.this, ReturnedPage.class);
//                startActivity(ii);
                break;



        }

        return false;
    }
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(this, Trucks.class));

    }
}