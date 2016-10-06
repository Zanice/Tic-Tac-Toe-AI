package Agents;

import Application.Board;
import Application.Board.Token;

public class HumanAgent extends Agent {
	public HumanAgent(Token token, Token[] otherAgents) {
		super(token, otherAgents);
	}

	@Override
	public boolean allowsHumanInput() {
		return true;
	}
	
	@Override
	public void configureTesting(boolean testing) {
		
	}
	
	@Override
	public void configureExploration(boolean exploring) {
		
	}
	
	@Override
	public int[] getMove(Board board) {
		return null;
	}

	@Override
	public void processEndGame(Board board) {
		
	}
}
