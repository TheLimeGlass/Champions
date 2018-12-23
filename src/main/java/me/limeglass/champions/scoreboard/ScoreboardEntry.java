package me.limeglass.champions.scoreboard;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardEntry {

	private Scoreboard scoreboard;
	private Integer slot;
	private Score score;
	private Team team;
	
	public ScoreboardEntry(Scoreboard scoreboard, Integer slot, Score score, Team team) {
		this.scoreboard = scoreboard;
		this.score = score;
		this.slot = slot;
		this.team = team;
	}
	
	public void delete() {
		scoreboard.resetScores(score.getEntry());
		Team team = scoreboard.getTeam(score.getEntry());
		if (team != null) scoreboard.getTeam(score.getEntry()).unregister();
	}
	
	public Score getScore() {
		return this.score;
	}

	public Integer getSlot() {
		return this.slot;
	}
	
	public Team getTeam() {
		return this.team;
	}
}
