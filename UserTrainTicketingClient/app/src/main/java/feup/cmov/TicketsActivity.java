package feup.cmov;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TicketsActivity extends AppCompatActivity implements OnApiRequestCompleted{

    BroadcastReceiver br;
    private static boolean firstConnect = true;
    private ArrayList<Ticket> tickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        removePastLocalTickets();

        if(! isNetworkConnected()) {
            Toast toast = Toast.makeText(this, "You have no internet connection. Only tickets from local storage are available now. Reconnect to internet to check all your tickets.", Toast.LENGTH_LONG);
            ((TextView)toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
            toast.show();
            setTicketsNoInternet();
        }else {
            setTicketsInternet();
        }


    }

    private void setTicketsInternet() {
        tickets.clear();

        SharedPreferences sp = this.getSharedPreferences("login", 0);
        String token = sp.getString("token", null);
        if (token == null) {
            Toast toast = Toast.makeText(this, "You need to be logged in to check your unused tickets.", Toast.LENGTH_LONG);
            ((TextView)toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
            toast.show();
        } else {
            ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.TICKETS, token);
            request.execute("tickets");
        }
    }

    private void setTicketsNoInternet() {
        tickets.clear();

        SharedPreferences sp = this.getSharedPreferences("tickets", 0);
        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String ticketString = sp.getString(entry.getKey(), null);
            try {
                if(ticketString != null) {
                    JSONObject ticketObj = new JSONObject(ticketString);
                    Ticket t = Ticket.JSONtoTicket(ticketObj, entry.getKey());
                    tickets.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setTickets(tickets);
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

        setTickets(tickets);
    }

    private void setTickets(ArrayList<Ticket> tickets) {

        if(tickets.size() == 0){
            ((TextView)findViewById(R.id.label_info)).setText("No available tickets");
            findViewById(R.id.label_info).setVisibility(View.VISIBLE);
            findViewById(R.id.local_storage_actions).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.label_info).setVisibility(View.GONE);
            findViewById(R.id.local_storage_actions).setVisibility(View.VISIBLE);
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

    public void addAllLocal(View view){
        ListView listTickets = (ListView)findViewById(R.id.list_tickets);
        ArrayAdapter<Ticket> adapter = (ArrayAdapter<Ticket>)listTickets.getAdapter();
        for(int i=0; i<adapter.getCount();i++){
            Ticket t = adapter.getItem(i);

            //View row = listTickets.getChildAt(i);
            //ImageButton symbol = (ImageButton)row.findViewById(R.id.symbol);
            //t.savedLocal = true;

            SharedPreferences sp = getSharedPreferences("tickets", 0);
            SharedPreferences.Editor editor = sp.edit();

            if(! ticketExistsLocally(t.code)){
                editor.putString(t.code, t.toJSON().toString());
                editor.commit();

                //symbol.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void removeAllLocal(View view){
        ListView listTickets = (ListView)findViewById(R.id.list_tickets);
        ArrayAdapter<Ticket> adapter = (ArrayAdapter<Ticket>)listTickets.getAdapter();
        for(int i=0; i<adapter.getCount();i++){
            Ticket t = adapter.getItem(i);

            //View row = listTickets.getChildAt(i);
            //ImageButton symbol = (ImageButton)row.findViewById(R.id.symbol);

            SharedPreferences sp = getSharedPreferences("tickets", 0);
            SharedPreferences.Editor editor = sp.edit();

            if(ticketExistsLocally(t.code)){
                editor.putString(t.code, null);
                editor.commit();

                //symbol.setImageResource(R.drawable.ic_add_black_24dp);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void removePastLocalTickets(){
        SharedPreferences sp = this.getSharedPreferences("tickets", 0);
        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String ticketString = sp.getString(entry.getKey(), null);
            try {
                if(ticketString != null) {
                    JSONObject ticketObj = new JSONObject(ticketString);
                    Ticket t = Ticket.JSONtoTicket(ticketObj, entry.getKey());

                    String ticketDateString = t.date.split(" ")[0];
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date ticketDate = df.parse(ticketDateString);
                    Date today = Calendar.getInstance().getTime();

                    if(ticketDate.before(today)){
                        //remove from local storage
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(entry.getKey(), null);
                        editor.commit();
                    }else {
                        tickets.add(t);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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

            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sp = context.getSharedPreferences("login", 0);
                    String userId = sp.getString("id", null);

                    ListView listView = (ListView) v.getParent();
                    Ticket t = (Ticket) listView.getItemAtPosition(position);

                    Intent ticketActivity = new Intent(context, TicketActivity.class);
                    ticketActivity.putExtra("code", t.code);
                    ticketActivity.putExtra("route", Integer.toString(t.route));
                    ticketActivity.putExtra("date", t.date);
                    ticketActivity.putExtra("id", userId);
                    ticketActivity.putExtra("signature", t.signature);

                    ticketActivity.putExtra("fromStation", t.fromStation);
                    ticketActivity.putExtra("toStation", t.toStation);
                    ticketActivity.putExtra("distance", Integer.toString(t.distance));
                    ticketActivity.putExtra("price", Integer.toString(t.price));

                    startActivity(ticketActivity);
                }
            });
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

                        ListView listTickets = (ListView)findViewById(R.id.list_tickets);
                        ArrayAdapter<Ticket> adapter = (ArrayAdapter<Ticket>)listTickets.getAdapter();
                        tickets.remove(t);
                        adapter.notifyDataSetChanged();

                        if(tickets.size() == 0){
                            ((TextView)findViewById(R.id.label_info)).setText("No available tickets");
                            findViewById(R.id.label_info).setVisibility(View.VISIBLE);
                            findViewById(R.id.local_storage_actions).setVisibility(View.INVISIBLE);
                        }

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

    }

    private boolean ticketExistsLocally(String code){
        SharedPreferences sp = getSharedPreferences("tickets", 0);

        String ticketLocal = sp.getString(code, null);

        return (ticketLocal != null);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(br != null) {
            unregisterReceiver(br);
            firstConnect = true;
            br = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (isNetworkConnected()) {
                    if(firstConnect) {
                        firstConnect = false;
                    }else {
                        Toast toast = Toast.makeText(TicketsActivity.this, "You are now connected to a network again.\n You can check all your tickets.", Toast.LENGTH_LONG);
                        ((TextView)toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                        toast.show();
                        setTicketsInternet();
                        findViewById(R.id.add_tickets_local_storage_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.label_info).setVisibility(View.GONE);
                    }
                }else{
                    if(firstConnect) {
                        firstConnect = false;
                    }else {
                        Toast toast = Toast.makeText(TicketsActivity.this, "You have no internet connection.\n Only tickets from local storage are available now.\n Reconnect to internet to see all your tickets.", Toast.LENGTH_LONG);
                        ((TextView)toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                        toast.show();
                        setTicketsNoInternet();
                        findViewById(R.id.add_tickets_local_storage_view).setVisibility(View.GONE);
                        findViewById(R.id.label_info).setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        registerReceiver(br, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

}
