package me.limeglass.tickets.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;

import me.limeglass.tickets.BungeeTickets;
import me.limeglass.tickets.objects.TicketArgument;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ArgumentManager {

	private static Map<String, TicketArgument> arguments = new HashMap<String, TicketArgument>();
	
	public static void addArgument(TicketArgument ticket) {
		Set<String> args = ticket.getArguments();
		BungeeTickets.debugMessage("Added ticket argument(s): " + args.toString() + " (" + ticket.getClass().getSimpleName() + ")");
		for (String argument : args) {
			if (!hasArgument(argument)) arguments.put(argument, ticket);
		}
	}
	
	public static Boolean hasArgument(String... args) {
		for (String argumnet : args) {
			if (arguments.containsKey(argumnet)) return true;
		}
		return false;
	}
	
	public static void setup(BungeeTickets instance) {
		try {
			JarFile jar = new JarFile(instance.getFile());
			Set<Class<?>> classes = getClasses(jar, "me.limeglass.tickets.command.arguments");
			Reflection.initialize(classes.toArray(new Class[classes.size()]));
		} catch (IOException e) {}
	}
	
	public static Set<Class<?>> getClasses(JarFile jar, String... packages) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName().replace("/", ".");
				String className = name.substring(0, name.length() - 6);
				className = className.replace('/', '.');
				for (String packageName : packages) {
					if (name.startsWith(packageName) && name.endsWith(".class")) {
						classes.add(Class.forName(className));
					}
				}
			}
			jar.close();
		} catch (IOException | ClassNotFoundException e1) {}
		return classes;
	}
	
	public static void execute(ProxiedPlayer player, String[] args) {
		if (hasArgument(args[0])) {
			TicketArgument argument = arguments.get(args[0]);
			Configuration configuration = BungeeTickets.getInstance().getConfig();
			String permission = configuration.getString(argument.getNode() + "permission");
			if (permission != null && !player.hasPermission(permission)) {
				if (configuration.getBoolean("UnknownCommand", false)) {
					player.sendMessage(new TextComponent("Unknown command. Type \"/help\" for help."));
				} else {
					String message = configuration.getString("Messages.no-permission", "{PREFIX}&cYou do not have the correct permissions to use this command.");
					player.sendMessage(new TextComponent(Placeholder.parse(message, player)));
				}
				return;
			}
			if (!args[0].equalsIgnoreCase("create") && !configuration.getBoolean(argument.getNode() + "enabled")) {
				String message = configuration.getString("Messages.not-enabled", "{PREFIX}&cThis argument is currently disabled.");
				player.sendMessage(new TextComponent(Placeholder.parse(message, player)));
				return;
			}
			ArrayList<String> arguments = Lists.newArrayList(args);
			arguments.remove(0);
			boolean success = argument.execute(player, arguments);
			if (!success) player.sendMessage(new TextComponent(BungeeTickets.getPrefix() + argument.getUsage()));
		} else {
			ArrayList<String> arguments = Lists.newArrayList(args);
			arguments.add(0, "create");
			execute(player, arguments.toArray(new String[arguments.size()]));
		}
	}

}
