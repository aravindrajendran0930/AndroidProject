package actual.newactivity1;

/**
 * Created by Aravind on 11/14/2015.
 */
public class ParkingStructure {

    public ParkingStructure(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    int id;
    public String name;
    public String lat;
    public String lng;
    public String idString;
    public String toString()
    {
        return this.name + "("+ id +")";

    }
}
