package com.connect5.game.server.repository;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.util.GameUtil;

@Repository
public class Connect5Repository {

	public Player saveOrUpdatePlayer(Player player) {
		GameUtil.PLAYERS_DB.put(player.getId(), player);
		return player;
	}

	public Player getPlayer(String playerId) {
		return GameUtil.PLAYERS_DB.get(playerId);
	}

	public void removePlayer(String playerId) {
		GameUtil.PLAYERS_DB.remove(playerId);
	}

	public int getTotalPlayers() {
		return GameUtil.PLAYERS_DB.size();
	}

	public Map<String, Player> getPlayers() {
		return GameUtil.PLAYERS_DB;
	}

	public boolean isPlayerExists() {
		return !GameUtil.PLAYERS_DB.isEmpty();
	}

	public boolean removeIfStatusMatch() {
		
		return GameUtil.PLAYERS_DB.entrySet()
				.removeIf(entry -> 
				entry.getValue().getGameState().equals(GameState.CLIENT_DISCONNECTED)
						|| entry.getValue().getGameState().equals(GameState.WIN)
						|| entry.getValue().getGameState().equals(GameState.END));

	}
}
