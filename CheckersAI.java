package checker;

import java.util.Stack;


//TODO fix pieces getting stuck between the middle rows when the board is empty
//TODO re-enable error messages and find out why game sometimes freezes
//TODO make the pieces be able to chase or run
//possibly make 0 the incorrect value, and negative values indicate a piece in danger, whichever util has a greater
//abs value will go, also influenced by how many pieces are left on the board
public class CheckersAI {
	//value of the move;
	int util=-1;
	//array to hold the moves that will be made for the best util
	int[] moves=new int[4];
	private Board board;
	private CheckersMain main;
	public void aiMove(CheckersMain main) {
		this.main=main;
		board=main.getBoard();
		Stack<int[]> pieces=new Stack<int[]>();
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				try {
					if(!board.getSpotBool(i, j)) {
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
			if(board.getCheckKing(x, y)) {
				
				//checks caps only the king can make
				checkUtil(main.testMove(x, y, x+2, y+2),x, y, x+2, y+2);
				checkUtil(main.testMove(x, y, x+2, y-2),x, y, x+2, y-2);
				//checks king moves
				checkUtil(main.testMove(x, y, x+1, y+1),x, y, x+1, y+1);
				checkUtil(main.testMove(x, y, x+1, y-1),x, y, x+1, y-1);
			}
			//checks caps all pieces can make
			checkUtil(main.testMove(x, y, x-2, y-2),x, y, x-2, y-2);
			checkUtil(main.testMove(x, y, x-2, y+2),x, y, x-2, y+2);
			//checks moves all pieces can make
			checkUtil(main.testMove(x, y, x-1, y-1),x, y, x-1, y-1);
			checkUtil(main.testMove(x, y, x-1, y+1),x, y, x-1, y+1);
		}
		//makes the move that had the highest util
		System.out.print(moves[0]);
		System.out.print(moves[1]);
		System.out.print(moves[2]);
		System.out.print(moves[3]);
		System.out.println();
		main.makeMove(moves[0], moves[1], moves[2], moves[3]);
		util=-1;
		moves=new int[4];
//		while(!pieces.isEmpty()) {
//			int spot[]=pieces.pop();
//			int x=spot[0];
//			int y=spot[1];
//			//multicap moves always will go if they can execute
//			spot=board.multiCapCheck(x, y);
//			if(spot[0]!=-1) {
//				if (main.makeMove(x, y, spot[0], spot[1])) break;;
//	
//			}
//			
//			if(board.possibleCap(x-2, y+2)) {
//				if (main.makeMove(x, y, x-2, y+2)) break;;
//				
//			}
//
//			if(board.possibleCap(x-2, y-2)) {
//				if (main.makeMove(x, y, x-2, y-2)) break;;
//				
//			}
//			if(board.getCheckKing(x, y)) {
////				if (main.makeMove(x, y, x+2, y+2)) break;
////				if (main.makeMove(x, y, x+2, y-2)) break;		
////				if (main.makeMove(x, y, x+1, y+1)) break;
////				if (main.makeMove(x, y, x+1, y-1)) break;	
//				if(board.possibleCap(x+2, y+2)) {
//					if (main.makeMove(x, y, x+2, y+2)) break;;
//					
//				}
//				if(board.possibleCap(x+2, y-2)) {
//					if (main.makeMove(x, y, x+2, y-2)) break;;
//					
//				}
//			}
//			if(board.getCheckKing(x, y)) {
//				if (main.makeMove(x, y, x+1, y+1)) break;
//				if (main.makeMove(x, y, x+1, y-1)) break;
//			}
////			if (main.makeMove(x, y, x-2, y+2)) break;
////			if (main.makeMove(x, y, x-2, y-2)) break;		
//			if (main.makeMove(x, y, x-1, y+1)) break;
//			if (main.makeMove(x, y, x-1, y-1)) break;	
//		}
	}
	
	private void checkUtil(int newUtil, int x, int y, int xn, int yn) {
		if(newUtil>util) {
			util=newUtil;
			moves=new int[] {x,y,xn,yn};
		}
	}
}
