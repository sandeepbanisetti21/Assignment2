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
//			gamePlayer.standardInput();
			gamePlayer.play();
			System.out.println("Rohith: " + gamePlayer.player2Score + ", " + gamePlayer.player2Time + ", " + "sandy: " + gamePlayer.player1Time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(Integer n, Integer f) {
		try {

			GamePlayer gamePlayer = new GamePlayer();
			gamePlayer.generateInput(n, f);
//			gamePlayer.standardInput();
			gamePlayer.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void play() throws IOException {
		boolean maxTurn = false;
		homework homework = new homework();
		FruitSolver fruitSolver = new FruitSolver();

		do {
			if (maxTurn) {
//				System.out.println("Max Turn");
				player1(homework);
				maxTurn = false;
			} else {
//				System.out.println("Min Turn");
				player2(fruitSolver);
				maxTurn = true;
			}
		} while (!updateInput(maxTurn) && player1Time >= 0.1 && player2Time >= 0.1);
	}

	private void player2(FruitSolver fruitSolver) {
		long startTime = System.nanoTime();
		PointScore outputNode = fruitSolver.run();
		long endTime = System.nanoTime();
		long timeTaken = ((endTime - startTime) / 1000000000);
		player2Score = player2Score + outputNode.score;
		System.out.println("Rohith score " + player2Score);
		player2Time = player2Time - timeTaken;
		System.out.println("Rohith time remaining: " + player2Time);
	}

	private void player1(homework homework) {
		long startTime = System.nanoTime();
		Node outputNode = homework.run();
		long endTime = System.nanoTime();
		long timeTaken = ((endTime - startTime) / 1000000000);
		player1Score = (int) (player1Score + Math.pow((outputNode.getSelectedPosition().getEnergy()),2));
		System.out.println("Sandy score " + player1Score);
		player1Time = player1Time - timeTaken;
	}

	private boolean updateInput(boolean maxTurn) throws IOException {
//		System.out.println(player1Time);
//		System.out.println(player2Time);
		List<String> lines = new ArrayList<>();
		lines.add(Integer.toString(sizeOfBoard));
		lines.add(Integer.toString(numOfFruits));
		if (maxTurn)
			lines.add(Float.toString(player1Time));
		else
			lines.add(Float.toString(player2Time));
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

	private void generateInput(Integer size, Integer fruits) throws IOException{
		sizeOfBoard = size; //rand.nextInt(26)
		numOfFruits = fruits;
		timeTaken = 300;

		TestGenerator testGenerator = new TestGenerator();
		testGenerator.createBoard(numOfFruits, sizeOfBoard, timeTaken);

		System.out.println("Size of baord " + sizeOfBoard);
		System.out.println("Number of fruits " + numOfFruits);
	}

	private void generateInput() throws IOException {
		Random rand = new Random();
		sizeOfBoard = 10; //rand.nextInt(26)
		numOfFruits = 6;
		timeTaken = 300;

		TestGenerator testGenerator = new TestGenerator();
		testGenerator.createBoard(numOfFruits, sizeOfBoard, timeTaken);

		System.out.println("Size of baord " + sizeOfBoard);
		System.out.println("Number of fruits " + numOfFruits);

	}

	private void standardInput() {
		sizeOfBoard = 26;
		numOfFruits = 2;
		timeTaken = 300;
		System.out.println("Size of baord " + sizeOfBoard);
		System.out.println("Number of fruits " + numOfFruits);
	}
}

/*
    public PointScore alpha_beta(Input input, Integer depth){
        Integer [][] newBoard = deepcopy(input.board, input.size);
        PointScore p = maxNode(input.board, input.size, input.fruits, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        p.score = pointVal(newBoard, input.size, p.point);
        return p;
    }


    public PointScore run(){
        Input input = readInput();
        Integer depth = getDepth(input);
        System.out.println("estimated depth: " + depth);
        PointScore ans = alpha_beta(input, depth);
        try{
            writeOutput(ans.point, input.board, input.size);
        } catch (IOException e){
            e.printStackTrace();
        }
        return ans;
    }

 */