package me.limeglass.champions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.limeglass.champions.commands.CommandHandler;
import me.limeglass.champions.listeners.EventListener;
import me.limeglass.champions.managers.GameManager;
import me.limeglass.champions.objects.ChampionsGame;
import me.limeglass.champions.utils.Utils;

/*
 * Main class for the Champions plugin.
 * Developed by LimeGlass
*/

public class Champions extends JavaPlugin {
	
	private static Map<String, FileConfiguration> files = new HashMap<String, FileConfiguration>();
	private static Set<ChampionsAddon> addons = new HashSet<ChampionsAddon>();
	//Nameplate is currently only used in console prefix messages.
	private final static String nameplate = "[Champions] ";
	private static ChampionsAddon registrar;
	private static Champions instance;
	private static File championsDataFolder;
	
	public void onEnable(){
		instance = this;
		getCommand("champions").setExecutor(new CommandHandler());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		File configFile = new File(getDataFolder(), "config.yml");
		championsDataFolder = new File(getDataFolder(), File.separator + "data");
		championsDataFolder.mkdir();
		//If newer version was found, update config.
		if (!getDescription().getVersion().equals(getConfig().getString("version"))) {
			if (configFile.exists()) configFile.delete();
		}
		//Create all the default files
		for (String name : Arrays.asList("config", "data", "joinItems", "abilities", "messages", "kits", "menus")) {
			File file = new File(getDataFolder(), name + ".yml");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				saveResource(file.getName(), false);
				debugMessage("created new default file " + file.getName());
			}
			FileConfiguration configuration = new YamlConfiguration();
			try {
				configuration.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			files.put(name, configuration);
		}
		GameManager.load();
		registrar = new ChampionsAddon(this);
		registrar.loadClasses("me.limeglass.champions", "menus", "abilities");
		consoleMessage("has been enabled!");
	}
	
	public void onDisable() {
		for (ChampionsGame game : GameManager.tempgames.values()) {
			game.delete();
		}
	}
	
	public static void save(String configuration) {
		try {
			File configurationFile = new File(instance.getDataFolder(), configuration + ".yml");
			getConfiguration(configuration).save(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Champions getInstance() {
		return instance;
	}
	
	//Used internally, there is no need to use this method.
	static void registerAddon(ChampionsAddon addon) {
		addons.add(addon);
	}
	
	public static ChampionsAddon getRegistrar() {
		return registrar;
	}
	
	public String getNameplate() {
		return nameplate;
	}
	
	public static Boolean isBungeecordMode() {
		return getConfiguration("config").getBoolean("Bungeecord");
	}
	
	//Grabs a FileConfiguration of a defined name. The name can't contain .yml in it.
	public static FileConfiguration getConfiguration(String file) {
		return (files.containsKey(file)) ? files.get(file) : null;
	}
	
	public static void debugMessage(String text) {
		if (instance.getConfig().getBoolean("debug")) consoleMessage("&b" + text);
	}

	public static void consoleMessage(String... messages) {
		for (String text : messages) Bukkit.getConsoleSender().sendMessage(Utils.cc(nameplate + text));
	}

	public static File getChampionsDataFolder() {
		return championsDataFolder;
	}

	public static void setChampionsDataFolder(File championsDataFolder) {
		Champions.championsDataFolder = championsDataFolder;
	}
}