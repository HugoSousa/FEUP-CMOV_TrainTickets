package feup.cmov;

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

    public Ticket(String code, String date, int price, String fromStation, String toStation, String signature, int distance){
        this.code = code;
        this.date = date;
        this.price = price;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.signature = signature;
        this.distance = distance;
    }

    public String toString(){
        return fromStation + " - " + toStation + "\n" + date;
    }
}
