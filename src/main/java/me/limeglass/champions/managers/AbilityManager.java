package me.limeglass.champions.managers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import me.limeglass.champions.abstracts.Ability;


public class AbilityManager {

	private static Set<Ability> abilities = new HashSet<>();
	
	public static void addAbility(Ability ability) {
		if (!abilities.contains(ability))
			abilities.add(ability);
	}
	
	public static void removeAbility(Ability ability) {
		if (abilities.contains(ability))
			abilities.remove(ability);
	}
	
	public static Set<Ability> getRegisteredAbilities() {
		return abilities;
	}
	
	public static Optional<Ability> getAbility(String name) {
		return abilities.parallelStream()
				.filter(ability -> ability.getName().equals(name))
				.findFirst();
	}
	
	public static Boolean isAbilityRegistered(String ability) {
		return getAbility(ability).isPresent();
	}

}
