package feup.cmov;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        Bundle extras = getIntent().getExtras();
        String ticketCode = (String)extras.get("code");
        String ticketFromStation = (String)extras.get("fromStation");
        String ticketToStation = (String)extras.get("toStation");
        String ticketDate = (String)extras.get("date");

        System.out.println(ticketCode + " " + ticketFromStation + " " + ticketToStation + " " + ticketDate);
    }
}
