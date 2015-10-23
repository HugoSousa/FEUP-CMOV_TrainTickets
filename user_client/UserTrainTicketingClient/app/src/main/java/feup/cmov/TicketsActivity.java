package feup.cmov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class TicketsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        //do something with the tickets data.
        //Bundle extras = getIntent().getExtras();
        //String ticketsString = (String)extras.get("data");
        

        ArrayList<Ticket> tickets = new ArrayList<Ticket>();

        tickets.add(new Ticket("abcdefgh", "2015-10-21Z10:45:00", 10, "A", "B"));
        tickets.add(new Ticket("abcd", "2015-10-22Z10:45:00", 10, "A", "B"));
        tickets.add(new Ticket("abcdef", "2015-10-23Z10:45:00", 10, "A", "B"));

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
