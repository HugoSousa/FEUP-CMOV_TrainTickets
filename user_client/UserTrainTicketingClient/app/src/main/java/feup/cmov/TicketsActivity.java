package feup.cmov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TicketsActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        SharedPreferences sp = this.getSharedPreferences("login", 0);
        String token = sp.getString("token", null);
        if(token == null) {
            Toast.makeText(this, "You need to be logged in to check your unused tickets.", Toast.LENGTH_LONG).show();
        }else{
            ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.TICKETS, token);
            request.execute("tickets");
        }

        //do something with the tickets data.
        //Bundle extras = getIntent().getExtras();
        //String ticketsString = (String)extras.get("data");
    }


    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();

        try {
            JSONArray ticketsArray = result.getJSONArray("tickets");
            for(int i = 0; i < ticketsArray.length(); i++){
                JSONObject ticket = (JSONObject)ticketsArray.get(i);
                String from = getStationName(ticket.getString("start_station"));
                String to = getStationName(ticket.getString("end_station"));
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(ticket.getString("route_date"));
                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date).toString();
                //String date = ticket.getString("route_date");
                int price = ticket.getInt("price");
                int distance = ticket.getInt("distance");

                String code = ticket.getString("uuid");
                String signature = ticket.getString("signature");

                Ticket t = new Ticket(code, dateString, price, from, to, signature, distance);
                tickets.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ListView listTickets = (ListView)findViewById(R.id.list_tickets);
        ArrayAdapter adapter = new ArrayAdapter<Ticket>(this, android.R.layout.simple_list_item_1, tickets);
        listTickets.setAdapter(adapter);

        listTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Ticket ticket = (Ticket) parent.getItemAtPosition(position);

                Intent ticketActivity = new Intent(getApplicationContext(), TicketActivity.class);
                ticketActivity.putExtra("code", ticket.code);
                ticketActivity.putExtra("fromStation", ticket.fromStation);
                ticketActivity.putExtra("toStation", ticket.toStation);
                ticketActivity.putExtra("date", ticket.date);
                startActivity(ticketActivity);
                //Toast.makeText(SuggestionActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getStationName(String id){

        SharedPreferences sp = this.getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }
}
