package cs486.artificial.inteligence;

import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SimulatedAnealing {

    private ArrayList<CityInformation> cityList = new ArrayList<CityInformation>();
    private int numOfCities = 0;

    private final double decreasePercent = 0.00001;

    //random number for temperature
    private double temperature = 66666;

    //initialize random best route
    private RouteInformation bestRoute;

    public void addCityList(CityInformation newElement) {
        cityList.add(newElement);
    }

    public void setNumOfCities(int num) {
        numOfCities = num;
    }

    public void findOptimalRoute() {
        bestRoute = new RouteInformation(cityList, true);
        RouteInformation currentRoute = new RouteInformation(cityList, true);

        System.out.print(currentRoute.constructTotalDistance() + " ");

        int counter = 0;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        String last = df.format(currentRoute.constructTotalDistance());

        while(temperature > 1) {
            counter ++;
            //System.out.println("current temperature: " + temperature);

            //get a random pair of integer representing the swapping index
            Pair<Integer, Integer> randInts = getRandInt();

            //swap the index and generate a new route
            RouteInformation newRoute = swapCities(randInts, currentRoute);

            //construct the new distance for both routes
            double distance = currentRoute.constructTotalDistance();
            double newDistance = newRoute.constructTotalDistance();

            //random double used for probability comparison
            double rand = ThreadLocalRandom.current().nextDouble(0, 1);

            if(distance > newDistance) {
                //accept for sure
                //pick new distance as best route
                currentRoute = new RouteInformation(newRoute.route, false);
            } else if (Math.exp((distance - newDistance)/temperature) > rand) {
                //case where new distance is equal or larger than current distance
                //pick the less optimal distance with the probability
                currentRoute = new RouteInformation(newRoute.route, false);
            }

            if(!df.format(currentRoute.constructTotalDistance()).equals(last)) {
                System.out.println(currentRoute.constructTotalDistance() + " ");
                last = df.format(currentRoute.constructTotalDistance());
            }

            //keep track of the best route with minimal distance
            if(currentRoute.constructTotalDistance() < bestRoute.constructTotalDistance()) {
                bestRoute = new RouteInformation(currentRoute.route, false);
            }

            //decrease the temperature
            //first schedule: temperature *= (1 - decreasePercent);
            //second schedule: temperature -= decreasePercent;
            //third schedule: rand = ThreadLocalRandom.current().nextDouble(0, 0.00001);
            //temperature *=  (1 - rand);

            temperature *= (1 - decreasePercent);
        }
        System.out.println();

        System.out.println("The best route is: ");
        for(int i = 0; i < numOfCities; i++) {
            System.out.print(bestRoute.route.get(i).city + " ");
        }

        System.out.println("with distance: " + bestRoute.constructTotalDistance());
        System.out.println("runs: " + counter);

    }

    //swapping cities using indexes
    private RouteInformation swapCities(Pair<Integer, Integer> swapIndex, RouteInformation current) {

        RouteInformation newRoute = new RouteInformation(current.route, false);

        CityInformation temp = newRoute.route.get(swapIndex.fst);
        newRoute.route.set(swapIndex.fst, newRoute.route.get(swapIndex.snd));
        newRoute.route.set(swapIndex.snd, temp);

        return newRoute;
    }

    //generate a pair of random int within the total number of cities
    private Pair<Integer, Integer> getRandInt() {
        if (numOfCities == 1) {
            return new Pair<>(0, 0);
        }

        int first = ThreadLocalRandom.current().nextInt(0, numOfCities);
        int second = first;

        while(first == second) {
            second = ThreadLocalRandom.current().nextInt(0, numOfCities);
        }

        return new Pair<>(first, second);
    }


    public static void main(String[] args) throws FileNotFoundException {
        //read from file with path from user input
        //String folderPath = "randTSP/16";
        //folderPath + System.getProperty("file.separator")
        //+ "instance_" + i + ".txt"

        String folderPath = "";
        long startTime = System.currentTimeMillis();

        //for loop of reading 10 instance files
        for(int i = 1; i <= 1; i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader("randTSP/problem36"))) {

                SimulatedAnealing simulatedAnealing = new SimulatedAnealing();

                String line = reader.readLine();
                //first line indicates the number of cities for the current example set
                simulatedAnealing.setNumOfCities(Integer.parseInt(line));
                line = reader.readLine();


                while (line != null) {
                    //construct the list of cities
                    String[] splitedLine = line.split(" ");
                    CityInformation city = new CityInformation(splitedLine[0], Integer.parseInt(splitedLine[1]), Integer.parseInt(splitedLine[2]));
                    simulatedAnealing.addCityList(city);
                    line = reader.readLine();
                }

                simulatedAnealing.findOptimalRoute();

            } catch (IOException e) {
                System.out.println("Unable to read file.");
            }
        }

        long stopTime = System.currentTimeMillis();

        System.out.println("execution time: " + (stopTime - startTime));
    }
}
