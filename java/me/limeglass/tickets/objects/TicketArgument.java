package me.limeglass.tickets.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.Sets;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.managers.ArgumentManager;
import me.limeglass.tickets.managers.Placeholder;
import net.md_5.bungee.api.chat.TextComponent;
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
	
	protected void sendMessage(ProxiedPlayer player, String node) {
		String message = configuration.getString("Messages." + node, "Undefined");
		player.sendMessage(new TextComponent(Placeholder.parse(message, player)));
	}
	
	protected void sendMessage(ProxiedPlayer player, Ticket ticket, String node) {
		String message = configuration.getString("Messages." + node, "Undefined");
		player.sendMessage(new TextComponent(Placeholder.parse(message, ticket)));
	}
	
	protected void sendMessages(ProxiedPlayer player, Ticket ticket, String node) {
		List<String> messages = configuration.getStringList("Messages." + node);
		messages.parallelStream()
			.map(message -> Placeholder.parse(message, ticket))
			.forEach(message -> player.sendMessage(new TextComponent(message)));
	}
	
	protected void sendMessages(ProxiedPlayer player, String node) {
		List<String> messages = configuration.getStringList("Messages." + node);
		messages.forEach(message -> player.sendMessage(new TextComponent(Placeholder.parse(message, player))));
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
		if (configuration.getBoolean(node + "enabled", true)) ArgumentManager.addArgument(argument);
	}
	
	public abstract boolean execute(ProxiedPlayer player, ArrayList<String> arguments);

}
