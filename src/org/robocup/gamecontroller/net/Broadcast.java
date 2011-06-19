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

package org.robocup.gamecontroller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

import org.robocup.common.Constants;
import org.robocup.common.net.Provider;

/**
 * The Broadcast file contains the networking code of the program.
 * It is used by the MainGUI file to interact with the network.
 * Depending on the actions in the GUI, changes will be made to the RoboCupGameControlData structure and the structure is then serialized and broadcasted in this class.
 * 
 * The Broadcast class runs in a separate thread to the rest of the program so that broadcasts are not held back by delays in the GUI code.
 * 
 * By default, once the thread is started, it will broadcast the data structure (a reference of which is passed from MainGUI when it initializes this class) twice a second (500ms heartbeat).
 * When there are important state changes (game kick off, drop ins), there is 3 second period where the heartbeat rate is decreased to 100ms.
 * All these values can be adjusted in Constants.java.
 * 
 * @author willu@cse.unsw.edu.au shnl327@cse.unsw.edu.au
 */
public class Broadcast implements Runnable {

	private static Logger logger = Logger.getLogger("org.robocup.gamecontroller.net.broadcast");

	private Provider provider = null;
	private boolean stopController = false;

	// some objects for networking
	private DatagramSocket socket; // socket for broadcasting
	private int port; // port to broadcast to
	private int range = 1; // the number of following ports
	private InetAddress group; // subnet to broadcast to
	private boolean burst = false; // enable/disable burst broadcasts
	private int heartbeat;

	/** Creates a new instance of Broadcast */
	// it is given a reference to the data structure to broadcast,
	// the broadcast address and port
	public Broadcast(Provider provider, String broadcast, int port, int range, int heartbeat) {
		this.heartbeat = heartbeat;

		this.port = port;
		this.range = range;
		if (this.range < 1) {
			this.range = 1;
		}

		this.provider = provider;

		try {
			this.socket = new DatagramSocket();
			this.group = InetAddress.getByName(broadcast);

		} catch (Exception err) {
			System.err.println("GameController (constructor) error: " + err.getMessage() + " " + "(No network devices set up?)");
			System.exit(1);
		}
	}

	// broadcast loop runs in a separate thread, this is stopped by socketCleanup
	public void run() {
		while (!stopController) {
			try {
				dataBroadcast();

				// if burst is enabled, repeat message for NETWORK_BURST_COUNT times
				if (burst) {
					logger.fine("[BURST]");
					for (int i = Constants.NETWORK_BURST_COUNT; i > 0; i--) {
						dataBroadcast();
					}
					this.burst = false;
					logger.fine("[/BURST]");
				}

				Thread.sleep(heartbeat);
			} catch (InterruptedException err) {
				// sleep interrupted, OS doing other things
			}
		}
	}

	// broadcast the data structure by getting the byte array representation of
	// the RoboCupGameControlData and then broadcasting it
	public void dataBroadcast() {
		byte[] packetData = provider.getAsByteArray();
		for (int i = 1; i <= range; i++) {
			try {
				logger.fine("broadcasting " + packetData.length + " bytes ...");
				DatagramPacket packet = new DatagramPacket(packetData, packetData.length, group, port + i - 1);
				socket.send(packet);
			} catch (IOException err) {
				System.err.println("dataBroadcast error: " + err.getMessage());
			}
		}
	}

	// called by MainGUI when program is closing
	public void socketCleanup() {
		try {
			logger.fine("Closing broadcast socket");
			socket.close();
			logger.fine("Stopping broadcast thread");
			stopController = true;
		} catch (Exception err) {
			System.err.println("socketCleanup error: " + err.getMessage());
			System.exit(1);
		}
	}

	// this method is called by MainGUI to indicate when burst mode is needed
	// from the UDP broadcasts
	public void setBurst(boolean enable) {
		this.burst = enable;
	}

	public boolean getBurst() {
		return this.burst;
	}

}
