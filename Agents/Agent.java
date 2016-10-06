package Agents;

import Application.Board;
import Application.Board.Token;

public abstract class Agent {
	private Token token;
	private Token[] otherAgents;
	
	public Agent(Token token, Token[] otherAgents) {
		this.token = token;
		this.otherAgents = otherAgents;
	}
	
	public Token getToken() {
		return token;
	}
	
	public Token[] getOtherAgents() {
		return otherAgents;
	}
	
	public abstract boolean allowsHumanInput();
	public abstract void configureTesting(boolean testing);
	public abstract void configureExploration(boolean exploring);
	public abstract int[] getMove(Board board);
	public abstract void processEndGame(Board board);
}
