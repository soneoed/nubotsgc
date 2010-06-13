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

package org.robocup.gamecontroller.rules;

import java.awt.Color;

import org.robocup.gamecontroller.Constants;

public class HumanoidLeagueKidSizeRuleBook extends RuleBook {

	protected void setup() {
		setApplicationTitle("RoboCup HL-KidSize GameController");
		setConfigDirectory("hl-kid");
		setNumPlayers(3);

		setTimeSeconds(600);
		setReadySeconds(30);
		setTimeOutSeconds(120);
		setPenaltyShootSeconds(60);
		setHalfTimeSeconds(300);

		setupTeams("Cyan", "Magenta", Color.CYAN, Color.MAGENTA);
		setupGoals("Blue Goal", "Yellow Goal", true);

		registerPenalty(Constants.PENALTY_HL_KID_BALL_MANIPULATION, "Ball Manipulation", 0);
		registerPenalty(Constants.PENALTY_HL_KID_PHYSICAL_CONTACT, "Physical Contact", 10);
		registerPenalty(Constants.PENALTY_HL_KID_ILLEGAL_ATTACK, "Illegal Attack", 20);
		registerPenalty(Constants.PENALTY_HL_KID_ILLEGAL_DEFENSE, "Illegal Defense", 30);
		registerPenalty(Constants.PENALTY_HL_KID_REQUEST_FOR_PICKUP, "Request For PickUp", 60);
	}

}
