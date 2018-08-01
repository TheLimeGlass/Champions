package me.limeglass.tickets.command;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.command.argument.ArgumentHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class TicketCommand extends Command {

	private static Configuration configuration = BungeeTickets.getConfiguration("config");
	
	public TicketCommand() {
		super("ticket", configuration.getString("Permissions.use", "bungeetickets.use"), BungeeTickets.getCommands());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(configuration.getString("Permissions.use", "bungeetickets.use"))) {
			if (configuration.getBoolean("UnknownCommand", false)) {
				sender.sendMessage(new TextComponent("Unknown command. Type \"/help\" for help."));
			} else {
				sender.sendMessage(new TextComponent(BungeeTickets.cc(BungeeTickets.getPrefix() +  configuration.getString("Messages.no-permission", "&cYou do not have the correct permissions to use this command."))));
			}
			return;
		}
		if (args == null || args.length <= 0) {
			//TODO message help menu.
		} else {
			ArgumentHandler.execute(sender, args);
		}
	}

}