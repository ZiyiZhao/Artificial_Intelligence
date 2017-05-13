package cs486.artificial.inteligence;

import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by mac on 2017-01-28.
 */
public class Sudoku_BackTracking_ForwardChecking {

    private int[][] grid;
    public int variableAssignmentCount = 0;

    public Sudoku_BackTracking_ForwardChecking(int[][] grid) {
        this.grid = grid;

    }

    public boolean solveSudoku(StringBuffer[][] availableDomain) {

        //find unassigned location
        Pair<Integer, Integer> unassigned_coordinate = searchUnassignedGrid();

        if(unassigned_coordinate == null) {
            //there are no unassigned grids, sudoku completes
            return true;
        } else if(variableAssignmentCount > 10000) {
            return false;
        }

        int row = unassigned_coordinate.fst;
        int col = unassigned_coordinate.snd;

        String[] availableDomainList = availableDomain[row][col].toString().split("");

        //failed assignment if no available domain
        if(availableDomainList.length == 0) {
            return false;
        }

        //try each number from available domain for a single grid
        for (int i = 0; i < availableDomainList.length; i++) {
            if(checkForConflict(row, col, Integer.parseInt(availableDomainList[i]))) {

                //assign current grid if no conflict against constraints
                variableAssignmentCount ++;
                grid[row][col] = Integer.parseInt(availableDomainList[i]);

                //construct new available domain for next grid, forward checking to remove assigned domain
                StringBuffer[][] newAvailableDomain = new StringBuffer[9][9];
                for (int a = 0; a < 9; a++) {
                    for(int b = 0; b < 9; b++) {
                        if(a == col || b == row || ((a/3 == row/3) && (b/3 == col/3))) {
                            newAvailableDomain[a][b] = new StringBuffer(availableDomain[a][b].toString().replace(String.valueOf(grid[row][col]), ""));
                        } else {
                            newAvailableDomain[a][b] = availableDomain[a][b];
                        }
                    }
                }

                //recursion for next unassigned grid
                //returns true when assignment is successful
                if(solveSudoku(newAvailableDomain)) return true;

                //failed assignment
                variableAssignmentCount++;
                grid[row][col] = 0;
            }
        }

        return false;
    }

    //true means no conflict, false means should not proceed
    public boolean checkForConflict(int row, int col, int number) {
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
        String folderPath = "problems/1";
        int totalAssignments = 0;
        int grid[][] = new int[9][9];

        long startTime = System.currentTimeMillis();

        //for loop of reading 10 instance files
        for(int i = 1; i <= 10; i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(folderPath + System.getProperty("file.separator")
                    + i + ".sd"))) {

                String line = reader.readLine();
                int row = 0;
                while (line != null) {
                    String[] rowData = line.split(" ");

                    for(int j = 0; j < 9; j++) {
                        grid[row][j] = Integer.parseInt(rowData[j]);
                    }

                    line = reader.readLine();
                    if(row == 8) break;
                    row ++;
                }

                System.out.println("solving file: " + i);
                Sudoku_BackTracking_ForwardChecking sudokuBackTrackingForwardChecking = new Sudoku_BackTracking_ForwardChecking(grid);

                StringBuffer[][] availableDomain = new StringBuffer[9][9];

                //initialize available domain
                for(int index = 0; index < 9; index++) {
                    for(int j = 0; j < 9; j++) {
                        StringBuffer domain = new StringBuffer();
                        //for each value in the grid
                        for(int value = 1; value <=9; value++) {
                            if (sudokuBackTrackingForwardChecking.checkForConflict(index, j, value)) {
                                domain.append(value);
                            }
                        }
                        availableDomain[index][j] = domain;
                    }
                }

                if(sudokuBackTrackingForwardChecking.solveSudoku(availableDomain) == true) {
                    //print grid
                    totalAssignments += sudokuBackTrackingForwardChecking.variableAssignmentCount;
                    sudokuBackTrackingForwardChecking.print();
                } else {
                    System.out.println("Unable to find a solution");
                }

            } catch (IOException e) {
                System.out.println("Unable to read file.");
            }
        }

        System.out.println("avg number of assignments: " + totalAssignments/10);
    }
}
