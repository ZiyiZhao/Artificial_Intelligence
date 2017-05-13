package cs486.artificial.inteligence;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mac on 2017-02-09.
 */
public class RouteInformation {

    public ArrayList<CityInformation> route = new ArrayList<>();

    //constructor
    public RouteInformation(ArrayList<CityInformation> cityList, boolean init) {
        this.route = (ArrayList<CityInformation>) cityList.clone();

        //generate a random route iff first initializing the route
        if(init) {
            Collections.shuffle(route);

        }
    }

    //find the total distance for current route
    public double constructTotalDistance() {
        double totalDistance = 0;
        for(int i = 0; i < route.size(); i++) {
            //compute distance
            //if not at the last element
            if (i != route.size() -1) {
                totalDistance += Math.sqrt( Math.pow((route.get(i).x - route.get(i+1).x), 2) + Math.pow((route.get(i).y - route.get(i+1).y), 2) );

            } else {
                //compute the distance between last element and start city
                totalDistance += Math.sqrt( Math.pow((route.get(i).x - route.get(0).x), 2) + Math.pow((route.get(i).y - route.get(0).y), 2) );
            }

        }

        return totalDistance;
    }

}

