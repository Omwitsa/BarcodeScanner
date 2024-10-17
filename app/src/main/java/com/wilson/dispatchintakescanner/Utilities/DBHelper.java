package com.wilson.dispatchintakescanner.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "stock.db";
    public  static final String COLUMN_CODE = "code";
    public  static final String COLUMN_QUANTITY = "quantity";
    public  static final String COLUMN_DATECREATED = "datecreated";
    public  static final String COLUMN_TIMECREATED = "timecreated";
    public  static final String COLUMN_TYPE = "type";
    public  static final String COLUMN_REGNO = "regno";
    public  static final String COLUMN_CLIENTID = "ClientId";
    public  static final String COLUMN_CLIENTNAME = "ClientName";
    public  static final String COLUMN_NAME = "name";
    public  static final String COLUMN_CUSTOMERID = "customerid";
    public  static final String COLUMN_CUSTOMERNAME = "customername";
    public  static final String COLUMN_BRANCHID = "branchid";
    public  static final String COLUMN_BRANCHNAME = "branchname";
    //    pallet table
    public  static final String COLUMN_PALLETNO = "PalletNumber";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "create table clients " +
                        "(ClientId integer primary key, ClientName TEXT)"
        );
        db.execSQL(
                "create table products " +
                        "(id integer primary key, code TEXT, datecreated TEXT, timecreated TEXT, type TEXT, regno TEXT, farm TEXT, status integer default 0)"
        );
        db.execSQL(
                "create table palletheader " +
                        "(id integer primary key, code TEXT, datecreated TEXT, timecreated TEXT, pallet_number TEXT)"
        );
        db.execSQL(
                "create table customer " +
                        "(customerid integer primary key, customername TEXT)"
        );
        db.execSQL(
                "create table branch " +
                        "(branchid integer primary key, branchname TEXT, customerid TEXT)"
        );
        db.execSQL(
                "create table dispatchheader " +
                        "(dispatchid integer primary key, dispatchdate TEXT, customerid TEXT, branchid TEXT)"
        );
        db.execSQL(
                "create table dispatchheaderline " +
                        "(dispatchlineid integer primary key, dispatchid integer, productid integer, barcode TEXT, boxqty integer, scanqty integer default null, scandate TEXT default current_timestamp)"
        );
        db.execSQL(
                "create table cproducts " +
                        "(productid integer primary key, code TEXT, productname TEXT, type TEXT, category TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS clients");
        db.execSQL("DROP TABLE IF EXISTS palletheader");
        db.execSQL("DROP TABLE IF EXISTS customer");
        onCreate(db);
    }

    //get time
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "E dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getDate(String mydate) {

        // SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
        return mydate;
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public boolean tableExists(String tableName)
    {
        SQLiteDatabase db = getReadableDatabase();
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"'", null);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public boolean insertProduct(String code, int quantity, String type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("quantity", quantity);
        contentValues.put("datecreated", getDateTime());
        contentValues.put("type", type);
        db.insert("products", null, contentValues);
        return true;
    }

    public boolean insertProduct(String code, String type, String regno, String farm){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("datecreated", getDateTime());
        contentValues.put("timecreated", getCurrentTime());
        contentValues.put("type", type);
        contentValues.put("regno", regno);
        contentValues.put("farm", farm);
        long result=db.insert("products", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }

    public boolean insertPallet(String code, String palletno){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("datecreated", getDateTime());
        contentValues.put("timecreated", getCurrentTime());
        contentValues.put("pallet_number", palletno);

        long result=db.insert("palletheader", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }

    public int getCountPallet(String code, String date, String palletno){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM palletheader WHERE code = '"+code+"' AND datecreated = '"+date+"' AND pallet_number = '"+palletno+"'", null);
        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;
    }

    public int getCountPalletInitial(String date, String palletno){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RoCount = db.rawQuery("SELECT COUNT(*) FROM palletheader WHERE datecreated = '"+date+"' AND pallet_number = '"+palletno+"'", null);
        RoCount.moveToFirst();
        int result = RoCount.getInt(0);
        RoCount.close();
        return result;
    }

    public boolean insertClient(String clientid, String clientname){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ClientId", clientid);
        contentValues.put("ClientName", clientname);
        db.insert("clients", null, contentValues);
        return true;
    }

    public ArrayList<String> getAllProducts(String type){
        ArrayList<String> product_code_list = new ArrayList<String>();
        ArrayList<String> product_quantity = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from products where type ='"+type+"'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            product_code_list .add(res.getString(res.getColumnIndex(COLUMN_CODE)) + "  "
                    + res.getString(res.getColumnIndex(COLUMN_DATECREATED)) + " : "
                    + res.getString(res.getColumnIndex(COLUMN_TIMECREATED)) + "    "
                    + res.getString(res.getColumnIndex(COLUMN_REGNO)));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return product_code_list;
    }
    public boolean insertDispatch(int dispatchid, String dispatchdate, String customerid, String branchid){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("dispatchid", dispatchid);
        contentValues.put("dispatchdate", dispatchdate);
        contentValues.put("customerid", customerid);
        contentValues.put("branchid", branchid);
        db.insert("dispatchheader", null, contentValues);
        return true;

    }
    public int CheckLine(){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RoCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE status=0", null);
        RoCount.moveToFirst();
        int result = RoCount.getInt(0);
        RoCount.close();
        return result;
    }
    public boolean insertDispatchLine(String dispatchlineid, String dispatchid, String productid, String barcode, String boxqty){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("dispatchlineid", dispatchlineid);
        contentValues.put("dispatchid", dispatchid);
        contentValues.put("productid", productid);
        contentValues.put("barcode", barcode);
        contentValues.put("boxqty", boxqty);
        db.insert("dispatchheaderline", null, contentValues);
        return true;

    }
    public boolean updateDispatchLine(String dispatchlineid, String dispatchid, String productid, String barcode, String boxqty){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("dispatchid", dispatchid);
        contentValues.put("productid", productid);
        contentValues.put("barcode", barcode);
        contentValues.put("boxqty", boxqty);
        db.update("dispatchheaderline", contentValues, "dispatchlineid="+dispatchlineid, null);
        return true;

    }

    public ArrayList<String> getAllDispatch(){
        ArrayList<String> dispatch_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select dh.dispatchid as dispatchid, dh.dispatchdate as ddate, c.customername as name, b.branchname as branch from dispatchheader dh inner join " +
                "customer c on c.customerid=dh.customerid left join branch b on b.branchid=dh.branchid", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            dispatch_list .add(res.getString(res.getColumnIndex("dispatchid")) + " : \nDate: "
                    + res.getString(res.getColumnIndex("ddate")) + "\nClient: "
                    + res.getString(res.getColumnIndex("name")) + "\nBranch: "
                    + res.getString(res.getColumnIndex("branch")));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return dispatch_list;
    }

    public ArrayList<String> getAllDispatch(String mydate){
        ArrayList<String> dispatch_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select dh.dispatchid as dispatchid, dh.dispatchdate as ddate, c.customername as name, b.branchname as branch from dispatchheader dh inner join " +
                "customer c on c.customerid=dh.customerid left join branch b on b.branchid=dh.branchid where dh.dispatchdate='"+mydate+"'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            dispatch_list .add(res.getString(res.getColumnIndex("dispatchid")) + " : \nDate: "
                    + res.getString(res.getColumnIndex("ddate")) + "\nClient: "
                    + res.getString(res.getColumnIndex("name")) + "\nBranch: "
                    + res.getString(res.getColumnIndex("branch")));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return dispatch_list;
    }
    //    public LineList[] getDispatchLine(String hid){
//        ArrayList<String> dispatchline_list = new ArrayList<String>();
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor res = db.rawQuery("select p.productname as name, dl.barcode as barcode, dl.boxqty as qty, dl.scanqty as scanned from dispatchheaderline dl inner join cproducts p on dl.productid=p.productid", null);
//        res.moveToFirst();
//        LineList[] myListData = new LineList[]{};
//        while(!res.isAfterLast()){
//            new LineList(res.getString(res.getColumnIndex("name")),res.getString(res.getColumnIndex("qty")),res.getString(res.getColumnIndex("scanned")));
////            dispatchline_list .add(res.getString(res.getColumnIndex("name")) + "  "
////                    + res.getString(res.getColumnIndex("barcode")) + " : "
////                    + res.getString(res.getColumnIndex("qty")) + "    "
////                    + res.getString(res.getColumnIndex("scanned")));
//            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
//            res.moveToNext();
//        }
//        return myListData;
//    }
    public ArrayList<String> getAllDispatchLine(String hid){
        ArrayList<String> dispatchline_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select p.productname as name, dl.barcode as barcode, dl.boxqty as qty, dl.scanqty as scanned from dispatchheaderline dl " +
                "inner join cproducts p on dl.productid=p.productid where dl.dispatchid='"+hid+"' order by p.type asc", null);
        res.moveToFirst();

        while(!res.isAfterLast()){

            dispatchline_list .add(res.getString(res.getColumnIndex("name")) + "  "
                    + res.getString(res.getColumnIndex("barcode")) + " \nBoxQty:"
                    + res.getString(res.getColumnIndex("qty")) + "\nScanQty:"
                    + res.getString(res.getColumnIndex("scanned")));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return dispatchline_list;
    }
    public Cursor getAllDispatchLineCusor(String hid){
        ArrayList<String> dispatchline_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select p.productname as name, dl.barcode as barcode, dl.boxqty as qty, dl.scanqty as scanned from dispatchheaderline dl " +
                "inner join cproducts p on dl.productid=p.productid where dl.dispatchid='"+hid+"'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){

        }
        return res;
    }
    public String getHeaderLineId(String hid, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select dispatchlineid from dispatchheaderline where dispatchid='"+hid+"' and barcode like '%"+barcode+"%'", null);
        res.moveToFirst();
        String ress="";
        while(!res.isAfterLast()){


            ress =  res.getString(res.getColumnIndex("dispatchlineid"));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }

        return ress;
    }
    public void UpdateDispatchQty(int qty, String barcode, String hid){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String mbarcode=getHeaderLineId(hid, barcode);
        contentValues.put("scanqty", qty);
        long recid = db.update("dispatchheaderline", contentValues, "dispatchlineid="+mbarcode,  null);
//        if (recid != -1) {
//            return null;
//        } else {
//           return false;
//        }
    }
    public boolean truncateTable(String tablename){
        SQLiteDatabase db = getWritableDatabase();
        return true;
    }

    public ArrayList<String> getProductList(){
        ArrayList<String> product_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from cproducts", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            product_list .add("Type/C: "+res.getString(res.getColumnIndex("type")) + "/ "
                    + res.getString(res.getColumnIndex("category")) + " \nDetails: "
                    + res.getString(res.getColumnIndex("productid")) + "    \n"
                    + res.getString(res.getColumnIndex("code")) + "\nName:   "
                    + res.getString(res.getColumnIndex("productname")));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return product_list;
    }
    public  boolean insertProduct(String productid, String code, String productname, String type, String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productid",productid);
        contentValues.put("code",code);
        contentValues.put("productname",productname);
        contentValues.put("type",type);
        contentValues.put("category",category);
        db.insert("cproducts",null, contentValues);
        return true;
    }
    public String getDispatchTitle(String hid){
        SQLiteDatabase db = getReadableDatabase();
        StringBuffer name = new StringBuffer();
        String resname = "";
        Cursor res = db.rawQuery("select dh.dispatchid as dispatchid, dh.dispatchdate as ddate, c.customername as name, b.branchname as branch from dispatchheader dh inner join " +
                "customer c on c.customerid=dh.customerid left join branch b on b.branchid=dh.branchid where dh.dispatchid='"+hid+"'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            String date = getDate(res.getString(res.getColumnIndex("ddate")));
            resname = "Dispatch Date:  "
                    + date + "\nCustomer: "
                    + res.getString(res.getColumnIndex("name")) + "\nBranch:    "
                    + res.getString(res.getColumnIndex("branch"));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }

        return resname;


        //return truckCount;
    }
    public String getLineDetails(String hid, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        String str = barcode;
        Cursor res = db.rawQuery("select p.productname as pname, dh.boxqty as boxqty, dh.scanqty as scanqty from dispatchheaderline dh inner join cproducts p on p.productid=dh.productid " +
                "where dh.dispatchid='"+hid+"' and dh.barcode like '%"+barcode+"%'", null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            str = res.getString(res.getColumnIndex("pname")) + " \nBoxQty:" + res.getString(res.getColumnIndex("boxqty"))+" \nScanQty:"+res.getString(res.getColumnIndex("scanqty"));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }

        return  str;
    }
    public int getLineDetailsCount(String hid, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        Cursor res = db.rawQuery("select COUNT(*) from dispatchheaderline dh inner join cproducts p on p.productid=dh.productid " +
                "where dh.dispatchid='"+hid+"' and dh.barcode like '%"+barcode+"%'", null);
        res.moveToFirst();
        int ress = res.getInt(0);
        res.close();
        return ress;
    }
    public String getScannedQty(String hid, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        StringBuffer name = new StringBuffer();
        String resname = "";
        Cursor res = db.rawQuery("select * from dispatchheaderline dh where dh.dispatchid='"+hid+"' and dh.barcode like '%"+barcode+"%'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            resname = res.getString(res.getColumnIndex("scanqty"));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }

        return resname;


        //return truckCount;
    }
    public String getBoxQty(String hid, String barcode){
        SQLiteDatabase db = getReadableDatabase();
        StringBuffer name = new StringBuffer();
        String resname = "";
        Cursor res = db.rawQuery("select * from dispatchheaderline dh where dh.dispatchid='"+hid+"' and dh.barcode like '%"+barcode+"%'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            resname = res.getString(res.getColumnIndex("boxqty"));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }

        return resname;


        //return truckCount;
    }
    public boolean insertCustomerBranch(String id, String name, String customerid){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("branchid", id);
        contentValues.put("branchname", name);
        contentValues.put("customerid", customerid);
        db.insert("branch", null, contentValues);
        return true;

    }

    public ArrayList<String> getAllBranches(){
        ArrayList<String> branchlist = new ArrayList<String>();
        ArrayList<String> product_quantity = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from branch b inner join customer c on c.customerid=b .customerid order by c.customername asc", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            String customer = res.getString(res.getColumnIndex(COLUMN_CUSTOMERNAME));
            //String name = getCustomerName(customerid);
            branchlist .add(customer + " :  " +
                    res.getString(res.getColumnIndex(COLUMN_BRANCHNAME)) );
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return branchlist;
    }
    public boolean insertCustomer(String id, String name){
        SQLiteDatabase db = getWritableDatabase();
//        contentValues.put("code", code.substring(0, code.length() - 1));
        ContentValues contentValues = new ContentValues();
        contentValues.put("customerid", id);
        contentValues.put("customername", name);
        db.insert("customer", null, contentValues);
        return true;

    }
    public int getCustomerName(String customerid){
        StringBuffer name = new StringBuffer();
        String resname = "";
        int myid=0;
        if (customerid == "") {
            myid=0;
        }
        else{
            myid=1;
        }
        //int myid=Integer.parseInt(customerid);

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCustomername = db.rawQuery("SELECT * FROM customer WHERE customerid = '"+myid+"'", null);

//        RCustomername.moveToFirst();
        if (RCustomername.moveToFirst()) {
            // resname = RCustomername.getString(RCustomername.getColumnIndex(COLUMN_CUSTOMERNAME));
            resname="yes";
        }
        else{
            resname="no";
        }

        return myid;


        //return truckCount;
    }
    public String getCustomer(int customerid){
        StringBuffer name = new StringBuffer();
        String resname = "";
        int myid=0;
//        if (customerid == 0) {
//            resname="no";
//        }
//        else{
//            resname=" yes";
//        }
        // myid=Integer.parseInt(customerid);

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCustomername = db.rawQuery("SELECT * FROM customer WHERE customerid = '"+customerid+"'", null);

        RCustomername.moveToFirst();
        if (RCustomername.moveToFirst()) {
            resname = RCustomername.getString(RCustomername.getColumnIndex(COLUMN_CUSTOMERNAME));
            //resname="yes";
        }
        else{
            resname="no";
        }

        return resname;


        //return truckCount;
    }
    public ArrayList<String> getAllClients(){
        ArrayList<String> clientlist = new ArrayList<String>();
        ArrayList<String> product_quantity = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from customer order by customerid asc", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            clientlist .add(res.getString(res.getColumnIndex(COLUMN_CUSTOMERID)) + "  "
                    + res.getString(res.getColumnIndex(COLUMN_CUSTOMERNAME)));
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return clientlist;
    }
    public ArrayList<String> getAllBranchesWithcustomer(){
        ArrayList<String> branchlist = new ArrayList<String>();
        ArrayList<String> product_quantity = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from branch order by customerid asc", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            String customerid = res.getString(res.getColumnIndex(COLUMN_CUSTOMERID));
            //String name = String.valueOf(getCustomerName(customerid));
            String name = getCustomer(257);
            branchlist .add(res.getString(res.getColumnIndex(COLUMN_BRANCHID)) + "  " +
                    res.getString(res.getColumnIndex(COLUMN_BRANCHNAME)) + "  "
                    + " C= " + name);
            //product_quantity.add(res.getString(res.getColumnIndex(COLUMN_QUANTITY)));
            res.moveToNext();
        }
        return branchlist;
    }
    public ArrayList<String> getSqliteTables(){
        ArrayList<String> client_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor clresult = db.rawQuery("SELECT name FROM sqlite_master\n" +
                "WHERE type='table'\n" +
                "ORDER BY name", null);
        clresult.moveToFirst();

        while (!clresult.isAfterLast()){
            client_list .add(clresult.getString(clresult.getColumnIndex(COLUMN_NAME)));
            clresult.moveToNext();
        }
        return client_list;
    }
    public ArrayList<String> getClientList(){
        ArrayList<String> client_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select * from clients", null);
        result.moveToFirst();

        while(!result.isAfterLast()){
            client_list .add(result.getString(result.getColumnIndex(COLUMN_CLIENTNAME)));
            result.moveToNext();
        }
        return client_list;
    }
