package com.connect5.game.common;

public enum GameState {

	WAITING('W', "Waiting"), READY('R', "Ready"), WIN('N', "Win"), END('E', "End"),
	CLIENT_DISCONNECTED('D', "disconnected"), IN_PROGRESS('P', "InProgress");

	private final char stateChar;
	private final String stateName;

	GameState(char stateChar, String stateName) {
		this.stateChar = stateChar;
		this.stateName = stateName;
	}

	public char getStateChar() {
		return stateChar;
	}

	public String getStateName() {
		return stateName;
	}

}
