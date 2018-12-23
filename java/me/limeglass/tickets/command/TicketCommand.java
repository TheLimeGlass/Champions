package me.limeglass.tickets.command;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.managers.ArgumentManager;
import me.limeglass.tickets.managers.Placeholder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class TicketCommand extends Command {

	private static Configuration configuration = BungeeTickets.getConfiguration("config");
	
	public TicketCommand() {
		super("ticket", configuration.getString("Permissions.use", "bungeetickets.use"), BungeeTickets.getCommands());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			BungeeTickets.consoleMessage("Only players may execute Ticket commands, the system is built around using the command executer.");
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if (!player.hasPermission(configuration.getString("Permissions.use", "bungeetickets.use"))) {
			if (configuration.getBoolean("UnknownCommand", false)) {
				player.sendMessage(new TextComponent("Unknown command. Type \"/help\" for help."));
			} else {
				String message = configuration.getString("Messages.no-permission", "&cYou do not have the correct permissions to use this command.");
				player.sendMessage(new TextComponent(Placeholder.parse(message, player)));
			}
			return;
		}
		if (args == null) return;
		if (args.length <= 0) {
			//HELP MENU
		} else {
			ArgumentManager.execute(player, args);
		}
	}

}