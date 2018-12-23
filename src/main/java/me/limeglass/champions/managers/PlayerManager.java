package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import me.limeglass.champions.objects.ChampionsPlayer;

public class PlayerManager {

	private static Set<ChampionsPlayer> players = new HashSet<>();
	
	public static void addPlayer(ChampionsPlayer player) {
		if (!players.contains(player))
			players.add(player);
	}
	
	public static void removePlayer(ChampionsPlayer player) {
		player.setConnected(false);
		player.setIngame(false);
		if (players.contains(player))
			players.remove(player);
	}
	
	public static Boolean containsPlayer(Player player) {
		return players.parallelStream()
				.map(p -> p.getPlayer())
				.anyMatch(p -> p.getUniqueId() == player.getUniqueId());
	}
	
	public static Set<ChampionsPlayer> getPlayers() {
		return players;
	}
	
	public static Set<ChampionsPlayer> getPlayersIngame() {
		return players.parallelStream()
				.filter(player -> player.isIngame())
				.collect(Collectors.toSet());
	}
	
	public static Boolean isIngame(Player player) {
		return getPlayersIngame().parallelStream()
				.anyMatch(p -> p.getPlayer() == player && p.isIngame());
	}
	
	public static Optional<ChampionsPlayer> getChampionsPlayer(Player player) {
		return players.parallelStream()
				.filter(p -> p.getPlayer() == player)
				.findFirst();
	}
	
	public static Set<ChampionsPlayer> getIdlePlayers() {
		return players.parallelStream()
				.filter(player -> !player.isIngame())
				.collect(Collectors.toSet());
	}

}
