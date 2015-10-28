package feup.cmov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements OnApiRequestCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        String username = ((EditText) findViewById(R.id.edit_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest request = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.LOGIN);
        request.execute("login", body.toString());

    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {

        if(result != null) {

            if(requestCode == ApiRequest.requestCode.LOGIN){

                if (!result.has("error")) {

                    SharedPreferences sp = getSharedPreferences("login", 0);
                    SharedPreferences.Editor editor = sp.edit();

                    String token = null;
                    try {
                        token = result.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    editor.putString("token", token);
                    editor.commit();

                    finish();

                    SharedPreferences spRoute = this.getSharedPreferences("route", 0);
                    String route = spRoute.getString("route", null);

                    if (route != null) {

                        //values to be used to retrieve the route info
                        String routeFrom = spRoute.getString("route_from", null);
                        String routeTo = spRoute.getString("route_to", null);
                        String routeTime = spRoute.getString("route_time", null);
                        String routeDate = spRoute.getString("route_date", null);

                        System.out.println("Redirect to TicketPurchaseActivity");
                        finish();
                        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.ROUTE); //will redirect to purchase of ticket with info of the route
                        request.execute("route?from=" + routeFrom + "&to=" + routeTo + "&time=" + routeTime + "&date=" + routeDate);

                    }
                } else {
                    try {
                        String error = result.getString("error");
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else if(requestCode == ApiRequest.requestCode.ROUTE){
                //REDIRECT TO TICKET VIEW AND CLEAN SHARED PREFERENCES
                SharedPreferences spRoute = this.getSharedPreferences("route", 0);
                SharedPreferences.Editor editorRoute = spRoute.edit();
                editorRoute.putString("route", null);
                editorRoute.putString("route_from", null);
                editorRoute.putString("route_to", null);
                editorRoute.putString("route_time", null);
                editorRoute.putString("route_date", null);
                editorRoute.commit();

                Intent intent = new Intent(this, TicketPurchaseActivity.class);
                intent.putExtra("data", result.toString());
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, "Can't retrieve your search. Please check if you you're connected to Internet.", Toast.LENGTH_LONG).show();
        }
    }
}
