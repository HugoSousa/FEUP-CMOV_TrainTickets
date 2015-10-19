package feup.cmov;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Hugo on 19/10/2015.
 */
public class ApiRequest extends AsyncTask<String, Void, JSONObject> {

    private Context context;

    ApiRequest(Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return requestWebService("http://192.168.1.65:8080/api/route?from=1&to=4");
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        System.out.println(result);
        //create new activity with list of routes
        Intent listRoutesIntent = new Intent(context, RoutesListActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        listRoutesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(listRoutesIntent);
        System.out.println("Open list of routes");
    }

    public static JSONObject requestWebService(String serviceUrl) {
        //disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();

            urlConnection.setConnectTimeout(5000);
            //urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

            } else{
                // handle any other errors, like 404, 500,..
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));
        } catch (Exception e) {
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
