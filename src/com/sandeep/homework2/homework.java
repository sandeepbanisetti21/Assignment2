package com.sandeep.homework2;

//package com.sandeep.homework2;

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

public class homework {

	private int boardSize = 0;
	private int noOfFruits = 0;
	private float timeRemaining = 0;
	private char[][] inputBoard;
	private static final int minValue = -99999;

	private Node runAlphaBetaPruning(int maxDepth) {
		Node initalNode = createInitalNode();
		Node finalNode = maxNode(initalNode.getBoard(), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, maxDepth, 0);
		System.out.println("Score is " + finalNode.getSelectedPosition().energy + " x is "
				+ finalNode.getSelectedPosition().getxPosition() + " y is "
				+ finalNode.getSelectedPosition().getyPosition());
		System.out.println(
				"Value is " + getIntegerValueOfCell(inputBoard[finalNode.getSelectedPosition().getxPosition()][finalNode
						.getSelectedPosition().getyPosition()]));
		return finalNode;
	}

	private Node maxNode(char[][] board, int currentScore, int alpha, int beta, int maxDepth, int depth) {

		Node maxValue = new Node();
		int bestScore = Integer.MIN_VALUE;
		maxValue.setTotalScore(bestScore);
		char[][] newBoard;

		if (depth >= maxDepth) {
			Node updatedNode = new Node();
			updatedNode.setSelectedPosition(null);
			updatedNode.setTotalScore(currentScore + evaluate(board, NodeType.MAX));
			printNodeMinMax(updatedNode);
			return updatedNode;
		}

		List<Pair> childrenList = getChildrenNodes(new Node(board, new Pair(), 0));
		if (isTerminal(board, childrenList)) {
			if (childrenList.size() == 0) {
				Node updatedNode = new Node();
				updatedNode.setTotalScore(currentScore);
				return updatedNode;
			} else {
				Node childrenNode = new Node();
				childrenNode.setTotalScore(currentScore + evaluate(board, NodeType.MAX));
				childrenNode.setSelectedPosition(childrenList.get(0));
				return childrenNode;
			}
		}
		for (Pair childrenNode : childrenList) {
			newBoard = getUpdatedBoard(board, childrenNode);
			maxValue = nodeMax(maxValue,
					minNode(newBoard, currentScore + childrenNode.getEnergy(), alpha, beta, maxDepth, depth + 1));

			if (maxValue.getTotalScore() > bestScore) {
				bestScore = maxValue.getTotalScore();
				maxValue.setSelectedPosition(childrenNode);
			}

			if (maxValue.getTotalScore() >= beta) {
				System.out.println("Prune");
				printNodeMinMax(maxValue);
				return maxValue;
			}
			alpha = Integer.max(alpha, maxValue.getTotalScore());
		}
		printNodeMinMax(maxValue);
		return maxValue;
	}

	private Node minNode(char[][] board, int currentScore, int alpha, int beta, int maxDepth, int depth) {

		Node minValue = new Node();
		int bestScore = Integer.MAX_VALUE;
		minValue.setTotalScore(bestScore);
		char[][] newBoard;

		if (depth >= maxDepth) {
			Node updatedNode = new Node();
			updatedNode.setSelectedPosition(null);
			updatedNode.setTotalScore(currentScore + evaluate(board, NodeType.MIN));
			printNodeMinMax(updatedNode);
			return updatedNode;
		}

		List<Pair> childrenList = getChildrenNodes(new Node(board, new Pair(), 0));
		if (isTerminal(board, childrenList)) {
			if (childrenList.size() == 0) {
				Node updatedNode = new Node();
				updatedNode.setTotalScore(currentScore);
				return updatedNode;
			} else {
				Node childrenNode = new Node();
				childrenNode.setTotalScore(currentScore + evaluate(board, NodeType.MIN));
				childrenNode.setSelectedPosition(childrenList.get(0));
				return childrenNode;
			}
		}
		for (Pair childrenNode : childrenList) {
			newBoard = getUpdatedBoard(board, childrenNode);
			minValue = nodeMin(minValue,
					maxNode(newBoard, currentScore - childrenNode.getEnergy(), alpha, beta, maxDepth, depth + 1));

			if (minValue.getTotalScore() > bestScore) {
				bestScore = minValue.getTotalScore();
				minValue.setSelectedPosition(childrenNode);
			}

			if (minValue.getTotalScore() >= beta) {
				System.out.println("Prune");
				printNodeMinMax(minValue);
				return minValue;
			}
			alpha = Integer.max(alpha, minValue.getTotalScore());
		}
		printNodeMinMax(minValue);
		return minValue;
	}

	private Node nodeMax(Node maxValue, Node minNode) {

		if (maxValue == null) {
			return minNode;
		}
		if (minNode == null) {
			return maxValue;
		}
		if (maxValue.getTotalScore() >= minNode.getTotalScore()) {
			return maxValue;
		} else {
			return minNode;
		}
	}

	private Node nodeMin(Node minValue, Node maxNode) {

		if (minValue == null) {
			return maxNode;
		}
		if (maxNode == null) {
			return minValue;
		}
		if (minValue.getTotalScore() <= maxNode.getTotalScore()) {
			return minValue;
		} else {
			return maxNode;
		}
	}

