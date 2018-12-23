package me.limeglass.champions.scoreboard;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

import me.limeglass.champions.Champions;
import me.limeglass.champions.objects.ChampionsPlayer;

public class ScoreboardManager {
	
	private static Map<ChampionsPlayer, ChampionsScoreboard> scoreboards = new HashMap<ChampionsPlayer, ChampionsScoreboard>();
	
	public static void addScoreboard(ChampionsPlayer player, ChampionsScoreboard scoreboard) {
		scoreboards.put(player, scoreboard);
	}
	
	public static ChampionsScoreboard getScoreboard(ChampionsPlayer player) {
		return scoreboards.get(player);
	}
	
	public static void removeScoreboard(ChampionsScoreboard scoreboard) {
		if (scoreboards.containsValue(scoreboard)) scoreboards.remove(scoreboard.getPlayer());
	}
	
	public static void delete(ChampionsPlayer player) {
		if (!scoreboards.containsKey(player)) return;
		scoreboards.get(player).delete();
		scoreboards.remove(player);
	}
	
	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Champions.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (scoreboards != null && !scoreboards.isEmpty()) {
					for (ChampionsScoreboard scoreboard : scoreboards.values()) {
						scoreboardUpdate(scoreboard);
					}
				}
			}
		}, 1, 20 * 2); //2 seconds
	}
	
	private static void scoreboardUpdate(ChampionsScoreboard scoreboard) {
		scoreboard.setSlot(15, "&6Testing example");
		scoreboard.setSlot(5, "&6Testing example 2");
		scoreboard.setSlot(2, "&6Testing example 3");
		scoreboard.setSlot(1, "&6Testing example 4");
		scoreboard.setSlot(0, "&6Testing example 5");
	}
}
