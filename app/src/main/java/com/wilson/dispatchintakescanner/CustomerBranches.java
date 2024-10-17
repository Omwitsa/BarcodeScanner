package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class CustomerBranches extends AppCompatActivity {

    private static final String JSON_URL = "https://www.aaagrowers.co.ke/orders/get_branches.php?";
    ListView listView;
    List<Client> clientList;
    public DBHelper mydb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        mydb = new DBHelper(this);
        listView = (ListView) findViewById(R.id.customerListView);
        ArrayList<String> branchlist = mydb.getAllBranches();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, branchlist);
        listView.setAdapter(arrayAdapter);

        Button loadbranch = (Button) findViewById(R.id.customerSyncBtn);
        loadbranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadbranchList();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = listView.getItemAtPosition(i).toString();
                Toast.makeText(CustomerBranches.this, info , Toast.LENGTH_LONG).show();
                return false;
            }
        });
        //this method will fetch and parse the data

    }

    private void loadbranchList() {
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
                            JSONArray clientArray = obj.getJSONArray("branches");


                            //now looping through all the elements of the json array
                            for (int i = 0; i < clientArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject clientsObject = clientArray.getJSONObject(i);
                                String branchid=clientsObject.getString("branchid");
                                String branchname=clientsObject.getString("branchname");
                                String customerid=clientsObject.getString("customerid");

                                //creating a tutorial object and giving them the values from json object
                                //Clients client = new Clients(clientsObject.getString("clientname"), clientsObject.getInt("clientid"));
//                                Toast.makeText(CustomerBranches.this, clientsObject.getString("branchname"), Toast.LENGTH_SHORT).show();
                                mydb.insertCustomerBranch(branchid, branchname, customerid);
                                //adding the tutorial to tutoriallist
//                                clientList.add(client);
                            }
//
//                            //creating custom adapter object
//                            ClientAdapter adapter = new ClientAdapter(clientList, CustomerBranches.this);
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
                        Toast.makeText(CustomerBranches.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
