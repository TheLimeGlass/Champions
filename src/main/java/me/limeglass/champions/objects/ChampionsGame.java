package me.limeglass.champions.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.GameManager;
import me.limeglass.champions.managers.PlayerManager;

public class ChampionsGame {
	
	private Map<Integer, Set<Location>> KIT_SPAWNS = new HashMap<Integer, Set<Location>>();
	private Map<String, Location> CAPTURES = new HashMap<String, Location>();
	private FileConfiguration data = Champions.getConfiguration("data");
	private Set<Location> SPECTATOR_SPAWNS = new LinkedHashSet<Location>();
	private Set<Location> TEAM1_SPAWNS = new LinkedHashSet<Location>();
	private Set<Location> TEAM2_SPAWNS = new LinkedHashSet<Location>();
	private final ChampionsMode mode;
	private ChampionsState state;
	private Boolean captures;
	private String name;
	
	private enum ChampionsState {
		WAITING,
		INGAME,
		STARTING,
		RESTARTING,
		DISABLED;
	}
	
	public enum ChampionsMode {
		TEAMDEATHMATCH;
	}
	
	public ChampionsGame(Boolean temp, String name, ChampionsMode mode) {
		this.mode = mode;
		this.name = name;
		data.set("Arenas." + name + ".gamemode", mode.toString());
		Champions.save("data");
		if (!temp) GameManager.addGame(this);
	}
	
	public final Set<ChampionsPlayer> getPlayers() {
		Set<ChampionsPlayer> players = new HashSet<ChampionsPlayer>();
		for (ChampionsPlayer player : PlayerManager.getPlayers()) {
			if (player.getMap().equalsIgnoreCase(name)) players.add(player);
		}
		return players;
	}

	public Boolean isIngame() {
		return state == ChampionsState.INGAME;
	}
	
	public Boolean hasCaptures() {
		return captures;
	}
	
	public void setHasCaptures(Boolean captures) {
		this.captures = captures;
	}
	
	public void setState(ChampionsState state) {
		this.state = state;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<Location> getKitSpawns(String kit) {
		int index = 1;
		for (String node : Champions.getConfiguration("kits").getConfigurationSection("kits").getKeys(false)) {
			if (node.equalsIgnoreCase(kit)) {
				if (!KIT_SPAWNS.containsKey(index) || KIT_SPAWNS.get(index).isEmpty()) {
					KIT_SPAWNS.put(index, getLocations("Arenas." + name + ".kit" + index + ".spawns"));
				}
				return KIT_SPAWNS.get(index);
			}
			index++;
		}
		return null;
	}
	
	public void delete() {
		data.set("Arenas." + name, null);
		if (GameManager.containsGame(name)) {
			GameManager.removeGame(GameManager.getGame(name));
		}
		Champions.save("data");
	}
	
	//This isn't working
	public Boolean kitSpawnsAreSetup() {
		if (KIT_SPAWNS == null || KIT_SPAWNS.isEmpty()) return false;
		int length = Champions.getConfiguration("kits").getConfigurationSection("Kits").getKeys(false).size();
		return KIT_SPAWNS.keySet().size() >= length * 2;
	}
	
	public void addKitSpawn(int kit, Location location) {
		String node = "Arenas." + name + ".kit" + kit + ".spawns";
		int index = 1;
		Set<Location> locations = getLocations(node);
		if (locations != null) index = locations.size() + 1; 
		node = node + "." + index;
		data.set(node + ".x", location.getX());
		data.set(node + ".y", location.getY());
		data.set(node + ".z", location.getZ());
		data.set(node + ".pitch", location.getPitch());
		data.set(node + ".yaw", location.getYaw());
		data.set(node + ".world", location.getWorld().getName());
		Champions.save("data");
		KIT_SPAWNS.put(kit, getLocations("Arenas." + name + ".kit" + kit + ".spawns"));
	}
	
	public void addSpectatorSpawn(Location location) {
		String node = "Arenas." + name + ".spectator.spawns";
		int index = 1;
		Set<Location> locations = getLocations(node);
		if (locations != null) index = locations.size() + 1; 
		node = node + "." + index;
		data.set(node + ".x", location.getX());
		data.set(node + ".y", location.getY());
		data.set(node + ".z", location.getZ());
		data.set(node + ".pitch", location.getPitch());
		data.set(node + ".yaw", location.getYaw());
		data.set(node + ".world", location.getWorld().getName());
		Champions.save("data");
		SPECTATOR_SPAWNS.add(location);
	}
	
	public void addCapture(String name, Location location) {
		String node = "Arenas." + name + ".capture." + name;
		data.set(node + ".x", location.getX());
		data.set(node + ".y", location.getY());
		data.set(node + ".z", location.getZ());
		data.set(node + ".pitch", location.getPitch());
		data.set(node + ".yaw", location.getYaw());
		data.set(node + ".world", location.getWorld().getName());
		Champions.save("data");
		CAPTURES.put(name, location);
	}
	
	public Set<Location> getTeamSpawns(int team) {
		if (TEAM1_SPAWNS == null || TEAM1_SPAWNS.isEmpty()) {
			TEAM1_SPAWNS = getLocations("Arenas." + name + ".team1.spawns");
		}
		if (TEAM2_SPAWNS == null || TEAM2_SPAWNS.isEmpty()) {
			TEAM2_SPAWNS = getLocations("Arenas." + name + ".team2.spawns");
		}
		return (team == 1) ? TEAM1_SPAWNS : TEAM2_SPAWNS;
	}
	
	public void addTeamSpawn(int team, int index, Location location) {
		String node = "Arenas." + name + ".team" + team + ".spawns." + index;
		data.set(node + ".x", location.getX());
		data.set(node + ".y", location.getY());
		data.set(node + ".z", location.getZ());
		data.set(node + ".pitch", location.getPitch());
		data.set(node + ".yaw", location.getYaw());
		data.set(node + ".world", location.getWorld().getName());
		Champions.save("data");
		if (team == 1) TEAM1_SPAWNS.add(location);
		else TEAM2_SPAWNS.add(location);
	}
	
	private Set<Location> getLocations(String node) {
		Set<Location> LOCATIONS = new LinkedHashSet<Location>();
		if (data.isConfigurationSection(node)) {
			for (String index : data.getConfigurationSection(node).getKeys(false)) {
				int x = data.getInt(node + "." + index + ".x", 0);
				int y = data.getInt(node + "." + index + ".y", 0);
				int z = data.getInt(node + "." + index + ".z", 0);
				int pitch = data.getInt(node + "." + index + ".pitch", 0);
				int yaw = data.getInt(node + "." + index + ".yaw", 0);
				World world = Bukkit.getWorld(data.getString(node + "." + index + ".world", "world"));
				if (world != null) {
					LOCATIONS.add(new Location(world, x, y, z, pitch, yaw));
				}
			}
		}
		return LOCATIONS;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void end() {
		//TODO
	}

	public ChampionsMode getMode() {
		return mode;
	}
}