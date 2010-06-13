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

package org.robocup.gamecontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.robocup.gamecontroller.gui.MainGUI;
import org.robocup.gamecontroller.logging.SingleLineFormatter;
import org.robocup.gamecontroller.rules.HumanoidLeagueKidSizeRuleBook;
import org.robocup.gamecontroller.rules.HumanoidLeagueTeenSizeRuleBook;
import org.robocup.gamecontroller.rules.RuleBook;
import org.robocup.gamecontroller.rules.StandardPlatformLeagueRuleBook;

/*
 * GameController.java
 *
 * Created on 14 January 2005, 10:34
 */

/**
 * This is the starting class.
 * Its really just a wrapper to start MainGUI.
 * From there the GUI, data structure, and network code are initialised.
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 * 
 *         Modified by: Tekin Mericli
 */
public class GameController {

	private static Logger logger = Logger.getLogger("org.robocup.gamecontroller.gamecontroller");

	public GameController(String args[]) {
		final int NUM_MIN_ARGS = 2;

		final int leagueSpl = 0;
		final int leagueHlKid = 1;
		final int leagueHlTeen = 2;

		// default league is SPL
		// int leagueType = leagueHlKid;
		int leagueType = leagueSpl;

		// team info
		byte[] teamNumbers = new byte[Constants.NUM_TEAMS];
		String[] teamNames = new String[Constants.NUM_TEAMS];

		// command line argument defaults, uses these of the arguments don't
		// modify them
		String broadcast = Constants.NETWORK_BROADCAST; // broadcast address
		int port = Constants.NETWORK_DATA_PORT; // default port
		int range = 1;
		boolean debug = false;
		boolean quiet = false;
		int numPlayers = -1;

		// check there are at least the blue and red team numbers
		// the default league is spl
		if (args.length < NUM_MIN_ARGS) {
			System.err.println(Constants.HELP);
			System.exit(1);
		}

		// the last two arguments will always be the team numbers
		if (args.length >= NUM_MIN_ARGS) {
			teamNumbers[0] = Byte.parseByte(args[args.length - 2]);
			teamNumbers[1] = Byte.parseByte(args[args.length - 1]);
		}

		// scan each argument looking for specific switches
		for (int i = 0; i < args.length; i++) {

			// look for turned on debug switch
			if (args[i].equals(Constants.ARG_DEBUG)) {
				debug = true;
				System.out.println("Debugging on");
			}

			// look for the port switch
			if (args[i].equals(Constants.ARG_PORT)) {
				port = Integer.parseInt(args[i + 1]);
			}

			// look for the range switch
			if (args[i].equals(Constants.ARG_RANGE)) {
				range = Integer.parseInt(args[i + 1]);
			}

			// look for turned on quiet switch
			if (args[i].equals(Constants.ARG_QUIET)) {
				quiet = true;
				System.out.println("Sound suppressed");
			}

			// look for the broadcast switch
			if (args[i].equals(Constants.ARG_BROADCAST)) {
				broadcast = args[i + 1];
			}

			// look for the number of players switch
			if (args[i].equals(Constants.ARG_NUMPLAYERS)) {
				numPlayers = Integer.parseInt(args[i + 1]);
			}

			// check if the gc is initialized for SPL
			if (args[i].equals(Constants.ARG_SPL)) {
				leagueType = leagueSpl;
			}

			// check if the gc is initialized for HL kid
			if (args[i].equals(Constants.ARG_HLKID)) {
				leagueType = leagueHlKid;
			}
			if (args[i].equals(Constants.ARG_HL)) {
				leagueType = leagueHlKid;
			}

			// check if the gc is initialized for HL teen
			if (args[i].equals(Constants.ARG_HLTEEN)) {
				leagueType = leagueHlTeen;
			}
		}

		// set up logging
		debug = true;
		logger.setLevel(debug ? Level.INFO : Level.FINEST);
		logger.setUseParentHandlers(false);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SingleLineFormatter());
		logger.addHandler(consoleHandler);

		RuleBook rulebook = null;
		if (leagueType == leagueSpl) {
			rulebook = new StandardPlatformLeagueRuleBook();
		} else if (leagueType == leagueHlKid) {
			rulebook = new HumanoidLeagueKidSizeRuleBook();
		} else if (leagueType == leagueHlTeen) {
			rulebook = new HumanoidLeagueTeenSizeRuleBook();
		}

		Properties properties = new Properties();
		try {
			InputStream in;
			String resource = "/" + rulebook.getConfigDirectory() + "/" + "teams.cfg";
			File file = new File("config" + resource);
			if (file.exists()) {
				in = new FileInputStream("config" + resource);
			} else {
				in = getClass().getResourceAsStream(resource);
			}

			properties.load(in);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Error loading properties file");
		}
		teamNames[0] = properties.getProperty("" + teamNumbers[0], "Team " + teamNumbers[0]);
		teamNames[1] = properties.getProperty("" + teamNumbers[1], "Team " + teamNumbers[1]);

		logger.info(rulebook.getTeamName(0) + " is " + teamNames[0] + " (" + teamNumbers[0] + ")");
		logger.info(rulebook.getTeamName(1) + " is " + teamNames[1] + " (" + teamNumbers[1] + ")");
		logger.info("Using broadcast address: " + broadcast);
		logger.info("Using port " + port + " for broadcast");

		// start the GUI
		MainGUI gui = new MainGUI(rulebook, teamNames, teamNumbers, broadcast, port, range, numPlayers, quiet);

		gui.setVisible(true);
	}

	public static void main(String args[]) {
		new GameController(args);
	}

}
