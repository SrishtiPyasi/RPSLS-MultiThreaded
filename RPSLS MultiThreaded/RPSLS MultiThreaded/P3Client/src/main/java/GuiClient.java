import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.util.Duration;
public class GuiClient extends Application{

	
	TextField s1,s2,s3,s4, c1, portText, IPText;
	Button serverChoice,onButton,b1, pic1Button, pic2Button, pic3Button, pic4Button,pic5Button;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	HBox ImageBox;
	Scene startScene;
	BorderPane startPane;
	BorderPane imagePane;
	BorderPane lastPane;
	BorderPane pane;
	Client clientConnection, anotherClient;
	Label portLabel;
	Label IPLabel;
	Button challengeButton; //click this button to challenge the selected player on scene 2
	ListView<String> playersListView, listItems2, anotherList;
	HBox portBox;
	HBox IPBox;
	VBox centerVbox;
	PauseTransition pause = new PauseTransition(Duration.seconds(10));
	
	Label warningLabel;//to display a warning if an unavailable client is selected
	
	Socket clientSocket;//socket for the client
	
	String img = " ";
	static final int picHeight = 100;
	static final int picWidth = 150;
	//port number:
	Integer portNum;
	String IPAddress;
	//An instance of the game info class
	GameInfo info;
	Image rockImg = new Image("rock.png");
	Image spockImg = new Image("spock.png");
	Image paperImg = new Image("paper.png");
	Image lizardImg = new Image("lizard.png");
	Image scissorImg = new Image("scissor.png");
	Image imgChoice = new Image("gift.jpg");
	Button playAgain = new Button("Play Again");
	//Button restartGameButton = new Button("Restart Game");
	//Button quitButton = new Button("Quit");
	Button imgChoiceButton = new Button();
	ImageView v = new ImageView();
	Label portIpError;//to display port or IP errors
	Button backButton;//button on scene 2 to go back to lobby.

