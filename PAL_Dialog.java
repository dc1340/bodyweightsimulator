/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PAL_Dialog.java
 *
 * Created on Jun 11, 2010, 11:33:54 AM
 */

package weightapplet;

import weightapplet.MainPanel;
import weightapplet.*;


/**
 *
 * @author Dhruva
 */
public class PAL_Dialog extends javax.swing.JDialog {

    private MainPanel mpanel;
    private java.util.HashMap hash;
    private String leisure="Moderate", work="Moderate";

    /** Creates new form PAL_Dialog */
    
    
    public PAL_Dialog(java.awt.Frame parent, boolean modal, MainPanel mpanel) {
        super(parent, modal);
        this.mpanel=mpanel;
        inithash();
        initComponents();

    }

    private void inithash(){
        hash =new java.util.HashMap();
        hash.put("Very light, Very light", 1.4);
        hash.put("Very light, Light", 1.5);
        hash.put("Very light, Moderate", 1.6);
        hash.put("Very light, Heavy", 1.7);

        hash.put("Light, Very light", 1.5);
        hash.put("Light, Light", 1.6);
        hash.put("Light, Moderate", 1.7);
        hash.put("Light, Heavy", 1.8);

        hash.put("Moderate, Very light", 1.6);
        hash.put("Moderate, Light", 1.7);
        hash.put("Moderate, Moderate", 1.8);
        hash.put("Moderate, Heavy", 1.9);

        hash.put("Active, Very light", 1.7);
        hash.put("Active, Light", 1.8);
        hash.put("Active, Moderate", 1.9);
        hash.put("Active, Heavy", 2.1);

        hash.put("Very active, Very light", 1.9);
        hash.put("Very active, Light", 2.0);
        hash.put("Very active, Moderate", 2.2);
        hash.put("Very active, Heavy", 2.3);
    }

   
   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        Work_Combo = new javax.swing.JComboBox();
        Leisure_Combo = new javax.swing.JComboBox();
        EstimatePALButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        SpaceLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        SpaceLabel1 = new javax.swing.JLabel();
        SpaceLabel3 = new javax.swing.JLabel();
        CancelButton = new javax.swing.JButton();

        setTitle("Estimate Your Physical Activity Level");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        Work_Combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Very light, e.g. sitting at the computer most of the day or sitting at a desk", "Light, e.g. light industrial work, sales or office work that comprises light activities", "Moderate, e.g. cleaning,  kitchen staff, or delivering mail on foot or by bicycle", "Heavy, e.g. heavy industrial work, construction work or farming" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(Work_Combo, gridBagConstraints);

        Leisure_Combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Very light: almost no activity at all", "Light, e.g. walking, nonstrenuous cycling or gardening approximately once a week", "Moderate: regular activity at least once a week, e.g., walking or bicycling (including to work), or gardening ", "Active: regular activities more than once a week, e.g., intense walking or bicycling or sports", "Very Active: strenuous activities several times a week" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(Leisure_Combo, gridBagConstraints);

        EstimatePALButton.setText("Estimate Initial Physical Activity Level");
        EstimatePALButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EstimatePALButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(EstimatePALButton, gridBagConstraints);

        jLabel2.setText("Describe your physical activity at leisure time (if the activities vary between summer and winter, try to give a mean estimate):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel2, gridBagConstraints);

        SpaceLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        SpaceLabel.setText("     ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(SpaceLabel, gridBagConstraints);

        jLabel6.setText("Describe your physical activity at work (including work at home, sick leave and schoolwork):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel6, gridBagConstraints);

        SpaceLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        SpaceLabel1.setText("     ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(SpaceLabel1, gridBagConstraints);

        SpaceLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        SpaceLabel3.setText("     ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(SpaceLabel3, gridBagConstraints);

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        getContentPane().add(CancelButton, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void EstimatePALButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EstimatePALButtonActionPerformed
        this.setVisible(false);
        mpanel.getBaseline().setPAL(lookupPAL());
        mpanel.getBaseline().print();
        mpanel.syncPAL();
        mpanel.recalc();
        
    }//GEN-LAST:event_EstimatePALButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_CancelButtonActionPerformed

    private double lookupPAL(){
        
        double PAL;

        setleisure_string();
        setwork_string();
        String key=leisure+", "+work;

        try{
            PAL=Double.parseDouble(hash.get(key).toString());
        }catch (Exception e){
            PAL=1.3;
        }

        //System.out.println(key);
        //System.out.println(PAL);
        
        
        return PAL;
    }

    private void setleisure_string(){
        
        int index=Leisure_Combo.getSelectedIndex();
        //System.out.println("leisure index "+index);
        if (index==0){
            leisure="Very light";
        }else if(index==1){
            leisure="Light";
        }else if(index==2){
            leisure="Moderate";
        }else if(index==3){
            leisure="Active";
        }else if(index==4){
            leisure="Very active";
        }
        
    }

    private void setwork_string(){
        
        int index=Work_Combo.getSelectedIndex();
        //System.out.println("work index "+index);
        if (index==0){
            work="Very light";
        }else if(index==1){
            work="Light";
        }else if(index==2){
            work="Moderate";
        }else if(index==3){
            work="Heavy";
        }
        
    }
    /**
    * @param args the command line arguments
    */
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton EstimatePALButton;
    private javax.swing.JComboBox Leisure_Combo;
    private javax.swing.JLabel SpaceLabel;
    private javax.swing.JLabel SpaceLabel1;
    private javax.swing.JLabel SpaceLabel3;
    private javax.swing.JComboBox Work_Combo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables

}