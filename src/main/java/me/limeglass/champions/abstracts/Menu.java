package me.limeglass.champions.abstracts;

import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.utils.ItemBuilder;
import me.limeglass.champions.utils.Utils;

public abstract class Menu {

	protected final static Integer[] section = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
	protected final static FileConfiguration menu = Champions.getConfiguration("menus");
	private final Class<? extends Menu> menuClass;
	private final String header;
	private final String name;
	private final int size;
	private Boolean background = true;
	
	public Menu(String name, int size, String header) {
		this.menuClass = getClass();
		this.header = header;
		this.name = name;
		this.size = 9 * size;
		InventoryManager.addMenu(name, this);
	}
	
	public Inventory build() {
		Inventory inventory = Bukkit.createInventory(null, size, Utils.cc(header));
		if (background) for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, getBackgroundItem());
		for (Entry<Integer, ItemStack> entry : getItems().entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue());
		}
		return inventory;
	}
	
	protected Boolean isNull() {
		return getItems() == null;
	}
	
	protected static void registerMenu(Menu menu) {
		Champions.getRegistrar().registerMenu(menu);
	}
	
	protected ItemStack getBackgroundItem() {
		FileConfiguration menu = Champions.getConfiguration("menus");
		short colour = (short) menu.getInt("General.backgroundColour", 1);
		String material = menu.getString("General.background", "STAINED_GLASS_PANE");
		if (colour == -1) return new ItemBuilder(material, 1).build();
		else return new ItemBuilder(material, 1).durability(colour).build();
	}
	
	protected abstract Map<Integer, ItemStack> getItems();
	
	public abstract void onInventoryClick(InventoryClickEvent event);

	public final Class<? extends Menu> getMenuClass() {
		return menuClass;
	}
	
	public final String getName() {
		return name;
	}
	
	public final int getInventorySize() {
		return size;
	}

	public String getHeader() {
		return header;
	}

	public Boolean hasBackground() {
		return background;
	}

	public void setBackground(Boolean hasBackground) {
		this.background = hasBackground;
	}
}