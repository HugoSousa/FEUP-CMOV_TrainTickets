package feup.cmov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        SharedPreferences sp = getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = sp.edit();
        //TODO remove: fake data to check if user is logged in
        String fakeTestToken = "abcd";
        editor.putString("token", fakeTestToken);
        editor.commit();

        finish();

        SharedPreferences spRoute = this.getSharedPreferences("route", 0);

        String token = spRoute.getString("route", null);
        if(token != null){

            //values to be used to retrieve the route info
            String routeFrom = spRoute.getString("route_from", null);
            String routeTo = spRoute.getString("route_to", null);
            String routeTime = spRoute.getString("route_time", null);
            String routeDate = spRoute.getString("route_date", null);

            /*
            //redirect to purchase of ticket
            Intent purchaseIntent = new Intent(this, TicketPurchaseActivity.class);

            purchaseIntent.putExtra("route_from", spRoute.getString("route_from", null));
            purchaseIntent.putExtra("route_to", spRoute.getString("route_to", null));
            purchaseIntent.putExtra("route_time", spRoute.getString("route_time", null));


            SharedPreferences.Editor editorRoute = spRoute.edit();
            editorRoute.putString("route", null);
            editorRoute.putString("route_from", null);
            editorRoute.putString("route_to", null);
            editorRoute.putString("route_time", null);
            editorRoute.commit();
            */
            System.out.println("Redirect to TicketPurchaseActivity");
            finish();
            ApiRequest request = new ApiRequest(this.getApplicationContext(), TicketPurchaseActivity.class); //will redirect to purchase of ticket with info of the route
            request.execute("teste");
            //startActivity(purchaseIntent);
        }
    }
}
