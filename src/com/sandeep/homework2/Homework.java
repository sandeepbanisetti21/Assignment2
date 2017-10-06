package com.sandeep.homework2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Homework {

	private int boardSize = 0;
	private int noOfFruits = 0;
	private float timeRemaining = 0;
	private char[][] inputBoard;

	private void run() {

		Node initalNode = createInitalNode();

	}

	private Node createInitalNode() {

		Node node = new Node();
		node.setBoard(inputBoard);
		node.setChildren(getChildren(inputBoard));
		node.setOrderedChildren(sortListByUtility(node.getChildren()));
		node.setParent(null);

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
		homework.run();
	}

	class Node {

		private float utility; // value you get after doing min max with children
		private char[][] board; // the state of board at this particular node
		private List<Node> children; // the list of children nodes available
		private List<Node> orderedChildren; // the list of children nodes in the order of the utility
		private Node parent; //
		private Pair selectedPosition;
		private boolean isPruned;

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
	}

	class Pair {
		private int xPosition;
		private int yPosition;

		public Pair(int xPosition, int yPosition) {
			super();
			this.xPosition = xPosition;
			this.yPosition = yPosition;
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
	}
}