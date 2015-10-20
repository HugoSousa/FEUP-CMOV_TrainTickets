package feup.cmov;

import android.app.DialogFragment;
import android.content.Context;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.ListenerActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO get from db instead of hardcoded
        final ArrayList<Station> stationsFromList = new ArrayList<Station>();

        stationsFromList.add(new Station("A", 1));
        stationsFromList.add(new Station("A/CENTRAL", 2));
        stationsFromList.add(new Station("CENTRAL", 3));
        stationsFromList.add(new Station("B", 4));
        stationsFromList.add(new Station("B/CENTRAL", 5));
        stationsFromList.add(new Station("C", 6));
        stationsFromList.add(new Station("C/CENTRAL", 7));

        final ArrayList<Station> stationsToList = new ArrayList<Station>(stationsFromList);
        final ArrayList<Station> stationsCopy = new ArrayList<Station>(stationsToList);

        Spinner spinner_from = (Spinner) findViewById(R.id.spinner_from);
        ArrayAdapter<Station> adapter_from = new ArrayAdapter<Station>(this, android.R.layout.simple_spinner_item, stationsFromList); // initialize the adapter
        adapter_from.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_from.setAdapter(adapter_from);

        Spinner spinner_to = (Spinner) findViewById(R.id.spinner_to);
        ArrayAdapter<Station> adapter_to = new ArrayAdapter<Station>(this, android.R.layout.simple_spinner_item, stationsToList); // initialize the adapter
        adapter_to.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_to.setAdapter(adapter_to);
        spinner_to.setSelection(1);

        spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CLICKED");
                stationsToList.clear();
                //remove the same element from the list
                for(int i = 0; i < stationsCopy.size(); i++){
                    if(! stationsCopy.get(i).name.equals(stationsFromList.get(position).name)){
                        stationsToList.add(stationsCopy.get(i));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stationsFromList.clear();
                //remove the same element from the list
                for(int i = 0; i < stationsCopy.size(); i++){
                    if(! stationsCopy.get(i).name.equals(stationsToList.get(position).name)){
                        stationsFromList.add(stationsCopy.get(i));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    @Override
    public void updateDate(String date) {
        System.out.println("OI");
        TextView dateTextView = (TextView)findViewById(R.id.date_text);
        dateTextView.setText(date);
    }

    public void searchRoutes(View v){

        if(isNetworkConnected()){
            ApiRequest request = new ApiRequest(getApplicationContext());
            request.execute("teste"); //TODO check for connection timeout and send a warning to the user
            System.out.println("After request");
        }
        else{
            System.out.println("No internet connection");
        }
        System.out.println("search");

        //request to /api/routes?from=X&to=Y
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm.getActiveNetworkInfo() != null);
    }
}


