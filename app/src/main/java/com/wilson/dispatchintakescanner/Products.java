package com.wilson.dispatchintakescanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Products extends AppCompatActivity {
    ListView listView;
    DBHelper mydb;
    Button btnsync;
    private static final String JSON_URL = "https://www.aaagrowers.co.ke/orders/get_products.php";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        mydb = new DBHelper(this);
        btnsync = (Button) findViewById(R.id.productSyncBtn);

        listView = (ListView) findViewById(R.id.productListView);
        List<String> products = mydb.getProductList();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(arrayAdapter);
        btnsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadproducts();
            }
        });
    }

    private void loadproducts() {
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
                            JSONArray clientArray = obj.getJSONArray("products");


                            //now looping through all the elements of the json array
                            for (int i = 0; i < clientArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject clientsObject = clientArray.getJSONObject(i);
                                String productid=clientsObject.getString("productid");
                                String code=clientsObject.getString("code");
                                String name=clientsObject.getString("name");
                                String type=clientsObject.getString("type");
                                String category=clientsObject.getString("category");

                                //creating a tutorial object and giving them the values from json object
                                //Clients client = new Clients(clientsObject.getString("clientname"), clientsObject.getInt("clientid"));
//                                Toast.makeText(Products.this, "count=: ", Toast.LENGTH_SHORT).show();
                                if(mydb.checkProduct(productid)==0){
                                    mydb.insertProduct(productid, code, name, type, category);
                                }
                                else{
                                    mydb.updateProduct(productid, code, name, type, category);
                                }

                                //adding the tutorial to tutoriallist
//                                clientList.add(client);
                            }
//
//                            //creating custom adapter object
//                            ClientAdapter adapter = new ClientAdapter(clientList, Products.this);
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
                        Toast.makeText(Products.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
