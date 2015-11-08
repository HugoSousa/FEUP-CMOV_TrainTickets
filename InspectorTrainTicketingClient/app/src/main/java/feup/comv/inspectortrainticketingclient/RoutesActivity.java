package feup.comv.inspectortrainticketingclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class RoutesActivity extends AppCompatActivity {

    private boolean enableStatistics;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(br != null) {
            unregisterReceiver(br);
            br = null;
        }
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
            if(entry.getValue() != null)
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

        invalidateOptionsMenu(); //to update the menu variable

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (isNetworkConnected()) {
                    findViewById(R.id.btn_download_trip).setEnabled(true);
                    enableStatistics = true;
                    invalidateOptionsMenu();
                }else{
                    findViewById(R.id.btn_download_trip).setEnabled(false);
                    enableStatistics = false;
                    invalidateOptionsMenu();
                }
            }
        };

        registerReceiver(br, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(enableStatistics)
            menu.findItem(R.id.action_statistics).setEnabled(true);
        else
            menu.findItem(R.id.action_statistics).setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_statistics)
            showStatistics();
        else if(id == R.id.action_logout)
            logout();

        return super.onOptionsItemSelected(item);
    }

    public void downloadTrip(View view){
        Intent mainRoutesActivity = new Intent(this, MainRoutesActivity.class);
        startActivity(mainRoutesActivity);
    }

    private void showStatistics(){
        Intent statisticsActivity = new Intent(this, StatisticsActivity.class);
        startActivity(statisticsActivity);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    private void logout(){
        SharedPreferences sp = getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", null);
        editor.putString("email", null);
        editor.putString("pub", null);
        editor.commit();

        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivity);
        finish();
    }
}
