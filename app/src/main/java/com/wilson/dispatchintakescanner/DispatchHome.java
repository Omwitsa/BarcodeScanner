package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wilson.dispatchintakescanner.Utilities.DBHelper;

public class DispatchHome extends AppCompatActivity {
    final Context context = this;
    public DBHelper mydb;
    public String type = "4";
    public String t;
    SharedPreferences prf;
    Intent intent;
    public String m_text = "";
    public Button senddata;
    public TextView luser;
    public Button dispatchlist;
    public String farm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_home);
        mydb = new DBHelper(this);

        Button receive = (Button) findViewById(R.id.receiveBtn);
        Button load = (Button) findViewById(R.id.loadBtn);
        Button returning = (Button) findViewById(R.id.returnBtn);
        Button palletbtn = (Button) findViewById(R.id.palletBtn);
//        Button sync = (Button) findViewById(R.id.syncBtn);
        luser = (TextView) findViewById(R.id.txtuser);
        senddata = (Button) findViewById(R.id.senData);


//        Button download = (Button) findViewById(R.id.downloadall);
//        Button btnToday = (Button) findViewById(R.id.btnviewtoday);
//        Button clientlist =(Button) findViewById(R.id.fetchclients);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(!prf.contains("username") || !prf.contains("password")) {
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }

        String user = prf.getString("username", "");
        farm = prf.getString("farm", "");
        String xxx = Integer.toString(mydb.getCountToTransfer(farm));
        senddata.setText("POST ("+xxx+" Records)");
        luser.setText("Logged in as: " + user);
//        sync.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent l= new Intent(this, SyncControlPanel.class);
//                startActivity(l);
//            }
//        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l= new Intent(DispatchHome.this, Trucks.class);
                startActivity(l);
            }
        });
        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mydb.getCountToTransfer()==0){
                    Toast.makeText(DispatchHome.this, "No records to post", Toast.LENGTH_LONG).show();
                }
                else{
                    if(isNetworkAvailable()){
                        Bundle bundle = new Bundle();
                        bundle.putString("Type", "type");
                        Intent l= new Intent(DispatchHome.this, PostData.class);
                        l.putExtras(bundle);
                        startActivity(l);
                    }
                    else{
                        Toast.makeText(DispatchHome.this, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

//        dispatchlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent r= new Intent(HomePage.this, DispatchList.class);
//                startActivity(r);
//            }
//        });


        returning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent r= new Intent(HomePage.this, SelectTruck.class);
//                startActivity(r);
//                Toast.makeText(DispatchHome.this, "Comming soon", Toast.LENGTH_SHORT).show();
            }
        });
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent re= new Intent(HomePage.this, ReceivingPage.class);
//                startActivity(re);
                Toast.makeText(DispatchHome.this, "Comming soon", Toast.LENGTH_SHORT).show();
            }
        });
        palletbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DispatchHome.this);
                builder.setTitle("Enter Pallet Number");

// Set up the input
                final EditText input = new EditText(DispatchHome.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_text = input.getText().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("pallet_no", m_text);
                        Intent pallet = new Intent(DispatchHome.this, HoneyWell.class);
                        pallet.putExtras(bundle);
                        startActivity(pallet);
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
    }

    //menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                moveTaskToBack(true);
                System.exit(0);
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = prf.edit();
                editor.clear();
                editor.commit();
                // startActivity(intent);
                Intent ii=new Intent(this, MainActivity.class);
                startActivity(ii);
                break;



        }

        return false;
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onBackPressed()
    {
        Intent ii=new Intent(this, Home.class);
        startActivity(ii);
    }
}