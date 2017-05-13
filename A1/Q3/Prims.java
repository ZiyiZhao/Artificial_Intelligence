package cs486.artificial.intelligence;

/**
 * Created by mac on 2017-01-27.
 */

import java.util.*;
public class Prims
{
    public int isVisited[];
    public double cost[][];
    //number of nodes
    public int n;

    public Prims (double[][] euclideanDistanceOfCities) {

        n = euclideanDistanceOfCities.length;
        cost = new double[n][n];
        isVisited = new int[n];

        //construct cost matrix
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if( euclideanDistanceOfCities[i][j] == 0.0) {
                    cost[i][j] = 999;
                } else {
                    cost[i][j] = euclideanDistanceOfCities[i][j];
                }
            }
        }

        isVisited[0] = 1;
    }

    public double[][] calc() {
        int i,j,num_edges=1,a=1,b=1,minpos_i=1,minpos_j=1;
        double min;
        double mstCost[][] = new double[n][n];

        while(num_edges < n) {

            for(i=0,min=999;i < n;i++) {
                for (j = 0; j < n; j++) {
                    //find the minimum cost
                    if (this.cost[i][j] < min) {
                        if (this.isVisited[i] != 0) {
                            min = this.cost[i][j];
                            a = minpos_i = i;
                            b = minpos_j = j;
                        }
                    }
                }

                //find MST min cost if the city is not yet visited
                if (this.isVisited[minpos_i] == 0 || this.isVisited[minpos_j] == 0) {
                    mstCost[a][b] = min;
                    num_edges = num_edges + 1;
                    this.isVisited[b] = 1;
                }

                this.cost[a][b] = this.cost[b][a] = 999;
            }
        }
        return mstCost;
    }

}