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

/**
 * RobotInfo is part of the RoboCupGameControlData data structure.
 * The class can convert itself into a little endian byte array for sending over a network.
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 */
public class RobotInfo {

	private short penalty; // the penalty state the robot is in (see
	// Constants.java)
	private short secsTillUnpenalise; // estimated seconds till unpenalised

	/** Creates a new instance of RobotInfo */
	public RobotInfo() {
	}

	// convert into a string (for data structure serialisation)
	public String formString() {
		String player = new String(penalty + "," + secsTillUnpenalise);
		return player;
	}

	// get/set for penalty state
	public void setPenalty(short penalty) {
		this.penalty = penalty;
	}

	public short getPenalty() {
		return this.penalty;
	}

	// get/set for secsTillUnpenalise
	public short getSecsTillUnpenalised() {
		return this.secsTillUnpenalise;
	}

	public void setSecsTillUnpenalised(short secsTillUnpenalise) {
		this.secsTillUnpenalise = secsTillUnpenalise;
	}

}
