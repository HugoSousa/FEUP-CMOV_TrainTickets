package feup.cmov;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnApiRequestCompleted {

    private ArrayList<Station> stationsFromList;
    private ArrayList<Station> stationsToList;
    private ArrayList<Station> stationsCopy;

    private ArrayAdapter<Station> adapter_from;
    private ArrayAdapter<Station> adapter_to;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        invalidateOptionsMenu();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stationsFromList = new ArrayList<Station>();

        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.STATIONS, null);
        request.execute("stations");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //update the options menu when activity is restored from login activity
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SharedPreferences sp = this.getSharedPreferences("login", 0);

        String token = sp.getString("token", null);
        if(token == null){
            //user not logged in
            menu.findItem(R.id.action_tickets).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(false);
        }
        else{
            //user logged in
            menu.findItem(R.id.action_register).setVisible(false);
            menu.findItem(R.id.action_login).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        }else if(id == R.id.action_register){
            Intent loginIntent = new Intent(this, RegisterActivity.class);
            startActivity(loginIntent);
            return true;
        }else if(id == R.id.action_logout){
            SharedPreferences sp = this.getSharedPreferences("login", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("token", null);
            editor.commit();

            SharedPreferences spRoute = this.getSharedPreferences("route", 0);
            SharedPreferences.Editor editorRoute = spRoute.edit();
            editorRoute.putString("route", null);
            editorRoute.putString("route_from", null);
            editorRoute.putString("route_to", null);
            editorRoute.putString("route_time", null);
            editorRoute.putString("route_date", null);
            editorRoute.commit();

            invalidateOptionsMenu();

        }else if(id == R.id.action_tickets){
            //ApiRequest request = new ApiRequest(this, TicketsActivity.class, null, ApiRequest.GET, null);
            ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.TICKETS, null);
            request.execute("teste");
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(new DatePickerFragment.ListenerActivity() {
            @Override
            public void updateDate(String date) {
                TextView dateTextView = (TextView)findViewById(R.id.date_text);
                dateTextView.setText(date);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /*
    @Override
    public void updateDate(String date) {
        System.out.println("OI");
        TextView dateTextView = (TextView)findViewById(R.id.date_text);
        dateTextView.setText(date);
    }
    */

    public void searchRoutes(View v){

        if(isNetworkConnected()){

            String date = ((TextView)findViewById(R.id.date_text)).getText().toString();

            if(date.equals(getString(R.string.label_date))){
                ((TextView)findViewById(R.id.text_warning_search)).setText("Please insert a date.");
                return;
            }else{
                ((TextView)findViewById(R.id.text_warning_search)).setText("");
            }


            Bundle bundle = new Bundle();
            bundle.putString("date", date);

            ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.ROUTES, null);
            //get id from and to
            Spinner spinner_from = (Spinner) findViewById(R.id.spinner_from);
            int fromPosition = spinner_from.getSelectedItemPosition();
            int fromId = stationsFromList.get(fromPosition).id;
            Spinner spinner_to = (Spinner) findViewById(R.id.spinner_to);
            int toPosition = spinner_to.getSelectedItemPosition();
            int toId = stationsToList.get(toPosition).id;

            request.execute("routes?from=" + fromId + "&to=" + toId + "&date=" + date);
        }
        else {
            System.out.println("No internet connection");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm.getActiveNetworkInfo() != null);
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {

        if(result != null){
            if(requestCode == ApiRequest.requestCode.ROUTES){
                //redirect to routes
                Intent intent = new Intent(this, RoutesListActivity.class);
                intent.putExtra("data", result.toString());
                intent.putExtra("date", ((TextView) findViewById(R.id.date_text)).getText().toString());
                startActivity(intent);

            }else if(requestCode == ApiRequest.requestCode.STATIONS){

                try {
                    JSONArray stations = (JSONArray)result.get("stations");

                    SharedPreferences sp = this.getSharedPreferences("stations", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    for(int i = 0; i < stations.length(); i++){
                        JSONObject station = (JSONObject)stations.get(i);
                        stationsFromList.add(new Station((String) station.get("name"), (int) station.get("id")));
                        editor.putString(station.get("id").toString(), (String) station.get("name"));
                    }

                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                stationsToList = new ArrayList<Station>(stationsFromList);
                stationsCopy = new ArrayList<Station>(stationsToList);

                Spinner spinner_from = (Spinner) findViewById(R.id.spinner_from);
                adapter_from = new ArrayAdapter<Station>(this, R.layout.spinner_centered_textview, stationsFromList); // initialize the adapter
                adapter_from.setDropDownViewResource(R.layout.spinner_centered_textview);
                spinner_from.setAdapter(adapter_from);

                final Spinner spinner_to = (Spinner) findViewById(R.id.spinner_to);
                adapter_to = new ArrayAdapter<Station>(this, R.layout.spinner_centered_textview, stationsToList); // initialize the adapter
                adapter_to.setDropDownViewResource(R.layout.spinner_centered_textview);
                spinner_to.setAdapter(adapter_to);

                stationsFromList.remove(1);
                adapter_to.notifyDataSetChanged();
                adapter_from.notifyDataSetChanged();

                spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        stationsToList.clear();
                        //remove the same element from the list
                        for (int i = 0; i < stationsCopy.size(); i++) {
                            if (!stationsCopy.get(i).name.equals(stationsFromList.get(position).name)) {
                                //System.out.println("ADDED " + stationsCopy.get(i));
                                stationsToList.add(stationsCopy.get(i));
                            }
                        }

                        //adapter_from.notifyDataSetChanged();
                        //adapter_to.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("SPINNER TO CLICKED");

                        stationsFromList.clear();
                        //remove the same element from the list
                        for (int i = 0; i < stationsCopy.size(); i++) {
                            if (!stationsCopy.get(i).name.equals(stationsToList.get(position).name)) {
                                stationsFromList.add(stationsCopy.get(i));
                            }
                        }
                        //adapter_from.notifyDataSetChanged();
                        //adapter_to.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else if(requestCode == ApiRequest.requestCode.TICKETS){
                Intent intent = new Intent(this, TicketsActivity.class);
                intent.putExtra("data", result.toString());
                startActivity(intent);
            }
        }
    }
}


