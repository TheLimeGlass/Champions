package me.limeglass.tickets.command.argument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.Sets;

import me.limeglass.tickets.BungeeTickets;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public abstract class TicketArgument {
	
	protected static Configuration configuration = BungeeTickets.getInstance().getConfig();
	protected static String node = "Arguments.";
	private final HashSet<String> arguments;
	
	protected TicketArgument(@NonNull String argument) {
		node = node + argument + ".";
		List<String> aliases = configuration.getStringList(node + "aliases");
		this.arguments = Sets.newHashSet(aliases);
		arguments.add(argument);
	}

	public HashSet<String> getArguments() {
		return arguments;
	}
	
	public String getUsage() {
		return configuration.getString(node + "usage", "The usage was not defiend.");
	}
	
	public String getNode() {
		return node;
	}
	
	protected static void registerArgument(TicketArgument argument) {
		if (configuration.getBoolean(node + "enabled", true)) ArgumentHandler.addArgument(argument);
	}
	
	protected abstract boolean execute(ProxiedPlayer player, Set<String> arguments);

}
