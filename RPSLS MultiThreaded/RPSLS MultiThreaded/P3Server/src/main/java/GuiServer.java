import java.util.HashMap;

import javafx.animation.KeyFrame;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GuiServer extends Application{

	TextField s1,s2,s3,s4, c1, portText;
	Button serverChoice,clientChoice,b1, onButton;
	Label portLabel;// label for port TextField
	Label warningLabel; //label to display warning(Ex. incorrect port number)
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Integer port;
	Server serverConnection;
	ListView<String> listItems, listItems2;
	Text instructionText;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		//Make button for port Number and port Text
		//Make button to  connect - port button to go ahead
		//portButton = new Button("Enter port button to connect");
		portText = new TextField();
		//portButton.setOnAction(e->{port = Integer.parseInt(portText.getText());});
		onButton = new Button("ON");
		onButton.setDefaultButton(true);//set this so we can hit ENTER instead of clicking mouse
		
		onButton.setOnAction(e->{
			try {
				if(!isValidPort()) {
					showError(warningLabel,"Please enter port from 4500-6500 !");
					return;
				}	
			}
			catch(Exception ex){
				showError(warningLabel,"Port only contains integers !");
				return;
			}
			
			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> {
				Platform.runLater(()->{
					listItems.getItems().add(data.toString()); 
				});
				}, port);
											
		});
		
		
		portLabel = new Label("Enter port:");//label for port TextField
		warningLabel = new Label();//for displaying warnings
		warningLabel.setStyle("-fx-font: 24 Arial");
		
		this.buttonBox = new HBox(10,portLabel, portText, onButton);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		startPane.setBottom(warningLabel);//for displaying warnings
		startPane.setStyle("-fx-background-color: #00CED1");
		
		instructionText = new Text(10, 50, "This is a test");
		instructionText.setText("Hi! \n 1. Please enter port number and press the Port button"
				+ "\n  2. Press the ON button to connect");
		instructionText.setFont(new Font(20));
	
		startScene = new Scene(startPane, 600,600);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
	
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
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
	 
	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		pane.setCenter(listItems);
		return new Scene(pane, 500, 400);	
	}
	
	
	public Scene createClientGui() {
		clientBox = new VBox(10, c1,b1,listItems2);
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 400, 300);
		
	}
	
	//Function to validate port number
	//For this program, we only use port 5555 for simplicity
	boolean isValidPort() throws Exception{
		port = Integer.parseInt(portText.getText());
		return port >= 4500 && port <= 6500;//avoid reserved ports
	}
		
	//Function to display input error
	void showError(Label label,String msg) {
		/*Pause the program for certain duration*/
		//create an empty event handler to act as a place holder for Timeline
		EventHandler<ActionEvent> empty = e ->{};
		//create a Timeline object to pause the scene
		Timeline pause = new Timeline(new KeyFrame(Duration.millis(3000),empty));
					
		//add the message for 2 seconds then remove it
		label.setText(msg);
		pause.play();
		pause.setOnFinished(q->{
			label.setText("");
		});
	}
}