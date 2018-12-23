package me.limeglass.champions.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import me.limeglass.champions.Champions;
import me.limeglass.champions.abstracts.Ability;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.objects.ChampionsPlayer;

public class EventHandler {
	
	private static Event last;
	private static Set<Class<? extends Event>> registeredEvents = new HashSet<Class<? extends Event>>();
	private final static Listener listener = new Listener(){};
	private static Map<Ability, Class<? extends Event>[]> abilities = new HashMap<Ability, Class<? extends Event>[]>();
	
	public static void remove(Ability ability) {
		if (abilities.containsKey(ability)) abilities.remove(ability);
	}
	
	public static Map<Ability, Class<? extends Event>[]> getEventAbilities() {
		return abilities;
	}
	
	final static EventExecutor executor = new EventExecutor() {
		@Override
		public void execute(final Listener listner, final Event event) {
			if (event == null || last == event) return; // an event is received multiple times if multiple superclasses of it are registered
			last = event;
			for (Entry<Ability, Class<? extends Event>[]> entry : abilities.entrySet()) {
				Player player = entry.getKey().check(event);
				if (player != null) {
					ChampionsPlayer championsPlayer = PlayerManager.getChampionsPlayer(player);
					if (championsPlayer != null && championsPlayer.hasKit() && championsPlayer.getKit().getAbilities().contains(entry.getKey())) {
						entry.getKey().onAbilityExecute(championsPlayer);
					}
				}
			}
		}
	};
	
	@SafeVarargs
	public static void register(Ability ability, Class<? extends Event>... events) {
		if (!abilities.containsKey(ability)) {
			abilities.put(ability, events);
			for (Class<? extends Event> event : events) {
				Bukkit.getPluginManager().registerEvent(event, listener, EventPriority.HIGHEST, executor, Champions.getInstance());
				registeredEvents.add(event);
			}
		}
	}
}
