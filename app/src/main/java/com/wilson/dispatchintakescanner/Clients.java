package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wilson.dispatchintakescanner.Utilities.Client;
import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Clients extends AppCompatActivity {
    public DBHelper mydb;
    private static final String JSON_URL = "https://www.aaagrowers.co.ke/orders/get_clients.php";
    ListView listView;
//    List<Clients> clientList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        mydb = new DBHelper(this);

        listView = (ListView) findViewById(R.id.customerListView);
        ArrayList<String> clientlist = mydb.getAllClients();
        // ct = (TextView) findViewById(R.id.textView);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, clientlist);
        ListView obj = (ListView) findViewById(R.id.customerListView);
        obj.setAdapter(arrayAdapter);
        Button syncBtn =  (Button) findViewById(R.id.customerSyncBtn);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    loadTutorialList();
                }else{
                    Toast.makeText(Clients.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //this method will fetch and parse the data
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                String customerInfo = listView.getItemAtPosition(index).toString();
                String customerId = customerInfo.substring(0, customerInfo .indexOf(" "));
                Toast.makeText(Clients.this,customerId, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void loadTutorialList() {
        //getting the progressbar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        progressBar.setVisibility(View.INVISIBLE);


                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named tutorial inside the object
                            //so here we are getting that json array
                            JSONArray clientArray = obj.getJSONArray("clients");


                            //now looping through all the elements of the json array
                            for (int i = 0; i < clientArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject clientsObject = clientArray.getJSONObject(i);
                                String id=clientsObject.getString("clientid");
                                String name=clientsObject.getString("clientname");

                                //creating a tutorial object and giving them the values from json object
                                Client client = new Client(clientsObject.getString("clientname"), clientsObject.getInt("clientid"));
//                                Toast.makeText(Clients.this, name + id, Toast.LENGTH_SHORT).show();
//                                if(mydb.checkProduct(String pid)==0){
//
//                                }
                                mydb.insertCustomer(id, name);
                                //mydb.insertClient(id, name);
                                //adding the tutorial to tutoriallist
//                                clientList.add(client);
                            }
//
//                            //creating custom adapter object
//                            ClientAdapter adapter = new ClientAdapter(clientList, Clients.this);
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
                        Toast.makeText(Clients.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}