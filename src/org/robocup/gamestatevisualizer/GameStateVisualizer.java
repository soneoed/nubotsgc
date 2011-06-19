package org.robocup.gamestatevisualizer;

/*
Copyright (C) 2009  University of Bremen

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.awt.GraphicsEnvironment;

import org.robocup.common.Constants;
import org.robocup.common.data.GameState;

/**
* The starting point of the application.
* @author rieskamp@tzi.de
* @author Thomas.Roefer@dfki.de
*/
public class GameStateVisualizer {
	public static void main(String args[]) {
	    GameState gameData = new GameState(Constants.TEAM_BLUE, Constants.TEAM_RED);
	            gameData = new GameState(Constants.TEAM_BLUE, Constants.TEAM_RED);
	    gameData.setHalf(true);
	    gameData.setScore(Constants.TEAM_BLUE, (byte) 0);
	    gameData.setScore(Constants.TEAM_RED, (byte) 0);
	    gameData.setEstimatedSecs(60 * Constants.TIME_MINUTES, false);
	    gameData.setTeamNumber(Constants.TEAM_BLUE, (byte) 0);
	    gameData.setTeamNumber(Constants.TEAM_RED, (byte) 0);
	
	    boolean penaltyDisplay = true;
	    int mode = Constants.MODE_SPL;
	    boolean debug = false;
	    String configDir = null;
	    int display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
	    boolean fullscreen = false;
	    for(int i = 0; i < args.length; ++i) {
	        if (Constants.ARG_PENALTY.equals(args[i])) {
	            penaltyDisplay = false;
	        } else if (Constants.ARG_DISPLAY.equals(args[i])) {
	            if (i + 1 < args.length) {
	                int d = Integer.valueOf(args[++i]).intValue();
	                if (d < 1 || d > display) {
	                    System.err.println("This system only supports display 1 to " + display);
	                    System.exit(1);
	                }
	                display = d;
	            } else {
	                usage();
	            }
	        } else if (Constants.ARG_FULLSCREEN.equals(args[i])) {
	            fullscreen = true;
	        } else if (Constants.ARG_DEBUG.equals(args[i])) {
	            debug = true;	            
	        } else if(Constants.ARG_CONFIGDIR.equals(args[i])) {
	        	if (i + 1 < args.length) {
	        		configDir = args[i++ + 1];
	        	}		            
	        } else if(Constants.ARG_SSL.equals(args[i])) {
	            mode = Constants.MODE_SSL;
	            if (i + 2 < args.length) {
	                int sslTeamIdBlue = Integer.valueOf(args[++i]).intValue();
	                int sslTeamIdYellow = Integer.valueOf(args[++i]).intValue();
	                gameData.setTeamNumber(Constants.TEAM_BLUE, (byte) sslTeamIdBlue);
	                gameData.setTeamNumber(Constants.TEAM_RED, (byte) sslTeamIdYellow);
	            } else {
	              usage();
	            }            
	        } else if(Constants.ARG_HLKID.equals(args[i])) {
	    	    mode = Constants.MODE_HLKID;
	        } else if(Constants.ARG_HLTEEN.equals(args[i])) {
	    	    mode = Constants.MODE_HLTEEN;
	        } else if(!"-spl".equals(args[i])) {
	            usage();
	        }
	    }
	
	    new MainGUI(Constants.NETWORK_DATA_PORT, penaltyDisplay, mode, gameData, display - 1, fullscreen, configDir, debug);
	}
	
	private static void usage() {
	      System.err.println("Usage: gameStateVisualizer.jar <options>");
	      System.err.println("  -spl                 Visualize Standard Platform League game state");
	      System.err.println("  -hlkid               Visualize Humanoid League Kid Size game state");
	      System.err.println("  -hlteen              Visualize Humanoid League Kid Size game state");
	      System.err.println("  -configdir <dir>     Alternativ configuration directory");
	      System.err.println("  -ssl <blue> <yellow> Visualize Small Size League game state");
	      System.err.println("                       for the two teams with the given numbers");
	      System.err.println("  -penalty             Don't show the state-based penalty shootout display");
	      System.err.println("  -display <display>   Use the display with the given number");
	      System.err.println("  -debug          	 Enable Debug output");
	      System.err.println("  -fullscreen          Hide menu bar and dock on a Mac");
	      System.exit(1);
	}
}
