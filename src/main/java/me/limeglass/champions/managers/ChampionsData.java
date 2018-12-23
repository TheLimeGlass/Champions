package me.limeglass.champions.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.limeglass.champions.Champions;

public class ChampionsData {

	private static final FileConfiguration data = Champions.getConfiguration("data");
	private static Location spawn;
	
	public static Location getSpawn() {
		if (spawn == null) {
			int x = 0, y = 60, z = 0, pitch = 90, yaw = 90;
			World world = null;
			if (data.isSet("spawn")) {
				x = data.getInt("spawn.x", 0);
				y = data.getInt("spawn.y", 60);
				z = data.getInt("spawn.z", 0);
				pitch = data.getInt("spawn.pitch", 90);
				yaw = data.getInt("spawn.yaw", 90);
				world = Bukkit.getWorld(data.getString("spawn.world", "world"));
			}
			if (world != null) {
				spawn = new Location(world, x, y, z, pitch, yaw);
			}
		}
		return spawn;
	}
	
	public static void setSpawn(Location location) {
		data.set("spawn.x", location.getX());
		data.set("spawn.y", location.getY());
		data.set("spawn.z", location.getZ());
		data.set("spawn.pitch", location.getPitch());
		data.set("spawn.yaw", location.getYaw());
		data.set("spawn.world", location.getWorld().getName());
		Champions.save("data");
		spawn = location;
	}
}
