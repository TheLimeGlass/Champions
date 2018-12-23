package me.limeglass.champions.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.Functions;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.objects.ChampionsPlayer;
import me.limeglass.champions.utils.Utils;

public class EventListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		FileConfiguration items = Champions.getConfiguration("joinItems");
		Inventory inventory = event.getClickedInventory();
		Player player = (Player) event.getWhoClicked();
		Optional<ChampionsPlayer> optional = PlayerManager.getChampionsPlayer(player);
		if (!optional.isPresent())
			return;
		ChampionsPlayer championsPlayer = optional.get();
		if (InventoryManager.isMenu(inventory)) {
			event.setCancelled(true);
			InventoryManager.getMenu(inventory).onInventoryClick(event);
		} else if (inventory.getType() == InventoryType.PLAYER && championsPlayer.isIngame()) {
			if (!player.hasPermission("champions.ingame.moveinventory"))
				event.setCancelled(true);
		} else if (inventory.getType() == InventoryType.PLAYER && championsPlayer.isConnected()) {
			if (Champions.isBungeecordMode() && !player.hasPermission("champions.ingame.moveinventory")) {
				event.setCancelled(true);
				return;
			}
			ItemStack current = event.getCurrentItem();
			if (current != null && current.getType() != Material.AIR) {
				if (Utils.getItem(items, "JoinItems." + event.getSlot()).isSimilar(current)) {
					event.setCancelled(true);
					if (items.isSet("JoinItems." + event.getSlot() + ".function")) {
						Functions.executeFunction(championsPlayer, items.getString("JoinItems." + event.getSlot() + ".function"));
					}
				}
			}
		}
    }
	
	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
		FileConfiguration items = Champions.getConfiguration("joinItems");
		Player player = event.getPlayer();
		Optional<ChampionsPlayer> optional = PlayerManager.getChampionsPlayer(player);
		if (!optional.isPresent())
			return;
		ChampionsPlayer championsPlayer = optional.get();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (item != null && item.getType() != Material.AIR && championsPlayer.isConnected()) {
				for (String slot : items.getConfigurationSection("JoinItems").getKeys(false)) {
					if (Utils.getItem(items, "JoinItems." + slot).isSimilar(event.getItem())) {
						event.setCancelled(true);
						if (items.isSet("JoinItems." + slot + ".function")) {
							Functions.executeFunction(championsPlayer, items.getString("JoinItems." + slot + ".function"));
							break;
						}
					}
				}
			}
		}
    }
	
	@EventHandler
    public void onJoin(PlayerLoginEvent event) {
		if (Champions.isBungeecordMode()) {
			final Player player = event.getPlayer();
			//This task is delayed because on some Minecraft versions there is a glitch that when clearing an inventory on join
			//it makes the player deal less damage for some reason.
			Bukkit.getScheduler().scheduleSyncDelayedTask(Champions.getInstance(), new Runnable() {
				@Override
				public void run() {
					Optional<ChampionsPlayer> optional = PlayerManager.getChampionsPlayer(player);
					if (!optional.isPresent())
						return;
					optional.get().join();
				}
			}, 1);
		}
    }
	
	@EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
		//because Spigot unloads plugins before this event is called...
		try {
			Optional<ChampionsPlayer> optional = PlayerManager.getChampionsPlayer(event.getPlayer());
			if (!optional.isPresent())
				return;
			ChampionsPlayer player = optional.get();
			PlayerManager.removePlayer(player);
		} catch (Exception stupidSpigot) {}
    }
	
	@EventHandler
    public void onDisconnect(WeatherChangeEvent event) {
		if (Champions.isBungeecordMode())
			event.setCancelled(event.toWeatherState());
    }

}
