package me.limeglass.champions.managers;

import org.bukkit.Bukkit;
import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.champions.Champions;
import me.limeglass.champions.objects.ChampionsPlayer;

public class Functions {
	
	public static void executeFunction(@Nullable ChampionsPlayer player, String function) {
		if (function.contains("ConsoleCommand")) {
			consoleCommand(function);
		} else if (function.contains("PlayerCommand") && player != null) {
			playerCommand(player, function);
		} else if (function.equalsIgnoreCase("Quit") && player != null) {
			quit(player);
		} else if (function.equalsIgnoreCase("Join") && player != null) {
			player.join();
		}
	}
	
	public static void quit(ChampionsPlayer player) {
		if (Champions.isBungeecordMode()) {
			//String server = Champions.getConfiguration("config").getString("FallbackServer");
			//TODO insert PluginMessage to send to server.
		}
		player.quit();
	}
	
	public static void playerCommand(ChampionsPlayer player, String input) {
		input = input.replaceFirst("(?i)PlayerCommand:", "");
		String[] commands = new String[] {input};
		if (input.contains(";")) commands = input.split(";");
		for (String command : commands) {
			player.getPlayer().performCommand(command);
		}
	}
	
	public static void consoleCommand(String input) {
		input = input.replaceFirst("(?i)ConsoleCommand:", "");
		String[] commands = new String[] {input};
		if (input.contains(";")) commands = input.split(";");
		for (String command : commands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}

}
