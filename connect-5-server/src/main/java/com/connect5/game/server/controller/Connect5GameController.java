package com.connect5.game.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connect5.game.common.GameException;
import com.connect5.game.common.GameExceptionCode;
import com.connect5.game.common.GameState;
import com.connect5.game.common.Player;
import com.connect5.game.server.service.Connect5Service;

/**
 * The Connect5GameController will be responsible for receiving request from client. 
 *
 */
@RestController
public class Connect5GameController {

	private static final Logger log = LoggerFactory.getLogger(Connect5GameController.class);

	@Autowired
	private Connect5Service connect5Service;

	/**
	 * This service used to receive the player name and will save it into in memory storage.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/addplayer")
	public ResponseEntity<Player> addPlayer(HttpServletRequest request, Model model) {
		String name = request.getParameter("name");
		Player player = null;
		try {
			player = connect5Service.savePlayer(name);
			return ResponseEntity.ok(player);
		} catch (GameException e) {
			player = new Player();
			if (e.getExceptionCode().equals(GameExceptionCode.GAME_INPROGRESS)) {
				player.setGameState(GameState.IN_PROGRESS);
				player.setErrorMessage("Please wait previous game still in progress.");
			} else {
				player.setErrorMessage("General Exception");
			}
			return ResponseEntity.badRequest().body(player);
		}
	}

	/**
	 * 
	 * This service used to receive the position/column of the disc and will check the status of other player and game.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/checkStatus")
	public ResponseEntity<Player> dropDicsCheckStatus(HttpServletRequest request, Model model) {
		String playerId = request.getParameter("id");
		String column = request.getParameter("column");
		Player player = connect5Service.populateStatus(playerId, column);
		return ResponseEntity.ok(player);
	}

	/**
	 * This service used to keep the client session active.
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/keepAlive")
	public ResponseEntity<String> clientKeepAliveRequest(HttpServletRequest request, Model model) {
		try {
			String id = request.getParameter("id");
			log.debug("keepalive request reached for id:" + id);
			String status = connect5Service.updateKeepAliveStatus(id);
			return ResponseEntity.ok(status);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
