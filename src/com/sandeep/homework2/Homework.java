package com.sandeep.homework2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Homework {

	private int boardSize = 0;
	private int noOfFruits = 0;
	private float timeRemaining = 0;
	private char[][] inputBoard;
	private static final int minValue = -99999;

	private void runAlphaBetaPruning() {
		// it does alph beta pruning and gives best option to play with.
		Deque<Node> dfsStack = new ArrayDeque<>();
		Node initalNode = createInitalNode();
		dfsStack.push(initalNode);
		while (!dfsStack.isEmpty()) {
			Node node = dfsStack.pop();
			Pair maxEnergyNode = evaluate(node.getBoard());
			// char[][] updateBoard = getUpdatedBoard(node.getBoard(), maxEnergyNode);
		}
		char[][] updatedBoard = getUpdatedBoard(inputBoard, new Pair(1, 1));

		for (int i = 0; i < inputBoard.length; i++) {
			for (int j = 0; j < inputBoard.length; j++) {
				System.out.print(updatedBoard[i][j]);
			}
			System.out.println();
		}

	}

	private char[][] getUpdatedBoard(char[][] board, Pair maxEnergyNode) {

		char[][] updateBoard = cloneChar(board);
		boolean isTraversed[][] = new boolean[board.length][board.length];
		Deque<Pair> valuePairstack = new ArrayDeque<>();
		valuePairstack.addFirst(maxEnergyNode);
		Pair pair;
		int cellValue = getIntegerValueOfCell(board[maxEnergyNode.getxPosition()][maxEnergyNode.getyPosition()]);

		while (!valuePairstack.isEmpty()) {
			pair = valuePairstack.pop();

			int xvalue = pair.getxPosition();
			int yvalue = pair.getyPosition();
			isTraversed[xvalue][yvalue] = true;
			if (xvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue - 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue - 1, yvalue));
			} else if (xvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue + 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue + 1, yvalue));
			} else if (yvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue, yvalue - 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue - 1));
			} else if (yvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue, yvalue + 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue + 1));
			}

			updateBoard[xvalue][yvalue] = '*';
			for (int i = xvalue; i > 0; i--) {
				updateBoard[i][yvalue] = updateBoard[i - 1][yvalue];
			}
			updateBoard[0][yvalue] = '*';
		}

		return updateBoard;
	}

	private char[][] cloneChar(char[][] board) {

		char[][] newBoard = new char[board.length][board.length];

		for (int i = 0; i < newBoard.length; i++) {
			for (int j = 0; j < newBoard.length; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	private Pair evaluate(char[][] board) {

		boolean isTraversed[][] = new boolean[board.length][board.length];
		List<Pair> sortedPair = new ArrayList<>();
		Deque<Pair> valuePairstack;
		int maxEnergy = 0;
		Pair maxEnergyPiar = new Pair(0, 0);

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (isTraversed[i][j] == false && (getIntegerValueOfCell(board[i][j]) != minValue)) {
					valuePairstack = new ArrayDeque<>();
					int value = getIntegerValueOfCell(board[i][j]);
					valuePairstack.add(new Pair(i, j));
					int energy = getEnergy(value, board, isTraversed, valuePairstack);
					if (energy > maxEnergy) {
						maxEnergy = energy;
						maxEnergyPiar.setxPosition(i);
						maxEnergyPiar.setyPosition(j);
						maxEnergyPiar.setEnergy(maxEnergy);
					}
				}
			}
		}
		return maxEnergyPiar;
	}

	private int getFruitValue(char[][] board, Pair pair) {

		boolean isTraversed[][] = new boolean[board.length][board.length];
		Deque<Pair> valuePairstack = new ArrayDeque<>();
		int value = getIntegerValueOfCell(board[pair.getxPosition()][pair.getyPosition()]);
		valuePairstack.add(pair);
		return getEnergy(value, board, isTraversed, valuePairstack);
	}

	private int getEnergy(int cellValue, char[][] board, boolean[][] isTraversed, Deque<Pair> valuePairstack) {
		Pair pair = null;
		int nodeEnergy = 1;
		while (!valuePairstack.isEmpty()) {
			pair = valuePairstack.pop();

			int xvalue = pair.getxPosition();
			int yvalue = pair.getyPosition();

			isTraversed[xvalue][yvalue] = true;

			if (xvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue - 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue - 1, yvalue));
				nodeEnergy = nodeEnergy + 1;
			} else if (xvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue + 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue + 1, yvalue));
				nodeEnergy = nodeEnergy + 1;
			} else if (yvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue, yvalue - 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue - 1));
				nodeEnergy = nodeEnergy + 1;
			} else if (yvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue, yvalue + 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue + 1));
				nodeEnergy = nodeEnergy + 1;
			}
		}
		return nodeEnergy;
	}

	private boolean isConsistent(char[][] board, boolean[][] isTraversed, int xvalue, int yvalue, int cellValue) {
		return (getIntegerValueOfCell(board[xvalue][yvalue]) == cellValue && isTraversed[xvalue][yvalue] == false);
	}

	private int getIntegerValueOfCell(char charcter) {

		if (charcter != '*') {
			return Integer.parseInt(String.valueOf(charcter));
		}
		return minValue;
	}

	private Node createInitalNode() {
		Node node = new Node();
		node.setBoard(inputBoard);
		node.setChildren(getChildren(inputBoard));
		node.setOrderedChildren(sortListByUtility(node.getChildren()));
		node.setParent(null);
		node.setPruned(false);
		node.setDepth(0);
		return node;
	}

	private List<Node> sortListByUtility(List<Node> children) {
		return null;
	}

	private List<Node> getChildren(char[][] input) {

		List<Node> children = new ArrayList<>();
		return children;
	}

	private void readInputData() {
		Stream<String> stream = null;
		try {
			String currentDirectory = System.getProperty("user.dir");
			List<String> inputLines = new ArrayList<>();
			stream = Files.lines((Paths.get(currentDirectory + "/input.txt")));
			inputLines = stream.collect(Collectors.toList());
			convertToProblemVariables(inputLines);
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		} finally {
			stream.close();
		}
	}

	private void convertToProblemVariables(List<String> inputLines) {
		boardSize = Integer.parseInt(inputLines.remove(0));
		noOfFruits = Integer.parseInt(inputLines.remove(0));
		timeRemaining = Float.parseFloat(inputLines.remove(0));
		inputBoard = new char[boardSize][boardSize];
		String inputString;
		for (int i = 0; i < boardSize; i++) {
			inputString = inputLines.get(i);
			for (int j = 0; j < boardSize; j++) {
				inputBoard[i][j] = inputString.charAt(j);
			}
		}
	}

	private void printInput() {
		System.out.println("Input size " + boardSize);
		System.out.println("Number of Fruits " + noOfFruits);
		System.out.println("Time remaining " + timeRemaining);
		for (int i = 0; i < inputBoard.length; i++) {
			for (int j = 0; j < inputBoard.length; j++) {
				System.out.print(inputBoard[i][j]);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		Homework homework = new Homework();
		homework.readInputData();
		homework.printInput();
		homework.runAlphaBetaPruning();
	}

	class Node {

		private float utility; // value you get after doing min max with children
		private char[][] board; // the state of board at this particular node
		private List<Node> children; // the list of children nodes available
		private List<Node> orderedChildren; // the list of children nodes in the order of the utility
		private Node parent; //
		private Pair selectedPosition;
		private boolean isPruned;
		private int depth;

		public float getUtility() {
			return utility;
		}

		public void setUtility(float utility) {
			this.utility = utility;
		}

		public char[][] getBoard() {
			return board;
		}

		public void setBoard(char[][] board) {
			this.board = board;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}

		public List<Node> getOrderedChildren() {
			return orderedChildren;
		}

		public void setOrderedChildren(List<Node> orderedChildren) {
			this.orderedChildren = orderedChildren;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public Pair getSelectedPosition() {
			return selectedPosition;
		}

		public void setSelectedPosition(Pair selectedPosition) {
			this.selectedPosition = selectedPosition;
		}

		public boolean isPruned() {
			return isPruned;
		}

		public void setPruned(boolean isPruned) {
			this.isPruned = isPruned;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

	}

	class Cell {
		private boolean isTraversed = false;
		private char value;

		public boolean isTraversed() {
			return isTraversed;
		}

		public void setTraversed(boolean isTraversed) {
			this.isTraversed = isTraversed;
		}

		public char getValue() {
			return value;
		}

		public void setValue(char value) {
			this.value = value;
		}
	}

	class Pair {
		private int xPosition;
		private int yPosition;
		private int energy;

		public Pair(int xPosition, int yPosition) {
			super();
			this.xPosition = xPosition;
			this.yPosition = yPosition;
		}

		public Pair(int xPosition, int yPosition, int energy) {
			super();
			this.xPosition = xPosition;
			this.yPosition = yPosition;
			this.energy = energy;
		}

		public int getxPosition() {
			return xPosition;
		}

		public void setxPosition(int xPosition) {
			this.xPosition = xPosition;
		}

		public int getyPosition() {
			return yPosition;
		}

		public void setyPosition(int yPosition) {
			this.yPosition = yPosition;
		}

		public int getEnergy() {
			return energy;
		}

		public void setEnergy(int energy) {
			this.energy = energy;
		}
	}
}