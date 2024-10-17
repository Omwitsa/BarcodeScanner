package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class PostData extends AppCompatActivity {
    public DBHelper mydb;
    public TextView status;
    SharedPreferences prf;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_data);
        Bundle extras = getIntent().getExtras();
        String pallet_no = extras.getString("Type");
        if(pallet_no==""){
            Intent intent = new Intent(this, DispatchHome.class);
            startActivity(intent);
        }
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(prf.contains("username") && prf.contains("password")) {

        }
        else{
            Intent l= new Intent(PostData.this, MainActivity.class);
            startActivity(l);
        }
        String user = prf.getString("username", "");
        String lfarm = prf.getString("farm", "");
        mydb = new DBHelper(this);
        status=(TextView) findViewById(R.id.status);
        if(mydb.getCountToTransfer(lfarm)>0){
            Cursor result = mydb.getDataToTransfer(lfarm);
            result.moveToFirst();
            int count=0;
            while(!result.isAfterLast()){
                count++;
                String codeno = result.getString(result.getColumnIndex("code"));
                String datecreated = result.getString(result.getColumnIndex("datecreated"));
                String type = result.getString(result.getColumnIndex("type"));
                String regno = result.getString(result.getColumnIndex("regno"));
                String farm = result.getString(result.getColumnIndex("farm"));
                addDataToDatabase(codeno, datecreated, type, regno, farm);
                mydb.UpdateTransfered(result.getInt(0));

//              Toast.makeText(PostData.this, String.valueOf(result.getInt(0)), Toast.LENGTH_SHORT).show();
                result.moveToNext();
            }
            status.setText(String.valueOf(count) + "posted");
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(count+" Record(s) posted successfully");
            builder1.setCancelable(true);
//          Vibrator vibrator;
//          vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//          vibrator.vibrate(700);

            builder1.setPositiveButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

//                          dialog.cancel();
                            Intent intent = new Intent(PostData.this,DispatchHome.class);
                            startActivity(intent);
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

    }

    private void addDataToDatabase(final String codeno, final String datecreated, final String type, final String regno, final String farm) {

        // url to post our data
        String url = "https://www.aaagrowers.co.ke/api/scan-api-v1.php";

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
                    Toast.makeText(PostData.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(PostData.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
                params.put("codeno", codeno);
                params.put("datecreated", datecreated);
                params.put("type", type);
                params.put("regno", regno);
                params.put("farm", farm);

                // at last we are returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(this,DispatchHome.class));

    }
}