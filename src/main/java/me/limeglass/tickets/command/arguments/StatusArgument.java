package me.limeglass.tickets.command.arguments;

import java.util.Set;

import me.limeglass.tickets.command.argument.TicketArgument;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StatusArgument extends TicketArgument {

	static {
		registerArgument(new StatusArgument());
	}
	
	protected StatusArgument() {
		super("status");
	}

	@Override
	protected boolean execute(ProxiedPlayer player, Set<String> arguments) {
		if (arguments.isEmpty()) {
			
		} else {
			
		}
		return false;
	}

}
