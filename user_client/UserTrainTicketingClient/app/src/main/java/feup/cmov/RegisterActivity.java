package feup.cmov;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText cc_validity = (EditText)findViewById(R.id.register_cc_validity);
        cc_validity.setOnClickListener(new View.OnClickListener() {

            Calendar today = Calendar.getInstance();
            private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                }
            };

            DatePickerDialog dlg = new DatePickerDialog(RegisterActivity.this, datePickerListener,
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH))
            {
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    int day = getContext().getResources()
                            .getIdentifier("android:id/day", null, null);
                    if(day != 0){
                        View dayPicker = findViewById(day);
                        if(dayPicker != null){
                            dayPicker.setVisibility(View.GONE);
                        }
                    }

                    getDatePicker().setCalendarViewShown(false);
                }


            };

            @Override
            public void onClick(View v) {
                System.out.println("SHOW dialog");
                dlg.show();
            }
        });

    }
}
