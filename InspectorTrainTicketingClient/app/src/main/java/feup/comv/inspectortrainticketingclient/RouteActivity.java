package feup.comv.inspectortrainticketingclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;

public class RouteActivity extends AppCompatActivity implements OnApiRequestCompleted{

    private Route route;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle extras = getIntent().getExtras();
        String routeKey = (String)extras.get("route_key");

        SharedPreferences sp = getSharedPreferences("routes", 0);
        Map<String,?> keys = sp.getAll();

        HashSet<String> tickets = null;
        for(Map.Entry<String,?> entry : keys.entrySet()){

            if(entry.getKey().equals(routeKey)) {
                tickets = (HashSet<String>) entry.getValue();
                break;
            }
        }

        int validated = 0;
        for(String ticketString: tickets){
            JSONObject ticketObj = null;
            try {
                ticketObj = new JSONObject(ticketString);
                System.out.println(ticketObj);
                JSONArray validatedArray = ticketObj.getJSONArray("is_validated");
                if(validatedArray.getInt(0) == 1)
                    validated++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        route = Route.convertKeyToTicket(routeKey);

        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + route.fromString + " TO " + route.toString);
        ((TextView)findViewById(R.id.text_route_date)).setText(route.date + " " + route.time);
        ((TextView)findViewById(R.id.text_total_tickets)).setText(Integer.toString(tickets.size()));
        ((TextView)findViewById(R.id.text_validated_tickets)).setText(Integer.toString(validated));
        //System.out.println(route);
        //System.out.println("TICKETS: \n" + tickets);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(br != null) {
            unregisterReceiver(br);
            br = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (isNetworkConnected()) {
                    findViewById(R.id.upload_btn).setEnabled(true);
                }else{
                    findViewById(R.id.upload_btn).setEnabled(false);
                }
            }
        };

        registerReceiver(br, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    public void scanRoute(View view){
        //pass the route data to the activity (need route_id...?)

        Intent scanActivity = new Intent(this, ScanActivity.class);
        scanActivity.putExtra("route", route.getKey());
        startActivity(scanActivity);

    }

    public void uploadTrip(View view){

        SharedPreferences spToken = getSharedPreferences("login", 0);
        String token = spToken.getString("token", null);

        SharedPreferences sp = getSharedPreferences("routes", 0);

        HashSet<String> tickets = new HashSet<>();
        Map<String,?> keys = sp.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){

            if(entry.getKey().equals(route.getKey())){
                tickets = (HashSet<String>)entry.getValue();
                break;
            }
        }


        JSONObject bodyObj = new JSONObject();
        JSONArray ticketsObj = new JSONArray();
        for(String ticket: tickets){
            try {
                JSONObject  ticketObj = new JSONObject(ticket);

                ticketObj.remove("id");
                ticketObj.remove("switch_central");
                ticketObj.remove("distance");
                ticketObj.remove("price");
                ticketObj.remove("route_1");
                ticketObj.remove("route_2");
                ticketObj.remove("uuid");
                ticketObj.remove("signature");

                ticketsObj.put(ticketObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            bodyObj.put("tickets", ticketsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest request = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.UPLOAD, token);
        request.execute("tickets/upload", bodyObj.toString());


        //TODO upload statistics too
        //calculate noShows and totalTickets here...? (not from shared prefs)
        //update statistics and send to server. Set them to 0 on request completed
        /*
        SharedPreferences sp = getSharedPreferences("statistics", 0);

        int uploadedTickets = sp.getInt("uploaded_tickets", 0);
        int validatedTickets = sp.getInt("validated_tickets", 0);
        int fraudulentTickets = sp.getInt("fraudulent_tickets", 0);
        int noShows = sp.getInt("no_shows", 0);

        JSONObject statisticsObj = new JSONObject();
        try {
            statisticsObj.put("uploaded_routes", 1);
            statisticsObj.put("uploaded_tickets", uploadedTickets);
            statisticsObj.put("validated_tickets", validatedTickets);
            statisticsObj.put("fraudulent_tickets", fraudulentTickets);
            statisticsObj.put("no_shows", noShows);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest requestStatistics = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.UPDATE_STATISTICS, token);
        requestStatistics.execute("statistics/upload", statisticsObj.toString());
        */

        System.out.println(ticketsObj);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(result != null){
            if(requestCode == ApiRequest.requestCode.UPLOAD){
                System.out.println("AFTER UPLOAD");
            }else if(requestCode == ApiRequest.requestCode.UPDATE_STATISTICS){
                if(!result.has("error")){
                    SharedPreferences sp = getSharedPreferences("statistics", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putInt("uploaded_tickets", 0);
                    editor.putInt("validated_tickets", 0);
                    editor.putInt("fraudulent_tickets", 0);
                    editor.putInt("no_shows", 0);
                }
            }

        }
    }
}
