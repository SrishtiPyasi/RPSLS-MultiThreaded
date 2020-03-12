import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{
	//Different from count, this is for setting the number of clients at one time
	Integer numClients =0;
	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	GameInfo info;
	Integer portNum;
	Boolean twoMsg = false;
	int messages =0;
	public Boolean newGame = false;
	Server(Consumer<Serializable> call, Integer port){
	
		callback = call;
		server = new TheServer();
		server.start();
		portNum = port;
		info = new GameInfo();	
	}
	
	public String whoWon(String p1Choice, String p2Choice) {
		String winner = "None";

	     if(p1Choice.equals("ROCK")) {
	        if (p2Choice.equals("ROCK")) {
	            winner = "TIE";
	     
	        } 
	        else if ( p2Choice.equals("SPOCK")) {
	            winner = "PLAYER 2";
	           
	        } else if ( p2Choice.equals("LIZARD") ) {
	            winner = "PLAYER 1";
	            
	        }  else if (p2Choice.equals("PAPER")) {
	            winner = "PLAYER 2";
	            
	        }  else if ( p2Choice.equals("SCISSORS") ) {
	            winner = "PLAYER 1";
	           
	        }
	    } 
	     else if(p1Choice.equals("PAPER")) {
	        if (p2Choice.equals("PAPER")) {
	            winner = "TIE"; 
	        } 
	        else if ( p2Choice.equals("SPOCK")) {
	            winner = "PLAYER 1";
	            
	        } 
	        else if ( p2Choice.equals("LIZARD") ) {
	            winner = "PLAYER 2";
	            
	        }  else if ( p2Choice.equals("ROCK")) {
	            winner = "PLAYER 1";
	          
	        }  else if ( p2Choice.equals("SCISSORS") ) {
	            winner = "PLAYER 2";
	           
	        }
	    } 
	     else if(p1Choice.equals("SCISSORS")) {
	        if (p2Choice.equals("SCISSORS")) {
	            winner = "TIE";
	         
	        } else if (p2Choice.equals("SPOCK")) {
	            winner = "PLAYER 2";
	            
	        } else if ( p2Choice.equals("LIZARD") ) {
	            winner = "PLAYER 1";
	            
	        }  else if (p2Choice.equals("ROCK")) {
	            winner = "PLAYER 2";
	            
	        }  else if ( p2Choice.equals("PAPER") ) {
	             winner = "PLAYER 1";
	           
	        } 
	    } 
	    else if(p1Choice.equals("LIZARD")) {
	        if (p2Choice.equals("LIZARD")) {
	            winner = "TIE";
	        } 
	        else if ( p2Choice.equals("SPOCK")) {
	            winner = "PLAYER 1"; 
	        } 
	        else if ( p2Choice.equals("SCISSORS") ) {
	            winner = "PLAYER 2" ;
	            
	        }  
	        else if (p2Choice.equals("ROCK")) {
	            winner = "PLAYER 2";
	            
	        }  
	        else if ( p2Choice.equals("PAPER") ) {
	            winner = "PLAYER 1";
	           
	        }  
	    } 
	    else if(p1Choice.equals("SPOCK")) {
	        if (p2Choice.equals("SPOCK")) {
	            winner = "TIE";
	           
	        } 
	        else if (p2Choice.equals("LIZARD")) {
	            winner = "PLAYER 2";
	        } 
	        else if ( p2Choice.equals("SCISSORS") ) {
	            winner = "PLAYER 1";
	          
	        }  
	        else if ( p2Choice.equals("ROCK") ) {
	            winner = "PLAYER 1";
	          
	        }  else if ( p2Choice.equals("PAPER") ) {
	            winner = "PLAYER 2";
	            
	        };  
	    } 
	    return winner;
	}
	
	public void playerPoints (String winner) {
		System.out.println("from evaluate winner " );
		if(winner.equals("PLAYER 1")){
			info.p1Points++;
		}
		else if(winner.equals("PLAYER 2")) {
			info.p2Points++;
		}
	}
	
	public void newInfo(GameInfo info) {
		info.message = "RESTART";
		info.have2players = false;
		info.p1Plays = " ";
		info.p2Plays = " ";
		info.p1Points = 0;
		info.p2Points = 0;
		info.limit = false;
		info.winner = 999;
		if(numClients == 1) {
			info.have2players = false;
		}
		else{
			info.have2players = true;
		}
		
	}
	
	public class TheServer extends Thread{
		
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(portNum);){
		    System.out.println("Server is waiting for a client!");
			
		    while(true) {
		    	//make a check or something that I've received from 2 clients
		    	//index of clients starts at 1
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				count++;
				System.out.println("Count is "+ count);
				numClients++;
				
			    }
		    
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
			Socket connection;
			//int count;
			Integer index;
			Integer opIndex;//opponent's index
			ObjectInputStream in;
			ObjectOutputStream out;
			Boolean isAvai;//if the client is available to play
		    ClientThread opponent;  //your opponent thread
		    String choice; //Your game choice;
			
			ClientThread(Socket s, int count){
				this.connection = s;
		
				index = count;
				opIndex = 0;
				isAvai = true;
				info.avaiList.add(index);//add the new client to list of available clients
			}
			
			//Just sends out the info to all the clients in the array (all connected)
			public void updateClients(GameInfo sendInfo) {
				
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(sendInfo);
					}
					catch(Exception e) {}
				}
			}
			
			public void reset() {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.reset();
					}
					catch(Exception e) {}
				}
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
					
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
	
				
				
				//sending ID signal so the client take the ID number
				info.message = "Get id";
				info.IdToTake = index;//new ID for the client
				try {
						out.writeObject(info);
						info.message = "";
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				reset();
				
			
				info.message = "new client on server: client #"+index;
				if(numClients == 2) {
					info.have2players = true;
					
				}
				updateClients(info);
				callback.accept("Number of clients connected: " + numClients);
				//reset the stream
				reset();
				
				 while(true) {
					    try {
					    	
					        GameInfo newinfo = (GameInfo)in.readObject();
					    	//Convert to string:
					    	
					        callback.accept("client: " + index + " sent: " + newinfo.message.toString());
					       
					        if(newinfo.message.equals("PLAY AGAIN")) {
					    		newGame = true;
					    		isAvai = true;//only set isAvai to true when client presses playAgain/restart
					    		info.avaiList.add(index);//add this client back to avaiList
					    		
					    		//Your choice becomes null:
					    		choice = null;
					    	    //Opponent choice is also null:
					    		this.opponent.choice = null;
					    		
					    		newInfo(info);
					    		
					    		updateClients(info);
					    		reset();
					    		continue;
					    	}
					        
					        if(newinfo.message.equals("BACK")) {
					    		newGame = true;
					    		isAvai = true;//only set isAvai to true when client presses playAgain/restart
					    		info.avaiList.add(index);//add this client back to avaiList
					    		
					    		//Your choice becomes null:
					    		choice = null;
					    	    //Opponent choice is also null:
					    		this.opponent.choice = null;
					    		
					    		newInfo(info);
					    		
					    		updateClients(info);
					    		reset();
					    		
					    		info.message = "OPP QUIT";
					    		this.opponent.out.writeObject(info);
					    		this.opponent.out.reset();
					    		
					    		continue;
					    	}
					        
					        
					        if(this.isAvai == true) {
					        	//If the message sent is challenge
						        if(newinfo.message.contains("CHALLENGE")) {
						        	System.out.println("Im in challenge! YAy");
						        	
						        	//Find the opponentSelected
						        	String opponentSelected = newinfo.message.substring(18, 19);
						        	//Convert from String to Int
						        	Integer opponentSelectedInt = Integer.valueOf(opponentSelected);
						        	
						        	System.out.println("opponent Slected: " + opponentSelected);
						        	System.out.println("opponent Slected: " + opponentSelectedInt);
						        	
						        	
						        	//"3" -> "5'
						        	// 5 exists in the array?
						        	//if opponentSelectedint exists in array 
						        	
						        	//If your opponent is available
						        	ClientThread extra = clients.get(opponentSelectedInt-1);
						        	if(extra.isAvai == true) {
						        		//Then set your opponent thread 
						        		this.opponent = clients.get(opponentSelectedInt-1);
						        		//make your opponent's opponent you
						        		this.opponent.opponent = this;
						        		
						        		//set your and opponents availability to false
						        		this.isAvai = false;
						        		info.avaiList.remove((Integer)index);//remove this client from avaiList
						        		this.opponent.isAvai = false;
						        		info.avaiList.remove((Integer)opponent.index);//remove opponent from avaiList
						        		
						        		info.message =  "ROCK";
						        		updateClients(info);//to update avaiList of all clients because we already lost two clients
						        		reset();
						        		
						        		//Tell each client who they're playing
						        		info.message = "ACCEPT" + opponentSelected;
						        		this.out.writeObject(info);
						        		//send the opponent your own index
						        		info.message = "ACCEPT" + this.index;
						        		opponent.out.writeObject(info);
						        		reset();

						        	}
						        	else {//selected client is not available
						        		info.message = "CLIENT UNAVAILABLE";
						        		this.out.writeObject(info);
						        		reset();
						        	}			        
						        }      	      	
					        }
					        
					        else {
					      
						    	 //I get a message
						    	 this.choice = newinfo.message;
						    	 //check if opponents message is also set
						    	 if(this.opponent.choice != null) {
						    		 //Opponent has also made a choice
						    		 String oppChoice = this.opponent.choice;
						    		 info.message = "OPPLAYS";
						    		 info.oppChoice = oppChoice;
						    		 //1. Send yourself, your opponents choice
						    		 this.out.writeObject(info);
						    		 out.reset();
						    		 //2.Send your opponent your choice
						    		 info.oppChoice = choice;
						    		 this.opponent.out.writeObject(info);
						    		 this.opponent.out.reset();
						    		 //whoWon- first parameter is your choice, 2nd param is opponents choice
						    		 String win = whoWon(choice, this.opponent.choice);
						    		 Integer finalWinner = 999;
						    		 String winString = "";
						    		 if(win.equals("PLAYER 1")) {
						    			 //You're the winner
						    			 finalWinner = index;
						    			 winString = "Client " + index + " won";
						    		 }
						    		 else if(win.equals("PLAYER 2")){
						    			 finalWinner = this.opponent.index;
						    			 winString = "Client " + this.opponent.index + " won";
						    		 }
						    		 else if(win.equals("TIE")) {
						    			 finalWinner = 999;
						    			 winString = "It is a tie";
						    		 }
						    		 //Once you've sent the winners- you essentially finish the game so set availabilit to 
						    		 //true
						    		 this.isAvai = true;
						    		 this.opponent.isAvai = true;
						    		 
						    		 //display final winner on server side:
						    		 callback.accept(winString);
						    		 info.message = "Winner";
						    		 info.winner = finalWinner;
						    		 this.out.writeObject(info);
						    		 this.opponent.out.writeObject(info);
						
						    		 reset();
						    	 }				   
					        }
					    }
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + index + "....closing down!");
					    	numClients = numClients -1;
					    	if(numClients == 1) {
					    		info.have2players = false;
					    	}
					    	
					    	info.message = "Client #"+index+" has left the server!";
					    	
					    	//remove this client from available list
					    	info.avaiList.remove((Integer)index);//explicitly cast to Integer object
					    	
					    	//System.out.println("Count is "+ count);
					    	updateClients(info);
					    	clients.set(index-1, null);
					    	reset();
					    
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread


		public Integer getPort() {
			// TODO Auto-generated method stub
			return portNum;
		}
		
}

class GameInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	String message = " ";
	Integer winner = 999;
	Integer p1Points = 0; 
	Integer p2Points = 0;
	String p1Plays = " ";
	String p2Plays = " ";
	Boolean have2players = false;
	Boolean limit = false;
	ArrayList<Integer> avaiList = new ArrayList<Integer>();//keep track of available players
	Integer IdToTake; //Id number that the server assigns to a client, the client takes it when there is signal
	String oppChoice = " ";
}



	
	

	