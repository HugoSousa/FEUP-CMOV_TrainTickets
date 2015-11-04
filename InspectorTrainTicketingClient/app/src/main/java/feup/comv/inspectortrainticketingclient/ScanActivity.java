package feup.comv.inspectortrainticketingclient;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        //setContentView(scannerView);              // Set the scanner view as the content view

        ViewGroup layout = (ViewGroup) findViewById(R.id.ll_teste);
        scannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(scannerView);

        Bundle extras = getIntent().getExtras();
        String routeString = (String)extras.get("route");
        Route route = Route.convertKeyToTicket(routeString);


        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + route.fromString + " TO " + route.toString);
        ((TextView)findViewById(R.id.text_route_date)).setText(route.date + " " + route.time);

    }


    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {

    }
}
