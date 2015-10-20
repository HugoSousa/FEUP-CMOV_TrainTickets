package feup.cmov;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class RoutesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        Bundle extras = getIntent().getExtras();
        String routesString = (String)extras.get("routes");
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
        
        ExpandableListView routesList = (ExpandableListView) findViewById(R.id.routes_listview);
        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(getApplicationContext(), routes);
        routesList.setAdapter(adapter);

    }
}
