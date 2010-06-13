/*
  Copyright (C) 2005 University Of New South Wales.
  
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.robocup.gamecontroller.rules;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.robocup.gamecontroller.Constants;

public abstract class RuleBook {

	private LinkedList<Short> penalties;
	private Hashtable<Short, String> penaltyNames;
	private Hashtable<Short, Short> penaltyTimes;

	private String applicationTitle;
	private String configDirectory;
	private byte numPlayers = 3;

	private String[] teamColorNames = new String[Constants.NUM_TEAMS];
	private Color[] teamColorValues = new Color[Constants.NUM_TEAMS];

	private String[] goalColorNames = new String[Constants.NUM_TEAMS];
	private boolean changeGoals = false;

	private int timeSeconds = 600;
	private int readySeconds = 45;
	private int timeOutSeconds = 300;
	private int penaltyShootSeconds = 60;
	private int halfTimeSeconds = 600;

	// should be adjusted according to the number of robots in each team
	private final int[] pushingThresholds = { 3, 5, 7 };

	private int[] pushingCounter = new int[Constants.NUM_TEAMS];
	private int[][] pushingPenaltySeconds = new int[Constants.NUM_TEAMS][Constants.NUM_PLAYERS_SPL];
	private boolean usePushingCounter = false;

	public RuleBook() {
		penalties = new LinkedList<Short>();
		penaltyNames = new Hashtable<Short, String>();
		penaltyTimes = new Hashtable<Short, Short>();
		registerPenalty(Constants.PENALTY_MANUAL, "Manual", 0);

		setup();
	}

	protected abstract void setup();

	protected void setApplicationTitle(String title) {
		applicationTitle = title;
	}

	public String getApplicationTitle() {
		return applicationTitle;
	}

	protected void setConfigDirectory(String directory) {
		configDirectory = directory;
	}

	public String getConfigDirectory() {
		return configDirectory;
	}

	protected void setNumPlayers(int numPlayers) {
		this.numPlayers = (byte) numPlayers;
	}

	public byte getNumPlayers() {
		return numPlayers;
	}

	protected void setupTeams(String team0Name, String team1Name, java.awt.Color team0Color, java.awt.Color team1Color) {
		teamColorNames[0] = team0Name;
		teamColorNames[1] = team1Name;

		teamColorValues[0] = team0Color;
		teamColorValues[1] = team1Color;
	}

	public String getTeamName(int team) {
		return teamColorNames[team];
	}

	public Color getTeamColor(int team) {
		return teamColorValues[team];
	}

	protected void setupGoals(String goal0Name, String goal1Name, boolean changeGoals) {
		goalColorNames[0] = goal0Name;
		goalColorNames[1] = goal1Name;
		this.changeGoals = changeGoals;
	}

	public String getGoalName(int goal) {
		return goalColorNames[goal];
	}

	public boolean getChangeGoals() {
		return changeGoals;
	}

	protected void registerPenalty(int code, String name, int time) {
		short codeShort = (short) code;
		penalties.addLast(codeShort);
		penaltyNames.put(codeShort, name);
		penaltyTimes.put(codeShort, (short) time);
	}

	public Iterator getPenalties() {
		return penalties.listIterator();
	}

	public String getPenaltyName(short code) {
		return (String) penaltyNames.get(code);
	}

	public short getPenaltyTime(short code) {
		return ((Short) penaltyTimes.get(code)).shortValue();
	}

	public void setTimeSeconds(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	public int getTimeSeconds() {
		return timeSeconds;
	}

	public void setReadySeconds(int readySeconds) {
		this.readySeconds = readySeconds;
	}

	public int getReadySeconds() {
		return readySeconds;
	}

	public void setTimeOutSeconds(int timeOutSeconds) {
		this.timeOutSeconds = timeOutSeconds;
	}

	public int getTimeOutSeconds() {
		return timeOutSeconds;
	}

	public void setPenaltyShootSeconds(int penaltyShootSeconds) {
		this.penaltyShootSeconds = penaltyShootSeconds;
	}

	public int getPenaltyShootSeconds() {
		return penaltyShootSeconds;
	}

	public void setHalfTimeSeconds(int halfTimeSeconds) {
		this.halfTimeSeconds = halfTimeSeconds;
	}

	public int getHalfTimeSeconds() {
		return halfTimeSeconds;
	}

	public void setUsePushingCounter(boolean usePushingCounter) {
		this.usePushingCounter = usePushingCounter;
	}

	public boolean getUsePushingCounter() {
		return this.usePushingCounter;
	}

	public void resetPushingCounter() {
		for (int i = 0; i < Constants.NUM_TEAMS; i++) {
			this.pushingCounter[i] = 0;
		}
	}

	public void incrementPushingCounter(byte team) {
		// this.pushingCounter[team] = (this.pushingCounter[team] + 1) % maxNumPushing;
		this.pushingCounter[team]++;
	}

	public int getPushingCounter(byte team) {
		return this.pushingCounter[team];
	}

	public int getPushingThreshold(int numPushing) {
		return this.pushingThresholds[numPushing];
	}

}
