package me.limeglass.champions.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.ChampionsData;
import me.limeglass.champions.managers.GameManager;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.objects.ChampionsGame;
import me.limeglass.champions.objects.ChampionsGame.ChampionsMode;
import me.limeglass.champions.utils.Utils;

public class CommandHandler implements CommandExecutor {

	//TODO Turn this into an abstract API calling like Abilities.
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Champions.consoleMessage("Only players may execute this command!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("setup") && player.hasPermission("champions.admin")) {
				ChampionsGame game = GameManager.tempgames.get(player);
				if (args.length == 1) {
					GameManager.tempgames.put(player, new ChampionsGame(true, UUID.randomUUID().toString(), ChampionsMode.TEAMDEATHMATCH));
					player.sendMessage(Utils.getMessage(true, "setup.1", player));
				} else if (args.length >= 2) {
					if (game == null) player.sendMessage(Utils.getMessage(true, "setup.notInSetup", player));
					else if (args.length > 2) {
						if (args[1].equalsIgnoreCase("name")) {
							game.setName(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
							player.sendMessage(Utils.getMessage(true, "setup.2", player));
						} else if (args[1].equalsIgnoreCase("mode")) {
							ChampionsMode mode = Utils.getEnum(ChampionsMode.class, args[2]);
							if (mode != null) {
								GameManager.tempgames.put(player, new ChampionsGame(true, game.getName(), mode));
								player.sendMessage(Utils.getMessage(true, "setup.3", player));
							} else {
								player.sendMessage(Utils.getMessage(true, "setup.modeNotFound", player));
							}
						} else if (args[1].equalsIgnoreCase("addspawn")) {
							String team1 = Champions.getConfiguration("config").getString("Teams.colour1");
							int team = (args[2].equalsIgnoreCase(team1)) ? 1 : 2;
							game.addTeamSpawn(team, game.getTeamSpawns(team).size(), player.getLocation());
							if (team == 1) player.sendMessage(Utils.getMessage(true, "setup.4", player));
							else player.sendMessage(Utils.getMessage(true, "setup.5", player));
						} else if (args[1].equalsIgnoreCase("addkitspawn")) {
							int kitIndex = 1;
							for (String node : Champions.getConfiguration("kits").getConfigurationSection("Kits").getKeys(false)) {
								if (node.equalsIgnoreCase(args[2])) {
									game.addKitSpawn(kitIndex, player.getLocation());
									if (game.kitSpawnsAreSetup()) {
										player.sendMessage(Utils.getMessage(true, "setup.7", player));
									} else {
										player.sendMessage(Utils.getMessage(true, "setup.6", player));
									}
									break;
								}
							}
						} else if (args[1].equalsIgnoreCase("capture")) {
							game.setHasCaptures(Boolean.parseBoolean(args[2]));
							if (game.hasCaptures()) {
								player.sendMessage(Utils.getMessage(true, "setup.9", player));
							} else {
								player.sendMessage(Utils.getMessage(true, "setup.almost", player));
							}
						} else if (args[1].equalsIgnoreCase("addcapture") && game.hasCaptures()) {
							game.addCapture(args[2], player.getLocation().subtract(0, 1, 0));
							player.sendMessage(Utils.getMessage(true, "setup.10", player));
							player.sendMessage(Utils.getMessage(true, "setup.almost", player));
						} else {
							player.sendMessage(Utils.getMessage(true, "setupInvalidInput", player));
						}
						//TODO Emeralds and Inventory restock setup for DOM mode when that's made.
					} else if (args[1].equalsIgnoreCase("addspectator")) {
						game.addSpectatorSpawn(player.getLocation());
						player.sendMessage(Utils.getMessage(true, "setup.8", player));
					} else if (args[1].equalsIgnoreCase("quit") || args[1].equalsIgnoreCase("exit")) {
						GameManager.tempgames.remove(player);
						player.sendMessage(Utils.getMessage(true, "setup.left", player));
					} else if (args[1].equalsIgnoreCase("finish") && game != null) {
						GameManager.addGame(game);
						GameManager.tempgames.remove(player);
						player.sendMessage(Utils.getMessage(true, "finish", player));
					} else {
						player.sendMessage(Utils.getMessage(true, "setupInvalidInput", player));
					}
				}
			} else if (args[0].equalsIgnoreCase("admin") && player.hasPermission("champions.admin")) {
				player.sendMessage(Utils.getMessage(false, "adminhelp", player));
			} else if (args[0].equalsIgnoreCase("setspawn") && player.hasPermission("champions.admin")) {
				ChampionsData.setSpawn(player.getLocation());
				player.sendMessage(Utils.getMessage(true, "setspawn", player));
			} else if (args[0].equalsIgnoreCase("spawn")) {
				//TODO check if the player is in a Champions world + config option for it.
				if (!PlayerManager.isIngame(player)) {
					Location spawn = ChampionsData.getSpawn();
					if (spawn != null) {
						player.teleport(ChampionsData.getSpawn());
						player.sendMessage(Utils.getMessage(true, "spawnteleport", player));
					} else {
						player.sendMessage(Utils.getMessage(true, "spawnNotSet", player));
					}
				} else {
					player.sendMessage(Utils.getMessage(true, "spawnteleportIngame", player));
				}
			} else {
				player.performCommand("/champions");
			}
		} else {
			player.sendMessage(Utils.getMessage(false, "commandhelp", player));
		}
		return true;
	}

}
