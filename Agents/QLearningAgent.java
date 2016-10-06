package Agents;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.print.attribute.standard.MediaSize.Other;

import Application.Board;
import Application.BoardAnalyzer;
import Application.Board.Token;

public class QLearningAgent extends Agent {
	private static final double GAMMA = 0.9;
	private static final double LEARNING_RATE = 0.0001;
	private static final double EPSILON = 0.02;
	
	private static final double TURN_TAKEN_REWARD = -1.0;
	private static final double POTENTIAL_WIN_REWARD = 10.0;
	private static final double GAME_OVER_REWARD = 100.0;
	
	private static final int NUMBER_OF_FEATURES = 7;
	private static final double CONSTANT_FEATURE = 1.0;
	
	private boolean freezeWeights;
	private double[] weights;
	
	private boolean epsilonExploration;
	
	private double totalReward;
	private double rewardAfterLastMove;
	
	private Board previousBoard;
	private double[] previousFeatures;
	
	public QLearningAgent(Token token, Token[] otherAgents) {
		super(token, otherAgents);
		
		weights = createRandomWeights();
		
		freezeWeights = false;
		epsilonExploration = true;
	}
	
	@Override
	public boolean allowsHumanInput() {
		return false;
	}

	@Override
	public void configureTesting(boolean testing) {
		freezeWeights = testing;
	}
	
	@Override
	public void configureExploration(boolean exploring) {
		epsilonExploration = exploring;
	}
	
	@Override
	public int[] getMove(Board board) {
		// Calculate the reward from the previous action
		rewardAfterLastMove = calculateReward(previousBoard, board);
		totalReward += rewardAfterLastMove;
		
		// Update weights for agent
		if (!freezeWeights)
			weights = calculateWeightVector(weights, previousFeatures, rewardAfterLastMove, board);
		
		// Determine next move
		int[] nextMove = determineEpsilonGreedyMove(board);
		
		return nextMove;
	}
	
	@Override
	public void processEndGame(Board board) {
		// Calculate the end game reward
		if (BoardAnalyzer.winningBoardForToken(board, getToken()))
			rewardAfterLastMove = GAME_OVER_REWARD;
		else if (BoardAnalyzer.getWinner(board) != Token.NONE)
			rewardAfterLastMove = -GAME_OVER_REWARD;
		else
			rewardAfterLastMove = 0;
		totalReward += rewardAfterLastMove;
		
		// Update the weights according to the end game reward
		if (!freezeWeights)
			weights = calculateWeightVector(weights, previousFeatures, rewardAfterLastMove, board);
		
		previousBoard = null;
		previousFeatures = null;
	}
	
	private double[] createRandomWeights() {
		double[] randomWeights = new double[NUMBER_OF_FEATURES];
		
		Random random = new Random();
		for (int index = 0; index < randomWeights.length; index++) {
            randomWeights[index] = (random.nextDouble() * 2) - 1;
        }
		
		return randomWeights;
	}
	
	private double calculateReward(Board oldBoard, Board newBoard) {
		double totalReward = 0;
		
		Token winner = BoardAnalyzer.getWinner(newBoard);
		if (winner != Token.NONE) {
			if (winner == getToken())
				return GAME_OVER_REWARD;
			else
				return -GAME_OVER_REWARD;
		}
		
		totalReward += TURN_TAKEN_REWARD;
		
		totalReward += POTENTIAL_WIN_REWARD * BoardAnalyzer.potentialWinsForToken(newBoard, getToken());
		
		for (Token enemy : getOtherAgents()) {
			totalReward -= POTENTIAL_WIN_REWARD * BoardAnalyzer.potentialWinsForToken(newBoard, enemy);
		}
		
		return totalReward;
	}
	
	private double[] calculateFeatureVector(Board board, int[] move) {
		int moveX = move[0];
		int moveY = move[1];
		
		Board moveResult = board.createBoardAfterMove(moveX, moveY, getToken());
		
		// Calculate feature 0;1 for move on a corner position
		double cornerMoveFeature = BoardAnalyzer.isCornerPosition(moveResult, moveX, moveY) ? 1.0 : 0.0;
		
		// Calculate feature 0;1 for move on a center position
		double centerMoveFeature = BoardAnalyzer.isCenterPosition(moveResult, moveX, moveY) ? 1.0 : 0.0;
		
		// Calculate feature (0,1] for highest completeness of a line after move
		double highestLineCompletenessFeature = BoardAnalyzer.highestCompletenessOfLines(moveResult, getToken());
		
		// Calculate feature 0;1;2;... for number of possible wins the move creates
		double friendlyPossibleWinsFeature = BoardAnalyzer.potentialWinsForToken(moveResult, getToken());
		
		// Calculate feature 0;1;2;... for highest number of possible wins the move leaves the other players with
		int maxPossibleWins = 0;
		int currentPossibleWins;
		for (Token currentToken : getOtherAgents()) {
			currentPossibleWins = BoardAnalyzer.potentialWinsForToken(moveResult, currentToken);
			if (currentPossibleWins > maxPossibleWins)
				maxPossibleWins = currentPossibleWins;
		}
		double enemyPossibleWinsFeature = maxPossibleWins;
		
		// Calculate feature 0;1 for win with this move
		double winWithMoveFeature = BoardAnalyzer.winningBoardForToken(moveResult, getToken()) ? 1.0 : 0.0;
		
		return new double[] {
				CONSTANT_FEATURE,
				cornerMoveFeature,
				centerMoveFeature,
				highestLineCompletenessFeature,
				friendlyPossibleWinsFeature,
				enemyPossibleWinsFeature,
				winWithMoveFeature
		};
	}
	
