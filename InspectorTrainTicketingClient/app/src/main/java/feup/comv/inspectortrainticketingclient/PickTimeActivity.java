package feup.comv.inspectortrainticketingclient;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class PickTimeActivity extends AppCompatActivity implements OnApiRequestCompleted{

    private boolean timeSelected = false;
    private boolean dateSelected = false;
    private String fromStation, toStation;
    private String date, time;
    private String fromStationString, toStationString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);
        Bundle extras = getIntent().getExtras();
        String train = extras.getString("train");
        System.out.println(train);
        RadioGroup groupTimes = (RadioGroup) findViewById(R.id.radio_group_times);

        try {
            JSONObject trainJson = new JSONObject(train);
            fromStation = trainJson.getString("start_id");
            toStation = trainJson.getString("end_id");
            fromStationString = trainJson.getString("start");
            toStationString = trainJson.getString("end");

            JSONArray trips = trainJson.getJSONArray("trips");
            for (int i = 0; i < trips.length(); i++) {
                JSONObject trip = (JSONObject) trips.get(i);
                RadioButton b = new RadioButton(this);
                b.setText("Train "+ trip.getString("train") + " - " +trip.getString("start_time") + " to "+ trip.getString("end_time") );
                b.setTag(trip.toString());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                groupTimes.addView(b, lp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        groupTimes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(RadioGroup group, int checkedId) {

                  if (group.getCheckedRadioButtonId() == -1)
                      timeSelected = false;
                  else
                      timeSelected = true;

                  updateDownloadButton();

              }
          }
        );

        findViewById(R.id.edit_text_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(new DatePickerFragment.ListenerActivity() {
                    @Override
                    public void updateDate(String date) {
                        dateSelected = true;
                        TextView dateTextView = (TextView)findViewById(R.id.edit_text_date);
                        dateTextView.setText(date);
                        updateDownloadButton();
                    }
                });
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    public void downloadSpecificTrip(View view){
        ((Button) view).setEnabled(false);
        //--
        date = ((TextView)findViewById(R.id.edit_text_date)).getText().toString();
        RadioGroup groupTimes = (RadioGroup) findViewById(R.id.radio_group_times);
        int selectedId = groupTimes.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)groupTimes.findViewById(selectedId);

        try {
            JSONObject timeObj = new JSONObject(radioButton.getTag().toString());
            time = timeObj.getString("start_time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(radioButton.getTag());
        System.out.println(date);
        System.out.println(fromStation);
        System.out.println(toStation);


        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.DOWNLOAD_TICKET, null);
        String url = "downloadtickets?from=" + fromStation + "&to=" + toStation + "&date=" + date + "&time=" + time;
        request.execute(url);

    }

    private void updateDownloadButton() {
        Button downloadButton = (Button) findViewById(R.id.download_button);
        downloadButton.setEnabled(dateSelected && timeSelected);
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(result != null){
            if(requestCode == ApiRequest.requestCode.DOWNLOAD_TICKET){

                SharedPreferences sp = getSharedPreferences("routes", 0);
                SharedPreferences.Editor editor = sp.edit();

                Set<String> ticketSet = new HashSet<>();
                JSONArray tickets = null;
                try {
                    tickets = result.getJSONArray("tickets");

                    for(int i = 0; i < tickets.length(); i++){
                        JSONObject ticket = (JSONObject) tickets.get(i);
                        ticketSet.add(ticket.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                editor.putStringSet(new Route(Integer.parseInt(fromStation), Integer.parseInt(toStation), fromStationString, toStationString, date, time).getKey(), ticketSet);
                editor.commit();

                Intent routesActivity = new Intent(this, RoutesActivity.class);
                routesActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(routesActivity);
                finish();
            }
        }
    }
}
