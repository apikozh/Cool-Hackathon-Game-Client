
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andrew
 */
public class StatsPanel extends javax.swing.JPanel {

    /**
     * Creates new form StatsPanel
     */
    public StatsPanel() {
        initComponents();
    }

    public void updateInfo(GameInfo game) {
        DefaultTableModel tm = (DefaultTableModel)statsTable.getModel();
        tm.setRowCount(game.getPlayers().size());
        int index = 0;
        for (Unit unit : game.getPlayers().values()) {
            tm.setValueAt(unit.getName().replace("_", " "), index, 0);
            tm.setValueAt(unit.getLives(), index, 1);
            tm.setValueAt(unit.getScore(), index, 2);
            tm.setValueAt(unit.getKills(), index, 3);
            index++;
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

        jScrollPane2 = new javax.swing.JScrollPane();
        statsTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane2.setPreferredSize(new java.awt.Dimension(100, 100));

        statsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Andrew",  new Integer(3),  new Integer(10010),  new Integer(10)},
                {"Sasha",  new Integer(1),  new Integer(1501),  new Integer(5)},
                {"Vova",  new Integer(6),  new Integer(3456),  new Integer(4)}
            },
            new String [] {
                "Player", "Lives", "Score", "Kills"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        statsTable.setEnabled(false);
        jScrollPane2.setViewportView(statsTable);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable statsTable;
    // End of variables declaration//GEN-END:variables
}
