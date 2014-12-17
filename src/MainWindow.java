
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */

enum MoveAction {
    MOVE_UP,
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_DOWN
}

public class MainWindow extends javax.swing.JFrame implements IUpdatesListener {
    GameInfo game;
    SocketCommunicator communicator;
    boolean shooting, movingUp, movingLeft, movingRight, movingDown;
    LinkedBlockingQueue<MoveAction> moves = new LinkedBlockingQueue<>(5);
    int leftDelayForMovement;
    boolean realTimeMoving;
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {        
        game = new GameInfo();
        
        //setLocationRelativeTo(null);
        initComponents();
        ConnectionDialog dialog = new ConnectionDialog(this, true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        dialog.setVisible(true);
        String hostname = dialog.getServerAddress().getText();
        int port = 6123;
        try {
            Socket socket = new Socket(hostname, port);
            communicator = new SocketCommunicator(socket, game);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + hostname);
            JOptionPane.showMessageDialog(this, "Don't know about host: " + hostname, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
            JOptionPane.showMessageDialog(this, "Couldn't get I/O for the connection to: " + hostname, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        gamePanel.setGameMap(game);
        game.setUpdatesListener(this);
        playerPanel.setMainWindow(this);
        
        while (!communicator.isReady()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        
        game.setPlayerName(dialog.getPlayerName().getText().trim().replace(" ", "_"));
        game.notifyUpdatesListener(UpdateType.UPD_PLAYER_NAME);
        playerPanel.getPlayerName().setText(game.getPlayerName());
    }

    @Override
    public void infoUpdated(UpdateType type) {
        switch (type) {
            case UPD_ITERATION:
                PlayerInfo pi = game.getPlayerInfo();
                if (pi != null && pi.isAlive()) {
                    if (shooting && pi.getReload() == 0) {
                        //System.out.println("AAA");
                        communicator.sendCommand("shot");
                    }

                    if (leftDelayForMovement == 0) {
                        leftDelayForMovement = 10;

                        if (realTimeMoving) {
                            if (movingUp)
                                communicator.sendCommand("up");
                            if (movingLeft)
                                communicator.sendCommand("left");
                            if (movingRight)
                                communicator.sendCommand("right");
                            if (movingDown)
                                communicator.sendCommand("down");
                        }else{
                            MoveAction mv = moves.poll();
                            if (mv != null) {
                                switch (mv) {
                                    case MOVE_UP:
                                        communicator.sendCommand("up");
                                        break;
                                    case MOVE_LEFT:
                                        communicator.sendCommand("left");
                                        break;
                                    case MOVE_RIGHT:
                                        communicator.sendCommand("right");
                                        break;
                                    case MOVE_DOWN:
                                        communicator.sendCommand("down");
                                        break;
                                }
                            }
                        }
                    }else
                        leftDelayForMovement--;
                }
                break;

            case UPD_MAP_INFO:
                gamePanel.repaint();
                playerPanel.getStatsPanel().updateInfo(game);
                break;
            
            case UPD_PLAYER_INFO:
                playerPanel.updateInfo(game.getPlayerInfo());
                break;
            case UPD_PLAYER_NAME:
                communicator.sendCommand("name " + game.getPlayerName());
                break;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gamePanel = new GamePanel();
        playerPanel = new PlayerPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cool Hackathon Game Client");
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        gamePanel.setMinimumSize(new java.awt.Dimension(100, 100));
        gamePanel.setPreferredSize(new java.awt.Dimension(400, 400));

        javax.swing.GroupLayout gamePanelLayout = new javax.swing.GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 370, Short.MAX_VALUE)
        );
        gamePanelLayout.setVerticalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        getContentPane().add(gamePanel);

        playerPanel.setMaximumSize(new java.awt.Dimension(290, 32767));
        playerPanel.setMinimumSize(new java.awt.Dimension(290, 276));
        playerPanel.setPreferredSize(new java.awt.Dimension(290, 276));
        getContentPane().add(playerPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {

            case KeyEvent.VK_P:
                if (game.getPlayerInfo() == null) {
                    game.setPlayerInfo(new PlayerInfo());
                    communicator.sendCommand("play");
                }
                break;
            case KeyEvent.VK_L:
                if (game.getPlayerInfo() != null) {
                    communicator.sendCommand("leave");
                    /*try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    game.setPlayerInfo(null);
                    infoUpdated(UpdateType.UPD_PLAYER_INFO);*/
                }
                break;
            
            case KeyEvent.VK_UP:
                //communicator.sendCommand("up");
                if (!movingUp) {
                    moves.offer(MoveAction.MOVE_UP);
                    movingUp = true;
                }
                break;
            case KeyEvent.VK_LEFT:
                //communicator.sendCommand("left");
                if (!movingLeft) {
                    moves.offer(MoveAction.MOVE_LEFT);
                    movingLeft = true;
                }
                break;
            case KeyEvent.VK_RIGHT:
                //communicator.sendCommand("right");
                if (!movingRight) {
                    moves.offer(MoveAction.MOVE_RIGHT);
                    movingRight = true;
                }
                break;
            case KeyEvent.VK_DOWN:
                //communicator.sendCommand("down");
                if (!movingDown) {
                    moves.offer(MoveAction.MOVE_DOWN);
                    movingDown = true;
                }
                break;
            case KeyEvent.VK_Q:
                communicator.sendCommand("rotleft");
                break;
            case KeyEvent.VK_E:
                communicator.sendCommand("rotright");
                break;
            case KeyEvent.VK_W:
                shooting = true;
                //communicator.sendCommand("shot");
                break;

            case KeyEvent.VK_1:
                communicator.sendCommand("weapon 0");
                break;
            case KeyEvent.VK_2:
                communicator.sendCommand("weapon 1");
                break;
            case KeyEvent.VK_3:
                communicator.sendCommand("weapon 2");
                break;
            case KeyEvent.VK_4:
                communicator.sendCommand("weapon 3");
                break;
            case KeyEvent.VK_5:
                communicator.sendCommand("weapon 4");
                break;
            case KeyEvent.VK_6:
                communicator.sendCommand("weapon 5");
                break;
            case KeyEvent.VK_7:
                communicator.sendCommand("weapon 6");
                break;
            case KeyEvent.VK_8:
                communicator.sendCommand("weapon 7");
                break;
            case KeyEvent.VK_9:
                communicator.sendCommand("weapon 8");
                break;
            case KeyEvent.VK_0:
                communicator.sendCommand("weapon 9");
                break;
        } 
        
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {

            case KeyEvent.VK_UP:
                movingUp = false;
                break;
            case KeyEvent.VK_LEFT:
                movingLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                movingRight = false;
                break;
            case KeyEvent.VK_DOWN:
                movingDown = false;
                break;
            case KeyEvent.VK_Q:
                break;
            case KeyEvent.VK_E:
                break;
            case KeyEvent.VK_W:
                shooting = false;
                break;
            case KeyEvent.VK_R:
                realTimeMoving = !realTimeMoving;
                moves.clear();
                break;
        } 
    }//GEN-LAST:event_formKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        communicator.stop();
        //communicator.close();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private GamePanel gamePanel;
    private PlayerPanel playerPanel;
    // End of variables declaration//GEN-END:variables
}
