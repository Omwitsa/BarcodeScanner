package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SyncControlPanel extends AppCompatActivity {
    public DBHelper mydb;
    ListView listView;
    SharedPreferences prf;
    public String farm;
    private String m_Text = "";
    public ProgressDialog progressDialog;
    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_sync_control_panel);

        Button customersbtn = (Button) findViewById(R.id.customerBtn);
        Button branchesbtn = (Button) findViewById(R.id.branchBtn);
        Button dispatchbtn = (Button) findViewById(R.id.dispatchBtn);
        Button productbtn = (Button) findViewById(R.id.productBtn);
        Button postdata = (Button) findViewById(R.id.sendSData);
        progressDialog = new ProgressDialog(this);
        mydb = new DBHelper(this);

        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(prf.contains("username") && prf.contains("password")) {
            farm = prf.getString("farm", "");
        }
        else{
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }

        customersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SyncControlPanel.this, Clients.class);
                startActivity(intent);
            }
        });
        branchesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent branch = new Intent(SyncControlPanel.this, CustomerBranches.class);
                startActivity(branch);

            }
        });
        postdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SyncControlPanel.this);
                builder1.setTitle("Select Date To Send");
                final DatePicker picker = new DatePicker(SyncControlPanel.this);
                picker.setCalendarViewShown(false);

// Set up the input
                final EditText input = new EditText(SyncControlPanel.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder1.setView(picker);

// Set up the buttons
                builder1.setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = picker.toString();
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth() + 1;
                        int year = picker.getYear();

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date(year, month, day);
                        String strDate = Integer.toString(year) +"-"+ String.format("%02d", month)+"-" + String.format("%02d", day);
                        Toast.makeText(SyncControlPanel.this, strDate, Toast.LENGTH_SHORT).show();
                        postData(strDate);
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder1.show();
            }

        });
        dispatchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent dispatch = new Intent(this, Dispatch.class);
//                startActivity(dispatch);
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncControlPanel.this);
                builder.setTitle("Select Date From");
                final DatePicker picker = new DatePicker(SyncControlPanel.this);
                picker.setCalendarViewShown(false);

// Set up the input
                final EditText input = new EditText(SyncControlPanel.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(picker);

// Set up the buttons
                builder.setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = picker.toString();
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth() + 1;
                        int year = picker.getYear();



                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date(year, month, day);
                        String strDate = Integer.toString(year) +"-"+ String.format("%02d", month)+"-" + String.format("%02d", day);
                        loadDispatch(strDate);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        productbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(SyncControlPanel.this, Products.class);
                startActivity(p);
            }
        });
    }

    private void loadDispatch(String mdate) {
        if(isNetworkAvailable()){
            showProgressDialogWithTitle("PLEASE WAIT!!","Downloading Packlists");
            //getting the progressbar
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_sync);
//            progressBar.setVisibility(View.VISIBLE);
            final String HEADER_JSON_URL = "https://www.aaagrowers.co.ke/orders/packlist/"+farm+"/dispatch_data.php";

            //creating a string request to send request to the url
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HEADER_JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //hiding the progressbar after completion
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("CUSTOMERS SYNCHED SUCCESSFULLY \n\n");
                            try {
                                //getting the whole json object from the response
                                JSONObject dispatch = new JSONObject(response);

                                //we have the array named tutorial inside the object
                                //so here we are getting that json array
                                JSONArray headerArray = dispatch.getJSONArray("dispatch");

                                //now looping through all the elements of the json array
                                for (int headerCount = 0; headerCount < headerArray.length(); headerCount++) {
                                    //getting the json object of the particular index inside the array
                                    JSONObject headerObject = headerArray.getJSONObject(headerCount);

                                    String dispatchid=headerObject.getString("dispatchheaderid");
                                    String dispatchdate=headerObject.getString("dispatchdate");
                                    String customerid=headerObject.getString("customerid");
                                    String branchid=headerObject.getString("branchid");
                                    String branchname=headerObject.getString("branchname");
                                    String customername=headerObject.getString("customername");
                                    stringBuilder.append(customername + " "+ branchname+"\n");

                                    showProgressDialogWithTitle(customername,branchname);

                                    if(mydb.insertDispatch(Integer.parseInt(dispatchid), dispatchdate, customerid, branchid)){
                                        JSONArray lineArray = headerObject.getJSONArray("line");
                                        for (int lineCount = 0; lineCount < lineArray.length(); lineCount++){
                                            JSONObject lineObject = lineArray.getJSONObject(lineCount);
                                            String lineid=lineObject.getString("lineid");
                                            String headerid=lineObject.getString("headerid");
                                            String productid=lineObject.getString("productid");
                                            String barcode=lineObject.getString("barcode");
                                            String boxqty=lineObject.getString("boxqty");

                                            if(mydb.checkDispatchLine(lineid)==0){
                                                mydb.insertDispatchLine(lineid, headerid, productid, barcode, boxqty);
                                            }else{
                                                mydb.updateDispatchLine(lineid, headerid, productid, barcode, boxqty);
                                            }
                                        }
                                    }
                                }

                                String newString = stringBuilder.toString();
                                progressBar.setVisibility(View.INVISIBLE);
                                hideProgressDialogWithTitle();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SyncControlPanel.this);
                                builder.setTitle("Success")
                                        .setMessage(newString)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // do something when the "OK" button is clicked
//                                                dialog.dismiss();
                                                Intent nn = new Intent(SyncControlPanel.this, SyncControlPanel.class);
                                                startActivity(nn);
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
//
//                            //creating custom adapter object
//                            ClientAdapter adapter = new ClientAdapter(clientList, SyncControlPanel.this);
//
                                //adding the adapter to listview
//                            listView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //displaying the error in toast if occur
                            Toast.makeText(SyncControlPanel.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Set the POST parameters
                    Map<String, String> params = new HashMap<>();
                    params.put("mdate", mdate);
//                   params.put("param2", "value2");
                    return params;
                }
            };

            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //adding the string request to request queue
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(SyncControlPanel.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent ii = new Intent(SyncControlPanel.this, PackHouse.class);
        startActivity(ii);


    }
    // Method to show Progress bar
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        //Setting Title
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
    public void postData(String mdate){
        if(isNetworkAvailable()){
            Cursor result = mydb.getDispatchDataToTransfer(mdate);
            result.moveToFirst();
            int count=0;
            while(!result.isAfterLast()){
                count++;
                String scanqty = result.getString(result.getColumnIndex("scanqty"));
                String lineid = result.getString(result.getColumnIndex("dispatchlineid"));
                //addDataToDatabase(lfarm, scanqty, lineid);
                if(scanqty != null){
//                        Toast.makeText(DispatchList.this, lineid, Toast.LENGTH_SHORT).show();
                    addDataToDatabase(farm, scanqty, lineid);
                }

                result.moveToNext();
            }
        }
        else {
            Toast.makeText(SyncControlPanel.this, "No internet connection", Toast.LENGTH_SHORT ).show();
        }
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
                    Toast.makeText(SyncControlPanel.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(SyncControlPanel.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
