package me.limeglass.champions.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.limeglass.champions.Champions;
import me.limeglass.champions.objects.ChampionsGame;
import me.limeglass.champions.objects.ChampionsGame.ChampionsMode;
import me.limeglass.champions.utils.Utils;

public class GameManager {

	public static Map<Player, ChampionsGame> tempgames = new HashMap<>();
	private static Set<ChampionsGame> games = new HashSet<>();
	
	public static void addGame(ChampionsGame game) {
		if (!games.contains(game))
			games.add(game);
	}
	
	public static void removeGame(ChampionsGame game) {
		game.end();
		if (games.contains(game))
			games.remove(game);
	}
	
	public static Boolean containsGame(String name) {
		return getGame(name).isPresent();
	}
	
	public static Set<ChampionsGame> getGames() {
		return games;
	}
	
	public static Optional<ChampionsGame> getGame(String name) {
		return games.parallelStream()
				.filter(game -> game.getName().equals(name))
				.findFirst();
	}
	
	public static Set<ChampionsGame> getRunningGames() {
		return games.parallelStream()
				.filter(game -> game.isIngame())
				.collect(Collectors.toSet());
	}
	
	public static Set<ChampionsGame> getIdlingGames() {
		return games.parallelStream()
				.filter(game -> !game.isIngame())
				.collect(Collectors.toSet());
	}
	
	public static void endGames() {
		games.forEach(game -> game.end());
		games.clear();
	}
	
	public static void load() {
		FileConfiguration configuration = Champions.getConfiguration("data");
		for (String arena : configuration.getConfigurationSection("Arenas").getKeys(false)) {
			new ChampionsGame(false, UUID.randomUUID().toString(), Utils.getEnum(ChampionsMode.class, configuration.getString("Arenas." + arena + ".gamemode", "TEAMDEATHMATCH")));
		}
	}

}
