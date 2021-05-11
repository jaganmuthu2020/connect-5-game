package com.connect5.game.common;

public enum GameColor {
	
	RED('R', "Red"), GREEN('G', "Green");
	
	private final char symbol;
    private final String name;
    
	GameColor(char symbol, String name) {
		this.symbol = symbol;
		this.name = name;
	}

	public char getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}
}
