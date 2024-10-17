package com.wilson.dispatchintakescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public TextView forgotPassword;
    private Button login_button;
    private EditText username;
    private EditText password;
    public Spinner spinner;
    SharedPreferences pref;
    Intent intent;
    public String farmid="";
    private static final String[] paths = {"item 1", "item 2", "item 3"};
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.btnLogin);
        username = (EditText) findViewById(R.id.edtUsername);
        password = (EditText) findViewById(R.id.edtPassword);
        spinner = (Spinner) findViewById(R.id.myfarms);

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.farms_array, R.layout.spinner_list);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        spinner.setAdapter(adapter);
        int initialSelectedPosition=spinner.getSelectedItemPosition();
        spinner.setSelection(initialSelectedPosition, false); //clear selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                farmid=item.toString().trim();
                username.setText(farmid);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        forgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Contact IT for Support", Toast.LENGTH_SHORT).show();
//                //setContentView(R.layout.home_page);
//            }
//        });
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        intent = new Intent(this, Home.class);
        if(pref.contains("username") && pref.contains("password")){
            startActivity(intent);


//            ArrayAdapter<String>adapter = new ArrayAdapter<String>(LandingPage.this,
//                    android.R.layout.simple_spinner_item,paths);
//
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner.setAdapter(adapter);

        }

        //process login
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();

                String user = username.getText().toString();
                user = user.trim();


                if(farmid.equals("Thika")){
                    if(user.equals("Thika") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","Thika");
                        editor.putString("password","" +
                                "");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end thika
                if(farmid.equals("HO")){

                    if(user.equals("HO") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","HO");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end HO
                else if(farmid.equals("Chestnut")){
                    if(user.equals("Chestnut") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","Chestnut");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end chestnut
                else if(farmid.equals("Kisima Roses")){
                    if(user.equals("Kisima Roses") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","Kisima");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end kisima
                else if(farmid.equals("Simba Roses")){
                    if(user.equals("Simba Roses") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","Simba Roses");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end simba roses
                else if(farmid.equals("SimbaVeg")){
                    if(user.equals("SimbaVeg") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","SimbaVeg");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }//end simba veg
                else if(farmid.equals("Airport")){
                    if(user.equals("Airport") & pass.equals("aaa2020")){
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("username","Airport");
                        editor.putString("password","aaa2020");
                        editor.putString("farm",farmid);
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else{
//                    Intent i = new Intent(LandingPage.this, HomePage.class);
//                    startActivity(i);
//                    Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(v.getContext(), "Wrong Username or Password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }//end airport
                else{
                    Toast.makeText(MainActivity.this, "No farm selected",Toast.LENGTH_SHORT).show();
                }

//                if(farmid.equals("--Select Farm--") | farmid.equals("")){
//                    Toast.makeText(MainActivity.this, "No farm selected",Toast.LENGTH_SHORT).show();
//                }else{
//
//
//
//            }
            }//end onclick
        });//end btn login


    }

}