//    public Integer getCustomerCount(int customerid){
//
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE datecreated = '"+date+"' AND type = '"+type+"' AND regno = '"+truckno+"'", null);
//
//        RCount.moveToFirst();
//        int res = RCount.getInt(0);
//        RCount.close();
//        return res;
//
//
//        //return truckCount;
//    }

    public Integer getTruckCount(String type, String date, String truckno){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE datecreated = '"+date+"' AND type = '"+type+"' AND regno = '"+truckno+"'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }
    //truck count by farm
    public Integer getTruckCount(String type, String date, String truckno, String farm){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE datecreated = '"+date+"' AND type = '"+type+"' AND regno = '"+truckno+"' AND farm = '"+farm+"'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }
    public Integer checkDispatchProduct(String hid, String barcode){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM dispatchheaderline WHERE dispatchid='"+hid+"' and barcode like '%"+barcode+"%'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }
    public Integer getTruckCount(String type, String date){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE datecreated = '"+date+"' AND type = '"+type+"'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }

    public Integer getFarmTruckTotal(String type, String date, String farm){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE datecreated = '"+date+"' AND type = '"+type+"' AND farm = '"+farm+"'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }
    public Integer checkProduct(String pid){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM cproducts WHERE productid = '"+pid+"' ", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }

    public Integer checkDispatchLine(String pid){

        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM dispatchheaderline WHERE dispatchlineid = '"+pid+"' ", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;


        //return truckCount;
    }

    public ArrayList<String> getSpecificDateProducts(String type, String date){
        ArrayList<String> date_product_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"'", null);
        result.moveToFirst();

        while(!result.isAfterLast()){
            date_product_list .add(result.getString(result.getColumnIndex(COLUMN_CODE)) + " X "
                    + result.getString(result.getColumnIndex(COLUMN_DATECREATED)) + " : "
                    + result.getString(result.getColumnIndex(COLUMN_TIMECREATED)) + "    "
                    + result.getString(result.getColumnIndex(COLUMN_REGNO)));
            result.moveToNext();
        }
        return date_product_list;
    }

    public ArrayList<String> getSpecificDateProductsTruckno(String type, String date, String truckno){
        ArrayList<String> date_product_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"' AND regno = '"+truckno+"'", null);
        result.moveToFirst();

        while(!result.isAfterLast()){
            date_product_list .add(result.getString(result.getColumnIndex(COLUMN_CODE)) + " X "
                    + result.getString(result.getColumnIndex(COLUMN_DATECREATED)) + " : "
                    + result.getString(result.getColumnIndex(COLUMN_TIMECREATED)) + "    "
                    + result.getString(result.getColumnIndex(COLUMN_REGNO)) + "    F:"
                    + result.getString(result.getColumnIndex("farm")));
            result.moveToNext();
        }
        return date_product_list;
    }
    public ArrayList<String> getSpecificDateProductsTrucknoFarm(String type, String date, String truckno, String farm){
        ArrayList<String> date_product_list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"' AND regno = '"+truckno+"' AND farm = '"+farm+"'", null);
        result.moveToFirst();

        while(!result.isAfterLast()){
            date_product_list .add(result.getString(result.getColumnIndex(COLUMN_CODE)) + " X "
                    + result.getString(result.getColumnIndex(COLUMN_DATECREATED)) + " : "
                    + result.getString(result.getColumnIndex(COLUMN_TIMECREATED)) + "    "
                    + result.getString(result.getColumnIndex(COLUMN_REGNO)) + "    F:"
                    + result.getString(result.getColumnIndex("farm")));
            result.moveToNext();
        }
        return date_product_list;
    }


    public Cursor getData(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from products where datecreated ='"+date+"'", null);
        return res;
    }
    public Cursor getData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from products order by datecreated asc", null);
        return res;
    }
    //checkif data to transfer exists
    public int getCountToTransfer(){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RoCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE status=0", null);
        RoCount.moveToFirst();
        int result = RoCount.getInt(0);
        RoCount.close();
        return result;
    }

    //checkif data to transfer exists
    public int getCountToTransfer(String farm){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RoCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE farm ='"+farm+"' and status=0", null);
        RoCount.moveToFirst();
        int result = RoCount.getInt(0);
        RoCount.close();
        return result;
    }
    //get untransfered data
    public Cursor getDataToTransfer(String farm){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from products where farm ='"+farm+"' and status=0 order by datecreated asc", null);
        return res;
    }
    public Cursor getDispatchDataToTransfer(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from dispatchheaderline", null);
        return res;
    }
    public Cursor getDispatchDataToTransfer(String mdate){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select dl.scanqty, dl.dispatchlineid from dispatchheader dp inner join  dispatchheaderline dl on dp.dispatchid=dl.dispatchid where dispatchdate = '"+mdate+"'", null);
        return res;
    }

    //update on transfer
    public void UpdateTransfered(int colid){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", 1);
        long recid = db.update("products", contentValues, "id="+colid, null);
//        if (recid != -1) {
//            return null;
//        } else {
//           return false;
//        }
    }
    public void updateProduct(String productid, String code, String productname, String type, String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code",code);
        contentValues.put("productname",productname);
        contentValues.put("type",type);
        contentValues.put("category",category);
        long recid = db.update("cproducts", contentValues, "productid="+productid, null);
//        if (recid != -1) {
//            return null;
//        } else {
//           return false;
//        }
    }
    public Cursor getData(String date, String type){
        SQLiteDatabase db = getReadableDatabase();
        if(type.equals("4")){
            Cursor res = db.rawQuery("select * from products order by type", null);
            return res;
        }
        else{
            Cursor res = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"'", null);
            return res;
        }

    }
    public Cursor getDataExport(String date, String type, String regno, String farm){
        SQLiteDatabase db = getReadableDatabase();
//        if(type.equals("4")){
//            Cursor res = db.rawQuery("select * from products order by type", null);
//            return res;
//        }
//        else{
        Cursor res = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"' AND regno = '"+regno+"' AND farm = '"+farm+"'", null);
        return res;
