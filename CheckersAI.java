package checker;

import java.util.Stack;

//TODO re-enable error messages and find out why game sometimes freezes
//TODO make the pieces be able to chase or run
//TODO bring AI methods from the board class to this class
//possibly make 0 the incorrect value, and negative values indicate a piece in danger, whichever util has a greater
//abs value will go, also influenced by how many pieces are left on the board
public class CheckersAI {
	//value of the move;
	int[] util=new int[3];
	//util used to make real move
	int[] maxUtil=new int[3];
	//array to hold the moves that will be made for the best util
	int[][] moves=new int[3][4];
	//array of the original moves for the real move
	int[][] maxMoves=new int[3][4];
	private Board board;
	private CheckersMain main;
	public void aiMove(CheckersMain main) {
		this.main=main;
		try {
			board=(Board)main.getBoard().clone();
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//clones boards to test methods on
		for(int k=0;k<3;k++) {
		Stack<int[]> pieces=new Stack<int[]>();
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				try {
					if(board.getSpotBool(i, j)==board.getPlayer()) {
						pieces.push(new int[] {i,j});
					}
				}catch(Exception e) {
					//System.out.println("AI miss");
				}
			}
		}
		
		
		while(!pieces.isEmpty()) {
			int spot[]=pieces.pop();
			int x=spot[0];
			int y=spot[1];
			int offset;
			int capOffset;
			if(board.getPlayer()) {
				offset=1;
				capOffset=2;
			}else {
				offset=-1;
				capOffset=-2;
			}
			if(board.getCheckKing(x, y)) {
				
				//checks caps only the king can make
				checkUtil(moveCheck(k, x, y, x+capOffset, y+capOffset),x, y, x+capOffset, y+capOffset);
				checkUtil(moveCheck(k, x, y, x+capOffset, y-capOffset),x, y, x+capOffset, y-capOffset);
				//all moves have to be checked when it is a king otherwise the 
				//king's regular moves could override a capture
				checkUtil(moveCheck(k, x, y, x-capOffset, y-capOffset),x, y, x-capOffset, y-capOffset);
				checkUtil(moveCheck(k, x, y, x-capOffset, y+capOffset),x, y, x-capOffset, y+capOffset);
				//if a piece can capture it must by the rules or the game freezes
				//so this makes sure if there is a possible cap it will be chosen
				if(util[k]==3) {
					continue;
				}
				//checks king moves
				checkUtil(moveCheck(k, x, y, x+offset, y+offset),x, y, x+offset, y+offset);
				checkUtil(moveCheck(k, x, y, x+offset, y-offset),x, y, x+offset, y-offset);
				checkUtil(moveCheck(k, x, y, x-offset, y-offset),x, y, x-offset, y-offset);
				checkUtil(moveCheck(k, x, y, x-offset, y+offset),x, y, x-offset, y+offset);
			}else {
			//checks caps all pieces can make
			checkUtil(moveCheck(k, x, y, x+capOffset, y-capOffset),x, y, x+capOffset, y-capOffset);
			checkUtil(moveCheck(k, x, y, x+capOffset, y+capOffset),x, y, x+capOffset, y+capOffset);
			//same as above
			if(util[k]==3) {
				continue;
			}
			//checks moves all pieces can make
			checkUtil(moveCheck(k, x, y, x+offset, y-offset),x, y, x+offset, y-offset);
			checkUtil(moveCheck(k, x, y, x+offset, y+offset),x, y, x+offset, y+offset);
		}
		}
		//makes the move that had the highest util
//		System.out.print(moves[0]);
//		System.out.print(moves[1]);
//		System.out.print(moves[2]);
//		System.out.print(moves[3]);
//		System.out.println();
		if(k==0) {
			maxUtil=util.clone();
			maxMoves= moves.clone();
		}else {
			//adds util to total
			if(board.getPlayer()) {
				if(util[k]<0) {
					maxUtil[k]+=util[k];
				}else {
					maxUtil[k]-=util[k];
				}
			}else {
				maxUtil[k]+=util[k];
			}
		}
		board.makeMove(moves[k][0], moves[k][1], moves[k][2], moves[k][3]);
		
		//resets the util
		util=new int[] {-1,-1,-1};
		moves=new int[3][4];
	}
		
