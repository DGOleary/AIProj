package checker;

/**
 * @author Darby Oleary
 *
 *         Class that represents a spot on a checker board, being empty or
 *         having a piece on it
 */
public class Checker {
	private boolean king;
	private char team;
	private int x;
	private int y;
	private String label;

	/**
	 * Default constructor that sets up an empty spot on the board
	 */
	public Checker() {
		x = -1;
		y = -1;
		king = false;
		team = 'n';
		label = "|_|";
	}

	/**
	 * Constructor that sets a spot to a checker piece
	 * 
	 * @param t The team the piece is on
	 * @param x The row it is in
	 * @param y The column it is in
	 */
	public Checker(boolean t, int x, int y) {
		this.x = x;
		this.y = y;
		king = false;
		if (t) {
			team = 'x';
			label = "|x|";
		} else {
			team = 'o';
			label = "|o|";
		}
	}

	public boolean getKing() {
		return king;
	}

	public int[] getPos() {
		return new int[] { x, y };
	}

	/**
	 * Makes this piece a king.
	 */
	public void makeKing() {
		king = true;
		if (team == 'x') {
			label = "|X|";
		} else {
			label = "|O|";
		}
	}

	public char getTeam() {
		return team;
	}

	/**
	 * Gets the team as a boolean.
	 *
	 * @return The team as a boolean
	 * @throws Exception when this spot has no piece on it so it has no team
	 */
	public boolean getTeamBool() throws Exception {
		if (team == 'x') {
			return true;
		} else if (team == 'o') {
			return false;
		} else {
			throw new Exception("no team");
		}
	}

	/**
	 * Sets the position.
	 *
	 * @param x The row
	 * @param y The coumn
	 */
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the label to print to the board.
	 *
	 * @return The label
	 */
	public String getLabel() {
		return label;
	}

}
