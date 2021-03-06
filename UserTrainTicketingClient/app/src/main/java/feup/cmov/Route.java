package feup.cmov;

import java.util.ArrayList;

/**
 * Created by Hugo on 19/10/2015.
 */
public class Route {
    //public ArrayList<Integer> stations;
    //public ArrayList<String> times;
    public ArrayList<StationTime> stationTimes = new ArrayList<StationTime>();
    public int train1;
    public int train2;
    public int waitingTime = -1;
    public boolean soldOut = false;

    public Route(ArrayList<Integer> stations, ArrayList<String> times, int train, boolean soldOut){
        //this.stations = stations;
        //this.times = times;
        for(int i=0; i < stations.size(); i++){
            StationTime st = new StationTime();
            st.station = stations.get(i);
            st.time = times.get(i);
            stationTimes.add(st);
        }
        this.train1 = train;
        this.soldOut = soldOut;
    }

    public Route(ArrayList<Integer> stations, ArrayList<String> times, int train1, boolean soldOut, int train2, int waitingTime){
        //this.stations = stations;
        //this.times = times;
        for(int i=0; i < stations.size(); i++){
            StationTime st = new StationTime();
            st.station = stations.get(i);
            st.time = times.get(i);
            stationTimes.add(st);
        }
        this.train1 = train1;
        this.train2 = train2;
        this.waitingTime = waitingTime;
        this.soldOut = soldOut;
    }

    @Override
    public String toString(){
        return stationTimes.toString();
    }
}

