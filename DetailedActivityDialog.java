/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DetailedActivityDialog.java
 *
 * Created on Nov 24, 2010, 1:29:41 PM
 */

package weightapplet;

import weightapplet.MainPanel;
import weightapplet.*;
import weightapplet.Intervention;
import java.awt.Color;
import java.util.Vector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author Dhruva
 */
public class DetailedActivityDialog extends javax.swing.JDialog {

    private Color activecolor=Color.black;
    private Color inactivecolor=Color.gray;
    private MainPanel mpanel;
    
    private int index;

    private double metmin_total;
    private int activityindex=4;
    private double weightunits=1;
    private double energyunits=1;
    private double actchangeperc;
    private double oldactchangeperc;
    private Object oldboxvalue;
    private boolean liveupdating=false;
    


    java.awt.Component[] comboxlist;
    Object[] statelist;

    java.util.Vector  combostatevector;
    java.util.Vector  combovector;
    java.util.Vector<Boolean>  checkstatevector;
    java.util.Vector  checkvector;


    /** Creates new form DetailedActivityDialog */
    
    
    public DetailedActivityDialog(java.awt.Frame parent, boolean modal, MainPanel mainpanel, String statement, int index) {
        super(parent, false);
        initComponents();
        mpanel=mainpanel;
        StatementLabel.setText(statement);
        this.index=index;
        if (index==3 || index==4) {
            liveupdating=false;
            LiveUpdatingCheckBox.setSelected(false);
            LiveUpdatingCheckBox.setVisible(false);
        }

        boxvector();
        update();
        remember();
    }

    
    private void boxvector(){
        //System.out.println("Making boxvector");
        //List list=Arrays.asList(this.getComponents());
        java.awt.Component[] complist=this.getContentPane().getComponents();
        combovector=new Vector();
        checkvector=new Vector();

        for (int i=0; i<complist.length; i++){
            //System.out.println(complist[i].getClass().getName());
            if (complist[i].getClass().getName().contains("Combo")){
                combovector.add(complist[i]);
                //System.out.println(complist[i].getClass().getName());
            }

            if (complist[i].getClass().getName().contains("Check") && !complist[i].equals(LiveUpdatingCheckBox)){
                checkvector.add(complist[i]);
                //System.out.println(complist[i].getClass().getName());
            }
        }
    }

    private void remember(){
        combostatevector=new Vector();
        checkstatevector=new Vector<Boolean>();

        for (int i=0; i<combovector.size(); i++){
            combostatevector.add(((javax.swing.JComboBox) combovector.elementAt(i)).getSelectedItem());
        }
        for (int i=0; i<checkvector.size(); i++){
            checkstatevector.add(((javax.swing.JCheckBox) checkvector.elementAt(i)).isSelected());
        }
    }

    private void restore(){
        for (int i=0; i<combovector.size(); i++){
            ((javax.swing.JComboBox) combovector.elementAt(i)).setSelectedItem(combostatevector.elementAt(i));
        }

        for (int i=0; i<checkvector.size(); i++){
            System.out.println("Check vector index is "+i);
            ((javax.swing.JCheckBox) checkvector.elementAt(i)).setSelected(checkstatevector.elementAt(i));
        }
        /**for (int i=0; i<statelist.length; i++){
            if (statelist[i].toString() !=null){
                ((javax.swing.JComboBox) complist[i]).setSelectedItem(statevector.elementAt(i));
                ((javax.swing.JComboBox) complist[i]).setSelectedItem((Object) statelist[i]);
                
            }
        }
         **/

        
    }

    public double act2met(String Act){

        //From  Compendium of Physical Activities
        //https://sites.google.com/site/compendiumofphysicalactivities/home
        double met=0;
        if (Act.matches("Light Walking")){
            met=3.0; //Taylor code 17170
        }else if (Act.matches("Medium Walking")){
            met=4.3; //Taylor code 17200
        }else if (Act.matches("Intense Walking")){
            met=5.0; //Taylor code 17220

        }else if (Act.matches("Light Running")){
            met=8.3; //Taylor code 12030
        }else if (Act.matches("Medium Running")){
            met=12.8;  //Taylor code 12110
        }else if (Act.matches("Intense Running")){
            met=19; //Taylor code 12132

        }else if (Act.matches("Light Cycling")){
            met=6.8;  //Taylor code 01020
        }else if (Act.matches("Medium Cycling")){
            met=8;  //Taylor code 01030
        }else if (Act.matches("Intense Cycling")){
            met=10; //Taylor code 01040

        }else if (Act.matches("Swimming")){
            met=8;
        }

        //We subtract 1 off to account for the already included RMR value
        return (met-1);
    }

