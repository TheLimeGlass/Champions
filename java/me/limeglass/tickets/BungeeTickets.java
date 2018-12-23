package me.limeglass.tickets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.Lists;

import me.limeglass.tickets.command.TicketCommand;
import me.limeglass.tickets.managers.ArgumentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeTickets extends Plugin {
	
	private static Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	private final static String prefix = "&8[&cBungeeTickets&8] &f";
	private static BungeeTickets instance;
	private BungecordMetrics metrics;
	
	public void onEnable(){
		instance = this;
		loadConfiguration();
		ArgumentManager.setup(instance);
		metrics = new BungecordMetrics(this);
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TicketCommand());
		if (!getConfig().getBoolean("DisableRegisteredInfo", false)) consoleMessage("&ahas been enabled!");
	}
	
	private void loadConfiguration() {
		getDataFolder().mkdir();
		File config = new File(instance.getDataFolder(), "config.yml");
		try (InputStream in = getResourceAsStream("src/main/resources/config.yml")) {
			if (!config.exists()) Files.copy(in, config.toPath());
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
			if (!getDescription().getVersion().equals(configuration.getString("version"))) {
				new BungeeConfigSaver(instance).execute();
				consoleMessage("&eThere is a new BungeeTickets version. Generating new config...");
				loadConfiguration();
				return;
			}
			configurations.put("config", configuration);
		} catch (IOException e) {}
	}
	
	public static BungeeTickets getInstance() {
		return instance;
	}
	
	public Configuration getConfig() {
		return getConfiguration("config");
	}
	
	public BungecordMetrics getMetrics() {
		return metrics;
	}
	
	public static String getPrefix() {
		return instance.getConfig().getString("Messages.prefix", prefix);
	}
	
	public static void debugMessage(String text) {
		if (instance.getConfig().getBoolean("debug")) consoleMessage("&b" + text);
	}
	
	//Grabs a Configuration of a defined name. The name can't contain .yml in it.
	public static Configuration getConfiguration(String file) {
		return (configurations.containsKey(file)) ? configurations.get(file) : null;
	}
	
	//Needed in order for custom commands.
	public static String[] getCommands() {
		List<String> commands = getConfiguration("config").getStringList("Commands");
		if (commands == null || commands.isEmpty()) commands = Lists.newArrayList("ticket", "tickets", "bungeeticket", "bungeetickets");
		return commands.toArray(new String[commands.size()]);
	}

	public static String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info("[BungeeTickets] " + text);
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (instance.getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (instance.getConfig().getBoolean("DisableConsoleColour", false)) infoMessage(ChatColor.stripColor(cc(text)));
				else ProxyServer.getInstance().getLogger().info(cc(prefix + text));
			}
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

}