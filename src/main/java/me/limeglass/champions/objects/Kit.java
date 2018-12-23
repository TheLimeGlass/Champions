package me.limeglass.champions.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.abstracts.Ability;
import me.limeglass.champions.listeners.EventHandler;
import me.limeglass.champions.managers.AbilityManager;
import me.limeglass.champions.managers.KitManager;
import me.limeglass.champions.utils.Utils;

public class Kit {

	private final FileConfiguration kits = Champions.getConfiguration("kits");
	private final Boolean hasAbilities, hasItems, hasHelmet, hasChestplate, hasLeggings, hasBoots;
	private final String name;
	private String node;
	private double health;
	
	public Kit(String name) {
		this.node = "Kits." + name + ".kit";
		this.name = name;
		this.hasHelmet = kits.isSet(node + ".helmet");
		this.hasChestplate = kits.isSet(node + ".chestplate");
		this.hasLeggings = kits.isSet(node + ".leggings");
		this.hasBoots = kits.isSet(node + ".boots");
		this.health = kits.getDouble(node + ".health", 20.0);
		this.hasItems = (kits.isSet(node + ".items") && kits.getStringList(node + ".items") != null);
		this.hasAbilities = (kits.isSet(node + ".abilities") && kits.getStringList(node + ".abilities") != null);
		KitManager.addKit(this);
	}
	
	public void applyKit(ChampionsPlayer kitpvpPlayer) {
		Player player = kitpvpPlayer.getPlayer();
		player.getInventory().clear();
		if (hasHelmet != null) player.getEquipment().setHelmet(Utils.getItem(kits, node + ".helmet"));
		if (hasChestplate != null) player.getEquipment().setChestplate(Utils.getItem(kits, node + ".chestplate"));
		if (hasLeggings != null) player.getEquipment().setLeggings(Utils.getItem(kits, node + ".leggings"));
		if (hasBoots != null) player.getEquipment().setBoots(Utils.getItem(kits, node + ".boots"));
		for (Entry<Integer, ItemStack> entry : getItems().entrySet()) {
			if (entry.getValue() != null) player.getInventory().setItem(entry.getKey(), entry.getValue());
		}
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
		kitpvpPlayer.setKit(this);
		kitpvpPlayer.playSound("sounds.kitSelect");
		player.closeInventory();
	}

	public Map<Integer, ItemStack> getItems() {
		Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		for (String slot : kits.getConfigurationSection(node + ".items").getKeys(false)) {
			if (Integer.parseInt(slot) != -1) items.put(Integer.parseInt(slot), Utils.getItem(kits, node + ".items." + slot));
		}
		return items;
	}
	
	public void executeAbilities(ChampionsPlayer player) {
		if (hasAbilities && getAbilities() != null) {
			for (Ability ability : getAbilities()) {
				ability.onAbilityExecute(player);
			}
		}
	}
	
	public void executeAbilities(ChampionsPlayer player, String... abilities) {
		if (hasAbilities && getAbilities() != null) {
			for (String ability : abilities) {
				Optional<Ability> optional = AbilityManager.getAbility(ability);
				if (optional.isPresent())
					optional.get().onAbilityExecute(player);
			}
		}
	}
	
	public Set<Ability> getAbilities() {
		if (getConfigurationAbilities() == null) return null;
		Set<Ability> abilities = new HashSet<Ability>();
		for (Ability ability : EventHandler.getEventAbilities().keySet()) {
			if (getConfigurationAbilities().contains(ability.getName())) {
				abilities.add(ability);
			}
		}
		return abilities;
	}
	
	public List<String> getConfigurationAbilities() {
		return (hasAbilities) ? kits.getStringList(node + ".abilities") : null;
	}

	public String getName() {
		return name;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Boolean hasAbilitites() {
		return hasAbilities;
	}

	public Boolean hasChestplate() {
		return hasChestplate;
	}

	public Boolean hasLeggings() {
		return hasLeggings;
	}

	public Boolean hasBoots() {
		return hasBoots;
	}

	public Boolean getHasItems() {
		return hasItems;
	}

}
