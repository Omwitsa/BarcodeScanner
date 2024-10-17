package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DispatchScanning extends AppCompatActivity {
    public TextView mytitle;
    DBHelper mydb;
    ListView listView;
    SharedPreferences prf;
    Intent intent;
    public Intent myintent;
    public String type = "2";
    public String farm;
    public  TextView truckCount;
    public String mdate="";
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
    public  String hid;
    TextView product;
    public ArrayAdapter arrayAdapter;
    public String mt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_scanning);

        mytitle = (TextView) findViewById(R.id.textHeader);
        mydb = new DBHelper(this);
//        listView = (ListView) findViewById(R.id.scanListView);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        myintent = getIntent();
        hid = myintent.getStringExtra("hid");
        if (myintent.hasExtra("mdate")) {
            mdate = myintent.getStringExtra("mdate");
        }
//        mdate = myintent.getStringExtra("mdate");
//        Toast.makeText(DispatchScanning.this, mdate, Toast.LENGTH_SHORT).show();

        product = (TextView) findViewById(R.id.textProduct);
        ArrayList<String> linelist = mydb.getAllDispatchLine(hid);
        mt = mydb.getDispatchTitle(hid);
        mytitle.setText(mt);
        Button viewpk=(Button) findViewById(R.id.viewBtn);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, linelist);
//        listView.setAdapter(arrayAdapter);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);

        if(prf.contains("username") && prf.contains("password")) {

        }
        else{
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }
        String user = prf.getString("username", "");
        farm = prf.getString("farm", "");
//        if(user.equals("Kisima") | user.equals("Simba Roses")){
//            setContentView(R.layout.trucks_activity_roses);
//        }
        // intent = new Intent(this, LandingPage.class);
        mydb = new DBHelper(this);


        viewpk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.view_packlist);
                ListView mlist=(ListView) findViewById(R.id.packlistListView);
                TextView ttt=(TextView) findViewById(R.id.mytextHeader);

                mlist.setAdapter(arrayAdapter);
                ttt.setText(mt);
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
//        Toast.makeText(DispatchScanning.this, "Inside set text " +text,
//                Toast.LENGTH_SHORT).show();
        if (text != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    textView.setText(text);
                    mydb = new DBHelper(DispatchScanning.this);
                    String finalcode=text.trim();
                    int codel=finalcode.length();
                    String len = String.valueOf(codel);

//                    int rc = mydb.getCount(myfinal, datecreated, "2");
//                    Toast.makeText(DispatchScanning.this, len,
//                            Toast.LENGTH_SHORT).show();
//                    textView.setText(finalcode);
                    if(codel>12){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DispatchScanning.this);
                        builder1.setMessage("Whoops!! Invalid Barcode Number "+finalcode);
                        builder1.setCancelable(true);
                        Vibrator vibrator;
                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(700);
//                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
                        MediaPlayer mediaPlayer = MediaPlayer.create(DispatchScanning.this,R.raw.complete);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();
                        mediaPlayer.release();

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

                        if(mydb.checkDispatchProduct(hid, finalcode) != 0){
                            String farm = prf.getString("farm", "");
                            String scan = mydb.getScannedQty(hid, finalcode);
                            String boxq = mydb.getBoxQty(hid, finalcode);
                            int scanqty=0;
                            int boxqty=0;
                            if(scan != null){
                                scanqty=Integer.parseInt(scan);
                            }
                            if(boxq != null){
                                boxqty=Integer.parseInt(boxq);
                            }
                            int newqty=scanqty+1;
                            if(newqty > boxqty){
                                Toast.makeText(DispatchScanning.this, "Full",
                                        Toast.LENGTH_SHORT).show();
                                product.setText(mydb.getLineDetails(hid, finalcode));
//                            product.setText("mhealth mix");
                                Vibrator vibrator;
                                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(700);
                                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
//                            Toast.makeText(this, boxqty,
//                                    Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(newqty==boxqty){
                                    mydb.UpdateDispatchQty(newqty, finalcode, hid);
                                    //addDataToDatabase(farm, newqty,)
                                    product.setText(mydb.getLineDetails(hid, finalcode));
                                    Toast.makeText(DispatchScanning.this, "now full",
                                            Toast.LENGTH_SHORT).show();
                                    Vibrator vibrator;
                                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(700);
                                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
                                }
                                else{
//                                mydb.getHeaderLineId(hid, finalcode);
                                    mydb.UpdateDispatchQty(newqty, finalcode, hid);
                                    product.setText(mydb.getLineDetails(hid, finalcode));
                                    Toast.makeText(DispatchScanning.this, "Cont "+ mydb.getHeaderLineId(hid, finalcode),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }



                        }
                        else{
                            Toast.makeText(DispatchScanning.this, "Count: "+mydb.checkDispatchProduct(hid, finalcode),
                                    Toast.LENGTH_SHORT).show();
                            //product does not exist
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(DispatchScanning.this);
                            builder2.setMessage("Whoops!! Product Not Found for code: "+finalcode);
                            builder2.setCancelable(true);
                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);

                            builder2.setPositiveButton(
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

                            AlertDialog alert11 = builder2.create();
                            alert11.show();


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
        mymenu.inflate(R.menu.trucktwo, menu);
        return true;
    }


    private void addDataToDatabase(final String farm, final String scanqty, final String lineid) {

        // url to post our data
        String url = "https://www.aaagrowers.co.ke/api/packlist-api-v1.php";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "RESPONSE IS " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // on below line we are displaying a success toast message.
                    Toast.makeText(DispatchScanning.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(DispatchScanning.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // below line we are creating a map for storing
                // our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our
                // key and value pair to our parameters.
                params.put("farm", farm);
                params.put("scanqty", scanqty);
                params.put("lineid", lineid);


                // at last we are returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {


            case R.id.tselect:
                Intent ire=new Intent(this, Trucks.class);
                startActivity(ire);
                break;
            case R.id.tmenu1:
                Intent ir=new Intent(this, TruckTwoHw.class);
                startActivity(ir);
                break;
            case R.id.tmenu3:
                Intent ii=new Intent(this, TruckThreeHw.class);
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
        if (myintent.hasExtra("mdate")) {
            mdate = myintent.getStringExtra("mdate");
//            Toast.makeText(this, mdate, Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("date", mdate);
            Intent n = new Intent(DispatchScanning.this, DispatchList.class);
            n.putExtras(bundle);
            startActivity(n);
        } else {
//            Toast.makeText(this, "has no date", Toast.LENGTH_SHORT).show();
            this.startActivity(new Intent(this, DispatchList.class));
        }


    }
}