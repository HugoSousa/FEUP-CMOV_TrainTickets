package feup.comv.inspectortrainticketingclient;

/**
 * Created by Hugo on 03/11/2015.
 */
public class Route {
    public int from;
    public int to;
    public String date;
    public String time;

    Route(int from, int to, String date, String time){
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
    }

    public String getKey(){
        return from + " " + to + " " + date + " " + time;
    }


    public static Route convertKeyToTicket(String key){
        String[] split = key.split(" ");

        return new Route(Integer.parseInt(split[0]), Integer.parseInt(split[1]), split[2], split[3]);
    }
}
