import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Client extends Thread{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	GameInfo info;
	public Boolean newGame = false;
	public Boolean have2 = false;
	public String imgChoice = "ROCK";
	public Boolean hasImg = false;
	public Integer id; //the unique id number of this client
	public Boolean accepted = false;
	
	//Two lists
	private Consumer<Serializable> callback;
	private Consumer<Serializable> callback2;
	private Consumer<Serializable> callback3;//to display list of available players
	
	Client(Consumer<Serializable> call, Socket clientSckt, Consumer<Serializable> call2,Consumer<Serializable> call3 ){
		callback = call;
		socketClient = clientSckt;
		callback2 = call2;
		info = new GameInfo();
		callback3 = call3;
		try {
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
		}catch(Exception e) {}
	}
	
	public void run() {
		
		try {
			info = (GameInfo)in.readObject();
		} catch (ClassNotFoundException | IOException e1) {}	
		
    	//take ID number if there is signal
    	if(info.message.equals("Get id")) {
    		id = info.IdToTake;
    		info.IdToTake = 0;//reset this field after taking ID
    		callback3.accept("This is client "  + id);
    	}
    	
		while(true) {
			 
			try {
				
		    	info = (GameInfo)in.readObject();
		    	System.out.println("this is info message  " + info.message);
		    	System.out.println("Player 1 had played: " + info.p1Plays);
		    	System.out.println("Player 2 had played:  " + info.p2Plays);
		    	
		    	//list available players
		    	callback3.accept("clear");//send signal to GUI thread to clear list view
	    		callback3.accept("You are client " + id);
		    	
	    		//Game info keeps track of available clients as well so print all clients here
	    		for(Integer i : info.avaiList) {	
		    		if( Integer.compare(i, id) != 0) {
			    		callback3.accept("Client " + i);
		    		}
		    	}
	    		
	    		//when selected client is not available
	    		if(info.message.contains("CLIENT UNAVAILABLE")) {
	    			callback3.accept(info.message);
	    		}
	    		
	    		if(info.message.contains("ACCEPT")) {
	    			callback3.accept(info.message);
	    			callback.accept("You're playing with client " + info.message.substring(6,7));
	    			callback.accept("Please make a choice!");
	    			accepted = true;
	    		}
	    
	    		if(info.message.contains("OPP QUIT")){
	    			callback.accept("OPP QUIT");
	    		}
	    		
		    	if(info.message.equals("OPPLAYS")) {
		    		imgChoice = info.oppChoice;
		    		hasImg = true;
		    		callback2.accept("Opponent played : " + imgChoice);	
		    	}
		    	
		    	if(info.message.equals("RESTART")) {
		    		newGame = false;
		    	}
		    	
		    	
		    	if(info.have2players == true) {
		    		//have 2 variable is set to true now
		    		have2 = true;
		    	}
		    	else if(info.have2players == false) {
		    		//Else only client is on the server so tell them to wait for another
		    		have2 = false;
		    	}
		    	
		    	//You output the message
		    	//If that message has winner then:
		        if(info.message.equals("Winner")) {
		        	System.out.println("Winner has been computed and sent back" + info.message);
			    	printWinner(info);
			    	
			    	//If you've reached the limit:
			    	if(info.limit == true) {
			    		newGame = true;
			    		if(info.p1Points == 3) {
			    			callback2.accept("Player 1 has won three points!");
			    		}
			    		else if(info.p2Points == 3) {
			    			callback2.accept("Player 2 has won three points! ");
			    		}
			    		callback2.accept("Choose to either play again from the beginning or quit the game \n"
			    				+ " Please choose an option! ");
			    	}
			    	
		        }
			}
			catch(Exception e) {}
		}
    }
	
	
	public void sendString(String data) {
		
		try {
			out.writeObject(data);
		} catch (IOException e) {}
	}
	
	public void sendInfo(GameInfo info) {
		try {
			out.writeObject(info);
		} catch (IOException e) {}
	}
	
	public void printWinner(GameInfo info) {
		
    	if(info.winner == 999) {
    		callback2.accept("It's tie");
    	}
    	else if(Integer.compare(info.winner, id) == 0) {
    		callback2.accept("Congrats, you won!");
    	}
    	else {
    		callback2.accept("Sorry, you lost");
    	}
    	
	}

}


class GameInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	String message = "";
	Integer winner = 999;
	Integer p1Points = 0; 
	Integer p2Points = 0;
	String p1Plays = " ";
	String p2Plays = " ";
	Boolean have2players = false;
	Boolean limit = false;
	ArrayList<Integer> avaiList = new ArrayList<Integer>();//keep track of available players
	Integer IdToTake; //Id number that the server assigns to a client, the client takes it when there is signal
						//and then sets this field to zero
	String oppChoice = " ";
}

