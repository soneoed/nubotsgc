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

package org.robocup.common.rules;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.robocup.common.Constants;

public abstract class RuleBook {

	private LinkedList<Short> penalties;
	private Hashtable<Short, String> penaltyNames;
	private Hashtable<Short, Short> penaltyTimes;
	private Hashtable<Short, Boolean> penaltyIsAddon;
	private Hashtable<Short, Boolean> penaltyStayOnStateChange;	
	private boolean penaltyTimeStayOnStateChange;	

	private String applicationTitle;
	private String configDirectory;
	private byte numPlayers = 3;

	private boolean dropBall = false;

	private String[] teamColorNames = new String[Constants.NUM_TEAMS];
	private Color[] teamColorValues = new Color[Constants.NUM_TEAMS];

	private String[] goalColorNames = new String[Constants.NUM_TEAMS];
	private boolean changeGoals = false;

	private int timeSeconds = 600;
	private int overtimeSeconds = 0;
	private int readySeconds = 45;
	private boolean automaticReady2Set = false;
	private int timeOutSeconds = 300;
	private int penaltyShootSeconds = 60;
	private int halfTimeSeconds = 600;
	private byte KickOffTeamColor = Constants.TEAM_BLUE;
	
	private boolean switchTeamColorBetweenHalfs = true;

	// should be adjusted according to the number of robots in each team
	private final int[] pushingThresholds = { 4, 6, 8 };

	private int[] pushingCounter = new int[Constants.NUM_TEAMS];
	private boolean usePushingCounter = false;

	public RuleBook() {
		penalties = new LinkedList<Short>();
		penaltyNames = new Hashtable<Short, String>();
		penaltyTimes = new Hashtable<Short, Short>();
		penaltyIsAddon = new Hashtable<Short, Boolean>();
		penaltyStayOnStateChange = new Hashtable<Short, Boolean>();
		penaltyTimeStayOnStateChange = false;		
		registerPenalty(Constants.PENALTY_MANUAL, "Manual (0s)", 0);

		setup();
	}

	protected abstract void setup();

	protected void setApplicationTitle(String title) {
		applicationTitle = title;
	}

	public String getApplicationTitle() {
		return applicationTitle;
	}

	public void setConfigDirectory(String directory) {
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

	public void setDropBall(boolean value) {
		this.dropBall = value;
	}

	public boolean getDropBall() {
		return dropBall;
	}

	protected void setupTeams(String team0Name, String team1Name, java.awt.Color team0Color, java.awt.Color team1Color) {
		teamColorNames[0] = team0Name;
		teamColorNames[1] = team1Name;

		teamColorValues[0] = team0Color;
		teamColorValues[1] = team1Color;
	}

	public String getTeamColorName(int team) {
		return teamColorNames[team];
	}

	public Color getTeamColor(int team) {
		return teamColorValues[team];
	}
	
	public void setKickOffTeamColor(byte color) {
		this.KickOffTeamColor = color;
	}
	
	public byte getKickOffTeamColor() {
		return this.KickOffTeamColor;
	}	

	protected void setupGoals(String goal0Name, String goal1Name, boolean changeGoals) {
		goalColorNames[0] = goal0Name;
		goalColorNames[1] = goal1Name;
		this.changeGoals = changeGoals;
	}

	public String getGoalColorName(int goal) {
		return goalColorNames[goal];
	}

	public boolean getChangeGoals() {
		return changeGoals;
	}

	
	/** 
	 * registerPenalty. Register a penalty to the rulebook
	 *
	 * @param code the id of the penalty
	 * @param name the name of the penalty
	 * @param time the time how long a penalty is assign
	 */
	protected void registerPenalty(int code, String name, int time) {
		registerPenalty(code, name, time, false);
	}
	
	/** 
	 * registerPenalty. Register a penalty to the rulebook
	 *
	 * @param code the id of the penalty
	 * @param name the name of the penalty
	 * @param time the time how long a penalty is assign
	 * @param isAddon possibility to add more then one penalty 
	 */	
	protected void registerPenalty(int code, String name, int time, boolean isAddon) {
		registerPenalty(code, name, time, isAddon, false);
	}

	/** 
	 * registerPenalty. Register a penalty to the rulebook
	 *
	 * @param code the id of the penalty
	 * @param name the name of the penalty
	 * @param time the time how long a penalty is assign
	 * @param isAddon possibility to add more then one penalty 
	 * @param stayOnStateChange penalty stay after gamestate change
	*/		
	protected void registerPenalty(int code, String name, int time, boolean isAddon, boolean stayOnStateChange) {
		short codeShort = (short) code;
		penalties.addLast(codeShort);
		penaltyNames.put(codeShort, name);
		penaltyTimes.put(codeShort, (short) time);
		penaltyIsAddon.put(codeShort, isAddon);
		penaltyStayOnStateChange.put(codeShort, stayOnStateChange);
	}	

	public Iterator<Short> getPenalties() {
		return penalties.listIterator();
	}

	public String getPenaltyName(short code) {
		return (String) penaltyNames.get(code);
	}

	public short getPenaltyTime(short code) {
		return ((Short) penaltyTimes.get(code)).shortValue();
	}

	public boolean getPenaltyIsAddon(short code) {
		return (Boolean) penaltyIsAddon.get(code).booleanValue();
	}
	
	public boolean getPenaltyStayOnStateChange(short code) {
		if(code == 0) return false;
		return (Boolean) penaltyStayOnStateChange.get(code).booleanValue();
	}
	
	public boolean getPenaltyTimeStayOnStateChange() {
		return penaltyTimeStayOnStateChange;
	}	

	public void setPenaltyTimeStayOnStateChange(boolean val) {
		penaltyTimeStayOnStateChange = val;
	}		

	public void setTimeSeconds(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	public int getTimeSeconds() {
		return timeSeconds;
	}

	public void setOvertimeSeconds(int overtimeSeconds) {
		this.overtimeSeconds = overtimeSeconds;
	}

	public int getOvertimeSeconds() {
		return overtimeSeconds;
	}

	public void setReadySeconds(int readySeconds) {
		this.readySeconds = readySeconds;
	}

	public int getReadySeconds() {
		return readySeconds;
	}

	public void setAutomaticReady2Set(boolean value) {
		this.automaticReady2Set = value;
	}

	public boolean getAutomaticReady2Set() {
		return automaticReady2Set;
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

	public void setSwitchTeamColorBetweenHalfs(boolean switchTeamColorBetweenHalfs) {
		this.switchTeamColorBetweenHalfs = switchTeamColorBetweenHalfs;
	}

	public boolean getSwitchTeamColorBetweenHalfs() {
		return switchTeamColorBetweenHalfs;
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

	public int getPushingThreshold(int numEjected) {
		return this.pushingThresholds[numEjected];
	}

}
