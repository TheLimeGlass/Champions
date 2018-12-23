package me.limeglass.champions.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.limeglass.champions.Champions;
import me.limeglass.champions.objects.ChampionsGame;
import me.limeglass.champions.objects.ChampionsGame.ChampionsMode;
import me.limeglass.champions.utils.Utils;

public class GameManager {

	public static Map<Player, ChampionsGame> tempgames = new HashMap<Player, ChampionsGame>();
	private static Set<ChampionsGame> games = new HashSet<ChampionsGame>();
	
	public static void addGame(ChampionsGame game) {
		if (!games.contains(game)) games.add(game);
	}
	
	public static void removeGame(ChampionsGame game) {
		game.end();
		if (games.contains(game)) games.remove(game);
	}
	
	public static Boolean containsGame(String name) {
		return getGame(name) != null;
	}
	
	public static Set<ChampionsGame> getGames() {
		return games;
	}
	
	public static ChampionsGame getGame(String name) {
		for (ChampionsGame game : games) {
			if (game.getName().equals(name)) {
				return game;
			}
		}
		return null;
	}
	
	public static Set<ChampionsGame> getRunningGames() {
		Set<ChampionsGame> running = new HashSet<ChampionsGame>();
		for (ChampionsGame game : games) {
			if (game.isIngame()) running.add(game);
		}
		return (running != null && !running.isEmpty()) ? running : null;
	}
	
	public static Set<ChampionsGame> getIdlingGames() {
		Set<ChampionsGame> idle = new HashSet<ChampionsGame>();
		for (ChampionsGame game : games) {
			if (!game.isIngame()) idle.add(game);
		}
		return (idle != null && !idle.isEmpty()) ? idle : null;
	}
	
	public static void clearGames() {
		for (ChampionsGame game : games) {
			game.end();
		}
		games.clear();
	}
	
	public static void load() {
		FileConfiguration configuration = Champions.getConfiguration("data");
		for (String arena : configuration.getConfigurationSection("Arenas").getKeys(false)) {
			new ChampionsGame(false, UUID.randomUUID().toString(), Utils.getEnum(ChampionsMode.class, configuration.getString("Arenas." + arena + ".gamemode", "TEAMDEATHMATCH")));
		}
	}
}
