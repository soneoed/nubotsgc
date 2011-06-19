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

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.robocup.common.Constants;
import org.robocup.common.data.GameState;
import org.robocup.common.data.RobotInfo;
import org.robocup.gamecontroller.data.RobotState;
import org.robocup.gamecontroller.net.Broadcast;
import org.robocup.gamecontroller.net.Listener;

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

	protected JLabel lbKickoff;
	protected JLabel lbState;
	protected JLabel lbTeam;
	protected JLabel lbGoal;
	protected JLabel lbRobot;
	protected JLabel lbPenalty;
	protected JLabel lbRemaining;
	protected JLabel lbPeriod;
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

		mainPane.add(new JLabel("Kickoff:"));
		lbKickoff = new JLabel("-");
		mainPane.add(lbKickoff);

		mainPane.add(new JLabel("State:"));
		lbState = new JLabel("-");
		mainPane.add(lbState);

		mainPane.add(new JLabel("Team:"));
		lbTeam = new JLabel("-");
		mainPane.add(lbTeam);

		mainPane.add(new JLabel("Goal:"));
		lbGoal = new JLabel("-");
		mainPane.add(lbGoal);

		mainPane.add(new JLabel("Robot:"));
		lbRobot = new JLabel("-");
		mainPane.add(lbRobot);

		mainPane.add(new JLabel("Penalty:"));
		lbPenalty = new JLabel("-");
		mainPane.add(lbPenalty);

		mainPane.add(new JLabel("Time:"));
		lbRemaining = new JLabel("--:--");
		mainPane.add(lbRemaining);

		mainPane.add(new JLabel("Period:"));
		lbPeriod = new JLabel("--:--");
		mainPane.add(lbPeriod);

		mainPane.add(new JLabel("Score:"));
		lbScore = new JLabel("-:-");
		mainPane.add(lbScore);

		frame.setBounds(10, 10, 300, 200);
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
			String kickoff = "";
			if (rgcd.getKickOffTeam() == Constants.TEAM_BLUE) {
				kickoff = "Blue";
			} else if (rgcd.getKickOffTeam() == Constants.TEAM_RED) {
				kickoff = "Red";
			} else if (rgcd.getKickOffTeam() == Constants.DROPBALL) {
				kickoff = "Drop ball";
			}
			lbKickoff.setText(kickoff);

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

			lbTeam.setText(String.valueOf((int) rgcd.getTeamNumber(team)) + " - " + (team == Constants.TEAM_CYAN ? "cyan" : "magenta"));
			lbGoal.setText(rgcd.getGoalColour(team) == Constants.GOAL_BLUE ? "blue" : "yellow");
			lbRobot.setText(String.valueOf(robotId));

			String penalty = "none";
			byte robot = (byte)(robotId - 1);
			RobotInfo robotInfo = rgcd.getTeam(team).getPlayer(robot);
			switch (robotInfo.getPenalty()) {
				/*case Constants.PENALTY_SPL_BALL_HOLDING:
					penalty = "Ball holding";
					break;
				case Constants.PENALTY_SPL_PLAYER_PUSHING:
					penalty = "Player pushing";
					break;
				case Constants.PENALTY_SPL_OBSTRUCTION:
					penalty = "Obstruction";
					break;
				case Constants.PENALTY_SPL_INACTIVE_PLAYER:
					penalty = "Inactive player";
					break;
				case Constants.PENALTY_SPL_ILLEGAL_DEFENDER:
					penalty = "Illegal defender";
					break;
				case Constants.PENALTY_SPL_LEAVING_THE_FIELD:
					penalty = "Leaving the field";
					break;*/
				case Constants.PENALTY_SPL_PLAYING_WITH_HANDS:
					penalty = "Playing with hands";
					break;
				//case Constants.PENALTY_SPL_REQUEST_FOR_PICKUP:
				case Constants.PENALTY_HL_KID_REQUEST_FOR_PICKUP:
				//case Constants.PENALTY_HL_TEEN_REQUEST_FOR_PICKUP:
					penalty = "Request for pickup";
					break;
				case Constants.PENALTY_HL_KID_BALL_MANIPULATION:
				//case Constants.PENALTY_HL_TEEN_BALL_MANIPULATION:
					penalty = "Ball manipulation";
					break;
				case Constants.PENALTY_HL_KID_PHYSICAL_CONTACT:
				//case Constants.PENALTY_HL_TEEN_PHYSICAL_CONTACT:
					penalty = "Physical contact";
					break;
				case Constants.PENALTY_HL_KID_ILLEGAL_ATTACK:
				//case Constants.PENALTY_HL_TEEN_ILLEGAL_ATTACK:
					penalty = "Illegal attack";
					break;
				case Constants.PENALTY_HL_KID_ILLEGAL_DEFENSE:
				//case Constants.PENALTY_HL_TEEN_ILLEGAL_DEFENSE:
					penalty = "Illegal defense";
					break;
				case Constants.PENALTY_HL_KID_REQUEST_FOR_SERVICE:
				//case Constants.PENALTY_HL_TEEN_REQUEST_FOR_SERVICE:
					penalty = "Request for service";
					break;

				case Constants.PENALTY_MANUAL:
					penalty = "Manual";
					break;
			}
			if (penalty != "") {
				penalty += " - " + String.valueOf(robotInfo.getSecsTillUnpenalised()) + "s";
			}
			lbPenalty.setText(penalty);

			int remaining = rgcd.getEstimatedSecs();
			int min = remaining / 60;
			int secs = remaining % 60;
			lbRemaining.setText(String.valueOf(min) + ":" + String.valueOf(secs));

			String period = "";
			switch (rgcd.getSecondaryGameState()) {
				case Constants.STATE2_NORMAL:
					period = "Halftime";
					break;
				case Constants.STATE2_PENALTYSHOOT:
					period = "Penalty shoot";
					break;
				case Constants.STATE2_OVERTIME:
					period = "Overtime";
					break;
				default:
					period = "unknown";
					break;
			}
			period += " - ";
			if (rgcd.getHalf()) {
				period += "First";
			} else {
				period += "Second";
			}
			lbPeriod.setText(period);
			
			lbScore.setText(String.valueOf(rgcd.getScore(team)) + ":" + String.valueOf(rgcd.getScore(opponent)));
		}
	}

	public void windowClosing(WindowEvent e) {
		listener.stop();
		broadcast.socketCleanup();
		frame.dispose();
	}

}
