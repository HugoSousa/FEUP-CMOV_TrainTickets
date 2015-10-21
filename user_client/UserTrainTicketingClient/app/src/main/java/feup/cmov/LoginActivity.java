package feup.cmov;

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

        System.out.println("LOGGED IN: SET SHAREDPREFERENCES");

        finish();
    }
}
