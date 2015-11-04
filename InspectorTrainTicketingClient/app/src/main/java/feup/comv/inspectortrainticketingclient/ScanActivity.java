package feup.comv.inspectortrainticketingclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView scannerView;
    private String routeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(scannerView);              // Set the scanner view as the content view

        Bundle extras = getIntent().getExtras();
        routeString = (String)extras.get("route");
        Route route = Route.convertKeyToTicket(routeString);

        String result_string = "{\"data\":{\"date\":\"2015-11-04 09:00:00\",\"user\":\"1\",\"route\":\"15\",\"code\":\"58b0d531-e344-41bf-9554-6306f9a75405\"},\"signature\":\"ZtrDlNUGprZnW2IHafZFUS6C3IP07A4MEE\\/Dzn7ssFCkfj\"}";
        validateResult(result_string);

    }


    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        Log.d("cenas", result.getContents());
        validateResult(result.getContents());

    }

    public void validateResult(String result) {
        Log.d("cenas", "Reading result:");
        Log.d("cenas", result);
        JSONObject ticketObj = null;
        try {
            ticketObj = new JSONObject(result);
            JSONObject dataObj = ticketObj.getJSONObject("data");
            String code = dataObj.getString("code");
            String signature = ticketObj.getString("signature");


            SharedPreferences sp = getSharedPreferences("routes", 0);
            HashSet<String> tickets = (HashSet<String>) sp.getStringSet(routeString, null);
            Boolean found = false;
            for (String ticket: tickets) {
                JSONObject tObj = new JSONObject(ticket);
                String ticketCode =  tObj.getString("uuid");
                if (ticketCode.equals(code)) {
                    Log.d("cenas", "Local match:");
                    Log.d("cenas", ticket);
                    String tSig = tObj.getString("signature");
                    String tRouteId = tObj.getString("route_id");
                    String userId = tObj.getString("user_id");
                    String dateString = tObj.getString("route_date");

                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = dt.parse(dateString);

                    SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String properDate = dt1.format(date);

                    String mask = code + " " + tRouteId + " " + userId + " " + properDate;
                    Log.d("cenas",mask);
                    Log.d("cenas",signature);
                    //TODO check with mask/signature
                    found = true;
                    break;
                }
            }

        } catch (JSONException e) {
            Log.d("cenas",e.toString());
        } catch (ParseException e) {
            Log.d("cenas",e.toString());
        }

    }
}
