package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import me.limeglass.champions.objects.Kit;

public class KitManager {

	private static Set<Kit> kits = new HashSet<>();
	
	public static void addKit(Kit kit) {
		if (!kits.contains(kit))
			kits.add(kit);
	}
	
	public static void removeKit(Kit kit) {
		if (kits.contains(kit))
			kits.remove(kit);
	}
	
	public static Set<Kit> getRegisteredKits() {
		return kits;
	}
	
	public static Optional<Kit> getKit(String name) {
		return kits.parallelStream()
				.filter(kit -> kit.getName().equals(name))
				.findFirst();
	}
	
	public static Boolean isAbilityRegistered(String kit) {
		return getKit(kit).isPresent();
	}

}
