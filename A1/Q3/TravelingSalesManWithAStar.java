package cs486.artificial.intelligence;


import java.io.*;
import java.util.*;

public class TravelingSalesManWithAStar {
    //total number of cities
    private int numOfCities;
    //2d map of distance between all cities
    private List<ArrayList<Double>> euclideanDistanceOfCities;
    //mapping between city name and index
    private HashMap<String, Integer> listOfCities;
    //used to store expanded tree nodes when executing
    HashMap<StringBuffer, ArrayList<String>> processQueue;
    private String optimalRoute = "";
    private int numOfNodes = 1;


    public TravelingSalesManWithAStar(String fileContent) {
        euclideanDistanceOfCities = new ArrayList<ArrayList<Double>>();
        listOfCities = new HashMap<>();
        processQueue = new HashMap<StringBuffer, ArrayList<String>>();

        //parse the file content
        String[] seperatedFileContent = fileContent.split(System.lineSeparator());
        numOfCities = Integer.parseInt(seperatedFileContent[0]);

        for (int i = 1; i < seperatedFileContent.length; i++) {

            String[] node1 = seperatedFileContent[i].split(" ");
            //city name and index value in euclideanDistanceOfCities 2d array
            listOfCities.put(node1[0], i-1);
            ArrayList<Double> rowDistance = new ArrayList<>();

            //construct euclidean distance among cities
            for (int j = 1; j < seperatedFileContent.length; j++) {
                String[] node2 = seperatedFileContent[j].split(" ");

                    //calculate euclideanDistance for 2 nodes and add to the hash table
                    int node1_x = Integer.parseInt(node1[1]);
                    int node1_y = Integer.parseInt(node1[2]);
                    int node2_x = Integer.parseInt(node2[1]);
                    int node2_y = Integer.parseInt(node2[2]);

                    Double distance = Math.sqrt( Math.pow((node1_x - node2_x), 2) + Math.pow((node1_y - node2_y), 2) );

                    rowDistance.add(distance);
            }
            euclideanDistanceOfCities.add(rowDistance);

        }


        //start with initial node, with index 0
        //current visited cities, next unvisited city, current g(n)
        constructNewProcessQueueEntry("00", "0", 0.0);

    }

    public void findOptimalRoute() {

        //search for the lowest fn in processQueue
        StringBuffer minFnRoute = getMinFn();

        //get a list of unvisited nodes for current route
        String unvisited = processQueue.get(minFnRoute).get(0);

        //goal node
        if(unvisited.equals("")) {
            optimalRoute = minFnRoute.toString();
            System.out.println(optimalRoute);
            return;
        }

        String[] minFn_unvisitedNodes = processQueue.get(minFnRoute).get(0).split(",");

        //expand the routeNode using unvisited cities by appending to the end of the route
        for(int i = 0; i < minFn_unvisitedNodes.length; i++) {
            numOfNodes ++;
            constructNewProcessQueueEntry(minFnRoute.toString(), minFn_unvisitedNodes[i], Double.parseDouble(processQueue.get(minFnRoute).get(2)));
        }

        //delete current node with minimum fn
        processQueue.remove(minFnRoute);

        findOptimalRoute();
    }

    public int getNumNodes() {
        return numOfNodes;
    }

    //current visited cities, next unvisited city, current g(n)
    private void constructNewProcessQueueEntry(String visitedCities, String nextCity, Double currentGn) {
        StringBuffer routeNodes = new StringBuffer();
        String[] citiesList = visitedCities.split(",");
        int lastVisitedCityIndex;

        //if not the initial node
        if(!visitedCities.equals("00")) {
            routeNodes.append(visitedCities);
            routeNodes.append(",").append(nextCity);
            lastVisitedCityIndex = Integer.parseInt(String.valueOf(citiesList[citiesList.length-1]));

        } else {
            //initial node
            routeNodes.append(nextCity);
            lastVisitedCityIndex = 0;
        }

        ArrayList<String> nodeInfo = new ArrayList<>();

        //index 0: list of unvisited cities
        String unvisited_cities = findUnvisitedCities(routeNodes.toString());
        nodeInfo.add(unvisited_cities);

        //new g(n) = current g(n) + new edge from last visited city to next unvisited city
        int nextCityIndex = Integer.parseInt(nextCity);
        Double newGnValue = currentGn
                + euclideanDistanceOfCities.get(lastVisitedCityIndex).get(nextCityIndex);

        //index 1: f(n) = g(n) + h(n)
        //calculate the new fn for each new route added
        nodeInfo.add(calculateFn(newGnValue, nextCity + "," + unvisited_cities));

        //index 2: g(n) representation
        nodeInfo.add(String.valueOf(newGnValue));

        processQueue.put(routeNodes, nodeInfo);

    }

