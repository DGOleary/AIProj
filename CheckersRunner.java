package checker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CheckersRunner extends Application implements PropertyChangeListener, EventHandler<ActionEvent>{
	private Scene scene;
	//array of the squares on board
	private Button[][] buttons;
	//array of the checker pieces on board
	private Button[][] checkers;
	//remove checkers, draw checkers on the board and rework lambdas
	
	private boolean currentPlayer;
	private Button clear;
	private GridPane grid;
	private BorderPane root;
	private BorderPane top;
	private Alert alert;
	private Label output;
	private Stage stage;
	private int pieceX;
	private int pieceY;
	private CheckersMain main;
public static void main(String args[]) {
	launch(args);
}

public void start(Stage arg0) throws Exception {
	try {
		alert = new Alert(AlertType.NONE);
		root = new BorderPane();
		top = new BorderPane();
		grid = new GridPane();
		top = new BorderPane();
		output = new Label();
		output.setPrefHeight(10);
		root.setCenter(grid);
		root.setTop(top);
		root.setBottom(output);
		clear = new Button();
		clear.setPrefHeight(10);
		clear.setText("Reset");
		clear.setOnAction(e -> {
			try {
				start(arg0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		top.setRight(clear);
		createGame(arg0);
	}catch(Exception e) {
		e.printStackTrace();
	}
	
}

public void createGame(Stage primaryStage) {
	main=new CheckersMain();
	main.runGame();
	main.addPropertyChangeListener(this);
	main.addBoardPropertyChangeListener(this);
	scene = new Scene(root, 400, 450);
	output.setText("Black's turn");
	stage=primaryStage;
	buttons=new Button[8][8];
	int xVal=0;
	//values for the piece chosen, start as nothing on board
	pieceX=-1;
	pieceY=-1;
	for(int i=0;i<8;i++) {
		int yVal=0;
		for(int j=0;j<8;j++) {
			int fi=i, fj=j;
			buttons[i][j] = new Button();
			buttons[i][j].setOnAction(e -> {
				if(pieceX==-1) {
					//button gets itself and sets the piece locations to it's row and column
					Button temp = (Button) e.getSource();
					pieceX=(GridPane.getRowIndex(temp)/50);
					pieceY=(GridPane.getColumnIndex(temp)/50);
				}else {
				//current player is saved
				currentPlayer=main.teamGet(pieceX, pieceY);
				//move is made by clicking on a piece, and then a box on the board, gives an error to the player if it's not done correctly and lets them choose again
				main.makeMove(pieceX, pieceY, (GridPane.getRowIndex(buttons[fi][fj])/50), (GridPane.getColumnIndex(buttons[fi][fj])/50));
				//reset piece choice after a move is attempted
				pieceX=-1;
				pieceY=-1;
				}
			});
			Image image;
			if((i+j)%2==0) {
				image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\red.png");
				//buttons[i][j].setStyle("-fx-border-color: red; -fx-color: red;");
			}else {
				//buttons[i][j].setStyle("-fx-border-color: black; -fx-color: black;");
				image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\black.png");
			}
			ImageView imageView=new ImageView(image);
			//buttons[i][j].setMaxSize(50, 50);
			buttons[i][j].setPrefWidth(50);
			buttons[i][j].setPrefHeight(50);
			//buttons[i][j].setShape(new Rectangle(50, 50));
			buttons[i][j].setGraphic(imageView);
			buttons[i][j].setStyle("-fx-padding: 0;");
			grid.add(buttons[i][j], xVal, yVal);
			yVal+=50;
		}
		xVal+=50;
	}
	xVal=0;
	//Adds black checkers
	checkers=new Button[8][8];
	for(int i=0;i<3;i++) {		///// set to 3
		int yVal;
		//k is used to offset the pieces so they are on the right color of squares
		int k;
		//offset to make sure it is on the right color spots on the board
		if((i%2)==0) {
			k=1;
			yVal=50;
		}else {
			k=0;
			yVal=0;
		}
		for(int j=k;j<8;j+=2) {
//			int fi=i, fj=j;
//			checkers[i][j]=new Button();
//			checkers[i][j].setOnAction(e -> {
//				//button gets itself and sets the piece locations to it's row and column
//				System.out.println("fire");
//				Button temp = (Button) e.getSource();
//				pieceX=(GridPane.getRowIndex(temp)/50);
//				pieceY=(GridPane.getColumnIndex(temp)/50);
//				//checks if the piece should be styled as a king piece
//				if(main.kingGet(pieceX, pieceY)) {
//					temp.setStyle("-fx-border-color: gold; -fx-color: black;");
//					}
//			});
//			checkers[i][j].setPrefSize(45, 45);
//			checkers[i][j].setShape(new Circle(22.5));
//			checkers[i][j].setStyle("-fx-border-color: grey; -fx-color: black;");
//			//centers checker in square
//			checkers[i][j].setTranslateX(2.5);
//			checkers[i][j].setTranslateY(-1);
//			grid.add(checkers[i][j], yVal, xVal);
			Image image;
			image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\blackpiece.png");
			ImageView imageView=new ImageView(image);
			buttons[j][i].setGraphic(imageView);
			yVal+=100;
		}
		xVal+=50;
	}

	
	//adds red checkers
	xVal=350;
	for(int i=7;i>4;i--) {		///// set to 4
		int yVal;
		int k;
		//offset to make sure it is on the right color spots on the board
		if((i%2)==0) {
			k=1;
			yVal=50;
		}else {
			k=0;
			yVal=0;
		}
		for(int j=k;j<8;j+=2) {
//			int fi=i, fj=j;
//			checkers[i][j]=new Button();
//			checkers[i][j].setOnAction(e -> {
//				System.out.println("fire");
//				Button temp = (Button) e.getSource();
//				pieceX=(GridPane.getRowIndex(temp)/50);
//				pieceY=(GridPane.getColumnIndex(temp)/50);
//				if(main.kingGet(pieceX, pieceY)) {
//					temp.setStyle("-fx-border-color: gold; -fx-color: red;");
//					}
//			});
//			checkers[i][j].setPrefSize(45, 45);
//			checkers[i][j].setShape(new Circle(22.5));
//			checkers[i][j].setStyle("-fx-border-color: red; -fx-color: red;");
//			//centers checker in square
//			checkers[i][j].setTranslateX(2.5);
//			checkers[i][j].setTranslateY(-1);
//			grid.add(checkers[i][j], yVal, xVal);
		Image image;
		image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\redpiece.png");
		ImageView imageView=new ImageView(image);
		buttons[j][i].setGraphic(imageView);
		yVal+=100;
		}
		
		xVal-=50;
	}
	stage.setScene(scene);
	stage.show();
	
}

@Override
public void handle(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
}

public void propertyChange(PropertyChangeEvent evt) {
	 if(evt.getPropertyName().equals("label")) {
		output.setText((String) evt.getNewValue());
	}else if(evt.getPropertyName().equals("error")) {
		alert= new Alert(AlertType.NONE, (String) evt.getNewValue(), ButtonType.CLOSE);
		alert.show();
	}else if(evt.getPropertyName().equals("kill")) {
		System.out.println("sdsd");
		int[] spot= (int[]) evt.getNewValue();
		Image image;
		image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\black.png");
		ImageView imageView=new ImageView(image);
		buttons[spot[1]][spot[0]].setGraphic(imageView);
//		grid.getChildren().remove(checkers[spot[0]][spot[1]]);
//		checkers[spot[0]][spot[1]]=null;
	}else if(evt.getPropertyName().equals("move")) {
		int[] oldSpot= (int[]) evt.getOldValue();
		int[] spot= (int[]) evt.getNewValue();
		Image image;
		image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\black.png");
		ImageView imageView=new ImageView(image);
		buttons[oldSpot[1]][oldSpot[0]].setGraphic(imageView);
		//checks if the piece should be styled as a king piece
		if(currentPlayer) {
			if(main.kingGet(spot[0], spot[1])) {
				image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\blackking.png");
				imageView=new ImageView(image);
				buttons[spot[1]][spot[0]].setGraphic(imageView);
			}else {
				image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\blackpiece.png");
				imageView=new ImageView(image);
				buttons[spot[1]][spot[0]].setGraphic(imageView);
			}
			}else {
				if(main.kingGet(spot[0], spot[1])) {
					image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\redking.png");
					imageView=new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				}else {
					image=new Image("C:\\Users\\Mango T. Shih-tzu\\Desktop\\School\\CompSci\\Algo\\Checkers\\src\\checker\\Assets\\redpiece.png");
					imageView=new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				}
			}
		
		//grid.getChildren().remove(checkers[oldSpot[0]][oldSpot[1]]);
		//grid.add(checkers[oldSpot[0]][oldSpot[1]], spot[1]*50, spot[0]*50);
		//checkers[spot[0]][spot[1]]=checkers[oldSpot[0]][oldSpot[1]];
		//checkers[oldSpot[0]][oldSpot[1]]=new Button();
	}else if(evt.getPropertyName().equals("turn")) {
		if((boolean) evt.getNewValue()) {
			output.setText("Black's turn");
		}else {
			output.setText("Red's turn");
		}
	}
	
}

}
