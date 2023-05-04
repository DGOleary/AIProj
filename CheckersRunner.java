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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CheckersRunner extends Application implements PropertyChangeListener, EventHandler<ActionEvent>{
	Scene scene;
	Button[][] buttons;
	Button[][] checkers;
	int[][][] locations;
	Button clear;
	GridPane grid;
	BorderPane root;
	BorderPane top;
	Alert alert;
	private Label output;
	Stage stage;
	int pieceX;
	int pieceY;
	CheckersMain main;
public static void main(String args[]) {
	//when the GUI is implemented this will be the class that controls the view and receives information from the CheckersMain
	//CheckersMain main=new CheckersMain();
	//main.runGame();
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
	stage=primaryStage;
	output.setText("Black's turn");
	buttons=new Button[8][8];
	int xVal=0;
	pieceX=-1;
	pieceY=-1;
//	Button jeff=new Button();
//	jeff.setPrefSize(50, 50);
//	jeff.setShape(new Rectangle(50, 50));
//	grid.add(jeff, xVal, yVal);
	for(int i=0;i<8;i++) {
		int yVal=0;
		for(int j=0;j<8;j++) {
			int fi=i, fj=j;
			buttons[i][j] = new Button();
			buttons[i][j].setOnAction(e -> {
//				System.out.println(pieceX);
//				System.out.println(pieceY);
//				System.out.println((GridPane.getRowIndex(buttons[fi][fj])/50));
//				System.out.println((GridPane.getColumnIndex(buttons[fi][fj])/50));
				main.makeMove(pieceX, pieceY, (GridPane.getRowIndex(buttons[fi][fj])/50), (GridPane.getColumnIndex(buttons[fi][fj])/50));
				pieceX=-1;
				pieceY=-1;
			});
			if((i+j)%2==0) {
				buttons[i][j].setStyle("-fx-border-color: red; -fx-color: red;");
			}else {
				buttons[i][j].setStyle("-fx-border-color: black; -fx-color: black;");
			}
			buttons[i][j].setPrefSize(50, 50);
			buttons[i][j].setShape(new Rectangle(50, 50));
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
			int fi=i, fj=j;
			
			checkers[i][j]=new Button();
			checkers[i][j].setOnAction(e -> {
//				System.out.println("test");
//				System.out.println(checkers[fi][fj]);
//				System.out.println(fi);
//				System.out.println(fj);
//				pieceX=(GridPane.getRowIndex(checkers[fi][fj])/50);
//				pieceY=(GridPane.getColumnIndex(checkers[fi][fj])/50);
				Button temp = (Button) e.getSource();
				pieceX=(GridPane.getRowIndex(temp)/50);
				pieceY=(GridPane.getColumnIndex(temp)/50);
				//checks with it's position on the board so it updates instantly and doesn't need to be clicked again
				if(main.kingGet(pieceX, pieceY)) {
					temp.setStyle("-fx-border-color: gold; -fx-color: black;");
					}
			});
			checkers[i][j].setPrefSize(45, 45);
			checkers[i][j].setShape(new Circle(22.5));
			checkers[i][j].setStyle("-fx-border-color: grey; -fx-color: black;");
			//centers checker in square
			checkers[i][j].setTranslateX(2.5);
			checkers[i][j].setTranslateY(-1);
			grid.add(checkers[i][j], yVal, xVal);
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
			int fi=i, fj=j;
			checkers[i][j]=new Button();
			checkers[i][j].setOnAction(e -> {
//				pieceX=(GridPane.getRowIndex(checkers[fi][fj])/50);
//				pieceY=(GridPane.getColumnIndex(checkers[fi][fj])/50);
				Button temp = (Button) e.getSource();
				pieceX=(GridPane.getRowIndex(temp)/50);
				pieceY=(GridPane.getColumnIndex(temp)/50);
				if(main.kingGet(pieceX, pieceY)) {
					temp.setStyle("-fx-border-color: gold; -fx-color: red;");
					}
			});
			checkers[i][j].setPrefSize(45, 45);
			checkers[i][j].setShape(new Circle(22.5));
			checkers[i][j].setStyle("-fx-border-color: red; -fx-color: red;");
			//centers checker in square
			checkers[i][j].setTranslateX(2.5);
			checkers[i][j].setTranslateY(-1);
			grid.add(checkers[i][j], yVal, xVal);
			yVal+=100;
		}
		xVal-=50;
	}
	//test checker
//	Button checker=new Button();
//	checker.setPrefSize(45, 45);
//	checker.setShape(new Circle(22.5));
//	checker.setStyle("-fx-border-color: black; -fx-color: black;");
//	// Set the circle's position within the button
//	checker.setTranslateX(2.5);
//	checker.setTranslateY(-1);
//	grid.add(checker, 0, 0);
//	grid.getChildren().remove(checker);
//	grid.add(checker, 100, 100);
	stage.setScene(scene);
	stage.show();
	
}

@Override
public void handle(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
}

public void propertyChange(PropertyChangeEvent evt) {
	if(evt.getPropertyName().equals("instruction_alert")) {
		alert= new Alert(AlertType.NONE, (String) evt.getNewValue(), ButtonType.CLOSE);
		alert.show();
	}else if(evt.getPropertyName().equals("label")) {
		output.setText((String) evt.getNewValue());
	}else if(evt.getPropertyName().equals("error")) {
		alert= new Alert(AlertType.NONE, (String) evt.getNewValue(), ButtonType.CLOSE);
		alert.show();
	}else if(evt.getPropertyName().equals("kill")) {
		int[] spot= (int[]) evt.getNewValue();
		grid.getChildren().remove(checkers[spot[0]][spot[1]]);
		checkers[spot[0]][spot[1]]=null;
	}else if(evt.getPropertyName().equals("move")) {
		int[] oldSpot= (int[]) evt.getOldValue();
//		System.out.println("old");
//		System.out.println(oldSpot[0]);
//		System.out.println(oldSpot[1]);
		int[] spot= (int[]) evt.getNewValue();
//		System.out.println("new");
//		System.out.println(spot[0]);
//		System.out.println(spot[1]);
		grid.getChildren().remove(checkers[oldSpot[0]][oldSpot[1]]);
		grid.add(checkers[oldSpot[0]][oldSpot[1]], spot[1]*50, spot[0]*50);
		checkers[spot[0]][spot[1]]=checkers[oldSpot[0]][oldSpot[1]];
		checkers[oldSpot[0]][oldSpot[1]]=new Button();
		if(main.kingGet(spot[0], spot[1])) {
			checkers[spot[0]][spot[1]].setStyle("-fx-border-color: gold; -fx-color: black;");
			}
//		System.out.println(GridPane.getRowIndex(checkers[spot[0]][spot[1]])/50);
//		System.out.println(GridPane.getColumnIndex(checkers[spot[0]][spot[1]])/50);
	}else if(evt.getPropertyName().equals("turn")) {
		if((boolean) evt.getNewValue()) {
			output.setText("Black's turn");
		}else {
			output.setText("Red's turn");
		}
	}
	
}

}
