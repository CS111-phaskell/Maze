// filename: MazeSolver.java
//
// Maze Solver - Full Solution
// authors: dkhati2

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class Maze {
    private char[][] maze;
    int width;
    int height;
    int startRow;   // row position of 'S'
    int startCol;   // col position of 'S'

    // ---- CONSTRUCTOR ----
    // reads the maze from a file and stores it in a 2D array
    public Maze(String fileName) {
        try {
            // --- FIRST PASS: count rows and columns ---
            Scanner scan = new Scanner(new File(fileName));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (height == 0) {
                    width = line.length(); // first line sets the width
                }
                // if any line is a different length, maze is not rectangular
                if (line.length() != width) {
                    System.out.println("ERROR: maze is not rectangular");
                    System.exit(1);
                }
                height++;
            }
            scan.close();

            // now that we know the dimensions, create the 2D array
            maze = new char[height][width];

            // --- SECOND PASS: fill in the array ---
            Scanner scan2 = new Scanner(new File(fileName));
            for (int i = 0; i < height; i++) {
                String line = scan2.nextLine();
                for (int j = 0; j < width; j++) {
                    char c = line.charAt(j);
                    // check for invalid characters
                    if (c != 'S' && c != 'E' && c != 'O' && c != '-') {
                        System.out.println("ERROR: invalid character: " + c);
                        System.exit(1);
                    }
                    maze[i][j] = c;
                }
            }
            scan2.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: file was not found: " + fileName);
            System.exit(1);
        }
    }

    // ---- FIND START ----
    // scans the maze to find the row and col of 'S'
    // stores them in startRow and startCol
    public void findStart() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j] == 'S') {
                    startRow = i;
                    startCol = j;
                }
            }
        }
    }

    // ---- SOLVE ----
    // main solving logic using wall following with backtracking
    // marks active path with 'X' and dead ends with 'D'
    public void solve() {
        findStart();

        int row = startRow;
        int col = startCol;

        // direction: 0=North, 1=East, 2=South, 3=West
        // row offsets for each direction
        int[] dRow = {-1, 0, 1, 0};
        // col offsets for each direction
        int[] dCol = {0, 1, 0, -1};

        // step counter to detect loops
        int steps = 0;
        int maxSteps = height * width;

        // mark start as part of path
        maze[row][col] = 'X';

        // keep going until we find 'E'
        while (true) {

            // check if we are stuck in a loop
            if (steps > maxSteps) {
                System.out.println("ERROR: STUCK IN A LOOP :(");
                System.exit(1);
            }

            // try all 4 directions to find an unvisited 'O'
            boolean moved = false;
            for (int dir = 0; dir < 4; dir++) {
                int newRow = row + dRow[dir];
                int newCol = col + dCol[dir];

                // check if the new position is within bounds
                if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
                    continue; // skip if out of bounds
                }

                // if we found the exit, we are done!
                if (maze[newRow][newCol] == 'E') {
                    System.out.println("Maze solved!");
                    cleanup();
                    return;
                }

                // move to unvisited cell
                if (maze[newRow][newCol] == 'O') {
                    maze[newRow][newCol] = 'X'; // mark as path
                    row = newRow;
                    col = newCol;
                    moved = true;
                    steps++;
                    break; // move in this direction
                }
            }

            // if we couldn't move, we are at a dead end
            // backtrack until we find a cell with an unvisited neighbor
            if (!moved) {
                // mark current cell as dead end
                maze[row][col] = 'D';

                // backtrack until we find an 'X' cell next to an 'O'
                boolean foundNewPath = false;
                while (!foundNewPath) {

                    // check if we are back at start with no options
                    if (row == startRow && col == startCol) {
                        System.out.println("ERROR: THERE IS NO GETTING OUT OF HERE!");
                        System.exit(1);
                    }

                    // look for an adjacent X to backtrack to
                    for (int dir = 0; dir < 4; dir++) {
                        int newRow = row + dRow[dir];
                        int newCol = col + dCol[dir];

                        // check bounds
                        if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
                            continue;
                        }

                        // backtrack along X cells
                        if (maze[newRow][newCol] == 'X') {
                            row = newRow;
                            col = newCol;

                            // check if this X cell has any unvisited O neighbors
                            for (int dir2 = 0; dir2 < 4; dir2++) {
                                int checkRow = row + dRow[dir2];
                                int checkCol = col + dCol[dir2];

                                // check bounds
                                if (checkRow < 0 || checkRow >= height || checkCol < 0 || checkCol >= width) {
                                    continue;
                                }

                                // found an unvisited neighbor, stop backtracking
                                if (maze[checkRow][checkCol] == 'O') {
                                    foundNewPath = true;
                                }
                            }

                            // if no new path found, mark this cell as D too
                            if (!foundNewPath) {
                                maze[row][col] = 'D';
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    // ---- CLEANUP ----
    // converts all 'D' cells back to 'O'
    // leaving only 'X' to mark the solution path
    public void cleanup() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j] == 'D') {
                    maze[i][j] = 'O';
                }
            }
        }
    }

    // ---- PRINT ----
    // prints the maze row by row
    public void print() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }
}

class MazeSolver {
    public static void main(String[] args) {
        // check that a filename was provided
        if (args.length < 1) {
            System.out.println("ERROR: no filename provided.");
            return;
        }
        Maze thisMaze = new Maze(args[0]);
        System.out.println("Original maze:");
        thisMaze.print();
        System.out.println("Solved maze:");
        thisMaze.solve();
        thisMaze.print();
    }
}