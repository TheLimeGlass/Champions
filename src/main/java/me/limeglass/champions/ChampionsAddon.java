package me.limeglass.champions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import me.limeglass.champions.abstracts.Ability;
import me.limeglass.champions.abstracts.Menu;
import me.limeglass.champions.listeners.EventHandler;

public class ChampionsAddon {
	
	private Set<Ability> abilities = new HashSet<>();
	private Set<Class<?>> classes = new HashSet<>();
	private Set<Menu> menus = new HashSet<Menu>();
	private JavaPlugin plugin;
	private boolean current;
	private JarFile jar;
	
	public ChampionsAddon(JavaPlugin plugin) {
		this.plugin = plugin;
		this.jar = getJar();
		Champions.registerAddon(this);
	}
	
	@SuppressWarnings("unchecked")
	public void registerAbility(Ability ability, Class<? extends Event>... events) {
		EventHandler.register(ability, events);
		abilities.add(ability);
		Champions.debugMessage("Registered Ability " + ability.getName());
	}
	
	public void registerMenu(Menu menu) {
		menus.add(menu);
		Champions.debugMessage("Registered Menu " + menu.getName());
	}
	
	private JarFile getJar() {
		try {
			Method method = JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			File file = (File) method.invoke(plugin);
			return new JarFile(file);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadClasses(String basePackage, String... subPackages) {
		if (subPackages == null)
			return;
		for (int i = 0; i < subPackages.length; i++) {
			subPackages[i] = subPackages[i].replace('.', '/') + "/";
		}
		basePackage = basePackage.replace('.', '/') + "/";
		this.current = true;
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName();
				if (name.startsWith(basePackage) && name.endsWith(".class")) {
					for (String sub : subPackages) {
						if (name.startsWith(sub, basePackage.length())) {
							String clazz = name.replace("/", ".").substring(0, name.length() - 6);
							classes.add(Class.forName(clazz, true, plugin.getClass().getClassLoader()));
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				jar.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		this.current = false;
		Champions.debugMessage("Loaded a total of " + menus.size() + " Menus and " + abilities.size() + " Abilities for the plugin " + plugin.getName());
	}

	public boolean isCurrentlyLoading() {
		return current;
	}

}
