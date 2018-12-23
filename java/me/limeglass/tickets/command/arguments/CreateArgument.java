package me.limeglass.tickets.command.arguments;

import java.util.ArrayList;
import java.util.List;

import me.limeglass.tickets.managers.Placeholder;
import me.limeglass.tickets.objects.Ticket;
import me.limeglass.tickets.objects.TicketArgument;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CreateArgument extends TicketArgument {

	static {
		registerArgument(new CreateArgument());
	}
	
	protected CreateArgument() {
		super("create");
	}

	@Override
	public boolean execute(ProxiedPlayer player, ArrayList<String> arguments) {
		int length = configuration.getInt("IssueLegnth", 1);
		if (arguments.size() - 1 >= length) {
			Ticket ticket = new Ticket(player, String.join(" ", arguments));
			String permission = configuration.getString("Permissions.admin", "bungeetickets.admin");
			ProxyServer.getInstance().getPlayers().parallelStream().filter(p -> p.hasPermission(permission)).forEach(p -> adminMessage(p, ticket, "new-ticket"));
		} else {
			sendMessage(player, "length-not-allowed");
		}
		return true;
	}
	
	private void adminMessage(ProxiedPlayer player, Ticket ticket, String node) {
		List<String> messages = configuration.getStringList("Messages." + node);
		for (String message : messages) {
			TextComponent component = new TextComponent(Placeholder.parse(message, ticket));
			List<String> hover = configuration.getStringList("new-ticket-hover");
			ComponentBuilder builder = new ComponentBuilder(Placeholder.parse(hover.get(0), ticket));
			if (hover.size() > 1) {
				for (int i = 1; i < hover.size(); i++) {
					builder.append(Placeholder.parse(hover.get(i), ticket));
				}
			}
			component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, builder.create()));
			component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "ticket reply " + ticket.getID() + " "));
			player.sendMessage(component);
		}
	}

}
