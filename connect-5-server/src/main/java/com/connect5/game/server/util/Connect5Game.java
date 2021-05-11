package com.connect5.game.server.util;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * The Connect5Game is responsible for adding the disc into board and also
 * checking wheather player won the match.
 *
 */
public class Connect5Game {

	private final int row, column, moves;
	private final String[][] grid;
	private final String[] symbolArray;
	private final String charString = "%s";
	private final String charPattern;

	private int lastRow = -1, lastColumn = -1;

	public Connect5Game(int row, int column, int noOfMatching) {
		this.row = row;
		this.column = column;
		this.moves = row * column;
		symbolArray = new String[noOfMatching];
		charPattern = charString.repeat(noOfMatching);
		this.grid = new String[row][column];
		for (int i = 0; i < row; i++) {
			Arrays.fill(grid[i] = new String[column], " ");
		}
	}

	/**
	 * Dropping disc to available column and keel last row and column in local
	 * variable
	 * 
	 * @param symbol
	 * @param col
	 * @return
	 */
	public boolean dropDiscToSlot(String symbol, int col) {
		for (int r = row - 1; r >= 0; r--) {
			if (grid[r][col] == " ") {
				grid[lastRow = r][lastColumn = col] = symbol;
				return true;
			}
		}
		return false;
	}

	private boolean checkContainsSymbols(String boardVaues, String symbol5String) {
		return boardVaues.indexOf(symbol5String) >= 0;
	}

	private String getMatchingPattern(String symbol) {
		Arrays.fill(symbolArray, symbol);
		return String.format(charPattern, (Object[]) symbolArray);
	}

	public boolean isPlayerWon(String symbol) {
		String symbol5String = getMatchingPattern(symbol);
		return checkContainsSymbols(getHorizontalValues(), symbol5String)
				|| checkContainsSymbols(getVerticalValues(), symbol5String)
				|| checkContainsSymbols(getMainDiagonal(), symbol5String)
				|| checkContainsSymbols(getAntiDiagonal(), symbol5String);
	}

	/**
	 * Getting horizontal row as string based on the last drop position
	 * 
	 * @return
	 */
	private String getHorizontalValues() {
		return String.join("", grid[lastRow]);
	}

	/**
	 * Getting vertical row as string based on the last drop position
	 * 
	 * @return
	 */
	private String getVerticalValues() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < row; r++) {
			sb.append(grid[r][lastColumn]);
		}
		return sb.toString();
	}

	/**
	 * Getting Main Diagonal(\) string string based on the last drop position
	 * 
	 * @return
	 */
	private String getMainDiagonal() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < row; r++) {
			int c = lastColumn - lastRow + r;
			if (0 <= c && c < column) {
				sb.append(grid[r][c]);
			}
		}
		return sb.toString();
	}

	/**
	 * Getting Anti Diagonal(/) string string based on the last drop position
	 * 
	 * @return
	 */
	private String getAntiDiagonal() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < row; r++) {
			int c = (lastRow + lastColumn) - r;
			if (0 <= c && c < column) {
				sb.append(grid[r][c]);
			}
		}
		return sb.toString();
	}

	/**
	 * Converting string 2 dimensional array into game board format.
	 */
	@Override
	public String toString() {
		return Stream.of(grid).map(this::stingArrayToString).collect(Collectors.joining("\n"));
	}

	private String stingArrayToString(String[] row) {
		return Arrays.asList(row).stream().collect(Collectors.joining("] [", "[", "]"));
	}

	public int getTotalMoves() {
		return moves;
	}

}
