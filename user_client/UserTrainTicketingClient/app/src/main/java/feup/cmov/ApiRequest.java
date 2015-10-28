package feup.cmov;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Hugo on 19/10/2015.
 */
public class ApiRequest extends AsyncTask<String, Void, JSONObject> {

    private OnApiRequestCompleted listener;

    public static int GET = 1;
    public static int POST = 2;
    private int requestType;

    //private final String API_URL = "http://192.168.1.65:8080/api/";
    private final String API_URL = "http://172.30.33.222:8080/api/";

    public enum requestCode {
        LOGIN,
        ROUTE,
        ROUTES,
        STATIONS,
        TICKETS,
        PING
    }
    private requestCode requestCode;

    ApiRequest(int requestType, OnApiRequestCompleted listener, requestCode requestCode){
        this.requestType = requestType;
        this.listener = listener;
        this.requestCode = requestCode;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try{
            JSONObject result = null;
            if(requestType == GET)
                result = requestWebService(API_URL + params[0], GET, null);
            else if(requestType == POST)
                result = requestWebService(API_URL + params[0], POST, params[1]);
            return result;
        }catch(SocketTimeoutException e){
            System.out.println("Connection timed out. Show a warning to the user.");
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        if(listener != null)
            listener.onTaskCompleted(result, requestCode);
    }

    public static JSONObject requestWebService(String serviceUrl, int requestType, String query) throws SocketTimeoutException {
        //disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();
            urlConnection.setConnectTimeout(5000);

            if(requestType == ApiRequest.POST) {
                urlConnection.setDoOutput(true); // Triggers POST.
                urlConnection.setRequestProperty("Content-Type", "application/json");
                try {
                    OutputStream output = urlConnection.getOutputStream();
                    output.write(query.getBytes("UTF-8"));
                }catch (Exception e){
                    System.out.println("Error: " + e);
                    throw e;
                }
            }
            InputStream in = null;

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } else{
                // handle any other errors, like 404, 500,..
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }

            // create JSON object from content
            return new JSONObject(getResponseText(in));
        } catch(SocketTimeoutException e){
            System.out.println("Error: " + e);
            throw e;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            // response body is no valid JSON string
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }
    
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}
