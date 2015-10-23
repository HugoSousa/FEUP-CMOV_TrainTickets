package feup.cmov;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TicketPurchaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase);

        //data of the route is in extras "data"
        Bundle extras = getIntent().getExtras();
        String routeString = (String)extras.get("data");


        //TODO fill fields with data of route
        /*
        ArrayList<Route> routes = new ArrayList<Route>();
        try {
            JSONObject routesJSON = new JSONObject(routesString);
            JSONArray ja = (JSONArray)routesJSON.getJSONArray("trips");
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

                Route r = new Route(stations,times, 1);
                routes.add(r);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        //if waiting central, set button text "BUY TICKETS" and show "Waiting Time"
        //otherwise, set button text "BUY TICKET" and hide "Waiting Time"
        //do only 1 request to server, even if it's 2 separate tickets
    }
}
