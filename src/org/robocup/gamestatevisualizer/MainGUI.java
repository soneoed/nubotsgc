package org.robocup.gamestatevisualizer;

/*
Copyright (C) 2009  University of Bremen

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.robocup.common.Constants;
import org.robocup.common.data.GameState;
import org.robocup.common.util.LogFormatter;
import org.robocup.gamestatevisualizer.net.Listen;
import org.robocup.gamestatevisualizer.net.ListenSSL;

/**
* The main GUI of the application.
*
* @author rieskamp@tzi.de
*/
public class MainGUI extends JFrame implements KeyListener{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("org.robocup.gamestatevisualizer.maingui");
	FileHandler logHandler = null;

	  int w, h;
	  int f;
	
	  private GameState gameData;
	  Image imageTeamRed;
	  Image imageTeamBlue;
	  private Date timeWhenLastPacketReceived;
	  Image bgImage;
	  private HashMap<String, String> teams = new HashMap<String, String>();
	  private HashMap<String, Image> teamImages = new HashMap<String, Image>();
	  private HashMap<String, Vector<String>> penaltyStates = new HashMap<String, Vector<String>>();
	  private boolean penaltyDisplay;
	  private int mode;
	  private String configDir;
	  private boolean debug;
	  
	public static final byte TEAM_1 = Constants.TEAM_BLUE;
	public static final byte TEAM_2 = Constants.TEAM_RED;  

  /**
   *
   * @param port
   */

  public MainGUI(int port, boolean penaltyDisplay, int mode, GameState gameData,
          int display, boolean fullscreen, String configDir, boolean debug) {
      super("RoboCup GameStateVisualizer",
            GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[display]
            .getDefaultConfiguration());

  	// log handler
  	try {
  		logHandler = new FileHandler(Constants.LOG_FILENAME_GSV, true); // append to logfile
  		logger.addHandler(logHandler);
  		logger.setLevel(Level.INFO);
  		LogFormatter formatter = new LogFormatter();
  	    logHandler.setFormatter(formatter);
  	} catch (IOException e) {

  	}   
  	
      System.out.println("Initializing MainGUI");

      this.mode = mode;
      this.configDir = configDir;
      this.gameData = gameData;
      this.penaltyDisplay = penaltyDisplay;
      this.debug = debug;
      timeWhenLastPacketReceived = new Date(0);
      
      this.addKeyListener(this);
      
      try {
          initDisplay(display, fullscreen);
      }
      catch(Exception e) {
          System.err.print(e);
      }
      
   

      // Load Properties
      Properties properties = new Properties();
      try {
          FileInputStream fis = new FileInputStream(getLeague(mode) + File.separator + "teams.cfg");
          properties.load(fis);
          for(int i=1;;i++) {
              String team = properties.getProperty(""+i, "unknown");
              if("unknown".equals(team))
                break;
              teams.put(""+i, team);

              String[] imageTypes = {".gif", ".jpg", ".png"};
              Image image = null;
              for(int j=0; j<imageTypes.length && image == null; j++) {
                  String sourceDir = getLeague(mode);
                  String source = sourceDir + File.separator + i + imageTypes[j];
                  File file = new File(source);
                  if(file.exists())
                      image = ImageIO.read(file);
              }

              if(image != null) {
                  assert image.getWidth(this) > 0 && image.getHeight(this) > 0;
                  int width  = w / 3;
                  int height = h / 3;
                  if (width * image.getHeight(this) < height * image.getWidth(this))
                      image = image.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
                  else
                      image = image.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
              }

              teamImages.put(""+i, image);
          }
      }
      catch(IOException e) {
          logger.info("Error loading config file");
          System.exit(0);
      }


      Thread listenThread;
      if (mode == Constants.MODE_SSL) {
          listenThread = new Thread(new ListenSSL(10001, this));
      }
      else {
          listenThread = new Thread(new Listen(port, this));
      }

      // start the listening thread
      listenThread.start();      
  }

