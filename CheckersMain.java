package checker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Darby Oleary
 * 
 *         The Class CheckersMain that functions as the model of the game.
 */
public class CheckersMain {

	// player true is black/x, player false is red/o
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/** The gameboard. */
	private Board board;

	/**
	 * Main function that will run the game.
	 */
	public CheckersMain() {
		board = new Board();
	}

	/**
	 * Makes move given by player, checks for winners, and makes sure the move is
	 * valid.
	 *
	 * @param px the initial x position
	 * @param py the initial y position
	 * @param sx the second x position
	 * @param sy the second x position
	 * @return true, if successful
	 */
	public boolean makeMove(int px, int py, int sx, int sy) {
		if (!board.getWon()) {
			try {
				// checks for the correct team playing
				if (board.getPlayer() != board.getSpotBool(px, py)) {
					this.pcs.firePropertyChange("error", null, "Pick a piece that is your color");
					return false;
				}
			} catch (Exception e) {
				this.pcs.firePropertyChange("error", null, "Pick a piece that is your color");
				return false;
			}
			if (!moveCheck(px, py)) {
				return false;
			}

			// takes in error message or message that the move passed
			String move = board.makeMove(px, py, sx, sy);
			if (!move.equals("pass")) {
				this.pcs.firePropertyChange("error", null, move);
				return false;
			}

		}
		return true;
	}

	/**
	 * Checks to make sure the move inputed is valid by the standards of the game.
	 *
	 * @param m The string of the move
	 * @return true, if successful
	 */
	public boolean moveCheck(int x, int y) {
		if (x < 0 || y < 0) {
			this.pcs.firePropertyChange("error", null, "Incorrect move, pick a piece before picking a spot to move to");
			return false;
		}
		if (board.getPlayer()) {
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

	private boolean possibleMoves(int x, int y) {
		// code repeats because the king and normal moves need to be separate so it
		// would need two loops, and only 4 moves are made so it's more simple to just
		// hardcode each possible move
		try {
			if (board.getSpotBool(x, y) || board.getCheckKing(x, y)) {
				if (board.validMove(x, y, x + 1, y + 1)) {
					return true;
				}
				if (board.validMove(x, y, x + 1, y - 1)) {
					return true;
				}
			}
		} catch (Exception e1) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (!board.getSpotBool(x, y) || board.getCheckKing(x, y)) {
				if (board.validMove(x, y, x - 1, y + 1)) {
					return true;
				}
				if (board.validMove(x, y, x - 1, y - 1)) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		return false;
	}

	/**
	 * Gets if the piece at that spot is a king.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if king
	 */
	public boolean kingGet(int x, int y) {
		return board.getCheckKing(x, y);
	}

	/**
	 * Returns what team the piece at the spot is.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if x, false if o or not a spot
	 */
	public boolean teamGet(int x, int y) {
		try {
			return board.getSpotBool(x, y);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return returns a pointer to the board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @return returns the checker at that spot
	 */
	public boolean getPlayer() {
		return board.getPlayer();
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
