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

package org.robocup.gamecontroller.gui;

import java.nio.ByteBuffer;

import org.robocup.gamecontroller.data.RobotState;
import org.robocup.gamecontroller.net.Handler;

public class GuiHandler implements Handler {

	protected MainGUI gui;
	protected RobotState state;

	public GuiHandler() {
		state = new RobotState();
	}

	public void setGui(MainGUI gui) {
		this.gui = gui;
	}

	public synchronized boolean process(ByteBuffer buffer) {
		boolean success = state.getFromByteArray(buffer);

		short robotID = state.getRobotId();

		// check for illegal robot IDs sent by the robots
		success = success && (robotID >= 0 && robotID < gui.numPlayers);

		if (success) {
			gui.updateRobot(state);
		}
		return success;
	}

}
