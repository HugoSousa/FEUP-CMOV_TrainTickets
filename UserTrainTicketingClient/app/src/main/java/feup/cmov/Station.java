package feup.cmov;

/**
 * Created by Hugo on 19/10/2015.
 */
public class Station {
    public String name;
    public int id;

    public Station(String name, int id){
        this.name= name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
