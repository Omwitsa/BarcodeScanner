package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wilson.dispatchintakescanner.Utilities.DBHelper;

public class Trucks extends AppCompatActivity {
    DBHelper mydb;
    Button truck1, truck2, truck3;
    TextView txtTrc1, txtTrc2, txtTrc3, txtTotals, txtToday;
    SharedPreferences prf;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trucks);

        intent = new Intent(this, MainActivity.class);
        mydb = new DBHelper(this);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(!prf.contains("username") || !prf.contains("password")) {
            startActivity(intent);
        }

        String farm = prf.getString("farm", "");
        truck1 = (Button)findViewById(R.id.truck1);
        truck2 = (Button)findViewById(R.id.truck2);
        truck3 = (Button)findViewById(R.id.truck3);
        txtTrc1 = (TextView)findViewById(R.id.txtTruck1);
        txtTrc2 = (TextView)findViewById(R.id.txtTruck2);
        txtTrc3 = (TextView)findViewById(R.id.txtTruck3);
        txtToday = (TextView)findViewById(R.id.txtDate);
        txtTotals = (TextView)findViewById(R.id.txtTotal);
        String sdate = mydb.getDateTime();
        String ctr1 = Integer.toString(mydb.getTruckCount("2", sdate, "1", farm));
        String ctr2 = Integer.toString(mydb.getTruckCount("2", sdate, "2", farm));
        String ctr3 = Integer.toString(mydb.getTruckCount("2", sdate, "3", farm));
        String ctt = Integer.toString(mydb.getFarmTruckTotal("2", sdate, farm));
        txtTrc1.setText(ctr1);
        txtTrc2.setText(ctr2);
        txtTrc3.setText(ctr3);
        txtTotals.append(ctt);
        txtToday.append(mydb.getToday());
        truck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplication(),
//                        "Working ", Toast.LENGTH_SHORT).show();
                Intent ex= new Intent(Trucks.this, TruckOneHw.class);
                //ex.putExtras(bundle);
                startActivity(ex);
            }
        });
        //truck 2
        //txtTrc1.setText("COUNT BOX TODAY);
        truck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ex= new Intent(Trucks.this, TruckTwoHw.class);
                //ex.putExtras(bundle);
                startActivity(ex);
            }
        });

        //truck 3
        truck3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ex= new Intent(Trucks.this, TruckThreeHw.class);
                //ex.putExtras(bundle);
                startActivity(ex);
            }
        });
    }
    //menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.selecttruck, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {


            case R.id.tmenu1:
                Intent ire=new Intent(this, TruckOneHw.class);
                startActivity(ire);
                break;
            case R.id.tmenu2:
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

    public void onBackPressed() {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        super.onBackPressed();
        this.startActivity(new Intent(this, DispatchHome.class));

    }
}