  /**
   *
   * @throws Exception
   */
  private void initDisplay(int display, boolean fullscreen) throws Exception {
      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setName("frmMain");

      // remove window frame
      this.setUndecorated(true);

      // setting size to fullscreen
      GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
      setSize(gs[display].getDefaultConfiguration().getBounds().getSize());

      // getting display resolution: width and height
      w = this.getWidth();
      h = this.getHeight();
      f = w * 9 / 16;
      System.out.println("Display resolution: " + String.valueOf(w) + "x" + String.valueOf(h));

      // load bgimage
      String sourceDir = getLeague(mode);
      
      String[] imageTypes = {".gif", ".jpg", ".png"};
      Image image = null;
      for(int j=0; j<imageTypes.length && image == null; j++) {
          String source = sourceDir + File.separator  + "background" + imageTypes[j];
          File file = new File(source);
          if(file.exists()){
              bgImage = Toolkit.getDefaultToolkit().getImage(sourceDir + File.separator + "background" + imageTypes[j]);
              break;
          }
      }
      
      if(bgImage != null) {
          bgImage = bgImage.getScaledInstance(w, -1, Image.SCALE_SMOOTH);
      }

      if (fullscreen) {
          GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[display].setFullScreenWindow(this);
      }

      this.setBackground(Color.WHITE);
      this.setVisible(true);
      this.createBufferStrategy(2);
  }


  private void paintBackground(Graphics g) {
      g.drawImage(bgImage, 0, 0, this);
  }

  private void paintTeamImages(Graphics g) {
	  for(byte i = 0; i <= 1; i++) {
	      Image image = teamImages.get(""+gameData.getTeamNumber(i));
	      if (image != null) {
	    	  if(gameData.getHalf() && i == 0 || !gameData.getHalf() && i == 1)
	              g.drawImage(image, w / 6 - image.getWidth(this) / 2 + 5, 5 * h / 6 - image.getHeight(this) / 2 - 5, this);
	          else
	              g.drawImage(image, 5 * w / 6 - image.getWidth(this) / 2 - 5, 5 * h / 6 - image.getHeight(this) / 2 - 5, this);
	      }		  
	  }
  }

