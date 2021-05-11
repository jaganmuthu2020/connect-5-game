package com.connect5.game.client;

import com.connect5.game.common.GameException;

/**
 * The Connect5GamePlayer main method used to start the player game. Before
 * running this class we need to start the server
 *
 */
public class Connect5GamePlayer {

	public static void main(String[] args) {
		Connect5Client connect5Client = new Connect5Client();
		try {
			connect5Client.getPlayerName();
			new Thread(connect5Client::clientKeepAliveRequest).start();
			connect5Client.getDiscPositionAndCheckStatus();
			connect5Client.gameStatusUpdate();
		} catch (GameException e) {
			System.out.print("Sorry! Game Ended.");
			System.out.println(e.getMessage());
			return;
		}
	}

}
