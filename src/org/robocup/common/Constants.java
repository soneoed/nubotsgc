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

package org.robocup.common;

import java.awt.Color;
import java.nio.ByteOrder;

/**
 * This class contains only constants
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 * 
 *         Modified by: Tekin Mericli
 */

public class Constants {
	// number of teams
	public static final byte NUM_TEAMS = 2;

	// number of players
	public static final byte MAX_NUM_PLAYERS = 11;
	public static final byte NUM_PLAYERS_SPL = MAX_NUM_PLAYERS; // 3;
	public static final byte NUM_PLAYERS_HL = MAX_NUM_PLAYERS; // 4;

	// TRUE/FALSE - use this instead of built-in true/false to allow for sending
	// of the data in a byte (when converted to a string)
	// used only by the first half flag
	public static final byte TRUE = 1;
	public static final byte FALSE = 0;

	// number of bytes for the different types
	public static final int INT32_SIZE = 4;
	public static final int INT16_SIZE = 2;
	public static final int INT8_SIZE = 1;

	// strings for command line arguments
	public static final String ARG_DEBUG = "-debug";
	public static final String ARG_QUIET = "-quiet";
	public static final String ARG_PORT = "-port";
	public static final String ARG_RANGE = "-range";
	public static final String ARG_BROADCAST = "-broadcast";
	public static final String ARG_NUMPLAYERS = "-numplayers";
	public static final String ARG_SPL = "-spl";
	public static final String ARG_HL = "-hl";
	public static final String ARG_HLKID = "-hlkid";
	public static final String ARG_HLTEEN = "-hlteen";
	public static final String ARG_CONFIGDIR = "-configdir";
	// strings for command line arguments (GameState Visualizer)
	public static final String ARG_SSL = "-ssl";
	public static final String ARG_DISPLAY = "-display";
	public static final String ARG_FULLSCREEN = "-fullscreen";
	public static final String ARG_PENALTY = "-penalty";
	
	// some networking constants/defaults
	public static final int NETWORK_DATA_PORT = 3838;
	public static final String NETWORK_BROADCAST = "255.255.255.255";
	public static final int NETWORK_HEARTBEAT = 500; // 500ms
	public static final int NETWORK_BURST_COUNT = 3; // burst goes for 3 packets
	// extra
	public static final ByteOrder NETWORK_BYTEORDER = ByteOrder.LITTLE_ENDIAN;

	// data structure version
	public static final int STRUCT_VERSION = 7;

	// data structure header string, keep this at 4 characters/bytes
	public static final String STRUCT_HEADER = "RGme";

	// return data structure version
	public static final int RETURN_STRUCT_VERSION = 1;

	// return data structure header string, keep this at 4 characters/bytes
	public static final String RETURN_STRUCT_HEADER = "RGrt";

	public static final int GAMECONTROLLER_RETURN_MSG_MAN_PENALISE = 0;
	public static final int GAMECONTROLLER_RETURN_MSG_MAN_UNPENALISE = 1;
	public static final int GAMECONTROLLER_RETURN_MSG_ALIVE = 2;

	// team colors
	public static final byte TEAM_CYAN = 0;
	public static final byte TEAM_MAGENTA = 1;
	public static final byte TEAM_BLUE = 0;
	public static final byte TEAM_RED = 1;
	public static final byte DROPBALL = 2;

	public static final byte GOAL_BLUE = 0;
	public static final byte GOAL_YELLOW = 1;

	// game states
	public static final byte STATE_INITIAL = 0;
	public static final byte STATE_READY = 1;
	public static final byte STATE_SET = 2;
	public static final byte STATE_PLAYING = 3;
	public static final byte STATE_FINISHED = 4;

	// secondary game states
	public static final byte STATE2_NORMAL = 0;
	public static final byte STATE2_PENALTYSHOOT = 1;
	public static final byte STATE2_OVERTIME = 2;

