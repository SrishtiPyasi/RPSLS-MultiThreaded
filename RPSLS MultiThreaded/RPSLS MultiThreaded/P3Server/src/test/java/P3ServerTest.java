import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.application.Platform;

class P3ServerTest {

//	@Test
//	void test() {
//		fail("Not yet implemented");
//	}

	@Test
	public void validPort() {
		Server server = new Server(data ->{Platform.runLater(()->{});
			
		}, 5555);
		
		Integer port = server.getPort();
		boolean Range = false;
		
		if(port>= 1023) {
			Range = true;
		}
		
		assertTrue(Range);
	}
	
	
	@Test
	public void testLogic() {
		Server server = new Server(data ->{Platform.runLater(()->{});
		
	 }, 5555);
	
		String p1Choice = "ROCK";
		String p2Choice = "PAPER";
		String answer = server.whoWon(p1Choice, p2Choice);
		assertEquals("PLAYER 2", answer);
		
	}
	
	@Test
	public void testLogic2() {
		Server server = new Server(data ->{Platform.runLater(()->{});
		
	 }, 5555);
	
		String p1Choice = "LIZARD";
		String p2Choice = "SPOCK";
		
		String answer = server.whoWon(p1Choice, p2Choice);
		assertEquals("PLAYER 1", answer);
		
	}
	
	@Test
	public void testLogic3() {
		Server server = new Server(data ->{Platform.runLater(()->{});
	 }, 5555);
	
		String p1Choice = "PAPER";
		String p2Choice = "SCISSORS";
		
		String answer = server.whoWon(p1Choice, p2Choice);
		assertEquals("PLAYER 2", answer);
		
	}
	
	
	
	
	
	
}
