package me.limeglass.tickets.managers;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.objects.Ticket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class TicketManager {

private static final Map<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();
	
	public static int getEmptyID() {
		int i = 1;
		while (tickets.containsKey(i)) i++;
		return i;
	}

	public static Optional<Ticket> getTicket(int id) {
		if (!tickets.containsKey(id)) return Optional.empty();
		return Optional.of(tickets.get(id));
	}
	
	public static void addTicket(@NonNull Ticket ticket) {
		int id = ticket.getID();
		if (!tickets.containsKey(id)) tickets.put(id, ticket);
	}
	
	public static void removeTicket(int id) {
		tickets.remove(id);
	}
	
	public static Set<Entry<Integer, Ticket>> getEntrySet() {
		return tickets.entrySet();
	}
	
	public static Set<Ticket> getTicketsByReporter(ProxiedPlayer player) {
		return tickets.values().parallelStream().filter(ticket -> ticket.getReporter().equals(player)).collect(Collectors.toSet());
	}
	
	public static String getDateFormat(Ticket ticket) {
		Configuration configuration = BungeeTickets.getInstance().getConfig();
		SimpleDateFormat formater = new SimpleDateFormat(configuration.getString("DateFormat", "MMM d, yyyy at kk:mm"));
		return formater.format(new Date(ticket.getCreation()));
	}
	
	public static Ticket getLatestTicket(ProxiedPlayer player) {
		Ticket latest = null;
		long time = 0;
		for (Ticket ticket : getTicketsByReporter(player)) {
			if (time <= 0) {
				latest = ticket;
				time = ticket.getCreation();
			}
			else if (time < ticket.getCreation()) {
				latest = ticket;
				time = ticket.getCreation();
			}
		}
		return latest;
	}
	
	public static Map<Integer, Ticket> getTicketMap() {
		return tickets;
	}
	
	public static Collection<Ticket> getTickets() {
		return tickets.values();
	}
	
	public static Set<Integer> getIds() {
		return tickets.keySet();
	}

}