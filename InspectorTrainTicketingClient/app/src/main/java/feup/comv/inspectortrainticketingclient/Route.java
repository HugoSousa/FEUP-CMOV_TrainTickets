package feup.comv.inspectortrainticketingclient;

/**
 * Created by Hugo on 03/11/2015.
 */
public class Route {
    public int from;
    public int to;
    public String fromString;
    public String toString;
    public String date;
    public String time;

    Route(int from, int to, String fromString, String toString, String date, String time){
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.fromString = fromString;
        this.toString = toString;
    }

    public String getKey(){
        return from + "/" + fromString + " " + to + "/" + toString + " " + date + " " + time;
    }

    public String toString(){
        return "FROM " + fromString + " TO " + toString + "\n" + date + " " + time;
    }


    public static Route convertKeyToTicket(String key){
        String[] split = key.split(" ");
        String[] fromSplit = split[0].split("/");
        String[] toSplit = split[1].split("/");

        return new Route(Integer.parseInt(fromSplit[0]), Integer.parseInt(toSplit[0]), fromSplit[1], toSplit[1], split[2], split[3]);
    }
}
