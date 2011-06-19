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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import org.robocup.common.Constants;

public class Listener implements Runnable {

	private int port;
	private Handler handler;

	private boolean running = false;
	private boolean stop = false;

	public Listener(int port, Handler handler) {
		this.port = port;
		this.handler = handler;
	}

	public void stop() {
		stop = true;
	}

	public boolean isRunning() {
		return running;
	}

	public void run() {
		running = true;
		stop = false;

		try {
			DatagramSocket s = new DatagramSocket(null);

			s.setReuseAddress(true);
			s.bind(new InetSocketAddress(port));

			s.setSoTimeout(1000);

			ByteBuffer buffer = ByteBuffer.allocate(1000);
			buffer.order(Constants.NETWORK_BYTEORDER);
			byte[] data = buffer.array();
			DatagramPacket p = new DatagramPacket(data, data.length);

			while (!stop) {
				try {
					s.receive(p);
					buffer.rewind();
					handler.process(buffer);
				} catch (SocketTimeoutException ste) {
					// ignore timeout and resume normally
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		running = false;
	}

}
