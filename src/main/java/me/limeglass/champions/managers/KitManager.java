package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Set;

import me.limeglass.champions.objects.Kit;

public class KitManager {

	private static Set<Kit> kits = new HashSet<Kit>();
	
	public static void addKit(Kit kit) {
		if (!kits.contains(kit)) kits.add(kit);
	}
	
	public static void removeKit(Kit kit) {
		if (kits.contains(kit)) kits.remove(kit);
	}
	
	public static Set<Kit> getRegisteredKits() {
		return kits;
	}
	
	public static void clearKits() {
		kits.clear();
	}
	
	public static Kit getKit(String name) {
		if (getRegisteredKits() == null) return null;
		for (Kit kit : getRegisteredKits()) {
			if (kit.getName() == name) return kit;
		}
		return null;
	}
	
	public static Boolean isAbilityRegistered(String kit) {
		return getKit(kit) != null;
	}
}
