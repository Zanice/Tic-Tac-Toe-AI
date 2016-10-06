package Application;

public class Board {
	public static enum Token {
		NONE,
		X,
		CIRCLE
	}
	
	private final int dimension;
	
	private Token[][] board;
	
	public Board(int dimension) {
		this.dimension = dimension;
		
		board = new Token[dimension][dimension];
		resetBoard();
	}
	
	public Board(Board oldBoard) {
		dimension = oldBoard.dimension;
		
		board = new Token[dimension][dimension];
		setBoardByMatrix(oldBoard.board);
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public Token getTokenAt(int x, int y) {
		return board[x][y];
	}
	
	public void resetBoard() {
		for (int xIndex = 0; xIndex < dimension; xIndex++) {
			for (int yIndex = 0; yIndex < dimension; yIndex++) {
				board[xIndex][yIndex] = Token.NONE;
			}
		}
	}
	
	public void setBoardByMatrix(Token[][] matrix) {
		for (int xIndex = 0; xIndex < dimension; xIndex++) {
			for (int yIndex = 0; yIndex < dimension; yIndex++) {
				board[xIndex][yIndex] = matrix[xIndex][yIndex];
			}
		}
	}
	
	public boolean isPositionOnBoard(int x, int y) {
		boolean validX = 0 <= x && x < dimension;
		boolean validY = 0 <= y && y < dimension;
		
		return validX && validY;
	}
	
	public boolean isValidMove(int x, int y) {
		// If the position does not exist on the board, fail the attempt
		if (!isPositionOnBoard(x, y))
			return false;
		
		// If the specified position already has a token, fail the attempt
		if (board[x][y] != Token.NONE)
			return false;
		
		// Otherwise, the move is valid
		return true;
	}
	
	public Board createBoardAfterMove(int x, int y, Token token) {
		// If the move is not valid, return null
		if (!isValidMove(x, y))
			return null;
		
		// Otherwise, create a copy of the board and carry out the move on the copy
		Board newBoard = new Board(this);
		newBoard.board[x][y] = token;
		
		// Return the board with the carried out move
		return newBoard;
	}
}
