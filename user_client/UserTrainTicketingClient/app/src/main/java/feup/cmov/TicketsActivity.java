package feup.cmov;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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


        //TODO se nao tem internet, abrir tickets da localStorage

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
                int route = ticket.getInt("route_id");
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(ticket.getString("route_date"));
                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date).toString();
                int price = ticket.getInt("price");
                int distance = ticket.getInt("distance");

                String code = ticket.getString("uuid");
                String signature = ticket.getString("signature");

                Ticket t = new Ticket(code, dateString, price, from, to, signature, distance, route);
                tickets.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ListView listTickets = (ListView)findViewById(R.id.list_tickets);
        //ArrayAdapter adapter = new ArrayAdapter<Ticket>(this, android.R.layout.simple_list_item_1, tickets);
        TicketAdapter adapter = new TicketAdapter(this, tickets);
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
                ticketActivity.putExtra("price", Integer.toString(ticket.price));
                ticketActivity.putExtra("distance", Integer.toString(ticket.distance));
                ticketActivity.putExtra("signature", ticket.signature);
                ticketActivity.putExtra("route", Integer.toString(ticket.route));
                startActivity(ticketActivity);
                //Toast.makeText(SuggestionActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getStationName(String id){

        SharedPreferences sp = this.getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }

    class TicketAdapter extends ArrayAdapter<Ticket>{

        private ArrayList<Ticket> tickets;
        private Context context;

        TicketAdapter(Context context, ArrayList<Ticket> tickets) {
            super(TicketsActivity.this, R.layout.ticket_list_row, tickets);

            this.context = context;
            this.tickets = tickets;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row=convertView;
            if (row==null) {
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.ticket_list_row, parent, false);
            }
            Ticket t = tickets.get(position);
            ((TextView)row.findViewById(R.id.ticket_text)).setText(t.toString());
            ImageButton symbol = (ImageButton)row.findViewById(R.id.symbol);

            SharedPreferences sp = context.getSharedPreferences("tickets", 0);
            String ticketLocal = sp.getString(t.code, null);

            if(ticketLocal == null)
                symbol.setImageResource(R.drawable.ic_add_black_24dp);
            else
                symbol.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);

            ImageButton.OnClickListener clickListener = new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {

                    System.out.println("AQUI");

                    View row = (View) v.getParent();
                    ListView listView = (ListView) row.getParent();
                    Ticket t = (Ticket) listView.getItemAtPosition(position);
                    ImageButton symbol = (ImageButton)row.findViewById(R.id.symbol);

                    SharedPreferences sp = getSharedPreferences("tickets", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    if(ticketExistsLocally(t.code)){
                        //remove from local
                        editor.putString(t.code, null);
                        editor.commit();


                        symbol.setImageResource(R.drawable.ic_add_black_24dp);
                    }else{
                        //add to local
                        editor.putString(t.code, t.toJSON().toString());
                        editor.commit();

                        symbol.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
                    }

                }
            };

            symbol.setOnClickListener(clickListener);

            return(row);
        }


        private boolean ticketExistsLocally(String code){
            SharedPreferences sp = getSharedPreferences("tickets", 0);

            String ticketLocal = sp.getString(code, null);

            return (ticketLocal != null);
        }
    }


}
