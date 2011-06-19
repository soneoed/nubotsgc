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

public class StandardPlatformLeagueRuleBook extends RuleBook {

	protected void setup() {
		setApplicationTitle("RoboCup SPL GameController");
		setConfigDirectory("spl");
		setNumPlayers(4);

		setTimeSeconds(600);
		setReadySeconds(45);
		setAutomaticReady2Set(true);
		setTimeOutSeconds(300);
		setPenaltyShootSeconds(60);
		setHalfTimeSeconds(600);
		setKickOffTeamColor(Constants.TEAM_BLUE);
		setSwitchTeamColorBetweenHalfs(true);

		// the team color used to be red, but the current uniform color is pink
		setupTeams("Blue", "Red", new Color(0, 81, 255), Color.PINK);
		setupGoals("Blue Goal", "Yellow Goal", false);

		registerPenalty(Constants.PENALTY_SPL_BALL_HOLDING, "Ball Holding", 30);
		registerPenalty(Constants.PENALTY_SPL_PLAYER_PUSHING, "Player Pushing", 30);
		//registerPenalty(Constants.PENALTY_SPL_OBSTRUCTION, "Obstruction", 30);
		registerPenalty(Constants.PENALTY_SPL_INACTIVE_PLAYER, "Inactive Player", 30);
		registerPenalty(Constants.PENALTY_SPL_ILLEGAL_DEFENDER, "Illegal Defender", 30);
		registerPenalty(Constants.PENALTY_SPL_LEAVING_THE_FIELD, "Leaving the Field", 30);
		registerPenalty(Constants.PENALTY_SPL_PLAYING_WITH_HANDS, "Playing with Hands", 30);
		registerPenalty(Constants.PENALTY_SPL_REQUEST_FOR_PICKUP, "Request for Pick-up", 30, false, true);

		setUsePushingCounter(true);
	}

}
