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

    public static Ticket JSONtoTicket(JSONObject obj, String code){

        try {
            String date = (String)obj.get("date");
            int price = (int)obj.get("price");
            String from = (String)obj.get("from");
            String to = (String)obj.get("to");
            String signature = (String)obj.get("signature");
            int distance = (int)obj.get("distance");
            int route = (int)obj.get("route");
            return new Ticket(code, date, price, from, to, signature, distance, route);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
