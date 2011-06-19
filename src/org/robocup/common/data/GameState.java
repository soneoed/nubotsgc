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

package org.robocup.common.data;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import org.robocup.common.Constants;
import org.robocup.common.net.Provider;
import org.robocup.common.rules.RuleBook;
import org.robocup.gamestatevisualizer.MainGUI;

/**
 * This class holds the RoboCupGameControl data structure.
 * Refer to the readme for more information of its structure.
 * The class can convert itself into a little endian byte array for sending over a network.
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 * 
 *         Modified by: Tekin Mericli
 */

public class GameState implements Provider {

	// RoboCupGameControlData has two TeamInfos in it, a red and a blue
	private TeamInfo[] teams = new TeamInfo[Constants.NUM_TEAMS];

	private byte numPlayers;

	private byte gameState = Constants.STATE_INITIAL;
	private byte firstHalf = Constants.TRUE;

	private byte secGameState = Constants.STATE2_NORMAL;

	private int estimatedSecs;
	private byte kickOffTeam;
	private byte dropInTeam;
	private short dropInTime = -1; // -ve time = first dropin yet to happen
	private boolean noDropInYet = true; // this will stop the timer from
	
	// data structure header string, keep this at 4 characters/bytes
	public static final String STRUCT_HEADER = "RGme";
	public static final int STRUCT_VERSION = 6;	
	
	private int version = 6;	

	// counting drop in time

	public static final int packet_size = (
			4 + // header
			Constants.INT32_SIZE + // version
			Constants.INT8_SIZE + // playersPerTeam
			Constants.INT8_SIZE + // state
			Constants.INT8_SIZE + // firstHalf
			Constants.INT8_SIZE + // kickOffTeam
			Constants.INT8_SIZE + // secondaryState
			Constants.INT8_SIZE + // dropInTeam
			Constants.INT16_SIZE + // dropInTime
			Constants.INT32_SIZE + // secsRemaining
			Constants.NUM_TEAMS * ( // for each team
				Constants.INT8_SIZE + // teamNumber
				Constants.INT8_SIZE + // teamColour
				Constants.INT8_SIZE + // goalColour
				Constants.INT8_SIZE + // score
				Constants.MAX_NUM_PLAYERS * ( // for each player
					Constants.INT16_SIZE + // penalty
					Constants.INT16_SIZE // secsTillUnpenalised
				)
			)
	);

	/** Creates a new instance of RoboCupGameControlData */
	public GameState() {
		numPlayers = Constants.MAX_NUM_PLAYERS;

		teams[0] = new TeamInfo((byte) 0, (byte) 1, Constants.GOAL_BLUE);
		teams[1] = new TeamInfo((byte) 1, (byte) 2, Constants.GOAL_YELLOW);

		// estimatedSecs = Constants.TIME_SECONDS;
	}
	
	public GameState(byte blueNumber, byte redNumber) {
		numPlayers = Constants.MAX_NUM_PLAYERS;
		
	    teams[0] = new TeamInfo(Constants.TEAM_BLUE, blueNumber, Constants.GOAL_BLUE);
	    teams[1] = new TeamInfo(Constants.TEAM_RED, redNumber, Constants.GOAL_YELLOW);
	    
	    estimatedSecs = 60*Constants.TIME_MINUTES;
	}
	

	public GameState(RuleBook rulebook, byte[] teamNumbers) {
		numPlayers = rulebook.getNumPlayers();

		teams[0] = new TeamInfo((byte) 0, teamNumbers[0], Constants.GOAL_BLUE);
		teams[1] = new TeamInfo((byte) 1, teamNumbers[1], Constants.GOAL_YELLOW);

		// estimatedSecs = Constants.TIME_SECONDS;
	}

	// get the object as a byte array
	public synchronized byte[] getAsByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(packet_size);
		buffer.order(Constants.NETWORK_BYTEORDER);

		buffer.put(Constants.STRUCT_HEADER.getBytes(), 0, 4);
		buffer.putInt(Constants.STRUCT_VERSION);

		buffer.put(numPlayers);
		buffer.put(gameState);
		buffer.put(firstHalf);
		buffer.put(kickOffTeam);

		buffer.put(secGameState);
		buffer.put(dropInTeam);
		buffer.putShort(dropInTime);

		buffer.putInt(estimatedSecs);

