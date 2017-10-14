package com.sandeep.homework2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Homework {

	private int boardSize = 0;
	private int noOfFruits = 0;
	private float timeRemaining = 0;
	private char[][] inputBoard;
	private static final int minValue = -99999;

	private Node runAlphaBetaPruning(int maxDepth) {
		Node initalNode = createInitalNode();
		Node finalNode = maxNode(Integer.MIN_VALUE, Integer.MAX_VALUE, maxDepth, initalNode);
		System.out.println("FInal node is " + finalNode.getSelectedPosition().getxPosition() + " "
				+ finalNode.getSelectedPosition().getyPosition());
		return finalNode;
	}

	private Node maxNode(int alpha, int beta, int maxDepth, Node node) {

		Node maxValue = new Node();
		node.setUtility(Integer.MIN_VALUE);

		if (isTerminal(node, maxDepth)) {
			node.setUtility(evaluate(node));
			node.setTotalScore(node.getScore() + node.getUtility());
			return node;
		} else {
			List<Node> childrenList = getChildrenNodes(node);
			for (Node childNode : childrenList) {
				maxValue = nodeMax(maxValue, minNode(alpha, beta, maxDepth, childNode));
				if (maxValue.getTotalScore() >= beta) {
					System.out.println("Prune");
					return maxValue;
				}
				alpha = Integer.max(alpha, maxValue.getTotalScore());
			}
		}
		return maxValue;
	}

	private Node nodeMax(Node maxValue, Node minNode) {

		if (maxValue.getTotalScore() >= minNode.getTotalScore()) {
			return maxValue;
		} else {
			return minNode;
		}
	}

	private Node minNode(int alpha, int beta, int maxDepth, Node node) {
		Node minValue = new Node();
		node.setUtility(Integer.MAX_VALUE);

		if (isTerminal(node, maxDepth)) {
			node.setUtility(evaluate(node));
			node.setTotalScore(node.getScore() + node.getUtility());
			return node;
		} else {
			List<Node> childrenList = getChildrenNodes(node);
			for (Node childNode : childrenList) {
				minValue = nodeMin(minValue, maxNode(alpha, beta, maxDepth, childNode));
				if (minValue.getTotalScore() <= alpha) {
					System.out.println("Prune");
					return minValue;
				}
				beta = Integer.min(minValue.getTotalScore(), beta);
			}
		}
		return minValue;
	}

	private Node nodeMin(Node minValue, Node maxNode) {
		if (minValue.getTotalScore() <= maxNode.getTotalScore()) {
			return minValue;
		} else {
			return maxNode;
		}
	}

	private boolean isTerminal(Node node, int maxDepth) {

		if (node.getDepth() >= maxDepth) {
			return true;
		}
		return false;
	}

	private void printNode(Node node2) {
		System.out.println("Score is " + node2.selectedPosition.getEnergy() + "x is "
				+ node2.getSelectedPosition().xPosition + "y is " + node2.getSelectedPosition().yPosition);
	}

	private boolean areTwoComponentsConnected(char[][] board, Pair inital, Pair next) {

		if (inital.getxPosition() == next.getxPosition() && inital.getyPosition() == next.getyPosition()) {
			return true;
		}

		boolean isTraversed[][] = new boolean[board.length][board.length];
		Deque<Pair> valuePairstack = new ArrayDeque<>();
		isTraversed[inital.getxPosition()][inital.getyPosition()] = true;
		valuePairstack.addFirst(inital);
		Pair pair;
		int cellValue = getIntegerValueOfCell(board[inital.getxPosition()][inital.getyPosition()]);

		while (!valuePairstack.isEmpty()) {
			pair = valuePairstack.pop();

			int xvalue = pair.getxPosition();
			int yvalue = pair.getyPosition();

			if (xvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue - 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue - 1, yvalue));
				isTraversed[xvalue - 1][yvalue] = true;

				if (xvalue - 1 == next.getxPosition() && yvalue == next.getyPosition()) {
					return true;
				}
			}
			if (xvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue + 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue + 1, yvalue));
				isTraversed[xvalue + 1][yvalue] = true;

				if (xvalue + 1 == next.getxPosition() && yvalue == next.getyPosition()) {
					return true;
				}
			}
			if (yvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue, yvalue - 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue - 1));
				isTraversed[xvalue][yvalue - 1] = true;

				if (xvalue == next.getxPosition() && yvalue - 1 == next.getyPosition()) {
					return true;
				}
			}
			if (yvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue, yvalue + 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue + 1));
				isTraversed[xvalue][yvalue + 1] = true;

				if (xvalue == next.getxPosition() && yvalue + 1 == next.getyPosition()) {
					return true;
				}
			}
		}
		return false;
	}

	private List<Node> getChildrenNodes(Node parentNode) {

		char[][] board = parentNode.getBoard();

		Map<Integer, List<Pair>> scoreList = new HashMap<>();

		for (int i = 0; i < board.length; i++) {
			List<Pair> topNodes = getTopChildrenForARow(i, board);
			for (Pair pair : topNodes) {
				if (!compareAPairWithHashTable(board, pair, scoreList)) {
					if (scoreList.get(pair.getEnergy()) == null) {
						scoreList.put(pair.getEnergy(), new ArrayList<Pair>());
					}
					scoreList.get(pair.getEnergy()).add(pair);
				}
			}
		}
		for (int i = 0; i < board.length; i++) {
			List<Pair> topNodes = getTopChildrenForAColumn(i, board);
			for (Pair pair : topNodes) {
				if (!compareAPairWithHashTable(board, pair, scoreList)) {
					if (scoreList.get(pair.getEnergy()) == null) {
						scoreList.put(pair.getEnergy(), new ArrayList<Pair>());
					}
					scoreList.get(pair.getEnergy()).add(pair);
				}
			}
		}
		return sortedHashMap(scoreList, parentNode);
	}

	private List<Node> sortedHashMap(Map<Integer, List<Pair>> scoreList, Node parentNode) {

		List<Node> sortedList = new ArrayList<>();
		Map<Integer, List<Pair>> sortedHashList = new TreeMap<>(scoreList).descendingMap();
		sortedHashList.forEach((k, v) -> sortedList.addAll(createNodesFromPair(v, parentNode)));
		return sortedList;
	}

	private List<Node> createNodesFromPair(List<Pair> pairList, Node parentNode) {

		List<Node> nodeList = new ArrayList<>();
		for (Pair pair : pairList) {
			Node node = new Node();
			node.setParent(parentNode);
			node.setBoard(getUpdatedBoard(parentNode.getBoard(), pair));
			node.setDepth(parentNode.getDepth() + 1);
			node.setNodetype(parentNode.getNodetype().equals(NodeType.MAX) ? NodeType.MIN : NodeType.MAX);
			node.setSelectedPosition(pair);
			node.setScore(parentNode.getNodetype().equals(NodeType.MAX) ? parentNode.score + pair.getEnergy()
					: parentNode.score - pair.getEnergy());
			nodeList.add(node);
		}
		return nodeList;
	}

	private boolean compareAPairWithHashTable(char[][] board, Pair pair, Map<Integer, List<Pair>> scoreList) {

		List<Pair> hashList = scoreList.get(pair.getEnergy());
		if (hashList != null) {
			for (Pair hashPair : hashList) {
				if (areTwoComponentsConnected(board, pair, hashPair)) {
					return true;
				}
			}
		}
		return false;
	}

	private List<Pair> getTopChildrenForARow(int row, char[][] board) {

		List<Pair> topRowNodes = new ArrayList<>();
		for (int j = 0; j < board.length; j++) {
			Pair pair = new Pair(row, j);
			pair.setEnergy(getFruitValue(board, pair));

			if (ifNotConnectedInAList(board, pair, topRowNodes)) {
				topRowNodes.add(pair);
			}
		}
		topRowNodes.sort((Comparator.comparingInt(Pair::getEnergy).reversed()));
		// printPair(topRowNodes.size() >= 2 ? topRowNodes.subList(0, 2) : topRowNodes);
		return (topRowNodes.size() >= 2 ? topRowNodes.subList(0, 2) : topRowNodes);
	}

	private void printPair(List<Pair> topRowNodes) {

		for (Pair pair : topRowNodes) {
			System.out.println("x is " + pair.xPosition + " y is " + pair.yPosition + " Score is " + pair.getEnergy());
		}
	}

	private List<Pair> getTopChildrenForAColumn(int column, char[][] board) {

		List<Pair> topColumnNodes = new ArrayList<>();
		for (int j = 0; j < board.length; j++) {
			Pair pair = new Pair(j, column);
			pair.setEnergy(getFruitValue(board, pair));

			if (ifNotConnectedInAList(board, pair, topColumnNodes)) {
				topColumnNodes.add(pair);
			}
		}
		topColumnNodes.sort((Comparator.comparingInt(Pair::getEnergy).reversed()));
		// printPair(topColumnNodes.size() >= 2 ? topColumnNodes.subList(0, 2) :
		// topColumnNodes);
		return (topColumnNodes.size() >= 2 ? topColumnNodes.subList(0, 2) : topColumnNodes);
	}

	private boolean ifNotConnectedInAList(char[][] board, Pair pair, List<Pair> topColumnNodes) {

		for (Pair iterativePair : topColumnNodes) {
			if (pair.getEnergy() == iterativePair.getEnergy()) {
				if (areTwoComponentsConnected(board, pair, iterativePair)) {
					return false;
				}
			}
		}
		return true;
	}

	private char[][] getUpdatedBoard(char[][] board, Pair maxEnergyNode) {

		char[][] updateBoard = cloneChar(board);
		boolean isTraversed[][] = new boolean[board.length][board.length];
		Deque<Pair> valuePairstack = new ArrayDeque<>();
		isTraversed[maxEnergyNode.getxPosition()][maxEnergyNode.getyPosition()] = true;
		valuePairstack.addFirst(maxEnergyNode);
		Pair pair;
		int cellValue = getIntegerValueOfCell(board[maxEnergyNode.getxPosition()][maxEnergyNode.getyPosition()]);

		while (!valuePairstack.isEmpty()) {
			pair = valuePairstack.pop();

			int xvalue = pair.getxPosition();
			int yvalue = pair.getyPosition();

			if (xvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue - 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue - 1, yvalue));
				isTraversed[xvalue - 1][yvalue] = true;
			}
			if (xvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue + 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue + 1, yvalue));
				isTraversed[xvalue + 1][yvalue] = true;
			}
			if (yvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue, yvalue - 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue - 1));
				isTraversed[xvalue][yvalue - 1] = true;
			}
			if (yvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue, yvalue + 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue + 1));
				isTraversed[xvalue][yvalue + 1] = true;
			}
			updateBoard[xvalue][yvalue] = '*';
		}

		for (int i = 0; i < updateBoard.length; i++) {
			List<Character> fruitArray = getUpdatedCharArray(updateBoard, i);
			int numberOfStars = updateBoard.length - fruitArray.size();
			for (int j = 0; j < numberOfStars; j++) {
				updateBoard[j][i] = '*';
			}
			for (int k = numberOfStars; k < updateBoard.length; k++) {
				updateBoard[k][i] = fruitArray.remove(0);
			}
		}

		return updateBoard;
	}

	private List<Character> getUpdatedCharArray(char[][] updateBoard, int columnNumber) {

		List<Character> charList = new ArrayList<>();
		for (int i = 0; i < updateBoard.length; i++) {
			if (updateBoard[i][columnNumber] != '*') {
				charList.add(updateBoard[i][columnNumber]);
			}
		}
		return charList;
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

	private int evaluate(Node node) {

		char[][] board = node.getBoard();
		boolean isTraversed[][] = new boolean[board.length][board.length];
		List<Integer> sortedPair = new ArrayList<>();
		Deque<Pair> valuePairstack;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (isTraversed[i][j] == false && (getIntegerValueOfCell(board[i][j]) != minValue)) {
					valuePairstack = new ArrayDeque<>();
					int value = getIntegerValueOfCell(board[i][j]);
					valuePairstack.add(new Pair(i, j));
					isTraversed[i][j] = true;
					int energy = getEnergy(value, board, isTraversed, valuePairstack);
					sortedPair.add(energy);
				}
			}
		}
		Collections.sort(sortedPair, Collections.reverseOrder());
		return getEnergy(sortedPair, node.getNodetype());

	}

	private int getEnergy(List<Integer> sortedPair, NodeType nodetype) {

		switch (nodetype) {
		case MAX:

			if (sortedPair.size() >= 2) {
				return sortedPair.get(0) - sortedPair.get(1);
			} else if (sortedPair.size() == 1) {
				return sortedPair.get(0);
			} else {
				return 0;
			}
		case MIN:
			if (sortedPair.size() >= 2) {
				return sortedPair.get(1) - sortedPair.get(0);
			} else if (sortedPair.size() == 1) {
				return 0 - sortedPair.get(0);
			} else {
				return 0;
			}
		}
		return 0;

	}

	private int getFruitValue(char[][] board, Pair pair) {
		boolean isTraversed[][] = new boolean[board.length][board.length];
		Deque<Pair> valuePairstack = new ArrayDeque<>();
		int value = getIntegerValueOfCell(board[pair.getxPosition()][pair.getyPosition()]);
		valuePairstack.add(pair);
		isTraversed[pair.getxPosition()][pair.getyPosition()] = true;
		return getEnergy(value, board, isTraversed, valuePairstack);
	}

	private int getEnergy(int cellValue, char[][] board, boolean[][] isTraversed, Deque<Pair> valuePairstack) {
		Pair pair = null;
		int nodeEnergy = 1;
		while (!valuePairstack.isEmpty()) {
			pair = valuePairstack.pop();

			int xvalue = pair.getxPosition();
			int yvalue = pair.getyPosition();

			if (xvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue - 1, yvalue, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue - 1, yvalue));
				nodeEnergy = nodeEnergy + 1;
				isTraversed[xvalue - 1][yvalue] = true;
			}
			if ((xvalue + 1 < boardSize) && (isConsistent(board, isTraversed, xvalue + 1, yvalue, cellValue))) {
				valuePairstack.addFirst(new Pair(xvalue + 1, yvalue));
				nodeEnergy = nodeEnergy + 1;
				isTraversed[xvalue + 1][yvalue] = true;
			}
			if (yvalue - 1 != -1 && isConsistent(board, isTraversed, xvalue, yvalue - 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue - 1));
				nodeEnergy = nodeEnergy + 1;
				isTraversed[xvalue][yvalue - 1] = true;
			}
			if (yvalue + 1 < boardSize && isConsistent(board, isTraversed, xvalue, yvalue + 1, cellValue)) {
				valuePairstack.addFirst(new Pair(xvalue, yvalue + 1));
				nodeEnergy = nodeEnergy + 1;
				isTraversed[xvalue][yvalue + 1] = true;
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
		node.setDepth(0);
		node.setNodetype(NodeType.MAX);
		node.setParent(null);

		return node;
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
		int maxDepth = homework.calculateDepth();
		Node outputNode = homework.runAlphaBetaPruning(maxDepth);
		// Node outputNode =homework.runIterativeDeepening(maxDepth);
		try {
			homework.writeOutput(outputNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeOutput(Node outputNode) throws IOException {

		List<String> lines = new ArrayList<>();
		char[][] outputBoard = outputNode.getBoard();
		lines.add(String.valueOf((char) ((int) 'A' + outputNode.getSelectedPosition().getyPosition()))
				+ outputNode.getSelectedPosition().getxPosition());
		for (int i = 0; i < outputNode.getBoard().length; i++) {
			StringBuilder string = new StringBuilder();
			for (int j = 0; j < outputNode.getBoard().length; j++) {
				string.append(outputBoard[i][j]);
			}
			lines.add(string.toString());
		}
		Path file = Paths.get("output.txt");
		Files.write(file, lines, Charset.forName("UTF-8"));
	}

	private void runIterativeDeepening(int maxDepth) {

		for (int i = 0; i < maxDepth; i++) {
			runAlphaBetaPruning(i + 1);
		}

	}

	private int calculateDepth() {
		return 1;
	}

	public enum NodeType {
		MIN, MAX;
	}

	class Node {

		private int utility; // value you get after doing min max with children
		private int score;
		private char[][] board; // the state of board at this particular node
		private Node parent; //
		private Pair selectedPosition;
		private int depth;
		private int totalScore;
		private NodeType nodetype;

		public int getUtility() {
			return utility;
		}

		public void setUtility(int utility) {
			this.utility = utility;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public char[][] getBoard() {
			return board;
		}

		public void setBoard(char[][] board) {
			this.board = board;
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

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public NodeType getNodetype() {
			return nodetype;
		}

		public void setNodetype(NodeType nodetype) {
			this.nodetype = nodetype;
		}

		public int getTotalScore() {
			return totalScore;
		}

		public void setTotalScore(int totalScore) {
			this.totalScore = totalScore;
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