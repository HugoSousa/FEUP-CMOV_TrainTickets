package feup.cmov;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class InitialActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        if(isNetworkConnected()) {
            redirectIfServerUp();
        }else{
            Toast.makeText(this, "You have no internet connection. You're now in offline mode.", Toast.LENGTH_LONG).show();

            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if(isNetworkConnected()) {
                        redirectIfServerUp();
                        unregisterReceiver(this);
                    }
                }
            }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }

        //if server is down or no internet
        SharedPreferences sp = this.getSharedPreferences("login", 0);

        String token = sp.getString("token", null);
        if(token == null){
            //user not logged in - Switch to Online Mode
            TextView tv = (TextView)findViewById(R.id.text_info_connectivity);
            tv.setText(R.string.text_offline_not_logged_in);
        }
        else{
            //user logged in - show tickets and Switch to Online Mode
            TextView tv = (TextView)findViewById(R.id.text_info_connectivity);
            tv.setText(R.string.text_offline_logged_in);
            findViewById(R.id.button_show_tickets).setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    private void redirectIfServerUp(){

        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.PING);
        request.execute("ping");
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {

        if(requestCode == ApiRequest.requestCode.PING) {

            if (result != null) {
                //server is up
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Server is down. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
