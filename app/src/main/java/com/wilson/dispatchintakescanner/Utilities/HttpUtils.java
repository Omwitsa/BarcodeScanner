package com.wilson.dispatchintakescanner.Utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static String sendPostRequest(String urlString, String postData) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Send data
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(postData);
            dataOutputStream.flush();
            dataOutputStream.close();

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return response
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

//if (NetworkUtils.isNetworkAvailable(this)) {
//        // Network is available, proceed with posting data
//
//        // Prepare the data to be posted (e.g., convert to JSON or URL-encoded form data)
//        String postData = "param1=value1&param2=value2";
//
//        // Send the HTTP POST request to the server-side script
//        String response = HttpUtils.sendPostRequest("http://your-server.com/post-data-script.php", postData);
//
//        // Handle the response from the server-side script
//        if (response != null) {
//        // Post successful, do something
//        } else {
//        // Post failed, show an error message or handle the failure as per your app's requirements
//        }
//        } else {
//        // No network connection
//        // Show an error message or handle the lack of network connection as per your app's requirements
//        }
