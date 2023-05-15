package checker;

import java.util.Stack;

/**
 * @author Darby Oleary
 * 
 *         The AI for the checkers game, works by checking every piece for the
 *         best moves, and then going 4 turns ahead to attempt to determine what
 *         move will be the best.
 */
public class CheckersAI {
	// value of the move
	/** The util of the current move being tested. */
	// set to -1 so first value is always higher
	int util = -1;

	/** The initial util of the first moves. */
	int[] initUtil = new int[] { -1, -1, -1 };

	/** The max util, util used to make real move. */
	int[] maxUtil;// =new int[3];

	/** The moves, array to hold the moves that will be made for the best util. */
	int[] moves = new int[4];

	/** The init moves, array of moves for first move. */
	int[][] initMoves = new int[3][4];

	/** The board. */
	private Board board[];

	/** Variable to hold pointer to the main model class. */
	private CheckersMain main;

	/**
	 * Ai move, the method called to make an AI move, it uses a minmax method to
	 * determine what move to use.
	 *
	 * @param main pointer to the model
	 */
	public void aiMove(CheckersMain main) {
		// resets trackers of moves
		maxUtil = new int[] { -1, -1, -1 };
		initUtil = new int[] { -1, -1, -1 };
		initMoves = new int[3][4];
		board = new Board[3];
		this.main = main;
		// clones boards to test methods on
		try {
			board[0] = (Board) main.getBoard().clone();
			board[1] = (Board) main.getBoard().clone();
			board[2] = (Board) main.getBoard().clone();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		// generates the initial moves and saves them so the best one can be used after
		// testing
		generateInitialMoves();

		// makes 3 different boards of the best 3 initial moves
		board[0].makeMove(initMoves[0][0], initMoves[0][1], initMoves[0][2], initMoves[0][3]);
		board[1].makeMove(initMoves[1][0], initMoves[1][1], initMoves[1][2], initMoves[1][3]);
		board[2].makeMove(initMoves[2][0], initMoves[2][1], initMoves[2][2], initMoves[2][3]);
		maxUtil = initUtil;
		for (int i = 0; i < 12; i++) {
			// mods by 3 to have i go to the correct board but run each board 3 times
			generateMoves(i % 3);
		}

		// finds the best initial move by the final util
		int maxInd = 0, maxVal = -200;
		for (int i = 0; i < 3; i++) {
			if (maxVal < maxUtil[i]) {
				maxVal = maxUtil[i];
				maxInd = i;
			}
		}
		// makes move
		main.makeMove(initMoves[maxInd][0], initMoves[maxInd][1], initMoves[maxInd][2], initMoves[maxInd][3]);
	}

	/**
	 * Checks if the util being tracked should be updated to a better value.
	 *
	 * @param newUtil the new util being compared to the current
	 * @param x       the x of the piece
	 * @param y       the y of the piece
	 * @param xn      the new x of the piece
	 * @param yn      the new y of the piece
	 */
	private void checkUtil(int newUtil, int x, int y, int xn, int yn) {
		if (newUtil > util) {
			if (moves[0] == x && moves[1] == y && moves[2] == xn && moves[3] == yn) {
				return;
			}
			util = newUtil;
			moves = new int[] { x, y, xn, yn };
		}
	}

	/**
	 * Checks the move to make sure it is a valid move.
	 *
	 * @param b  the b
	 * @param px the x of the piece
	 * @param py the y of the piece
	 * @param sx the new x of the piece
	 * @param sy the new y of the piece
	 * @return -1 if an incorrect move, the util of a correct move otherwise
	 */
	public int moveCheck(int b, int px, int py, int sx, int sy) {

		try {
			// checks for the correct team playing
			if (board[b].getPlayer() != board[b].getSpotBool(px, py)) {
				return -1;
			}
		} catch (Exception e) {
			// if it's not a spot with a player on it it's incorrect
			return -1;
		}
		if (!validCheck(b, px, py)) {
			return -1;
		}

		// takes in error message or message that the move passed
		return testMove(b, px, py, sx, sy);

	}

	/**
	 * Looks through possible moves the piece can make and returns the best move, or
	 * a capture if it can capture because a piece that can capture is forced to.
	 *
	 * @param b  the board being updated
	 * @param x  the x of the piece
	 * @param y  the y of the piece
	 * @param xn the new x of the piece
	 * @param yn the new y of the piece
	 * @return util of the move
	 */
	// AI can use this to test a move
	public int testMove(int b, int x, int y, int xn, int yn) {
		if (board[b].validMove(x, y, xn, yn)) {
			if (board[b].checkCap(x, y, xn, yn)) {
				if (multiCap(b, xn, yn)) {
					if (board[b].getPlayer() && board[b].getOCount() <= 3) {
						return 7;
					} else if (!board[b].getPlayer() && board[b].getXCount() <= 3) {
						return 7;
					}
					// 5 is a multicap
					return 5;
				}
				if (board[b].getPlayer() && board[b].getOCount() <= 3) {
					return 5;
				} else if (!board[b].getPlayer() && board[b].getXCount() <= 3) {
					return 5;
				}
				// 3 is a capture
				return 3;
			}
			if (possibleCapped(b, x, y, xn, yn)) {
				// 0 is a move that gets captured but it's a 0 so it will be chosen if there are
				// no other possible moves to make
				return 0;
			}

			if (possibleCapped(b, x, y) && !possibleCapped(b, x, y, xn, yn)) {
				// 4 is a move that evades a capture
				return 4;
			}
			if (!board[b].getCheckKing(x, y)) {
				// helps lead pieces to the back row to get kinged
				try {
					if (!board[b].getSpotBool(x, y) && xn > x) {
						return 2;
					} else if (board[b].getSpotBool(x, y) && xn < x) {
						return 2;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
			if (x != 3 || x != 4) {
				// checks it's not already on one of those spots so it wont loop
				if (xn == 3 || xn == 4) {
					// 2 is a move that helps control an important position
					return 2;
				}
			}
			if (board[b].getCheckKing(x, y)) {
				// defending the back row is a better move than a regular move
				try {
					if (!board[b].getSpotBool(x, y) && xn > x) {
						return 2;
					} else if (board[b].getSpotBool(x, y) && xn < x) {
						return 2;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
			// 1 is a regular move
			return 1;
		}
		// -1 is an illegal move
		return -1;

	}

	/**
	 * Tests if a piece, when moved, will be in a position where it can be captured.
	 *
	 * @param b  the board being updated
	 * @param x  the x of the piece
	 * @param y  the y of the piece
	 * @param xn the new x of the piece
	 * @param yn the new y of the piece
	 * @return true, if it can be captured if it is moved to that position
	 */
	public boolean possibleCapped(int b, int x, int y, int xn, int yn) {
		// checks if its a capture move and exits if it is
		if (Math.abs(x - xn) == 2) {
			return false;
		}
		if (x < 0 || x > 7 || y < 0 || y > 7 || xn < 0 || xn > 7 || yn < 0 || yn > 7) {
			return false;
		}
		// checks if the pieces around it are able to cap the piece
		char enemyTeam;
		int offset;
		if (board[b].getPlayer()) {
			enemyTeam = 'o';
			offset = -1;
		} else {
			enemyTeam = 'x';
			offset = 1;
		}
		// code repeats because the king and normal moves need to be separate so it
		// would need two loops, and only 4 moves are made so it's more simple to just
		// hardcode each possible move
		try {
			if (board[b].getPieceBoard()[xn + offset][yn - offset].getTeam() == enemyTeam
					&& board[b].getPieceBoard()[xn + offset][yn - offset].getKing()) {
				// checks to make sure the points are on the same diagonal
				if ((Math.abs((xn + offset) - x) == Math.abs((yn - offset) - y))
						|| board[b].getPieceBoard()[xn - offset][yn + offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (board[b].getPieceBoard()[xn + offset][yn + offset].getTeam() == enemyTeam
					& board[b].getPieceBoard()[xn + offset][yn + offset].getKing()) {
				if ((Math.abs((xn + offset) - x) == Math.abs((yn + offset) - y))
						|| board[b].getPieceBoard()[xn - offset][yn - offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (board[b].getPieceBoard()[xn - offset][yn - offset].getTeam() == enemyTeam) {
				// if the enemy piece is not a king then it must be coming from a direction it
				// can capture in
				if ((Math.abs((xn - offset) - x) == Math.abs((yn - offset) - y))
						|| board[b].getPieceBoard()[xn + offset][yn + offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (board[b].getPieceBoard()[xn - offset][yn + offset].getTeam() == enemyTeam) {
				if ((Math.abs((xn - offset) - x) == Math.abs((yn + offset) - y))
						|| board[b].getPieceBoard()[xn + offset][yn - offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		return false;
	}

	/**
	 * If the piece at this spot currently can be captured.
	 *
	 * @param b the board being updated
	 * @param x the x of the piece
	 * @param y the y of the piece
	 * @return true, if it will be captured
	 */
	public boolean possibleCapped(int b, int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		char enemyTeam;
		int offset;
		if (board[b].getPlayer()) {
			enemyTeam = 'o';
			offset = 1;
		} else {
			enemyTeam = 'x';
			offset = -1;
		}
		// code repeats because the king and normal moves need to be separate so it
		// would need two loops, and only 4 moves are made so it's more simple to just
		// hardcode each possible move

		// checks if the pieces around it are able to cap the piece
		try {
			if (board[b].getPieceBoard()[x - offset][y - offset].getTeam() == enemyTeam) {
				if (board[b].getPieceBoard()[x + offset][y + offset].getTeam() == 'n') {
					return true;
				}
				if (board[b].getPieceBoard()[x - offset][y + offset].getTeam() == enemyTeam
						&& board[b].getPieceBoard()[x + offset][y - offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}

		try {
			if (board[b].getPieceBoard()[x - offset][y + offset].getTeam() == enemyTeam) {
				if (board[b].getPieceBoard()[x + offset][y - offset].getTeam() == 'n') {
					return true;
				}
				if (board[b].getPieceBoard()[x - offset][y - offset].getTeam() == enemyTeam
						&& board[b].getPieceBoard()[x + offset][y + offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (board[b].getPieceBoard()[x + offset][y + offset].getTeam() == enemyTeam
					&& board[b].getPieceBoard()[x + offset][y + offset].getKing()) {
				if (board[b].getPieceBoard()[x - offset][y - offset].getTeam() == 'n') {
					return true;
				}
				if (board[b].getPieceBoard()[x + offset][y - offset].getTeam() == enemyTeam
						&& board[b].getPieceBoard()[x - offset][y + offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (board[b].getPieceBoard()[x + offset][y - offset].getTeam() == enemyTeam
					&& board[b].getPieceBoard()[x + offset][y + offset].getKing()) {
				if (board[b].getPieceBoard()[x - offset][y + offset].getTeam() == 'n') {
					return true;
				}
				if (board[b].getPieceBoard()[x + offset][y + offset].getTeam() == enemyTeam
						&& board[b].getPieceBoard()[x - offset][y - offset].getTeam() == 'n') {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		return false;
	}

	/**
	 * Checks if the given spot is valid for moves to be attempted there.
	 *
	 * @param b the board being updated
	 * @param x the x of the piece
	 * @param y the y of the piece
	 * @return true, if successful
	 */
	public boolean validCheck(int b, int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		if (board[b].getPlayer()) {
			try {
				if (!board[b].getSpotBool(x, y)) {
					return false;
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
		} else {
			try {
				if (board[b].getSpotBool(x, y)) {
					return false;
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
		}
		try {
			// empty spot check
			if (board[b].getSpotBool(x, y) != true && board[b].getSpotBool(x, y) != false) {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
			return false;
		}

		if (!possibleMoves(b, x, y) && !board[b].possibleCap(x, y)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if a multicapture can be made with the piece on the given spot.
	 *
	 * @param b the board being updated
	 * @param x the x of the piece
	 * @param y the y of the piece
	 * @return true, if a multicapture can be made
	 */

	// code repeats because the king and normal moves need to be separate so it
	// would need two loops, and only 4 moves are made so it's more simple to just
	// hardcode each possible move

	// caps another piece if there are multiple in a row
	public boolean multiCap(int b, int x, int y) {
		if (board[b].getPlayer() || board[b].getCheckKing(x, y)) {
			try {
				// this if statement works because if it tries to get the team bool of an
				// empty spot it throws an exception and breaks out of the statement
				if (board[b].getSpotBool(x + 1, y + 1) != board[b].getPlayer()) {
					// same checks as the possible cap function
					if ((x + 2) < 8 && (y + 2) < 8 && board[b].getPieceBoard()[x + 2][y + 2].getTeam() == 'n') {
						return true;
					}
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
			try {
				if (board[b].getSpotBool(x + 1, y - 1) != board[b].getPlayer()) {
					if ((x + 2) < 8 && (y - 2) > -1 && board[b].getPieceBoard()[x + 2][y - 2].getTeam() == 'n') {
						return true;
					}
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
		}
		if (!board[b].getPlayer() || board[b].getCheckKing(x, y)) {
			try {
				if (board[b].getSpotBool(x - 1, y + 1) != board[b].getPlayer()) {
					if ((x - 2) > -1 && (y + 2) < 8 && board[b].getPieceBoard()[x - 2][y + 2].getTeam() == 'n') {
						return true;
					}
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
			try {
				if (board[b].getSpotBool(x - 1, y - 1) != board[b].getPlayer()) {
					if ((x - 2) > -1 && (y - 2) > -1 && board[b].getPieceBoard()[x - 2][y - 2].getTeam() == 'n') {
						return true;
					}
				}
			} catch (Exception e) {
				System.out.println("Ilegal move attempted Exception");
			}
		}
		return false;
	}

	/**
	 * Checks if the piece has any possible moves.
	 *
	 * @param b the board being updated
	 * @param x the x of the piece
	 * @param y the y of the piece
	 * @return true, if successful
	 */
	boolean possibleMoves(int b, int x, int y) {
		// code repeats because the king and normal moves need to be separate so it
		// would need two loops, and only 4 moves are made so it's more simple to just
		// hardcode each possible move
		try {
			if (board[b].getSpotBool(x, y) || board[b].getCheckKing(x, y)) {
				try {
					if (board[b].validMove(x, y, x + 1, y + 1)) {
						return true;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
				try {
					if (board[b].validMove(x, y, x + 1, y - 1)) {
						return true;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
		} catch (Exception e1) {
			System.out.println("Ilegal move attempted Exception");
		}
		try {
			if (!board[b].getSpotBool(x, y) || board[b].getCheckKing(x, y)) {
				try {
					if (board[b].validMove(x, y, x - 1, y + 1)) {
						return true;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
				try {
					if (board[b].validMove(x, y, x - 1, y - 1)) {
						return true;
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
		} catch (Exception e) {
			System.out.println("Ilegal move attempted Exception");
		}
		return false;
	}

	/**
	 * Generates minmax moves to determine which move would lead to the best path.
	 *
	 * @param k the index of the board and util being updated
	 */
	private void generateMoves(int k) {

		Stack<int[]> pieces = new Stack<int[]>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				try {
					if (board[k].getSpotBool(i, j) == board[k].getPlayer()) {
						pieces.push(new int[] { i, j });
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
		}

		while (!pieces.isEmpty()) {
			int spot[] = pieces.pop();
			int x = spot[0];
			int y = spot[1];
			int offset;
			int capOffset;
			if (board[k].getPlayer()) {
				offset = 1;
				capOffset = 2;
			} else {
				offset = -1;
				capOffset = -2;
			}
			if (board[k].getCheckKing(x, y)) {

				// checks caps only the king can make
				checkUtil(moveCheck(k, x, y, x + capOffset, y + capOffset), x, y, x + capOffset, y + capOffset);
				checkUtil(moveCheck(k, x, y, x + capOffset, y - capOffset), x, y, x + capOffset, y - capOffset);
				// all moves have to be checked when it is a king otherwise the
				// king's regular moves could override a capture
				checkUtil(moveCheck(k, x, y, x - capOffset, y - capOffset), x, y, x - capOffset, y - capOffset);
				checkUtil(moveCheck(k, x, y, x - capOffset, y + capOffset), x, y, x - capOffset, y + capOffset);
				// if a piece can capture it must by the rules or the game freezes
				// so this makes sure if there is a possible cap it will be chosen
				if (util == 3 || util == 5 || util == 7) {
					continue;
				}
				// checks king moves
				checkUtil(moveCheck(k, x, y, x + offset, y + offset), x, y, x + offset, y + offset);
				checkUtil(moveCheck(k, x, y, x + offset, y - offset), x, y, x + offset, y - offset);
				checkUtil(moveCheck(k, x, y, x - offset, y - offset), x, y, x - offset, y - offset);
				checkUtil(moveCheck(k, x, y, x - offset, y + offset), x, y, x - offset, y + offset);
			} else {
				// checks caps all pieces can make
				checkUtil(moveCheck(k, x, y, x + capOffset, y - capOffset), x, y, x + capOffset, y - capOffset);
				checkUtil(moveCheck(k, x, y, x + capOffset, y + capOffset), x, y, x + capOffset, y + capOffset);
				// same as above
				if (util == 3 || util == 5 || util == 7) {
					continue;
				}
				// checks moves all pieces can make
				checkUtil(moveCheck(k, x, y, x + offset, y - offset), x, y, x + offset, y - offset);
				checkUtil(moveCheck(k, x, y, x + offset, y + offset), x, y, x + offset, y + offset);
			}
		}
		// adds util to total
		if (board[k].getPlayer()) {
			if (util < 0) {
				maxUtil[k] += util;
			} else {
				maxUtil[k] -= util;
			}
		} else {
			maxUtil[k] += util;
		}
		board[k].makeMove(moves[0], moves[1], moves[2], moves[3]);
		// resets the util
		util = -1;
		moves = new int[4];

	}

	/**
	 * Compares the current util for the given for the first set of moves.
	 *
	 * @param newUtil the new util to compare to the old one
	 * @param x       the x of the piece
	 * @param y       the y of the piece
	 * @param xn      the new x of the piece
	 * @param yn      the new y of the piece
	 * @return true, if a capture move was made
	 */
	private boolean checkInitialUtil(int newUtil, int x, int y, int xn, int yn) {
		for (int i = 0; i < 3; i++) {
			if (newUtil > initUtil[i]) {
				// if it tries to do the exact same move exclude it from the list of possible
				// moves because it's a duplicate
				if (initMoves[i][0] == x && initMoves[i][1] == y && initMoves[i][2] == xn && initMoves[i][3] == yn) {
					return false;
				}
				for (int j = 2 - i; j > 0; j--) {
					initUtil[j] = initUtil[j - 1];
					initMoves[j] = initMoves[j - 1];
				}
				initUtil[i] = newUtil;
				initMoves[i] = new int[] { x, y, xn, yn };
				// return true to say that it was a capture move because it must be made
				if (newUtil == 3 || newUtil == 5 || util == 7) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Generates initial moves, does this differently than the other generate
	 * function because the initial moves must be saved and kept separate so they
	 * can be chosen when a move is sent to the model.
	 */
	private void generateInitialMoves() {

		Stack<int[]> pieces = new Stack<int[]>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				try {
					if (board[0].getSpotBool(i, j) == board[0].getPlayer()) {
						pieces.push(new int[] { i, j });
					}
				} catch (Exception e) {
					System.out.println("Ilegal move attempted Exception");
				}
			}
		}

		while (!pieces.isEmpty()) {
			int spot[] = pieces.pop();
			int x = spot[0];
			int y = spot[1];
			int offset;
			int capOffset;
			// boolean to check if a capture was made which will override any other move
			// because it must capture when possible
			boolean capped = false;
			if (board[0].getPlayer()) {
				offset = 1;
				capOffset = 2;
			} else {
				offset = -1;
				capOffset = -2;
			}
			if (board[0].getCheckKing(x, y)) {

				// checks caps only the king can make
				capped = checkInitialUtil(moveCheck(0, x, y, x + capOffset, y + capOffset), x, y, x + capOffset,
						y + capOffset);
				capped = checkInitialUtil(moveCheck(0, x, y, x + capOffset, y - capOffset), x, y, x + capOffset,
						y - capOffset);
				// all moves have to be checked when it is a king otherwise the
				// king's regular moves could override a capture
				capped = checkInitialUtil(moveCheck(0, x, y, x - capOffset, y - capOffset), x, y, x - capOffset,
						y - capOffset);
				capped = checkInitialUtil(moveCheck(0, x, y, x - capOffset, y + capOffset), x, y, x - capOffset,
						y + capOffset);
				// if a piece can capture it must by the rules or the game freezes
				// so this makes sure if there is a possible cap it will be chosen
				if (capped) {
					continue;
				}
				// checks king moves
				checkInitialUtil(moveCheck(0, x, y, x + offset, y + offset), x, y, x + offset, y + offset);
				checkInitialUtil(moveCheck(0, x, y, x + offset, y - offset), x, y, x + offset, y - offset);
				checkInitialUtil(moveCheck(0, x, y, x - offset, y - offset), x, y, x - offset, y - offset);
				checkInitialUtil(moveCheck(0, x, y, x - offset, y + offset), x, y, x - offset, y + offset);
			} else {
				// checks caps all pieces can make
				capped = checkInitialUtil(moveCheck(0, x, y, x + capOffset, y - capOffset), x, y, x + capOffset,
						y - capOffset);
				capped = checkInitialUtil(moveCheck(0, x, y, x + capOffset, y + capOffset), x, y, x + capOffset,
						y + capOffset);
				// same as above
				if (capped) {
					continue;
				}
				// checks moves all pieces can make
				checkInitialUtil(moveCheck(0, x, y, x + offset, y - offset), x, y, x + offset, y - offset);
				checkInitialUtil(moveCheck(0, x, y, x + offset, y + offset), x, y, x + offset, y + offset);
			}
		}

	}
}