		//TODO add code to pick best choice out of all paths
		int maxInd=0, maxVal=-200;
		for(int i=0;i<3;i++) {
			if(maxVal<maxUtil[i]) {
				maxVal=maxUtil[i];
				maxInd=i;
			}
		}
		main.makeMove(maxMoves[maxInd][0], maxMoves[maxInd][1], maxMoves[maxInd][2], maxMoves[maxInd][3]);
	}
	
	private void checkUtil(int newUtil, int x, int y, int xn, int yn) {
		for(int i=0;i<3;i++) {
		if(newUtil>util[i]) {
			util[i]=newUtil;
			moves[i]=new int[] {x,y,xn,yn};
			break;
		}
		}
	}
	
	public int moveCheck(int b, int px, int py, int sx, int sy) {
		
		try {
			//checks for the correct team playing
		if (board.getPlayer()!=board.getSpotBool(px,py)) {
			return -1;
		} 
		}catch (Exception e) {
			return -1;
		}
		if (!validCheck(px, py)) {
			return -1;
		}
		
		//takes in error message or message that the move passed
		return testMove(b, px, py, sx, sy);
		
}
	
	//TODO make pieces avoid picking a spot that will endanger another piece
	
	//move AI methods to AI class
	//AI can use this to test a move
	public int testMove(int b, int x, int y, int xn, int yn) {
		if (board.validMove(x, y, xn, yn)) {
			if (board.checkCap(x, y, xn, yn)) {
				if(multiCap(xn,yn)) {
					//5 is a multicap
					return 5;
				}
				//3 is a capture
				return 3;	
			} 
			if(possibleCapped(b,x,y,xn,yn)) {
				//0 is a move that gets captured but it's a 0 so it will be chosen if there are no other possible moves to make
				return 0;
			}
			if(possibleCapped(b,x,y)&&!possibleCapped(b,x,y,xn,yn)) {
				//4 is a move that evades a capture
				return 4;
			}
			if(x!=3||x!=4) {
				//checks it's not already on one of those spots so it wont loop
				if(xn==3||xn==4) {
					//2 is a move that helps control an important position
					return 2;
				}
			}
			if(board.getCheckKing(x,y)) {
				//defending the back row is a better move than a regular move
				try {
					if(!board.getSpotBool(x, y)&&xn>x) {
						return 2;
					}else if(board.getSpotBool(x, y)&&xn<x) {
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!board.getCheckKing(x,y)) {
				//helps lead pieces to the back row to get kinged
				try {
					if(!board.getSpotBool(x, y)&&xn>x) {
						return 2;
					}else if(board.getSpotBool(x, y)&&xn<x) {
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//1 is a regular move
			return 1;
		}
		//-1 is an illegal move
		return -1;
		
	}
	
	public boolean possibleCapped(int b, int x, int y, int xn, int yn) {
		//checks if its a capture move and exits if it is
		if(Math.abs(x-xn)==2) {
			return false;
		}
		if (x < 0 || x > 7 || y < 0 || y > 7 ||xn < 0 || xn > 7 || yn < 0 || yn > 7 ) {
			return false;
		}
		//checks if the pieces around it are able to cap the piece
		char enemyTeam;
		int offset;
		if(board.getPlayer()) {
			enemyTeam='o';
			offset=-1;
		}else {
			enemyTeam='x';
			offset=1;
		}
		try {
			if(board.getPieceBoard()[xn+offset][yn-offset].getTeam()==enemyTeam&&board.getPieceBoard()[xn+offset][yn-offset].getKing()) {
				//checks to make sure the points are on the same diagonal
				if((Math.abs((xn+offset)-x)==Math.abs((yn-offset)-y))||board.getPieceBoard()[xn-offset][yn+offset].getTeam()=='n') {
					return true;
				}
			}
			}catch(Exception e) {	
			}
		try {
			if(board.getPieceBoard()[xn+offset][yn+offset].getTeam()==enemyTeam&board.getPieceBoard()[xn+offset][yn+offset].getKing()) {
				if((Math.abs((xn+offset)-x)==Math.abs((yn+offset)-y))||board.getPieceBoard()[xn-offset][yn-offset].getTeam()=='n') {
					return true;
				}
			}
			}catch(Exception e) {	
			}
		try {
			if(board.getPieceBoard()[xn-offset][yn-offset].getTeam()==enemyTeam) {
				//if the enemy piece is not a king then it must be coming from a direction it can capture in
				if((Math.abs((xn-offset)-x)==Math.abs((yn-offset)-y))||board.getPieceBoard()[xn+offset][yn+offset].getTeam()=='n') {
					return true;
				}
			}
			}catch(Exception e) {	
			}
		try {
			if(board.getPieceBoard()[xn-offset][yn+offset].getTeam()==enemyTeam) {
				if((Math.abs((xn-offset)-x)==Math.abs((yn+offset)-y))||board.getPieceBoard()[xn+offset][yn-offset].getTeam()=='n') {
					return true;
				}
			}
			}catch(Exception e) {	
			}
		return false;
	}
	
	//method for AI to see if it will be captured
		public boolean possibleCapped(int b, int x, int y) {
			if (x < 0 || x > 7 || y < 0 || y > 7 ) {
				return false;
			}
			char enemyTeam;
			int offset;
			if(board.getPlayer()) {
				enemyTeam='o';
				offset=1;
			}else {
				enemyTeam='x';
				offset=-1;
			}
			//checks if the pieces around it are able to cap the piece
			try {
			if(board.getPieceBoard()[x-offset][y-offset].getTeam()==enemyTeam) {
				if(board.getPieceBoard()[x+offset][y+offset].getTeam()=='n') {
					return true;
				}
				if(board.getPieceBoard()[x-offset][y+offset].getTeam()==enemyTeam&&board.getPieceBoard()[x+offset][y-offset].getTeam()=='n') {
					return true;
				}
			}
			}catch(Exception e) {	
			}
			
			try {
				if(board.getPieceBoard()[x-offset][y+offset].getTeam()==enemyTeam) {
					if(board.getPieceBoard()[x+offset][y-offset].getTeam()=='n') {
						return true;
					}
					if(board.getPieceBoard()[x-offset][y-offset].getTeam()==enemyTeam&&board.getPieceBoard()[x+offset][y+offset].getTeam()=='n') {
						return true;
					}
				}
				}catch(Exception e) {	
				}
			try {
				if(board.getPieceBoard()[x+offset][y+offset].getTeam()==enemyTeam&&board.getPieceBoard()[x+offset][y+offset].getKing()) {
					if(board.getPieceBoard()[x-offset][y-offset].getTeam()=='n') {
						return true;
					}
					if(board.getPieceBoard()[x+offset][y-offset].getTeam()==enemyTeam&&board.getPieceBoard()[x-offset][y+offset].getTeam()=='n') {
						return true;
					}
				}
				}catch(Exception e) {	
				}
			try {
				if(board.getPieceBoard()[x+offset][y-offset].getTeam()==enemyTeam&&board.getPieceBoard()[x+offset][y+offset].getKing()) {
					if(board.getPieceBoard()[x-offset][y+offset].getTeam()=='n') {
						return true;
					}
					if(board.getPieceBoard()[x+offset][y+offset].getTeam()==enemyTeam&&board.getPieceBoard()[x-offset][y-offset].getTeam()=='n') {
						return true;
					}
				}
				}catch(Exception e) {	
				}
			return false;
		}
		
		public boolean validCheck(int x, int y) {
			if (x<0||y<0) {
					return false;
			}if (board.getPlayer()) {
				try {
					if (board.getSpotBool(x, y)) {
						return false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					if (board.getSpotBool(x, y)) {
						return false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				if (board.getSpotBool(x, y)!=true&&board.getSpotBool(x, y)!=false) {
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			if (!main.possibleMoves(x, y) && !board.possibleCap(x, y)) {
				return false;
			}
			return true;
		}
		
		//caps another piece if there are multiple in a row
		public boolean multiCap(int x, int y) {
			if (board.getPlayer() || board.getCheckKing(x, y)) {
				try {
					// this if statement works because if it tries to get the team bool of an
					// empty spot it throws an exception and breaks out of the statement
					if (board.getSpotBool(x+1, y+1)  != board.getPlayer()) {
						// same checks as the possible cap function
						if ((x + 2) < 8 && (y + 2) < 8 && board.getPieceBoard()[x + 2][y + 2].getTeam()=='n') {
							return true;
						}
					}
				} catch (Exception e) { //Do something with the exception
				}
				try {
					if (board.getSpotBool(x+1, y-1) != board.getPlayer()) {
						if ((x + 2) < 8 && (y - 2) > -1 && board.getPieceBoard()[x + 2][y - 2].getTeam()=='n') {
							return true;
						}
					}
				} catch (Exception e) {
				}
			}
			if (!board.getPlayer() ||  board.getCheckKing(x, y)) {
				try {
					if (board.getSpotBool(x-1, y+1) != board.getPlayer()) {
						if ((x - 2) > -1 && (y + 2) < 8 && board.getPieceBoard()[x - 2][y + 2].getTeam()=='n') {
							return true;
						}
					}
				} catch (Exception e) {
				}
				try {
					if (board.getSpotBool(x-1, y-1) != board.getPlayer()) {
						if ((x - 2) > -1 && (y - 2) > -1 && board.getPieceBoard()[x - 2][y - 2].getTeam()=='n') {
							return true;
						}
					}
				} catch (Exception e) {
				}
			}
			return false;
		}

}
