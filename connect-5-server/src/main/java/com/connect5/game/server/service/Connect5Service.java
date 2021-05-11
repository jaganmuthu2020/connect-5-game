package com.connect5.game.server.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.connect5.game.common.GameColor;
import com.connect5.game.common.GameException;
import com.connect5.game.common.GameExceptionCode;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.repository.Connect5Repository;
import com.connect5.game.server.scheduler.Connect5GameScheduler;
import com.connect5.game.server.util.Connect5Game;
import com.connect5.game.server.util.GameUtil;

/**
 * The Connect5Service is responsible possible the details to repository class and implemented the business logic to check the game status. 
 * @author MuthusamyJ
 *
 */
@Service
public class Connect5Service {

	private static final Logger log = LoggerFactory.getLogger(Connect5GameScheduler.class);

	boolean toggleTurn = false;
	boolean wonGame = false;
	boolean clientDisconnected = false;
	boolean reachedNoOfAttempts = false;
	int noOfTurn = 0;
	Connect5Game connect5Game = null;

	@Value("${client.timeout}")
	private int clientTimeout;

	@Autowired
	private Connect5Repository connect5Repository;
	// public Player player;

	public Connect5Service() {
		connect5Game = new Connect5Game(6, 9, 5);
	}

	public Player savePlayer(String name) throws GameException {
		Player player = getPlayerObject(name);
		return connect5Repository.saveOrUpdatePlayer(player);
	}

	private Player getPlayerObject(String name) throws GameException {
		Player player = new Player();
		String id = GameUtil.getUUID();
		player.setId(id);
		player.setName(name);
		player.setConnectionTime(new Date());
		if (connect5Repository.getTotalPlayers() == 0) {
			clearBoard();
			player.setColor(GameColor.RED);
			player.setTurn(true);
			player.setGameState(GameState.WAITING);
		} else if (connect5Repository.getTotalPlayers() == 1) {
			player.setColor(GameColor.GREEN);
			player.setTurn(false);
			player.setGameState(GameState.READY);
		} else {
			throw new GameException(GameExceptionCode.GAME_INPROGRESS, "Please wait previous game still in progress.");
		}
		player.setBoard(connect5Game.toString());
		return player;
	}

	public Player getPlayer(String playerId) {
		return connect5Repository.getPlayer(playerId);
	}

	public boolean isPlayerExists() {
		return connect5Repository.isPlayerExists();
	}

	public int getTotalPlayers() {
		return connect5Repository.getTotalPlayers();
	}

	private boolean checkStatus(Player player) {
		return player.getGameState().equals(GameState.READY) && !wonGame && !reachedNoOfAttempts && !clientDisconnected;
	}

	private void clearBoard() {
		toggleTurn = false;
		wonGame = false;
		clientDisconnected = false;
		reachedNoOfAttempts = false;
		noOfTurn = 0;
		connect5Game = new Connect5Game(6, 9, 5);
	}

	public Player populateStatus(String playerId, String column) {
		Player player = connect5Repository.getPlayer(playerId);
		if (connect5Repository.getTotalPlayers() == 2) {
			if (checkStatus(player)) {
				if (player.isTurn()) {
					player.setColumnPosition(column);
					boolean isDiscDropped = connect5Game.dropDiscToSlot(String.valueOf(player.getColor().getSymbol()),
							Integer.parseInt(column) - 1);
					player.setDiscDropped(isDiscDropped);
					if (player.isDiscDropped()) {
						noOfTurn++;
						if (noOfTurn == connect5Game.getTotalMoves()) {
							reachedNoOfAttempts = true;
						}
						if (connect5Game.isPlayerWon(String.valueOf(player.getColor().getSymbol()))) {
							player.setWon(true);
							wonGame = true;
						} else {
							toggleTurn = true;
						}
						player.setTurn(false);
					}
				} else {
					if (toggleTurn) {
						toggleTurn = false;
						player.setTurn(true);
					}
				}
			}
		}

		if (wonGame) {
			player.setGameState(GameState.WIN);
		} else if (reachedNoOfAttempts) {
			player.setGameState(GameState.END);
		} else if (clientDisconnected) {
			player.setGameState(GameState.CLIENT_DISCONNECTED);
		} else if (connect5Repository.getTotalPlayers() < 2) {
			player.setGameState(GameState.WAITING);
		} else if (connect5Repository.getTotalPlayers() == 2) {
			player.setGameState(GameState.READY);
		}
		player.setBoard(connect5Game.toString());
		connect5Repository.saveOrUpdatePlayer(player);
		return player;
	}

	public String updateKeepAliveStatus(String id) throws RuntimeException {
		String keepAliveResponse = "success";
		Map<String, Player> players = connect5Repository.getPlayers();

		BiConsumer<String, Player> playerAction = new BiConsumer<String, Player>() {
			@Override
			public void accept(String playerId, Player player) {
				if (id.equals(playerId)) {
					player.setConnectionTime(new Date());
					connect5Repository.saveOrUpdatePlayer(player);
				} else {
					long diffInSeconds = TimeUnit.MILLISECONDS
							.toSeconds(new Date().getTime() - player.getConnectionTime().getTime());
					if (diffInSeconds > clientTimeout) {
						player.setGameState(GameState.CLIENT_DISCONNECTED);
						connect5Repository.saveOrUpdatePlayer(player);
						clientDisconnected = true;
						log.debug("Player " + player.getName() + " diconnected, so removed from repository.");
					}
				}
			}
		};
		players.forEach(playerAction);
		if(clientDisconnected) {
			keepAliveResponse = "diconnected";
		}
		return keepAliveResponse;
	}

	public void checkGameStatusRemovePlayer() {
		connect5Repository.removeIfStatusMatch();
		BiConsumer<String, Player> playerAction = new BiConsumer<String, Player>() {
			@Override
			public void accept(String playerId, Player player) {
				long diffInSeconds = TimeUnit.MILLISECONDS
						.toSeconds(new Date().getTime() - player.getConnectionTime().getTime());
				if (diffInSeconds > clientTimeout) {
					clientDisconnected = true;
					connect5Repository.removePlayer(playerId);
					log.debug("Player " + player.getName() + " diconnected, so removed from repository.");
				}
			}
		};
		Map<String, Player> players = connect5Repository.getPlayers();
		players.forEach(playerAction);
	}
}
