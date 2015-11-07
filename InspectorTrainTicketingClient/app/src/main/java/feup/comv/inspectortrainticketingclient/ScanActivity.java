package feup.comv.inspectortrainticketingclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import android.util.Base64;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView scannerView;
    private String routeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(scannerView);              // Set the scanner view as the content view

        Bundle extras = getIntent().getExtras();
        routeString = (String)extras.get("route");
        Route route = Route.convertKeyToTicket(routeString);

        String result_string = "{\"data\":{\"code\":\"d5554dd3-892a-4bc3-824e-aea250eb53c6\"},\"signature\":\"Wx/s30Ejtsq7lq+KVirGYIvvk3F2yDuKURILeFJ93PBb+VQ4iFSlX6sLNzhmmQ==\"}";
        validateResult(result_string);

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
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        validateResult(result.getContents());

    }

    public void validateResult(String result) {
        Log.d("cenas", "Reading result:");
        Log.d("cenas", result);
        JSONObject ticketObj = null;
        try {
            ticketObj = new JSONObject(result);
            JSONObject dataObj = ticketObj.getJSONObject("data");
            String code = dataObj.getString("code");
            String signature = ticketObj.getString("signature");

            SharedPreferences sp2 = getSharedPreferences("login", 0);
            SharedPreferences sp = getSharedPreferences("routes", 0);
            String public_string = sp2.getString("pub", null);
            Log.d("cenas", "Public key:");
            Log.d("cenas", public_string);

            HashSet<String> tickets = (HashSet<String>) sp.getStringSet(routeString, null);
            Boolean found = false;
            for (String ticket: tickets) {

                JSONObject tObj = new JSONObject(ticket);
                String ticketCode =  tObj.getString("uuid");
                if (ticketCode.equals(code)) {
                    Log.d("cenas", "Local match:");
                    Log.d("cenas", ticket);
                    //String tSig = tObj.getString("signature");
                    String tRouteId = tObj.getString("route_id");
                    String userId = tObj.getString("user_id");
                    String dateString = tObj.getString("route_date");

                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = dt.parse(dateString);

                    SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String properDate = dt1.format(date);

                    String mask = code + " " + tRouteId + " " + userId + " " + properDate;
                    Log.d("cenas",mask);
                    Log.d("cenas",signature);
                    //TODO check with mask/signature

                    //remove comment lines from public_key
                    String[] publicKeySplit = public_string.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < publicKeySplit.length - 1; i++) {
                        if(! publicKeySplit[i].startsWith("-----"))
                            sb.append(publicKeySplit[i]);
                    }
                    String publicKeyString = sb.toString();

                    //PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString.getBytes(), Base64.DEFAULT)));
                    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString.getBytes("utf-8"), Base64.DEFAULT)));
                    //http://stackoverflow.com/questions/11532989/android-decrypt-rsa-text-using-a-public-key-stored-in-a-file
                    
                    Signature sg = Signature.getInstance("SHA1WithRSA");          // for signing with the stated algorithm


                    sg.initVerify(publicKey);
                    sg.update(mask.getBytes());
                    boolean r = sg.verify(Base64.decode(signature.getBytes(), Base64.DEFAULT));
                    Log.d("cenas", "Resultado "+ r);
                    found = true;
                    break;
                }
            }

        } catch (JSONException e) {
            Log.d("cenas","erro",e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch(Exception e){
            //bad base 64 encoding for example
            Log.d("cenas", "erro", e);
        }

    }
}