    public double changesign(String change){
        if (change.matches("Removing")){
            return -1;
        }else{
            return 1;
        }
    }

    public double dayweek(String dayweek){
        if (dayweek.matches("week")){
            return 1/((double) 7);
        }else{
            return 1;
        }
    }

   

    public void setoldactchange(double oldact){
        oldactchangeperc=oldact;
    }

    public void setoldboxvalue(Object oldval){
        oldboxvalue=oldval;
    }

    public Object getoldboxvalue(){
        return oldboxvalue;
    }

    public double getactchangeperc(){
        return actchangeperc;
    }

    private Intervention getintervention() throws Exception{
        if (index==1){
            return mpanel.getIntervention1();
        }else if(index==2){
            return mpanel.getIntervention2();
        }else if (index==3){
            return mpanel.getGoalIntervention();
        }else if (index==4){
            return mpanel.getGoalMaintenanceIntervention();
        }else{
            throw new Exception();
        }
    }

    public javax.swing.JComboBox getbox() throws Exception{
        if (index==1){
            return mpanel.getIntervention1ActChangeBox();
        }else if(index==2){
            return mpanel.getIntervention2ActChangeBox();
        }else if (index==3){
            return mpanel.getGoalActChangeBox();
        }else if (index==4){
            return mpanel.getGoalMaintActChangeBox();
        }else{
            throw new Exception();
        }
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

        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        METlabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        StatementLabel = new javax.swing.JLabel();
        ActivityBox1 = new javax.swing.JComboBox();
        ActCheck4 = new javax.swing.JCheckBox();
        ActCheck2 = new javax.swing.JCheckBox();
        ActChange4 = new javax.swing.JComboBox();
        ActChange1 = new javax.swing.JComboBox();
        ActChange2 = new javax.swing.JComboBox();
        ActChange3 = new javax.swing.JComboBox();
        ActCheck3 = new javax.swing.JCheckBox();
        ActCheck1 = new javax.swing.JCheckBox();
        ActivityBox2 = new javax.swing.JComboBox();
        ActivityBox3 = new javax.swing.JComboBox();
        ActivityBox4 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        DurationBox4 = new javax.swing.JComboBox();
        DurationBox1 = new javax.swing.JComboBox();
        DurationBox2 = new javax.swing.JComboBox();
        DurationBox3 = new javax.swing.JComboBox();
        FrequencyBox4 = new javax.swing.JComboBox();
        FrequencyBox3 = new javax.swing.JComboBox();
        FrequencyBox1 = new javax.swing.JComboBox();
        FrequencyBox2 = new javax.swing.JComboBox();
        SpaceLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        OkButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        DayWeekBox1 = new javax.swing.JComboBox();
        DayWeekBox2 = new javax.swing.JComboBox();
        DayWeekBox3 = new javax.swing.JComboBox();
        DayWeekBox4 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        LiveUpdatingCheckBox = new javax.swing.JCheckBox();
        NewActChangePercField = new javax.swing.JFormattedTextField();

        jLabel3.setText("These changes will result in a net change of ");

        jTextField1.setColumns(2);
        jTextField1.setEditable(false);

        METlabel.setText("METS (kJ/kg)");

        setTitle("Enter A Physical Activity Regimen");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("minutes, ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        getContentPane().add(jLabel2, gridBagConstraints);

        StatementLabel.setText("I will change my physical activities by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        getContentPane().add(StatementLabel, gridBagConstraints);

        ActivityBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Light Walking", "Medium Walking", "Intense Walking", "Light Running", "Medium Running", "Intense Running", "Light Cycling", "Medium Cycling", "Intense Cycling" }));
        ActivityBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActivityBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        getContentPane().add(ActivityBox1, gridBagConstraints);

        ActCheck4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ActCheck4StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        getContentPane().add(ActCheck4, gridBagConstraints);

        ActCheck2.setSelected(true);
        ActCheck2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ActCheck2StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        getContentPane().add(ActCheck2, gridBagConstraints);

        ActChange4.setForeground(new java.awt.Color(153, 153, 153));
        ActChange4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adding", "Removing" }));
        ActChange4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActChange4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        getContentPane().add(ActChange4, gridBagConstraints);

        ActChange1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adding", "Removing" }));
        ActChange1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActChange1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        getContentPane().add(ActChange1, gridBagConstraints);

        ActChange2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adding", "Removing" }));
        ActChange2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActChange2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(ActChange2, gridBagConstraints);

        ActChange3.setForeground(new java.awt.Color(153, 153, 153));
        ActChange3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adding", "Removing" }));
        ActChange3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActChange3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        getContentPane().add(ActChange3, gridBagConstraints);

        ActCheck3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ActCheck3StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        getContentPane().add(ActCheck3, gridBagConstraints);

        ActCheck1.setSelected(true);
        ActCheck1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ActCheck1ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(ActCheck1, gridBagConstraints);

        ActivityBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Light Walking", "Medium Walking", "Intense Walking", "Light Running", "Medium Running", "Intense Running", "Light Cycling", "Medium Cycling", "Intense Cycling" }));
        ActivityBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActivityBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        getContentPane().add(ActivityBox2, gridBagConstraints);

        ActivityBox3.setForeground(new java.awt.Color(153, 153, 153));
        ActivityBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Light Walking", "Medium Walking", "Intense Walking", "Light Running", "Medium Running", "Intense Running", "Light Cycling", "Medium Cycling", "Intense Cycling" }));
        ActivityBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActivityBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        getContentPane().add(ActivityBox3, gridBagConstraints);

        ActivityBox4.setForeground(new java.awt.Color(153, 153, 153));
        ActivityBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Light Walking", "Medium Walking", "Intense Walking", "Light Running", "Medium Running", "Intense Running", "Light Cycling", "Medium Cycling", "Intense Cycling" }));
        ActivityBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActivityBox4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        getContentPane().add(ActivityBox4, gridBagConstraints);

        jLabel5.setText("for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jLabel5, gridBagConstraints);

        jLabel6.setText("for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        getContentPane().add(jLabel6, gridBagConstraints);

        jLabel7.setText("for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jLabel7, gridBagConstraints);

        jLabel8.setText("minutes, ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel9.setText("minutes, ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jLabel9, gridBagConstraints);

        jLabel10.setText("minutes, ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        getContentPane().add(jLabel10, gridBagConstraints);

        DurationBox4.setForeground(new java.awt.Color(153, 153, 153));
        DurationBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "75", "90", "120" }));
        DurationBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DurationBox4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        getContentPane().add(DurationBox4, gridBagConstraints);

        DurationBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "75", "90", "120" }));
        DurationBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DurationBox1ActionPerformed(evt);
            }
        });
        DurationBox1.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                DurationBox1VetoableChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        getContentPane().add(DurationBox1, gridBagConstraints);

        DurationBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "75", "90", "120" }));
        DurationBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DurationBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        getContentPane().add(DurationBox2, gridBagConstraints);

        DurationBox3.setForeground(new java.awt.Color(153, 153, 153));
        DurationBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "75", "90", "120" }));
        DurationBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DurationBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        getContentPane().add(DurationBox3, gridBagConstraints);

        FrequencyBox4.setForeground(new java.awt.Color(153, 153, 153));
        FrequencyBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        FrequencyBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FrequencyBox4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        getContentPane().add(FrequencyBox4, gridBagConstraints);

        FrequencyBox3.setForeground(new java.awt.Color(153, 153, 153));
        FrequencyBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        FrequencyBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FrequencyBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        getContentPane().add(FrequencyBox3, gridBagConstraints);

        FrequencyBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        FrequencyBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FrequencyBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        getContentPane().add(FrequencyBox1, gridBagConstraints);

        FrequencyBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        FrequencyBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FrequencyBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        getContentPane().add(FrequencyBox2, gridBagConstraints);

        SpaceLabel.setText("     ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 9;
        getContentPane().add(SpaceLabel, gridBagConstraints);

        jLabel11.setText("time(s) per ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jLabel11, gridBagConstraints);

        jLabel12.setText("time(s) per ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        getContentPane().add(jLabel12, gridBagConstraints);

        jLabel13.setText("time(s) per ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jLabel13, gridBagConstraints);

        jLabel14.setText("time(s) per ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        getContentPane().add(jLabel14, gridBagConstraints);

        OkButton.setText("OK");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(OkButton, gridBagConstraints);

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(CancelButton, gridBagConstraints);

        DayWeekBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "day", "week" }));
        DayWeekBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DayWeekBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        getContentPane().add(DayWeekBox1, gridBagConstraints);

        DayWeekBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "day", "week" }));
        DayWeekBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DayWeekBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        getContentPane().add(DayWeekBox2, gridBagConstraints);

        DayWeekBox3.setForeground(new java.awt.Color(153, 153, 153));
        DayWeekBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "day", "week" }));
        DayWeekBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DayWeekBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        getContentPane().add(DayWeekBox3, gridBagConstraints);

        DayWeekBox4.setForeground(new java.awt.Color(153, 153, 153));
        DayWeekBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "day", "week" }));
        DayWeekBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DayWeekBox4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        getContentPane().add(DayWeekBox4, gridBagConstraints);

        jLabel4.setText("This will be a change of");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jLabel4, gridBagConstraints);

        jLabel15.setText("percent of your baseline activity level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel15, gridBagConstraints);

        LiveUpdatingCheckBox.setText("Live Updating");
        LiveUpdatingCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LiveUpdatingCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(LiveUpdatingCheckBox, gridBagConstraints);

        NewActChangePercField.setColumns(4);
        NewActChangePercField.setEditable(false);
        NewActChangePercField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        getContentPane().add(NewActChangePercField, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed

        try{
            getintervention().setactchangepercent(oldactchangeperc);
            getbox().setSelectedItem(oldboxvalue);

            this.setVisible(false);
            mpanel.recalc();
        } catch (Exception e){

        }
        
        //mpanel.getIntervention2ActChangeBox().setSelectedItem(String.valueOf(mpanel.getIntervention2().getactchangepercent())+"%");

        

    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed

        try{
            remember();
            getintervention().setdetailed(true);

            update();
            getintervention().setactchangepercent(actchangeperc);
            mpanel.recalc();
            this.setVisible(false);
        }catch (Exception e){

        }
        //mpanel.recalc();


    }//GEN-LAST:event_OkButtonActionPerformed

    private void ActChange1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActChange1ActionPerformed
        update();
    }//GEN-LAST:event_ActChange1ActionPerformed

    private void ActivityBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActivityBox1ActionPerformed
        update();
    }//GEN-LAST:event_ActivityBox1ActionPerformed

    private void DurationBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DurationBox1ActionPerformed
        update();
    }//GEN-LAST:event_DurationBox1ActionPerformed

    private void FrequencyBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FrequencyBox1ActionPerformed
        update();
    }//GEN-LAST:event_FrequencyBox1ActionPerformed

    private void DayWeekBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DayWeekBox1ActionPerformed
        update();
    }//GEN-LAST:event_DayWeekBox1ActionPerformed

    private void ActChange2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActChange2ActionPerformed
        update();
    }//GEN-LAST:event_ActChange2ActionPerformed

    private void ActivityBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActivityBox2ActionPerformed
        update();
    }//GEN-LAST:event_ActivityBox2ActionPerformed

    private void DurationBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DurationBox2ActionPerformed
        update();
    }//GEN-LAST:event_DurationBox2ActionPerformed

    private void FrequencyBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FrequencyBox2ActionPerformed
        update();
    }//GEN-LAST:event_FrequencyBox2ActionPerformed

    private void DayWeekBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DayWeekBox2ActionPerformed
        update();
    }//GEN-LAST:event_DayWeekBox2ActionPerformed

    private void ActChange3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActChange3ActionPerformed
        update();
    }//GEN-LAST:event_ActChange3ActionPerformed

    private void ActivityBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActivityBox3ActionPerformed
        update();
    }//GEN-LAST:event_ActivityBox3ActionPerformed

    private void DurationBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DurationBox3ActionPerformed
        update();
    }//GEN-LAST:event_DurationBox3ActionPerformed

    private void FrequencyBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FrequencyBox3ActionPerformed
        update();
    }//GEN-LAST:event_FrequencyBox3ActionPerformed

    private void DayWeekBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DayWeekBox3ActionPerformed
        update();
    }//GEN-LAST:event_DayWeekBox3ActionPerformed

    private void ActChange4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActChange4ActionPerformed
        update();
    }//GEN-LAST:event_ActChange4ActionPerformed

    private void ActivityBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActivityBox4ActionPerformed
        update();
    }//GEN-LAST:event_ActivityBox4ActionPerformed

    private void DurationBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DurationBox4ActionPerformed
        update();
    }//GEN-LAST:event_DurationBox4ActionPerformed

    private void FrequencyBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FrequencyBox4ActionPerformed
        update();
    }//GEN-LAST:event_FrequencyBox4ActionPerformed

    private void DayWeekBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DayWeekBox4ActionPerformed
        update();
    }//GEN-LAST:event_DayWeekBox4ActionPerformed

    private void LiveUpdatingCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LiveUpdatingCheckBoxActionPerformed
        if (LiveUpdatingCheckBox.isSelected()){
            liveupdating=true;
            update();
        }else{
            liveupdating=false;
        }
    }//GEN-LAST:event_LiveUpdatingCheckBoxActionPerformed

    private void DurationBox1VetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_DurationBox1VetoableChange
        if (evt.getPropertyName().equalsIgnoreCase("value")){
            try{
                Double.parseDouble(evt.getNewValue().toString());
            }catch (NumberFormatException e){
                throw new java.beans.PropertyVetoException("Non-number", evt);
            }
        }
    }//GEN-LAST:event_DurationBox1VetoableChange

    private void ActCheck1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ActCheck1ItemStateChanged
        if (ActCheck1.isSelected()){
            ActChange1.setForeground(activecolor);
            ActivityBox1.setForeground(activecolor);
            DurationBox1.setForeground(activecolor);
            FrequencyBox1.setForeground(activecolor);
            DayWeekBox1.setForeground(activecolor);
        }else{
            ActChange1.setForeground(inactivecolor);
            ActivityBox1.setForeground(inactivecolor);
            DurationBox1.setForeground(inactivecolor);
            FrequencyBox1.setForeground(inactivecolor);
            DayWeekBox1.setForeground(inactivecolor);
        }
        update();
    }//GEN-LAST:event_ActCheck1ItemStateChanged

    private void ActCheck2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ActCheck2StateChanged
        if (ActCheck2.isSelected()){
            ActChange2.setForeground(activecolor);
            ActivityBox2.setForeground(activecolor);
            DurationBox2.setForeground(activecolor);
            FrequencyBox2.setForeground(activecolor);
            DayWeekBox2.setForeground(activecolor);
        }else{
            ActChange2.setForeground(inactivecolor);
            ActivityBox2.setForeground(inactivecolor);
            DurationBox2.setForeground(inactivecolor);
            FrequencyBox2.setForeground(inactivecolor);
            DayWeekBox2.setForeground(inactivecolor);
        }
        update();
    }//GEN-LAST:event_ActCheck2StateChanged

    private void ActCheck3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ActCheck3StateChanged
        if (ActCheck3.isSelected()){
            ActChange3.setForeground(activecolor);
            ActivityBox3.setForeground(activecolor);
            DurationBox3.setForeground(activecolor);
            FrequencyBox3.setForeground(activecolor);
            DayWeekBox3.setForeground(activecolor);
        }else{
            ActChange3.setForeground(inactivecolor);
            ActivityBox3.setForeground(inactivecolor);
            DurationBox3.setForeground(inactivecolor);
            FrequencyBox3.setForeground(inactivecolor);
            DayWeekBox3.setForeground(inactivecolor);
        }
        update();
    }//GEN-LAST:event_ActCheck3StateChanged

    private void ActCheck4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ActCheck4StateChanged
        if (ActCheck4.isSelected()){
            ActChange4.setForeground(activecolor);
            ActivityBox4.setForeground(activecolor);
            DurationBox4.setForeground(activecolor);
            FrequencyBox4.setForeground(activecolor);
            DayWeekBox4.setForeground(activecolor);
        }else{
            ActChange4.setForeground(inactivecolor);
            ActivityBox4.setForeground(inactivecolor);
            DurationBox4.setForeground(inactivecolor);
            FrequencyBox4.setForeground(inactivecolor);
            DayWeekBox4.setForeground(inactivecolor);
        }
        update();
    }//GEN-LAST:event_ActCheck4StateChanged

    public void update(){
         double[] metmin=new double [activityindex];
        if (ActCheck1.isSelected()) metmin[0]=changesign(ActChange1.getSelectedItem().toString())*
                                          act2met(ActivityBox1.getSelectedItem().toString())*
                                          Double.parseDouble(DurationBox1.getSelectedItem().toString())/60*
                                          Double.parseDouble(FrequencyBox1.getSelectedItem().toString())*
                                          dayweek(DayWeekBox1.getSelectedItem().toString());

        if (ActCheck2.isSelected()) metmin[1]=changesign(ActChange2.getSelectedItem().toString())*
                                          act2met(ActivityBox2.getSelectedItem().toString())*
                                          Double.parseDouble(DurationBox2.getSelectedItem().toString())/60*
                                          Double.parseDouble(FrequencyBox2.getSelectedItem().toString())*
                                          dayweek(DayWeekBox2.getSelectedItem().toString());

        if (ActCheck3.isSelected()) metmin[2]=changesign(ActChange3.getSelectedItem().toString())*
                                          act2met(ActivityBox3.getSelectedItem().toString())*
                                          Double.parseDouble(DurationBox3.getSelectedItem().toString())/60*
                                          Double.parseDouble(FrequencyBox3.getSelectedItem().toString())*
                                          dayweek(DayWeekBox3.getSelectedItem().toString());

        if (ActCheck4.isSelected()) metmin[3]=changesign(ActChange4.getSelectedItem().toString())*
                                          act2met(ActivityBox4.getSelectedItem().toString())*
                                          Double.parseDouble(DurationBox4.getSelectedItem().toString())/60*
                                          Double.parseDouble(FrequencyBox4.getSelectedItem().toString())*
                                          dayweek(DayWeekBox4.getSelectedItem().toString());
        metmin_total=0;

        for (int i=0; i<metmin.length; i++){
            if (metmin[i]!=0) {

                metmin_total=metmin_total+metmin[i];
            }
        }


        //System.out.println("first activity change ="+metmin[0]);
        //System.out.println("dayweek ="+dayweek(DayWeekBox1.getSelectedItem().toString()));

        actchangeperc= metmin_total/mpanel.getBaseline().getActParam()*100;
        if (actchangeperc<-100) actchangeperc=-100;

        NewActChangePercField.setText(Double.toString((int) actchangeperc));

        if (liveupdating && !mpanel.isgoalsim()) {
            if (index==1 && mpanel.getIntervention1().isdetailed()) {
                mpanel.getIntervention1().setactchangepercent(actchangeperc);
                mpanel.recalc();
            }
            if (index==2 && mpanel.getIntervention2().isdetailed()) {
                mpanel.getIntervention2().setactchangepercent(actchangeperc);
                mpanel.recalc();
            }
        }
        
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DetailedActivityDialog dialog = new DetailedActivityDialog(new javax.swing.JFrame(), true, new MainPanel(), null, 0);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox ActChange1;
    private javax.swing.JComboBox ActChange2;
    private javax.swing.JComboBox ActChange3;
    private javax.swing.JComboBox ActChange4;
    private javax.swing.JCheckBox ActCheck1;
    private javax.swing.JCheckBox ActCheck2;
    private javax.swing.JCheckBox ActCheck3;
    private javax.swing.JCheckBox ActCheck4;
    private javax.swing.JComboBox ActivityBox1;
    private javax.swing.JComboBox ActivityBox2;
    private javax.swing.JComboBox ActivityBox3;
    private javax.swing.JComboBox ActivityBox4;
    private javax.swing.JButton CancelButton;
    private javax.swing.JComboBox DayWeekBox1;
    private javax.swing.JComboBox DayWeekBox2;
    private javax.swing.JComboBox DayWeekBox3;
    private javax.swing.JComboBox DayWeekBox4;
    private javax.swing.JComboBox DurationBox1;
    private javax.swing.JComboBox DurationBox2;
    private javax.swing.JComboBox DurationBox3;
    private javax.swing.JComboBox DurationBox4;
    private javax.swing.JComboBox FrequencyBox1;
    private javax.swing.JComboBox FrequencyBox2;
    private javax.swing.JComboBox FrequencyBox3;
    private javax.swing.JComboBox FrequencyBox4;
    private javax.swing.JCheckBox LiveUpdatingCheckBox;
    private javax.swing.JLabel METlabel;
    private javax.swing.JFormattedTextField NewActChangePercField;
    private javax.swing.JButton OkButton;
    private javax.swing.JLabel SpaceLabel;
    private javax.swing.JLabel StatementLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
