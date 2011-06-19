package org.robocup.gamestatevisualizer.net;

/*
Copyright (C) 2006  University Of New South Wales
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Vector;

import org.robocup.common.Constants;
import org.robocup.common.data.GameState;
import org.robocup.gamestatevisualizer.MainGUI;

/**
* The class listens to the packages sent by the GameContoller. 
*
* @author rieskamp@tzi.de
*/
public class Listen implements Runnable {      

private boolean stopController = false;

private MainGUI gui;

// some objects for networking
private DatagramSocket listen;      // socket for broadcasting
private ByteBuffer packetData;      // the packet data
private DatagramPacket packet;      // an incomming packet

private boolean penaltySuccess = false;

static final int packetSize = GameState.packet_size;   // the size of the return packet

public Listen(int port, MainGUI gui) {
    
    try {
        listen = new DatagramSocket(null);
        listen.setReuseAddress(true);
        listen.bind(new InetSocketAddress(port));

        byte[] buf = new byte[packetSize*2];
        packet = new DatagramPacket(buf, packetSize*2);
        this.gui = gui;
        packetData  = ByteBuffer.allocate(packetSize*3);
		packetData.order(Constants.NETWORK_BYTEORDER);
    } catch (Exception err) {
        System.err.println("GameStateVisualizer (constructor) error: " + 
                           err.getMessage() + " " +
                           "(No network devices set up?)");
        System.exit(1);
    }
}

// broadcast loop runs in a separate thread, this is stopped by socketCleanup
public void run() {
    while (!stopController) {
        try {
			listen.receive(packet);
			if (gui.getDebug()) { System.out.println("Incomming packet"); }
			if (packet.getLength() != packetSize) {
				if (gui.getDebug()) { System.out.println("Bad size"); }
				continue;
			}
			
			GameState gameData = gui.getGameData();
			// monitor for changes during penalty shoot-out
			byte oldSecondaryGameState  = gameData.getSecondaryGameState();
			boolean oldHalf 			= gameData.getHalf();
			short oldTeam2Score			= gameData.getTeam(Constants.TEAM_RED).getTeamScore();
			
			// read package
			gameData.readPacket(packet, gui);
			
			if(oldSecondaryGameState == Constants.STATE2_NORMAL 
					&& gameData.getSecondaryGameState() == Constants.STATE2_PENALTYSHOOT) {
				
				for(byte i = 0; i <= 1; i++)
					gui.getPenaltyStates().put(""+(int) gameData.getTeam(i).getTeamNumber(), new Vector<String>());
				penaltySuccess = false;
			}
			if(oldSecondaryGameState == Constants.STATE2_PENALTYSHOOT
				&& oldHalf != gameData.getHalf()) {
				
				if(!penaltySuccess) {
					Vector<String> statesBlue = gui.getPenaltyStates().get(""+(int)gameData.getTeam(Constants.TEAM_BLUE).getTeamNumber());
					statesBlue.add(""+'\u25CB');
				}
				else
					penaltySuccess = false;
			}
			if(oldSecondaryGameState == Constants.STATE2_PENALTYSHOOT
				&& oldHalf == gameData.getHalf()
				&& oldTeam2Score < gameData.getTeam(Constants.TEAM_RED).getTeamScore()) {
				
				Vector<String> statesBlue = gui.getPenaltyStates().get(""+(int)gameData.getTeam(Constants.TEAM_RED).getTeamNumber());
				statesBlue.add(""+'\u25CF');
				penaltySuccess = true;
			}
			
			gui.setTimeWhenLastPacketReceived(new Date());
			gui.repaint();		
       } catch (IOException e) {
       		if (!stopController) {
       			System.err.println("IOException while listening for packets: " + e);
       		}
       }
    }
}

// called by MainGUI when program is closing
public void socketCleanup() {
    try {
        if (gui.getDebug()) { System.out.println("Stopping listen thread"); }
        stopController = true;
        if (gui.getDebug()) { System.out.println("Closing listen socket"); }
        listen.close();   
    } catch (Exception err) {
        System.err.println("socketCleanup error: " + err.getMessage());
        System.exit(1);
    }
}

}