	private double calculateQValue(double[] weights, double[] features) {
		// Calculate the dot product of the weight and feature vectors
		try {
			return calculateDotProduct(weights, features);
		} catch (Exception e) {
			System.err.println("ERROR: Mismatched vector lengths when calculating Q value!");
			return Double.NEGATIVE_INFINITY;
		}
	}
	
	private double[] calculateWeightVector(double[] oldWeights, double[] oldFeatures, double reward, Board board) {
		if (oldFeatures == null)
			return oldWeights;
    	
    	double[] newWeights = new double[oldWeights.length];
    	
    	// Calculate Qw(s, a) from the old features, f(s, a)
    	double previousQValue = calculateQValue(oldWeights, oldFeatures);
    	
    	// Calculate max Qw(s', a') over all a', where a' is any possible move this turn
    	List<int[]> possibleMoves = BoardAnalyzer.possibleMoves(board);
    	double maxPossibleQValue = Double.NEGATIVE_INFINITY;
    	if (!possibleMoves.isEmpty()) {
    		double currentQValue;
    		
    		for (int index = 0; index < possibleMoves.size(); index++) {
    			currentQValue = calculateQValue(oldWeights, calculateFeatureVector(board, possibleMoves.get(index)));
    			if (currentQValue > maxPossibleQValue)
    				maxPossibleQValue = currentQValue;
    		}
    	}
    	
    	// Calculate the loss function
    	double bestFutureMove = GAMMA * maxPossibleQValue;
    	double lossFunctionValue = -(totalReward + bestFutureMove - previousQValue);
    	
    	// Calculate the new weight for each weight using the loss function
    	double partialLossForWeight;
    	for (int index = 0; index < oldWeights.length; index++) {
    		partialLossForWeight = lossFunctionValue * oldFeatures[index];
    		newWeights[index] = oldWeights[index] - (LEARNING_RATE * partialLossForWeight);
    	}
    	
    	return newWeights;
	}
	
	private int[] determineEpsilonGreedyMove(Board board) {
		List<int[]> possibleMoves = BoardAnalyzer.possibleMoves(board);
		
		// Determine the best advisable move
		List<int[]> recommendedMoves = new LinkedList<int[]>();
		double recommendedMoveQValue = Double.NEGATIVE_INFINITY;
		int possibleMoveX;
		int possibleMoveY;
		double possibleMoveQValue;
		for (int[] possibleMove : possibleMoves) {
			possibleMoveX = possibleMove[0];
			possibleMoveY = possibleMove[1];
			possibleMoveQValue = calculateQValue(weights, calculateFeatureVector(board, possibleMove));
			
			if (possibleMoveQValue > recommendedMoveQValue) {
				recommendedMoves.clear();
				recommendedMoves.add(possibleMove);
				recommendedMoveQValue = possibleMoveQValue;
			}
			else if (possibleMoveQValue == recommendedMoveQValue)
				recommendedMoves.add(possibleMove);
		}
		
		// Create the random number generator
		Random random = new Random();
		
		// If exploration is not flagged to occur, return a random advised move
		if (!epsilonExploration)
			return recommendedMoves.get(random.nextInt(recommendedMoves.size()));
		
		// Adjust epsilon for decay based on how long into the game it is
		double decayedEpsilon = EPSILON - (BoardAnalyzer.fractionFilled(board) * EPSILON);
		
		// Take a random advisable move with probability (1 - epsilon)
		double probabilityTakeAdvisedAction = 1 - decayedEpsilon;
		if (random.nextDouble() < probabilityTakeAdvisedAction)
			return recommendedMoves.get(random.nextInt(recommendedMoves.size()));
		
		// Otherwise, return a random exploratory move
		return possibleMoves.get(random.nextInt(possibleMoves.size()));
	}
	
	private double calculateDotProduct(double[] v1, double[] v2) throws Exception {
		// Assert the vectors are the same length
    	if (v1.length != v2.length)
    		throw new Exception("Error when performing dot product; vector lengths " + v1.length + ", " + v2.length + " are not the same!");
    	
    	// Perform the dot product
    	double result = 0;
    	for (int index = 0; index < v1.length; index++) {
    		result += v1[index] * v2[index];
    	}
    	
    	return result;
    }
}
