package feup.cmov;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Hugo on 19/10/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    ListenerActivity listener;

    public interface ListenerActivity{
        public void updateDate(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listener = (ListenerActivity) getActivity();

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dp = new DatePickerDialog(getActivity(), this, year, month, day);
        dp.getDatePicker().setMinDate(c.getTimeInMillis());
        return dp;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (view.isShown()) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(c.getTime());
            if (listener != null) {
                listener.updateDate(formattedDate);
            }
        }
        // Do something with the date chosen by the user
    }
}
