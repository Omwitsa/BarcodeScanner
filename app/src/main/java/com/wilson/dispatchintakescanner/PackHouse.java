package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PackHouse extends AppCompatActivity {
    Button sync;
    Button btndate;
    Button btnall;
    Button btncustomer;
    private String m_Text = "";
    SharedPreferences prf;
    public String farm;
    public String user;
    //    BackgroundTask bk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_house);

        sync = (Button) findViewById(R.id.syncBtn);
        btndate = (Button) findViewById(R.id.dateBtn);
        btnall = (Button) findViewById(R.id.allBtn);
        btncustomer = (Button) findViewById(R.id.customerBtn);
        TextView luser = (TextView) findViewById(R.id.txtuser);

        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        if(prf.contains("username") && prf.contains("password")) {
            farm = prf.getString("farm", "");
            user = prf.getString("username", "");
        }
        else{
            Intent l= new Intent(this, MainActivity.class);
            startActivity(l);
        }
        luser.setText("Logged in as: " + user);

//        bk = new BackgroundTask(farm, this);
//        bk.execute();
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l= new Intent(PackHouse.this, SyncControlPanel.class);
                startActivity(l);
            }
        });

        btncustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PackHouse.this);
                builder.setTitle("Select Date");
                final DatePicker picker = new DatePicker(PackHouse.this);
                picker.setCalendarViewShown(false);

// Set up the input
                final EditText input = new EditText(PackHouse.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(picker);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = picker.toString();
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth() + 1;
                        int year = picker.getYear();



                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = new Date(year, month, day);
                        String strDate = Integer.toString(year) +"-"+ String.format("%02d", month)+"-" + String.format("%02d", day);
                        //Toast.makeText(this, strDate, Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("date", strDate);
                        Intent i = new Intent(PackHouse.this, DispatchList.class);
                        i.putExtras(bundle);
                        startActivity(i);
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

        btnall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii=new Intent(PackHouse.this, DispatchList.class);
                startActivity(ii);
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

    public void onBackPressed() {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        super.onBackPressed();
        this.startActivity(new Intent(PackHouse.this, Home.class));

    }
}