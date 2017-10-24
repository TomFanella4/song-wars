package com.songwars.api.utilities;

public class Matchup {

	private int round;
	private int pos1;
	private int pos2;
	
	public Matchup(int round, int pos1) {
		this.round = round;
		this.pos1 = pos1;
		this.pos2 = Utilities.getOpponentsPosition(pos1);
	}

	public int getRound() {
		return round;
	}

	public int getPos1() {
		return pos1;
	}

	public int getPos2() {
		return pos2;
	}
	
}
