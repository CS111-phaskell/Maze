// filename: MazeSolver.java
//
// in class practice(modified)
// authors: dkhati2

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class Maze {
	private char[][] maze; // for 2-d array
	int width;
	int height;


	public Maze(String fileName) {
		try {
			Scanner scan = new Scanner(new File(fileName));
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				if (height == 0) { 		
					width = line.length(); // setting the width of the maze to be the length of the first line
				}
				// if the maze is not rectangular
				if(line.length() != width) {
					System.out.println("ERROR: maze is not rectangular");
					System.exit(0);
				}
				height++;
			}

			maze = new char[height][width];
			scan.close();

			// filling in the values because we have an empty array so far
			Scanner scan2 = new Scanner(new File(fileName)); // second scanner
			for (int i = 0; i < height; i++) {
				String line = scan2.nextLine();
				for(int j = 0; j < width; j++) {
					char c = line.charAt(j);
					// in case of invalid charaters
					if(c != 'S' && c != 'E' && c != 'O' && c != '-') {
						System.out.println("ERROR: invalid charater.");
						System.exit(0);
					}
					maze[i][j] = c;
				}
			}
			scan2.close();

		} catch (FileNotFoundException e) { //incase there is no file
			System.out.println("ERROR: file was not found: " + e);
		}
	}

	public void print() {
		// 2-d arrays can't be printed out directly so printing it out line by line 
		for(int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.print(maze[i][j]);
			}
			System.out.println();
		}
	}
}

class MazeSolver {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("ERROR: no filename provided.");
			return;
		}
		Maze thisMaze = new Maze(args[0]);
		thisMaze.print();
	}

}
