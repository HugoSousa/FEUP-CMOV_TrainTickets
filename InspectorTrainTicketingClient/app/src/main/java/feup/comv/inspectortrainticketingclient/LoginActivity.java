package feup.comv.inspectortrainticketingclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View view) {

        String username = ((EditText) findViewById(R.id.edit_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("email", username);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest request = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.LOGIN_EMPLOYEE, null);
        request.execute("loginemployee", body.toString());

    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(result != null) {

            if(requestCode == ApiRequest.requestCode.LOGIN_EMPLOYEE){

                if (!result.has("error")) {

                    SharedPreferences sp = getSharedPreferences("login", 0);
                    SharedPreferences.Editor editor = sp.edit();


                    try {
                        String token = result.getString("token");
                        JSONObject user = result.getJSONObject("user");
                        String email = user.getString("email");

                        editor.putString("token", token);
                        editor.putString("email", email);
                        editor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("data", result.toString());
                    startActivity(intent);

                    finish();


                } else {
                    try {
                        String error = result.getString("error");
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else System.out.println("unknown callback");
        }else{
            Toast.makeText(this, "Can't retrieve your search. Please check if you you're connected to Internet.", Toast.LENGTH_LONG).show();
        }
    }
}
