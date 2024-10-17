package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilson.dispatchintakescanner.Utilities.BackgroundTask;
import com.wilson.dispatchintakescanner.Utilities.DBHelper;
import com.wilson.dispatchintakescanner.Utilities.PostScannedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DispatchList extends AppCompatActivity {
    DBHelper mydb;
    ArrayList<String> mylist;
    Button post_btn;
    BackgroundTask bk;
    SharedPreferences prf;
    public String mdate="";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_list);

        mydb = new DBHelper(this);

        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(!prf.contains("username") || !prf.contains("password")) {
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }

        final String lfarm = prf.getString("farm", "");


        final ListView listView = (ListView) findViewById(R.id.dispatchList);
        post_btn = (Button) findViewById(R.id.postBtn);
//        ArrayList <String> mylist = mydb.getAllDispatch();
        Intent intent = getIntent();

        if (intent.hasExtra("date")) {
            mdate = intent.getStringExtra("date");
//            Toast.makeText(this, "has date", Toast.LENGTH_SHORT).show();
            mylist = mydb.getAllDispatch(mdate);
        } else {
//            Toast.makeText(this, "has no date", Toast.LENGTH_SHORT).show();
            mylist = mydb.getAllDispatch();
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mylist);
        listView.setAdapter(arrayAdapter);

        //String mdate = intent.getStringExtra("date");
//        Toast.makeText(this, mdate, Toast.LENGTH_SHORT).show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = listView.getItemAtPosition(i).toString();
                String hid = info.substring(0, info .indexOf(" "));

                Bundle bundle = new Bundle();
                bundle.putString("hid", hid);
                if(TextUtils.isEmpty(mdate)){
                    // String is empty or null
                }else {
                    // string has value
                    bundle.putString("mdate", mdate);
                }
                Intent n = new Intent(DispatchList.this, DispatchScanning.class);
                n.putExtras(bundle);
                startActivity(n);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                return false;
            }
        });
        post_btn.setOnClickListener(new View.OnClickListener() {
            //getting the progressbar
            @Override
            public void onClick(View view) {
                // Create a new thread and define the heavy task to run

                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarD);
                progressBar.setVisibility(View.VISIBLE);
                // Perform your heavy task here
//
////                    Cursor result1 = mydb.getDispatchDataToTransfer();
//                        Cursor result = mydb.getDispatchDataToTransfer(mdate);
//                        result.moveToFirst();
//                        int count = 0;
//                if (isNetworkAvailable()) {
//                        while (!result.isAfterLast()) {
//                            count++;
//
//                                String scanqty = result.getString(result.getColumnIndex("scanqty"));
//                                String lineid = result.getString(result.getColumnIndex("dispatchlineid"));
//                                //addDataToDatabase(lfarm, scanqty, lineid);
//                                if (scanqty != null) {
////                        Toast.makeText(DispatchList.this, lineid, Toast.LENGTH_SHORT).show();
//                                    addDataToDatabase(lfarm, scanqty, lineid);
//                                }
//
//
//                                result.moveToNext();
////                            Toast.makeText(DispatchList.this, "Syncing Header "+count, Toast.LENGTH_SHORT ).show();
//
//                        }
//                        } else {
//                    Toast.makeText(DispatchList.this, "No internet connection", Toast.LENGTH_SHORT).show();
//                }
                PostScannedData postScannedData = new PostScannedData(DispatchList.this);
                //Toast.makeText(DispatchList.this, "Lfarm="+lfarm, Toast.LENGTH_SHORT).show();
                postScannedData.execute(lfarm, mdate);
                progressBar.setVisibility(View.INVISIBLE);



//                Toast.makeText(DispatchList.this, "Syncing Header "+count, Toast.LENGTH_SHORT ).show();

            }
        });
    }

    public void addDataToDatabase(final String farm, final String scanqty, final String lineid) {
        int MY_TIMEOUT_MS = 60000;
        // url to post our data
        String url = "https://www.aaagrowers.co.ke/api/packlist-api-v1.php";
//        Toast.makeText(DispatchList.this, "Syncing Farm "+farm+ "scanned "+scanqty+" lineid:"+lineid, Toast.LENGTH_SHORT ).show();

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
                    Toast.makeText(DispatchList.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(DispatchList.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//    // AsyncTask class
//    private class MyTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            // Perform background task here
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            // Perform post-execution task here
//        }
//    }

    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(this, PackHouse.class));
    }
}
