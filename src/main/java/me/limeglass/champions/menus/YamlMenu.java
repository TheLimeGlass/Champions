package me.limeglass.champions.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.abstracts.Menu;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.utils.Utils;

public class YamlMenu extends Menu {

	static {
		for (String name : Champions.getConfiguration("menus").getConfigurationSection("Menus").getKeys(false)) {
			registerMenu(new YamlMenu(name));
		}
	}
	
	private final String node;
	private final Boolean hasFunctions;
	
	public YamlMenu(String name) {
		super(name, menu.getInt("Menus." + name + ".size", 6), menu.getString("Menus." + name + ".header", "&4Invalid header"));
		this.node = "Menus." + name;
		this.hasFunctions = (menu.isSet(node + ".functions") && menu.getStringList(node + ".functions") != null);
	}

	@Override
	protected Map<Integer, ItemStack> getItems() {
		Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		for (String slot : menu.getConfigurationSection(node + ".items").getKeys(false)) {
			items.put(Integer.parseInt(slot), Utils.getItem(menu, node + ".items." + slot));
		}
		return items;
	}
	
	public Boolean hasFunctions() {
		return hasFunctions;
	}
	
	public Set<String> getFunctions() {
		return (hasFunctions) ? menu.getConfigurationSection(node + ".functions").getKeys(false) : null;
	}
	
	public String getFunction(int slot) {
		for (String function : getFunctions()) {
			if (menu.getInt(node + ".functions." + function) == slot) return function;
		}
		return null;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if (hasFunctions && getFunction(event.getSlot()) != null && InventoryManager.get(getFunction(event.getSlot())) != null) {
			event.getWhoClicked().openInventory(InventoryManager.get(getFunction(event.getSlot())));
		}
	}
}