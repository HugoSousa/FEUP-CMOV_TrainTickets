package feup.cmov;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Hugo on 27/10/2015.
 */
public class MainActivityCheckServerUpTask extends AsyncTask<Void, Void, Boolean> {

    private final String API_URL = "http://192.168.1.65:8080/api/";
    private Context context;

    public MainActivityCheckServerUpTask(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        int statusCode = -1;

        try {
            HttpURLConnection urlConnection;
            URL urlToRequest = new URL(API_URL + "ping");
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(5000);
            statusCode = urlConnection.getResponseCode();
        }catch(Exception e){
            System.out.println(e);
            return false;
        }

        return (statusCode == HttpURLConnection.HTTP_OK);
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if(result){
            //server is up

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity)context).finish();
        }else{
            Toast.makeText(context, "Server is down. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }
}
