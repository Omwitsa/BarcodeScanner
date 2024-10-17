package com.wilson.dispatchintakescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.wilson.dispatchintakescanner.Utilities.DBHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class FileExport extends AppCompatActivity {
    String CODE = "Code";
    String QUANTITY = "Quantity";
    String DATE = "Date";
    public String pdate1="2019-04-20";
    public DBHelper mydb;
    public boolean res=true;
    public  static final String COLUMN_CODE = "code";
    public  static final String COLUMN_QUANTITY = "quantity";
    public  static final String COLUMN_DATECREATED = "datecreated";
    public String Fnamexls;
    public String type;
    public String regno;
    public String sregno;
    public String date;
    public String time;
    //    public String farm;
    SharedPreferences prf;

    // @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export);
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("Type");
        regno = bundle.getString("Truckno");
        date = bundle.getString("Date");
        mydb = new DBHelper(this);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        String farm = prf.getString("farm", "");

        ImageView mImgCheck = (ImageView) findViewById(R.id.fileimageView);

        if(date == null){
            time = mydb.getDateTime();
//            Toast.makeText(FileExport.this,"inside Time " + getDateTime() + "type=" + type, Toast.LENGTH_SHORT).show();
        }
        else{
            time = date;
//            Toast.makeText(FileExport.this,"inside else Time " + getDateTime() + "type=" + type, Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(FileExport.this,"inside Time " + getDateTime() + "type=" + type, Toast.LENGTH_SHORT).show();



        //return res;
        if (isStoragePermissionGranted()) {
            mydb = new DBHelper(this);
            SQLiteDatabase db = mydb.getReadableDatabase();
            Cursor result = mydb.getData(time,type,regno);
//            Cursor result = mydb.getData(time,type);
            result.moveToFirst();
            if(regno.equals("1")){
                sregno = farm +"_truck1";
            }
            if(regno.equals("2")){
                sregno = farm +"_truck2";
            }
            if(regno.equals("3")){
                sregno = farm +"_truck3";
            }
            if(type.equals("1")){
                Fnamexls = ""+sregno+"-"+time+"-received" + ".xls";
            }
            if(type.equals("2")){
                Fnamexls = ""+sregno+"-"+time+"-loaded" + ".xls";
            }
            if(type.equals("3")){
                Fnamexls = ""+sregno+"-"+time+"-returns" + ".xls";
            }
            if(type.equals("4")){
                Fnamexls = "all_file" + ".xls";
            }

//            Toast.makeText(getApplication(),
//                    "list "+ Fnamexls + time + sregno, Toast.LENGTH_LONG).show();


            File sdCard = Environment.getExternalStorageDirectory();

            File directory = new File(sdCard.getAbsolutePath() + "/download");

            if (!directory.exists()) {
                boolean mkdirs = directory.mkdirs();
            }

            File file = new File(directory, Fnamexls);

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            WritableWorkbook workbook;
            try {


                //file path


                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("codeList", 0);
                // column and row
                sheet.addCell(new Label(0, 0, "Product Code"));
                sheet.addCell(new Label(1, 0, "Date"));
                sheet.addCell(new Label(2, 0, "Time"));
                sheet.addCell(new Label(3, 0, "Type"));
                sheet.addCell(new Label(4, 0, "TruckNo"));
                Cursor cursor = mydb.getDataExport(mydb.getDateTime(), type, regno, farm);

                if (cursor.moveToFirst()) {
//                    Toast.makeText(getApplication(),
//                            "inside cursor ", Toast.LENGTH_LONG).show();
                    do {
                        String pcode = cursor.getString(cursor.getColumnIndex("code"));
                        String pqnty = cursor.getString(cursor.getColumnIndex("datecreated"));
                        String pdate = cursor.getString(cursor.getColumnIndex("timecreated"));
                        String type1 = cursor.getString(cursor.getColumnIndex("type"));
                        String regno = cursor.getString(cursor.getColumnIndex("regno"));

                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, pcode));
                        sheet.addCell(new Label(1, i, pqnty));
                        sheet.addCell(new Label(2, i, pdate));
                        sheet.addCell(new Label(3, i, type1));
                        sheet.addCell(new Label(4, i, regno));
                    } while (cursor.moveToNext());
                }
//                else{
//                    Toast.makeText(getApplication(),
//                            "outside cursor ", Toast.LENGTH_LONG).show();
//                }

                //closing cursor
                cursor.close();
                workbook.write();
                workbook.close();
                Toast.makeText(getApplication(),
                        "Data Exported in a Excel Sheet " + ""+time+"", Toast.LENGTH_SHORT).show();
            } catch(Exception e){
                e.printStackTrace();
            }

            Toast.makeText(this, "downloaded", Toast.LENGTH_SHORT).show();
            ((Animatable) mImgCheck.getDrawable()).start();
//
        }
    }
    //menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.tselect:
                Intent ire=new Intent(this, Trucks.class);
                startActivity(ire);
                break;
            case R.id.tmenu1:
                Intent ir=new Intent(this, TruckOneHw.class);
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
//                Intent ii=new Intent(HomePage.this, ReturnedPage.class);
//                startActivity(ii);
                break;



        }

        return false;
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        if(type.equals("3")){
            Intent dispatchHome=new Intent(this, DispatchHome.class);
            this.startActivity(dispatchHome);
        }
        else{
            if(regno.equals("1")){
                this.startActivity(new Intent(this, TruckOneHw.class));
            }
            if(regno.equals("2")){
                this.startActivity(new Intent(this, TruckTwoHw.class));
            }
            if(regno.equals("3")){
                this.startActivity(new Intent(this, TruckThreeHw.class));
            }

        }

    }

}