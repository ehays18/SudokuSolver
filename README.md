# SudokuSolver
A program to solve any sudoku puzzle

This algorithm uses a recursive back-tracking approach to solve a sudoku puzzle. 

It iterates through each index of the given puzzle and calculates the possible numbers for that index. This is done by checking the other numbers in the respective row, column, and 3x3 subsquare of the index. After finding the possible numbers, the program attempts each of those possible numbers for that index. It continues on to the next indices until either a solution is found or no possible numbers can be found for an index. If no possible numbers can be found, the program backtracks to the last correct number found. If the program runs all the way through without finding a solution, a message is output telling the user that a solution could not be found. 
