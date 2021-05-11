package com.connect5.game.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.connect5.game.common.GameException;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Connect5Client {

	private static final String NAME_SERVER_URL = "http://localhost:8080/connect5server/addplayer";
	private static final String ID_SERVER_URL = "http://localhost:8080/connect5server/checkStatus";
	private static final String SERVER_KEEP_ALIVE_REQUEST = "http://localhost:8080/connect5server/keepAlive";

	HttpClient client = null;
	ObjectMapper objectMapper = new ObjectMapper();
	Player player = null;
	String previousStatus = null;
	boolean previousTurn;

	public Connect5Client() {
		super();
		client = HttpClient.newHttpClient();
	}

	/**
	 * This method used to get the player name input and send the details to server as get service
	 * @return
	 * @throws GameException
	 */
	@SuppressWarnings("resource")
	public Player getPlayerName() throws GameException {
		try {
			System.out.println("Welcome to Connect 5 Game!!!");
			System.out.print("Please Enter your name:");
			Scanner inputScan = new Scanner(System.in);
			String name = inputScan.nextLine();
			StringBuffer requestURL = new StringBuffer(NAME_SERVER_URL).append("?name=").append(name);
			player = connectServer(client, requestURL.toString());
			System.out.println("Hello " + player.getName() + ", Your color is " + player.getColor());
			return player;
		} catch (IOException | InterruptedException e) {
			throw new GameException(e.getMessage());
		} 
	}

	/**
	 * This method used to get check the game status and also get the input from player
	 * 
	 * @throws GameException
	 */
	public void getDiscPositionAndCheckStatus() throws GameException {
		StringBuffer requestURL = new StringBuffer();
		Scanner scan = null;
		try {
			while (isGameInprgress(player)) {
				Thread.sleep(2000);
				if (!isSameStatus(player)) {
					System.out.println();
					System.out.println(player.getBoard());
				}
				if (player.getGameState().equals(GameState.WAITING)) {
					if (!isSameStatus(player)) {
						System.out.println("Please wait other player to Join");
					}
					requestURL = new StringBuffer(ID_SERVER_URL).append("?id=").append(player.getId());
					player = connectServer(client, requestURL.toString());
				} else if (player.getGameState().equals(GameState.READY)) {
					if (player.isTurn()) {
						System.out.print("It's your turn " + player.getName() + ", please enter column (1 - 9) :");
						requestURL = new StringBuffer(ID_SERVER_URL).append("?id=").append(player.getId())
								.append("&column=" + getPosition(scan));
						player = connectServer(client, requestURL.toString());
					} else {
						if (!isSameStatus(player)) {
							System.out.println("Waiting for other player to complete the turn.");
						}
						requestURL = new StringBuffer(ID_SERVER_URL).append("?id=").append(player.getId());
						player = connectServer(client, requestURL.toString());
					}

					if (!player.isDiscDropped()) {
						System.out.print("Column " + player.getColumnPosition()
								+ " is full!!! Please choose different column (0 - 9) :");
						requestURL = new StringBuffer(ID_SERVER_URL).append("?id=").append(player.getId())
								.append("&column=" + getPosition(scan));
						player = connectServer(client, requestURL.toString());
					}
				}
				if (player == null) {
					System.out.println("Game End!!! Some issue at Server end");
					return;
				}
			}
		} catch (InterruptedException | IOException | GameException e) {
			throw new GameException(e.getMessage());
		} 
	}

	public void gameStatusUpdate() {
		System.out.println();
		if (player.getGameState().equals(GameState.WIN)) {
			System.out.println(player.getBoard());
			if (player.isWon()) {
				System.out.println("Congratulations " + player.getName() + "!!! You Won the Match");
			} else {
				System.out.println("Sorry  " + player.getName() + "!!! You Lost the Match");
			}
		} else if (player.getGameState().equals(GameState.CLIENT_DISCONNECTED)) {
			System.out.print("Game Ended!!! Other player disconnected.");
		} else if (player.getGameState().equals(GameState.END)) {
			System.out.println("Sorry!!! Game over. No winner. Plesae try again.");
		}
	}

	public void clientKeepAliveRequest() {
		try {
			HttpClient client = HttpClient.newHttpClient();
			while (isGameInprgress(player)) {
				StringBuffer requestURL = new StringBuffer(SERVER_KEEP_ALIVE_REQUEST).append("?id=")
						.append(player.getId());
				connectServerForKeepAlive(client, requestURL.toString());
				Thread.sleep(3000);
			}
		} catch (IOException | InterruptedException | GameException e) {
			System.out.println("Keep Alive Request Failed, " + e.getMessage());
			player.setGameState(GameState.END);
		}
	}

	private boolean isGameInprgress(Player player) {
		return player != null && !player.getGameState().equals(GameState.END)
				&& !player.getGameState().equals(GameState.WIN)
				&& !player.getGameState().equals(GameState.CLIENT_DISCONNECTED);
	}

	private boolean isSameStatus(Player player) {
		if (previousStatus != null && previousStatus.equals(player.getGameState().getStateName())
				&& previousTurn == player.isTurn()) {
			return true;
		}
		return false;
	}

	private int getPosition(Scanner scan) {
		int position = 1;
		try {
			scan = new Scanner(System.in);
			position = scan.nextInt();
			if (position < 1 || position > 9) {
				System.out.println("Please enter the value between 1 to 9");
				getPosition(scan);
			}
		} catch (InputMismatchException e) {
			System.out.println("Please enter numeric value");
			getPosition(scan);
		}
		return position;
	}

	private Player connectServer(HttpClient client, String requestURL)
			throws IOException, InterruptedException, GameException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			if (player != null) {
				previousStatus = player.getGameState().getStateName();
				previousTurn = player.isTurn();
			}
			return getObjectFromJson(response.body());
		} else {
			throw new GameException(getObjectFromJson(response.body()).getErrorMessage());
		}
	}

	private Player getObjectFromJson(String playerJson) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(playerJson, Player.class);
	}

	/**
	 * This method used to send keep alive request to server to inform that client is still active.
	 * 
	 * @param client
	 * @param requestURL
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws GameException
	 */
	private void connectServerForKeepAlive(HttpClient client, String requestURL)
			throws IOException, InterruptedException, GameException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new GameException("Server Disconnected");
		}
	}
}
