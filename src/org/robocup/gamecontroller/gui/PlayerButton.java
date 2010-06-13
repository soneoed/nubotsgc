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

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.robocup.gamecontroller.Constants;

public class PlayerButton extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1942990813640148837L;

	protected int player;
	protected String color;
	protected double lastSeen = -1;

	protected short penalty = Constants.PENALTY_NONE;
	protected double seconds;

	enum Mode {
		modeNORMAL,
		modeUNPENALIZE
	};

	protected Mode mode;

	public PlayerButton(int player, String color) {
		this.player = player;
		this.color = color;
		mode = Mode.modeNORMAL;
		setFocusPainted(false);
		updateLabel();
	}

	public void resetPlayerSeen() {
		lastSeen = -1;
		updateLabel();
	}

	public void setPlayerSeen() {
		lastSeen = 0;
		updateLabel();
	}

	public void updateLastSeen(double diff) {
		if (lastSeen >= 0) {
			lastSeen += diff;
			updateLabel();
		}
	}

	public void setPenalty(short penalty, double seconds) {
		this.penalty = penalty;
		this.seconds = seconds;

		if (penalty == Constants.PENALTY_NONE) {
			mode = Mode.modeNORMAL;
		} else {
			mode = Mode.modeUNPENALIZE;
		}

		updateLabel();
	}

	protected void updateLabel() {
		switch (mode) {
			case modeNORMAL:

				String suffix = "";
				if (lastSeen >= 0) {
					DecimalFormat format = new DecimalFormat("0.0");
					suffix = " (" + format.format(lastSeen) + ")";
				}
				this.setText("Player " + player + suffix);
				this.setToolTipText("Click to select " + color + " robot " + player);

				if (lastSeen >= 0) {
					if (lastSeen < 10.0) {
						setBackground(Color.GREEN);
					} else {
						setBackground(Color.RED);
					}
				} else {
					// setBackground(Color.YELLOW);
					setBackground(UIManager.getColor("Button.background"));
				}

				// setSelected(false);

				break;

			case modeUNPENALIZE:

				setEnabled(true);

				if (seconds == -1) { // ejected
					setText("<html><center>Player " + player + "<br/>Ejected</center></html>");
				} else {
					DecimalFormat format = new DecimalFormat("0.0");
					setText("<html><center>Unpenalise<br/>Player " + player + "<br/>" + format.format(seconds) + "</center></html>");
				}
				setToolTipText("Click to unpenalise " + color + " robot " + player);

				if (seconds == 0) {
					setBackground(Color.GREEN);
				} else if (seconds > 0 && seconds < 5) {
					setBackground(Color.YELLOW);
				} else {
					setBackground(Color.RED);
				}

				break;
		}
	}

}
