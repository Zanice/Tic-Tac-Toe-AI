package Application;

import java.util.LinkedList;
import java.util.List;

import Application.Board.Token;

public class BoardAnalyzer {
	public static boolean isCenterPosition(Board board, int x, int y) {
		int halfPoint = board.getDimension() / 2;
		boolean centerX;
		boolean centerY;
		
		if (board.getDimension() % 2 == 1) {
			centerX = x == halfPoint;
			centerY = y == halfPoint;
		}
		else {
			centerX = x == halfPoint - 1 || x == halfPoint;
			centerY = y == halfPoint - 1 || y == halfPoint;
		}
		
		return centerX && centerY;
	}
	
	public static boolean isCornerPosition(Board board, int x, int y) {
		boolean cornerX = x == 0 || x == board.getDimension() - 1;
		boolean cornerY = y == 0 || y == board.getDimension() - 1;
		
		return cornerX && cornerY;
	}
	
	public static List<int[]> possibleMoves(Board board) {
		List<int[]> possibleMoves = new LinkedList<int[]>();
		
		for (int xIndex = 0; xIndex < board.getDimension(); xIndex++) {
			for (int yIndex = 0; yIndex < board.getDimension(); yIndex++) {
				if (board.isValidMove(xIndex, yIndex))
					possibleMoves.add(new int[] {
							xIndex,
							yIndex
					});
			}
		}
		
		return possibleMoves;
	}
	
	public static double fractionFilled(Board board) {
		double positionsFilled = 0;
		
		for (int xIndex = 0; xIndex < board.getDimension(); xIndex++) {
			for (int yIndex = 0; yIndex < board.getDimension(); yIndex++) {
				if (board.getTokenAt(xIndex, yIndex) != Token.NONE)
					positionsFilled++;
			}
		}
		
		return positionsFilled / (double) (board.getDimension() * board.getDimension());
	}
	
