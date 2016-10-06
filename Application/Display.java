package Application;

import java.awt.Color;
import java.awt.Graphics;

import Application.Board.Token;
import ZClasses.ZDisplay;

public class Display extends ZDisplay {
	public static final int BOARD_POSITION_DIMENSION = 40;
	
	private static final Color BACKGROUND_COLOR = new Color(200, 200, 200);
	
	private boolean threadToggle;
	
	public Display(int dimX, int dimY, int boardDimension) {
		super(dimX, dimY);
		
		threadToggle = false;
	}
	
	private int getIdFromPosition(int x, int y) {
		return (y * Main.BOARD_DIMENSION) + x;
	}
	
	public void updateBoard(Board board) {
		for (int xIndex = 0; xIndex < Main.BOARD_DIMENSION; xIndex++) {
			for (int yIndex = 0; yIndex < Main.BOARD_DIMENSION; yIndex++) {
				getFields().get(getFields().IDOf(getIdFromPosition(xIndex, yIndex))).setElement(board.getTokenAt(xIndex, yIndex));
			}
		}
		
		repaint();
	}

	@Override
	public void setup() {
		getButtons().addNewLockedButton(1, "Step Move", 45, 10 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION), 5, 150, 30, new Color(200, 200, 255), Color.BLACK);
		getButtons().addNewLockedButton(2, "Start Thread", 45, 10 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION), 40, 150, 30, new Color(200, 255, 200), Color.BLACK);
		
		int xPos;
		int yPos;
		for (int xIndex = 0; xIndex < Main.BOARD_DIMENSION; xIndex++) {
			for (int yIndex = 0; yIndex < Main.BOARD_DIMENSION; yIndex++) {
				xPos = 10 + ((BOARD_POSITION_DIMENSION + 5) * xIndex);
				yPos = 10 + ((BOARD_POSITION_DIMENSION + 5) * yIndex);
				getFields().addNewLockedField(getIdFromPosition(xIndex, yIndex), Token.NONE, xPos, yPos, BOARD_POSITION_DIMENSION, BOARD_POSITION_DIMENSION, BACKGROUND_COLOR, BACKGROUND_COLOR);
			}
		}
	}

	@Override
	public void paintDisplay(Graphics g) {
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, getXDimension(), getYDimension());
		
		g.setColor(Color.BLACK);
		for (int index = 1; index < Main.BOARD_DIMENSION; index++) {
			g.drawLine(7 + ((BOARD_POSITION_DIMENSION + 5) * index), 10, 7 + ((BOARD_POSITION_DIMENSION + 5) * index), 5 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION));
			g.drawLine(8 + ((BOARD_POSITION_DIMENSION + 5) * index), 10, 8 + ((BOARD_POSITION_DIMENSION + 5) * index), 5 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION));
		}
		for (int index = 1; index < Main.BOARD_DIMENSION; index++) {
			g.drawLine(10, 7 + ((BOARD_POSITION_DIMENSION + 5) * index), 5 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION), 7 + ((BOARD_POSITION_DIMENSION + 5) * index));
			g.drawLine(10, 8 + ((BOARD_POSITION_DIMENSION + 5) * index), 5 + ((BOARD_POSITION_DIMENSION + 5) * Main.BOARD_DIMENSION), 8 + ((BOARD_POSITION_DIMENSION + 5) * index));
		}
	}

	@Override
	public void paintElements(Graphics g, Object obj, int x, int y, int fieldID, int fieldAssignedID) {
		switch ((Token) obj) {
			case X:
				g.setColor(Color.RED);
				g.drawLine(x + 2, y + 4, x + BOARD_POSITION_DIMENSION - 4, y + BOARD_POSITION_DIMENSION - 2);
				g.drawLine(x + 3, y + 3, x + BOARD_POSITION_DIMENSION - 3, y + BOARD_POSITION_DIMENSION - 3);
				g.drawLine(x + 4, y + 2, x + BOARD_POSITION_DIMENSION - 2, y + BOARD_POSITION_DIMENSION - 4);
				g.drawLine(x + 2, y + BOARD_POSITION_DIMENSION - 4, x + BOARD_POSITION_DIMENSION - 4, y + 2);
				g.drawLine(x + 3, y + BOARD_POSITION_DIMENSION - 3, x + BOARD_POSITION_DIMENSION - 3, y + 3);
				g.drawLine(x + 4, y + BOARD_POSITION_DIMENSION - 2, x + BOARD_POSITION_DIMENSION - 2, y + 4);
				break;
			case CIRCLE:
				g.setColor(Color.BLUE);
				g.drawOval(x + 1, y + 1, BOARD_POSITION_DIMENSION - 2, BOARD_POSITION_DIMENSION - 2);
				g.drawOval(x + 2, y + 2, BOARD_POSITION_DIMENSION - 4, BOARD_POSITION_DIMENSION - 4);
				g.drawOval(x + 3, y + 3, BOARD_POSITION_DIMENSION - 6, BOARD_POSITION_DIMENSION - 6);
				break;
		}
	}

	@Override
	public void buttonEvent(int buttonID, int assignedID) {
		switch (assignedID) {
			case 1:
				Main.runTurn();
				break;
			case 2:
				threadToggle = !threadToggle;
				Main.toggleThread(threadToggle);
				
				if (threadToggle) {
					getButtons().get(buttonID).setLabel("Stop Thread");
					getButtons().get(buttonID).setLabelOffset(45);
					getButtons().get(buttonID).setButtonColor(new Color(255, 200, 200));
				}
				else {
					getButtons().get(buttonID).setLabel("Start Thread");
					getButtons().get(buttonID).setLabelOffset(45);
					getButtons().get(buttonID).setButtonColor(new Color(200, 255, 200));
				}
				break;
		}
	}

	@Override
	public void fieldEvent(int fieldID, int assignedID) {
		Main.processInterfaceMove(assignedID % Main.BOARD_DIMENSION, assignedID / Main.BOARD_DIMENSION);
	}

	@Override
	public void textFieldTypeEvent(int textfieldID, int assignedID, String text, boolean insertion) {
		 
	}

	@Override
	public void textFieldEnterEvent(int textfieldID, int assignedID, String text) {
		 
	}

	@Override
	public void dropDownSelectionEvent(int dropdownlistID, int assignedID, int index, Object item) {
		
	}

	@Override
	public void typeEvent(boolean pressed, int keycode) {
		
	}

	@Override
	public void onResize() {
		 
	}
}
