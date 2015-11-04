package feup.comv.inspectortrainticketingclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class RoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ArrayList<Route> routes = new ArrayList<>();
        //get downloaded routes from local storage and add to the listview
        SharedPreferences sp = getSharedPreferences("routes", 0);
        Map<String,?> keys = sp.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){

            Route t = Route.convertKeyToTicket(entry.getKey());
            //System.out.println("oi");
            System.out.println(t.getKey());
            routes.add(t);
        }

        ListView routesListView = (ListView)findViewById(R.id.list_view_routes);
        ArrayAdapter adapter = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, routes);
        //TicketAdapter adapter = new TicketAdapter(this, tickets);
        routesListView.setAdapter(adapter);

        routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CLICKED " + routes.get(position).getKey());
                String routeKey = routes.get(position).getKey();
                //get tickets from local storage (with the route key)

                Intent routeActivity = new Intent(RoutesActivity.this, RouteActivity.class);
                routeActivity.putExtra("route_key", routeKey);
                startActivity(routeActivity);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void downloadTrip(View view){
        Intent mainRoutesActivity = new Intent(this, MainRoutesActivity.class);
        startActivity(mainRoutesActivity);
    }

    public void showStatistics(View view){
        Intent statisticsActivity = new Intent(this, StatisticsActivity.class);
        startActivity(statisticsActivity);
    }
}
