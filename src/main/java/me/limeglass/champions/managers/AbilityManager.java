package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Set;

import me.limeglass.champions.abstracts.Ability;


public class AbilityManager {

	private static Set<Ability> abilities = new HashSet<Ability>();
	
	public static void addAbility(Ability ability) {
		if (!abilities.contains(ability)) abilities.add(ability);
	}
	
	public static void removeAbility(Ability ability) {
		if (abilities.contains(ability)) abilities.remove(ability);
	}
	
	public static Set<Ability> getRegisteredAbilitys() {
		return abilities;
	}
	
	public static void clearAbilitys() {
		abilities.clear();
	}
	
	public static Ability getAbility(String name) {
		if (getRegisteredAbilitys() == null) return null;
		for (Ability ability : getRegisteredAbilitys()) {
			if (ability.getName() == name) return ability;
		}
		return null;
	}
	
	public static Boolean isAbilityRegistered(String ability) {
		return getAbility(ability) != null;
	}
}