    //search for the min Fn among current process queue
    private StringBuffer getMinFn() {

        StringBuffer minFnRoute = new StringBuffer();
        Set<StringBuffer> routeSets = processQueue.keySet();
        double minFnValue = 999;
        for (StringBuffer route: routeSets) {
            double fn = Double.parseDouble(processQueue.get(route).get(1));
            if(minFnValue > fn) {
                minFnValue = fn;
                minFnRoute = route;
            }
        }
        return minFnRoute;
    }

    public String calculateFn(Double currentGn, String unvisited_cities) {
        //sort unvisited cities
        int i = 0;
        int[] cities;
        String[] unvisitedCitiesList = unvisited_cities.split(",");
        if(!unvisitedCitiesList[0].equals("0")) {
            cities = new int[unvisitedCitiesList.length+1];
            cities[i++] = 0;
        } else {
            cities = new int[unvisitedCitiesList.length];
        }

        double unvisited_cost[][] = new double[cities.length][cities.length];
        double cost = 0;

        for(String city: unvisitedCitiesList) {
            cities[i] = Integer.parseInt(city);
            i++;
        }

        Arrays.sort(cities);

        //calculate MST cost for current unvisited cities
        for(i = 0; i < cities.length; i++) {
            for(int j = 0; j < cities.length; j++) {
                //construct new mst map for unvisited cities
                unvisited_cost[i][j] = euclideanDistanceOfCities.get(cities[i]).get(cities[j]);
            }
        }

        Prims mstCalculator = new Prims(unvisited_cost);
        double[][] mst_unvisited = mstCalculator.calc();

        for(i = 0; i < cities.length; i++) {
            for(int j = 0; j < cities.length; j++) {
                //compute the total cost for current MST
                cost += mst_unvisited[i][j];
            }
        }

        return String.valueOf(currentGn + cost);
    }

    //generate a list of unvisited cities for current route
    public String findUnvisitedCities(String routeNodes) {

        StringBuffer unvisitedCities = new StringBuffer("");
        String[] splitedRouteNodes = routeNodes.split(",");

        for(int i = 0; i < listOfCities.size(); i++) {
            if(!Arrays.asList(splitedRouteNodes).contains(Integer.toString(i))) {
                if(unvisitedCities.length() != 0) {
                    unvisitedCities.append(",");
                }
                unvisitedCities.append(Integer.toString(i));
            }
        }

        return unvisitedCities.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
	    //read from file with path from user input
        String folderPath = "randTSP/16";
        int totalNodes = 0;

        long startTime = System.currentTimeMillis();

        //for loop of reading 10 instance files
        for(int i = 1; i <= 1; i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(folderPath + System.getProperty("file.separator")
                    + "instance_" + i + ".txt"))) {

                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();

                while (line != null) {
                    builder.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }

                TravelingSalesManWithAStar travelingSalesManWithAStar = new TravelingSalesManWithAStar(builder.toString());
                travelingSalesManWithAStar.findOptimalRoute();
                totalNodes += travelingSalesManWithAStar.getNumNodes();

            } catch (IOException e) {
                System.out.println("Unable to read file.");
            }
        }

        long stopTime = System.currentTimeMillis();

        System.out.println("execution time: " + (stopTime - startTime));
        System.out.println("avg number of nodes: " + totalNodes/10);
    }

}
