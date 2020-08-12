/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WeightApplet.java
 *
 * Created on Jul 14, 2010, 1:55:59 PM
 */

package weightapplet;

import weightapplet.MainPanel;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
/**
 *
 * @author Dhruva
 */
public class WeightApplet extends javax.swing.JApplet {

    private boolean isStandalone = false;
    private MainPanel mpanel;
    private WeightApplet applet;
    //private JButton startButton;
    //private JLabel statusLabel;
    //private DESSolverApplet applet = null;

    /** Initializes the applet WeightApplet */
    public static void main(String[] args) {
        //MainPanel.main(null);
    }

    @Override
    public void init() {
        try {
            this.applet = this;
            initComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        startButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        startButton.setText("Start Simulator");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        getContentPane().add(startButton, new java.awt.GridBagConstraints());

        statusLabel.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(statusLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
         SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            // lets update the status of the Applet window
            AppletContext ac = getAppletContext();
            //ac.showStatus(res.getString("TXT_LOADING_APPLET"));
            statusLabel.setText("Loading");
            update(getGraphics());

            // lets create a kind a application and signal it that
            // this is an applet and do not need any frame at all.
            mpanel = new MainPanel(applet);

            if(mpanel != null)
            {
              //ac.showStatus("Loaded");
              statusLabel.setText("Loaded");
            }
          }
	});
    }//GEN-LAST:event_startButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton startButton;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}