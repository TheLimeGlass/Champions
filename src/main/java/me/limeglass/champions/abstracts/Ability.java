package me.limeglass.champions.abstracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.limeglass.champions.Champions;
import me.limeglass.champions.ChampionsAddon;
import me.limeglass.champions.objects.ChampionsPlayer;
import me.limeglass.champions.utils.Utils;

public abstract class Ability {

	protected final FileConfiguration messages = Champions.getConfiguration("messages");
	protected final FileConfiguration menu = Champions.getConfiguration("abilities");
	protected Map<ChampionsPlayer, Integer> cooldowns = new HashMap<>();
	private final Class<? extends Ability> ability;
	private static ChampionsAddon addon;
	private final String name, node;
	private final int cooldown;
	private boolean disabled;
	
	public Ability(String name, int cooldown) {
		Optional<ChampionsAddon> registrar = Champions.getCurrentRegistrar();
		if (registrar.isPresent())
			addon = registrar.get();
		this.node = "Abilities." + name;
		this.ability = getClass();
		this.cooldown = cooldown;
		this.name = name;
		if (menu.isSet(node + "." + name))
			this.disabled = menu.getBoolean(node + "." + name, false);
		else {
			menu.set(node, disabled);
			Champions.save("abilities");
		}
	}
	
	public void startCooldown(ChampionsPlayer player) {
		if (!cooldowns.containsKey(player)) {
			cooldowns.put(player, cooldown);
			Bukkit.getScheduler().runTaskAsynchronously(Champions.getInstance(), new Runnable() {
				@Override
				public void run() {
					try {
						for (int i = cooldown; i > 0; i--) {
							Thread.sleep(1000);
							cooldowns.replace(player, i);
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					cooldowns.remove(player);
					onCooldownEnd(player);
					sendPlaceholderMessage(player, "abilityCooldownFinish");
				}
			});
		}
	}
	
	public void onAbilityExecute(ChampionsPlayer player) {
		if (!disabled) {
			if (cooldowns.containsKey(player)) {
				sendPlaceholderMessage(player, "abilityCooldown");
			} else {
				startCooldown(player);
				onAbilityUse(player);
			}
		}
	}
	
	@SafeVarargs
	protected static void registerAbility(Ability ability, Class<? extends Event>... events) {
		addon.registerAbility(ability, events);
	}
	
	protected void sendPlaceholderMessage(ChampionsPlayer player, String node) {
		String prefix = messages.getString("prefix");
		String message = messages.getString(node);
		message = message.replaceAll(Pattern.quote("{ABILITY}"), getName());
		if (cooldowns.containsKey(player))
			message = message.replaceAll(Pattern.quote("{COOLDOWN}"), cooldowns.get(player) + "");
		player.sendMessage(Utils.colour(prefix + message));
	}
	
	/**
	 * Called when an Ability is used.
	 * @param player The ChampionsPlayer calling the Ability.
	 */
	protected abstract void onAbilityUse(ChampionsPlayer player);
	
	/**
	 * Called when the cooldown of an Ability for a player has ended.
	 * @param player The ChampionsPlayer with the cooldown expiring.
	 */
	protected abstract void onCooldownEnd(ChampionsPlayer player);
	
	/**
	 * When one of the defined events are called, this method will require to check that the Ability is good to trigger and to return the player of the event.
	 * @param event The event being called to be checked apon.
	 * @return The Player involved in the Event if all checks are good.
	 */
	public abstract Player check(Event event);

	public final Class<? extends Ability> getAbilityClass() {
		return ability;
	}
	
	public final Set<ChampionsPlayer> getPlayerCooldowns() {
		return cooldowns.keySet();
	}
	
	/**
	 * @return The defined time of this Ability in the configurations.
	 */
	public final int getCooldown() {
		return cooldown;
	}
	
	/**
	 * @return The name of the Ability.
	 */
	public final String getName() {
		return name;
	}

}
