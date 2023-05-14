package checker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class represents a checkerboard, with a board of checker objects and a
 * board of strings to display.
 */
public class Board implements Cloneable{

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/** The game board, the board that is printed out to the console. */
	private String gameBoard[][]; //make public static final int

	/** The piece board, the board holding the checkers and the information. */
	private Checker pieceBoard[][];

	/** The player. */
//player true is x and false is o
	private boolean player;

	/** If the game is won. */
	private boolean won;

	/** The x count. */
//game is won when other player reaches 0 pieces
	private int xCount;

	/** The o count. */
	private int oCount;
	

	/**
	 * Instantiates a new board.
	 */
	public Board() {
		xCount = 12;
		oCount = 12;
		// creates a display board and a board of Checker objects to store information
		gameBoard = new String[8][8];
		pieceBoard = new Checker[8][8];
		player = true;
		won = false;
		// sets boards up in a regular checkers pattern
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (i <= 2 && (j + i) % 2 != 0) {
					pieceBoard[i][j] = new Checker(true, i, j);
				} else if (i >= 5 && (j + i) % 2 != 0) {
					pieceBoard[i][j] = new Checker(false, i, j);

				} else {
					pieceBoard[i][j] = new Checker();
				}
				gameBoard[i][j] = pieceBoard[i][j].getLabel();
			}
		}
	}

	/**
	 * Checks for a win.
	 */
	private void checkWin() {
		if (oCount == 0 || xCount == 0) {
			won = true;
		}
	}

	/**
	 * Checks if a piece can capture.
	 *
	 * @param x  The x of the player
	 * @param y  The y of the player
	 * @param xn The xn of the spot to move
	 * @param yn The yn of the spot to move
	 * @return true, if successful
	 */
	public boolean checkCap(int x, int y, int xn, int yn) {
		// checks if enemy piece is in diagonally in between the player and an empty
		// spot
		// this code finds the diagonal spot in between the player spot and the
		// spot being moved to, to check if there is a piece to capture in the middle
		// and the spot being jumped to is empty
		int offsetX=-1;
		int offsetY=-1;
		if(x<xn) {
			offsetX=1;
		}
		if(y<yn) {
			offsetY=1;
		}
		try {
		if((player!=pieceBoard[x+offsetX][y+offsetY].getTeamBool())&&pieceBoard[xn][yn].getTeam()=='n') {
			return true;
		}
		}catch( Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Checks if the piece at the spot should be made a king.
	 *
	 * @param x The x of the spot being checked
	 * @param y The y of the spot being checked
	 */
	private void checkKing(int x, int y) {
		// when a piece reaches the opposite end of the board it becomes king
		if (pieceBoard[x][y].getTeam() != 'n') {
			if (player && x == 7) {
				pieceBoard[x][y].makeKing();
			} else if (!player && x == 0) {
				pieceBoard[x][y].makeKing();
			}
		}
	}

	/**
	 * Checks if the move is to a valid spot
	 *
	 * @param x  The x of the spot being checked
	 * @param y  The y of the spot being checked
	 * @param xn The new x of the spot being checked
	 * @param yn The new y of the spot being checked
	 * @return true, if successful
	 */
	public boolean validMove(int x, int y, int xn, int yn) {
		// checks for out of bounds
		if (x < 0 || x > 7 || y < 0 || y > 7 || xn < 0 || xn > 7 || yn < 0 || yn > 7) {
			return false;
		}
		//makes sure move is diagonal
		if(x==xn||y==yn) {
			return false;
		}
		
		// checks for moving onto a piece
		if (pieceBoard[xn][yn].getTeam() != 'n') {
			return false;
		}
		//checks to make sure move is in range
		if(!(Math.abs(x-xn)==1&&Math.abs(y-yn)==1)) {	
			if(!checkCap(x, y,  xn, yn)) {
			return false;
			}
		}
		//makes sure moves are in the right direction if not a king
		if(!pieceBoard[x][y].getKing()) {
			if(player) {
				if(x>xn) {
					return false;
				}
			}else {
				if(x<xn) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	public boolean getPlayer() {
		return player;
	}

	/**
	 * Prints the board.
	 */
	public void printBoard() {
		// prints column labels
		System.out.println("  A  B  C  D  E  F  G  H ");
		for (int i = 0; i < 8; i++) {
			System.out.print(i + 1);
			for (int j = 0; j < 8; j++) {
				System.out.print(gameBoard[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * Makes a move, also will capture a piece if possible and do a multicapture if
	 * possible.
	 *
	 * @param x  The x of the spot being checked
	 * @param y  The y of the spot being checked
	 * @param xn The new x of the spot being checked
	 * @param yn The new y of the spot being checked
	 * @return true, if successful
	 */
	public String makeMove(int x, int y, int xn, int yn) {
		boolean cap = false;
		if (validMove(x, y, xn, yn)) {
			if (checkCap(x, y, xn, yn)) {
				// removes a piece from the count if it is captured
				if (!player) {
					xCount--;
				} else {
					oCount--;
				}
				// variable to check if a piece was captured so it can do a check for a
				// multicapture later
				cap = true;
				checkWin();
				// removes piece from board when it is captured
				pieceBoard[Math.min(x, xn) + 1][Math.min(y, yn) + 1] = new Checker();
				gameBoard[Math.min(x, xn) + 1][Math.min(y, yn)
						+ 1] = pieceBoard[Math.min(x, xn) + 1][Math.min(y, yn) + 1].getLabel();
				this.pcs.firePropertyChange("kill",null,new int[] {Math.min(x,xn)+1,Math.min(y,yn)+1});
			} else if (possibleCap(x, y)) {
				return "If there is a piece you can capture you must capture it";
			}
			// sets old spot to an empty object and puts the checker in the new spot
			pieceBoard[xn][yn] = pieceBoard[x][y];
			pieceBoard[xn][yn].setPos(xn, yn);
			pieceBoard[x][y] = new Checker();
			checkKing(xn, yn);
			gameBoard[x][y] = pieceBoard[x][y].getLabel();
			gameBoard[xn][yn] = pieceBoard[xn][yn].getLabel();
			this.pcs.firePropertyChange("move",new int[] {x,y} ,new int[] {xn,yn});
			// checks if the piece can do a multicapture after capturing a previous piece
			if (possibleCap(xn, yn) && cap) {
				multiCap(xn, yn);
			} else {
				player = !player;
				this.pcs.firePropertyChange("turn", null ,player);
				return "pass";
			}
			return "pass";
		}
		// fails if the move inputed is not possible
		return "Illegal Move";
	}
	
	/**
	 * Gets if the game is won.
	 *
	 * @return true, if the game was won
	 */
	public boolean getWon() {
		return won;
	}

	/**
	 * Gets the string at the spot.
	 *
	 * @param x The x of the spot
	 * @param y The y of the spot
	 * @return String of the spot on the display board
	 */
	public String getSpot(int x, int y) {
		return gameBoard[x][y];
	}
	
	public boolean getSpotBool(int x, int y) throws Exception{
		return pieceBoard[x][y].getTeamBool();
	}

	/**
	 * Gets if the checker at that spot is a king.
	 *
	 * @param x The x of the spot being checked
	 * @param y The y of the spot being checked
	 * @return true, if it is a king
	 */
	public boolean getCheckKing(int x, int y) {
		return pieceBoard[x][y].getKing();
	}

	/**
	 * Gets the winner.
	 *
	 * @return the winner as a string
	 */
	public String getWinner() {
		if (won) {
			if (xCount > oCount) {
				return "X";
			} else {
				return "O";
			}
		}
		return "";
	}
	
	public Checker[][] getPieceBoard(){
		return pieceBoard;
	}

	/**
	 * Checks if it is possible to cap.
	 *
	 * @param x The x of the spot
	 * @param y The y of the spot
	 * @return true, if successful
	 */
//checks if there is a possible capture to make and forces the player to make it if so
//takes in the location of the piece to be moved
	public boolean possibleCap(int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7 ) {
			return false;
		}
		// checks in every direction the piece is able to move
		if (player || getCheckKing(x, y)) {
			try {
				// this if statement works because if it tries to get the team bool of an
				// empty spot it throws an exception and breaks out of the statement
				if (pieceBoard[x + 1][y + 1].getTeamBool() != player) {
					// each of these lines is for a different diagonal direction from the piece to
					// check if it can cap there
					if ((x + 2) < 8 && (y + 2) < 8 && pieceBoard[x + 2][y + 2].getTeam()=='n') {
						return true;
					}
				}
			} catch (Exception e) {
			}
			try {
				if (pieceBoard[x + 1][y - 1].getTeamBool() != player) {
					if ((x + 2) < 8 && (y - 2) > -1 && pieceBoard[x + 2][y - 2].getTeam()=='n') {
						return true;
					}
				}
			} catch (Exception e) {
			}
		}
		if (!player || getCheckKing(x, y)) {
			try {
				if (pieceBoard[x - 1][y + 1].getTeamBool() != player) {
					if ((x - 2) > -1 && (y + 2) < 8 && pieceBoard[x - 2][y + 2].getTeam()=='n') {
						return true;
					}
				}
			} catch (Exception e) {
			}
			try {
				if (pieceBoard[x - 1][y - 1].getTeamBool() != player) {
					if ((x - 2) > -1 && (y - 2) > -1 && pieceBoard[x - 2][y - 2].getTeam()=='n') {
						return true;
					}
				}
			} catch (Exception e) {
			}
		}
		return false;

	}
	
	
	/**
	 * Multicapture function that allows for a double move when a move is made that
	 * opens the player up to capturing another piece immediately.
	 *
	 * @param x the x
	 * @param y the y
	 */
	
	//simply repeated code
//caps another piece if there are multiple in a row
	public void multiCap(int x, int y) {
		if (player || getCheckKing(x, y)) {
			try {
				// this if statement works because if it tries to get the team bool of an
				// empty spot it throws an exception and breaks out of the statement
				if (pieceBoard[x + 1][y + 1].getTeamBool() != player) {
					// same checks as the possible cap function
					if ((x + 2) < 8 && (y + 2) < 8 && pieceBoard[x + 2][y + 2].getTeam()=='n') {
						System.out.println("Multi-Capture!");
						// makes the move for the player because they would be forced to anyways
						makeMove(x, y, x + 2, y + 2);
					}
				}
			} catch (Exception e) { //Do something with the exception
			}
			try {
				if (pieceBoard[x + 1][y - 1].getTeamBool() != player) {
					if ((x + 2) < 8 && (y - 2) > -1 && pieceBoard[x + 2][y - 2].getTeam()=='n') {
						System.out.println("Multi-Capture!");
						makeMove(x, y, x + 2, y - 2);
					}
				}
			} catch (Exception e) {
			}
		}
		if (!player || getCheckKing(x, y)) {
			try {
				if (pieceBoard[x - 1][y + 1].getTeamBool() != player) {
					if ((x - 2) > -1 && (y + 2) < 8 && pieceBoard[x - 2][y + 2].getTeam()=='n') {
						System.out.println("Multi-Capture!");
						makeMove(x, y, x - 2, y + 2);
					}
				}
			} catch (Exception e) {
			}
			try {
				if (pieceBoard[x - 1][y - 1].getTeamBool() != player) {
					if ((x - 2) > -1 && (y - 2) > -1 && pieceBoard[x - 2][y - 2].getTeam()=='n') {
						System.out.println("Multi-Capture!");
						makeMove(x, y, x - 2, y - 2);
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	public int getXCount() {
		return xCount;
	}
	protected Object clone() throws CloneNotSupportedException{
		Board temp=new Board();
		try {
			temp=new Board();
			temp.gameBoard=new String[8][8];
			temp.pieceBoard=new Checker[8][8];
			temp.xCount=this.xCount;
			temp.oCount=this.oCount;
			temp.player=this.player;
			temp.won=this.won;
			for (int i = 0; i < 8; i++) {
	            for (int j = 0; j < 8; j++) {
	                temp.pieceBoard[i][j] = (Checker) this.pieceBoard[i][j].clone();
	            }
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
}
