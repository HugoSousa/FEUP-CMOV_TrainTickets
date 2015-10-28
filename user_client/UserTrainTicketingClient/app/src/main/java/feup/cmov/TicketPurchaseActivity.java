package feup.cmov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class TicketPurchaseActivity extends AppCompatActivity implements OnApiRequestCompleted{
    private String from = null;
    private String to = null;
    private String date = null;
    private String time = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase);

        //data of the route is in extras "data"
        Bundle extras = getIntent().getExtras();
        String routeString = (String)extras.get("data");

        boolean waitingCentral = false;

        //TODO fill fields with data of route
        ArrayList<Integer> stations = new ArrayList<Integer>();
        ArrayList<String> times = new ArrayList<String>();
        int price = -1, distance = -1;
        Route route = null;
        int ticket1Size = 0, ticket2Size = 0;
        int train1 = -1, train2 = -1;

        try {
            JSONObject routeJSON = new JSONObject(routeString);
            JSONArray ticket_1 = routeJSON.getJSONArray("ticket_1");
            ticket1Size = ticket_1.length();

            price = (int)routeJSON.get("price");
            distance = (int)routeJSON.get("distance");
            from = (String)routeJSON.get("from");
            to = (String)routeJSON.get("to");
            date = (String)routeJSON.get("date");

            train1 = (int)((JSONObject)ticket_1.get(0)).get("train_id");

            for(int i = 0; i < ticket_1.length(); i++){
                JSONObject stationTime = (JSONObject)ticket_1.get(i);

                int station = (int)stationTime.get("station_id");
                String time = (String) stationTime.get("time");

                stations.add(station);
                times.add(time);
            }

            try{
                JSONArray ticket_2 = (JSONArray)routeJSON.getJSONArray("ticket_2");
                ticket2Size = ticket_2.length();

                waitingCentral = true;
                train2 = (int)((JSONObject)ticket_2.get(0)).get("train_id");

                for(int i = 0; i < ticket_2.length(); i++){
                    JSONObject stationTime = (JSONObject)ticket_2.get(i);

                    int station = (int)stationTime.get("station_id");
                    String time = (String) stationTime.get("time");

                    stations.add(station);
                    times.add(time);
                }

                System.out.println("2 tickets");

                route = new Route(stations, times, train1, train2, 0); //calcular waiting time
            }
            catch(JSONException e){
                //it's not a complex route
                System.out.println("Only 1 ticket");

                route = new Route(stations, times, train1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        time = times.get(0);

        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + getStationName(from) + " TO " + getStationName(to));
        ((TextView)findViewById(R.id.text_route_date)).setText(date);
        ((TextView)findViewById(R.id.text_route_distance)).setText(((Integer) distance).toString());
        ((TextView)findViewById(R.id.text_route_price)).setText(((Integer)price).toString());

        ((TextView)findViewById(R.id.text_train_1)).setText("Train: " + Integer.toString(train1));


        //add stations to tables
        TableLayout table1 = (TableLayout) findViewById(R.id.table_ticket1);

        findViewById(R.id.table_ticket1_header).setVisibility(View.VISIBLE); //header row

        for(int i = 0; i < ticket1Size; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            TextView stationTextView = new TextView(this);
            stationTextView.setGravity(Gravity.CENTER);
            stationTextView.setText(getStationName(route.stationTimes.get(i).station.toString()));

            TextView timeTextView = new TextView(this);
            timeTextView.setGravity(Gravity.CENTER);
            timeTextView.setText(route.stationTimes.get(i).time);

            row.addView(stationTextView);
            row.addView(timeTextView);

            table1.addView(row);
        }

        if(waitingCentral) {
            TableLayout table2 = (TableLayout) findViewById(R.id.table_ticket2);

            findViewById(R.id.table_ticket2_header).setVisibility(View.VISIBLE);

            for (int i = ticket1Size; i < ticket1Size + ticket2Size; i++) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);

                TextView stationTextView = new TextView(this);
                stationTextView.setGravity(Gravity.CENTER);
                stationTextView.setText(getStationName(route.stationTimes.get(i).station.toString()));

                TextView timeTextView = new TextView(this);
                timeTextView.setGravity(Gravity.CENTER);
                timeTextView.setText(route.stationTimes.get(i).time);

                row.addView(stationTextView);
                row.addView(timeTextView);

                table2.addView(row);
            }
        }

        Button buyButton = (Button) findViewById(R.id.button_buy_ticket);
        if(waitingCentral) {

            String arrivalCentralTimeString = route.stationTimes.get(ticket1Size - 1).time;
            String departureCentralTimeString = route.stationTimes.get(ticket1Size).time;

            try {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

                Date arrivalDate = format.parse(arrivalCentralTimeString);
                Date departureDate = format.parse(departureCentralTimeString);

                long diff = departureDate.getTime() - arrivalDate.getTime();
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffMinutes = diff / (60 * 1000) % 60;

                DecimalFormat df = new DecimalFormat("00");
                String diffHoursString = df.format(diffHours);
                String diffMinutesString = df.format(diffMinutes);

                ((TextView)findViewById(R.id.text_route_waiting_time)).setText(diffHoursString + ":" + diffMinutesString);


            } catch (ParseException e) {
                e.printStackTrace();
            }


            buyButton.setText("BUY TICKETS");
            findViewById(R.id.tables_separator1).setVisibility(View.VISIBLE);
            findViewById(R.id.tables_separator2).setVisibility(View.VISIBLE);
            findViewById(R.id.label_route_waiting_time).setVisibility(View.VISIBLE);
            findViewById(R.id.ticket2_train_info).setVisibility(View.VISIBLE);

            ((TextView)findViewById(R.id.text_train_2)).setText("Train: " + Integer.toString(train2));

        }else{
            buyButton.setText("BUY TICKET");
        }

        //if waiting central, set button text "BUY TICKETS" and show "Waiting Time"
        //otherwise, set button text "BUY TICKET" and hide "Waiting Time"
        //do only 1 request to server, even if it's 2 separate tickets
    }

    public void buyTickets(View view){

        SharedPreferences sp = this.getSharedPreferences("login", 0);
        String token = sp.getString("token", null);
        if(token == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            Toast.makeText(this, "You need to be logged in to buy tickets.", Toast.LENGTH_LONG).show();
        }else {
            ApiRequest request = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.BUY_TICKET, token);

            JSONObject data = new JSONObject();
            try {
                data.put("to", to);
                data.put("from", from);
                data.put("date", date);
                data.put("time", time);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            request.execute("tickets/purchase", data.toString());
        }
    }
    private String getStationName(String id){

        SharedPreferences sp = this.getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(requestCode == ApiRequest.requestCode.BUY_TICKET){
            if(!result.has("error")){
                //success
                //redirect to tickets
                Intent ticketsIntent = new Intent(this, TicketsActivity.class);
                startActivity(ticketsIntent);
            }else{
                try {
                    String error = result.getString("error");
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
