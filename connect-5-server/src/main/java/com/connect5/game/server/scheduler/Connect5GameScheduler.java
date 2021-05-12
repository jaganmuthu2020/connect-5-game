package com.connect5.game.server.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.connect5.game.server.service.Connect5Service;

/**
 * 
 * The Connect5GameScheduler used to removed the players once the game is ended. 
 * This job is running for every 5 seconds to check the status
 *
 */
@Component
public class Connect5GameScheduler {

	@Autowired
	private Connect5Service connect5Service;

	@Scheduled(cron = "${client.timeout.cron.expression}")
	private void monitorClientDisconnected() {
		if (connect5Service.isPlayerExists()) {
			connect5Service.checkGameStatusRemovePlayer();
		}
	}

}
