package me.limeglass.champions.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;

import me.limeglass.champions.Champions;

public class Schematics {

	public static void paste() {
		File directory = new File(Champions.getInstance().getDataFolder() + File.separator + "schematics");
		if (!directory.exists())
			directory.mkdir();
		if (directory.listFiles() == null || directory.listFiles().length <= 0)
			return;
		File[] schematics = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".schematic");
			}
		});
		int random = new Random().nextInt(schematics.length);
		File schematic = schematics[random];
		if (Bukkit.getWorld("Champions") != null) {
			File kitpvp = Bukkit.getWorld("Champions").getWorldFolder();
			Bukkit.unloadWorld("Champions", false);
			try {
				Files.deleteIfExists(kitpvp.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Bukkit.createWorld(new WorldCreator("Champions").generator("EmptyWorldGenerator"));
		ClipboardFormat format = ClipboardFormats.findByFile(schematic);
		try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
			BlockArrayClipboard clipboard = (BlockArrayClipboard) reader.read();
			EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(clipboard.getRegion().getWorld(), -1);
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(session)
					.to(clipboard.getOrigin())
					.ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
	}

}
