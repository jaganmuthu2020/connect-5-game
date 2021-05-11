package com.connect5.game.server.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.connect5.game.common.GameColor;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.util.GameUtil;

@SpringBootTest
class Connect5RepositoryTest {

	@Autowired
	private Connect5Repository connect5Repository;

	private Player buildPlayer() {
		Player player = new Player();
		player.setId("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		player.setName("Jagan");
		player.setColor(GameColor.RED);
		player.setTurn(true);
		player.setWon(true);
		player.setGameState(GameState.WIN);
		return player;
	}

	@BeforeEach
	void init() {
		Player player = buildPlayer();
		GameUtil.PLAYERS_DB.put(player.getId(), player);
	}

	@Test
	void testSaveOrUpdatePlayer() {
		Player player = connect5Repository.saveOrUpdatePlayer(buildPlayer());
		assertEquals("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", player.getId());
	}

	@Test
	void testGetPlayer() {
		Player player = buildPlayer();
		Player actualPlayer = connect5Repository.getPlayer("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		assertEquals(player.getId(), actualPlayer.getId());
	}

	@Test
	void testRemovePlayer() {
		connect5Repository.removePlayer("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
	}

	@Test
	void testGetTotalPlayers() {
		int actual = connect5Repository.getTotalPlayers();
		assertEquals(1, actual);
	}

	@Test
	void testGetPlayers() {
		Map<String, Player> excepted = new ConcurrentHashMap<String, Player>();
		excepted.put(buildPlayer().getId(), buildPlayer());
		Map<String, Player> actual = connect5Repository.getPlayers();
		assertEquals(excepted.size(), actual.size());
	}

	@Test
	void testIsPlayerExists() {
		boolean actual = connect5Repository.isPlayerExists();
		assertEquals(true, actual);
	}

	@Test
	void testIsPlayerEmpty() {
		GameUtil.PLAYERS_DB = new ConcurrentHashMap<String, Player>();
		boolean actual = connect5Repository.isPlayerExists();
		assertEquals(false, actual);
	}

	@Test
	void testRemoveIfStatusMatch() {
		boolean actual = connect5Repository.removeIfStatusMatch();
		assertEquals(true, actual);
	}

}
