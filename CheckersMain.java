package checker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
/**
 * The Class CheckersMain that functions as the model of the game.
 */
//Model class for the game, it just prints out the statements currently but when the GUI is implemented it will instead send messages to the view to update the display
public class CheckersMain {

	//player true is black/x, player false is red/o
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/** The gameboard. */
	private Board board;

	/**
	 * Main function that will run the game.
	 */
	public void runGame() {
		board = new Board();
		//System.out.println("Word your moves as Letter, then Number of the spot, Example: A1\n");
		this.pcs.firePropertyChange("instruction_alert", null, "Word your moves as Letter, then Number of the spot, Example: A1\n");
	}

	public void makeMove(int px, int py, int sx, int sy) {
		board.printBoard();
		if (board.getWon()) {
			String winner="Red";
			if(board.getWinner().equals("X")) {
				winner="Black";
			}
			this.pcs.firePropertyChange("label", null, winner+" Wins!");
		}else {
			//board.printBoard();
			//System.out.print("Current Player: ");
			try {
			if (board.getPlayer()!=board.getSpotBool(px,py)) {
				this.pcs.firePropertyChange("error", null, "Pick a piece that is your color");
				return;
			} 
			}catch (Exception e) {
				this.pcs.firePropertyChange("error", null, "Pick a piece that is your color");
				return;
			}
			if (!moveCheck(px, py)) {
				return;
			}
			
			//takes in error message or message that the move passed
			String move=board.makeMove(px, py, sx, sy);
			if(!move.equals("pass")) {
				System.out.println(move);
				this.pcs.firePropertyChange("error", null, move);
			}
			
		}
	}
	/**
	 * Checks to make sure the move inputed is valid by the standards of the game.
	 *
	 * @param m The string of the move
	 * @return true, if successful
	 */
	private boolean moveCheck(int x, int y) {
		if (x<0||y<0) {
				this.pcs.firePropertyChange("error", null, "Incorrect move, pick a piece before picking a spot to move to");
				return false;
		}if (board.getPlayer()) {
			if (board.getSpot(x, y).equals("|o|") || board.getSpot(x, y).equals("|O|")) {
				this.pcs.firePropertyChange("error", null, "Incorrect move, you are playing as Black");
				return false;
			}
		} else {
			if (board.getSpot(x, y).equals("|x|") || board.getSpot(x, y).equals("|X|")) {
				this.pcs.firePropertyChange("error", null, "Incorrect move, you are playing as Red");
				return false;
			}
		}
		if (board.getSpot(x, y).equals("|_|")) {
			this.pcs.firePropertyChange("error", null, "Incorrect move, no piece is in that spot");
			return false;
		}

		if (!possibleMoves(x, y) && !board.possibleCap(x, y)) {
			this.pcs.firePropertyChange("error", null, "Incorrect move, no possible moves");
			return false;
		}
		return true;
	}

	/**
	 * Helper method to make sure a piece being picked has at least one possible
	 * move it can make so the game will not be softlocked by picking a piece that
	 * can't move.
	 *
	 * @param x The x of the piece
	 * @param y The y of the piece
	 * @return true, if successful
	 */
	boolean possibleMoves(int x, int y) {
		if (board.getPlayer() || board.getCheckKing(x, y)) {
			try {
				if (board.validMove(x, y, x + 1, y + 1)) {
					return true;
				}
			} catch (Exception e) {
			}
			try {
				if (board.validMove(x, y, x + 1, y - 1)) {
					return true;
				}
			} catch (Exception e) {
			}
		}
		if (!board.getPlayer() || board.getCheckKing(x, y)) {
			try {
				if (board.validMove(x, y, x - 1, y + 1)) {
					return true;
				}
			} catch (Exception e) {
			}
			try {
				if (board.validMove(x, y, x - 1, y - 1)) {
					return true;
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	public boolean kingGet(int x,int y) {
		return board.getCheckKing(x,y);
	}
	
	public boolean teamGet(int x,int y) {
		try {
		return board.getSpotBool(x,y);
		} catch(Exception e) {
			return false;
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
	/**
	 * @param listener
	 */
	public void addBoardPropertyChangeListener(PropertyChangeListener listener) {
		board.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 */
	public void removeBoardPropertyChangeListener(PropertyChangeListener listener) {
		board.removePropertyChangeListener(listener);
	}

}
