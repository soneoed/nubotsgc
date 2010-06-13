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

package org.robocup.gamecontroller.data;

import org.robocup.gamecontroller.Constants;

/*
 * TeamInfo.java
 *
 * Created on 10 January 2005, 12:21
 */

/**
 * TeamInfo is part of the RoboCupGameControlData data structure.
 * The class can convert itself into a little endian byte array for sending over a network.
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 */
public class TeamInfo {

	private RobotInfo[] players;

	private byte teamNumber;
	private byte teamColour;
	private byte goalColour;
	private byte score;

	// constructor that sets the team colour and number
	public TeamInfo(byte teamColour, byte teamNumber, byte goalColour) {
		players = new RobotInfo[Constants.MAX_NUM_PLAYERS];
		for (int i = 0; i < Constants.MAX_NUM_PLAYERS; i++) {
			players[i] = new RobotInfo();
		}

		this.teamColour = teamColour;
		this.teamNumber = teamNumber;
		this.goalColour = goalColour;
	}

	// get the robots in the team, return as an array
	public RobotInfo getPlayer(byte i) {
		return players[i];
	}

	// get/set the team number
	public void setTeamNumber(byte teamNumber) {
		this.teamNumber = teamNumber;
	}

	public byte getTeamNumber() {
		return this.teamNumber;
	}

	// get/set team colour
	public void setTeamColour(byte teamColour) {
		this.teamColour = teamColour;
	}

	public byte getTeamColour() {
		return this.teamColour;
	}

	// get/set goal colour
	public void setGoalColour(byte goalColour) {
		this.goalColour = goalColour;
	}

	public byte getGoalColour() {
		return this.goalColour;
	}

	// get/set team score
	public void setTeamScore(byte score) {
		this.score = score;
	}

	public byte getTeamScore() {
		return this.score;
	}
}
