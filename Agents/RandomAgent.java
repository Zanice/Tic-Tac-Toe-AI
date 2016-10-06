package Agents;

import java.util.List;
import java.util.Random;

import Application.Board;
import Application.Board.Token;
import Application.BoardAnalyzer;

public class RandomAgent extends Agent {
	public RandomAgent(Token token, Token[] otherAgents) {
		super(token, otherAgents);
	}

	@Override
	public boolean allowsHumanInput() {
		return false;
	}
	
	@Override
	public void configureTesting(boolean testing) {
		
	}

	@Override
	public void configureExploration(boolean exploring) {
		
	}

	@Override
	public int[] getMove(Board board) {
		List<int[]> possibleMoves = BoardAnalyzer.possibleMoves(board);
		
		Random random = new Random();
		int randomIndex = random.nextInt(possibleMoves.size());
		
		return possibleMoves.get(randomIndex);
	}

	@Override
	public void processEndGame(Board board) {
		
	}
}
