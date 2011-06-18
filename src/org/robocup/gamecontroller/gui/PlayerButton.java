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
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.robocup.common.Constants;

public class PlayerButton extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1942990813640148837L;

	protected int player;
	protected String color;
	protected long lastSeen = -1;
	protected long lastBlinkChange = 0;
	protected boolean blink = false;
	protected Color circleColor = UIManager.getColor("Button.background");

	protected short penalty = Constants.PENALTY_NONE;
	protected double seconds;

	enum Mode {
		modeNORMAL,
		modeREQUEST,		// additional mode for when a robot is autonomously requesting for a pickup
		modeUNPENALIZE
	};

	protected Mode mode;

	public PlayerButton(int player, String color) {
		this.player = player;
		this.color = color;
		mode = Mode.modeNORMAL;
		setFocusPainted(false);
		setOpaque(true);
		updateLabel();
	}
	
	public void resetPlayerSeen() {
		lastSeen = -1;
		updateLabel();
	}

	public void setPlayerSeen(long time) {
		lastSeen = time;
		updateLabel();
	}

	public void setPenalty(short penalty, double seconds) {
		this.penalty = penalty;
		this.seconds = seconds;
		
		if (mode == Mode.modeREQUEST)
			mode = (penalty == Constants.PENALTY_NONE) ? Mode.modeREQUEST : Mode.modeUNPENALIZE;
		else
			mode = (penalty == Constants.PENALTY_NONE) ? Mode.modeNORMAL : Mode.modeUNPENALIZE;

		updateLabel();
	}
	
	public void setRequest(boolean request) {
		if (mode == Mode.modeREQUEST && request == false)
			this.mode = Mode.modeNORMAL;
		else if (mode == Mode.modeNORMAL && request == true)
			this.mode = Mode.modeREQUEST;
		updateLabel();
	}
	
	/**
	 * getNextButtonIndex()
	 * 
	 * @return returns the next threshold for button color/blink change
	 */
	protected int getIndex(short[] threshold, double counter, boolean inv){
		for(int i = 0; i < threshold.length; i++)
			if(!inv && threshold[i] >= counter || inv && threshold[i] <= counter) return i; 
		return threshold.length - 1;
	}

	protected void updateLabel() {
		
		// blinking stuff
		int index = getIndex(Constants.PLAYER_BUTTON_THRESHOLD, seconds, false);
		if(lastBlinkChange + Constants.PLAYER_BUTTON_BLINK_INTERVAL[index] < System.currentTimeMillis() 
				&& Constants.PLAYER_BUTTON_BLINK_INTERVAL[index] != 0 // Blink when blink interval not set to 0
				&& penalty != Constants.PENALTY_SPL_REQUEST_FOR_PICKUP // Request for Pickup Penalty not blink
				&& penalty != Constants.PENALTY_MANUAL
				&& penalty != Constants.PENALTY_HL_KID_REQUEST_FOR_PICKUP
				&& penalty != Constants.PENALTY_HL_KID_REQUEST_FOR_SERVICE
				&& penalty != Constants.PENALTY_HL_KID_REQUEST_FOR_PICKUP_2_SERVICE) { // Manual penalty not blink
			blink = !blink;
			lastBlinkChange = System.currentTimeMillis();
		}
		else if(Constants.PLAYER_BUTTON_BLINK_INTERVAL[getIndex(Constants.PLAYER_BUTTON_THRESHOLD, seconds, false)] == 0) {
			blink = true;
		}	
		
		// color for last seen indicator
		long diff = System.currentTimeMillis() - lastSeen;
		circleColor = Constants.LASTSEEN_INDICATOR_COLORS[getIndex(Constants.LASTSEEN_INDICATOR_THRESHOLD, diff, true)];

		// modes
		switch (mode) {
			case modeNORMAL:
				this.setText("Player " + player);
				this.setToolTipText("Click to select " + color + " robot " + player);
				setBackground(UIManager.getColor("Button.background"));
				blink = true;
				break;
			case modeREQUEST:
				this.setText("<html><center>Player " + player + "</br> Request for pickup<center></html>");
				this.setToolTipText("Click to allow " + color + " robot " + player + " to be picked up");
				if (blink)
					setBackground(Color.YELLOW);
				else
					setBackground(UIManager.getColor("Button.background"));
				break;
			case modeUNPENALIZE:
				
				setEnabled(true);

				if (seconds == -1) { // ejected
					setText("<html><center>Player " + player + "<br/>Ejected</center></html>");
					setBackground(Color.RED);
				} else {
					DecimalFormat format = new DecimalFormat("0.0");
					setText("<html><center>Unpenalise<br/>Player " + player + "<br/>" + format.format(seconds) + "</center></html>");
					
					setToolTipText("Click to unpenalise " + color + " robot " + player);
				
					if(blink) setBackground(Constants.PLAYER_BUTTON_COLORS[getIndex(Constants.PLAYER_BUTTON_THRESHOLD, seconds, false)]);
					else setBackground(UIManager.getColor("Button.background"));				
				}
				
				break;
		}
		super.repaint(); // repaint for lastSeen indicator
	}
	
	// draw lastSeen indicator
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = 7;
        int padding = 2;
        int x = super.getWidth() - padding - w - 1;
        int y = padding;

        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x-1, y-1, w+2, w+2);
        g.setColor(circleColor);
        g.fillOval(x, y, w, w);
    }

}
