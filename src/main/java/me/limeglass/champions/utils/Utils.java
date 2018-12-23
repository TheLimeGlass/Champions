package me.limeglass.champions.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.GameManager;
import me.limeglass.champions.objects.ChampionsGame;
import me.limeglass.champions.objects.ChampionsGame.ChampionsMode;

public class Utils {
	
	public static boolean compareArrays(String[] arg1, String[] arg2) {
		if (arg1.length != arg2.length) return false;
		Arrays.sort(arg1);
		Arrays.sort(arg2);
		return Arrays.equals(arg1, arg2);
	}
	
	public static Boolean isEmpty(Inventory inventory) {
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	public static Boolean isEnum(Class<?> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
			return true;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getEnum(Class<?> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			return (T) method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			Champions.consoleMessage("&cUnknown type " + object + " in " + clazz.getName());
			return null;
		}
	}
	
	public static Set<String> getEnums(Class<?> clazz) {
		try {
			final Method method = clazz.getMethod("values");
			method.setAccessible(true);
			Set<String> enums = new HashSet<String>();
			for (Object object : (Object[]) method.invoke(clazz)) {
				enums.add(object.toString());
			}
			return enums;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			return null;
		}
	}
	
	public static Class<?> getArrayClass(Class<?> parameter) {
		return Array.newInstance(parameter, 0).getClass();
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItem(FileConfiguration configuration, String node) {
		String name = configuration.getString(node + ".name");
		String[] lores = null;
		if (configuration.isSet(node + ".lores")) lores = configuration.getStringList(node + ".lores").toArray(new String[configuration.getStringList(node + ".lores").size()]);
		String material = configuration.getString(node + ".material", "AIR");
		List<ItemFlag> flags = null;
		if (configuration.isSet(node + ".itemflags")) {
			for (String flag : configuration.getStringList(node + ".itemflags")) {
				try {
					flags.add(Utils.getEnum(ItemFlag.class, flag));
				} catch (NullPointerException e) {}
			}
		}
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		if (configuration.isSet(node + ".enchantments")) {
			for (String enchantment : configuration.getStringList(node + ".enchantments")) {
				try {
					if (!enchantment.contains(":")) {
						enchantments.put(Enchantment.getByName(enchantment), 1);
					} else {
						String[] handle = enchantment.split(":");
						int slot = Integer.parseInt(handle[1]);
						if (slot != -1) enchantments.put(Enchantment.getByName(handle[0]), slot);
						else enchantments.put(Enchantment.getByName(handle[0]), 1);
					}
				} catch (NullPointerException e) {}
			}
		}
		short colour = (short) configuration.getInt(node + ".colour", -1);
		//TODO add more option from the ItemBuilder if needed.
		if (colour == -1) return new ItemBuilder(material, 1).flags(flags).displayname(name).lore(lores).build();
		else return new ItemBuilder(material, 1).displayname(name).lore(lores).durability(colour).build();
	}
	
	public static String[] colour(String... messages) {
		for (int message = 0; message < messages.length; message++) {
			for (int i = 1; i <= 3; i++) {
				messages[message] = messages[message].replaceAll(Pattern.quote("{" + i + "}"), colour(i));
			}
		}
		return messages;
	}
	
	public static String colour(int index) {
		return (index == 1) ? Champions.getConfiguration("messages").getString("colours.main") : (index == 2) ? Champions.getConfiguration("messages").getString("colours.secondary") : Champions.getConfiguration("messages").getString("colours.third");
	}

	public static String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String[] getMessage(Boolean prefix, String node, Player player) {
		List<String> values = new ArrayList<String>();
		FileConfiguration configuration = Champions.getConfiguration("messages");
		if (configuration.isList(node)) values = configuration.getStringList(node);
		else values.add(configuration.getString(node));
		if (prefix) {
			values.add(0, configuration.getString("prefix") + values.get(0));
			values.remove(1);
		}
		if (values == null) return null;
		List<String> toReturn = new ArrayList<String>();
		for (String value : values) {
			value = value.replaceAll(Pattern.quote("{TEAM1}"), Champions.getConfiguration("config").getString("Teams.colour1"));
			value = value.replaceAll(Pattern.quote("{TEAM2}"), Champions.getConfiguration("config").getString("Teams.colour2"));
			value = value.replaceAll(Pattern.quote("{PLAYER}"), player.getName());
			value = value.replaceAll(Pattern.quote("{PLAYER_DISPLAY_NAME}"), player.getDisplayName());
			if (GameManager.tempgames.get(player) != null) {
				ChampionsGame game = GameManager.tempgames.get(player);
				value = value.replaceAll(Pattern.quote("{GAME_NAME}"), game.getName());
				value = value.replaceAll(Pattern.quote("{GAME_MODE}"), game.getMode().toString());
				value = value.replaceAll(Pattern.quote("{GAME_HAS_CAPTURE}"), game.hasCaptures() + "");
			}
			value = value.replaceAll(Pattern.quote("{MODES}"), getEnums(ChampionsMode.class).toString());
			value = value.replaceAll(Pattern.quote("{KITS}"), Champions.getConfiguration("kits").getConfigurationSection("Kits").getKeys(false).toString());
			toReturn.add(cc(colour(value)[0]));
		}
		return toReturn.toArray(new String[toReturn.size()]);
	}
}