	// penalties
	public static final short PENALTY_NONE = 0;
	// SPL
	public static final short PENALTY_SPL_BALL_HOLDING = 1;
	public static final short PENALTY_SPL_PLAYER_PUSHING = 2;
	public static final short PENALTY_SPL_OBSTRUCTION = 3;
	public static final short PENALTY_SPL_INACTIVE_PLAYER = 4;
	public static final short PENALTY_SPL_ILLEGAL_DEFENDER = 5;
	public static final short PENALTY_SPL_LEAVING_THE_FIELD = 6;
	public static final short PENALTY_SPL_PLAYING_WITH_HANDS = 7;
	public static final short PENALTY_SPL_REQUEST_FOR_PICKUP = 8;
	// HL Kid Size
	public static final short PENALTY_HL_KID_BALL_MANIPULATION = 1;
	public static final short PENALTY_HL_KID_PHYSICAL_CONTACT = 2;
	public static final short PENALTY_HL_KID_ILLEGAL_ATTACK = 3;
	public static final short PENALTY_HL_KID_ILLEGAL_DEFENSE = 4;
	public static final short PENALTY_HL_KID_REQUEST_FOR_PICKUP = 5;
	public static final short PENALTY_HL_KID_REQUEST_FOR_SERVICE = 6;
	public static final short PENALTY_HL_KID_REQUEST_FOR_PICKUP_2_SERVICE = 7;
	// HL Teen Size
	public static final short PENALTY_HL_TEEN_BALL_MANIPULATION = 1;
	public static final short PENALTY_HL_TEEN_PHYSICAL_CONTACT = 2;
	public static final short PENALTY_HL_TEEN_ILLEGAL_ATTACK = 3;
	public static final short PENALTY_HL_TEEN_ILLEGAL_DEFENSE = 4;
	public static final short PENALTY_HL_TEEN_REQUEST_FOR_PICKUP = 5;
	public static final short PENALTY_HL_TEEN_REQUEST_FOR_SERVICE = 6;
	public static final short PENALTY_HL_TEEN_REQUEST_FOR_PICKUP_2_SERVICE = 7;

	public static final short PENALTY_MANUAL = 15;

	// help message
	public static final String HELP = ""
			+ "Usage: java -jar GameController [-port p] [-range r] [-numplayers] [-spl] [-hlkid] [-hlteen] [-configdir dir] [-debug] [-broadcast broadcast_address] cyanTeamNumber magentaTeamNumber\n"
			+ "* The league type will default to hlkid if not specified.\n"
			+ "* The configdir will default to league rulebook if not specified.\n"
			+ "* The broadcast port will default to 3838 if not specified.\n"
			+ "* The broadcast address will default to 255.255.255.255 if not specified.\n"
			+ "* The team numbers must be specified for the GameController to work.\n"
			+ "* -debug will print debug information to the command line.\n"
			+ "eg: 1) java -jar GameController 25 15\n"
			+ "    2) java -jar GameController -hlteen -debug -broadcast 192.168.0.255 25 15\n";

	// player button settings (threshold to select button index, button color and blink interval
	public static final short[] PLAYER_BUTTON_THRESHOLD = {0, 5, 30}; // in s	
	public static final Color[] PLAYER_BUTTON_COLORS = {Color.GREEN, Color.YELLOW, Color.RED}; // in java color	
	public static final short[] PLAYER_BUTTON_BLINK_INTERVAL = {150, 250, 0}; // in ms	
	
	// last seen indicator settings (threshold to select index)
	public static final short[] LASTSEEN_INDICATOR_THRESHOLD = {2500, 1500, 0}; // in ms	
	public static final Color[] LASTSEEN_INDICATOR_COLORS = {Color.RED, Color.YELLOW, Color.GREEN}; // in java color	
	

	// play modes
	public static final short PLAY_MODE_FIRST_HALF = 0;
	public static final short PLAY_MODE_SECOND_HALF = 1;
	public static final short PLAY_MODE_FIRST_OVERTIME = 2;
	public static final short PLAY_MODE_SECOND_OVERTIME = 3;
	public static final short PLAY_MODE_FIRST_TEAM_PENALTY = 4;
	public static final short PLAY_MODE_SECOND_TEAM_PENALTY = 5;

	// Logs
	public static final String LOG_FILENAME_GSV = "GameStateVisualizer.log";
	public static final String LOG_FILENAME_GC = "GameController.log";	
	
	// number of minutes initially on the clock (GameState Visualizer)
	public static final int TIME_MINUTES = 10;
	public static final int READY_SECONDS = 45;
	public static final int TIME_OUT_MINS = 5;
	public static final int PENALTY_SHOOT_MINS = 1;
	public static final int PENALTY_SHOOT_SECS = 0;
	public static final int HALF_TIME_MINS = 10;	
	
	// modes (GameState Visualizer)
	public static final int MODE_SPL = 0;
	public static final int MODE_SSL = 1;
	public static final int MODE_HLKID = 2;
	public static final int MODE_HLTEEN = 3;	
	
	/** Creates a new instance of Constants */
	private Constants() {
	}

}
