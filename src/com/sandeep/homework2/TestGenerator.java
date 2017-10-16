package com.sandeep.homework2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGenerator {

	public static void main(String[] args) {

		TestGenerator testGenerator = new TestGenerator();
		int numberOfFruits = 3;
		int sizeOfBoard = 5;
		float timeTaken = 300;
		try {
			testGenerator.createBoard(numberOfFruits, sizeOfBoard, timeTaken);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createBoard(int numberOfFruits, int sizeOfBoard, float timeTaken) throws IOException {
		List<String> lines = new ArrayList<>();
		lines.add(Integer.toString(sizeOfBoard));
		lines.add(Integer.toString(numberOfFruits));
		lines.add(Float.toString(timeTaken));

		int[][] board = new int[sizeOfBoard][sizeOfBoard];

		Random rand = new Random();

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				board[i][j] = rand.nextInt(numberOfFruits);
			}
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
	}
}
