package me.limeglass.champions.abstracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.limeglass.champions.Champions;
import me.limeglass.champions.objects.ChampionsPlayer;
import me.limeglass.champions.utils.Utils;

public abstract class Ability {

	private final FileConfiguration menu = Champions.getConfiguration("abilities");
	private final Class<? extends Ability> abilityClass;
	private final int cooldown;
	private final String name;
	private String node;
	private Boolean disabled = false;
	private Map<ChampionsPlayer, Integer> cooldowns = new HashMap<ChampionsPlayer, Integer>();
	
	public Ability(String name, int cooldown) {
		this.node = "Abilities." + name;
		this.name = name;
		this.abilityClass = getClass();
		this.cooldown = cooldown;
		if (menu.isSet(node + "." + name)) this.disabled = menu.getBoolean(node + "." + name, false);
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
		Champions.getRegistrar().registerAbility(ability, events);
	}
	
	protected void sendPlaceholderMessage(ChampionsPlayer player, String node) {
		String prefix = Champions.getConfiguration("messages").getString("prefix");
		String message = Champions.getConfiguration("messages").getString(node);
		message = message.replaceAll(Pattern.quote("{ABILITY}"), getName());
		if (cooldowns.containsKey(player)) message = message.replaceAll(Pattern.quote("{COOLDOWN}"), cooldowns.get(player) + "");
		player.sendMessage(Utils.colour(prefix + message));
	}
	
	public abstract Player check(Event event);
	
	protected abstract void onAbilityUse(ChampionsPlayer player);
	
	protected abstract void onCooldownEnd(ChampionsPlayer player);

	public final Class<? extends Ability> getAbilityClass() {
		return abilityClass;
	}
	
	public final Set<ChampionsPlayer> getPlayerCooldowns() {
		return cooldowns.keySet();
	}
	
	public final String getName() {
		return name;
	}
	
	public final int getCooldown() {
		return cooldown;
	}
}