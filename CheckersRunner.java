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

/**
 * The view class for checkers, allows for 2 players or 1 player vs AI.
 */
public class CheckersRunner extends Application implements PropertyChangeListener, EventHandler<ActionEvent> {
	private Scene scene;
	// array of the squares on board
	private Button[][] buttons;
	private boolean currentPlayer;
	private Button clear;
	private Button players;
	private GridPane grid;
	private BorderPane root;
	private BorderPane top;
	private Label output;
	private Stage stage;
	// the X and Y currently of the piece that will be moved
	private int pieceX;
	private int pieceY;
	private CheckersMain main;
	// if the ai is active
	private boolean ai;
	private CheckersAI aiPlayer;

	/**
	 * Launches the game
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		launch(args);
	}

	/**
	 * Start function for the game
	 */
	public void start(Stage arg0) throws Exception {
		try {
			ai = false;
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
					e1.printStackTrace();
				}
			});
			top.setRight(clear);
			players = new Button();
			players.setPrefHeight(10);
			players.setText("2 Players");
			players.setOnAction(e -> {
				try {
					ai = !ai;
					if (ai) {
						players.setText("vs. AI");
						aiPlayer = new CheckersAI();
					} else {
						players.setText("2 Players");
					}
					createGame(arg0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			top.setLeft(players);
			scene = new Scene(root, 400, 450);
			createGame(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * function that creates game and sets up/resets all variables for a new game
	 * 
	 * @param primaryStage
	 */
	public void createGame(Stage primaryStage) {
		main = new CheckersMain();
		main.addPropertyChangeListener(this);
		main.addBoardPropertyChangeListener(this);
		output.setText("Black's turn");
		stage = primaryStage;
		buttons = new Button[8][8];
		int xVal = 0;
		// values for the piece chosen, start as nothing on board
		pieceX = -1;
		pieceY = -1;
		for (int i = 0; i < 8; i++) {
			int yVal = 0;
			for (int j = 0; j < 8; j++) {
				int fi = i, fj = j;
				buttons[i][j] = new Button();
				if ((i + j) % 2 == 1) {
					buttons[i][j].setOnAction(e -> {
						if (pieceX == -1) {
							// button gets itself and sets the piece locations to it's row and column
							Button temp = (Button) e.getSource();
							pieceX = (GridPane.getRowIndex(temp) / 50);
							pieceY = (GridPane.getColumnIndex(temp) / 50);
						} else {
							// current player is saved
							currentPlayer = main.teamGet(pieceX, pieceY);
							// move is made by clicking on a piece, and then a box on the board, gives an
							// error to the player if it's not done correctly and lets them choose again
							main.makeMove(pieceX, pieceY, (GridPane.getRowIndex(buttons[fi][fj]) / 50),
									(GridPane.getColumnIndex(buttons[fi][fj]) / 50));
							// reset piece choice after a move is attempted
							pieceX = -1;
							pieceY = -1;
						}
					});
				}
				Image image;
				if ((i + j) % 2 == 0) {
					image = new Image("Assets\\red.png");
				} else {
					image = new Image("Assets\\black.png");
				}
				ImageView imageView = new ImageView(image);
				buttons[i][j].setPrefWidth(50);
				buttons[i][j].setPrefHeight(50);
				buttons[i][j].setGraphic(imageView);
				buttons[i][j].setStyle("-fx-padding: 0;");
				grid.add(buttons[i][j], xVal, yVal);
				yVal += 50;
			}
			xVal += 50;
		}
		xVal = 0;
		// Adds black checkers
		for (int i = 0; i < 3; i++) {
			int yVal;
			// k is used to offset the pieces so they are on the right color of squares
			int k;
			// offset to make sure it is on the right color spots on the board
			if ((i % 2) == 0) {
				k = 1;
				yVal = 50;
			} else {
				k = 0;
				yVal = 0;
			}
			for (int j = k; j < 8; j += 2) {
				Image image;
				image = new Image("Assets\\blackpiece.png");
				ImageView imageView = new ImageView(image);
				buttons[j][i].setGraphic(imageView);
				yVal += 100;
			}
			xVal += 50;
		}

		// adds red checkers
		xVal = 350;
		for (int i = 7; i > 4; i--) {
			int yVal;
			int k;
			// offset to make sure it is on the right color spots on the board
			if ((i % 2) == 0) {
				k = 1;
				yVal = 50;
			} else {
				k = 0;
				yVal = 0;
			}
			for (int j = k; j < 8; j += 2) {
				Image image;
				image = new Image("Assets\\redpiece.png");
				ImageView imageView = new ImageView(image);
				buttons[j][i].setGraphic(imageView);
				yVal += 100;
			}

			xVal -= 50;
		}
		stage.setScene(scene);
		stage.show();

	}

	/**
	 *
	 */
	@Override
	public void handle(ActionEvent arg0) {
	}

	/**
	 * Handles the actions from the model and updates the board accordingly
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("label")) {
			output.setText((String) evt.getNewValue());
		} else if (evt.getPropertyName().equals("error")) {
			// sets label to error message when a wrong move is attempted
			if (ai && main.getPlayer()) {
				output.setText((String) evt.getNewValue());
				//resets piece chosen
				pieceX=-1;
				pieceY=-1;
			} else if (!ai) {
				output.setText((String) evt.getNewValue());
				//resets piece chosen
				pieceX=-1;
				pieceY=-1;
			}
		} else if (evt.getPropertyName().equals("kill")) {
			// removes a piece from the board
			int[] spot = (int[]) evt.getNewValue();
			Image image;
			image = new Image("Assets\\black.png");
			ImageView imageView = new ImageView(image);
			buttons[spot[1]][spot[0]].setGraphic(imageView);
		} else if (evt.getPropertyName().equals("move")) {
			int[] oldSpot = (int[]) evt.getOldValue();
			int[] spot = (int[]) evt.getNewValue();
			Image image;
			image = new Image("Assets\\black.png");
			ImageView imageView = new ImageView(image);
			buttons[oldSpot[1]][oldSpot[0]].setGraphic(imageView);
			// checks if the piece should be styled as a king piece
			if (currentPlayer) {
				if (main.kingGet(spot[0], spot[1])) {
					image = new Image("Assets\\blackking.png");
					imageView = new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				} else {
					image = new Image("Assets\\blackpiece.png");
					imageView = new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				}
			} else {
				if (main.kingGet(spot[0], spot[1])) {
					image = new Image("Assets\\redking.png");
					imageView = new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				} else {
					image = new Image("Assets\\redpiece.png");
					imageView = new ImageView(image);
					buttons[spot[1]][spot[0]].setGraphic(imageView);
				}
			}
			// switches turn
		} else if (evt.getPropertyName().equals("turn")) {
			if ((boolean) evt.getNewValue()) {
				output.setText("Black's turn");
			} else {
				output.setText("Red's turn");
				if (ai) {
					currentPlayer = main.getPlayer();
					aiPlayer.aiMove(main);
				}
			}
		}

	}

}