//        }

    }
    public Cursor getData(String date, String type, String regno){
        SQLiteDatabase db = getReadableDatabase();
        if(type.equals("4")){
            Cursor res = db.rawQuery("select * from products order by type", null);
            return res;
        }
        else{
            Cursor res = db.rawQuery("select * from products where datecreated ='"+date+"' AND type = '"+type+"' AND regno = '"+regno+"'", null);
            return res;
        }

    }

    public int getCount(String code, String date, String type){
        SQLiteDatabase db = getReadableDatabase();
//        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code.substring(0, code.length() - 1)+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM products WHERE code = '"+code+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;
    }
    public int getClientCount(String clientid, String clientname){
        SQLiteDatabase db = getReadableDatabase();
        Cursor RCount = db.rawQuery("SELECT COUNT(*) FROM clients WHERE ClientId = '"+clientid+"' AND ClientName = '"+clientname+"'", null);

        RCount.moveToFirst();
        int res = RCount.getInt(0);
        RCount.close();
        return res;
    }
    public int getColumnQuantity(String code,String date, String type){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select quantity from products where code = '"+code+"' AND datecreated = '"+date+"' AND type = '"+type+"' limit 1", null);
        res.moveToFirst();
        int qnt = res.getInt(res.getColumnIndex(COLUMN_QUANTITY));
        return qnt;


    }
    public boolean udateQuantity(int qnty, String code, String date, String type){
        SQLiteDatabase db = getWritableDatabase();
        Cursor updateqnt = db.rawQuery("update products set quantity = '"+qnty+"' where code = '"+code+"' AND datecreated = '"+date+"' AND type = '"+type+"'", null);
        updateqnt.moveToFirst();
        updateqnt.close();
        return true;

    }


}
