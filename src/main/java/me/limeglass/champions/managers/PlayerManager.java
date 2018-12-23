package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import me.limeglass.champions.objects.ChampionsPlayer;

public class PlayerManager {

	private static Set<ChampionsPlayer> players = new HashSet<ChampionsPlayer>();
	
	public static void addPlayer(ChampionsPlayer player) {
		if (!players.contains(player)) players.add(player);
	}
	
	public static void removePlayer(ChampionsPlayer player) {
		player.setConnected(false);
		player.setIngame(false);
		if (players.contains(player)) players.remove(player);
	}
	
	public static Boolean containsPlayer(Player player) {
		for (ChampionsPlayer championsPlayer : players) {
			if (championsPlayer.getPlayer().getUniqueId() == player.getUniqueId()) {
				return true;
			}
		}
		return false;
	}
	
	public static Set<ChampionsPlayer> getPlayers() {
		return players;
	}
	
	public static Set<ChampionsPlayer> getPlayersIngame() {
		Set<ChampionsPlayer> ingamePlayers = new HashSet<ChampionsPlayer>();
		for (ChampionsPlayer player : players) {
			if (player.isIngame()) ingamePlayers.add(player);
		}
		return (ingamePlayers != null && !ingamePlayers.isEmpty()) ? ingamePlayers : null;
	}
	
	public static Boolean isIngame(Player player) {
		if (getPlayersIngame() == null) return false;
		for (ChampionsPlayer kitpvpPlayer : getPlayersIngame()) {
			if (kitpvpPlayer.getPlayer() == player && kitpvpPlayer.isIngame()) return true;
		}
		return false;
	}
	
	public static ChampionsPlayer getChampionsPlayer(Player player) {
		if (getPlayers() == null) return new ChampionsPlayer(player);
		for (ChampionsPlayer kitpvpPlayer : getPlayers()) {
			if (kitpvpPlayer.getPlayer() == player) return kitpvpPlayer;
		}
		return new ChampionsPlayer(player);
	}
	
	public static Set<ChampionsPlayer> getPlayersNotIngame() {
		Set<ChampionsPlayer> freePlayers = new HashSet<ChampionsPlayer>();
		for (ChampionsPlayer player : players) {
			if (!player.isIngame()) freePlayers.add(player);
		}
		return (freePlayers != null && !freePlayers.isEmpty()) ? freePlayers : null;
	}
	
	public static void clearPlayers() {
		for (ChampionsPlayer player : players) {
			player.quit();
		}
		players.clear();
	}
}
