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

package org.robocup.gamecontroller.client;

import javax.swing.*;

import org.robocup.gamecontroller.Constants;
import org.robocup.gamecontroller.data.GameState;
import org.robocup.gamecontroller.data.RobotState;
import org.robocup.gamecontroller.net.Broadcast;
import org.robocup.gamecontroller.net.Listener;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DemoClient extends WindowAdapter {

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GuiHandler handler = new GuiHandler();

				Listener l = new Listener(Constants.NETWORK_DATA_PORT, handler);

				byte teamId = 1;
				byte robotId = 1;

				if (args.length >= 1) {
					try {
						teamId = Byte.parseByte(args[0]);
					} catch (NumberFormatException e) {
					}
				}
				if (args.length >= 2) {
					try {
						robotId = Byte.parseByte(args[1]);
					} catch (NumberFormatException e) {
					}
				}

				RobotState state = new RobotState();
				state.setTeamId(teamId);
				state.setRobotId(robotId);

				Broadcast broadcast = new Broadcast(state, Constants.NETWORK_BROADCAST, 3940, 1, 2000);

				DemoClient client = new DemoClient(l, broadcast, teamId, robotId);
				client.create();

				Thread broadcastThread = new Thread(broadcast);
				broadcastThread.start();

				handler.setGui(client);

				Thread t = new Thread(l);
				t.start();
			}
		});
	}

	protected Listener listener;
	protected Broadcast broadcast;

	protected JFrame frame;

	protected JLabel lbTeam;
	protected JLabel lbGoal;
	protected JLabel lbRobot;
	protected JLabel lbState;
	protected JLabel lbRemaining;
	protected JLabel lbScore;

	protected short teamId;
	protected short robotId;

	public DemoClient(Listener l, Broadcast b, short teamId, short robotId) {
		listener = l;
		broadcast = b;

		this.teamId = teamId;
		this.robotId = robotId;
	}

	private void create() {
		frame = new JFrame("DemoClient");

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);

		JPanel mainPane = new JPanel();
		frame.setContentPane(mainPane);
		mainPane.setLayout(new GridLayout(0, 2));

		mainPane.add(new JLabel("Team:"));
		lbTeam = new JLabel("-");
		mainPane.add(lbTeam);

		mainPane.add(new JLabel("Goal:"));
		lbGoal = new JLabel("-");
		mainPane.add(lbGoal);

		mainPane.add(new JLabel("Robot:"));
		lbRobot = new JLabel("-");
		mainPane.add(lbRobot);

		mainPane.add(new JLabel("State:"));
		lbState = new JLabel("-");
		mainPane.add(lbState);

		mainPane.add(new JLabel("Time:"));
		lbRemaining = new JLabel("--:--");
		mainPane.add(lbRemaining);

		mainPane.add(new JLabel("Score:"));
		lbScore = new JLabel("-:-");
		mainPane.add(lbScore);

		frame.pack();
		frame.setVisible(true);
	}

	public void update(GameState rgcd) {
		boolean found = false;
		byte team = 0;
		byte opponent = 0;
		if (rgcd.getTeamNumber(Constants.TEAM_CYAN) == teamId) {
			found = true;
			team = Constants.TEAM_CYAN;
			opponent = Constants.TEAM_MAGENTA;
		} else if (rgcd.getTeamNumber(Constants.TEAM_MAGENTA) == teamId) {
			found = true;
			team = Constants.TEAM_MAGENTA;
			opponent = Constants.TEAM_CYAN;
		}

		if (found) {
			lbTeam.setText(String.valueOf((int) rgcd.getTeamNumber(team)) + " - " + (team == Constants.TEAM_CYAN ? "cyan" : "magenta"));
			lbGoal.setText(rgcd.getGoalColour(team) == Constants.GOAL_BLUE ? "blue" : "yellow");
			lbRobot.setText(String.valueOf(robotId));

			String state = "";
			if (rgcd.getGameState() == Constants.STATE_INITIAL) {
				state = "Initial";
			} else if (rgcd.getGameState() == Constants.STATE_READY) {
				state = "Ready";
			} else if (rgcd.getGameState() == Constants.STATE_SET) {
				state = "Set";
			} else if (rgcd.getGameState() == Constants.STATE_PLAYING) {
				state = "Playing";
			} else if (rgcd.getGameState() == Constants.STATE_FINISHED) {
				state = "Finished";
			} else {
				state = "Unknown: " + String.valueOf((int) rgcd.getGameState());
			}

			if (rgcd.getGameState() == Constants.STATE_READY || rgcd.getGameState() == Constants.STATE_SET) {
				if (rgcd.getKickOffTeam() == team) {
					state = state.concat(" - KickOff");
				} else {
					state = state.concat(" - Op. KickOff");
				}
			}

			lbState.setText(state);

			int remaining = rgcd.getEstimatedSecs();
			int min = remaining / 60;
			int secs = remaining % 60;
			lbRemaining.setText(String.valueOf(min) + ":" + String.valueOf(secs));

			lbScore.setText(String.valueOf(rgcd.getScore(team)) + ":" + String.valueOf(rgcd.getScore(opponent)));
		}
	}

	public void windowClosing(WindowEvent e) {
		listener.stop();
		broadcast.socketCleanup();
		frame.dispose();
	}

}
