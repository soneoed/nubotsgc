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

import org.robocup.common.Constants;
import org.robocup.common.rules.RuleBook;

public class HumanoidLeagueTeenSizeRuleBook extends RuleBook {

	protected void setup() {
		setApplicationTitle("RoboCup HL-TeenSize GameController");
		setConfigDirectory("hl-teen");
		setNumPlayers(5);

		setDropBall(true);

		setTimeSeconds(600);
		setOvertimeSeconds(300);
		setReadySeconds(30);
		setTimeOutSeconds(120);
		setPenaltyShootSeconds(60);
		setHalfTimeSeconds(300);

		setSwitchTeamColorBetweenHalfs(false);

		setupTeams("Cyan", "Magenta", Color.CYAN, Color.MAGENTA);
		setupGoals("Blue Goal", "Yellow Goal", true);

		registerPenalty(Constants.PENALTY_HL_TEEN_BALL_MANIPULATION, "Ball Manipulation (30s)", 30);
		registerPenalty(Constants.PENALTY_HL_TEEN_PHYSICAL_CONTACT, "Physical Contact (30s)", 30);
		registerPenalty(Constants.PENALTY_HL_TEEN_ILLEGAL_ATTACK, "Illegal Attack (30s)", 30);
		registerPenalty(Constants.PENALTY_HL_TEEN_ILLEGAL_DEFENSE, "Illegal Defense (30s)", 30);
		registerPenalty(Constants.PENALTY_HL_TEEN_REQUEST_FOR_PICKUP, "Request For PickUp (30s)", 30);
		registerPenalty(Constants.PENALTY_HL_TEEN_REQUEST_FOR_SERVICE, "Request For Service (60s)", 60);
		registerPenalty(Constants.PENALTY_HL_TEEN_REQUEST_FOR_PICKUP_2_SERVICE, "Upgrade PickUp to Service (+30s)", 30, true);
	}

}
