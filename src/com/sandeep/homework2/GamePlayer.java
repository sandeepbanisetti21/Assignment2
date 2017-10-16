package com.sandeep.homework2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sandeep.homework2.homework.Node;

public class GamePlayer {

	private static final int minValue = -99999;
	int sizeOfBoard;
	int numOfFruits;
	float timeTaken;

	int player1Score = 0;
	int player2Score = 0;

	float player1Time = 300;
	float player2Time = 300;

	public static void main(String[] args) {
		try {

			GamePlayer gamePlayer = new GamePlayer();
			gamePlayer.generateInput();
			gamePlayer.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void play() throws IOException {
		boolean maxTurn = true;
		homework homework = new homework();

		do {
			if (maxTurn) {
				System.out.println("Max Turn");
				player1(homework);
				maxTurn = false;
			} else {
				System.out.println("Min Turn");
				player2(homework);
				maxTurn = true;
			}
		} while (!updateInput(maxTurn) && player1Time >= 0.1 && player2Time >= 0.1);
	}

	private void player2(homework homework) {
		long startTime = System.nanoTime();
		Node outputNode = homework.run();
		long endTime = System.nanoTime();
		long timeTaken = ((endTime - startTime) / 1000000000);
		// System.out.println("Took " + timeTaken + " s");
		// System.out.println("Move is " +
		// outputNode.getSelectedPosition().getxPosition()
		// + outputNode.getSelectedPosition().getyPosition());
		//System.out.println("Score for this move " + outputNode.getSelectedPosition().getEnergy());
		player2Score = player2Score + outputNode.getSelectedPosition().getEnergy();
		System.out.println("Player 2 score " + player2Score);
		player2Time = player2Time - timeTaken;
	}

	private void player1(homework homework) {
		long startTime = System.nanoTime();
		Node outputNode = homework.run();
		long endTime = System.nanoTime();
		long timeTaken = ((endTime - startTime) / 1000000000);
		// System.out.println("Took " + timeTaken + " s");
		// System.out.println("Move is " +
		// outputNode.getSelectedPosition().getxPosition()
		// + outputNode.getSelectedPosition().getyPosition());
		//System.out.println("Score for this move " + outputNode.getSelectedPosition().getEnergy());
		player1Score = player1Score + outputNode.getSelectedPosition().getEnergy();
		System.out.println("Player 1 score " + player1Score);
		player1Time = player1Time - timeTaken;
	}

	private boolean updateInput(boolean maxTurn) throws IOException {

		List<String> lines = new ArrayList<>();
		lines.add(Integer.toString(sizeOfBoard));
		lines.add(Integer.toString(numOfFruits));
		if (maxTurn)
			lines.add(Float.toString(player2Time));
		else
			lines.add(Float.toString(player1Time));
		char[][] board = readFile();
		if (isEndBoard(board)) {
			return true;
		}
		for (int i = 0; i < sizeOfBoard; i++) {
			StringBuilder string = new StringBuilder();
			for (int j = 0; j < sizeOfBoard; j++) {
				string.append(board[i][j]);
			}
			lines.add(string.toString());
		}
		Path file = Paths.get("input.txt");
		Files.write(file, lines, Charset.forName("UTF-8"));

		return false;

	}

	private char[][] readFile() {
		Stream<String> stream = null;
		char[][] inputBoard = null;
		try {
			String currentDirectory = System.getProperty("user.dir");
			List<String> inputLines = new ArrayList<>();
			stream = Files.lines((Paths.get(currentDirectory + "/output.txt")));
			inputLines = stream.collect(Collectors.toList());
			inputLines.remove(0);
			inputBoard = new char[sizeOfBoard][sizeOfBoard];
			String inputString;
			for (int i = 0; i < sizeOfBoard; i++) {
				inputString = inputLines.get(i);
				for (int j = 0; j < sizeOfBoard; j++) {
					inputBoard[i][j] = inputString.charAt(j);
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		} finally {
			stream.close();
		}
		return inputBoard;
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

	private int getIntegerValueOfCell(char charcter) {

		if (charcter != '*') {
			return Integer.parseInt(String.valueOf(charcter));
		}
		return minValue;
	}

	private void generateInput() throws IOException {
		Random rand = new Random();
		sizeOfBoard = rand.nextInt(26);
		numOfFruits = rand.nextInt(10);
		timeTaken = 300;

		TestGenerator testGenerator = new TestGenerator();
		testGenerator.createBoard(numOfFruits, sizeOfBoard, timeTaken);

		System.out.println("Size of baord " + sizeOfBoard);
		System.out.println("Number of fruits " + numOfFruits);

	}
}
