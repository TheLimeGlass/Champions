package me.limeglass.champions.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.limeglass.champions.Champions;

public class Storage {
	
	private static File file  = new File(Champions.getInstance().getDataFolder(), "storage.csv");
	private static final String NEW_LINE = "\n";
	private static final String DELIMITER = ": ";
	private static FileWriter writer = null;
	private static GsonBuilder gsonBuilder = new GsonBuilder();
	private static Gson gson;
	public static TreeMap<String, Object> data = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
	private static Boolean loadingHash = false;
	private static BukkitTask task;

	//TODO Add a read lock to the file.
	
	public static FileWriter getWriter() {
		return writer;
	}
	
	public static Object get(String ID) {
		return data.get(ID);
	}
	
	public static Integer getSize() {
		return data.size();
	}
	
	public static void save(Boolean running, Boolean reboot) {
		//running shuts down the stream
		//reboot is for an interval saver
		if (running) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Date date = new Date();
		File newFile = new File(Champions.getInstance().getDataFolder() + File.separator + "backups" + File.separator + date.toString().replaceAll(":", "-") + ".csv");
		try {
			Files.copy(file.toPath(), newFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (reboot) {
			load();
		}
	}
	
	public static void setup() {
		new File(Champions.getInstance().getDataFolder() + File.separator + "backups").mkdir();
		if (task != null) Bukkit.getScheduler().cancelTask(task.getTaskId());
		run();
		file = new File(Champions.getInstance().getDataFolder(), "storage.csv");
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		gson = gsonBuilder.create();
		if (!file.exists()) {
			try {
				writer = new FileWriter(file);
				writer.append(NEW_LINE);
				writer.append("# AussieMC flat file Kitpvp database.");
				writer.append(NEW_LINE);
				writer.append("# Please do not modify this file manually, thank you!");
				writer.append(NEW_LINE);
				writer.append(NEW_LINE);
				Champions.debugMessage("Successfully created CSV storage database!");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.flush();
				} catch (IOException e) {
					Champions.debugMessage("Error flushing data during setup!");
					e.printStackTrace();
				}
			}
		} else {
			load();
		}
	}
	
	private static void load() {
		String line = "";
		BufferedReader reader = null;
		try {
			ArrayList<String[]> data = new ArrayList<String[]>();
			reader = new BufferedReader(new FileReader(file));
			for (int i = 0; i < 4; i ++) {
				reader.readLine();
			}
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(DELIMITER, 2);
				data.add(values);
			}
			writer = new FileWriter(file);
			writer.append(NEW_LINE);
			writer.append("# AussieMC flat file Kitpvp database.");
			writer.append(NEW_LINE);
			writer.append("# Please do not modify this file manually, thank you!");
			writer.append(NEW_LINE);
			writer.append(NEW_LINE);
			for (String[] varaibleData : data) {
				write(varaibleData[0], gson.fromJson(varaibleData[1], Object.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				Champions.debugMessage("Error closing reader while loading!");
				e.printStackTrace();
			}
		}
	}
	
	private static void loadFromHash() {
		loadingHash = true;
		try {
			writer = new FileWriter(file);
			writer.append(NEW_LINE);
			writer.append("# AussieMC flat file Kitpvp database.");
			writer.append(NEW_LINE);
			writer.append("# Please do not modify this file manually, thank you!");
			writer.append(NEW_LINE);
			writer.append(NEW_LINE);
			if (!data.isEmpty()) {
				for (String ID : data.keySet()) {
					write(ID, data.get(ID));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
			} catch (IOException e) {
				Champions.debugMessage("Error flushing writer while loading from hash!");
				e.printStackTrace();
			}
		}
		loadingHash = false;
	}
	
	public static void remove(String ID) {
		if (data.containsKey(ID)) {
			if (!loadingHash) {
				try {
					getWriter().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				data.remove(ID);
				loadFromHash();
			}
		}
	}
	
	public static void write(String ID, Object value) {
		if (ID == null || value == null) return;
		if (data.containsKey(ID)) {
			if (!loadingHash) {
				try {
					getWriter().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				data.remove(ID);
				loadFromHash();
			}
		}
		data.put(ID, value);
		try {
			writer.append(ID);
			writer.append(DELIMITER);
			writer.append(gson.toJson(value));
			writer.append(NEW_LINE);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
			} catch (IOException e) {
				Champions.debugMessage("Error flushing data while writing!");
				e.printStackTrace();
			}
		}
	}
	
	public static void run() {
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(Champions.getInstance(), new Runnable() {
			@Override
			public void run() {
				Champions.consoleMessage("Data has been saved!");
				save(true, true);
			}
		}, 1, (20 * 60) * Champions.getConfiguration("config").getInt("Data.backup-timer", 60)); //60 minutes by default
	}
}
