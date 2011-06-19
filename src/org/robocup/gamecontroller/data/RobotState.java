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

import java.nio.ByteBuffer;

import org.robocup.common.Constants;
import org.robocup.common.net.Provider;

public class RobotState implements Provider {

	private short teamId;
	private short robotId;
	private int message;

	public RobotState() {
		teamId = 1;
		robotId = 1;
		message = 1;
	}

	public byte[] getAsByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.order(Constants.NETWORK_BYTEORDER);

		buffer.put(Constants.RETURN_STRUCT_HEADER.getBytes(), 0, 4);

		buffer.putInt(Constants.RETURN_STRUCT_VERSION);

		buffer.putShort(teamId);
		buffer.putShort(robotId);

		buffer.putInt(message);

		return buffer.array();
	}

	public synchronized boolean getFromByteArray(ByteBuffer buffer) {
		byte[] header = new byte[4];
		buffer.get(header, 0, 4);

		if (buffer.getInt() != Constants.RETURN_STRUCT_VERSION) {
			return false;
		}

		byte[] magic = Constants.RETURN_STRUCT_HEADER.getBytes();

		for (int i = 0; i < header.length; ++i) {
			if (magic[i] != header[i]) {
				return false;
			}
		}

		teamId = buffer.getShort();
		robotId = (short) (buffer.getShort() - 1);
		message = buffer.getInt();

		return true;
	}

	public short getTeamId() {
		return teamId;
	}

	public void setTeamId(short teamId) {
		this.teamId = teamId;
	}

	public short getRobotId() {
		return robotId;
	}

	public void setRobotId(short robotId) {
		this.robotId = robotId;
	}

	public int getMessage() {
		return message;
	}

	public void setMessage(int message) {
		this.message = message;
	}

}
