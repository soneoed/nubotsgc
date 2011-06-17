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
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class TimeTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6466573553867644364L;

	public TimeTextField() {
		super();
		setHorizontalAlignment(JTextField.CENTER);
	}

	public void setSeconds(double seconds) {
		boolean negative = seconds < 0;
		seconds = Math.abs(seconds);
		if (negative) {
			setForeground(Color.RED);
			setDisabledTextColor(Color.RED);
		} else {
			setForeground(Color.BLACK);
			setDisabledTextColor(Color.GRAY);
		}
		int mins = ((int) Math.floor(seconds)) / 60;
		double secs = seconds % 60;

		DecimalFormat format = new DecimalFormat("00.0");
		setText((negative ? "-" : "") + (mins < 10 ? "0" : "") + mins + ":" + format.format(secs));
	}

	public Dimension getPreferredSize() {
		JLabel label = new JLabel(new DecimalFormat("00").format(-99) + ":" + new DecimalFormat("00.0").format(99.9), JLabel.CENTER);
		label.setFont(this.getFont());
		Dimension d = label.getPreferredSize();

		return new Dimension(d.width + 10, d.height);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
