package com.connect5.game.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.connect5.game.common.GameColor;
import com.connect5.game.common.GameException;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class Connect5ClientTest {

	Connect5Client connect5Client;
	Player player = null;
	String playerJson = null;

	private MockWebServer mockWebServer;
	private HttpClient client;

	@BeforeEach
	void init() {
		connect5Client = new Connect5Client();
		player = buildPlayer();

		playerJson = "{\"id\":\"325ce7c0-fd61-40aa-b9c3-2d83b73bf74b\",\"name\":\"John\",\"color\":\"RED\","
				+ "\"columnPosition\":\"1\",\"gameState\":\"WAITING\",\"turn\":true,\"won\":false,\"clientConnectioned\":true,\"discDropped\":true,\"errorMessage\":\"Error\",\"connectionTime\":\"null\"}";
		this.mockWebServer = new MockWebServer();
		client = HttpClient.newHttpClient();
	}

	private Player buildPlayer() {
		player = new Player();
		player.setId("325ce7c0-fd61-40aa-b9c3-2d83b73bf74b");
		player.setName("Jagan");
		player.setColor(GameColor.RED);
		player.setTurn(true);
		return player;
	}

	@Test
	void testConnect5Client() {
		client = HttpClient.newHttpClient();
	}

	// @Test
	void testGetPlayerName() throws GameException {
		System.setIn(new ByteArrayInputStream("Jagan".getBytes()));
		connect5Client.player = buildPlayer();
		player = connect5Client.getPlayerName();
		assertEquals("Jagan", player.getName());
	}

	@Disabled
	@Test
	void testGetDiscPositionAndCheckStatus() throws GameException {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.READY);
		System.setIn(new ByteArrayInputStream("1".getBytes()));
		connect5Client.getDiscPositionAndCheckStatus();
	}

	@Disabled
	@Test
	void testGetDiscPositionAndCheckStatusWaiting() throws GameException {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.WAITING);
		System.setIn(new ByteArrayInputStream("1".getBytes()));
		connect5Client.getDiscPositionAndCheckStatus();
	}

	@Test
	void testGameStatusUpdate() {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.WIN);
		connect5Client.player.setWon(true);
		connect5Client.gameStatusUpdate();
	}

	@Test
	void testGameStatusUpdateLoss() {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.WIN);
		connect5Client.player.setWon(false);
		connect5Client.gameStatusUpdate();
	}

	@Test
	void testGameStatusUpdateDisconnected() {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.CLIENT_DISCONNECTED);
		connect5Client.gameStatusUpdate();
	}

	@Test
	void testGameStatusUpdateEnd() {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.END);
		connect5Client.gameStatusUpdate();
	}

	@Test
	void testGameStatusUpdateOtherStatus() {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.READY);
		connect5Client.gameStatusUpdate();
	}

	@Test
	public void testIsSameStatusPositiveTest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		connect5Client.previousStatus = GameState.WAITING.getStateName();
		connect5Client.previousTurn = true;
		Method method = Connect5Client.class.getDeclaredMethod("isSameStatus", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.WAITING);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(true, output);
	}

	/*
	 * @Test public void privateIsSameStatusNagativeTest() throws
	 * NoSuchMethodException, InvocationTargetException, IllegalAccessException {
	 * connect5Client.previousStatus = GameState.WAITING.getStateName();
	 * connect5Client.previousTurn = false; Method method =
	 * Connect5Client.class.getDeclaredMethod("isSameStatus", Player.class);
	 * method.setAccessible(true); boolean output = (boolean)
	 * method.invoke(connect5Client, player); assertEquals(false, output); }
	 */

	@Test
	public void testIsSameStatusNullTest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isSameStatus", Player.class);
		method.setAccessible(true);
		connect5Client.previousStatus = null;
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testIsSameStatusNegativeTest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		connect5Client.previousStatus = GameState.WAITING.getStateName();
		connect5Client.previousTurn = true;
		Method method = Connect5Client.class.getDeclaredMethod("isSameStatus", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.READY);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testGetObjectFromJson()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("getObjectFromJson", String.class);
		method.setAccessible(true);

		Player outputPlayer = (Player) method.invoke(connect5Client, playerJson);
		assertNotNull(outputPlayer);
		assertEquals(true, outputPlayer instanceof Player);
		assertEquals(outputPlayer.getId(), player.getId());
	}

	@Test
	public void testIsGameInprgressPositive()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isGameInprgress", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.READY);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(true, output);
	}

	@Test
	public void testIsGameInprgressWinNagative()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isGameInprgress", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.WIN);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testIsGameInprgressEndNagative()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isGameInprgress", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.END);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testIsGameInprgressClientDisconnectedNagative()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isGameInprgress", Player.class);
		method.setAccessible(true);
		player.setGameState(GameState.CLIENT_DISCONNECTED);
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testIsGameInprgressClientNull()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = Connect5Client.class.getDeclaredMethod("isGameInprgress", Player.class);
		method.setAccessible(true);
		player = null;
		boolean output = (boolean) method.invoke(connect5Client, player);
		assertEquals(false, output);
	}

	@Test
	public void testGetPosition()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("getPosition", Scanner.class);
		method.setAccessible(true);
		System.setIn(new ByteArrayInputStream("1".getBytes()));
		Scanner keyboard = new Scanner(System.in);
		Integer output = (Integer) method.invoke(connect5Client, keyboard);
		assertEquals(1, output.intValue());
	}

	@Disabled
	@Test
	public void testGetPositionNagative()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("getPosition", Scanner.class);
		method.setAccessible(true);
		System.setIn(new ByteArrayInputStream("0".getBytes()));
		Scanner keyboard = new Scanner(System.in);
		Integer output = (Integer) method.invoke(connect5Client, keyboard);
		assertEquals(0, output.intValue());
	}

	@Test
	public void testConnectServerForKeepAlivePlayer()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("connectServerForKeepAlive", HttpClient.class,
				String.class);
		method.setAccessible(true);
		mockWebServer.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
				.setBody("done").setResponseCode(200));
		method.invoke(connect5Client, client, mockWebServer.url("/").toString());
	}

	@Test
	public void testConnectServerForKeepAliveException()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("connectServerForKeepAlive", HttpClient.class,
				String.class);
		method.setAccessible(true);
		mockWebServer.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
				.setBody("Server Disconnected").setResponseCode(404));
		assertThrows(InvocationTargetException.class, () -> {
			method.invoke(connect5Client, client, mockWebServer.url("/").toString());
		});

	}

	@Test
	public void testConnectServer()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("connectServer", HttpClient.class, String.class);
		method.setAccessible(true);
		mockWebServer.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
				.setBody(playerJson).setResponseCode(200));
		method.invoke(connect5Client, client, mockWebServer.url("/").toString());
	}

	@Test
	public void testConnectServerPlayerNotNull()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("connectServer", HttpClient.class, String.class);
		method.setAccessible(true);
		mockWebServer.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
				.setBody(playerJson).setResponseCode(200));
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.READY);
		method.invoke(connect5Client, client, mockWebServer.url("/").toString());
	}

	@Test
	public void testClientKeepAliveRequest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		connect5Client.player = buildPlayer();
		connect5Client.player.setGameState(GameState.READY);
		connect5Client.clientKeepAliveRequest();
	}

	@Test
	public void testClientKeepAliveRequestEnd()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("clientKeepAliveRequest");
		method.setAccessible(true);
		player.setGameState(GameState.READY);
		method.invoke(connect5Client);
	}

	@Test
	public void testConnectServerExceptionTest()
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
		Method method = Connect5Client.class.getDeclaredMethod("connectServer", HttpClient.class, String.class);
		method.setAccessible(true);
		mockWebServer.enqueue(new MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
				.setBody(playerJson).setResponseCode(400));
		assertThrows(InvocationTargetException.class, () -> {
			method.invoke(connect5Client, client, mockWebServer.url("/").toString());
		});
	}
}
