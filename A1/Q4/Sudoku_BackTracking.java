package cs486.artificial.inteligence;

import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by mac on 2017-01-28.
 */
public class Sudoku_BackTracking {

    private int[][] grid;
    public int variableAssignmentCount = 0;

    public Sudoku_BackTracking(int[][] grid) {
        this.grid = grid;

    }

    public boolean solveByBackTracking() {

        //find unassigned location
        Pair<Integer, Integer> unassigned_coordinate = searchUnassignedGrid();

        if(unassigned_coordinate == null) {
            //there are no unassigned grids, sudoku completes
            return true;
        } else if (variableAssignmentCount > 10000) {
            return false;
        }

        int row = unassigned_coordinate.fst;
        int col = unassigned_coordinate.snd;

        //try each number from 1 to 9 for a single grid
        for (int number = 1; number <= 9; number++) {
            if(checkForConflict(row, col, number)) {
                //assign current grid if no conflict against constraints
                variableAssignmentCount ++;
                grid[row][col] = number;

                //recursion for next unassigned grid
                //returns true when assignment is successful
                if(solveByBackTracking()) return true;

                //failed assignment
                variableAssignmentCount++;
                grid[row][col] = 0;
            }
        }

        return false;
    }

    //true means no conflict, false means should not proceed
    private boolean checkForConflict(int row, int col, int number) {
        //check if current number violates any constraints

        //row check, if value exists, violates constraint and return false
        for (int column = 0; column < 9; column++) {
            if(grid[row][column] == number) {
                return false;
            }
        }

        //column check
        for(int i = 0; i < 9; i++) {
            if(grid[i][col] == number) {
                return false;
            }
        }

        //3x3 matrix check
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(grid[i + startRow][j + startCol] == number) return false;
            }
        }

        return true;

    }

    private Pair<Integer, Integer> searchUnassignedGrid() {
        //value 0 represents unassigned
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    public void print() {
        for(int i = 0; i < 9; i ++) {
            for(int j = 0; j < 9; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //read from file with path from user input
        int totalAssignments = 0;
        int grid[][] = new int[9][9];
        int fileNum = 10;

        long startTime = System.currentTimeMillis();

        //for loop of reading 10 instance files
        for(int num = 1; num <=71; num ++) {
            String folderPath = "problems/" + num;
            for (int i = 1; i <= 10; i++) {
                try (BufferedReader reader = new BufferedReader(new FileReader(folderPath + System.getProperty("file.separator")
                        + i + ".sd"))) {

                    String line = reader.readLine();
                    int row = 0;
                    while (line != null) {
                        String[] rowData = line.split(" ");

                        for (int j = 0; j < 9; j++) {
                            grid[row][j] = Integer.parseInt(rowData[j]);
                        }

                        line = reader.readLine();
                        if (row == 8) break;
                        row++;
                    }

                    //System.out.println("solving file: " + i);
                    Sudoku_BackTracking sudoku_backTracking = new Sudoku_BackTracking(grid);

                    if (sudoku_backTracking.solveByBackTracking() == true) {
                        //print grid
                        totalAssignments += sudoku_backTracking.variableAssignmentCount;
                        //sudoku_backTracking.print();
                    } else {
                        System.out.println("Unable to find a solution");
                        fileNum --;
                    }

                } catch (IOException e) {
                    System.out.println("Unable to read file.");
                }
            }
            System.out.println(num + " " + totalAssignments / fileNum);
            totalAssignments = 0;
            fileNum = 10;
        }
    }
}
