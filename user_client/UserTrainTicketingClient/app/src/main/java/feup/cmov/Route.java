package feup.cmov;

import java.util.ArrayList;

/**
 * Created by Hugo on 19/10/2015.
 */
public class Route {
    public ArrayList<Integer> stations;
    public ArrayList<String> times;
    public int train1;
    public int train2;
    public int waitingTime;

    public Route(ArrayList<Integer> stations, ArrayList<String> times, int train){
        this.stations = stations;
        this.times = times;
        this.train1 = train;
    }

    public Route(ArrayList<Integer> stations, ArrayList<String> times, int train1, int train2, int waitingTime){
        this.stations = stations;
        this.times = times;
        this.train1 = train1;
        this.train2 = train2;
        this.waitingTime = waitingTime;
    }

    @Override
    public String toString(){
        return stations.toString();
    }
}
