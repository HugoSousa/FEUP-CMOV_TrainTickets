package feup.comv.inspectortrainticketingclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainRoutesActivity extends AppCompatActivity implements OnApiRequestCompleted{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);

        //get simpletrains
        ApiRequest request = new ApiRequest(ApiRequest.GET, this, ApiRequest.requestCode.TRIPS, null);
        request.execute("simpletrains");
    }

    @Override
    public void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode) {
        if(result != null){
            if(requestCode == ApiRequest.requestCode.TRIPS){
                try {
                    JSONArray trains = result.getJSONArray("trains");


                    for(int i = 0; i < trains.length(); i++){
                        JSONObject train = (JSONObject)trains.get(i);
                        String start = train.getString("start");
                        String end = train.getString("end");

                        Button b = new Button(this);
                        b.setText("FROM " + start + " TO " + end);
                        b.setTag(train);

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent pickTime = new Intent(MainRoutesActivity.this, PickTimeActivity.class);
                                pickTime.putExtra("train", v.getTag().toString());

                                startActivity(pickTime);
                                //pass shit to activity
                            }
                        });

                        LinearLayout parent = (LinearLayout)findViewById(R.id.ll_main_routes);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parent.addView(b, lp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
