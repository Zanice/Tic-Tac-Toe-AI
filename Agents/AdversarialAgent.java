package Agents;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Application.Board;
import Application.Board.Token;
import Application.BoardAnalyzer;

public class AdversarialAgent extends Agent {
	private static final int NUMBER_OF_PLYS = 9;
	
	private static final double WINNING_BOARD_UTILITY = 1000.0;
	private static final double POTENTIAL_WIN_UTILITY = 100.0;
	
	public AdversarialAgent(Token token, Token[] otherAgents) {
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
		Random random = new Random();
		List<int[]> possibleMoves = BoardAnalyzer.possibleMoves(board);
		List<int[]> bestMoves = new LinkedList<int[]>();
		
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		
		double backupValue;
		double currentBest = Double.NEGATIVE_INFINITY;
		for (int[] possibleMove : possibleMoves) {
			backupValue = alphaBetaBackup(alpha, beta, false, board.createBoardAfterMove(possibleMove[0], possibleMove[1], getToken()), getToken(), getOtherAgents()[0], NUMBER_OF_PLYS);
			
			if (backupValue >= currentBest) {
				if (backupValue > currentBest) {
					bestMoves.clear();
					currentBest = backupValue;
				}
				bestMoves.add(possibleMove);
			}
		}
		
		if (bestMoves.isEmpty())
			return null;
		
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}
	
	@Override
	public void processEndGame(Board board) {
		
	}
	
	private double alphaBetaBackup(double alpha, double beta, boolean maxNode, Board board, Token maxToken, Token minToken, int depth) {
		Token tokenInUse;
		Token tokenNotInUse;
		
		if (maxNode) {
			tokenInUse = maxToken;
			tokenNotInUse = minToken;
		}
		else {
			tokenInUse = minToken;
			tokenNotInUse = maxToken;
		}
		
		if (depth <= 0 || BoardAnalyzer.winningBoardForToken(board, tokenInUse)) {
			double utility = calculateUtilityOf(tokenInUse, tokenNotInUse, board);
			return maxNode ? utility : -utility;
		}
		
		double backupValue;
		double currentBest = maxNode ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		
		List<int[]> possibleMoves = BoardAnalyzer.possibleMoves(board);
		
		for (int[] possibleMove : possibleMoves) {
			backupValue = alphaBetaBackup(alpha, beta, !maxNode, board.createBoardAfterMove(possibleMove[0], possibleMove[1], tokenInUse), maxToken, minToken, depth - 1);
			
			if (maxNode) {
				if (backupValue > beta)
					return backupValue;
				else {
					alpha = Math.max(alpha, backupValue);
					if (backupValue > currentBest)
						currentBest = backupValue;
				}
			}
			else {
				if (backupValue < alpha)
					return backupValue;
				else {
					beta = Math.min(beta, backupValue);
					if (backupValue < currentBest)
						currentBest = backupValue;
				}
			}
		}
		
		return currentBest;
	}

	private double calculateUtilityOf(Token agent, Token enemy, Board board) {
		double utility = 0.0;
		
		if (BoardAnalyzer.winningBoardForToken(board, agent))
			utility += WINNING_BOARD_UTILITY;
		else if (BoardAnalyzer.winningBoardForToken(board, enemy))
			utility -= WINNING_BOARD_UTILITY;
		
		utility += POTENTIAL_WIN_UTILITY * BoardAnalyzer.potentialWinsForToken(board, agent);
		utility -= POTENTIAL_WIN_UTILITY * BoardAnalyzer.potentialWinsForToken(board, enemy);
		
		return utility;
	}
}
