package me.limeglass.champions.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.limeglass.champions.objects.ChampionsPlayer;

public class ChampionsScoreboard {

	private static Map<Integer, ScoreboardEntry> scores = new HashMap<Integer, ScoreboardEntry>();
	private final ChampionsPlayer player;
	private Scoreboard scoreboard;
	private String title;
	
	public ChampionsScoreboard(ChampionsPlayer player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.scoreboard.registerNewObjective("ChampionsBoard", "dummy", "Champions");
		this.scoreboard.getObjective("ChampionsBoard").setDisplaySlot(DisplaySlot.SIDEBAR);
		ScoreboardManager.addScoreboard(player, this);
	}
	
	public void setSlot(int slot, String value) {
		String prefix = null, name = null, suffix = null;
		if (value.length() > 48) {
			value = value.substring(0, 47);
		}
		if (value.length() <= 16) {
			name = value;
		} else if (value.length() <= 32) {
			name = value.substring(0, 16);
			suffix = value.substring(16, value.length());
		} else {
			prefix = value.substring(0, 16);
			name = value.substring(16, 32);
			suffix = value.substring(32, value.length());
		}
		if (scores.containsKey(slot)) scores.get(slot).delete();
		Team team = scoreboard.getEntryTeam(name);
		if (team == null) {
			team = scoreboard.registerNewTeam(name);
		}
		team.addEntry(name);
		if (prefix != null) team.setPrefix(prefix);
		if (suffix != null) team.setSuffix(suffix);
		Score score = scoreboard.getObjective("ChampionsBoard").getScore(name);
		score.setScore(slot);
		scores.put(slot, new ScoreboardEntry(scoreboard, slot, score, team));
	}
	
	public void delete() {
		ScoreboardManager.removeScoreboard(this);
		for (Integer slot : scores.keySet()) {
			deleteSlot(slot);
		}
		scoreboard.getObjective("ChampionsBoard").unregister();
	}
	
	public void updateScore(int slot, String value) {
		if (scores.containsKey(slot)) {
			deleteSlot(slot);
			setSlot(slot, value);
		}
	}
	
	public void deleteSlot(int slot) {
		if (!scores.containsKey(slot)) return;
		ScoreboardEntry entry = scores.get(slot);
		entry.delete();
		scores.remove(slot);
	}
	
	public void setTitle(String title) {
		this.title = title;
		scoreboard.getObjective("ChampionsBoard").setDisplayName(title);
	}
	
	public Map<Integer, ScoreboardEntry> getScores() {
		return scores;
	}
	
	public ChampionsPlayer getPlayer() {
		return player;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public String getTitle() {
		return title;
	}
}
