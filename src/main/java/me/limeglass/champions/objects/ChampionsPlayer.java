package me.limeglass.champions.objects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.ChampionsData;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.scoreboard.ChampionsScoreboard;
import me.limeglass.champions.utils.Utils;

public class ChampionsPlayer {
	
	private ChampionsScoreboard scoreboard;
	private boolean ingame, connected;
	private Location pastLocation;
	private final Player player;
	private Inventory saved;
	private String map;
	private Kit kit;
	
	public ChampionsPlayer(Player player) {
		this.player = player;
		PlayerManager.addPlayer(this);
	}
	
	public ChampionsPlayer(Player player, String map) {
		this.player = player;
		this.map = map;
		PlayerManager.addPlayer(this);
	}
	
	public ChampionsPlayer(Player player, String map, Kit kit) {
		this.player = player;
		this.map = map;
		this.kit = kit;
		PlayerManager.addPlayer(this);
	}
	
	public void sendMessage(String... messages) {
		final String[] colouredMessages = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			colouredMessages[i] = Utils.cc(messages[i]);
		}
		player.sendMessage(colouredMessages);
	}
	
	public void playSound(String node) {
		if (Champions.getConfiguration("config").getBoolean(node + ".enabled", true)) {
			float[] pitches = new float[] {0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F};
			Sound sound = Utils.getEnum(Sound.class, Champions.getConfiguration("config").getString(node + ".sound"));
			int pitch = Champions.getConfiguration("config").getInt(node + ".pitch", 6);
			player.playSound(getLocation(), sound, SoundCategory.RECORDS, 10 / Champions.getConfiguration("config").getLong(node + ".volume", 1L), pitches[pitch]);
		}
	}
	
	public final World getWorld() {
		return player.getLocation().getWorld();
	}
	
	public final Location getLocation() {
		return player.getLocation();
	}
	
	public final Player getPlayer() {
		return player;
	}
	
	public void setMap(String map) {
		this.map = map;
	}
	
	public String getMap() {
		return map;
	}

	public void setIngame(Boolean ingame) {
		this.ingame = ingame;
	}
	
	public Boolean isIngame() {
		return ingame;
	}
	
	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	
	public Boolean isConnected() {
		if (Champions.isBungeecordMode())
			connected = true;
		return connected;
	}

	public Kit getKit() {
		return kit;
	}
	
	public Boolean hasKit() {
		return kit != null;
	}
	
	public void setKit(Kit kit) {
		this.kit = kit;
	}
	
	public void applyKit(Kit kit) {
		this.kit = kit;
		kit.applyKit(this);
	}

	public ChampionsScoreboard getScoreboard() {
		return scoreboard;
	}
	
	public Inventory getSavedInventory() {
		return saved;
	}

	public void setSavedInventory(Inventory inventory) {
		this.saved = inventory;
	}
	
	public Location getPastLocation() {
		return pastLocation;
	}

	public void setPastLocation(Location pastLocation) {
		this.pastLocation = pastLocation;
	}

	public void setScoreboard(ChampionsScoreboard scoreboard) {
		this.scoreboard = scoreboard;
		player.setScoreboard(scoreboard.getScoreboard());
	}
	
	public void quit() {
		connected = false;
		ingame = false;
		if (Champions.getConfiguration("config").getBoolean("endTeleportSpawn")) {
			player.teleport(ChampionsData.getSpawn());
		} else {
			if (pastLocation == null) 
				player.teleport(ChampionsData.getSpawn());
			else 
				player.teleport(pastLocation);
		}
		//TODO insert quit stuff
	}
	
	public void join() {
		saved = player.getInventory();
		pastLocation = player.getLocation();
		if (!Utils.isEmpty(player.getInventory())) player.getInventory().clear();
		for (String value : Champions.getConfiguration("joinItems").getConfigurationSection("JoinItems").getKeys(false)) {
			int slot = Integer.parseInt(value);
			if (!(slot < 0 || slot > InventoryType.PLAYER.getDefaultSize())) {
				player.getInventory().setItem(slot, Utils.getItem(Champions.getConfiguration("joinItems"), "JoinItems." + value));
			}
		}
		//TODO player.teleport(stuff
		setScoreboard(new ChampionsScoreboard(this));
		player.teleport(ChampionsData.getSpawn());
		connected = true;
	}

}
