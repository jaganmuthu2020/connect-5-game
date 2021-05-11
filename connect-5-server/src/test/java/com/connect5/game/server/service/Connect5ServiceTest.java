package com.connect5.game.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.connect5.game.common.GameColor;
import com.connect5.game.common.GameException;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.repository.Connect5Repository;

@SpringBootTest
class Connect5ServiceTest {

	@MockBean
	private Connect5Repository connect5Repository;

	@Autowired
	private Connect5Service connect5Service;

	private Player buildPlayer() {
		Player player = new Player();
		player.setId("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		player.setName("Jagan");
		player.setColor(GameColor.RED);
		player.setTurn(true);
		return player;
	}
	
	@Test
	void testSavePlayerWithZeroSize() throws GameException {
		Player player = buildPlayer();
		when(connect5Repository.getTotalPlayers()).thenReturn(0);
		when(connect5Repository.saveOrUpdatePlayer(any())).thenReturn(player);
		Player actualPlayer = connect5Service.savePlayer("");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testSavePlayerWithOneSize() throws GameException {
		Player player = buildPlayer();
		when(connect5Repository.getTotalPlayers()).thenReturn(1);
		when(connect5Repository.saveOrUpdatePlayer(any())).thenReturn(player);
		Player actualPlayer = connect5Service.savePlayer("");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testGetPlayer() {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		Player actualPlayer = connect5Service.getPlayer("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testIsPlayerExists() {
		when(connect5Repository.isPlayerExists()).thenReturn(true);
		boolean actualResult = connect5Service.isPlayerExists();
		assertEquals(true, actualResult);
	}

	@Test
	void testGetTotalPlayers() {
		when(connect5Repository.getTotalPlayers()).thenReturn(2);
		int actualResult = connect5Service.getTotalPlayers();
		assertEquals(2, actualResult);
	}

	@Test
	void testPopulateStatus() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(2);
		player.setGameState(GameState.READY);
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testPopulateStatusOnePlayer() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(1);
		player.setGameState(GameState.READY);
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	public void privateCheckStatusPositiveTest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		player.setGameState(GameState.READY);
		connect5Service.wonGame = false;
		connect5Service.reachedNoOfAttempts = false;
		connect5Service.clientDisconnected = false;
		Method method = Connect5Service.class.getDeclaredMethod("checkStatus", Player.class);
		method.setAccessible(true);

		boolean output = (boolean) method.invoke(connect5Service, player);
		assertEquals(true, output);
	}

	@Test
	void testPopulateStatusTurnOffPlayer()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(2);
		player.setGameState(GameState.READY);
		player.setTurn(false);
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testPopulateStatustoggleTurnOffPlayer()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(2);
		player.setGameState(GameState.READY);
		player.setTurn(false);
		connect5Service.toggleTurn = true;
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	void testPopulateStatustoggleDiscDroppedOnPlayer()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(2);

		player.setGameState(GameState.READY);
		player.setTurn(true);
		player.setDiscDropped(true);
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	public void privateCheckStatusWinCheck()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		player.setGameState(GameState.READY);
		connect5Service.wonGame = true;
		Method method = Connect5Service.class.getDeclaredMethod("checkStatus", Player.class);
		method.setAccessible(true);

		boolean output = (boolean) method.invoke(connect5Service, player);
		assertEquals(false, output);
	}

	@Test
	void testPopulateStatusPlayerNoOfTurn()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		connect5Service.noOfTurn = 54;
		when(connect5Repository.getPlayer(any())).thenReturn(player);
		when(connect5Repository.getTotalPlayers()).thenReturn(2);

		player.setGameState(GameState.READY);
		player.setTurn(true);
		player.setDiscDropped(true);
		Player actualPlayer = connect5Service.populateStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b", "1");
		assertEquals(player, actualPlayer);
	}

	@Test
	public void privateCheckStatusEndCheck()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player player = buildPlayer();
		player.setGameState(GameState.READY);
		connect5Service.reachedNoOfAttempts = true;
		Method method = Connect5Service.class.getDeclaredMethod("checkStatus", Player.class);
		method.setAccessible(true);

		boolean output = (boolean) method.invoke(connect5Service, player);
		assertEquals(false, output);
	}

	@Test
	void testUpdateKeepAliveStatus() {
		Player player = buildPlayer();
		connect5Service.clientDisconnected=false;
		player.setConnectionTime(new Date(System.currentTimeMillis() + 3600 * 1000));
		Map<String, Player> players = new ConcurrentHashMap<String, Player>();
		players.put(player.getId(), player);
		when(connect5Repository.getPlayers()).thenReturn(players);
		String actual = connect5Service.updateKeepAliveStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		assertEquals("success", actual);
	}

	@Test
	void testUpdateKeepAliveStatusDisconnectCheck() {
		Player player = buildPlayer();
		player.setConnectionTime(new Date(System.currentTimeMillis() - 3600 * 1000));
		Map<String, Player> players = new ConcurrentHashMap<String, Player>();
		players.put(player.getId(), player);
		when(connect5Repository.getPlayers()).thenReturn(players);
		String actual = connect5Service.updateKeepAliveStatus("325ce7c0-fd61-40aa-b9c3-2d83b73bfldb");
		assertEquals("diconnected", actual);
	}

	@Test
	void testCheckGameStatusRemovePlayer() {
		Player player = buildPlayer();
		Map<String, Player> players = new ConcurrentHashMap<String, Player>();
		player.setConnectionTime(new Date(System.currentTimeMillis() - 3600 * 1000));
		players.put(player.getId(), player);
		when(connect5Repository.getPlayers()).thenReturn(players);
		connect5Service.checkGameStatusRemovePlayer();
	}

}