	public static boolean winningBoardForToken(Board board, Token token) {
		int dimension = board.getDimension();
		boolean validRun;
		
		// Test each column
		for (int xIndex = 0; xIndex < dimension; xIndex++) {
			validRun = true;
			
			for (int yIndex = 0; yIndex < dimension; yIndex++) {
				if (board.getTokenAt(xIndex, yIndex) != token) {
					validRun = false;
					break;
				}
			}
			
			if (validRun)
				return true;
		}
		
		// Test each row
		for (int yIndex = 0; yIndex < dimension; yIndex++) {
			validRun = true;
			
			for (int xIndex = 0; xIndex < dimension; xIndex++) {
				if (board.getTokenAt(xIndex, yIndex) != token) {
					validRun = false;
					break;
				}
			}
			
			if (validRun)
				return true;
		}
		
		// Test the back diagonal
		int xIndex = 0;
		int yIndex = 0;
		validRun = true;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) != token) {
				validRun = false;
				break;
			}
			
			xIndex++;
			yIndex++;
		}
		if (validRun)
			return true;
		
		// Test the forward diagonal
		xIndex = 0;
		yIndex = dimension - 1;
		validRun = true;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) != token) {
				validRun = false;
				break;
			}
			
			xIndex++;
			yIndex--;
		}
		if (validRun)
			return true;
		
		// If all tests have failed, the specified token does not have a winning configuration
		return false;
	}
	
	public static int potentialWinsForToken(Board board, Token token) {
		int dimension = board.getDimension();
		int missingPositions;
		int potentialWins = 0;
		
		// Test each column
		for (int xIndex = 0; xIndex < dimension; xIndex++) {
			missingPositions = 0;
			
			for (int yIndex = 0; yIndex < dimension; yIndex++) {
				if (board.getTokenAt(xIndex, yIndex) != token) {
					if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
						missingPositions = -1;
						break;
					}
					
					missingPositions++;
					
					if (missingPositions > 1)
						break;
				}
			}
			
			if (missingPositions == 0 || missingPositions == 1)
				potentialWins++;
		}
		
		// Test each row
		for (int yIndex = 0; yIndex < dimension; yIndex++) {
			missingPositions = 0;
			
			for (int xIndex = 0; xIndex < dimension; xIndex++) {
				if (board.getTokenAt(xIndex, yIndex) != token) {
					if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
						missingPositions = -1;
						break;
					}
					
					missingPositions++;
					
					if (missingPositions > 1)
						break;
				}
			}
			
			if (missingPositions == 0 || missingPositions == 1)
				potentialWins++;
		}
		
		// Test the back diagonal
		int xIndex = 0;
		int yIndex = 0;
		missingPositions = 0;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) != token) {
				if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
					missingPositions = -1;
					break;
				}
				
				missingPositions++;
				
				if (missingPositions > 1)
					break;
			}
			
			xIndex++;
			yIndex++;
		}
		if (missingPositions == 0 || missingPositions == 1)
			potentialWins++;
		
		// Test the forward diagonal
		xIndex = 0;
		yIndex = dimension - 1;
		missingPositions = 0;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) != token) {
				if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
					missingPositions = -1;
					break;
				}
				
				missingPositions++;
				
				if (missingPositions > 1)
					break;
			}
			
			xIndex++;
			yIndex--;
		}
		if (missingPositions == 0 || missingPositions == 1)
			potentialWins++;
		
		// Return the number of successful tests
		return potentialWins;
	}
	
	public static int highestCompletenessOfLines(Board board, Token token) {
		int dimension = board.getDimension();
		int highestCompleteness = 0;
		int currentCompleteness;
		
		// Test each column
		for (int xIndex = 0; xIndex < dimension; xIndex++) {
			currentCompleteness = 0;
			
			for (int yIndex = 0; yIndex < dimension; yIndex++) {
				if (board.getTokenAt(xIndex, yIndex) == token)
					currentCompleteness++;
				else if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
					currentCompleteness = 0;
					break;
				}
			}
			
			if (currentCompleteness > highestCompleteness)
				highestCompleteness = currentCompleteness;
		}
		
		// Test each row
		for (int yIndex = 0; yIndex < dimension; yIndex++) {
			currentCompleteness = 0;
			
			for (int xIndex = 0; xIndex < dimension; xIndex++) {
				if (board.getTokenAt(xIndex, yIndex) == token)
					currentCompleteness++;
				else if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
					currentCompleteness = 0;
					break;
				}
			}
			
			if (currentCompleteness > highestCompleteness)
				highestCompleteness = currentCompleteness;
		}
		
		// Test the back diagonal
		int xIndex = 0;
		int yIndex = 0;
		currentCompleteness = 0;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) == token)
				currentCompleteness++;
			else if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
				currentCompleteness = 0;
				break;
			}
			
			xIndex++;
			yIndex++;
		}
		if (currentCompleteness > highestCompleteness)
			highestCompleteness = currentCompleteness;
		
		// Test the forward diagonal
		xIndex = 0;
		yIndex = dimension - 1;
		currentCompleteness = 0;
		while (board.isPositionOnBoard(xIndex, yIndex)) {
			if (board.getTokenAt(xIndex, yIndex) == token)
				currentCompleteness++;
			else if (board.getTokenAt(xIndex, yIndex) != Token.NONE) {
				currentCompleteness = 0;
				break;
			}
			
			xIndex++;
			yIndex--;
		}
		if (currentCompleteness > highestCompleteness)
			highestCompleteness = currentCompleteness;
		
		// Return the number of successful tests
		return highestCompleteness;
	}
	
	public static Token getWinner(Board board) {
		Token[] tokens = Token.values();
		boolean[] winners = new boolean[tokens.length];
		int winnerCount = 0;
		
		for (int index = 0; index < tokens.length; index++) {
			if (tokens[index] == Token.NONE) {
				winners[index] = false;
				continue;
			}
			
			winners[index] = winningBoardForToken(board, tokens[index]);
			if (winners[index])
				winnerCount++;
		}
		
		if (winnerCount != 1)
			return Token.NONE;
		
		for (int index = 0; index < tokens.length; index++) {
			if (winners[index])
				return tokens[index];
		}
		
		return Token.NONE;
	}
}
