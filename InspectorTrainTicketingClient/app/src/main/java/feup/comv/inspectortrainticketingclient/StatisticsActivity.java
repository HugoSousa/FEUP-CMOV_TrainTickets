package feup.comv.inspectortrainticketingclient;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        SharedPreferences spToken = getSharedPreferences("login", 0);
        String token = spToken.getString("token", null);

        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.STATISTICS, token);
        request.execute("statistics");

    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(result != null){
            if(requestCode == ApiRequest.requestCode.STATISTICS){
                if(! result.has("error")){
                    try {
                        int uploadedRoutes = result.getInt("uploaded_routes");
                        int uploadedTickets = result.getInt("uploaded_tickets");
                        int validatedTickets = result.getInt("validated_tickets");
                        int fraudulentTickets = result.getInt("fraudulent_tickets");
                        int noShows = result.getInt("no_shows");
                        int averageTicketsPerRoute;
                        if(uploadedRoutes == 0)
                            averageTicketsPerRoute = 0;
                        else
                            averageTicketsPerRoute = uploadedTickets/uploadedRoutes;

                        ((TextView)findViewById(R.id.text_uploaded_routes)).setText(Integer.toString(uploadedRoutes));
                        ((TextView)findViewById(R.id.text_total_tickets)).setText(Integer.toString(averageTicketsPerRoute));
                        ((TextView)findViewById(R.id.text_average_tickets_route)).setText(Integer.toString(uploadedTickets));
                        ((TextView)findViewById(R.id.text_validated_tickets)).setText(Integer.toString(validatedTickets));
                        ((TextView)findViewById(R.id.text_fraudulent_tickets)).setText(Integer.toString(fraudulentTickets));
                        ((TextView)findViewById(R.id.text_no_shows)).setText(Integer.toString(noShows));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
