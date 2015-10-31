package feup.cmov;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hugo on 21/10/2015.
 */
public class Ticket {
    public String code;
    public String date;
    public int price;
    public String fromStation;
    public String toStation;
    public String signature;
    public int distance;
    public int route;
    public boolean isLoadedLocal;

    public Ticket(String code, String date, int price, String fromStation, String toStation, String signature, int distance, int route){
        this.code = code;
        this.date = date;
        this.price = price;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.signature = signature;
        this.distance = distance;
        this.route = route;
    }

    public String toString(){
        return fromStation + " - " + toStation + "\n" + date;
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("date", date);
            obj.put("price", price);
            obj.put("from", fromStation);
            obj.put("to", toStation);
            obj.put("signature", signature);
            obj.put("distance", distance);
            obj.put("route", route);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
