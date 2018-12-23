package me.limeglass.champions.abstracts;

import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.ChampionsAddon;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.utils.ItemBuilder;
import me.limeglass.champions.utils.Utils;

public abstract class Menu {

	protected final static Integer[] section = new Integer[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
	protected final static FileConfiguration menu = Champions.getConfiguration("menus");
	private final Class<? extends Menu> menuClass;
	private static ChampionsAddon addon;
	private final String header, name;
	private boolean background = true;
	private final int size;
	
	public Menu(String name, int size, String header) {
		Optional<ChampionsAddon> registrar = Champions.getCurrentRegistrar();
		if (registrar.isPresent())
			addon = registrar.get();
		this.menuClass = getClass();
		this.size = 9 * size;
		this.header = header;
		this.name = name;
		InventoryManager.addMenu(this);
	}
	
	public Inventory build() {
		Inventory inventory = Bukkit.createInventory(null, size, Utils.cc(header));
		if (background)
			for (int i = 0; i < inventory.getSize(); i++) 
				inventory.setItem(i, getBackgroundItem());
		getItems().entrySet().forEach(entry -> inventory.setItem(entry.getKey(), entry.getValue()));
		return inventory;
	}
	
	protected static void registerMenu(Menu menu) {
		addon.registerMenu(menu);
	}
	
	protected ItemStack getBackgroundItem() {
		short colour = (short) menu.getInt("General.backgroundColour", 1);
		String material = menu.getString("General.background", "STAINED_GLASS_PANE");
		ItemBuilder builder = new ItemBuilder(material, 1);
		if (colour == -1)
			return builder.build();
		else 
			return builder.durability(colour).build();
	}
	
	/**
	 * @return Map of the Inventory. The integers are the slots, and ItemStacks are for the items of the menus.
	 */
	protected abstract Map<Integer, ItemStack> getItems();
	
	/**
	 * Called when the Menu is involved in an InventoryClickEvent event.
	 * @param event A InventoryClickEvent involved with this Menu.
	 */
	public abstract void onInventoryClick(InventoryClickEvent event);

	public final Class<? extends Menu> getMenuClass() {
		return menuClass;
	}
	
	public final int getInventorySize() {
		return size;
	}
	
	/**
	 * Should be called before building the inventory.
	 * @param background The boolean if this Menu should have a background or not.
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}

	public Boolean hasBackground() {
		return background;
	}
	
	public final String getName() {
		return name;
	}
	
	public String getHeader() {
		return header;
	}

}
