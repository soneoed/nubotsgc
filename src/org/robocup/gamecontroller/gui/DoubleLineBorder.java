package org.robocup.gamecontroller.gui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;

import javax.swing.border.AbstractBorder;

public class DoubleLineBorder extends AbstractBorder
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Color lineColor;
	protected Color sndLineColor;
	
    public DoubleLineBorder(Color color) {
        lineColor = color;
        sndLineColor = color;
    }
    
    public DoubleLineBorder(Color color, Color sndColor) {
        lineColor = color;
        sndLineColor = sndColor;
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    	  g.setColor(sndLineColor);
          g.drawRect(x+1, y+1, width-3, height-3);
          g.drawRect(x+2, y+2, width-5, height-5);
          g.setColor(lineColor);
          g.drawRect(x, y, width-1, height-1);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(3,3,3,3);
    }

    public boolean isBorderOpaque() { 
        return false; 
    }
}