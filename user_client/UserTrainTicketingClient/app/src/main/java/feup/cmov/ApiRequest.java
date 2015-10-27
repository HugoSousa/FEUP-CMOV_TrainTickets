package feup.cmov;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

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

    public static int GET = 1;
    public static int POST = 2;
    private int requestType;
    private Context context;
    private Class redirect;
    private Bundle data;
    //private final String API_URL = "http://192.168.1.65:8080/api/";
    private final String API_URL = "http://172.30.9.206:8080/api/";

    ApiRequest(Context context, Class redirect, Bundle data, int requestType){
        this.context = context;
        this.redirect = redirect;
        this.data = data;
        this.requestType = requestType;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try{
            //JSONObject result = requestWebService("http://172.30.9.206:8080/api/route?from=1&to=4");
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
        //System.out.println(result);
        //create new activity with list of routes
        if(result != null) {
            if(redirect != null) {
                System.out.println("ON POST EXECUTE");
                Intent intent = new Intent(context, redirect);
                intent.putExtra("data", result.toString());
                if(data != null)
                    intent.putExtra("other", data);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }else{
            Toast.makeText(context, "Can't retrieve your search. Please check if you you're connected to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public static JSONObject requestWebService(String serviceUrl, int requestType, String query) throws SocketTimeoutException {
        //disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();
            urlConnection.setConnectTimeout(5000);
            //urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            if(requestType == ApiRequest.POST) {
                urlConnection.setDoOutput(true); // Triggers POST.
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                try {
                    OutputStream output = urlConnection.getOutputStream();
                    output.write(query.getBytes("UTF-8"));
                }catch (Exception e){
                    System.out.println("Error: " + e);
                    throw e;
                }
            }

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

            } else{
                // handle any other errors, like 404, 500,..
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
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