	private boolean isTerminal(char[][] board, List<Pair> childrenList) {

		char[][] newBoard;
		if (childrenList.size() <= 1) {
			return true;
		}
		newBoard = getUpdatedBoard(board, childrenList.get(0));
		if (isEndBoard(newBoard)) {
			return true;
		}
		return false;
	}

	private boolean isEndBoard(char[][] board) {

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (getIntegerValueOfCell(board[i][j]) != minValue) {
					return false;
				}
			}
		}
		return true;
	}

	private void printNode(Node node2) {
		System.out.println("Score is " + node2.selectedPosition.getEnergy() + "x is "
				+ node2.getSelectedPosition().xPosition + "y is " + node2.getSelectedPosition().yPosition);
	}

	private void printNodeMinMax(Node node2) {

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

	private List<Pair> getChildrenNodes(Node parentNode) {

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

	private List<Pair> sortedHashMap(Map<Integer, List<Pair>> scoreList, Node parentNode) {

		List<Pair> sortedList = new ArrayList<>();
		Map<Integer, List<Pair>> sortedHashList = new TreeMap<>(scoreList).descendingMap();
		sortedHashList.forEach((k, v) -> sortedList.addAll(v));
		return sortedList;
	}

	private List<Node> createNodesFromPair(List<Pair> pairList, Node parentNode) {

		List<Node> nodeList = new ArrayList<>();
		for (Pair pair : pairList) {
			Node node = new Node();
			node.setSelectedPosition(pair);
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
			if (getIntegerValueOfCell(board[row][j]) != minValue) {
				Pair pair = new Pair(row, j);
				pair.setEnergy(getFruitValue(board, pair));
				if (ifNotConnectedInAList(board, pair, topRowNodes)) {
					topRowNodes.add(pair);
				}
			}
		}
		topRowNodes.sort((Comparator.comparingInt(Pair::getEnergy).reversed()));
		// printPair(topRowNodes.size() >= 2 ? topRowNodes.subList(0, 2) : topRowNodes);
		return (topRowNodes.size() >= 2 ? topRowNodes.subList(0, 2) : topRowNodes);
	}

	private List<Pair> getTopChildrenForAColumn(int column, char[][] board) {

		List<Pair> topColumnNodes = new ArrayList<>();
		for (int j = 0; j < board.length; j++) {
			if (getIntegerValueOfCell(board[j][column]) != minValue) {
				Pair pair = new Pair(j, column);
				pair.setEnergy(getFruitValue(board, pair));

				if (ifNotConnectedInAList(board, pair, topColumnNodes)) {
					topColumnNodes.add(pair);
				}
			}
		}
		topColumnNodes.sort((Comparator.comparingInt(Pair::getEnergy).reversed()));
		// printPair(topColumnNodes.size() >= 2 ? topColumnNodes.subList(0, 2) :
		// topColumnNodes);
		return (topColumnNodes.size() >= 2 ? topColumnNodes.subList(0, 2) : topColumnNodes);
	}

	private void printPair(List<Pair> topRowNodes) {

		for (Pair pair : topRowNodes) {
			System.out.println("x is " + pair.xPosition + " y is " + pair.yPosition + " Score is " + pair.getEnergy());
		}
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

	private int evaluate(char[][] board, NodeType nodeType) {

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
		return getMinMaxEnergy(sortedPair, nodeType);

	}

	private int getMinMaxEnergy(List<Integer> sortedPair, NodeType nodetype) {

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

		homework homework = new homework();
		homework.run();
	}

	private void run2() {
		readInputData();
		printInput();
		System.out.println(evaluate(inputBoard, NodeType.MIN));
	}

	private void run() {
		readInputData();
		printInput();
		int maxDepth = calculateDepth();
		Node outputNode = runAlphaBetaPruning(maxDepth);
		// Node outputNode =homework.runIterativeDeepening(maxDepth);
		try {
			writeOutput(outputNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeOutput(Node outputNode) throws IOException {

		List<String> lines = new ArrayList<>();
		char[][] outputBoard = getUpdatedBoard(inputBoard, outputNode.getSelectedPosition());
		int xValue = outputNode.getSelectedPosition().getxPosition() + 1;
		int yValue = outputNode.getSelectedPosition().getyPosition();
		String position = String.valueOf((char) ((int) 'A' + yValue)) + Integer.toString(xValue);
		lines.add(position);
		for (int i = 0; i < outputBoard.length; i++) {
			StringBuilder string = new StringBuilder();
			for (int j = 0; j < outputBoard.length; j++) {
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
		return 3;
	}

	public enum NodeType {
		MIN, MAX;
	}

	class Node {

		private char[][] board; // the state of board at this particular node
		private Pair selectedPosition;
		private int totalScore;

		public Node(char[][] board, Pair selectedPosition, int totalScore) {
			super();
			this.board = board;
			this.selectedPosition = selectedPosition;
			this.totalScore = totalScore;
		}

		public Node() {
			// TODO Auto-generated constructor stub
		}

		public char[][] getBoard() {
			return board;
		}

		public void setBoard(char[][] board) {
			this.board = board;
		}

		public Pair getSelectedPosition() {
			return selectedPosition;
		}

		public void setSelectedPosition(Pair selectedPosition) {
			this.selectedPosition = selectedPosition;
		}

		public int getTotalScore() {
			return totalScore;
		}

		public void setTotalScore(int totalScore) {
			this.totalScore = totalScore;
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

		public Pair() {
			// TODO Auto-generated constructor stub
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