	EventHandler<ActionEvent> returnButtons;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	//method to disable all buttons
	public void disableButtons() {
		pic1Button.setDisable(true);
		pic2Button.setDisable(true);
		pic3Button.setDisable(true);
		pic4Button.setDisable(true);
		pic5Button.setDisable(true);
	}
	//method to enable all buttons
	public void enableButtons() {
		pic1Button.setDisable(false);
		pic2Button.setDisable(false);
		pic3Button.setDisable(false);
		pic4Button.setDisable(false);
		pic5Button.setDisable(false);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		info = new GameInfo();
		//Making two labels for entering port label and IP address
		portLabel = new Label("Enter Port number: ");
		IPLabel = new Label("Enter IP number: ");
		
		//Making two text fields for each of these
		portText = new TextField();
		IPText = new TextField();
		
		//Making 2 HBoxes for each:
		portBox = new HBox(portLabel, portText);
		IPBox = new HBox(IPLabel, IPText);
		//Putting padding between the two Hboxes:
		portBox.setPadding(new Insets(10, 10, 10, 10));
		//New VBox to stick them there
		centerVbox = new VBox(portBox, IPBox);
		IPBox.setPadding(new Insets(10, 10, 10, 10));
		
		//Making the buttons take in port Number and IP Address
		this.onButton = new Button("ON");
		onButton.setDefaultButton(true);//set this so we can hit ENTER instead of clicking mouse
		centerVbox.getChildren().addAll(onButton);
		
		challengeButton = new Button("Challenge");//create the challenge button
		
		//Button on scene 2 to go back to lobby
		backButton = new Button("Back");

		//The on Button
		this.onButton.setOnAction(e-> { try {
											clientSocket = new Socket(IPText.getText(),Integer.parseInt(portText.getText()));
											clientSocket.setTcpNoDelay(true);
										}
										catch(Exception ex) {
											showError(portIpError,"IP is 127.0.0.1 for this program\nPort is from 4500 to 6500\n");
											return;
										}
		
										primaryStage.setScene(sceneMap.get("list"));
										primaryStage.setTitle("Available players");
											
											clientConnection = new Client(data->{
												Platform.runLater(()->{
												if(data.toString().equals("OPP QUIT")) {
													disableButtons();
													listItems2.getItems().add("Your opponent is disconnected \n"
									    					+ "Please go back to the lobby");
													
												}
												else {
													listItems2.getItems().add(data.toString());
													if(clientConnection.have2 == false) {
														//pic1Button.setDisable(true);
														disableButtons();
													}
													else if(clientConnection.have2 == true) {
														//pic1Button.setDisable(false);
														enableButtons();
													}
												}
												
											});
											}, clientSocket,
											data ->{	
												 Platform.runLater(()->{anotherList.getItems().add(data.toString());
												 if(clientConnection.newGame == true) {
													 playAgain.setDisable(true);
												 }
												 if(clientConnection.hasImg == true) {
													   img = clientConnection.imgChoice;
														if(img.equals("ROCK")) {
															imgChoice = rockImg;
															System.out.print("YAY imgchoice " + img);
														}
														else if(img.equals("PAPER")) {
															imgChoice = paperImg;
															System.out.print("YAY imgchoice " + img);
														}
														else if(img.equals("SPOCK")) {
															imgChoice = spockImg;	
														}
														else if(img.equals("SCISSORS")) {
															imgChoice = scissorImg;
														}
														else if(img.equals("LIZARD")) {
															imgChoice = lizardImg;
														}
											
														v.setImage(imgChoice);
														v.setFitHeight(picHeight);
														v.setFitWidth(picWidth);
														v.setPreserveRatio(true);	
														imgChoiceButton.setGraphic(v);
														
												 }
												 
												});
											}, data->{// the 5th argument for players list
												Platform.runLater(()->{
													
													//clear list view when receive signal from client thread
													if(data.toString().equals("clear")) {
														playersListView.getItems().clear();
													}
													else if(data.toString().equals("CLIENT UNAVAILABLE")) {
														showError(warningLabel,"Client Unavailable!!!");
													}
													else {
														playersListView.getItems().add(data.toString());
													}
													if(clientConnection.accepted == true) {
														primaryStage.setScene(sceneMap.get("client"));
														primaryStage.setTitle("This is client " + clientConnection.id);
														clientConnection.accepted = false;
													}
													
														
												});
											});
											
											System.out.print("Connected to port" + portNum);
											clientConnection.start();
		});
		
		//challenge button
		challengeButton.setOnAction(e->{
			info.message = "CHALLENGE";
			
			//if challengeButton is pressed without picking a client
			if(playersListView.getSelectionModel().getSelectedItems().isEmpty()
					|| playersListView.getSelectionModel().getSelectedItems().toString().length() != 10 ) {
				showError(warningLabel,"Please pick a valid opponent!!!");
				return;//skip the rest of codes in this EventHandler
			}
			
			info.message = info.message + " " + playersListView.getSelectionModel().getSelectedItems();
			System.out.println(playersListView.getSelectionModel().getSelectedItems());
			//send the message to the server
			System.out.println(info.message);
			clientConnection.sendInfo(info);
			
			try {
				clientConnection.out.reset();
			} catch (IOException e1) {}
		
		});
		
		//Pic1 button:
		pic1Button = new Button();
		pic1Button.setOnAction(data-> {
			primaryStage.setScene(sceneMap.get("win"));
			primaryStage.setTitle("This is the winning Screen");
			info.message = "ROCK";
			
			System.out.println("GUI client rock is : " + info.message);
			clientConnection.sendInfo(info);
			
			try {
				clientConnection.out.reset();
			} catch (IOException e1) {}
			
			Platform.runLater(()->{anotherList.getItems().add("You chose " + info.message);
			});
		});	
		
		//Pic2 button:
		pic2Button = new Button();
		pic2Button.setOnAction(data-> {
			primaryStage.setScene(sceneMap.get("win"));
			primaryStage.setTitle("This is the winning Screen");
		info.message = "SPOCK";
			
			System.out.println("GUI client spock is : " + info.message);
			clientConnection.sendInfo(info);
			
			try {
				clientConnection.out.reset();
			} catch (IOException e1) {}
			Platform.runLater(()->{anotherList.getItems().add("You chose " + info.message);
			});			
		});
				
	
		//Pic3 button:
		pic3Button = new Button();
			pic3Button.setOnAction(data-> {
				primaryStage.setScene(sceneMap.get("win"));
				primaryStage.setTitle("This is the winning Screen");
				info.message = "PAPER";
				
				System.out.println("GUI client paper is : " + info.message);
				clientConnection.sendInfo(info);
				
				try {
					clientConnection.out.reset();
				} catch (IOException e1) {}
				Platform.runLater(()->{anotherList.getItems().add("You chose " + info.message);
				});
			});
			
			
	     //Pic4 button:
		pic4Button = new Button();
			pic4Button.setOnAction(data-> {
				primaryStage.setScene(sceneMap.get("win"));
				primaryStage.setTitle("This is the winning Screen");
				info.message = "SCISSORS";
				
				System.out.println("GUI client paper is : " + info.message);
				clientConnection.sendInfo(info);
				
				try {
					clientConnection.out.reset();
				} catch (IOException e1) {}
				
				
				Platform.runLater(()->{anotherList.getItems().add("You chose " + info.message);
				});
			});
			
		//Pic5 button:	
		pic5Button = new Button();
			pic5Button.setOnAction(data-> {
				primaryStage.setScene(sceneMap.get("win"));
				primaryStage.setTitle("This is the winning Screen");
				info.message = "LIZARD";
				
				System.out.println("GUI client paper is : " + info.message);
				clientConnection.sendInfo(info);
				
				try {
					clientConnection.out.reset();
				} catch (IOException e1) {}
				
				Platform.runLater(()->{anotherList.getItems().add("You chose " + info.message);
				});
			});
				
			backButton.setOnAction(e-> {
				primaryStage.setScene(sceneMap.get("list"));
				primaryStage.setTitle("This is waiting list scene");
				System.out.println("coming back to Choosing scene" );
				info.message = "Please wait for the other player to come as well! ";
			
				listItems2.getItems().clear();
				anotherList.getItems().clear();

				Platform.runLater(()->{anotherList.getItems().add(info.message);
				
				
				info.message = "BACK";
			
				info.p1Plays = " ";
				info.p2Plays = " ";
				info.p1Points = 0;
				info.p2Points = 0;
				clientConnection.newGame = false;
				info.limit = false;
				clientConnection.sendInfo(info);
				imgChoice = rockImg;

				
				try {
					clientConnection.out.reset();
				} catch (IOException e1) {}
				
				});
		});		
		//Play again Button on last screen
		//In play again have check if a thats is set to something
			
		//UPDATE: Changed the play button to take you back to the waiting list scene
			//PUT ALL CODE OF RESTART IN HERE:
		playAgain.setOnAction(data-> {
				primaryStage.setScene(sceneMap.get("list"));
				primaryStage.setTitle("This is waiting list scene");
				System.out.println("coming back to Choosing scene" );
				info.message = "Please wait for the other player to come as well! ";
			
				listItems2.getItems().clear();
				anotherList.getItems().clear();
			
				Platform.runLater(()->{anotherList.getItems().add(info.message);
				
				
				info.message = "PLAY AGAIN";
				
				info.p1Plays = " ";
				info.p2Plays = " ";
				info.p1Points = 0;
				info.p2Points = 0;
				clientConnection.newGame = false;
				info.limit = false;
				clientConnection.sendInfo(info);
				imgChoice = rockImg;

				
				try {
					clientConnection.out.reset();
				} catch (IOException e1) {}
				});
		});	
		
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(centerVbox);
		startPane.setStyle("-fx-background-color: pink");
		
		portIpError = new Label();
		portIpError.setStyle("-fx-font: 24 Arial");
		startPane.setBottom(portIpError);//to display port of IP errors
		
		startScene = new Scene(startPane, 800,800);
		
		playersListView = new ListView<String>();
		listItems2 = new ListView<String>();
		anotherList = new ListView<String>();
	
		sceneMap = new HashMap<String, Scene>();
	
		sceneMap.put("win",  winningScreen());
		sceneMap.put("client",  createClientGui());
		sceneMap.put("list",createPlayerListScene());//put the player list scene to hashmap
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	
	public Scene winningScreen() {
		//This screen shows the winning info
		pane = new BorderPane();
		pane.setStyle("-fx-background-color: LIGHTSEAGREEN");
		
    	pane.setRight(anotherList);
    	pane.setBottom(playAgain);
    	
    	v.setImage(imgChoice);
		v.setFitHeight(picHeight);
		v.setFitWidth(picWidth);
		v.setPreserveRatio(true);	
		imgChoiceButton.setGraphic(v);
		
		Label imgLabel = new Label ("Opponent Played ^ ");
		
		VBox vbox = new VBox(imgChoiceButton, imgLabel);
    	pane.setLeft(vbox);
		return new Scene(pane, 500, 400);
		
		
	}
	
	public Scene createClientGui() {
		//This is the second scene
		//ImagePane: BorderPane
		//Right: ListeView
		//Center: 4 clickable images
		//One label at the top: Choose
		imagePane = new BorderPane();
		
		//One Image: 
		ImageView v1 = new ImageView(rockImg);
		v1.setFitHeight(picHeight);
		v1.setFitWidth(picWidth);
		v1.setPreserveRatio(true);
		pic1Button.setGraphic(v1);
		
		
		//Image 2:paper
		ImageView v2 = new ImageView(spockImg);
		v2.setFitHeight(picHeight);
		v2.setFitWidth(picWidth);
		v2.setPreserveRatio(true);
		pic2Button.setGraphic(v2);
		
		//Image 2:paper
		ImageView v3 = new ImageView(paperImg);
		v3.setFitHeight(picHeight);
		v3.setFitWidth(picWidth);
		v3.setPreserveRatio(true);
		pic3Button.setGraphic(v3);
		
		//Image: Scissors:
		ImageView v4 = new ImageView(scissorImg);
		v4.setFitHeight(picHeight);
		v4.setFitWidth(picWidth);
		v4.setPreserveRatio(true);
		pic4Button.setGraphic(v4);
		
		//Image: Lizard:
		ImageView v5 = new ImageView(lizardImg);
		v5.setFitHeight(picHeight);
		v5.setFitWidth(picWidth);
		v5.setPreserveRatio(true);
		pic5Button.setGraphic(v5);
		
		
		//Set the listView to the right of borderPane
		imagePane.setRight(listItems2);

		ImageBox = new HBox(10, pic1Button, pic3Button, pic2Button, pic4Button, pic5Button);
				
		imagePane.setCenter(ImageBox);
		imagePane.setTop(backButton);
		BorderPane.setMargin(backButton, new Insets(0,0,10,0));
				
		imagePane.setStyle("-fx-background-color: #FA8072");
		imagePane.setPadding(new Insets(10));
		return new Scene(imagePane, 900, 500);
	}
	
	//create scene to display list of available player
	public Scene createPlayerListScene() {
		BorderPane pane = new BorderPane();
		
		//label to show any warnings(Ex. unavailable client)
		warningLabel = new Label();
		
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: burlywood");
		pane.setCenter(new VBox(10,playersListView,warningLabel));
		pane.setBottom(challengeButton);
		return new Scene(pane, 500, 400);	
	}
	
	//Function to display input error
	void showError(Label label,String msg) {
		/*Pause the program for certain duration*/
		//create an empty event handler to act as a place holder for Timeline
		EventHandler<ActionEvent> empty = e ->{};
		//create a Timeline object to pause the scene
		Timeline pause = new Timeline(new KeyFrame(Duration.millis(2000),empty));
				
		//add the message for 2 seconds then remove it
		label.setText(msg);
		pause.play();
		pause.setOnFinished(q->{
			label.setText("");
		});
	}
	
	//Function to pause for given millis seconds
	void pause(int sec) {
		/*Pause the program for certain duration*/
		//create an empty event handler to act as a place holder for Timeline
		EventHandler<ActionEvent> empty = e ->{};
		//create a Timeline object to pause the scene
		Timeline pause = new Timeline(new KeyFrame(Duration.millis(sec),empty));
		pause.play();
		pause.setOnFinished(q->{});
	}
}