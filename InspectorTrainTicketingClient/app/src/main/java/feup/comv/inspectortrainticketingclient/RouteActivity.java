package feup.comv.inspectortrainticketingclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class RouteActivity extends AppCompatActivity implements OnApiRequestCompleted{

    private Route route;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Toast.makeText(getApplicationContext(), "NFC attempted. Open the current route to validate tickets.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_route);

        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.STATIONS, null);
        request.execute("stations");

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

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        if (route != null) {
            Log.d("cenas2",route.getKey());
            validateResult( new String(msg.getRecords()[0].getPayload()));
        }
        Log.d("cenas2", new String(msg.getRecords()[0].getPayload()));

    }

    public void validateResult(String result) {
        String routeString = route.getKey();

        JSONObject ticketObj = null;
        try {
            ticketObj = new JSONObject(result);
            JSONObject dataObj = ticketObj.getJSONObject("data");
            String code = dataObj.getString("code");
            String signature = ticketObj.getString("signature");

            SharedPreferences sp2 = getSharedPreferences("login", 0);
            SharedPreferences sp = getSharedPreferences("routes", 0);
            String public_string = sp2.getString("pub", null);


            HashSet<String> tickets = (HashSet<String>) sp.getStringSet(routeString, null);
            boolean found = false;
            boolean validated = false;
            String ticketCode = null;
            for (String ticket: tickets) {

                JSONObject tObj = new JSONObject(ticket);
                ticketCode =  tObj.getString("uuid");
                if (ticketCode.equals(code)) {
                    Log.d("cenas", "Local match:");
                    Log.d("cenas", ticket);
                    //String tSig = tObj.getString("signature");
                    String tRouteId = tObj.getString("route_id");
                    String userId = tObj.getString("user_id");
                    String dateString = tObj.getString("route_date");

                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = dt.parse(dateString);

                    SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String properDate = dt1.format(date);

                    String mask = code + " " + tRouteId + " " + userId + " " + properDate;

                    //remove comment lines from public_key
                    String[] publicKeySplit = public_string.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < publicKeySplit.length - 1; i++) {
                        if(! publicKeySplit[i].startsWith("-----"))
                            sb.append(publicKeySplit[i]);
                    }
                    String publicKeyString = sb.toString();

                    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString.getBytes(), Base64.DEFAULT)));
                    //PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString.getBytes("utf-8"), Base64.DEFAULT)));
                    //http://stackoverflow.com/questions/11532989/android-decrypt-rsa-text-using-a-public-key-stored-in-a-file

                    Signature sg = Signature.getInstance("SHA1WithRSA");


                    sg.initVerify(publicKey);
                    sg.update(mask.getBytes());
                    validated = sg.verify(Base64.decode(signature.getBytes(), Base64.DEFAULT));
                    Log.d("cenas", "Resultado "+ validated);
                    found = true;
                    break;
                }
            }


            if(found){
                if(validated) {
                    if(! isValidated(routeString, ticketCode)) {
                        String fromToMessage = setTicketValidated(routeString, ticketCode);
                        setValidation(true, fromToMessage);
                    }else{
                        setValidation(false, "Ticket already validated.");
                    }
                }else{
                    addFraudulentTicketStatistic();
                    setValidation(false, "Signature verify failed.");
                }
            }else{
                addFraudulentTicketStatistic();
                setValidation(false, "Invalid ticket code.");
            }

        }catch(Exception e){
            addFraudulentTicketStatistic();
            setValidation(false, e.toString());
        }

    }

    private void setValidation(boolean validated, String reason) {
        if (validated) {
            ImageView iv = (ImageView)findViewById(R.id.ticket_validation_view);
            iv.setColorFilter(Color.parseColor("#4caf50"));
            iv.setImageResource(R.drawable.ic_navigation_check);
            int validatedTickets = Integer.parseInt(((TextView) findViewById(R.id.text_validated_tickets)).getText().toString());
            validatedTickets++;
            ((TextView)findViewById(R.id.text_validated_tickets)).setText(Integer.toString(validatedTickets));

            //check from where to where it is valid

            ((TextView)findViewById(R.id.validation_info_tv)).setText(reason);

        }else {
            ImageView iv = (ImageView)findViewById(R.id.ticket_validation_view);
            iv.setColorFilter(Color.parseColor("#f32313"));
            iv.setImageResource(R.drawable.ic_navigation_close);
            ((TextView)findViewById(R.id.validation_info_tv)).setText(reason);
        }
    }

    private void addFraudulentTicketStatistic(){
        SharedPreferences spStatistics = getSharedPreferences("statistics", 0);
        SharedPreferences.Editor statisticsEditor = spStatistics.edit();
        int fraudulent = spStatistics.getInt("fraudulent_tickets", 0);
        statisticsEditor.putInt("fraudulent_tickets", fraudulent + 1);
        statisticsEditor.commit();
    }

    private boolean isValidated(String routeKey, String ticketCode){

        HashSet<String> tickets = null;
        SharedPreferences sp = getSharedPreferences("routes", 0);
        Map<String,?> keys = sp.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){

            if(entry.getKey().equals(routeKey)) {
                tickets = (HashSet<String>) entry.getValue();
                break;
            }
        }

        try {
            for(String ticket: tickets){
                JSONObject ticketObj = new JSONObject(ticket);

                String code = ticketObj.getString("uuid");
                if(ticketCode.equals(code)){
                    JSONArray validatedArray = ticketObj.getJSONArray("is_validated");
                    if(validatedArray.get(0).equals(1))
                        return true;
                    else
                        return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String setTicketValidated(String routeKey, String ticketCode){
        String fromToString = null;
        String routeString = route.getKey();

        JSONArray validatedArray = new JSONArray();
        validatedArray.put(1);
        SharedPreferences sp = getSharedPreferences("routes", 0);
        Map<String,?> keys = sp.getAll();
        System.out.println(keys);
        HashSet<String> tickets = null;
        for(Map.Entry<String,?> entry : keys.entrySet()){

            if(entry.getKey().equals(routeKey)) {
                tickets = (HashSet<String>) entry.getValue();
                break;
            }
        }

        boolean found = false;
        try {
            for(String ticket: tickets){
                JSONObject ticketObj = new JSONObject(ticket);

                String code = ticketObj.getString("uuid");
                if(ticketCode.equals(code)){
                    found = true;
                    //intent.putExtra("from", ticketObj.getInt("start_station"));
                    //intent.putExtra("to", ticketObj.getInt("end_station"));
                    int from = ticketObj.getInt("start_station");
                    int to = ticketObj.getInt("end_station");
                    fromToString = "Valid from " + getStationName(Integer.toString(from)) + " to " + getStationName(Integer.toString(to));

                    //replace this ticket in the hashset
                    tickets.remove(ticket);
                    ticketObj.put("is_validated", validatedArray);
                    tickets.add(ticketObj.toString());

                    System.out.println(ticket);
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(found){
            SharedPreferences.Editor editor = sp.edit();
            editor.putStringSet(routeString, tickets);
            editor.commit();

            SharedPreferences spStatistics = getSharedPreferences("statistics", 0);
            SharedPreferences.Editor statisticsEditor = spStatistics.edit();
            int validated = spStatistics.getInt("validated_tickets", 0);
            statisticsEditor.putInt("validated_tickets", validated + 1);
            statisticsEditor.commit();
        }
        return fromToString;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ImageView iv = (ImageView)findViewById(R.id.ticket_validation_view);
                iv.setColorFilter(Color.parseColor("#4caf50"));
                iv.setImageResource(R.drawable.ic_navigation_check);
                int validated = Integer.parseInt(((TextView) findViewById(R.id.text_validated_tickets)).getText().toString());
                validated++;
                ((TextView)findViewById(R.id.text_validated_tickets)).setText(Integer.toString(validated));

                //check from where to where it is valid
                int from = data.getIntExtra("from", -1);
                int to = data.getIntExtra("to", -1);

                ((TextView)findViewById(R.id.validation_info_tv)).setText("Valid from " + getStationName(Integer.toString(from)) + " to " + getStationName(Integer.toString(to)));

            }else if(resultCode == RESULT_CANCELED){
                ImageView iv = (ImageView)findViewById(R.id.ticket_validation_view);
                iv.setColorFilter(Color.parseColor("#f32313"));
                iv.setImageResource(R.drawable.ic_navigation_close);
                ((TextView)findViewById(R.id.validation_info_tv)).setText(data.getStringExtra("reason"));
            }
        }
    }

    public void scanRoute(View view){
        Intent scanActivity = new Intent(this, ScanActivity.class);
        scanActivity.putExtra("route", route.getKey());
        startActivityForResult(scanActivity, 1);

    }

    public void uploadTrip(View view){

        int uploadedTickets = 0;
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

        uploadedTickets = tickets.size();

        JSONObject bodyObj = new JSONObject();
        JSONArray ticketsObj = new JSONArray();
        for(String ticket: tickets){
            try {
                JSONObject  ticketObj = new JSONObject(ticket);

                //only need to send the ticket uuid and is_validated
                ticketObj.remove("end_station");
                ticketObj.remove("route_date");
                ticketObj.remove("start_station");
                ticketObj.remove("route_id");
                ticketObj.remove("user_id");
                ticketObj.remove("id");
                ticketObj.remove("switch_central");
                ticketObj.remove("distance");
                ticketObj.remove("price");
                ticketObj.remove("route_1");
                ticketObj.remove("route_2");
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


        //update statistics and send to server. Set them to 0 on request completed
        SharedPreferences spStatistics = getSharedPreferences("statistics", 0);

        int validatedTickets = spStatistics.getInt("validated_tickets", 0);
        int fraudulentTickets = spStatistics.getInt("fraudulent_tickets", 0);
        int noShows = uploadedTickets - validatedTickets;

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

                    editor.putInt("validated_tickets", 0);
                    editor.putInt("fraudulent_tickets", 0);
                    editor.commit();

                    //remove uploaded route from sharedpreferences
                    removeRoute(route.getKey());

                    finish();
                }
            }else if(requestCode == ApiRequest.requestCode.STATIONS){
                try {
                    JSONArray stations = (JSONArray)result.get("stations");

                    SharedPreferences sp = this.getSharedPreferences("stations", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    for(int i = 0; i < stations.length(); i++){
                        JSONObject station = (JSONObject)stations.get(i);
                        editor.putString(station.get("id").toString(), (String) station.get("name"));
                    }

                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void removeRoute(String routeKey){
        SharedPreferences sp = getSharedPreferences("routes", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(routeKey, null);
        editor.commit();
    }

    private String getStationName(String id){

        SharedPreferences sp = this.getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }
}