		for (byte team = 0; team < Constants.NUM_TEAMS; team++) {
			buffer.put(getTeamNumber(team));
			buffer.put(team);
			buffer.put(getGoalColour(team));
			buffer.put(getScore(team));

			for (byte i = 0; i < Constants.MAX_NUM_PLAYERS; i++) {
				RobotInfo robot = teams[team].getPlayer(i);
				buffer.putShort(robot.getPenalty());
				buffer.putShort(robot.getSecsTillUnpenalised());
			}
		}

		return buffer.array();
	}

	public synchronized boolean getFromByteArray(ByteBuffer buffer) {
		byte[] header = new byte[4];
		buffer.get(header, 0, 4);

		int version = buffer.getInt();
		if (version != Constants.STRUCT_VERSION) {
			return false;
		}

		numPlayers = buffer.get();
		gameState = buffer.get();
		firstHalf = buffer.get();
		kickOffTeam = buffer.get();

		secGameState = buffer.get();
		dropInTeam = buffer.get();
		dropInTime = buffer.getShort();

		estimatedSecs = buffer.getInt();

		for (byte team = 0; team < Constants.NUM_TEAMS; team++) {
			setTeamNumber(team, buffer.get());
			buffer.get();
			setGoalColour(team, buffer.get());
			setScore(team, buffer.get());

			for (byte i = 0; i < Constants.MAX_NUM_PLAYERS; i++) {
				teams[team].getPlayer(i).setPenalty(buffer.getShort());
				teams[team].getPlayer(i).setSecsTillUnpenalised(buffer.getShort());
			}
		}

		return true;
	}

	// update the drop in times
	public synchronized void updateDropInTime() {
		if (!noDropInYet) {
			this.dropInTime++;
		}
	}

	// change the last team that was favoured
	public synchronized void setDropInTeam(byte team) {
		noDropInYet = false;
		this.dropInTeam = team;
		this.dropInTime = 0; // reset counter on drop in change
	}

	// resets the drop in for a new half
	public synchronized void resetDropIn() {
		noDropInYet = true;

		this.dropInTeam = 0;
		this.dropInTime = -1;
	}

	// resets all the penalties of the robots for a new kick off, etc
	public synchronized void resetPenalties() {
		for (int team = 0; team < Constants.NUM_TEAMS; team++) {
			for (byte i = 0; i < Constants.MAX_NUM_PLAYERS; i++) {
				teams[team].getPlayer(i).setPenalty(Constants.PENALTY_NONE);
			}
		}
	}

	public synchronized void resetPenalty(byte team, byte player) {
		teams[team].getPlayer(player).setPenalty(Constants.PENALTY_NONE);
	}

	public synchronized byte getTeamColour(byte team) {
		return teams[team].getTeamColour();
	}

	public synchronized void setTeamColour(byte team, byte color) {
		teams[team].setTeamColour(color);
	}

	// return the specified team (0 = blue, 1 = red)
	public synchronized TeamInfo getTeam(byte team) {
		return teams[team];
	}

	public synchronized void setTeam(byte team, TeamInfo t) {
		teams[team] = t;
	}

	// make the data structure into a certain game state
	public synchronized void setGameState(byte gameState) {
		this.gameState = gameState;
	}

	public synchronized byte getGameState() {
		return this.gameState;
	}

	// make the data structure into a certain secondary game state
	public synchronized void setSecondaryGameState(byte secondaryState) {
		this.secGameState = secondaryState;
	}

	public synchronized byte getSecondaryGameState() {
		return this.secGameState;
	}

	// get the first half / second half flag
	public synchronized boolean getHalf() {
		return (firstHalf == Constants.TRUE);
	}

	// set the first half / second half flag
	public synchronized void setHalf(byte firstHalf) {
		this.firstHalf = firstHalf;
	}

	// set the first half / second half flag
	public synchronized void setHalf(boolean firstHalf) {
		setHalf(firstHalf ? Constants.TRUE : Constants.FALSE);
	}

	// set the number of estimated seconds remaining in the half
	// the parameter comes from the clock label
	// return 0 seconds when in overtime
	public synchronized void setEstimatedSecs(int time, boolean overTime) {
		if (overTime == false) {
			this.estimatedSecs = time;
		} else {
			this.estimatedSecs = 0;
		}
	}

	public synchronized int getEstimatedSecs() {
		return estimatedSecs;
	}

	// get the kick off team
	public synchronized byte getKickOffTeam() {
		return kickOffTeam;
	}

	// set the kick off team
	public synchronized void setKickOffTeam(byte kickOffTeam) {
		this.kickOffTeam = kickOffTeam;
	}

	// gets/sets for blue team number
	public synchronized byte getTeamNumber(byte team) {
		return teams[team].getTeamNumber();
	}

	public synchronized void setTeamNumber(byte team, byte number) {
		teams[team].setTeamNumber(number);
	}

	// gets/sets for goal color
	public synchronized byte getGoalColour(byte team) {
		return teams[team].getGoalColour();
	}

	public synchronized void setGoalColour(byte team, byte colour) {
		teams[team].setGoalColour(colour);
	}

	// get/set for blue team scores
	public synchronized byte getScore(byte team) {
		return teams[team].getTeamScore();
	}

	public synchronized void setScore(byte team, byte score) {
		teams[team].setTeamScore(score);
	}

	// get/set a penalty to a particular player
	public synchronized short getPenalty(byte team, byte player) {
		return teams[team].getPlayer(player).getPenalty();
	}

	public synchronized void setPenalty(byte team, byte player, short penalty) {
		teams[team].getPlayer(player).setPenalty(penalty);
	}

	// set the penalty time to a particular player
	public synchronized void setSecsTillUnpenalised(byte team, byte player, short secs) {
		teams[team].getPlayer(player).setSecsTillUnpenalised(secs);

	}

	// get the penalty time to a particular player
	public synchronized short getSecsTillUnpenalised(byte team, byte player) {
		return teams[team].getPlayer(player).getSecsTillUnpenalised();
	}
	
	public synchronized void updateTimeLastSeen(byte team, byte player, long time) {
		teams[team].getPlayer(player).updateTimeLastSeen(time);
	}	
	
	public synchronized long getTimeLastSeen(byte team, byte player) {
		return teams[team].getPlayer(player).getTimeLastSeen();
	}		
	
	// Read Packet for GameStateVisualizer
	public boolean readPacket(DatagramPacket packet,  MainGUI gui) {
		ByteBuffer packetData = ByteBuffer.allocate(GameState.packet_size*3);
		packetData.order(Constants.NETWORK_BYTEORDER);
		packetData.rewind();
		packetData.put(packet.getData());
		
		packetData.rewind();
		
		byte[] header = new byte[4];
		packetData.get(header);
		if (!GameState.STRUCT_HEADER.equals(new String(header))) {
			if (gui.getDebug()) { System.out.println("Bad header"); }
			return false;
		}
		
		int version = packetData.getInt();
		if (version != 6 && version != 7) {
			if (gui.getDebug()) {
				System.out.println("Bad packet version: "
					+ version + " expecting: 6 or 7");
			}
			return false;
		}

		packetData.get(); // playersPerTeam
		byte gameState = packetData.get(); // gameState
		byte firstHalf = packetData.get(); // firstHalf
		packetData.get(); // kickOffTeam
		byte secondaryState = packetData.get(); // secondaryState
		packetData.get(); // dropInTeam
		packetData.getShort(); // dropInTime
		int secsRemaining = packetData.getInt(); // secsRemaining
		
		// team blue
		byte team1Number = packetData.get(); // teamNumber
		packetData.get(); // teamColor
		if (version == 7)
		    packetData.get();
		int team1Score = version == 6 ? packetData.getShort() : packetData.get(); // score
		packetData.position(packetData.position() + Constants.MAX_NUM_PLAYERS * 4);
		// team red
		byte team2Number = packetData.get(); // teamNumber
		packetData.get(); // teamColor
		if (version == 7)
		    packetData.get();
		int team2Score = version == 6 ? packetData.getShort() : packetData.get(); // score
		this.setVersion(version);
		this.setGameState(gameState);
		this.setSecondaryGameState(secondaryState);
		this.setHalf(firstHalf);
		this.setEstimatedSecs(secsRemaining, false);
		this.getTeam(Constants.TEAM_BLUE).setTeamNumber(team1Number);
		this.getTeam(Constants.TEAM_BLUE).setTeamScore((byte) team1Score);
		this.getTeam(Constants.TEAM_RED).setTeamNumber(team2Number);
		this.getTeam(Constants.TEAM_RED).setTeamScore((byte) team2Score);
		return true;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}	
}
