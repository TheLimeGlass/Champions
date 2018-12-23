package me.limeglass.champions.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.abstracts.Menu;
import me.limeglass.champions.managers.KitManager;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.objects.Kit;
import me.limeglass.champions.objects.ChampionsPlayer;
import me.limeglass.champions.utils.Utils;

public class KitsMenu extends Menu {
	
	private final static FileConfiguration kits = Champions.getConfiguration("kits");
	
	static {
		for (String name : kits.getConfigurationSection("Kits").getKeys(false)) {
			new Kit(name);
		}
		registerMenu(new KitsMenu());
	}
	
	public KitsMenu() {
		super("kits", 6, menu.getString("General.kitsHeader", "&4Invalid header"));
	}

	@Override
	protected Map<Integer, ItemStack> getItems() {
		Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		int i = 0;
		for (String kit : kits.getConfigurationSection("Kits").getKeys(false)) {
			items.put(section[i], Utils.getItem(kits, "Kits." + kit + ".inventory"));
			i++;
			if (i > kits.getConfigurationSection("Kits").getKeys(false).size())
				break;
		}
		return items;
	}
	
	private Kit getKit(ItemStack clickedItem, int slot) {
		for (String kit : kits.getConfigurationSection("Kits").getKeys(false)) {
			if (clickedItem.getType().toString().equals(kits.getString("Kits." + kit + ".inventory.material"))) {
				if (clickedItem.isSimilar(getItems().get(slot))) {
					for (Kit kitTest : KitManager.getRegisteredKits()) {
						if (kit.equals(kitTest.getName()))
							return kitTest;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != getBackgroundItem() && getItems().values().contains(event.getCurrentItem())) {
			Optional<ChampionsPlayer> optional = PlayerManager.getChampionsPlayer((Player)event.getWhoClicked());
			if (!optional.isPresent())
				return;
			ChampionsPlayer player = optional.get();
			Kit kit = getKit(event.getCurrentItem(), event.getSlot());
			sendPlaceholderMessage(player, "kitSelect", kit);
			player.applyKit(kit);
		}
	}
	
	private void sendPlaceholderMessage(ChampionsPlayer player, String node, Kit kit) {
		if (kit == null)
			return;
		String message = Champions.getConfiguration("messages").getString("prefix") + Champions.getConfiguration("messages").getString(node);
		message = message.replaceAll(Pattern.quote("{KIT}"), kit.getName());
		player.sendMessage(Utils.colour(message));
	}

}
