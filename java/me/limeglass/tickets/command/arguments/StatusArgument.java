package me.limeglass.tickets.command.arguments;

import java.util.ArrayList;
import java.util.Optional;
import me.limeglass.tickets.managers.TicketManager;
import me.limeglass.tickets.objects.Ticket;
import me.limeglass.tickets.objects.TicketArgument;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StatusArgument extends TicketArgument {

	static {
		registerArgument(new StatusArgument());
	}
	
	protected StatusArgument() {
		super("status");
	}

	@Override
	public boolean execute(ProxiedPlayer player, ArrayList<String> arguments) {
		if (arguments.isEmpty()) {
			Ticket ticket = TicketManager.getLatestTicket(player);
			if (ticket == null) sendMessage(player, "no-ticket");
			else status(player, ticket);
			return true;
		} else if (arguments.size() == 1) {
			int id = Integer.parseInt(arguments.get(0));
			Optional<Ticket> present = TicketManager.getTicket(id);
			if (present.isPresent()) {
				Ticket ticket = present.get();
				//Check if the player executing the command owns this Ticket. (They can view their own Ticket status, unless this is disabled in configuration.)
				if (ticket.getReporter().equals(player)) {
					status(player, ticket);
				} else if (player.hasPermission(configuration.getString("Permissions.admin"))) {
					status(player, ticket);
				} else {
					sendMessage(player, "no-permission-status");
				}
			} else {
				sendMessage(player, "no-ticket-found");
			}
			return true;
		}
		return false;
	}
	
	private void status(ProxiedPlayer player, Ticket ticket) {
		sendMessages(player, ticket, "status");
	}

}
