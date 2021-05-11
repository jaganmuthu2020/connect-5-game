package com.connect5.game.common;

import java.util.Date;

public class Player {

	private String id;
	private String name;
	private GameColor color;
	private String columnPosition;
	private String board;

	private GameState gameState;

	private boolean turn;
	private boolean won = false;
	private boolean clientConnectioned = true;
	private boolean discDropped = true;

	private String errorMessage;
	private Date connectionTime;

	public Player() {
		super();
	}

	public Player(String id, String name, GameColor color, String columnPosition, GameState gameState,
			boolean turn, boolean won, boolean clientConnectioned, boolean discDropped, String errorMessage,
			Date connectionTime) {
		super();
		this.id = id;
		this.name = name;
		this.color = color;
		this.columnPosition = columnPosition;
		this.gameState = gameState;
		this.turn = turn;
		this.won = won;
		this.clientConnectioned = clientConnectioned;
		this.discDropped = discDropped;
		this.errorMessage = errorMessage;
		this.connectionTime = connectionTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GameColor getColor() {
		return color;
	}

	public void setColor(GameColor color) {
		this.color = color;
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isClientConnectioned() {
		return clientConnectioned;
	}

	public void setClientConnectioned(boolean clientConnectioned) {
		this.clientConnectioned = clientConnectioned;
	}

	public Date getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(Date connectionTime) {
		this.connectionTime = connectionTime;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isDiscDropped() {
		return discDropped;
	}

	public void setDiscDropped(boolean discDropped) {
		this.discDropped = discDropped;
	}

	public String getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(String columnPosition) {
		this.columnPosition = columnPosition;
	}
}
