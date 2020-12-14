import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

// Class to draw the game board and initiate the solver

public class Board extends JFrame {
	
	// dimensions for the graphics
	private final int width = 500;
	private final int height = 500;
	private final int subdivision_x = (width / 9);
	private final int subdivision_y = (height / 9);
	
	private static int[][] curr_board;
	private static SudokuSolver solver;
	
	private DrawCanvas canvas;
	
	// driver program
	public static void main(String[] args) {
		curr_board = new int[9][9];
		new Board();
	}
	
	// build the board and start the solver
	public Board() {
		canvas = new DrawCanvas();
		canvas.setPreferredSize(new Dimension(width, height));
		
		Container cp = getContentPane();
		cp.add(canvas);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    pack();
	    setTitle("Sudoku Board"); 
	    setVisible(true);    
	    
	    // start the solver
	    solver = new SudokuSolver();
	    
	    // timer for updating the board
	    // TODO: Figure out how to show solving in action
	    int delay = 10; //milliseconds
	    ActionListener taskPerformer = new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            curr_board = solver.getCurrentBoard();
	        	repaint();
	        }
	    };
	    new Timer(delay, taskPerformer).start();
	    
	}
	
	
	private class DrawCanvas extends JPanel {
		
	    @Override
	    public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	        setBackground(Color.WHITE);
	        setForeground(Color.BLACK);
	        
	        //change font and size
	        Font f = new Font("default", 50, 50);
	        g.setFont(f);

	        // draw the game board
	        for(int i = 1; i < 10; i++) {
	        	if(i % 3 == 0) {
	        		g.setColor(Color.RED);
	        		g.drawLine(subdivision_x * i, 0, subdivision_x * i, height);
	        		g.drawLine(0, subdivision_y * i, width, subdivision_y * i);
	        	}
	        	else {
	        		g.setColor(Color.BLACK);
	        		g.drawLine(subdivision_x * i, 0, subdivision_x * i, height);
	        		g.drawLine(0, subdivision_y * i, width, subdivision_y * i);
	        	}
	        }
	        
	        g.setColor(Color.GREEN);
	        
	        // draw the current values of the puzzle onto the board
	        for(int r = 0; r < 9; r++) {
	        	for(int c = 0; c < 9; c++) {
	        		g.drawString("" + curr_board[r][c], subdivision_x * c, subdivision_y * (r + 1));
	        	}
	        }

	        return;
	         
	    }
	}

    
	
}