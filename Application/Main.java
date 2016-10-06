package Application;

import Agents.AdversarialAgent;
import Agents.Agent;
import Agents.HumanAgent;
import Agents.QLearningAgent;
import Agents.RandomAgent;
import Application.Board.Token;

public class Main {
	public static final int BOARD_DIMENSION = 3;
	private static final int NUMBER_OF_GAMES = 500;
	private static final int GAMES_BETWEEN_TESTING = 1;
	private static final int GAMES_DURING_TESTING = 5;
	
	private static final Token AGENT_1_TOKEN = Token.X;
	private static final Token AGENT_2_TOKEN = Token.CIRCLE;
	
	private static Window window;
	private static Display display;
	
	private static int currentGame;
	private static int currentTestingGame;
	private static boolean inGame;
	private static int currentTurn;
	private static Board board;
	
	private static Agent agent1;
	private static Agent agent2;
	
	private static int agent1Score;
	private static int agent2Score;
	
	private static boolean threadRunning;
	private static Thread gameRunner;
	
	public static void main(String[] args) {
		window = new Window("AI Player - Tic Tac Toe");
		display = new Display(165 + ((Display.BOARD_POSITION_DIMENSION + 5) * BOARD_DIMENSION), 10 + ((Display.BOARD_POSITION_DIMENSION + 5) * BOARD_DIMENSION), BOARD_DIMENSION);
		window.add(display);
		
		currentGame = 0;
		currentTestingGame = 0;
		board = new Board(BOARD_DIMENSION);
		agent1 = new AdversarialAgent(AGENT_1_TOKEN, new Token[] { AGENT_2_TOKEN });
		agent2 = new AdversarialAgent(AGENT_2_TOKEN, new Token[] { AGENT_1_TOKEN });
		
		agent1Score = 0;
		agent2Score = 0;
		
		toggleThread(false);
		
		inGame = false;
		attemptNewGameStart();
	}
	
	private static void attemptNewGameStart() {
		if (currentGame >= NUMBER_OF_GAMES) {
			inGame = false;
			System.out.println("Games have ended. Agent 1 won " + agent1Score + " and Agent 2 won " + agent2Score + ".");
			return;
		}
		
		currentGame++;
		board.resetBoard();
		currentTurn = ((currentGame - 1) % 2) + 1;
		inGame = true;
		
		display.updateBoard(board);
	}
	
	public static void runTurn() {
		if (!inGame)
			return;
		
		int[] move;
		Token token;
		
		switch (currentTurn) {
			case 1:
				move = agent1.getMove(board);
				token = agent1.getToken();
				break;
			case 2:
				move = agent2.getMove(board);
				token = agent2.getToken();
				break;
			default:
				move = null;
				token = null;
				break;
		}
		
		if (move == null)
			return;
		
		processMove(move[0], move[1], token);
	}
	
	public static void processInterfaceMove(int x, int y) {
		if (!inGame)
			return;
		
		Token token;
		
		switch (currentTurn) {
			case 1:
				if (!agent1.allowsHumanInput())
					return;
				token = agent1.getToken();
				break;
			case 2:
				if (!agent2.allowsHumanInput())
					return;
				token = agent2.getToken();
				break;
			default:
				token = null;
				break;
		}
		
		processMove(x, y, token);
	}
	
	private static void processMove(int x, int y, Token token) {
		Board moveResult = board.createBoardAfterMove(x, y, token);
		
		if (moveResult == null)
			return;
		
		board = moveResult;
		display.updateBoard(board);
		
		Token winner = BoardAnalyzer.getWinner(board);
		if (winner != Token.NONE) {
			System.out.println(winner + " is the winner!");
			inGame = false;
			
			if (agent1.getToken() == winner)
				agent1Score++;
			else if (agent2.getToken() == winner)
				agent2Score++;
			
			agent1.processEndGame(board);
			agent2.processEndGame(board);
			
			attemptNewGameStart();
		}
		else if (BoardAnalyzer.possibleMoves(board).isEmpty()) {
			System.out.println("Stalemate!");
			inGame = false;
			
			attemptNewGameStart();
		}
		else {
			switch (currentTurn) {
				case 1:
					currentTurn = 2;
					break;
				case 2:
					currentTurn = 1;
					break;
			}
		}
	}
	
	public static void toggleThread(boolean status) {
		threadRunning = status;
		
		if (threadRunning)
			gameRunner.start();
		else
			gameRunner = new Thread() {
				@Override
				public void run() {
					while (threadRunning) {
						runTurn();
//						try {
//							sleep(20);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
				}
			};
	}
}
