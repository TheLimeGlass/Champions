package me.limeglass.tickets.managers;

import java.util.regex.Pattern;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.objects.Ticket;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Placeholder {

	private static String quote(String input) {
		return Pattern.quote(input);
	}
	
	public static String parse(String input, ProxiedPlayer player) {
		input = input.replace(quote("{PLAYER}"), player.getName());
		input = input.replace(quote("{PREFIX}"), BungeeTickets.getPrefix());
		input = input.replace(quote("{PLAYER_DISPLAY}"), player.getDisplayName());
		return BungeeTickets.cc(input);
	}
	
	public static String parse(String input, Ticket ticket) {
		input = input.replace(quote("{TICKET_ID}"), ticket.getID() + "");
		input = input.replace(quote("{TICKET_ISSUE}"), ticket.getIssue());
		input = input.replace(quote("{PREFIX}"), BungeeTickets.getPrefix());
		input = input.replace(quote("{PLAYER}"), ticket.getReporter().getName());
		input = input.replace(quote("{REPORTER}"), ticket.getReporter().getName());
		input = input.replace(quote("{TICKET_DATE}"), TicketManager.getDateFormat(ticket));
		input = input.replace(quote("{PLAYER_DISPLAY}"), ticket.getReporter().getDisplayName());
		input = input.replace(quote("{REPORTER_DISPLAY}"), ticket.getReporter().getDisplayName());
		return BungeeTickets.cc(input);
	}
	
}