  private void paintGameState(Graphics g) {
      Font fontSmall = new Font (Font.DIALOG, Font.BOLD, f / 13);
      Font fontMiddle = new Font (Font.DIALOG, Font.BOLD, f / 8);
      Font fontBig = new Font (Font.DIALOG, Font.BOLD, 7 * f / 20);  
      Font fontPenalty = new Font(Font.DIALOG, Font.BOLD, f / 16);
      FontMetrics fm;
      
      Color[] teamColor = new Color[2];
      if (mode == Constants.MODE_SPL) {
    	  teamColor[TEAM_1] = Color.BLUE;
    	  teamColor[TEAM_2] = Color.PINK;
      } else if (mode == Constants.MODE_HLKID || mode == Constants.MODE_HLTEEN) {
    	  teamColor[TEAM_1] = Color.CYAN;
    	  teamColor[TEAM_2] = Color.MAGENTA;
      } else if (mode == Constants.MODE_SSL) {
    	  teamColor[TEAM_1] = Color.BLUE;
    	  teamColor[TEAM_2] = Color.ORANGE;
      } else {
    	  logger.info("paintGameState(): unknown mode");
          System.exit(1);
      }

      for(byte i = 0; i <= 1; i++) {
    	  String teamName = teams.get(""+gameData.getTeamNumber((byte) i));
          if(teamName != null) {
              g.setFont(fontSmall);
              fm = g.getFontMetrics();
              g.setColor(teamColor[i]);
              
              if(gameData.getHalf() && i == 0 || !gameData.getHalf() && i == 1) {
                  if(fm.stringWidth(teamName) > w / 3) // left-aligned
                      g.drawString(teamName, 5, 3 * h / 5);
                  else // centered
                      g.drawString(teamName, w / 6 - (fm.stringWidth(teamName) / 2) + 5 , 3 * h / 5);
              }
              else {
                  if(fm.stringWidth(teamName) > w / 3) // right-aligned
                      g.drawString(teamName, w - 5 - fm.stringWidth(teamName), 3 * h / 5);
                  else // centered
                      g.drawString(teamName, 5 * w / 6 - (fm.stringWidth(teamName) / 2) - 5, 3 * h / 5);
              }
          } 
          
          g.setFont(fontBig);
          fm = g.getFontMetrics();
          
          if(timeWhenLastPacketReceived.getTime() > 0) {
              String score = ""+gameData.getScore((byte) i);
        	  
              if(gameData.getHalf() && i == 0 || !gameData.getHalf() && i == 1)
                  g.drawString(score, w / 3 - (fm.stringWidth(score) / 2), 1 * h / 2);
              else
                  g.drawString(score, 2 * w / 3 - (fm.stringWidth(score) / 2), 1 * h / 2);
        	  
          }

          if(gameData.getSecondaryGameState() == Constants.STATE2_PENALTYSHOOT && penaltyDisplay) {
              g.setFont(fontPenalty);
              fm = g.getFontMetrics();        	  
              Vector<String> states = this.getPenaltyStates().get(""+(int)gameData.getTeamNumber(i));
              if(states != null) {
                  for(int j=0; j<states.size(); j++) {
                      String str = states.get(j);
                      if(gameData.getHalf() && i == 0 || !gameData.getHalf() && i == 1)
                          g.drawString(str, 5 + j * (fm.stringWidth(str) + 5), h / 2);
                      else
                          g.drawString(str, w - (j+1) * (fm.stringWidth(str) + 5) - 5, h / 2);
                  }
              }          
          }
      }
      
      if(timeWhenLastPacketReceived.getTime() > 0) {

          g.setFont(fontBig);
          fm = g.getFontMetrics();
          
          // Score DEVIDER
          g.setColor(Color.BLACK);
          g.drawString(":", w / 2 - (fm.stringWidth(":") / 2), 1 * h / 2);
          
          g.setFont(fontMiddle);
          fm = g.getFontMetrics();
          // Half
          g.setColor(Color.BLACK);
          
          String half = "";
          if(gameData.getSecondaryGameState() == Constants.STATE2_NORMAL) {
              if(gameData.getHalf())
                  half = "1st Half";
              else
                  half = "2nd Half";

              g.drawString(half, w / 2 - (fm.stringWidth(half) / 2), 4 * h / 5);
          }
          else if(gameData.getSecondaryGameState() == Constants.STATE2_OVERTIME) { 
              if(gameData.getHalf())
                  half = "1st";
              else
                  half = "2nd";

              g.drawString(half, w / 2 - (fm.stringWidth(half) / 2),(int)( 3.7 * h / 5));
              g.drawString("Overtime", w / 2 - (fm.stringWidth("Overtime") / 2),(int)( 4.1 * h / 5));       	  
          }
          else {
              half = "Penalty";
              g.setFont(new Font (Font.DIALOG, Font.BOLD, f / 11));
              fm = g.getFontMetrics();
              g.drawString(half, w / 2 - (fm.stringWidth(half) / 2),(int)( 3.7 * h / 5));
              g.drawString("Shoot-out", w / 2 - (fm.stringWidth("Shoot-out") / 2),(int)( 4.1 * h / 5));
          }

          g.setFont(fontMiddle);
          fm = g.getFontMetrics();

          // Time
          SimpleDateFormat format = new SimpleDateFormat("mm:ss");
          String time = format.format(new Date(gameData.getEstimatedSecs()*1000));
          g.drawString(time, w / 2 - (fm.stringWidth(time) / 2), 19 * h / 20);
      }
  }

  public void update(Graphics g) {
      BufferStrategy bs = this.getBufferStrategy();
      if (bs != null)
          g = bs.getDrawGraphics();
      g.clearRect(0, 0, w, h);
      paintGameState(g);
      g.dispose();
      if (bs != null)
          bs.show();
      Toolkit.getDefaultToolkit().sync();
  }

  public void paint(Graphics g) {
      BufferStrategy bs = this.getBufferStrategy();
      if (bs != null)
          g = bs.getDrawGraphics();
      g.clearRect(0, 0, w, h);
      paintBackground(g);
      paintTeamImages(g);
      paintGameState(g);
      g.dispose();
      if (bs != null)
          bs.show();
      Toolkit.getDefaultToolkit().sync();
  }

  public GameState getGameData() {
      return gameData;
  }

  public void setTimeWhenLastPacketReceived(Date timeWhenLastPacketReceived) {
      this.timeWhenLastPacketReceived = timeWhenLastPacketReceived;
  }

  public HashMap<String, Vector<String>> getPenaltyStates() {
      return penaltyStates;
  }

  private String getLeague(int mode) {
	  if(this.configDir != null)
		  return "config/" + this.configDir;
	  else if(mode == Constants.MODE_SPL)
          return "config/spl";
      else if(mode == Constants.MODE_HLKID)
          return "config/hl-kid";
      else if(mode == Constants.MODE_HLTEEN)
          return "config/hl-teen";
      else if(mode == Constants.MODE_SSL)
          return "config/ssl";
      return null;
  }
  
  public boolean getDebug() {
	  return this.debug;
  }

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 121) // F10
			System.exit(0);		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {	
	}
}
