package me.limeglass.champions.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.limeglass.champions.abstracts.Menu;
import me.limeglass.champions.utils.Utils;

public class InventoryManager {

	private static Map<String, Menu> menus = new HashMap<>();
	
	public static void addMenu(Menu menu) {
		if (!menus.containsKey(menu.getName()))
			menus.put(menu.getName(), menu);
	}
	
	public static void removeMenu(String name) {
		if (menus.containsKey(name))
			menus.remove(name);
	}
	
	public static Map<String, Menu> getMenus() {
		return menus;
	}
	
	public static Boolean isMenu(Inventory inventory) {
		for (Menu menu : menus.values()) {
			if (inventory.getName().equals(Utils.cc(menu.getHeader())) && InventoryType.CHEST == inventory.getType())
				return true;
		}
		return false;
	}
	
	public static Boolean isMenu(Inventory inventory, String name) {
		for (Menu menu : menus.values()) {
			if (inventory.getName().equals(Utils.cc(menu.getHeader())) && InventoryType.CHEST == inventory.getType() && menu.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static Menu getMenu(Inventory inventory) {
		if (isMenu(inventory)) {
			for (Menu menu : menus.values()) {
				if (inventory.getName().equals(Utils.cc(menu.getHeader())) && InventoryType.CHEST == inventory.getType())
					return menu;
			}
		}
		return null;
	}
	
	public static Inventory get(String name) {
		if (menus.containsKey(name)) {
			return menus.get(name).build();
		}
		return null;
	}

}
