package org.robocup.gamestatevisualizer.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import org.robocup.common.Constants;
import org.robocup.common.data.GameState;
import org.robocup.gamestatevisualizer.MainGUI;

public class ListenSSL implements Runnable {
    private boolean stopController = false;

    private MainGUI gui;

    // some objects for networking
    private MulticastSocket listen;      // socket for broadcasting
    private ByteBuffer packetData;      // the packet data
    private DatagramPacket packet;
    private byte lastCounter;
    private int gameHalf; //0(unknown), 1(first), 2(second)
    static final int packetSize = 6;   // the size of the return packet

    public ListenSSL(int port, MainGUI gui) {
        lastCounter = 0;
        gameHalf = 0;
        byte[] buf = new byte[packetSize];
        packet = new DatagramPacket(buf, buf.length);
        packetData = ByteBuffer.allocate(packetSize * 3);
        packetData.order(ByteOrder.BIG_ENDIAN);
        this.gui = gui;

        try {
            InetAddress group = InetAddress.getByName("224.5.23.1");
            listen = new MulticastSocket(10001);
            listen.joinGroup(group);
        } catch (Exception err) {
            System.err.println("GameStateVisualizer (constructor) error: " +
                               err.getMessage() + " " +
                               "(No network devices set up?)");
            System.exit(1);
        }
    }

    // broadcast loop runs in a separate thread, this is stopped by socketCleanup
    public void run() {
        while (!stopController) {
            try {
                listen.receive(packet);

                if (gui.getDebug()) { System.out.println("Incoming packet"); }
                if (packet.getLength() != packetSize) {
                    if (gui.getDebug()) { System.out.println("Bad size"); }
                    continue;
                }

                packetData.clear();
                packetData.put(packet.getData());
                packetData.rewind();

                GameState gameData = gui.getGameData();
                byte lastCommand = packetData.get();
                byte counter = packetData.get(); //to detect transitions
                byte goalsBlue = packetData.get();
                byte goalsYellow = packetData.get();
                short secsRemaining = packetData.getShort();

                //state changes
                if (counter != lastCounter) {
                    if (lastCommand == '1')
                        gameHalf = 1;
                    else if (lastCommand == '2')
                        gameHalf = 2;
                }

                lastCounter = counter;
                //        gameData.setGameState(gameState);
                //        gameData.getTeam(Constants.TEAM_BLUE).setTeamNumber(team1Number);
                gameData.getTeam(Constants.TEAM_BLUE).setTeamScore(goalsBlue);
                gameData.getTeam(Constants.TEAM_RED).setTeamScore(goalsYellow); //yellow is the new red
                gameData.setEstimatedSecs(secsRemaining, false);

                //todo: gamestate
                //gameData.setGameState(...)

                //guess half:
                if (gameHalf == 1)
                    gameData.setHalf(true);
                else if (gameHalf == 2)
                    gameData.setHalf(false); //??

                gui.setTimeWhenLastPacketReceived(new Date());
                gui.repaint();
            } catch (IOException e) {
                if (!stopController) {
                    System.err.println("IOException while listening for packets: " + e);
                }
            }
        }
    }

    // called by MainGUI when program is closing
    public void socketCleanup() {
        try {
            if (gui.getDebug()) { System.out.println("Stopping listen thread"); }
            stopController = true;
            if (gui.getDebug()) { System.out.println("Closing listen socket"); }
            listen.close();
        } catch (Exception err) {
            System.err.println("socketCleanup error: " + err.getMessage());
            System.exit(1);
        }
    }
}
