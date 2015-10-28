package feup.cmov;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText cc_validity = (EditText)findViewById(R.id.register_cc_validity);
        cc_validity.setOnClickListener(new View.OnClickListener() {

            Calendar today = Calendar.getInstance();
            private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    DecimalFormat df = new DecimalFormat("00");
                    ((EditText) findViewById(R.id.register_cc_validity)).setText(df.format(selectedMonth) + "/" + selectedYear);
                }
            };

            DatePickerDialog dlg = new DatePickerDialog(RegisterActivity.this, datePickerListener,
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    int day = getContext().getResources()
                            .getIdentifier("android:id/day", null, null);
                    if (day != 0) {
                        View dayPicker = findViewById(day);
                        if (dayPicker != null) {
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

        TextView ccNumberView = (TextView)findViewById(R.id.register_cc_number);
        ccNumberView.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }
            }
        });

        Spinner spinner_cc_type = (Spinner) findViewById(R.id.spinner_cc_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.credit_card_types, R.layout.spinner_centered_textview);
        spinner_cc_type.setAdapter(adapter);

    }

    public void register(View view){
        EditText nameTV = (EditText)findViewById(R.id.register_name_input);
        EditText idTV = (EditText)findViewById(R.id.register_id_input);
        Spinner spinnerCCType = (Spinner)findViewById(R.id.spinner_cc_type);
        EditText ccNumberTV = (EditText)findViewById(R.id.register_cc_number);
        EditText ccValidityTV = (EditText)findViewById(R.id.register_cc_validity);
        EditText usernameTV = (EditText)findViewById(R.id.register_username);
        EditText passwordTV = (EditText)findViewById(R.id.register_password);
        EditText passwordConfirmTV = (EditText)findViewById(R.id.register_confirm_password);

        String name = nameTV.getText().toString().trim();
        String idNumber = idTV.getText().toString().trim();
        String ccType = spinnerCCType.getSelectedItem().toString().trim();
        String ccNumber = ccNumberTV.getText().toString().trim().replaceAll("\\s","");
        String ccValidity = ccValidityTV.getText().toString().trim();
        String username = usernameTV.getText().toString().trim();
        String password = passwordTV.getText().toString().trim();
        String passwordConfirm = passwordConfirmTV.getText().toString().trim();

        boolean missing = false;

        if(name.isEmpty()){
            nameTV.setError("Missing Value");
            missing = true;
        }
        if(idNumber.isEmpty()) {
            idTV.setError("Missing Value");
            missing = true;
        }
        if(ccNumber.isEmpty()) {
            ccNumberTV.setError("Missing Value");
            missing = true;
        }
        if(ccValidity.isEmpty()) {
            ccValidityTV.setError("Missing Value");
            missing = true;
        }
        if(username.isEmpty()) {
            usernameTV.setError("Missing Value");
            missing = true;
        }
        if(password.isEmpty()) {
            passwordTV.setError("Missing Value");
            missing = true;
        }
        if(passwordConfirm.isEmpty()) {
            passwordConfirmTV.setError("Missing Value");
            missing = true;
        }

        if(!missing){
            //other necessary validations

            if(! password.equals(passwordConfirm)){
                passwordTV.setText("");
                passwordConfirmTV.setText("");
                passwordTV.setError("Password and Confirmation don't match.");
            }else if(ccNumber.length() < 15 || ccNumber.length() > 19){
                ccNumberTV.setText("Invalid credit card number. Should have between 15 and 19 digits.");
            }else{
                //send register request to server
                //ApiRequest request = new ApiRequest(this, LoginActivity.class, null, ApiRequest.POST, null);
                ApiRequest request = new ApiRequest(ApiRequest.POST, this, ApiRequest.requestCode.REGISTER);
                request.execute("register", "");
                finish();
            }
        }
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(requestCode == ApiRequest.requestCode.REGISTER){
            try {
                JSONObject res = result.getJSONObject("result");
                if(res.has("error")){
                    String error = res.getString("error");
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }else{
                    //successful register
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
