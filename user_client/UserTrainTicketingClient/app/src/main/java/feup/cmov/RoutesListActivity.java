package feup.cmov;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RoutesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        Bundle extras = getIntent().getExtras();
        String routesString = (String)extras.get("data");
        String date = (String)extras.get("date");

        ((TextView)findViewById(R.id.text_route_date)).setText(date);

        ArrayList<Route> routes = new ArrayList<Route>();
        int distance = -1, price = -1;
        boolean switch_central = false;
        try {
            JSONObject routesJSON = new JSONObject(routesString);
            distance = routesJSON.getInt("distance");
            price = routesJSON.getInt("price");
            switch_central = routesJSON.getBoolean("switch_central");

            JSONArray ja = routesJSON.getJSONArray("trips");
            for(int i = 0; i < ja.length(); i++){
                JSONObject trip = (JSONObject)ja.get(i);

                JSONArray stationsJSON = (JSONArray) trip.get("stations");
                JSONArray timesJSON = (JSONArray) trip.get("times");
                ArrayList<Integer> stations = new ArrayList<Integer>();
                ArrayList<String> times = new ArrayList<String>();

                if (stationsJSON != null) {
                    for (int j=0;j < stationsJSON.length(); j++){
                        stations.add((Integer)stationsJSON.get(j));
                    }
                }
                if (timesJSON != null) {
                    for (int j=0;j < timesJSON.length(); j++){

                        times.add((String)timesJSON.get(j));
                    }
                }

                Route r;
                if(switch_central){
                    int waitingTime = trip.getInt("waiting_time");
                    r = new Route(stations, times, -1, -1, waitingTime);
                }else{
                    r = new Route(stations, times, -1);
                }
                routes.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + getStationName(routes.get(0).stationTimes.get(0).station.toString()) + " TO " + getStationName(routes.get(0).stationTimes.get(routes.get(0).stationTimes.size() - 1).station.toString()));
        ((TextView)findViewById(R.id.text_route_distance)).setText(Integer.toString(distance));
        ((TextView)findViewById(R.id.text_route_price)).setText(Integer.toString(price));
        if(switch_central){
            TextView switchTV = ((TextView)findViewById(R.id.text_route_waiting_central));
            switchTV.setText("You need to to buy 2 tickets, because you need to switch in the Central Station. Waiting Time varies on the chosen route.");
            switchTV.setVisibility(View.VISIBLE);
        }


        ExpandableListView routesList = (ExpandableListView) findViewById(R.id.routes_listview);
        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(getApplicationContext(), routes);
        routesList.setAdapter(adapter);

    }

    private String getStationName(String id){

        SharedPreferences sp = getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }
}
