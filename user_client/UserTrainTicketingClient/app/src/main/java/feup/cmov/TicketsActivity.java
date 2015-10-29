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

import java.util.ArrayList;

public class TicketsActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        SharedPreferences sp = this.getSharedPreferences("login", 0);
        String token = sp.getString("token", null);
        if(token == null) {
            ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.TICKETS, token);
            request.execute("tickets");
        }else{
            Toast.makeText(this, "You need to be logged in to check your unused tickets.", Toast.LENGTH_LONG).show();
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
                String from = ticket.getString("start_station");
                String to = ticket.getString("end_station");
                String date = ticket.getString("route_date");
                int price = ticket.getInt("price");
                int distance = ticket.getInt("distance");
                String code = ticket.getString("code");
                String signature = ticket.getString("signature");

                Ticket t = new Ticket(code, date, price, from, to, signature, distance);
                tickets.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        tickets.add(new Ticket("abcdefgh", "2015-10-21Z10:45:00", 10, "A", "B"));
        tickets.add(new Ticket("abcd", "2015-10-22Z10:45:00", 10, "A", "B"));
        tickets.add(new Ticket("abcdef", "2015-10-23Z10:45:00", 10, "A", "B"));
        */

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
}
