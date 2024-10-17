package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    Button dispatch;
    Button packhouse;
    SharedPreferences prf;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView luser = (TextView) findViewById(R.id.txtuser);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(!prf.contains("username") || !prf.contains("password")) {
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }

        String user = prf.getString("username", "");
        String farm = prf.getString("farm", "");
        luser.setText("Logged in as: " + user);

        if(isNetworkAvailable()){
            //startService();
        }

        dispatch = (Button) findViewById(R.id.dispatchBtn);
        packhouse = (Button) findViewById(R.id.packhouseBtn);
        dispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ds = new Intent(Home.this, DispatchHome.class);
                startActivity(ds);
            }
        });
        packhouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pk = new Intent(Home.this, PackHouse.class);
                startActivity(pk);
            }
        });
    }

//    private void startService() {
//        Intent serviceIntent = new Intent(this, ForegroundService.class);
//        serviceIntent.putExtra("inputExtra", "Posting scanned data...");
//        ContextCompat.startForegroundService(this, serviceIntent);
//    }

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

    public void  onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
        System.exit(0);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}