package com.connect5.game.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.connect5.game.common.GameColor;
import com.connect5.game.common.GameException;
import com.connect5.game.common.GameExceptionCode;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.service.Connect5Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@SpringBootTest
@AutoConfigureMockMvc
public class Connect5GameControllerTest {

	private final static String NAME_SERVICE_URL = "/addplayer?name=John";
	private final static String STATUS_SERVICE_URL = "/checkStatus?id=325ce7c0-fd61-40aa-b9c3-2d83b73bf74b&column=1";
	private final static String KEEPALIVE_SERVICE_URL = "/keepAlive?id=325ce7c0-fd61-40aa-b9c3-2d83b73bf74b";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
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
	public void testAddPlayer() throws Exception {
		Player player = buildPlayer();
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		when(connect5Service.savePlayer(any())).thenReturn(player);
		mockMvc.perform(get(NAME_SERVICE_URL)).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(player)));

	}

	@Test
	public void testAddPlayerWithException() throws Exception {
		Player player = new Player();
		player.setErrorMessage("General Exception");

		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		when(connect5Service.savePlayer(any())).thenThrow(new GameException()).thenReturn(player);
		mockMvc.perform(get(NAME_SERVICE_URL)).andExpect(status().isBadRequest())
				.andExpect(content().json(objectWriter.writeValueAsString(player)));

	}

	@Test
	public void testAddPlayerWith3rdPlayerException() throws Exception {
		Player player = new Player();
		player.setGameState(GameState.IN_PROGRESS);
		player.setErrorMessage("Please wait previous game still in progress.");

		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		when(connect5Service.savePlayer(any())).thenThrow(
				new GameException(GameExceptionCode.GAME_INPROGRESS, "Please wait previous game still in progress."))
				.thenReturn(player);
		mockMvc.perform(get(NAME_SERVICE_URL)).andExpect(status().isBadRequest())
				.andExpect(content().json(objectWriter.writeValueAsString(player)));

	}

	@Test
	public void testDropDicsCheckStatus() throws Exception {
		Player player = buildPlayer();
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		when(connect5Service.populateStatus(any(), any())).thenReturn(player);
		mockMvc.perform(get(STATUS_SERVICE_URL)).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(player)));
	}

	@Test
	public void testClientKeepAliveRequest() throws Exception {
		String keepAlive = "success";
		when(connect5Service.updateKeepAliveStatus(any())).thenReturn(keepAlive);
		mockMvc.perform(get(KEEPALIVE_SERVICE_URL)).andExpect(status().isOk())
				.andExpect(content().string(keepAlive));

		when(connect5Service.updateKeepAliveStatus(any())).thenThrow(new RuntimeException()).thenReturn("");
		mockMvc.perform(get(KEEPALIVE_SERVICE_URL)).andExpect(status().isBadRequest())
				.andExpect(content().string(""));
	}

}
