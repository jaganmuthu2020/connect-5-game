package com.connect5.game.server.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.connect5.game.common.Player;

public class GameUtil {

	public static Map<String, Player> PLAYERS_DB = new ConcurrentHashMap<String, Player>();

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

}
