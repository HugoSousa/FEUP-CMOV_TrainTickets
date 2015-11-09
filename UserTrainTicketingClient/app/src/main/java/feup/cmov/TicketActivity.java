package feup.cmov;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class TicketActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback  {
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        SharedPreferences sp = getSharedPreferences("login", 0);

        Bundle extras = getIntent().getExtras();
        String code = (String)extras.get("code");
        String route = (String)extras.get("route");
        String date = (String)extras.get("date");
        String user = sp.getString("id", null);
        String signature = (String)extras.get("signature");

        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("code", code);
            //dataObj.put("route", route);
            //dataObj.put("user", user);
            //dataObj.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("data", dataObj);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView imageView = (ImageView) findViewById(R.id.ticket_qr_view);

        data = obj.toString();
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(data, null,
                "TEXT_TYPE", BarcodeFormat.QR_CODE.toString(), data.length());

        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        String from = (String)extras.get("fromStation");
        String to = (String)extras.get("toStation");
        String distance = (String)extras.get("distance");
        String price = (String)extras.get("price");

        ((TextView)findViewById(R.id.text_route_from_to)).setText("FROM " + from + " TO " + to);
        ((TextView)findViewById(R.id.text_route_date)).setText(date.replace(" ", "\n"));
        ((TextView)findViewById(R.id.text_route_distance)).setText(distance);
        ((TextView)findViewById(R.id.text_route_price)).setText(price);
    }

    private String getStationName(String id){

        SharedPreferences sp = this.getSharedPreferences("stations", 0);

        return sp.getString(id, null);
    }

    public void sendNfc(View v) {
        NfcAdapter mNfcAdapter;
        String tag = "application/nfc.feup.cmov.tickettype";
        byte[] message;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "NFC is not available on this device.", Toast.LENGTH_LONG).show();
            return;
        } else {
            message = data.getBytes();
            NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(tag, message) });
            mNfcAdapter.setNdefPushMessage(msg, this);
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
            Toast.makeText(getApplicationContext(), "Ticket sending attempted...", Toast.LENGTH_LONG).show();


        }
    }


    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("ISO-8859-1"));
        return new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "Ticket sent.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
