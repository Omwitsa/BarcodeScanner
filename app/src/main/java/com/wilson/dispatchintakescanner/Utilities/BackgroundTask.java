package com.wilson.dispatchintakescanner.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class BackgroundTask extends AsyncTask<Void,Void, Void> {
    private Context mContext;
    DBHelper mydb;
    Context cxt;
    HttpUtils hp;
    SharedPreferences prf;
    private String farm;

    public BackgroundTask(String farm, Context mContext){
        this.farm=farm;
        this.mContext=mContext;
        mydb = new DBHelper(mContext);
    }
    protected Void doInBackground(Void... voids){
        //task to do in backgroud

        Log.i("Out-one", "testing");
        Cursor result = mydb.getDispatchDataToTransfer(mydb.getDateTime());
        result.moveToFirst();
        int count=0;
        String urlString = "";

        while(!result.isAfterLast()){
            count++;
            if(NetworkUtils.isNetworkAvailable(mContext.getApplicationContext())){
                String scanqty = result.getString(result.getColumnIndex("scanqty"));
                String lineid = result.getString(result.getColumnIndex("dispatchlineid"));
                //addDataToDatabase(lfarm, scanqty, lineid);
                if(scanqty == null){
                    String postData = "lineid="+lineid+"&scanqty="+scanqty+"&farm="+farm;
                    Log.i("Out"+count, postData);


                }


                result.moveToNext();
//                            Toast.makeText(getApplicationContext(), "Syncing Header "+count, Toast.LENGTH_SHORT ).show();
            }
            else {
                Log.i("netw", "no net");
            }
        }

        return null;
    }


}
//private class PostDataAsyncTask extends AsyncTask<String, Void, Void> {
//    @Override
//    protected Void doInBackground(String... params) {
//        String dataToPost = params[0];
//
//        // Perform your network operations here
//        // Connect to your MySQL database and post the data
//
//        // Example code for posting data using HttpURLConnection
//        try {
//            URL url = new URL("http://your-website.com/post-data.php");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//
//            // Set the data to post
//            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//            writer.write(dataToPost);
//            writer.flush();
//
//            // Get the response
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//
//            // Close connections
//            writer.close();
//            reader.close();
//
//            // Process the response if needed
//            String serverResponse = response.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
