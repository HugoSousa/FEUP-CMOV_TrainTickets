package feup.comv.inspectortrainticketingclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;

public class RouteActivity extends AppCompatActivity {

    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle extras = getIntent().getExtras();
        String routeKey = (String)extras.get("route_key");

        SharedPreferences sp = getSharedPreferences("routes", 0);
        Map<String,?> keys = sp.getAll();

        HashSet<String> tickets = null;
        for(Map.Entry<String,?> entry : keys.entrySet()){

            if(entry.getKey().equals(routeKey)) {
                tickets = (HashSet<String>) entry.getValue();
                break;
            }
        }

        int validated = 0;
        for(String ticketString: tickets){
            JSONObject ticketObj = null;
            try {
                ticketObj = new JSONObject(ticketString);
                System.out.println(ticketObj);
                JSONArray validatedArray = ticketObj.getJSONArray("is_validated");
                if(validatedArray.getInt(0) == 1)
                    validated++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        route = Route.convertKeyToTicket(routeKey);

        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + route.fromString + " TO " + route.toString);
        ((TextView)findViewById(R.id.text_route_date)).setText(route.date + " " + route.time);
        ((TextView)findViewById(R.id.text_total_tickets)).setText(Integer.toString(tickets.size()));
        ((TextView)findViewById(R.id.text_validated_tickets)).setText(Integer.toString(validated));
        //System.out.println(route);
        //System.out.println("TICKETS: \n" + tickets);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    public void scanRoute(View view){
        //pass the route data to the activity (need route_id...?)

        Intent scanActivity = new Intent(this, ScanActivity.class);
        scanActivity.putExtra("route", route.getKey());
        startActivity(scanActivity);

    }
}
