package me.limeglass.tickets.objects;

import me.limeglass.tickets.managers.TicketManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Ticket {

	private final ProxiedPlayer reporter;
	private final long creation;
	private final String issue;
	private final int id;
	
	public Ticket(ProxiedPlayer reporter, String issue) {
		this.creation = System.currentTimeMillis();
		this.id = TicketManager.getEmptyID();
		this.reporter = reporter;
		this.issue = issue;
	}

	public ProxiedPlayer getReporter() {
		return reporter;
	}
	
	public long getCreation() {
		return creation;
	}

	public String getIssue() {
		return issue;
	}

	public int getID() {
		return id;
	}

}
