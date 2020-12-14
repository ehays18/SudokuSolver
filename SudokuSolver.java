import java.util.*;    
import java.io.File;
import java.io.FileNotFoundException;

// Reads in a sudoku puzzle from a text file and finds a solution

public class SudokuSolver {
    
	private static int[][] final_board;
	private static int[][] current_board;
	
	public SudokuSolver() {
		// give a text file format of a puzzle to read in
		File file = new File("test2.txt");
    	Scanner scanner;
    	
		try {
			scanner = new Scanner(file);
			
			int[][] board = new int[9][9];
	        int row = 0;

	        openFile(scanner, board, row);
	        
	        current_board = copyMatrix(board);
	        
	        // Will keep track the 3x3 sectors - the 3x3 sub-squares of the puzzle
	        ArrayList<ArrayList<Integer>> sectors = new ArrayList<ArrayList<Integer>>();
	        sectors.clear();
	        for(int i = 0; i < 9; i++) {
	        	sectors.add(new ArrayList<Integer>());
	        }
	        updateSectors(board, sectors);	        
	        
	        final_board = bruteForceSolve(board, sectors);
	        
	        printBoard(final_board);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
	}
    
    // This function takes somewhat of a brute force approach, testing each possible number
	// for each space of the puzzle.
    public int[][] bruteForceSolve(int[][] board, ArrayList<ArrayList<Integer>> sectors) {
    	
    	// keep track of the numbers in each row and column
    	ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
    	ArrayList<ArrayList<Integer>> columns = new ArrayList<ArrayList<Integer>>();
    	
    	rows.clear();
    	columns.clear();
    	
    	// fill the lists with the contents of the puzzle
    	populateRowColumn(board, sectors, rows, columns);
    	
    	int[][] solved_board = copyMatrix(board);
    	
    	// call the recursive solver method to see if the puzzle can be solved
    	if(solveRecur(board, sectors, rows, columns, 0, 0)) {
    		System.out.println("SOLVED");
    		solved_board = getFinalBoard();
    		return solved_board;
    	}
    	else {
    		System.out.println("NOT SOLVED");
    		return board;
    	}
    }
    
    // Recursive sudoku solver method
    // Finds the possible numbers for the current space, and tries each possible number
    // Continues on until either the puzzle is solved, or a solution cannot be found, in which 
    // case the program backtracks to try different numbers.
    public boolean solveRecur(int[][] board, ArrayList<ArrayList<Integer>> sectors, ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> columns, int r, int c) {
    	int[][] copy_board = copyMatrix(board);
    	
    	current_board = copyMatrix(board);
    	
    	// if there are no zeroes left, it has been solved
    	if(!boardHasZeros(copy_board)) {
        	final_board = copyMatrix(copy_board);
    		return true;
    	}
    	// reached the end of the board
    	if(r >= 8 && c >= 9) {
    		return true;
    	}
    	// reached the end of the row
    	if(c >= 9) {
    		return solveRecur(board, sectors, rows, columns, r + 1, 0);
    	}
    	
    	// add numbers from new board passed in
    	updateRowColumn(board, rows, columns);
    	updateSectors(board, sectors);
    	
    	// if there is already a number, skip it
    	if(board[r][c] != 0) {
			return solveRecur(board, sectors, rows, columns, r, c + 1);
		}
    	
    	// Print the current index for debugging
    	//System.out.println("Board Space: " + r + " " + c);
    		
    	// find which sector of the board the current index is
    	int sector_index = calculateSectorNumber(r, c);
    	ArrayList<Integer> possible_nums = new ArrayList<Integer>();
        possible_nums = getPossibleNums(board, sectors, rows, columns, r, c, sector_index);            	    			
            	
        // if there are no possible numbers to input, it cannot be solved
        if(possible_nums.isEmpty()) {
        	//System.out.print(board[r][c]);
            //System.out.println(" NO POSSIBLE NUMS");
        	return false;
        }
        		
        // attempt each possible number for the current index of the board
        for(int i = 0; i < possible_nums.size(); i++) {
        		
        	updateRowColumn(board, rows, columns);
        	updateSectors(board, sectors);
        	
        	// create temporary lists to attempt each possible number
        	ArrayList<ArrayList<Integer>> temp_sectors = new ArrayList<ArrayList<Integer>>();
        	temp_sectors.addAll(sectors);
        	ArrayList<ArrayList<Integer>> temp_rows = new ArrayList<ArrayList<Integer>>();
        	temp_rows.addAll(rows);
        	ArrayList<ArrayList<Integer>> temp_columns = new ArrayList<ArrayList<Integer>>();
        	temp_columns.addAll(columns);
        	copy_board = copyMatrix(board);        	    	
        	
        	temp_sectors.get(sector_index).add(possible_nums.get(i));
    		temp_rows.get(r).add(possible_nums.get(i));
    		temp_columns.get(c).add(possible_nums.get(i));
        	copy_board[r][c] = possible_nums.get(i);
        			
        	// if still in bounds and at the end of the row, move to the next row with the possible number
        	if(r < 8 && c >= 8) {
        		return solveRecur(copy_board, temp_sectors, temp_rows, temp_columns, r + 1, 0);
        	}
        	// move to the next column
        	if(solveRecur(copy_board, temp_sectors, temp_rows, temp_columns, r, c + 1)) {
        		return true;
        	}
        			
        } //end for loop
    	
        // if the program has reached here and still has zeroes, return false
        if(boardHasZeros(copy_board)) {
    		return false;
    	}
        // otherwise set the final board equal to the solved puzzle and return true
        else {
        	final_board = copyMatrix(copy_board);
    		return true;
        }
    }
    
    // retrieves the possible numbers for the space given
    public ArrayList<Integer> getPossibleNums(int[][] board, ArrayList<ArrayList<Integer>> sectors, ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> columns, int row_index, int col_index, int sector_index) {
    	ArrayList<Integer> possible_nums = new ArrayList<Integer>();
    	ArrayList<Integer> taken_nums = new ArrayList<Integer>();
    	
    	//first get the list of numbers used already in the row relative to this position
    	for(int i = 0; i < rows.get(row_index).size(); i++) {
    		taken_nums.add(rows.get(row_index).get(i));
    	}
    	
    	//figure out all of the numbers not in the row
    	for(int i = 1; i < 10; i++) {
    		if(!taken_nums.contains(i)) {
    			possible_nums.add(i);
    		}
    	}
    	
    	//cross-examine which numbers are possible with columns
    	for(int i = 0; i < columns.get(col_index).size() ; i++) {
    		if(possible_nums.contains(columns.get(col_index).get(i))) {
    			for(int j = possible_nums.size() - 1; j >= 0 ; j--) {
    				if(possible_nums.get(j) == columns.get(col_index).get(i)) {
    					possible_nums.remove(j);
    				}    				
    			}
    		}
    	}
    	
    	//cross-examine which numbers are possible with sectors
    	for(int i = 0; i < sectors.get(sector_index).size(); i++) {
    		if(possible_nums.contains(sectors.get(sector_index).get(i))) {
    			for(int j = possible_nums.size() - 1; j >= 0 ; j--) {
    				if(possible_nums.get(j) == sectors.get(sector_index).get(i)) {
    					possible_nums.remove(j);
    				}    				
    			}
    		}
    	}
    	
    	return possible_nums;
    }
    
    // checks whether or not there are zeroes still on the board
    public boolean boardHasZeros(int[][] board) {
    	boolean zeros = false;
    	for(int r = 0; r < 9; r++) {
    		for(int c = 0; c < 9; c++) {
    			if(board[r][c] == 0) {
    				zeros = true;
    				break;
    			}
    		}
    	}
    	return zeros;
    }

    // returns the final board
    public int[][] getFinalBoard() {
    	return final_board;
    }
    
    // returns the current iteration of the board
    public int[][] getCurrentBoard() {
    	return current_board;
    }
    
    //function to print the contents of each of the sectors
    public void printSectors(ArrayList<ArrayList<Integer>> sectors) {
    	System.out.println("Sectors:");
    	for(int r = 0; r < sectors.size(); r++) {
            for(int c = 0; c < sectors.get(r).size(); c++) {
                System.out.print(sectors.get(r).get(c) + " ");
            }
            System.out.println();
        }
    	
    	System.out.println();
        
        return;
    }
    
    //function to print out the contents of each row and column
    public void printRowColumn(ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> columns) {
    	System.out.println("Rows");
    	for(int r = 0; r < rows.size(); r++) {
    		for(int c = 0; c < rows.get(r).size(); c++) {
    			System.out.print(rows.get(r).get(c) + " ");
    		}
    		System.out.println();
    	}
    	
    	System.out.println("Columns");
    	for(int r = 0; r < columns.size(); r++) {
    		for(int c = 0; c < columns.get(r).size(); c++) {
    			System.out.print(columns.get(r).get(c) + " ");
    		}
    		System.out.println();
    	}
    	
    	System.out.println();
    	
    	return;
    }
    
    //function to update the 3x3 sectors
    public void updateSectors(int[][] board, ArrayList<ArrayList<Integer>> sectors) {
    	
    	for(int i = 0; i < 9; i++) {
    		sectors.get(i).clear();
    	}
    	
    	// add the numbers to the sectors according to the row and column indices
    	for(int r = 0; r < 9; r++) {
            for(int c = 0; c < 9; c++) {
                
            	if(r < 3) {
                	if(c < 3) {
                		if(board[r][c] != 0)
                			sectors.get(0).add(board[r][c]);
                	}
                	else if(c >= 3 && c < 6) {
                		if(board[r][c] != 0)
                			sectors.get(1).add(board[r][c]);
                	}
                	else {
                		if(board[r][c] != 0)
                			sectors.get(2).add(board[r][c]);
                	}
                }
                
                else if(r >= 3 && r < 6) {
                	if(c < 3) {
                		if(board[r][c] != 0)
                			sectors.get(3).add(board[r][c]);
                	}
                	else if(c >= 3 && c < 6) {
                		if(board[r][c] != 0)
                			sectors.get(4).add(board[r][c]);
                	}
                	else {
                		if(board[r][c] != 0)
                			sectors.get(5).add(board[r][c]);
                	}
                }
            	
                else {
                	if(c < 3) {
                		if(board[r][c] != 0)
                			sectors.get(6).add(board[r][c]);
                	}
                	else if(c >= 3 && c < 6) {
                		if(board[r][c] != 0)
                			sectors.get(7).add(board[r][c]);
                	}
                	else {
                		if(board[r][c] != 0)
                			sectors.get(8).add(board[r][c]);
                	}
                }
            }	            
        }
    	
    	return;
    }
    
    // update the numbers in the row and column lists
    public void updateRowColumn(int[][] board, ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> columns) {
    	
    	for(int i = 0; i < 9; i++) {
    		rows.get(i).clear();
    		columns.get(i).clear();
    	}
    	
    	for(int r = 0; r < 9; r++) {
    		for(int c = 0; c < 9; c++) {
    			if(board[r][c] != 0)
    				rows.get(r).add(board[r][c]);
    		}
    	}
    	
    	for(int c = 0; c < 9; c++) {
    		for(int r = 0; r < 9; r++) {
    			if(board[r][c] != 0)
    				columns.get(c).add(board[r][c]);
    		}
    	}
    	
    	return;
    }
    
    // adds the contents of the board to the row and column arraylist variables
    public void populateRowColumn(int[][] board, ArrayList<ArrayList<Integer>> sectors, ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> columns) {
    	for(int r = 0; r < 9; r++) {
    		rows.add(r, new ArrayList<Integer>());
    	}
    	
    	for(int r = 0; r < 9; r++) {
    		for(int c = 0; c < 9; c++) {
    			if(board[r][c] != 0)
    				rows.get(r).add(board[r][c]);
    		}
    	}
    	
    	for(int r = 0; r < 9; r++) {
    		columns.add(r, new ArrayList<Integer>());
    	}
    	
    	for(int c = 0; c < 9; c++) {
    		for(int r = 0; r < 9; r++) {
    			if(board[r][c] != 0)
    				columns.get(c).add(board[r][c]);
    		}
    	}
    	
    	return;
    }
    
    // calculates the index of a sector based on the given row and column index of the board
    public int calculateSectorNumber(int row_index, int col_index) {
    	int sector_index;
    	if(row_index < 3) {
        	if(col_index < 3) {
        		sector_index = 0;
        	}
        	else if(col_index >= 3 && col_index < 6) {
        		sector_index = 1;
        	}
        	else {
        		sector_index = 2;
        	}
        }
        else if(row_index >= 3 && row_index < 6) {
        	if(col_index < 3) {
        		sector_index = 3;
        	}
        	else if(col_index >= 3 && col_index < 6) {
        		sector_index = 4;
        	}
        	else {
        		sector_index = 5;
        	}
        }
        else {
        	if(col_index < 3) {
        		sector_index = 6;
        	}
        	else if(col_index >= 3 && col_index < 6) {
        		sector_index = 7;
        	}
        	else {
        		sector_index = 8;
        	}
        }
    	return sector_index;
    }

    // makes a copy of a given matrix
    public int[][] copyMatrix(int[][] matrix) {
    	int[][] copy = new int[matrix.length][matrix[0].length];
    	
    	for(int r = 0; r < matrix.length; r++) {
    		for(int c = 0; c < matrix[r].length; c++) {
    			copy[r][c] = matrix[r][c];
    		}
    	}
    	
    	return copy;
    }
    
    // reads in a sudoku text file
    public void openFile(Scanner scanner, int[][] board, int row) {
    	//while there are still lines to read in from the sudoku board
        while(scanner.hasNext()) {
            String line = scanner.nextLine();

            for(int i = 0; i < line.length(); i++) {
            	if(line.charAt(i) == ' ') {
            		board[row][i] = 0;
            	}
            	else {
            		board[row][i] = Integer.parseInt(line.substring(i, i + 1));
            	}
            }
            row++;
        }    
    	return;
    }
    
    //function to print a given board
    public void printBoard(int[][] board) {
        for(int r = 0; r < board.length; r++) {
            for(int c = 0; c < board[r].length; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
        
        return;
    }
    
}