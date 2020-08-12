/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainPanel.java
 *
 * Created on Jul 7, 2010, 2:51:17 PM
 */

package weightapplet;


import weightapplet.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.PrintStream;
import javax.swing.JFileChooser;


/**
 *
 * @author Dhruva
 */
public class MainPanel extends javax.swing.JPanel {

    private Baseline Baseline;

    private boolean init_Male=true;
    private double init_Age=23, init_Height=180, init_Weight=70, init_Bfp=18, init_RMR=1709, init_PAL=1.5;
    //private double init_GoalWeight=70, init_GoalTime=365, init_Bfp=18, init_RMR=1709, init_PAL=1.4;

    private Intervention Intervention1;
    private Intervention Intervention2;

    private Intervention GoalIntervention;
    private Intervention GoalMaintenanceIntervention;

    private double GoalWeight;
    
    private double GoalBfp;
    private double TimetoGoal;
    private double WeightatGoal;
    private double BfpAtGoal;
    private double GoalCals;
    private double GoalMaintCals;
    
    private double OldWeight;
    private double OldPAL;
    private double OldRMR;
    private double OldTimetoGoal;
    private double OldGoalWeight;
    private double OldGoalActChange;
    private double OldGoalMaintActChange;
    private boolean OldMale;

    private String change;

    private double SimulationLength;
    private double FinalWeight;
    private double FinalBfp;
    private double FinalBMI;
    private double FinalRMR;

    private double weightunits = 1; //Converts between the displayed units and the internal kilograms representation
    private double heightunits = 1;  //Converts between the displayed units and the internal centimeters representation
    private double energyunits =1;
    private String energystring="Calories";


    private boolean simpleversion=false;
    private boolean liveupdating;
    private boolean goalsim = true;
    private boolean calcflag = false;
   


    private double MinCals = 0;
    private static double eps = .001;


    private boolean warningson=true;

    private boolean init;
    private double check;

    private Color black = new java.awt.Color(0, 0, 0);
    private Color grey = new java.awt.Color(102, 102, 102);
    private Color highlightgreen = new java.awt.Color(51, 251, 51);
    private Color highlightyellow = new java.awt.Color(255, 255, 10);
    private Color highlightblue = new java.awt.Color(51, 255, 255);
    private Color overviewhighlight = new java.awt.Color(255, 0, 255);
    //private Color overviewhighlight=highlightgreen;

    private JFrame Mainframe;
    private GraphPanel gpanel;
    private WeightApplet applet;
    private JFrame gframe;
    private double timestart;
    private double timezoom;
    private double vertical_zoom;
    private int[] graphparams={0,0,0};
    private boolean graphattached=true;
    private double spread_percent=10;
    /** Creates new form MainPanel */

    private PAL_Dialog pdialog;
    private DetailedActivityDialog inter1actdialog;
    private DetailedActivityDialog inter2actdialog;
    private DetailedActivityDialog goalactdialog;
    private DetailedActivityDialog goalmaintactdialog;
    //private JFileChooser fc;
    //private Object fc;

    public static void main(String[] args) {
        
        System.out.println("Main method of main panel");
        
                
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                MainPanel mpanel = new MainPanel();
                

                /*
                JFrame f = new JFrame();

                //System.out.println("pack 1");
                //f.pack();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setTitle("Human Weight Simulator");
                f.getContentPane().add(mpanel);
                //System.out.println("pack 2");
                f.pack();
                System.out.println("MainPanel visible");
                f.setVisible(true);
                int n = JOptionPane.showConfirmDialog(
                        f,
                        "Welcome to the Human Weight Simulator developed at the Lab of Biological Modeling, NIDDK.\n" +
                        "Would you like an overview of how to use the simulator?",
                        "Welcome",
                        JOptionPane.YES_NO_OPTION);
                if (n==0) mpanel.walkthrough();

                 //**/
            }
        });
    }

    public GraphPanel getgraphpanel(){
        return this.gpanel;
    }


    public MainPanel(){
                this(null);

            
        
    }

    public boolean isApplet(){
        return (applet!=null);
    }

    private void createFrame(){
         Mainframe=new JFrame();
            if(!isApplet()) Mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            else  Mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                Mainframe.setTitle("Human Weight Simulator");
                Mainframe.getContentPane().add(this);
                //System.out.println("pack 2");
                Mainframe.pack();
                System.out.println("MainPanel visible");
                Mainframe.setVisible(true);
    }

    public MainPanel(WeightApplet applet) {
        init=true;
        
        
        this.applet=applet;

        //Initialize the Baseline state first, as some of the components use to get their starting values;

        Baseline=new Baseline();
        
        //Initialize all the components
        initComponents();
        
        //remove the jpanel which is a place holder for the graph
        remove(jPanel2);
        
        //Initialize the values and sync the fields with the baseline and various interventions;
        initValues();

        //Initialize and add the graph
        initGraph();

        //Create and hide the PAL dialog box
        pdialog=new PAL_Dialog(null, false, this);
        //pdialog.setVisible(false);

        inter1actdialog= new DetailedActivityDialog(null, false, this, "In the first phase, I will change my activity by:", 1);
        inter1actdialog.setVisible(false);

        inter2actdialog= new DetailedActivityDialog(null, false, this, "In the second phase, I will change my activity by:", 2);
        inter2actdialog.setVisible(false);

        goalactdialog= new DetailedActivityDialog(null, false, this, "In my weight change phase, I will change my activity by:", 3);
        goalactdialog.setVisible(false);

        goalmaintactdialog= new DetailedActivityDialog(null, false, this, "In my weight maintenance phase, I will change my activity by:", 4);
        goalmaintactdialog.setVisible(false);

        //fc=new JFileChooser();

        //PoundsRadio.setSelected(true);
        
        

        System.out.println("Things made, now going to listen");
        System.out.println("Listening, now going to recalc");
        init=false;



        //Now do some cleanup operations to get things as we want them
        recalc();
        ChoicePanel.setSelectedIndex(0);
        

        //Make sure the height units are synced with what the user sees
        HeightUnitsChange();

        //Make sure the energy units are synced with what the user sees
        EnergyUnitsChange();
        
        //We don't want to overwhelm new users, so we start in the standard (non-advanced) mode
        AdvancedControlsCheckBox.setSelected(false);
        simplify();

        createFrame();
        System.out.println("Thing calculated, should be smooth from here");

        int n = JOptionPane.showConfirmDialog(
                        Mainframe,
                        "Welcome to the Human Weight Simulator developed at the Lab of Biological Modeling, NIDDK.\n" +
                        "Would you like an overview of how to use the simulator?",
                        "Welcome",
                        JOptionPane.YES_NO_OPTION);
                if (n==0) this.walkthrough();


        if (isApplet()) OptionsPanel.remove(SaveButton); OptionsPanel.repaint();
        

    }

    public void initGraph(){

        
        //If we're initialzing from the beginning, we need to create the graph panel, and a frame to hold it when it is detached
        if (init){

            gframe=new JFrame();
            gpanel = new GraphPanel(this, Baseline, Intervention1, Intervention2, SimulationLength, graphparams);
            gpanel.setPreferredSize(new java.awt.Dimension(800, 250));
        }

        //gpanel.setPreferredSize(new java.awt.Dimension(200, 250));

        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();

        

        if (graphattached){
            System.out.print("ATTACHING graph");

            //The constraints are the same as those for the removed jpanel2
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            //gridBagConstraints.ipadx = 50;
            gridBagConstraints.ipady = 150;
            //add(gpanel);
            if (!init) add(jPanel2, gridBagConstraints);
            add(gpanel, gridBagConstraints);

            //If we've got the mainpanel in a frame, then we can pack it
            if (Mainframe!=null) Mainframe.pack();

            //if (SwingUtilities.getRoot(this)!=null) ((JFrame) SwingUtilities.getRoot(this)).pack();
            //jPanel2.add(gpanel, gridBagConstraints);
        }else if(!graphattached){
            //gpanel.setPreferredSize(new java.awt.Dimension(200, 250));
            //gpanel.setPreferredSize(jPanel2.getPreferredSize());
            gpanel.setSize(jPanel2.getPreferredSize());

            gframe.getContentPane().add(gpanel);
            gframe.pack();
        }


        if (init) WeightUnitsChange();

        

    }

     public void setgraphparams(){

         //This method set the value for each of the graph view parameters using  corresponding sliders

        timestart=( (double) TimeStartSlider.getValue())/TimeStartSlider.getMaximum();
        timezoom=( (double) TimeZoomSlider.getValue())/TimeZoomSlider.getMaximum();
        vertical_zoom=( (double) VerticalZoomSlider.getValue())/VerticalZoomSlider.getMaximum();
    }

     public PAL_Dialog getPAL_Dialog(){
         return pdialog;
     }

    public void initValues() {

        //Method to initialize all values

        calcflag=true;


        //Create a baseline with the appropiate parameters, then sync the various components
        Baseline=new Baseline(init_Male, init_Age, init_Height, init_Weight, init_Bfp, init_RMR, init_PAL);


        InitialWeightField.setValue(Baseline.getWeight_kgs() * weightunits);
        InitialWeightField2.setValue(Baseline.getWeight_kgs() * weightunits);
        InitialBfpField.setValue(Baseline.getBfp());
        InitialBfpField2.setValue(Baseline.getBfp());
        HeightField.setValue(Baseline.getHeight_cms() * heightunits);
        AgeField.setValue(Baseline.getAge());
        GenderBox.setSelectedItem(Baseline.getMale()? "Male":"Female");
        InitialPALField.setValue(Baseline.getPAL());

        InitialCarbInPercField.setValue(Baseline.getCarbInPercent());
        InitialSodiumField.setValue(Baseline.getSodium());
        
        
        

        InitialRMRField.setValue(Baseline.getRMR()*energyunits);
        

        //Sync up the internal goal conditions with the appropriate components
        GoalWeight = 70;
        GoalWeightField.setValue(GoalWeight * weightunits);
        GoalBfp = 25;
        BodyModel goalstate=new BodyModel(GoalWeight*GoalBfp/100,GoalWeight*(100-GoalBfp)/100,0, 0.5, Constants.beta*Baseline.getMaintCals());
        GoalMaintCals=goalstate.cals4balance(Baseline,Baseline.getActParam());

        TimetoGoal = 180;
        GoalTimeField.setValue(TimetoGoal);


        SimulationLength = 365;
        SimulationLengthField.setValue(SimulationLength);
        
        WeightUnitsChange();


        goalsim = true;

        

        //Create the first intervention and sync its components
        Intervention1=new Intervention(90, 2000, 50, 0,4000);
        Intervention1.setproportionalsodium(Baseline);

        Intervention1.setTitle("Intervention1");
        Intervention1DayField.setValue(Intervention1.getday());
        Intervention1SodiumField.setValue(Intervention1.getsodium());
        Intervention1CarbInPercField.setValue(Intervention1.getcarbinpercent());
        Intervention1CaloriesField.setValue(Intervention1.getcalories()*energyunits);
        Intervention1ActChangeBox.setSelectedItem(Double.toString(Intervention1.getactchangepercent()));

        
        
        //Create the second intervention and sync its components
        Intervention2=new Intervention(180, 2400, 50, 0, 4000);
        Intervention2.setproportionalsodium(Baseline);

        Intervention2.setTitle("Intervention2");
        Intervention2DayField.setValue(Intervention2.getday());
        Intervention2SodiumField.setValue(Intervention2.getsodium());
        Intervention2CarbInPercField.setValue(Intervention1.getcarbinpercent());
        Intervention2CaloriesField.setValue(Intervention2.getcalories()*energyunits);
        Intervention2ActChangeBox.setSelectedItem(Double.toString(Intervention2.getactchangepercent()));

        
        //Create the goal intervention as a placeholder; it's values will be changed after a recalc
        GoalIntervention=new Intervention(1, 2000, Baseline.getCarbInPercent(), 0,Baseline.getSodium()*2000/Baseline.getMaintCals());
        GoalIntervention.setTitle("Goal Intervention");

        //The goal maintenance intervention is also initialized as a place holder
        GoalMaintenanceIntervention=new Intervention((int) TimetoGoal+1, GoalMaintCals,Baseline.getCarbInPercent(), 0,Baseline.getSodium()*GoalMaintCals/Baseline.getMaintCals());
        GoalMaintenanceIntervention.setTitle("Goal Maintenance Intervention");


        System.out.println("Values entered");

        calcflag=false;


    }

    /**
     private void GenderRadioChange() {
        if (MaleRadio.isSelected()) {
            Baseline.setMale(true);
        } else if (FemaleRadio.isSelected()) {
            Baseline.setMale(false);
        }
        recalc();
    }
     **/

    public void walkthrough(){
        /**JOptionPane.showMessageDialog(this,
                        "Welcome to this Human Body Weight Simulator, developed at the Lab of Biological Modeling",
                        "Welcome",JOptionPane.INFORMATION_MESSAGE);
         * */
        //Create the button names for the walkthrough
        Object[] cont={"Continue", "Cancel"};

        //Before each dialog we put a highlighted border around the appropriate panel, then after the user clicks, we remove it
        BaselinePanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        int n=JOptionPane.showOptionDialog(this,
                        "First enter the information needed to set the starting or \"baseline\" conditions such as your initial weight,\n" +
                        "sex, age, height, and physical activity level. \n" +
                        "\n" +
                        "If you're not sure of your Physical Activity Level, click the \"Estimate Activity Level\" button\n" +
                        "and answer the two questions.\n" +
                        "\n" +
                        "The Simulator will automatically calculate your \"Baseline Diet\" that specifies the daily energy intake\n" +
                        "required to maintain your initial weight.\n" +
                        "\n" +
                        "You can also use this panel to change the displayed units for weight, height, and energy.",
                        "Setting the Baseline",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        BaselinePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        //If the user clicked "Cancel," then n=1, and we know to return/exit the walkthrough
        if (n==1) return;


        ChoicePanel.setSelectedIndex(0);
        GoalPanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        n=JOptionPane.showOptionDialog(this,
                        "Click the \"Set Your Goal Weight\" tab to simulate how to achieve your goal weight.\n" +
                        "\n" +
                        "Start by entering a goal body weight and how many days you plan to take to achieve that weight.\n" +
                        "\n" +
                        "The Simulator will calculate the diet required to reach your goal weight in the specified number of days\n" +
                        "as well as the permanent diet required to maintain that goal weight. There may be some difference in the\n" +
                        "final maintained weight due to changes in body water.\n " +
                        "\n" +
                        "Note that these calculations take into account the specified changes of physical activity for both diet phases.\n" +
                        "\n" +
                        "If your goal is not achievable in the specified time period, a warning message will be provided\n" +
                        "and your last change will be reverted.\n   "+
                        "\n"+
                        "By selected \"Detailed\" in the activity boxes, you can specify a detailed change in your activity regimen",
                        "Setting a Goal",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        GoalPanel.setBorder(null);
        if (n==1) return;

        ChoicePanel.setSelectedIndex(1);
        InterventionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        n=JOptionPane.showOptionDialog(this,
                        "You can also enter planned changes in your diet and activity and calculate how much these would change your weight\n" +
                        "by clicking the \"Specify Lifestyle Change\" tab.\n " +
                        "\n" +
                        "You can enter up to two different changes of diet and physical activity beginning at the specified times.\n" +
                        "These changes can be turned on and off using the check boxes.",
                        "Entering Lifestyle Changes",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        InterventionPanel.setBorder(null);
        ChoicePanel.setSelectedIndex(0);
        if (n==1) return;

        gpanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        n=JOptionPane.showOptionDialog(this,
                        "By clicking the \"Run Simulation\" button simulation results will display in the bottom panel.\n" +
                        "\n" +
                        "The first tab displays a graph of the Body Weight versus Time. The second tab displays a graph of the \n" +
                        "Body Fat percentage versus Time. The third tab displays Energy Intake and Expenditure versus Time.\n" +
                        "The fourth tab displays the model outputs in tabulated form.\n" +
                        "\n" +
                        "Your initial and final weight, percent body fat, and BMI (Body Mass Index) are given above the graph.",
                        "Display Simulation Outputs",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        gpanel.setBorder(null);
        if (n==1) return;

        ZoomPanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        n=JOptionPane.showOptionDialog(ChoicePanel,
                        "Use the central panel to enter the length of the simulation and control the graphical display.\n" +
                        "\n" +
                        "Change the number of days in the \"Length of Simulation\" Field to change how many days of the simulation\n" +
                        "are calculated.\n" +
                        "\n" +
                        "The \""+TimeStartLabel.getText()+"\" slider changes the time when the graph begins plotting the data.\n" +
                        "\n" +
                        "The \""+TimeZoomLabel.getText()+"\" slider allows you to change the horizontal scale of the graph.\n" +
                        "\n" +
                        "The \""+VerticalZoomLabel.getText()+"\" slider allows you to change the vertical scale of the graph.",
                        "Graph Controls",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        ZoomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        if (n==1) return;

        OptionsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(overviewhighlight, 6));
        n=JOptionPane.showOptionDialog(ChoicePanel,
                        "The options panel on the right allows for various advanced options, restoring default values, and can be used to repeat this overview.\n" +
                        "\n" +
                        "Enabling \"Advanced Controls\" provides the ability to enter baseline information about resting metabolic rate (RMR) and body fat as well\n" +
                        "as specify more detailed diet changes such as dietary carbohydrate and sodium. Gradual lifestyle changes can also be implemented using the advanced controls.\n" +
                        "\n" +
                        "Highlights draw attention to the Simulator's inputs and outputs. Standard inputs are in green, advanced control inputs are in blue\n" +
                        "and outputs are in yellow\n" +
                        "\n" +
                        "Graph options include the ability to show an upper and lower weight trajectories around the main model prediction;\n" +
                        "these reflect the uncertainty in the initial state of energy balance.\n" +
                        "\n" +
                        "You can also display a grid or detach the graph from the main window, as well as change the color scheme.",
                        "Options",JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, cont,"Continue");
        OptionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        if (n==1) return;

         JOptionPane.showMessageDialog(this,
                        "This concludes the Simulator overview. You can repeat it by clicking the \"Help\" button in the options panel.\n" +
                        "Please send your comments and questions to Kevin Hall at kevinh@niddk.nih.gov ",
                        "Have Fun!",JOptionPane.INFORMATION_MESSAGE);


    }
    public void syncbase(){
        InitialWeightField.setValue(Baseline.getWeight_kgs()*weightunits);
    }

    public javax.swing.JComboBox getIntervention1ActChangeBox(){
        return Intervention1ActChangeBox;
    }

    public Intervention getIntervention1(){
        return Intervention1;
    }

    public javax.swing.JComboBox getIntervention2ActChangeBox(){
        return Intervention2ActChangeBox;
    }

    public Intervention getIntervention2(){
        return Intervention2;
    }

    public boolean isgoalsim(){
        return goalsim;
    }

    public boolean defaultcolors(){
        return DefaultColorsCheckBox.isSelected();
    }

    public Intervention getGoalIntervention(){
        return GoalIntervention;
    }

    public Intervention getGoalMaintenanceIntervention(){
        return GoalMaintenanceIntervention;
    }

    public javax.swing.JComboBox getGoalActChangeBox(){
        return GoalActChangeBox;
    }

    public javax.swing.JComboBox getGoalMaintActChangeBox(){
        return GoalMaintenanceActChangeBox;
    }

    public javax.swing.JPanel getBaselinePanel(){
        return BaselinePanel;
    }
    protected void WeightUnitsChange() {
        if (KilogramsRadio.isSelected()) {
            weightunits = 1;
            GoalUnitsLabel.setText("kg in");
        } else if (PoundsRadio.isSelected()) {
            weightunits = 2.20462;
            GoalUnitsLabel.setText("lbs in");
        }

        //If we're initizlizing, the graph doens't exist yet, so we need to check before we send it the units change
        if (!init) gpanel.setweightunits(weightunits);

        //Raise the calcflag to prevent problems with the propertychange listeners on the fields
        calcflag = true;
        InitialWeightField.setValue(Baseline.getWeight_kgs() * weightunits);
        InitialWeightField2.setValue(Baseline.getWeight_kgs() * weightunits);
        GoalWeightField.setValue(GoalWeight * weightunits);
        //WeightAtGoalField.setValue(WeightatGoal*weightunits);
        FinalWeightField.setValue(FinalWeight*weightunits);
        calcflag = false;
    }

 private void HeightUnitsChange() {
        System.out.println("Changing heightunits");
        if (CentimetersRadio.isSelected()) {
            heightunits = 1;
        } else if (InchesRadio.isSelected()) {
            heightunits = 0.3937;
        }
        calcflag = true;
        HeightField.setValue(Baseline.getHeight_cms() * heightunits);
        calcflag = false;
    }

  private void BfpRadioChange() {
        if (InitialBfpInputRadio.isSelected()) {
            //By turning off bfp calculation, it will stay at it's current value until manually changed by the user
            Baseline.setcalculatedBfp(false);

            //We change the bfp field's editable status, and it's appearance to signal the change to the user
            InitialBfpField.setEditable(true);
            InitialBfpField.setForeground(black);
            if (HighlightCheckBox.isSelected()){
                //Now that the field is an input, we should highlight it if needed
                InitialBfpField.setBackground(highlightblue);
            }

        } else if (InitialBfpCalculatedRadio.isSelected()) {
            //By turning on the calculated bfp in the baseline, it will automatically return the correct value based on changes in age and height
            Baseline.setcalculatedBfp(true);

            //We stop letting the user input values, and change the appearance to indicate that
            InitialBfpField.setEditable(false);
            InitialBfpField.setForeground(grey);
            
            InitialBfpField.setBackground(Color.white);
            
            //If the value currently in the field doesn't match what's returnged by the baseline, we recalculate
            if (!InitialBfpField.getValue().equals(Baseline.getBfp())) recalc();
        }
    }


    private void RMRRadioChange() {
        if (RMRCalculatedRadio.isSelected()) {
            //The baseline will now return calculated RMR values
            Baseline.setcalculatedRMR(true);

            //We stop letting the user edit the field, and change it's appearance to indicate that
            InitialRMRField.setEditable(false);
            InitialRMRField.setForeground(grey);
            InitialRMRField.setBackground(Color.white);

            //Recalculate if the value in the field doeesn't match the returned value
            if (!InitialRMRField.getValue().equals(Baseline.getRMR()*energyunits)) recalc();

        } else if (RMRInputRadio.isSelected()){
            //This will freeze the RMR value unless it is manually changed by the user
            Baseline.setcalculatedRMR(false);
            
            //We let the user edit the field, and hence change it's appearance
            InitialRMRField.setEditable(true);
            InitialRMRField.setForeground(black);
            if (HighlightCheckBox.isSelected()){
                //Now that the field is an input, it can be highlighted
                InitialRMRField.setBackground(highlightblue);
            }
        }
    }

    private void Intervention1SodiumRadioChange(){
        if (Intervention1SodiumCalculatedRadio.isSelected()){
            Intervention1.setproportionalsodium(Baseline);
            //System.out.println("inter1 set prop in SodiumChange, ="+Intervention1.getsodium());
            Intervention1SodiumField.setValue(Intervention1.getsodium());
            Intervention1SodiumField.setEditable(false);
            Intervention1SodiumField.setForeground(grey);
            
            Intervention1SodiumField.setBackground(Color.white);
            
            
            if (!goalsim && Intervention1.ison()){
                recalc();
            }
        }else if (Intervention1SodiumInputRadio.isSelected()){
            Intervention1SodiumField.setEditable(true);
            Intervention1SodiumField.setForeground(black);
            if (HighlightCheckBox.isSelected()){
                Intervention1SodiumField.setBackground(highlightblue);
            }
        }

        }


     private void Intervention2SodiumRadioChange(){
        if (Intervention2SodiumCalculatedRadio.isSelected()){
            Intervention2.setproportionalsodium(Baseline);
            Intervention2SodiumField.setEditable(false);
            Intervention2SodiumField.setForeground(grey);
            Intervention2SodiumField.setBackground(Color.white);
            Intervention2SodiumField.setValue(Intervention2.getsodium());

            if (!goalsim && Intervention2.ison()) {
                recalc();
            }

        }else if(Intervention2SodiumInputRadio.isSelected()){
            Intervention2SodiumField.setEditable(true);
            Intervention2SodiumField.setForeground(black);
            if (HighlightCheckBox.isSelected()){
                Intervention2SodiumField.setBackground(highlightblue);
            }
        }

    }

     public void syncPAL(){
         System.out.println("baseline PAL="+Baseline.getPAL());
         InitialPALField.setValue(Baseline.getPAL());

     }


     public void interventionupdate(){
         if(Intervention1SodiumCalculatedRadio.isSelected()){
                Intervention1.setproportionalsodium(Baseline);
                System.out.println("inter1 set prop in recalc, ="+Intervention1.getsodium());
                Intervention1SodiumField.setValue(Intervention1.getsodium());
            }

            if(Intervention2SodiumCalculatedRadio.isSelected()){
                Intervention2.setproportionalsodium(Baseline);
                Intervention2SodiumField.setValue(Intervention2.getsodium());
            }
     }

     //This is the main recalculation function that handles changes in the baseline, goal, and interventions
    public void recalc(){
        calcflag = true;
        
        //System.out.println("Sync fields");
        InitialBfpField.setValue(Baseline.getBfp());
        InitialBfpField2.setValue(Baseline.getBfp());

        MaintCalsField.setValue(Baseline.getMaintCals()*energyunits);

        InitialBMIField.setValue(Baseline.getBMI());

        InitialRMRField.setValue(Baseline.getRMR()*energyunits);
        gpanel.setcalspread(Baseline.getMaintCals()*spread_percent/100);

        //We only want to proceed if we're done initialzing, and now all the components are ready to be handled
        if (!init){


            //Grab all the values needed from the baseline. Note that calculations are done in metric, then units are changed as
            // fields are filled in
            double InitialFat = Baseline.getFatWt();
            double InitialLean = Baseline.getLeanWt();//-ECW;
            double InitialActivity=Baseline.getActParam();
            double InitialCarbinPerc=Baseline.getCarbInPercent();

            


            //Now we try to make the goal intervention
           if (GoalCheckBox.isSelected()){
               try{
                GoalIntervention=Intervention.forgoal(Baseline, GoalWeight, TimetoGoal,GoalIntervention.getactchangepercent(),
                    MinCals, eps);
                    
                }catch (Exception e){
                    //If we fail, we print the stacktrace
                    e.printStackTrace();
                    //The redo function resets the last change
                    redo();
                    //Now we try to recalculate again, but we also don't want to finish the recalc, so we return.
                    recalc();
                    return;
                }


               //With the goal intervention created we set the goal calories and goal calories change fields
               GoalCals=GoalIntervention.getcalories();
                GoalCalsField.setValue(GoalIntervention.getcalories()*energyunits);
                GoalCalsChangeField.setValue((GoalIntervention.getcalories()-Baseline.getMaintCals())*energyunits);

                /**
                System.out.println("GoalCals is"+GoalCals);
                System.out.println("    Initial fat is "+InitialFat);
                System.out.println("    Initial lean is "+InitialLean);
                System.out.println("    Goal fat is "+GoalFat);
                System.out.println("    Goal lean is "+GoalLean);
                System.out.println("    TimetoGoal is "+TimetoGoal);
                System.out.println("    MinCals is "+MinCals);
                System.out.println("    InitialActivity is "+InitialActivity);
                System.out.println("    eps is "+eps);
                 **/

                 //System.out.println("Check goal state");


                //We find out what the body composition will be at the goal time with the RKlast_ecw function
                BodyModel goalbc=BodyModel.projectFromBaseline(Baseline, GoalIntervention, TimetoGoal+1);


                WeightatGoal=Baseline.getnewWeight(goalbc);
                //WeightAtGoalField.setValue(WeightatGoal*weightunits);
                BfpAtGoal=goalbc.getFatPercent(Baseline);
                //BfpAtGoalField.setValue(BfpAtGoal);

                //System.out.println("Make goal maintenance intervention");
                
                //The goal body comp is used to calculated the calories needed to keep tissue from changing. However, it does not account for 
                //the sodium needed to stabilize the extracellular water.
                if (GoalWeight==Baseline.getWeight_kgs() && GoalMaintenanceIntervention.getactchangepercent()==0) {
                    GoalMaintCals=Baseline.getMaintCals();
                }else{
                
                    
                    GoalMaintCals=goalbc.cals4balance(Baseline, GoalMaintenanceIntervention.getAct(Baseline));
                    System.out.printf("Goal therm=%f, bad approx gives stabletherm=%f\n", goalbc.getTherm(), Constants.beta_therm*GoalMaintCals);
                    //GoalMaintCals=RK.cals4approxbalance_glyc(goalbc[0],goalbc[1],goalbc[2],goalbc[3], GoalMaintenanceIntervention.getAct(Baseline),Baseline);
                }
                //GoalMaintCals=RK.cals4balance_ecw(goalbc[0],goalbc[1],goalbc[3],GoalMaintenanceIntervention.getAct(Baseline),Baseline);

                //If the user has changed the maintenance activity level, we may get a negative result, so we check and redo if needed
                if (GoalMaintCals<0){
                    redo();
                }

                

                GoalMaintenanceIntervention.setday((int)TimetoGoal+1);
                GoalMaintenanceIntervention.setcalories(GoalMaintCals);
                GoalMaintenanceIntervention.setcarbinpercent(Baseline.getCarbInPercent());
                GoalMaintenanceIntervention.setproportionalsodium(Baseline);
                //GoalMaintenanceIntervention.setsodium(goalsodium);

                DailyParams goalparams=new DailyParams(GoalMaintenanceIntervention, Baseline);
                System.out.println("Check goal maintenance intervention");
                System.out.println("fc="+goalbc.dfdt(Baseline, goalparams)+", lc="+goalbc.dldt(Baseline, goalparams)+", glyc="+goalbc.dgdt(Baseline, goalparams)+", decwc="+goalbc.dDecwdt(Baseline, goalparams));

                GoalMaintCalsField.setValue(GoalMaintenanceIntervention.getcalories()*energyunits);
                GoalMaintCalsChangeField.setValue((GoalMaintenanceIntervention.getcalories()-Baseline.getMaintCals())*energyunits);
           }else if (!GoalCheckBox.isSelected()){
                        GoalIntervention.seton(false);
                        GoalMaintenanceIntervention.seton(false);
            }

            if(Intervention1SodiumCalculatedRadio.isSelected()){
                Intervention1.setproportionalsodium(Baseline);
                //System.out.println("inter1 set prop in recalc, ="+Intervention1.getsodium());
                Intervention1SodiumField.setValue(Intervention1.getsodium());
            }

            if(Intervention2SodiumCalculatedRadio.isSelected()){
                Intervention2.setproportionalsodium(Baseline);
                Intervention2SodiumField.setValue(Intervention2.getsodium());
            }

            //System.out.println("GoalMaintCals calculated with arguments "+goalbc[0]+","+goalbc[1]+", "+InitialActivity+", "+InitialK);

            if (goalsim) {
                
                try {
                    System.out.println("Setting goal mode with Goalscals="+GoalCals);
                    
                        
                         //gpanel.setInterventionView_ecw(Baseline, GoalIntervention, GoalMaintenanceIntervention, SimulationLength);
                         gpanel.calculate(Baseline, GoalIntervention, GoalMaintenanceIntervention, SimulationLength);
                                        
                    StatusLabel.setText("Goal Simulation Displayed");
                    //StatusLabel.setText("Displaying how you will achieve your goal weight");
                } catch (Exception e) {
                    System.out.println("GoalView Failed");
                    e.printStackTrace();
                    StatusLabel.setText("Error running goal simulahtion");
                }
            } else {
                try {
                    //System.out.println("Setting intervention view with");
                    //Intervention1.print();
                    //Intervention2.print();
                    
                        System.out.println("Setting intervention view with ecw on");
                        //StatusLabel.setText("Displaying the effect of the lifestyle changes you entered");
                        //gpanel.setInterventionView_ecw(Baseline, Intervention1, Intervention2, SimulationLength);
                        gpanel.calculate(Baseline, Intervention1, Intervention2, SimulationLength);
                   
                    StatusLabel.setText("Lifestyle Simulation Displayed");
                } catch (Exception e) {
                    StatusLabel.setText("Error running intervention simulation");
                }
            }

            //gpanel.remaketable();
            setgraphparams();
            gpanel.setparams(timestart, timezoom, vertical_zoom);
            gpanel.repaint();

            BodyModel finalbc = gpanel.getfinalbc();
            //System.out.println("Finalbc length is "+finalbc.length);
            FinalWeight = finalbc.getWeight(Baseline);
            FinalWeightField.setValue(FinalWeight*weightunits);
            FinalBfp = finalbc.getFatPercent(Baseline);
            FinalBfpField.setValue(FinalBfp);
            FinalBMI = finalbc.getBMI(Baseline);
            FinalBMIField.setValue(FinalBMI);
            //FinalRMR=Baseline.getnewRMR(FinalWeight, SimulationLength);
            //FinalRMRField.setValue(FinalRMR);
            double minBMI=Baseline.getnewBMI(gpanel.getminweight_kgs());
            if (minBMI<19 && Math.abs(minBMI-Baseline.getBMI())>.1 && warningson) {
                System.out.println("low bmi warning");
                JOptionPane.showMessageDialog(this, "Warning: This simulation resulted in an unhealthy low Body Mass Index (BMI).","Low Body Mass Index Reached",JOptionPane.WARNING_MESSAGE);
            }

            System.out.println("END of recalc");
            calcflag = false;
        }
    }

    public void redo(){
        ChoicePanel.setSelectedIndex(0);
        if (!change.equals("weight"))JOptionPane.showMessageDialog(this,
                        "You can't achieve "+GoalWeight*weightunits+" "+gpanel.getweightstring()+ " in "+TimetoGoal+" days with " +GoalActChangeBox.getSelectedItem().toString()+" change in activity" +
                        "\nThe last change you made has been reset so that you can enter something different." +
                        "\nTry giving yourself more time to achieve your goal, increasing your activity level, or setting a different goal" +
                        "\n" +
                        "\nYou can disable checking your goal by deselecting the check box in the \"Set Your Goal Weight\" Panel","Unachievable Goal",JOptionPane.WARNING_MESSAGE);
                if (change.equals("goalweight")){
                    change=null;
                    System.out.println("Goal weight change");
                    //goalweightchange=false;

                    GoalWeight=OldGoalWeight;
                    GoalWeightField.setValue(GoalWeight*weightunits);
                    calcflag=false;
                    recalc();
                    return;
                }else if (change.equals("goaltime")){
                    change=null;
                    System.out.println("Goal time change");

                    System.out.println("OldTimetogoal="+OldTimetoGoal);
                    TimetoGoal=OldTimetoGoal;
                    System.out.println("Timetogoal="+TimetoGoal);

                    GoalTimeField.setValue(TimetoGoal);
                    //goaltimechange=false;
                    recalc();
                    return;
                }else if (change.equals("weight")){
                    change=null;
                    //weightchange=false;
                    //Baseline.setWeight_kgs(OldWeight);
                    GoalWeight=Baseline.getWeight_kgs();
                    GoalWeightField.setValue(Baseline.getWeight_kgs()*weightunits);
                    //InitialWeightField.setValue(Baseline.getWeight_kgs());
                    //InitialWeightField2.setValue(Baseline.getWeight_kgs());
                    //goaltimechange=false;
                    recalc();
                    return;
                }else if (change.equals("PAL")){
                    change=null;
                    //PALchange=false;
                    Baseline.setPAL(OldPAL);
                    InitialPALField.setValue(Baseline.getPAL());
                    calcflag=false;
                    recalc();
                    return;
                }else if (change.equals("RMR")){
                    change=null;
                    //RMRchange=false;
                    Baseline.setRMR(OldRMR);
                    if (RMRInputRadio.isSelected()){
                        calcflag=false;
                        recalc();
                    }
                    return;
                }else if (change.equals("goalact")){
                    change=null;
                    GoalIntervention.setactchangepercent(OldGoalActChange);
                    GoalActChangeBox.setSelectedItem(String.valueOf((int)OldGoalActChange)+"%");
                    recalc();
                    return;
                }else if (change.equals("goalact")){
                    change=null;
                    GoalMaintenanceIntervention.setactchangepercent(OldGoalActChange);
                    GoalMaintenanceActChangeBox.setSelectedItem(String.valueOf((int)OldGoalMaintActChange)+"%");
                    recalc();
                    return;
                }else if (change.equals("gender")){
                    change=null;
                    Baseline.setMale(OldMale);
                    GenderBox.setSelectedItem(OldMale? "Male":"Female");
                    recalc();
                    return;
                }
    }

    public Baseline getBaseline(){
            return this.Baseline;
    }

    public String getenergystring(){
        return energystring;
    }

    public double getweightunits(){
        return weightunits;
    }

    public void highlightIntervention1(){

            Intervention1DayField.setBackground(highlightgreen);
            Intervention1CaloriesField.setBackground(highlightgreen);
            Intervention1ActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));

           



            if (Intervention1SodiumInputRadio.isSelected()){
                Intervention1SodiumField.setBackground(highlightblue);
            }

            Intervention1CarbInPercField.setBackground(highlightblue);
           

    }


    
    public void unhighlightIntervention1(){

            Intervention1DayField.setBackground(Color.white);
            Intervention1CaloriesField.setBackground(Color.white);
            Intervention1ActChangeBox.setBorder(null);

            
            Intervention1SodiumField.setBackground(Color.white);
            

            Intervention1CarbInPercField.setBackground(Color.white);
           

    }

    public void highlightIntervention2(){
        Intervention2DayField.setBackground(highlightgreen);
        Intervention2CaloriesField.setBackground(highlightgreen);
        Intervention2CarbInPercField.setBackground(highlightblue);
        Intervention2ActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));

        if (Intervention2SodiumInputRadio.isSelected()){
            Intervention2SodiumField.setBackground(highlightblue);
        }
    }

    public void unhighlightIntervention2(){

            Intervention2DayField.setBackground(Color.white);
            Intervention2CaloriesField.setBackground(Color.white);
            Intervention2ActChangeBox.setBorder(null);

            
            Intervention2SodiumField.setBackground(Color.white);


            Intervention2CarbInPercField.setBackground(Color.white);


    }

    public void highlightgoal(){
        GoalWeightField.setBackground(highlightgreen);
            GoalTimeField.setBackground(highlightgreen);
            GoalActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
            GoalMaintenanceActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));

            GoalCalsField.setBackground(highlightyellow);
            GoalMaintCalsField.setBackground(highlightyellow);
    }

    public void unhighlightgoal(){
        GoalWeightField.setBackground(Color.white);
        GoalTimeField.setBackground(Color.white);

        GoalActChangeBox.setBorder(null);
        GoalMaintenanceActChangeBox.setBorder(null);


        GoalCalsField.setBackground(Color.white);
        GoalMaintCalsField.setBackground(Color.white);
    }

        private void InitialWeightFieldChange() {
        try {

            check = Double.parseDouble(InitialWeightField.getValue().toString()) / weightunits;
            if (check > 0 && check!=Baseline.getWeight_kgs() && calcflag == false) {
                //System.out.println("good initial weight");
                calcflag = true;
                change="weight";
                //weightchange=true;
                OldWeight=Baseline.getWeight_kgs();
                Baseline.setWeight_kgs(check);
                InitialWeightField.setValue(Baseline.getWeight_kgs() * weightunits);
                InitialWeightField2.setValue(Baseline.getWeight_kgs() * weightunits);
                //Baseline.print();
                recalc();

            } else if (check <= 0) {
                System.out.println("negative initial weight");
                calcflag = true;
                InitialWeightField.setValue(Baseline.getWeight_kgs() * weightunits);
                InitialWeightField2.setValue(Baseline.getWeight_kgs() * weightunits);
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            System.out.println("stupid initial weight");
            calcflag = true;
            InitialWeightField.setValue(Baseline.getWeight_kgs() * weightunits);
            InitialWeightField2.setValue(Baseline.getWeight_kgs() * weightunits);
            calcflag = false;
        }
    }

    private void InitialBfpChange() {
        try {
            if (InitialBfpInputRadio.isSelected()){
                check = Double.parseDouble(InitialBfpField.getValue().toString());
                if (check >= 0 && check <= 100 && check!=Baseline.getBfp() && !calcflag) {
                    Baseline.setcalculatedBfp(false);
                    Baseline.setBfp(check);
                    InitialBfpField2.setValue(Baseline.getBfp());
                    if (!calcflag) {
                        recalc();
                    }

                }else if (check < 0 || check > 100) {
                    calcflag = true;
                    InitialBfpField.setValue(Baseline.getBfp());
                    InitialBfpField2.setValue(Baseline.getBfp());
                    calcflag = false;
                }
            }else if (InitialBfpCalculatedRadio.isSelected()){

                Baseline.setcalculatedBfp(true);
                InitialBfpField.setValue(Baseline.getBfp());
                InitialBfpField2.setValue(Baseline.getBfp());

            }
        } catch (NumberFormatException e) {
            calcflag = true;
            InitialBfpField.setValue(Baseline.getBfp());
            InitialBfpField2.setValue(Baseline.getBfp());
            calcflag = false;
        }
    }

    private void CarbInPercChange() {
        try {
            check = Double.parseDouble(InitialCarbInPercField.getValue().toString());
            if (check > 0 && check <= 100 && check!=Baseline.getCarbInPercent()) {
                calcflag = true;
                Baseline.setCarbInPercent(check);
                InitialCarbInPercField.setValue(Baseline.getCarbInPercent());
                recalc();
            } else {
                calcflag = true;
                InitialCarbInPercField.setValue(Baseline.getCarbInPercent());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            InitialCarbInPercField.setValue(Baseline.getCarbInPercent());
            calcflag = false;
        }
    }

    private void InitialSodiumChange() {
        try {
            check = Double.parseDouble(InitialSodiumField.getValue().toString());
            if (check >= 0 && check<=50000 && check!=Baseline.getSodium() && !calcflag) {
                Baseline.setSodium(check);
                recalc();

            }else if (check < 0) {
                calcflag = true;
                InitialSodiumField.setValue(Baseline.getSodium());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            InitialSodiumField.setValue(Baseline.getSodium());
            calcflag = false;
        }
    }

    private void InitialRMRChange() {
        if (RMRInputRadio.isSelected()) {
            try {
                check = Double.parseDouble(InitialRMRField.getValue().toString())/energyunits;
                if (check > 0 && check!=Baseline.getRMR() && !calcflag) {
                    OldRMR=Baseline.getRMR();
                    change="RMR";
                    Baseline.setcalculatedRMR(false);
                    Baseline.setRMR(check);

                    recalc();
                }
            } catch (NumberFormatException e) {
                calcflag = true;
                InitialRMRField.setValue(Baseline.getRMR()*energyunits);

                calcflag = false;
            }
        } else if (!RMRInputRadio.isSelected()){
            if (!calcflag){
                Baseline.setcalculatedRMR(true);
                InitialRMRField.setValue(Baseline.getRMR()*energyunits);
                recalc();
            }
        }
    }

    private void AgeChange() {
        try {
            check = Double.parseDouble(AgeField.getValue().toString());
            if (check >= 18 && check!=Baseline.getAge() && !calcflag) {
                Baseline.setAge(check);
                recalc();
            } else if (check < 18 && check!=Baseline.getAge()) {
                calcflag = true;
                if (warningson) JOptionPane.showMessageDialog(this, "This applet is designed to simulate the weight of persons 18 years or older","Age too low",JOptionPane.WARNING_MESSAGE);
                AgeField.setValue(Baseline.getAge());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            AgeField.setValue(Baseline.getAge());
            calcflag = false;
        }
    }

    private void HeightChange() {
        try {
            check = Double.parseDouble(HeightField.getValue().toString()) / heightunits;
            if (check > 0 && check!= Baseline.getHeight_cms() && calcflag == false) {
                Baseline.setHeight(check);
                recalc();
            } else if (check <= 0) {
                calcflag = true;
                HeightField.setValue(Baseline.getHeight_cms() * heightunits);
                calcflag = false;
            }

        } catch (NumberFormatException e) {
            calcflag = true;
            HeightField.setValue(Baseline.getHeight_cms() * heightunits);
            calcflag = false;
        }
    }

    private void initPALChange() {
        //System.out.println("PAL CHANGED");
        try {
            check = Double.parseDouble(InitialPALField.getValue().toString()) ;
            if (check==Baseline.getPAL()){

            }else if(check >= 1 && check<=3 && calcflag == false) {
                Baseline.setPAL(check);
                recalc();
            } else if (check<1 || check >3) {
                calcflag = true;
                InitialPALField.setValue(Baseline.getPAL());
                calcflag = false;
            }

        } catch (NumberFormatException e) {
            calcflag = true;
            InitialPALField.setValue(Baseline.getPAL());
            calcflag = false;
        }
    }

    private void SimulationLengthChange() {
        try {
            check = Double.parseDouble(SimulationLengthField.getValue().toString());
            if (check >= 1 && check!=SimulationLength && !calcflag) {
                SimulationLength = check+1;
                //TimeStartMaxLabel.setText(String.valueOf((int) SimulationLength-2));
                recalc();
            } else if (check <1) {
                calcflag = true;
                SimulationLengthField.setValue(SimulationLength);
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            SimulationLengthField.setValue(SimulationLength);
            calcflag = false;
        }
    }

    private void GoalWeightChange() {
        try {
            check = Double.parseDouble(GoalWeightField.getValue().toString()) / weightunits;
            if (check > 0 && check!=GoalWeight && !calcflag) {
                change="goalweight";
                //goalweightchange=true;
                OldGoalWeight=GoalWeight;
                GoalWeight = check;
                recalc();
            } else if (check <= 0) {
                calcflag = true;
                GoalWeightField.setValue(GoalWeight * weightunits);
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            GoalWeightField.setValue(GoalWeight * weightunits);
            calcflag = false;
        }
    }

    private void GoalTimeChange() {
        try {
            check = Double.parseDouble(GoalTimeField.getValue().toString());
            if (check > 0 && check!=TimetoGoal && !calcflag) {
                System.out.println("Goaltimechange");
                change="goaltime";
                //goaltimechange=true;
                OldTimetoGoal=TimetoGoal;
                TimetoGoal=check;
                System.out.println("in change: OldTimetogoal="+OldTimetoGoal);
                System.out.println("in change: Timetogoal="+TimetoGoal);
                recalc();
            }else if (check <= 0) {
                calcflag = true;
                GoalTimeField.setValue(GoalMaintenanceIntervention.getday());
                calcflag = false;
            }
          } catch (NumberFormatException e) {
            calcflag = true;
            GoalTimeField.setValue(GoalMaintenanceIntervention.getday());
            calcflag = false;
        }
    }

    private void Intervention1DayChange() {
        try {
            check = Double.parseDouble(Intervention1DayField.getValue().toString());
            if (check > 0  && check!=Intervention1.getday() && !calcflag) {
                System.out.println("inter1 day change");
                Intervention1.setday((int) check);
                if (!goalsim) {
                    recalc();
                }
            }else if (check <= 0) {
                calcflag = true;
                Intervention1DayField.setValue(Intervention1.getday());
                calcflag = false;
            }
          } catch (NumberFormatException e) {
            calcflag = true;
            GoalTimeField.setValue(Intervention1.getday());
            calcflag = false;
        }
    }

    private void Intervention1CaloriesChange() {
        try {
            check = Double.parseDouble(Intervention1CaloriesField.getValue().toString())/energyunits;
            if (check >= 0 && check!= Intervention1.getcalories() && !calcflag) {
                Intervention1.setcalories(check);
                if (Intervention1SodiumCalculatedRadio.isSelected()){
                    Intervention1.setproportionalsodium(Baseline);
                    Intervention1SodiumField.setValue(Intervention1.getsodium());
                }
                if (Intervention1.ison() && !goalsim) {
                    recalc();
                }
            }else if (check < 0) {
                calcflag = true;
                Intervention1CaloriesField.setValue(Intervention1.getcalories()*energyunits);
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            Intervention1CaloriesField.setValue(Intervention1.getcalories()*energyunits);
            calcflag = false;
        }
    }

    private void Intervention1CarbInPercChange() {
        try {
            check = Double.parseDouble(Intervention1CarbInPercField.getValue().toString());
            if (check >= 0 && check<=100 && check!= Intervention1.getcarbinpercent() && !calcflag) {
                Intervention1.setcarbinpercent(check);
                if (Intervention1.ison() && !goalsim) {
                    recalc();
                }
            }else if (check < 0 || check>100) {
                calcflag = true;
                Intervention1CarbInPercField.setValue(Intervention2.getcarbinpercent());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            Intervention1CarbInPercField.setValue(Intervention1.getcarbinpercent());
            calcflag = false;
        }
    }

    private void Intervention1SodiumChange() {
        try {
            if (Intervention1SodiumInputRadio.isSelected()){
                check = Double.parseDouble(Intervention1SodiumField.getValue().toString());
                if (check >= 0 && check!= Intervention1.getsodium() && !calcflag) {
                    Intervention1.setsodium(check);
                    if (!goalsim && Intervention1.ison()){
                        recalc();
                    }

                }else if (check < 0) {
                    calcflag = true;
                    //Intervention1SodiumField.setValue(Baseline.getSodium());
                    calcflag = false;
                }
            }else if (Intervention1SodiumCalculatedRadio.isSelected()){
                calcflag=true;
                Intervention1.setproportionalsodium(Baseline);
                //System.out.println("inter1 set prop in SodiumFieldProperty, ="+Intervention1.getsodium());
                //Intervention1SodiumField.setValue(Baseline.getSodium());
                calcflag=false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            //Intervention1SodiumField.setValue(Baseline.getSodium());
            calcflag = false;
        }
    }

    private void Intervention2DayChange() {
        try {
            check = Double.parseDouble(Intervention2DayField.getValue().toString());
            if (check > 0 && check!=Intervention2.getday() && !calcflag) {
                Intervention2.setday((int)check);
                if (Intervention2.ison() && !goalsim) {
                    recalc();
                }
            }else if (check <= 0) {
                calcflag = true;
                Intervention2DayField.setValue(Intervention2.getday());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            Intervention2DayField.setValue(Intervention2.getday());
            calcflag = false;
        }
    }

    private void Intervention2CaloriesChange() {
        try {
            check = Double.parseDouble(Intervention2CaloriesField.getValue().toString())/energyunits;
            if (check >= 0 && check!= Intervention2.getcalories() && !calcflag) {
                Intervention2.setcalories(check);
                if (Intervention2SodiumCalculatedRadio.isSelected()){
                    Intervention2.setproportionalsodium(Baseline);
                    Intervention2SodiumField.setValue(Intervention2.getsodium());
                }
                if (Intervention2.ison() && !goalsim) {
                    recalc();
                }
            }else if (check < 0) {
                calcflag = true;
                Intervention2CaloriesField.setValue(Intervention2.getcalories()*energyunits);
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            Intervention2CaloriesField.setValue(Intervention2.getcalories()*energyunits);
            calcflag = false;
        }
    }

    private void Intervention2CarbInPercChange() {
        try {
            check = Double.parseDouble(Intervention2CarbInPercField.getValue().toString());
            if (check >= 0 && check<=100 && check!= Intervention2.getcarbinpercent() && !calcflag) {
                Intervention2.setcarbinpercent(check);
                if (Intervention2.ison() && !goalsim) {
                    recalc();
                }
            }else if (check <= 0 || check>100) {
                calcflag = true;
                Intervention2CarbInPercField.setValue(Intervention2.getcarbinpercent());
                calcflag = false;
            }
        } catch (NumberFormatException e) {
            calcflag = true;
            Intervention2CarbInPercField.setValue(Intervention2.getcarbinpercent());
            calcflag = false;
        }
    }

    private void Intervention2SodiumChange() {
        try {
            if (Intervention2SodiumInputRadio.isSelected()){
                //System.out.println("Input selected");
                check = Double.parseDouble(Intervention2SodiumField.getValue().toString());
                if (check >= 0 && check!=Intervention2.getsodium() && !calcflag) {
                    //System.out.println("Check fine");
                    Intervention2.setsodium(check);
                    if (!goalsim && Intervention2.ison()){
                        recalc();
                    }

                }else if (check < 0) {
                    //System.out.println("Check failed");
                    calcflag = true;
                    Intervention2SodiumField.setValue(Intervention2.getsodium());
                    calcflag = false;
                }
            }else if (Intervention2SodiumCalculatedRadio.isSelected()){
                //System.out.println("Calc selected");
                calcflag=true;
                Intervention2.setproportionalsodium(Baseline);
                Intervention2SodiumField.setValue(Intervention2.getsodium());
                calcflag=false;
            }
        } catch (NumberFormatException e) {
            //System.out.println("Check epic failed");
            calcflag = true;
            Intervention2SodiumField.setValue(Intervention2.getsodium());
            calcflag = false;
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

        WeightUnitsRadioGroup = new javax.swing.ButtonGroup();
        BfpRadioGroup = new javax.swing.ButtonGroup();
        RMRRadioGroup = new javax.swing.ButtonGroup();
        GenderRadioGroup = new javax.swing.ButtonGroup();
        HeightUnitsGroup = new javax.swing.ButtonGroup();
        Intervention1SodiumRadioGroup = new javax.swing.ButtonGroup();
        Intervention2SodiumRadioGroup = new javax.swing.ButtonGroup();
        EnergyUnitsRadioGroup = new javax.swing.ButtonGroup();
        BaselinePanel = new javax.swing.JPanel();
        InitialWeightLabel = new javax.swing.JLabel();
        PoundsRadio = new javax.swing.JRadioButton();
        KilogramsRadio = new javax.swing.JRadioButton();
        InitialWeightField = new javax.swing.JFormattedTextField();
        BaselineLabel = new javax.swing.JLabel();
        InitialBfpLabel = new javax.swing.JLabel();
        InitialBfpField = new javax.swing.JFormattedTextField();
        InitialBfpCalculatedRadio = new javax.swing.JRadioButton();
        InitialBfpInputRadio = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        MaintCalsLabel = new javax.swing.JLabel();
        MaintCalsField = new javax.swing.JFormattedTextField();
        InitialCarbInPercLabel = new javax.swing.JLabel();
        InitialCarbInPercField = new javax.swing.JFormattedTextField();
        InitialSodiumLabel = new javax.swing.JLabel();
        InitialSodiumField = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        InitialRMRLabel = new javax.swing.JLabel();
        InitialRMRField = new javax.swing.JFormattedTextField();
        RMRCalculatedRadio = new javax.swing.JRadioButton();
        RMRInputRadio = new javax.swing.JRadioButton();
        PALLabel = new javax.swing.JLabel();
        EstimatePALButton = new javax.swing.JButton();
        AgeLabel = new javax.swing.JLabel();
        AgeField = new javax.swing.JFormattedTextField();
        HeightLabel = new javax.swing.JLabel();
        HeightField = new javax.swing.JFormattedTextField();
        InchesRadio = new javax.swing.JRadioButton();
        CentimetersRadio = new javax.swing.JRadioButton();
        RMRSeparator = new javax.swing.JSeparator();
        BfpSeparator = new javax.swing.JSeparator();
        GenderBox = new javax.swing.JComboBox();
        CaloriesRadio = new javax.swing.JRadioButton();
        KilojoulesRadio = new javax.swing.JRadioButton();
        UncertaintyBox = new javax.swing.JComboBox();
        UncertaintyLabel = new javax.swing.JLabel();
        GenderLabel = new javax.swing.JLabel();
        InitialPALField = new javax.swing.JFormattedTextField();
        ChoicePanel = new javax.swing.JTabbedPane();
        GoalPanel = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        RunGoalButton = new javax.swing.JButton();
        GoalMaintenanceInterventionLabel = new javax.swing.JLabel();
        GoalStatementPanel = new javax.swing.JPanel();
        GoalCheckBox = new javax.swing.JCheckBox();
        GoalWeightLabel = new javax.swing.JLabel();
        GoalWeightField = new javax.swing.JFormattedTextField();
        GoalUnitsLabel = new javax.swing.JLabel();
        GoalTimeField = new javax.swing.JFormattedTextField();
        GoalDaysLabel = new javax.swing.JLabel();
        GoalCalsChangeField = new javax.swing.JFormattedTextField();
        GoalInterventionLabel = new javax.swing.JLabel();
        GoalMaintCalsChangeLabel = new javax.swing.JLabel();
        GoalMaintCalsChangeField = new javax.swing.JFormattedTextField();
        GoalActivityLabel = new javax.swing.JLabel();
        GoalActChangeBox = new javax.swing.JComboBox();
        GoalCalsField = new javax.swing.JFormattedTextField();
        GoalCaloriesLabel = new javax.swing.JLabel();
        GoalMaintenanceActivityLabel = new javax.swing.JLabel();
        GoalMaintenanceActChangeBox = new javax.swing.JComboBox();
        GoalMaintCalsLabel = new javax.swing.JLabel();
        GoalCalsChangeLabel = new javax.swing.JLabel();
        GoalSpaceLabel = new javax.swing.JLabel();
        GoalMaintCalsField = new javax.swing.JFormattedTextField();
        InterventionPanel = new javax.swing.JPanel();
        Intervention1Label = new javax.swing.JLabel();
        Intervention1CheckBox = new javax.swing.JCheckBox();
        Intervention1DayLabel = new javax.swing.JLabel();
        Intervention2DayField = new javax.swing.JFormattedTextField();
        Intervention1CaloriesLabel = new javax.swing.JLabel();
        Intervention2CaloriesField = new javax.swing.JFormattedTextField();
        Intervention1CarbInPercLabel = new javax.swing.JLabel();
        Intervention2CarbInPercField = new javax.swing.JFormattedTextField();
        Intervention1ActivityLabel = new javax.swing.JLabel();
        Intervention2ActChangeBox = new javax.swing.JComboBox();
        Intervention1SodiumLabel = new javax.swing.JLabel();
        SpaceLabel = new javax.swing.JLabel();
        Intervention1SodiumCalculatedRadio = new javax.swing.JRadioButton();
        Intervention1SodiumInputRadio = new javax.swing.JRadioButton();
        Intervention1SodiumField = new javax.swing.JFormattedTextField();
        Intervention2Label = new javax.swing.JLabel();
        Intervention2CheckBox = new javax.swing.JCheckBox();
        Intervention2DayLabel = new javax.swing.JLabel();
        Intervention1DayField = new javax.swing.JFormattedTextField();
        Intervention2CaloriesLabel = new javax.swing.JLabel();
        Intervention1CaloriesField = new javax.swing.JFormattedTextField();
        Intervention2CarbInPercLabel = new javax.swing.JLabel();
        Intervention1CarbInPercField = new javax.swing.JFormattedTextField();
        Intervention2ActivityLabel = new javax.swing.JLabel();
        Intervention1ActChangeBox = new javax.swing.JComboBox();
        Intervention2SodiumLabel = new javax.swing.JLabel();
        Intervention2SodiumCalculatedRadio = new javax.swing.JRadioButton();
        Intervention2SodiumInputRadio = new javax.swing.JRadioButton();
        Intervention2SodiumField = new javax.swing.JFormattedTextField();
        RunInterventionButton = new javax.swing.JButton();
        Intervention1SodiumSeparator = new javax.swing.JSeparator();
        Intervention2SodiumSeparator = new javax.swing.JSeparator();
        Intervention1RampCheckBox = new javax.swing.JCheckBox();
        Intervention2RampCheckBox = new javax.swing.JCheckBox();
        ComparePanel = new javax.swing.JPanel();
        InitialWeightLabel2 = new javax.swing.JLabel();
        InitialWeightField2 = new javax.swing.JFormattedTextField();
        InitialBfpLabel2 = new javax.swing.JLabel();
        InitialBMILabel = new javax.swing.JLabel();
        FinalWeightLabel = new javax.swing.JLabel();
        FinalBMILabel = new javax.swing.JLabel();
        FinalBMIField = new javax.swing.JFormattedTextField();
        FinalBfpLabel = new javax.swing.JLabel();
        FinalBfpField = new javax.swing.JFormattedTextField();
        FinalWeightField = new javax.swing.JFormattedTextField();
        InitialBfpField2 = new javax.swing.JFormattedTextField();
        InitialBMIField = new javax.swing.JFormattedTextField();
        ZoomPanel = new javax.swing.JPanel();
        VerticalZoomMinLabel = new javax.swing.JLabel();
        SimulationLengthLabel = new javax.swing.JLabel();
        SimulationLengthField = new javax.swing.JFormattedTextField();
        TimeStartLabel = new javax.swing.JLabel();
        StatusLabel = new javax.swing.JLabel();
        VerticalZoomSlider = new javax.swing.JSlider();
        VerticalZoomMaxLabel = new javax.swing.JLabel();
        TimeZoomMinLabel = new javax.swing.JLabel();
        TimeZoomSlider = new javax.swing.JSlider();
        TimeZoomMaxLabel = new javax.swing.JLabel();
        TimeStartMinLabel = new javax.swing.JLabel();
        TimeStartSlider = new javax.swing.JSlider();
        TimeStartMaxLabel = new javax.swing.JLabel();
        TimeZoomLabel = new javax.swing.JLabel();
        VerticalZoomLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        OptionsPanel = new javax.swing.JPanel();
        GraphAttachedCheckBox = new javax.swing.JCheckBox();
        DefaultButton = new javax.swing.JButton();
        GridCheckBox = new javax.swing.JCheckBox();
        ShowLegendCheckBox = new javax.swing.JCheckBox();
        UncertaintyCheckBox = new javax.swing.JCheckBox();
        HighlightCheckBox = new javax.swing.JCheckBox();
        AdvancedControlsCheckBox = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        OverviewButton = new javax.swing.JButton();
        DefaultColorsCheckBox = new javax.swing.JCheckBox();
        SaveButton = new javax.swing.JButton();

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        BaselinePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        BaselinePanel.setMinimumSize(new java.awt.Dimension(275, 393));
        BaselinePanel.setLayout(new java.awt.GridBagLayout());

        InitialWeightLabel.setText("Initial Weight");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialWeightLabel, gridBagConstraints);

        WeightUnitsRadioGroup.add(PoundsRadio);
        PoundsRadio.setText("Pounds");
        PoundsRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PoundsRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(PoundsRadio, gridBagConstraints);

        WeightUnitsRadioGroup.add(KilogramsRadio);
        KilogramsRadio.setSelected(true);
        KilogramsRadio.setText("Kilograms");
        KilogramsRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KilogramsRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(KilogramsRadio, gridBagConstraints);

        InitialWeightField.setBackground(highlightgreen);
        InitialWeightField.setColumns(3);
        InitialWeightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        InitialWeightField.setMinimumSize(new java.awt.Dimension(56, 28));
        InitialWeightField.setValue(Baseline.getWeight_kgs());
        InitialWeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialWeightFieldActionPerformed(evt);
            }
        });
        InitialWeightField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialWeightFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialWeightField, gridBagConstraints);

        BaselineLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        BaselineLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BaselineLabel.setText("Enter Baseline Information");
        BaselineLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 20;
        BaselinePanel.add(BaselineLabel, gridBagConstraints);

        InitialBfpLabel.setText("Initial Body Fat %");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialBfpLabel, gridBagConstraints);

        InitialBfpField.setColumns(3);
        InitialBfpField.setEditable(false);
        InitialBfpField.setForeground(new java.awt.Color(102, 102, 102));
        InitialBfpField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        InitialBfpField.setMinimumSize(new java.awt.Dimension(56, 28));
        InitialBfpField.setValue(Baseline.getBfp());
        InitialBfpField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialBfpFieldActionPerformed(evt);
            }
        });
        InitialBfpField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialBfpFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialBfpField, gridBagConstraints);

        BfpRadioGroup.add(InitialBfpCalculatedRadio);
        InitialBfpCalculatedRadio.setSelected(true);
        InitialBfpCalculatedRadio.setText("Automatic");
        InitialBfpCalculatedRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialBfpCalculatedRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialBfpCalculatedRadio, gridBagConstraints);

        BfpRadioGroup.add(InitialBfpInputRadio);
        InitialBfpInputRadio.setText("Input");
        InitialBfpInputRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialBfpInputRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialBfpInputRadio, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        BaselinePanel.add(jSeparator1, gridBagConstraints);

        MaintCalsLabel.setText("Baseline Diet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(MaintCalsLabel, gridBagConstraints);

        MaintCalsField.setBackground(highlightyellow);
        MaintCalsField.setColumns(4);
        MaintCalsField.setEditable(false);
        MaintCalsField.setForeground(grey);
        MaintCalsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        MaintCalsField.setToolTipText("This is what you should eat daily in order to maintain your current weight");
        MaintCalsField.setMinimumSize(new java.awt.Dimension(56, 28));
        MaintCalsField.setValue(Baseline.getMaintCals());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(MaintCalsField, gridBagConstraints);

        InitialCarbInPercLabel.setText("% Calories from Carbs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialCarbInPercLabel, gridBagConstraints);

        InitialCarbInPercField.setBackground(highlightblue);
        InitialCarbInPercField.setColumns(3);
        InitialCarbInPercField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        InitialCarbInPercField.setMinimumSize(new java.awt.Dimension(56, 28));
        InitialCarbInPercField.setValue(Baseline.getCarbInPercent());
        InitialCarbInPercField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialCarbInPercFieldActionPerformed(evt);
            }
        });
        InitialCarbInPercField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialCarbInPercFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialCarbInPercField, gridBagConstraints);

        InitialSodiumLabel.setText("Initial Sodium (mg/day)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialSodiumLabel, gridBagConstraints);

        InitialSodiumField.setBackground(highlightblue);
        InitialSodiumField.setColumns(3);
        InitialSodiumField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        InitialSodiumField.setMinimumSize(new java.awt.Dimension(56, 28));
        InitialSodiumField.setValue(Baseline.getSodium());
        InitialSodiumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialSodiumFieldActionPerformed(evt);
            }
        });
        InitialSodiumField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialSodiumFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialSodiumField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        BaselinePanel.add(jSeparator2, gridBagConstraints);

        InitialRMRLabel.setText("Initial RMR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialRMRLabel, gridBagConstraints);

        InitialRMRField.setColumns(3);
        InitialRMRField.setEditable(false);
        InitialRMRField.setForeground(new java.awt.Color(102, 102, 102));
        InitialRMRField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        InitialRMRField.setMinimumSize(new java.awt.Dimension(56, 28));
        InitialRMRField.setValue(Baseline.getRMR());
        InitialRMRField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialRMRFieldActionPerformed(evt);
            }
        });
        InitialRMRField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialRMRFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialRMRField, gridBagConstraints);

        RMRRadioGroup.add(RMRCalculatedRadio);
        RMRCalculatedRadio.setSelected(true);
        RMRCalculatedRadio.setText("Automatic");
        RMRCalculatedRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RMRCalculatedRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(RMRCalculatedRadio, gridBagConstraints);

        RMRRadioGroup.add(RMRInputRadio);
        RMRInputRadio.setText("Input");
        RMRInputRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RMRInputRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(RMRInputRadio, gridBagConstraints);

        PALLabel.setText("Physical Activity Level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(PALLabel, gridBagConstraints);

        EstimatePALButton.setText("Estimate Activity Level");
        EstimatePALButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EstimatePALButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        BaselinePanel.add(EstimatePALButton, gridBagConstraints);

        AgeLabel.setText("Age (years)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(AgeLabel, gridBagConstraints);

        AgeField.setBackground(highlightgreen);
        AgeField.setColumns(3);
        AgeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        AgeField.setMinimumSize(new java.awt.Dimension(56, 28));
        AgeField.setValue(Baseline.getAge());
        AgeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgeFieldActionPerformed(evt);
            }
        });
        AgeField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                AgeFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(AgeField, gridBagConstraints);

        HeightLabel.setText("Height");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(HeightLabel, gridBagConstraints);

        HeightField.setBackground(highlightgreen);
        HeightField.setColumns(3);
        HeightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        HeightField.setMinimumSize(new java.awt.Dimension(56, 28));
        HeightField.setValue(Baseline.getHeight_cms());
        HeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HeightFieldActionPerformed(evt);
            }
        });
        HeightField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                HeightFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(HeightField, gridBagConstraints);

        HeightUnitsGroup.add(InchesRadio);
        InchesRadio.setText("Inches");
        InchesRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InchesRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InchesRadio, gridBagConstraints);

        HeightUnitsGroup.add(CentimetersRadio);
        CentimetersRadio.setSelected(true);
        CentimetersRadio.setText("Centimeters");
        CentimetersRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CentimetersRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(CentimetersRadio, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 100;
        BaselinePanel.add(RMRSeparator, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 100;
        BaselinePanel.add(BfpSeparator, gridBagConstraints);

        GenderBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Male", "Female" }));
        GenderBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
        GenderBox.setNextFocusableComponent(AgeField);
        GenderBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenderBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        BaselinePanel.add(GenderBox, gridBagConstraints);

        EnergyUnitsRadioGroup.add(CaloriesRadio);
        CaloriesRadio.setText("Calories/day");
        CaloriesRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CaloriesRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(CaloriesRadio, gridBagConstraints);

        EnergyUnitsRadioGroup.add(KilojoulesRadio);
        KilojoulesRadio.setSelected(true);
        KilojoulesRadio.setText("Kilojoules/day");
        KilojoulesRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KilojoulesRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(KilojoulesRadio, gridBagConstraints);

        UncertaintyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5%", "10%", "15%", "20%", "25%" }));
        UncertaintyBox.setSelectedIndex(1);
        UncertaintyBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UncertaintyBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(UncertaintyBox, gridBagConstraints);

        UncertaintyLabel.setText("Uncertainty Range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(UncertaintyLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        BaselinePanel.add(GenderLabel, gridBagConstraints);
        GenderLabel.setVisible(false);

        InitialPALField.setBackground(highlightgreen);
        InitialPALField.setColumns(3);
        InitialPALField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        InitialPALField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialPALFieldActionPerformed(evt);
            }
        });
        InitialPALField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InitialPALFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        BaselinePanel.add(InitialPALField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        add(BaselinePanel, gridBagConstraints);

        ChoicePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ChoicePanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ChoicePanelPropertyChange(evt);
            }
        });

        GoalPanel.setMinimumSize(new java.awt.Dimension(594, 229));
        GoalPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        GoalPanel.add(jSeparator4, gridBagConstraints);

        RunGoalButton.setText("Run Simulation");
        RunGoalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunGoalButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        GoalPanel.add(RunGoalButton, gridBagConstraints);

        GoalMaintenanceInterventionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        GoalMaintenanceInterventionLabel.setText("Goal Maintenance Phase");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        GoalPanel.add(GoalMaintenanceInterventionLabel, gridBagConstraints);

        GoalCheckBox.setSelected(true);
        GoalCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoalCheckBoxActionPerformed(evt);
            }
        });
        GoalStatementPanel.add(GoalCheckBox);

        GoalWeightLabel.setText("My goal is to weigh");
        GoalStatementPanel.add(GoalWeightLabel);

        GoalWeightField.setBackground(highlightgreen);
        GoalWeightField.setColumns(3);
        GoalWeightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        GoalWeightField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalWeightField.setValue(60);
        GoalWeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoalWeightFieldActionPerformed(evt);
            }
        });
        GoalWeightField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                GoalWeightFieldPropertyChange(evt);
            }
        });
        GoalStatementPanel.add(GoalWeightField);

        GoalUnitsLabel.setText("kg in");
        GoalStatementPanel.add(GoalUnitsLabel);

        GoalTimeField.setBackground(highlightgreen);
        GoalTimeField.setColumns(3);
        GoalTimeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        GoalTimeField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalTimeField.setValue(365);
        GoalTimeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoalTimeFieldActionPerformed(evt);
            }
        });
        GoalTimeField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                GoalTimeFieldPropertyChange(evt);
            }
        });
        GoalStatementPanel.add(GoalTimeField);

        GoalDaysLabel.setText("days");
        GoalStatementPanel.add(GoalDaysLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        GoalPanel.add(GoalStatementPanel, gridBagConstraints);

        GoalCalsChangeField.setColumns(4);
        GoalCalsChangeField.setEditable(false);
        GoalCalsChangeField.setForeground(grey);
        GoalCalsChangeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        GoalCalsChangeField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalCalsChangeField.setNextFocusableComponent(GoalMaintenanceActChangeBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalCalsChangeField, gridBagConstraints);

        GoalInterventionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        GoalInterventionLabel.setText("Weight Change Phase");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        GoalPanel.add(GoalInterventionLabel, gridBagConstraints);

        GoalMaintCalsChangeLabel.setText("Calories, which is a change of");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalMaintCalsChangeLabel, gridBagConstraints);

        GoalMaintCalsChangeField.setColumns(4);
        GoalMaintCalsChangeField.setEditable(false);
        GoalMaintCalsChangeField.setForeground(grey);
        GoalMaintCalsChangeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        GoalMaintCalsChangeField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalMaintCalsChangeField.setNextFocusableComponent(RunGoalButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalMaintCalsChangeField, gridBagConstraints);

        GoalActivityLabel.setText("If you change your physical activity by");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalActivityLabel, gridBagConstraints);

        GoalActChangeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-100%", "-80%", "-60%", "-40%", "-20%", "0%", "Detailed...", "20%", "40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "220%", "240%", "260%", "280%", "300%", "320%", "340%", "360%", "380%", "400%" }));
        GoalActChangeBox.setSelectedIndex(5);
        GoalActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
        GoalActChangeBox.setNextFocusableComponent(GoalCalsField);
        GoalActChangeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoalActChangeBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        GoalPanel.add(GoalActChangeBox, gridBagConstraints);

        GoalCalsField.setBackground(highlightyellow);
        GoalCalsField.setColumns(4);
        GoalCalsField.setEditable(false);
        GoalCalsField.setForeground(grey);
        GoalCalsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        GoalCalsField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalCalsField.setNextFocusableComponent(GoalCalsChangeField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalCalsField, gridBagConstraints);

        GoalCaloriesLabel.setText("you can meet your goal by eating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalCaloriesLabel, gridBagConstraints);

        GoalMaintenanceActivityLabel.setText("If you permanently change your activity by ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalMaintenanceActivityLabel, gridBagConstraints);

        GoalMaintenanceActChangeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-100%", "-80%", "-60%", "-40%", "-20%", "0%", "Detailed...", "20%", "40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "220%", "240%", "260%", "280%", "300%", "320%", "340%", "360%", "380%", "400%" }));
        GoalMaintenanceActChangeBox.setSelectedIndex(5);
        GoalMaintenanceActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
        GoalMaintenanceActChangeBox.setNextFocusableComponent(GoalMaintCalsField);
        GoalMaintenanceActChangeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoalMaintenanceActChangeBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        GoalPanel.add(GoalMaintenanceActChangeBox, gridBagConstraints);

        GoalMaintCalsLabel.setText("you can maintain your goal by eating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalMaintCalsLabel, gridBagConstraints);

        GoalCalsChangeLabel.setText("Calories, which is a change of");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalCalsChangeLabel, gridBagConstraints);

        GoalSpaceLabel.setText("     ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        GoalPanel.add(GoalSpaceLabel, gridBagConstraints);

        GoalMaintCalsField.setBackground(highlightyellow);
        GoalMaintCalsField.setColumns(4);
        GoalMaintCalsField.setEditable(false);
        GoalMaintCalsField.setForeground(grey);
        GoalMaintCalsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        GoalMaintCalsField.setMinimumSize(new java.awt.Dimension(56, 28));
        GoalMaintCalsField.setNextFocusableComponent(GoalMaintCalsChangeField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        GoalPanel.add(GoalMaintCalsField, gridBagConstraints);

        ChoicePanel.addTab("Set Your Goal Weight", GoalPanel);

        InterventionPanel.setLayout(new java.awt.GridBagLayout());

        Intervention1Label.setText("First Change");
        InterventionPanel.add(Intervention1Label, new java.awt.GridBagConstraints());

        Intervention1CheckBox.setSelected(true);
        Intervention1CheckBox.setText("On");
        Intervention1CheckBox.setNextFocusableComponent(Intervention1DayField);
        Intervention1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1CheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1CheckBox, gridBagConstraints);

        Intervention1DayLabel.setText("Start Change on Day:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1DayLabel, gridBagConstraints);

        Intervention2DayField.setBackground(highlightgreen);
        Intervention2DayField.setColumns(3);
        Intervention2DayField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention2DayField.setNextFocusableComponent(Intervention2CaloriesField);
        Intervention2DayField.setValue(300);
        Intervention2DayField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2DayFieldActionPerformed(evt);
            }
        });
        Intervention2DayField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention2DayFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2DayField, gridBagConstraints);

        Intervention1CaloriesLabel.setText("New Calories");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1CaloriesLabel, gridBagConstraints);

        Intervention2CaloriesField.setBackground(highlightgreen);
        Intervention2CaloriesField.setColumns(4);
        Intervention2CaloriesField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention2CaloriesField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention2CaloriesField.setNextFocusableComponent(Intervention2CarbInPercField);
        Intervention2CaloriesField.setValue(2500);
        Intervention2CaloriesField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2CaloriesFieldActionPerformed(evt);
            }
        });
        Intervention2CaloriesField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention2CaloriesFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2CaloriesField, gridBagConstraints);

        Intervention1CarbInPercLabel.setText("New Diet Carb %");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1CarbInPercLabel, gridBagConstraints);

        Intervention2CarbInPercField.setBackground(highlightblue);
        Intervention2CarbInPercField.setColumns(3);
        Intervention2CarbInPercField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention2CarbInPercField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention2CarbInPercField.setNextFocusableComponent(Intervention2ActChangeBox);
        Intervention2CarbInPercField.setValue(25);
        Intervention2CarbInPercField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2CarbInPercFieldActionPerformed(evt);
            }
        });
        Intervention2CarbInPercField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention2CarbInPercFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2CarbInPercField, gridBagConstraints);

        Intervention1ActivityLabel.setText("% Change in Physical Activity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1ActivityLabel, gridBagConstraints);

        Intervention2ActChangeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-100", "-80", "-60", "-40", "-20", "0", "Detailed...", "20", "40", "60", "80", "100", "120", "140", "160", "180", "200", "220", "240", "260", "280", "300", "320", "340", "360", "380", "400" }));
        Intervention2ActChangeBox.setSelectedIndex(5);
        Intervention2ActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
        Intervention2ActChangeBox.setNextFocusableComponent(Intervention2SodiumField);
        Intervention2ActChangeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2ActChangeBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2ActChangeBox, gridBagConstraints);

        Intervention1SodiumLabel.setText("New Sodium (mg/day)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1SodiumLabel, gridBagConstraints);

        SpaceLabel.setText("       ");
        InterventionPanel.add(SpaceLabel, new java.awt.GridBagConstraints());

        Intervention1SodiumRadioGroup.add(Intervention1SodiumCalculatedRadio);
        Intervention1SodiumCalculatedRadio.setSelected(true);
        Intervention1SodiumCalculatedRadio.setText("Automatic");
        Intervention1SodiumCalculatedRadio.setNextFocusableComponent(Intervention1SodiumInputRadio);
        Intervention1SodiumCalculatedRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1SodiumCalculatedRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1SodiumCalculatedRadio, gridBagConstraints);

        Intervention1SodiumRadioGroup.add(Intervention1SodiumInputRadio);
        Intervention1SodiumInputRadio.setText("Input");
        Intervention1SodiumInputRadio.setNextFocusableComponent(Intervention2CheckBox);
        Intervention1SodiumInputRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1SodiumInputRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1SodiumInputRadio, gridBagConstraints);

        Intervention1SodiumField.setColumns(3);
        Intervention1SodiumField.setEditable(false);
        Intervention1SodiumField.setForeground(new java.awt.Color(102, 102, 102));
        Intervention1SodiumField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention1SodiumField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention1SodiumField.setNextFocusableComponent(Intervention1SodiumCalculatedRadio);
        Intervention1SodiumField.setValue(4000);
        Intervention1SodiumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1SodiumFieldActionPerformed(evt);
            }
        });
        Intervention1SodiumField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention1SodiumFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1SodiumField, gridBagConstraints);

        Intervention2Label.setText("Second Change");
        InterventionPanel.add(Intervention2Label, new java.awt.GridBagConstraints());

        Intervention2CheckBox.setSelected(true);
        Intervention2CheckBox.setText("On");
        Intervention2CheckBox.setNextFocusableComponent(Intervention2DayField);
        Intervention2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2CheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2CheckBox, gridBagConstraints);

        Intervention2DayLabel.setText("Start Change on Day:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2DayLabel, gridBagConstraints);

        Intervention1DayField.setBackground(highlightgreen);
        Intervention1DayField.setColumns(3);
        Intervention1DayField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention1DayField.setNextFocusableComponent(Intervention1CaloriesField);
        Intervention1DayField.setValue(100);
        Intervention1DayField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1DayFieldActionPerformed(evt);
            }
        });
        Intervention1DayField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention1DayFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1DayField, gridBagConstraints);

        Intervention2CaloriesLabel.setText("New Calories");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2CaloriesLabel, gridBagConstraints);

        Intervention1CaloriesField.setBackground(highlightgreen);
        Intervention1CaloriesField.setColumns(4);
        Intervention1CaloriesField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention1CaloriesField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention1CaloriesField.setNextFocusableComponent(Intervention1CarbInPercField);
        Intervention1CaloriesField.setValue(2000);
        Intervention1CaloriesField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1CaloriesFieldActionPerformed(evt);
            }
        });
        Intervention1CaloriesField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention1CaloriesFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1CaloriesField, gridBagConstraints);

        Intervention2CarbInPercLabel.setText("New Diet Carb %");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2CarbInPercLabel, gridBagConstraints);

        Intervention1CarbInPercField.setBackground(highlightblue);
        Intervention1CarbInPercField.setColumns(3);
        Intervention1CarbInPercField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention1CarbInPercField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention1CarbInPercField.setNextFocusableComponent(Intervention1ActChangeBox);
        Intervention1CarbInPercField.setValue(50);
        Intervention1CarbInPercField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1CarbInPercFieldActionPerformed(evt);
            }
        });
        Intervention1CarbInPercField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention1CarbInPercFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1CarbInPercField, gridBagConstraints);

        Intervention2ActivityLabel.setText("% Change in Physical Activity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2ActivityLabel, gridBagConstraints);

        Intervention1ActChangeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-100", "-80", "-60", "-40", "-20", "0", "Detailed...", "20", "40", "60", "80", "100", "120", "140", "160", "180", "200", "220", "240", "260", "280", "300", "320", "340", "360", "380", "400" }));
        Intervention1ActChangeBox.setSelectedIndex(5);
        Intervention1ActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
        Intervention1ActChangeBox.setNextFocusableComponent(Intervention1SodiumField);
        Intervention1ActChangeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1ActChangeBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1ActChangeBox, gridBagConstraints);

        Intervention2SodiumLabel.setText("New Sodium (mg/day)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2SodiumLabel, gridBagConstraints);

        Intervention2SodiumRadioGroup.add(Intervention2SodiumCalculatedRadio);
        Intervention2SodiumCalculatedRadio.setSelected(true);
        Intervention2SodiumCalculatedRadio.setText("Automatic");
        Intervention2SodiumCalculatedRadio.setNextFocusableComponent(Intervention2SodiumInputRadio);
        Intervention2SodiumCalculatedRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2SodiumCalculatedRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2SodiumCalculatedRadio, gridBagConstraints);

        Intervention2SodiumRadioGroup.add(Intervention2SodiumInputRadio);
        Intervention2SodiumInputRadio.setText("Input");
        Intervention2SodiumInputRadio.setNextFocusableComponent(RunInterventionButton);
        Intervention2SodiumInputRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2SodiumInputRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2SodiumInputRadio, gridBagConstraints);

        Intervention2SodiumField.setColumns(3);
        Intervention2SodiumField.setEditable(false);
        Intervention2SodiumField.setForeground(new java.awt.Color(102, 102, 102));
        Intervention2SodiumField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        Intervention2SodiumField.setMinimumSize(new java.awt.Dimension(56, 28));
        Intervention2SodiumField.setNextFocusableComponent(Intervention2SodiumCalculatedRadio);
        Intervention2SodiumField.setValue(4000);
        Intervention2SodiumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2SodiumFieldActionPerformed(evt);
            }
        });
        Intervention2SodiumField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Intervention2SodiumFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2SodiumField, gridBagConstraints);

        RunInterventionButton.setText("Run  Simulation");
        RunInterventionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunInterventionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        InterventionPanel.add(RunInterventionButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 100;
        InterventionPanel.add(Intervention1SodiumSeparator, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 100;
        InterventionPanel.add(Intervention2SodiumSeparator, gridBagConstraints);

        Intervention1RampCheckBox.setText("Gradually Ramp Changes");
        Intervention1RampCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention1RampCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention1RampCheckBox, gridBagConstraints);

        Intervention2RampCheckBox.setText("Gradually Ramp Changes");
        Intervention2RampCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Intervention2RampCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        InterventionPanel.add(Intervention2RampCheckBox, gridBagConstraints);

        ChoicePanel.addTab("...or Specify a Lifestyle Change", InterventionPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.ipady = 20;
        add(ChoicePanel, gridBagConstraints);

        ComparePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ComparePanel.setLayout(new java.awt.GridBagLayout());

        InitialWeightLabel2.setText("Initial Weight");
        ComparePanel.add(InitialWeightLabel2, new java.awt.GridBagConstraints());

        InitialWeightField2.setColumns(3);
        InitialWeightField2.setEditable(false);
        InitialWeightField2.setForeground(new java.awt.Color(102, 102, 102));
        InitialWeightField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        ComparePanel.add(InitialWeightField2, gridBagConstraints);

        InitialBfpLabel2.setText("Initial Fat %");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        ComparePanel.add(InitialBfpLabel2, gridBagConstraints);

        InitialBMILabel.setText("Initial BMI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        ComparePanel.add(InitialBMILabel, gridBagConstraints);

        FinalWeightLabel.setText("Final Weight");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalWeightLabel, gridBagConstraints);

        FinalBMILabel.setText("Final BMI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalBMILabel, gridBagConstraints);

        FinalBMIField.setBackground(highlightyellow);
        FinalBMIField.setColumns(3);
        FinalBMIField.setEditable(false);
        FinalBMIField.setForeground(new java.awt.Color(102, 102, 102));
        FinalBMIField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalBMIField, gridBagConstraints);

        FinalBfpLabel.setText("Final Fat %");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalBfpLabel, gridBagConstraints);

        FinalBfpField.setBackground(highlightyellow);
        FinalBfpField.setColumns(3);
        FinalBfpField.setEditable(false);
        FinalBfpField.setForeground(new java.awt.Color(102, 102, 102));
        FinalBfpField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalBfpField, gridBagConstraints);

        FinalWeightField.setBackground(highlightyellow);
        FinalWeightField.setColumns(3);
        FinalWeightField.setEditable(false);
        FinalWeightField.setForeground(new java.awt.Color(102, 102, 102));
        FinalWeightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        ComparePanel.add(FinalWeightField, gridBagConstraints);

        InitialBfpField2.setColumns(3);
        InitialBfpField2.setEditable(false);
        InitialBfpField2.setForeground(new java.awt.Color(102, 102, 102));
        InitialBfpField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        ComparePanel.add(InitialBfpField2, gridBagConstraints);

        InitialBMIField.setColumns(3);
        InitialBMIField.setEditable(false);
        InitialBMIField.setForeground(new java.awt.Color(102, 102, 102));
        InitialBMIField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        ComparePanel.add(InitialBMIField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(ComparePanel, gridBagConstraints);

        ZoomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ZoomPanel.setLayout(new java.awt.GridBagLayout());

        VerticalZoomMinLabel.setText("Zoomed Out");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ZoomPanel.add(VerticalZoomMinLabel, gridBagConstraints);

        SimulationLengthLabel.setText("Length of Simulation (days)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        ZoomPanel.add(SimulationLengthLabel, gridBagConstraints);

        SimulationLengthField.setBackground(highlightgreen);
        SimulationLengthField.setColumns(4);
        SimulationLengthField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        SimulationLengthField.setValue(400);
        SimulationLengthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimulationLengthFieldActionPerformed(evt);
            }
        });
        SimulationLengthField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SimulationLengthFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        ZoomPanel.add(SimulationLengthField, gridBagConstraints);

        TimeStartLabel.setText("First Day Graphed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        ZoomPanel.add(TimeStartLabel, gridBagConstraints);

        StatusLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        StatusLabel.setForeground(new java.awt.Color(0, 153, 0));
        StatusLabel.setText("Goal Simulation Displayed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        ZoomPanel.add(StatusLabel, gridBagConstraints);

        VerticalZoomSlider.setMinorTickSpacing(10);
        VerticalZoomSlider.setPaintTicks(true);
        VerticalZoomSlider.setValue(0);
        VerticalZoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                VerticalZoomSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        ZoomPanel.add(VerticalZoomSlider, gridBagConstraints);

        VerticalZoomMaxLabel.setText("Zoomed In");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ZoomPanel.add(VerticalZoomMaxLabel, gridBagConstraints);

        TimeZoomMinLabel.setText("Zoomed Out");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ZoomPanel.add(TimeZoomMinLabel, gridBagConstraints);

        TimeZoomSlider.setMinorTickSpacing(10);
        TimeZoomSlider.setPaintTicks(true);
        TimeZoomSlider.setValue(0);
        TimeZoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TimeZoomSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        ZoomPanel.add(TimeZoomSlider, gridBagConstraints);

        TimeZoomMaxLabel.setText("Zoomed In");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ZoomPanel.add(TimeZoomMaxLabel, gridBagConstraints);

        TimeStartMinLabel.setText("Min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ZoomPanel.add(TimeStartMinLabel, gridBagConstraints);

        TimeStartSlider.setMinorTickSpacing(10);
        TimeStartSlider.setPaintTicks(true);
        TimeStartSlider.setValue(0);
        TimeStartSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TimeStartSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        ZoomPanel.add(TimeStartSlider, gridBagConstraints);

        TimeStartMaxLabel.setText("Max");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ZoomPanel.add(TimeStartMaxLabel, gridBagConstraints);

        TimeZoomLabel.setText("Horizontal Zoom");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        ZoomPanel.add(TimeZoomLabel, gridBagConstraints);

        VerticalZoomLabel.setText("Vertical Zoom");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        ZoomPanel.add(VerticalZoomLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        add(ZoomPanel, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(20, 300));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 250));
        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel2, gridBagConstraints);

        OptionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        OptionsPanel.setLayout(new java.awt.GridBagLayout());

        GraphAttachedCheckBox.setSelected(true);
        GraphAttachedCheckBox.setText("Graph Attached");
        GraphAttachedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GraphAttachedCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(GraphAttachedCheckBox, gridBagConstraints);

        DefaultButton.setText("Restore Defaults");
        DefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DefaultButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        OptionsPanel.add(DefaultButton, gridBagConstraints);

        GridCheckBox.setText("Show Grid");
        GridCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GridCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(GridCheckBox, gridBagConstraints);

        ShowLegendCheckBox.setSelected(true);
        ShowLegendCheckBox.setText("Show Legend");
        ShowLegendCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowLegendCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(ShowLegendCheckBox, gridBagConstraints);

        UncertaintyCheckBox.setText("Show Weight Range");
        UncertaintyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UncertaintyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(UncertaintyCheckBox, gridBagConstraints);

        HighlightCheckBox.setSelected(true);
        HighlightCheckBox.setText("Highlights On");
        HighlightCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HighlightCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(HighlightCheckBox, gridBagConstraints);

        AdvancedControlsCheckBox.setSelected(true);
        AdvancedControlsCheckBox.setText("Advanced Controls");
        AdvancedControlsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdvancedControlsCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(AdvancedControlsCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        OptionsPanel.add(jSeparator3, gridBagConstraints);

        OverviewButton.setText("Help");
        OverviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OverviewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        OptionsPanel.add(OverviewButton, gridBagConstraints);

        DefaultColorsCheckBox.setSelected(true);
        DefaultColorsCheckBox.setText("Default Colors");
        DefaultColorsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DefaultColorsCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        OptionsPanel.add(DefaultColorsCheckBox, gridBagConstraints);

        SaveButton.setText("Save...");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        OptionsPanel.add(SaveButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(OptionsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void HeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HeightFieldActionPerformed
        HeightChange();
    }//GEN-LAST:event_HeightFieldActionPerformed

    private void InitialBfpFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialBfpFieldActionPerformed
        InitialBfpChange();
    }//GEN-LAST:event_InitialBfpFieldActionPerformed

    private void InitialCarbInPercFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialCarbInPercFieldActionPerformed
        CarbInPercChange();
    }//GEN-LAST:event_InitialCarbInPercFieldActionPerformed

    private void InitialSodiumFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialSodiumFieldActionPerformed
        InitialSodiumChange();
    }//GEN-LAST:event_InitialSodiumFieldActionPerformed

    private void InitialRMRFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialRMRFieldActionPerformed
        InitialRMRChange();
    }//GEN-LAST:event_InitialRMRFieldActionPerformed

    private void KilogramsRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KilogramsRadioActionPerformed
        WeightUnitsChange();
    }//GEN-LAST:event_KilogramsRadioActionPerformed

    private void PoundsRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PoundsRadioActionPerformed
        WeightUnitsChange();
    }//GEN-LAST:event_PoundsRadioActionPerformed

    private void InitialBfpCalculatedRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialBfpCalculatedRadioActionPerformed
        BfpRadioChange();
    }//GEN-LAST:event_InitialBfpCalculatedRadioActionPerformed

    private void InitialBfpInputRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialBfpInputRadioActionPerformed
        BfpRadioChange();
    }//GEN-LAST:event_InitialBfpInputRadioActionPerformed

    private void RMRCalculatedRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RMRCalculatedRadioActionPerformed
        RMRRadioChange();
    }//GEN-LAST:event_RMRCalculatedRadioActionPerformed

    private void RMRInputRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RMRInputRadioActionPerformed
        RMRRadioChange();
    }//GEN-LAST:event_RMRInputRadioActionPerformed

    private void AgeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgeFieldActionPerformed
        AgeChange();
    }//GEN-LAST:event_AgeFieldActionPerformed

    private void InchesRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InchesRadioActionPerformed
        HeightUnitsChange();
    }//GEN-LAST:event_InchesRadioActionPerformed

    private void CentimetersRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CentimetersRadioActionPerformed
        HeightUnitsChange();
    }//GEN-LAST:event_CentimetersRadioActionPerformed

    private void Intervention1SodiumCalculatedRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1SodiumCalculatedRadioActionPerformed
        Intervention1SodiumRadioChange();
    }//GEN-LAST:event_Intervention1SodiumCalculatedRadioActionPerformed

    private void Intervention2SodiumCalculatedRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2SodiumCalculatedRadioActionPerformed
        Intervention2SodiumRadioChange();
    }//GEN-LAST:event_Intervention2SodiumCalculatedRadioActionPerformed

    private void Intervention1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1CheckBoxActionPerformed
        if (Intervention1CheckBox.isSelected()){
            Intervention1.seton(true);

            Intervention1DayField.setEnabled(true);
            Intervention1CaloriesField.setEnabled(true);
            Intervention1SodiumField.setEnabled(true);
            Intervention1CarbInPercField.setEnabled(true);
            Intervention1ActChangeBox.setEnabled(true);
            Intervention1SodiumCalculatedRadio.setEnabled(true);
            Intervention1SodiumInputRadio.setEnabled(true);

            if (HighlightCheckBox.isSelected()){
                highlightIntervention1();
            }
        }else{
            Intervention1.seton(false);

            Intervention1DayField.setEnabled(false);
            Intervention1CaloriesField.setEnabled(false);
            Intervention1SodiumField.setEnabled(false);
            Intervention1CarbInPercField.setEnabled(false);
            Intervention1ActChangeBox.setEnabled(false);
            Intervention1SodiumCalculatedRadio.setEnabled(false);
            Intervention1SodiumInputRadio.setEnabled(false);
            unhighlightIntervention1();
        }
        if (!goalsim){
            recalc();
        }
    }//GEN-LAST:event_Intervention1CheckBoxActionPerformed

    private void Intervention2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2CheckBoxActionPerformed
        if (Intervention2CheckBox.isSelected()){
            Intervention2.seton(true);

            Intervention2DayField.setEnabled(true);
            Intervention2CaloriesField.setEnabled(true);
            Intervention2SodiumField.setEnabled(true);
            Intervention2CarbInPercField.setEnabled(true);
            Intervention2ActChangeBox.setEnabled(true);
            Intervention2SodiumCalculatedRadio.setEnabled(true);
            Intervention2SodiumInputRadio.setEnabled(true);
            if (HighlightCheckBox.isSelected()){
                highlightIntervention2();
            }
        }else{
            Intervention2.seton(false);
            Intervention2DayField.setEnabled(false);
            Intervention2CaloriesField.setEnabled(false);
            Intervention2SodiumField.setEnabled(false);
            Intervention2CarbInPercField.setEnabled(false);
            Intervention2ActChangeBox.setEnabled(false);
            Intervention2SodiumCalculatedRadio.setEnabled(false);
            Intervention2SodiumInputRadio.setEnabled(false);
            unhighlightIntervention2();
        }
        if (!goalsim){
            recalc();
        }
    }//GEN-LAST:event_Intervention2CheckBoxActionPerformed

    private void Intervention2CaloriesFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2CaloriesFieldActionPerformed
        Intervention2CaloriesChange();
    }//GEN-LAST:event_Intervention2CaloriesFieldActionPerformed

    private void Intervention2CarbInPercFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2CarbInPercFieldActionPerformed
        Intervention2CarbInPercChange();
    }//GEN-LAST:event_Intervention2CarbInPercFieldActionPerformed

    private void Intervention1DayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1DayFieldActionPerformed
        Intervention1DayChange();
    }//GEN-LAST:event_Intervention1DayFieldActionPerformed

    private void Intervention2DayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2DayFieldActionPerformed
        Intervention2DayChange();
    }//GEN-LAST:event_Intervention2DayFieldActionPerformed

    private void GoalWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoalWeightFieldActionPerformed
       GoalWeightChange();
    }//GEN-LAST:event_GoalWeightFieldActionPerformed

    private void GoalTimeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoalTimeFieldActionPerformed
        GoalTimeChange();
    }//GEN-LAST:event_GoalTimeFieldActionPerformed

    private void SimulationLengthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimulationLengthFieldActionPerformed
        SimulationLengthChange();
    }//GEN-LAST:event_SimulationLengthFieldActionPerformed

    private void TimeStartSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TimeStartSliderStateChanged
        setgraphparams();
        gpanel.setparams(timestart,timezoom,vertical_zoom);
    }//GEN-LAST:event_TimeStartSliderStateChanged

    private void TimeZoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TimeZoomSliderStateChanged
        setgraphparams();
        gpanel.setparams(timestart,timezoom,vertical_zoom);
    }//GEN-LAST:event_TimeZoomSliderStateChanged

    private void VerticalZoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_VerticalZoomSliderStateChanged
        setgraphparams();
        gpanel.setparams(timestart,timezoom,vertical_zoom);
    }//GEN-LAST:event_VerticalZoomSliderStateChanged

    private void InitialWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialWeightFieldActionPerformed
        System.out.println("ACTION EVENT NAME IS "+evt.getActionCommand());
        InitialWeightFieldChange();
    }//GEN-LAST:event_InitialWeightFieldActionPerformed

    private void Intervention1SodiumInputRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1SodiumInputRadioActionPerformed
        Intervention1SodiumRadioChange();
    }//GEN-LAST:event_Intervention1SodiumInputRadioActionPerformed

    private void RunGoalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunGoalButtonActionPerformed
        goalsim=true;
        inter1actdialog.setVisible(false);
        inter2actdialog.setVisible(false);
        if (GoalActChangeBox.getSelectedItem().toString().contains("Detail")){
            //goalactdialog.update();
            GoalIntervention.setactchangepercent(goalactdialog.getactchangeperc());
            System.out.print("GOAL ACTIVITY WINDOW PARSED");
            //goalactdialog.setVisible(false);
        }
        if (GoalMaintenanceActChangeBox.getSelectedItem().toString().contains("Detailed")){
            //goalmaintactdialog.update();
            GoalMaintenanceIntervention.setactchangepercent(goalmaintactdialog.getactchangeperc());
            //goalmaintactdialog.setVisible(false);
        }
        recalc();

    }//GEN-LAST:event_RunGoalButtonActionPerformed

    private void RunInterventionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunInterventionButtonActionPerformed
        goalsim=false;
        goalactdialog.setVisible(false);
        goalmaintactdialog.setVisible(false);
        if (Intervention1ActChangeBox.getSelectedItem().toString().contains("Detailed")){
            //goalactdialog.update();
            Intervention1.setactchangepercent(inter1actdialog.getactchangeperc());
            //goalactdialog.setVisible(false);
        }
        if (Intervention2ActChangeBox.getSelectedItem().toString().contains("Detailed")){
            //goalmaintactdialog.update();
            Intervention2.setactchangepercent(inter2actdialog.getactchangeperc());
            //goalmaintactdialog.setVisible(false);
        }
        recalc();
    }//GEN-LAST:event_RunInterventionButtonActionPerformed

    private void Intervention2SodiumInputRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2SodiumInputRadioActionPerformed
        Intervention2SodiumRadioChange();
    }//GEN-LAST:event_Intervention2SodiumInputRadioActionPerformed

    private void Intervention1SodiumFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1SodiumFieldActionPerformed
        Intervention1SodiumChange();
    }//GEN-LAST:event_Intervention1SodiumFieldActionPerformed

    private void Intervention2SodiumFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2SodiumFieldActionPerformed
        Intervention2SodiumChange();
    }//GEN-LAST:event_Intervention2SodiumFieldActionPerformed

    private void GoalMaintenanceActChangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoalMaintenanceActChangeBoxActionPerformed
        try{
            
            OldGoalMaintActChange=GoalMaintenanceIntervention.getactchangepercent();
            goalmaintactdialog.setoldactchange(OldGoalMaintActChange);
            change="goalmaintact";


            GoalMaintenanceIntervention.setactchangepercent(Double.parseDouble(
                                            GoalMaintenanceActChangeBox.getSelectedItem().toString().replaceAll("%", "")));
            //goalmaintactchange=true;
            
            GoalMaintenanceIntervention.setdetailed(false);
            recalc();

        }catch (NumberFormatException e){

            goalmaintactdialog.setoldactchange(GoalMaintenanceIntervention.getactchangepercent());

            if (GoalMaintenanceIntervention.isdetailed()) {
                goalmaintactdialog.setoldboxvalue("Detailed...");
            }else{
                goalmaintactdialog.setoldboxvalue(String.valueOf((int)OldGoalMaintActChange)+"%");
            }
            GoalMaintenanceIntervention.setdetailed(true);
            goalmaintactdialog.setVisible(true);
        }





    }//GEN-LAST:event_GoalMaintenanceActChangeBoxActionPerformed

    private void Intervention2ActChangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2ActChangeBoxActionPerformed
       try{
            inter2actdialog.setoldactchange(Intervention2.getactchangepercent());
             Intervention2.setactchangepercent(Double.parseDouble(
                                            Intervention2ActChangeBox.getSelectedItem().toString()));
            Intervention2.setdetailed(false);
            if (!goalsim){
                recalc();
            }
        }catch (NumberFormatException e){


            if (Intervention2.isdetailed()) {
                inter2actdialog.setoldboxvalue("Detailed...");
            }else{
                inter2actdialog.setoldboxvalue(String.valueOf((int)Intervention2.getactchangepercent()));
            }
            Intervention2.setdetailed(true);
            inter2actdialog.setVisible(true);

        }
    }//GEN-LAST:event_Intervention2ActChangeBoxActionPerformed

    private void Intervention1CaloriesFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1CaloriesFieldActionPerformed
        Intervention1CaloriesChange();
    }//GEN-LAST:event_Intervention1CaloriesFieldActionPerformed

    private void Intervention1CarbInPercFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1CarbInPercFieldActionPerformed
        Intervention1CarbInPercChange();
    }//GEN-LAST:event_Intervention1CarbInPercFieldActionPerformed

    private void InitialWeightFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialWeightFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            InitialWeightFieldChange();
        }
    }//GEN-LAST:event_InitialWeightFieldPropertyChange

    private void InitialBfpFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialBfpFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            InitialBfpChange();
        }
    }//GEN-LAST:event_InitialBfpFieldPropertyChange

    private void InitialSodiumFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialSodiumFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            InitialSodiumChange();
        }
    }//GEN-LAST:event_InitialSodiumFieldPropertyChange

    private void InitialRMRFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialRMRFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            InitialRMRChange();
        }
    }//GEN-LAST:event_InitialRMRFieldPropertyChange

    private void AgeFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_AgeFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            AgeChange();
        }
    }//GEN-LAST:event_AgeFieldPropertyChange

    private void HeightFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_HeightFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            HeightChange();
        }
    }//GEN-LAST:event_HeightFieldPropertyChange

    private void GoalWeightFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_GoalWeightFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            GoalWeightChange();
        }
    }//GEN-LAST:event_GoalWeightFieldPropertyChange

    private void GoalTimeFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_GoalTimeFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            GoalTimeChange();
        }
    }//GEN-LAST:event_GoalTimeFieldPropertyChange

    private void Intervention1DayFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention1DayFieldPropertyChange
       if (!evt.getPropertyName().equals("Frame.active")){
           Intervention1DayChange();
       }
    }//GEN-LAST:event_Intervention1DayFieldPropertyChange

    private void Intervention1CaloriesFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention1CaloriesFieldPropertyChange
         if (!evt.getPropertyName().equals("Frame.active")){
             Intervention1CaloriesChange();
         }
    }//GEN-LAST:event_Intervention1CaloriesFieldPropertyChange

    private void Intervention1CarbInPercFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention1CarbInPercFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            Intervention1CarbInPercChange();
        }
    }//GEN-LAST:event_Intervention1CarbInPercFieldPropertyChange

    private void Intervention1SodiumFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention1SodiumFieldPropertyChange

        if (!evt.getPropertyName().equals("Frame.active")){
            Intervention1SodiumChange();
        }
    }//GEN-LAST:event_Intervention1SodiumFieldPropertyChange

    private void Intervention2DayFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention2DayFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            Intervention2DayChange();
        }
    }//GEN-LAST:event_Intervention2DayFieldPropertyChange

    private void Intervention2CaloriesFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention2CaloriesFieldPropertyChange
       if (!evt.getPropertyName().equals("Frame.active")){
           Intervention2CaloriesChange();
       }
    }//GEN-LAST:event_Intervention2CaloriesFieldPropertyChange

    private void Intervention2CarbInPercFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention2CarbInPercFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            Intervention2CarbInPercChange();
        }
    }//GEN-LAST:event_Intervention2CarbInPercFieldPropertyChange

    private void Intervention2SodiumFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Intervention2SodiumFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            Intervention2SodiumChange();
        }
    }//GEN-LAST:event_Intervention2SodiumFieldPropertyChange

    private void SimulationLengthFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SimulationLengthFieldPropertyChange
         if (!evt.getPropertyName().equals("Frame.active")){
             SimulationLengthChange();
         }
    }//GEN-LAST:event_SimulationLengthFieldPropertyChange

    private void EstimatePALButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EstimatePALButtonActionPerformed
        pdialog.setVisible(true);
        //recalc();
        

    }//GEN-LAST:event_EstimatePALButtonActionPerformed

    private void GoalActChangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoalActChangeBoxActionPerformed
         try{
            
            OldGoalActChange=GoalIntervention.getactchangepercent();
            goalactdialog.setoldactchange(OldGoalActChange);
            change="goalact";


            GoalIntervention.setactchangepercent(Double.parseDouble(
                                            GoalActChangeBox.getSelectedItem().toString().replaceAll("%", "")));
            //goalmaintactchange=true;
            
            GoalIntervention.setdetailed(false);
            recalc();

        }catch (NumberFormatException e){

            goalactdialog.setoldactchange(GoalIntervention.getactchangepercent());
            
            if (GoalIntervention.isdetailed()) {
                goalactdialog.setoldboxvalue("Detailed...");
            }else{
                goalactdialog.setoldboxvalue(String.valueOf((int)GoalIntervention.getactchangepercent())+"%");
            }
            GoalIntervention.setdetailed(true);
            goalactdialog.setVisible(true);
        }


    }//GEN-LAST:event_GoalActChangeBoxActionPerformed

    private void InitialCarbInPercFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialCarbInPercFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            CarbInPercChange();
        }
    }//GEN-LAST:event_InitialCarbInPercFieldPropertyChange

    private void UncertaintyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UncertaintyCheckBoxActionPerformed
        if (UncertaintyCheckBox.isSelected()){
            gpanel.setspread(true);
            
            //ShowLegendCheckBox.setEnabled(true);
            //if (ShowLegendCheckBox.isSelected()) gpanel.setlegend(true);

        }else if (!UncertaintyCheckBox.isSelected()){
            gpanel.setspread(false);
            //gpanel.setlegend(false);
            //ShowLegendCheckBox.setEnabled(false);
            
        }
    }//GEN-LAST:event_UncertaintyCheckBoxActionPerformed

    private void GridCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GridCheckBoxActionPerformed
        if (GridCheckBox.isSelected()){
            gpanel.setgrid(true);
        }else if (!GridCheckBox.isSelected()){
            gpanel.setgrid(false);
        }
        
    }//GEN-LAST:event_GridCheckBoxActionPerformed

    private void ShowLegendCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowLegendCheckBoxActionPerformed
        if (ShowLegendCheckBox.isSelected()){
            gpanel.setlegend(true);
        }else if (!ShowLegendCheckBox.isSelected()){
            gpanel.setlegend(false);
        }
        
    }//GEN-LAST:event_ShowLegendCheckBoxActionPerformed

    private void DefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DefaultButtonActionPerformed
       //System.out.println("GOAL DEFAULT PRESSED");
        //this=new MainPanel();
        //repaint();
       //**

       GoalCheckBox.setSelected(true);
       GoalCheckBoxActionPerformed(null);
       initValues();
       recalc();
       TimeStartSlider.setValue(0);
       TimeZoomSlider.setValue(0);
       VerticalZoomSlider.setValue(0);
       Intervention1CheckBox.isSelected();
       Intervention2CheckBox.isSelected();
       Intervention1ActChangeBox.setSelectedItem("0");
       Intervention2ActChangeBox.setSelectedItem("0");
       Intervention1SodiumCalculatedRadio.setSelected(true);
       Intervention2SodiumCalculatedRadio.setSelected(true);
       GoalActChangeBox.setSelectedItem("0");
       GoalMaintenanceActChangeBox.setSelectedItem("0");
       //**/
    }//GEN-LAST:event_DefaultButtonActionPerformed

    private void GraphAttachedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GraphAttachedCheckBoxActionPerformed
        if (!GraphAttachedCheckBox.isSelected()){
            graphattached=false;
            //GraphAttachedCheckBox.setText("Attach Graph");
            remove(jPanel2);
            remove(gpanel);

            gframe.getContentPane().add(gpanel);
            gframe.pack();
            gframe.setVisible(true);

            this.repaint();
            ((JFrame) SwingUtilities.getRoot(this)).pack();
        }else if(GraphAttachedCheckBox.isSelected()){
            graphattached=true;
            //GraphAttachedCheckBox.setText("Detach Graph");

            gframe.setVisible(false);


            initGraph();
            this.repaint();
            //gframe.repaint();
        }
    }//GEN-LAST:event_GraphAttachedCheckBoxActionPerformed

    private void AdvancedControlsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdvancedControlsCheckBoxActionPerformed
        if (!AdvancedControlsCheckBox.isSelected()){
           simpleversion=true;
           //FewerControlsCheckBox.setText("Fewer controls");
           //ControlsButton
           simplify();
          


       }else if(AdvancedControlsCheckBox.isSelected()){
            simpleversion=false;
           //FewerControlsCheckBox.setText("Fewer controls");
           //ControlsButton
           advanced();
           
           //((JFrame) SwingUtilities.getRoot(this)).pack();


       }
    }//GEN-LAST:event_AdvancedControlsCheckBoxActionPerformed

    private void HighlightCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HighlightCheckBoxActionPerformed
        if (!HighlightCheckBox.isSelected()){

            SimulationLengthField.setBackground(Color.white);

            InitialWeightField.setBackground(Color.white);
            AgeField.setBackground(Color.white);
            GenderBox.setBorder(null);
            HeightField.setBackground(Color.white);
            InitialPALField.setBackground(Color.white);

            GoalWeightField.setBackground(Color.white);
            GoalTimeField.setBackground(Color.white);
            GoalActChangeBox.setBorder(null);
            GoalMaintenanceActChangeBox.setBorder(null);



            MaintCalsField.setBackground(Color.white);
            InitialSodiumField.setBackground(Color.white);
            InitialCarbInPercField.setBackground(Color.white);
            
            InitialBfpField.setBackground(Color.white);
            
            

            InitialRMRField.setBackground(Color.white);
            FinalWeightField.setBackground(Color.white);
            FinalBfpField.setBackground(Color.white);
            FinalBMIField.setBackground(Color.white);

            GoalCalsField.setBackground(Color.white);
            GoalMaintCalsField.setBackground(Color.white);

            unhighlightIntervention1();
            unhighlightIntervention2();

        }else if(HighlightCheckBox.isSelected()){

            //Highlight major inputs in green
            SimulationLengthField.setBackground(highlightgreen);
            InitialWeightField.setBackground(highlightgreen);
            AgeField.setBackground(highlightgreen);
            HeightField.setBackground(highlightgreen);
            InitialPALField.setBackground(highlightgreen);
            GenderBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
            

            GoalWeightField.setBackground(highlightgreen);
            GoalTimeField.setBackground(highlightgreen);
            GoalActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));
            GoalMaintenanceActChangeBox.setBorder(javax.swing.BorderFactory.createLineBorder(highlightgreen, 3));

            

            //Highlight minor inputs in blue
            MaintCalsField.setBackground(highlightblue);
            InitialSodiumField.setBackground(highlightblue);
            InitialCarbInPercField.setBackground(highlightblue);

            

            if (RMRInputRadio.isSelected()){
                InitialRMRField.setBackground(highlightblue);
            }
            
            if (InitialBfpInputRadio.isSelected()) InitialRMRField.setBackground(highlightblue);


            //Highlight major outputs in yellow

            MaintCalsField.setBackground(highlightyellow);
            FinalWeightField.setBackground(highlightyellow);
            FinalBfpField.setBackground(highlightyellow);
            FinalBMIField.setBackground(highlightyellow);

            GoalCalsField.setBackground(highlightyellow);
            GoalMaintCalsField.setBackground(highlightyellow);

            if (Intervention1.ison()) highlightIntervention1();
            if (Intervention2.ison()) highlightIntervention2();
            

        }
    }//GEN-LAST:event_HighlightCheckBoxActionPerformed

    private void GenderBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenderBoxActionPerformed
        if (GenderBox.getSelectedItem().toString().equals("Male")){
            OldMale=Baseline.getMale();
            Baseline.setMale(true);
            GenderLabel.setText("Male");
        }else{
            OldMale=Baseline.getMale();
            Baseline.setMale(false);
            GenderLabel.setText("Female");
        }
        if (OldMale!=Baseline.getMale()){
            change="gender";
            //System.out.println("Changing gender, basecals is"+Baseline.getMaintCals()+", male is "+Baseline.getMale());
            recalc();
        }
    }//GEN-LAST:event_GenderBoxActionPerformed

    private void CaloriesRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CaloriesRadioActionPerformed
        EnergyUnitsChange();
    }//GEN-LAST:event_CaloriesRadioActionPerformed

    private void KilojoulesRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KilojoulesRadioActionPerformed
        EnergyUnitsChange();
    }//GEN-LAST:event_KilojoulesRadioActionPerformed

    private void Intervention1RampCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1RampCheckBoxActionPerformed
        if (Intervention1RampCheckBox.isSelected()){
            Intervention1.setrampon(true);
            Intervention1DayLabel.setText("Complete Change on Day");
        }else if (!Intervention1RampCheckBox.isSelected()){
            Intervention1.setrampon(false);
            Intervention1DayLabel.setText("Start Change on Day");
        }
        if (!goalsim) recalc();
    }//GEN-LAST:event_Intervention1RampCheckBoxActionPerformed

    private void Intervention2RampCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention2RampCheckBoxActionPerformed
        if (Intervention2RampCheckBox.isSelected()){
            Intervention2.setrampon(true);
            Intervention2DayLabel.setText("Complete Change on Day");
        }else if (!Intervention2RampCheckBox.isSelected()){
            Intervention2.setrampon(false);
            Intervention2DayLabel.setText("Start Change on Day");
        }
        if (!goalsim) recalc();
    }//GEN-LAST:event_Intervention2RampCheckBoxActionPerformed

    private void OverviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OverviewButtonActionPerformed
        walkthrough();
    }//GEN-LAST:event_OverviewButtonActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
    }//GEN-LAST:event_formKeyPressed

    private void ChoicePanelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ChoicePanelPropertyChange
        if (evt.getPropertyName().contains("Selected")){
            System.out.println("SELECTION CHANGE");
            if (ChoicePanel.getSelectedIndex()==0){
                goalsim=true;
            }else if(ChoicePanel.getSelectedIndex()==1){
                goalsim=false;
            }
            recalc();
        }
    }//GEN-LAST:event_ChoicePanelPropertyChange

    private void UncertaintyBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UncertaintyBoxActionPerformed
       try{

            spread_percent=Double.parseDouble(
                                            UncertaintyBox.getSelectedItem().toString().replaceAll("%", ""));

            //change="goalact";
            //goalactchange=true;
            recalc();
        }catch (NumberFormatException e){
            System.out.print("Goal act parse problem");
        }
    }//GEN-LAST:event_UncertaintyBoxActionPerformed

    private void GoalCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoalCheckBoxActionPerformed
        if (!GoalCheckBox.isSelected()){

            //If the checkbox is unchecked, then we turn off the goal interventions
            GoalIntervention.seton(false);
            GoalMaintenanceIntervention.seton(false);

            //We then disable the ui to signal to the user that their input is no longer accepted
            GoalWeightField.setEnabled(false);
            GoalTimeField.setEnabled(false);
            GoalCalsField.setEnabled(false);
            GoalCalsChangeField.setEnabled(false);
            GoalMaintCalsField.setEnabled(false);
            GoalMaintCalsChangeField.setEnabled(false);
            GoalActChangeBox.setEnabled(false);
            GoalMaintenanceActChangeBox.setEnabled(false);

            //Turn off the highlights to avoid confusion
            unhighlightgoal();

            //If we're in goal mode, we need to recalculate
            if (goalsim){
                recalc();
            }
        }else if(GoalCheckBox.isSelected()){

            //Turn on the interventions
            GoalIntervention.seton(true);
            GoalMaintenanceIntervention.seton(true);

            //Re-enable the ui
            GoalWeightField.setEnabled(true);
            GoalTimeField.setEnabled(true);
            GoalCalsField.setEnabled(true);
            GoalCalsChangeField.setEnabled(true);
            GoalMaintCalsField.setEnabled(true);
            GoalMaintCalsChangeField.setEnabled(true);
            GoalActChangeBox.setEnabled(true);
            GoalMaintenanceActChangeBox.setEnabled(true);

            //Turn on highlights if necessary
            if (HighlightCheckBox.isSelected()) highlightgoal();

            //Recalculate if needed
            if (goalsim){
                recalc();
            }
        }
    }//GEN-LAST:event_GoalCheckBoxActionPerformed

    private void DefaultColorsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DefaultColorsCheckBoxActionPerformed
        if (DefaultColorsCheckBox.isSelected()){
            gpanel.setdefaultcolors(true);
        }else{
            gpanel.setdefaultcolors(false);
        }
    }//GEN-LAST:event_DefaultColorsCheckBoxActionPerformed

    private void Intervention1ActChangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Intervention1ActChangeBoxActionPerformed
         try{
            inter1actdialog.setoldactchange(Intervention1.getactchangepercent());
             Intervention1.setactchangepercent(Double.parseDouble(
                                            Intervention1ActChangeBox.getSelectedItem().toString()));
            Intervention1.setdetailed(false);
            if (!goalsim){
                recalc();
            }
        }catch (NumberFormatException e){

            
            if (Intervention1.isdetailed()) {
                inter1actdialog.setoldboxvalue("Detailed...");
            }else{
                inter1actdialog.setoldboxvalue(String.valueOf((int)Intervention1.getactchangepercent()));
            }
            Intervention1.setdetailed(true);
            inter1actdialog.setVisible(true);

        }
    }//GEN-LAST:event_Intervention1ActChangeBoxActionPerformed

    private void InitialPALFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialPALFieldActionPerformed
        initPALChange();
    }//GEN-LAST:event_InitialPALFieldActionPerformed

    private void InitialPALFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InitialPALFieldPropertyChange
        if (!evt.getPropertyName().equals("Frame.active")){
            initPALChange();
        }
    }//GEN-LAST:event_InitialPALFieldPropertyChange

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed

        //**
        
            try {

                JFileChooser fc = new JFileChooser();
                int re = fc.showSaveDialog(this);
                PrintStream oldout = System.out;

                if (re == JFileChooser.APPROVE_OPTION) {

                    System.setOut(new PrintStream(fc.getSelectedFile()));

                    Baseline.print();
                    System.out.println();
                    if (goalsim) {
                        if (!GoalIntervention.ison()) {
                            System.out.println("No intervention");
                        } else {
                            GoalIntervention.print();
                            System.out.println();
                            GoalMaintenanceIntervention.print();
                            System.out.println();
                        }
                    } else {
                        if (!Intervention1.ison() && !Intervention2.ison()) {
                            System.out.println("No intervention");
                            System.out.println();
                        } else {
                            if (Intervention1.ison()) {
                                Intervention1.print();
                            }
                            System.out.println();

                            if (Intervention1.ison()) {
                                Intervention2.print();
                            }
                            System.out.println();
                        }
                    }
                    String[] colnames = gpanel.getcolumnnames();
                    for (int i = 0; i < colnames.length; i++) {
                        System.out.print(colnames[i] + "\t");
                    }

                    System.out.println();

                    Object[][] data = gpanel.gettabledata();
                    for (int i = 0; i < SimulationLength; i++) {
                        for (int j = 0; j < data[i].length; j++) {
                            System.out.print(data[i][j]);
                            System.out.print("\t");
                        }
                        System.out.print("\n");
                    }

                    System.out.close();
                    System.setOut(oldout);
                }

            } catch (Exception e) {
          
        }
         //**/

    }//GEN-LAST:event_SaveButtonActionPerformed

   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox AdvancedControlsCheckBox;
    private javax.swing.JFormattedTextField AgeField;
    private javax.swing.JLabel AgeLabel;
    private javax.swing.JLabel BaselineLabel;
    private javax.swing.JPanel BaselinePanel;
    private javax.swing.ButtonGroup BfpRadioGroup;
    private javax.swing.JSeparator BfpSeparator;
    private javax.swing.JRadioButton CaloriesRadio;
    private javax.swing.JRadioButton CentimetersRadio;
    private javax.swing.JTabbedPane ChoicePanel;
    private javax.swing.JPanel ComparePanel;
    private javax.swing.JButton DefaultButton;
    private javax.swing.JCheckBox DefaultColorsCheckBox;
    private javax.swing.ButtonGroup EnergyUnitsRadioGroup;
    private javax.swing.JButton EstimatePALButton;
    private javax.swing.JFormattedTextField FinalBMIField;
    private javax.swing.JLabel FinalBMILabel;
    private javax.swing.JFormattedTextField FinalBfpField;
    private javax.swing.JLabel FinalBfpLabel;
    private javax.swing.JFormattedTextField FinalWeightField;
    private javax.swing.JLabel FinalWeightLabel;
    private javax.swing.JComboBox GenderBox;
    private javax.swing.JLabel GenderLabel;
    private javax.swing.ButtonGroup GenderRadioGroup;
    private javax.swing.JComboBox GoalActChangeBox;
    private javax.swing.JLabel GoalActivityLabel;
    private javax.swing.JLabel GoalCaloriesLabel;
    private javax.swing.JFormattedTextField GoalCalsChangeField;
    private javax.swing.JLabel GoalCalsChangeLabel;
    private javax.swing.JFormattedTextField GoalCalsField;
    private javax.swing.JCheckBox GoalCheckBox;
    private javax.swing.JLabel GoalDaysLabel;
    private javax.swing.JLabel GoalInterventionLabel;
    private javax.swing.JFormattedTextField GoalMaintCalsChangeField;
    private javax.swing.JLabel GoalMaintCalsChangeLabel;
    private javax.swing.JFormattedTextField GoalMaintCalsField;
    private javax.swing.JLabel GoalMaintCalsLabel;
    private javax.swing.JComboBox GoalMaintenanceActChangeBox;
    private javax.swing.JLabel GoalMaintenanceActivityLabel;
    private javax.swing.JLabel GoalMaintenanceInterventionLabel;
    private javax.swing.JPanel GoalPanel;
    private javax.swing.JLabel GoalSpaceLabel;
    private javax.swing.JPanel GoalStatementPanel;
    private javax.swing.JFormattedTextField GoalTimeField;
    private javax.swing.JLabel GoalUnitsLabel;
    private javax.swing.JFormattedTextField GoalWeightField;
    private javax.swing.JLabel GoalWeightLabel;
    private javax.swing.JCheckBox GraphAttachedCheckBox;
    private javax.swing.JCheckBox GridCheckBox;
    private javax.swing.JFormattedTextField HeightField;
    private javax.swing.JLabel HeightLabel;
    private javax.swing.ButtonGroup HeightUnitsGroup;
    private javax.swing.JCheckBox HighlightCheckBox;
    private javax.swing.JRadioButton InchesRadio;
    private javax.swing.JFormattedTextField InitialBMIField;
    private javax.swing.JLabel InitialBMILabel;
    private javax.swing.JRadioButton InitialBfpCalculatedRadio;
    private javax.swing.JFormattedTextField InitialBfpField;
    private javax.swing.JFormattedTextField InitialBfpField2;
    private javax.swing.JRadioButton InitialBfpInputRadio;
    private javax.swing.JLabel InitialBfpLabel;
    private javax.swing.JLabel InitialBfpLabel2;
    private javax.swing.JFormattedTextField InitialCarbInPercField;
    private javax.swing.JLabel InitialCarbInPercLabel;
    private javax.swing.JFormattedTextField InitialPALField;
    private javax.swing.JFormattedTextField InitialRMRField;
    private javax.swing.JLabel InitialRMRLabel;
    private javax.swing.JFormattedTextField InitialSodiumField;
    private javax.swing.JLabel InitialSodiumLabel;
    private javax.swing.JFormattedTextField InitialWeightField;
    private javax.swing.JFormattedTextField InitialWeightField2;
    private javax.swing.JLabel InitialWeightLabel;
    private javax.swing.JLabel InitialWeightLabel2;
    private javax.swing.JComboBox Intervention1ActChangeBox;
    private javax.swing.JLabel Intervention1ActivityLabel;
    private javax.swing.JFormattedTextField Intervention1CaloriesField;
    private javax.swing.JLabel Intervention1CaloriesLabel;
    private javax.swing.JFormattedTextField Intervention1CarbInPercField;
    private javax.swing.JLabel Intervention1CarbInPercLabel;
    private javax.swing.JCheckBox Intervention1CheckBox;
    private javax.swing.JFormattedTextField Intervention1DayField;
    private javax.swing.JLabel Intervention1DayLabel;
    private javax.swing.JLabel Intervention1Label;
    private javax.swing.JCheckBox Intervention1RampCheckBox;
    private javax.swing.JRadioButton Intervention1SodiumCalculatedRadio;
    private javax.swing.JFormattedTextField Intervention1SodiumField;
    private javax.swing.JRadioButton Intervention1SodiumInputRadio;
    private javax.swing.JLabel Intervention1SodiumLabel;
    private javax.swing.ButtonGroup Intervention1SodiumRadioGroup;
    private javax.swing.JSeparator Intervention1SodiumSeparator;
    private javax.swing.JComboBox Intervention2ActChangeBox;
    private javax.swing.JLabel Intervention2ActivityLabel;
    private javax.swing.JFormattedTextField Intervention2CaloriesField;
    private javax.swing.JLabel Intervention2CaloriesLabel;
    private javax.swing.JFormattedTextField Intervention2CarbInPercField;
    private javax.swing.JLabel Intervention2CarbInPercLabel;
    private javax.swing.JCheckBox Intervention2CheckBox;
    private javax.swing.JFormattedTextField Intervention2DayField;
    private javax.swing.JLabel Intervention2DayLabel;
    private javax.swing.JLabel Intervention2Label;
    private javax.swing.JCheckBox Intervention2RampCheckBox;
    private javax.swing.JRadioButton Intervention2SodiumCalculatedRadio;
    private javax.swing.JFormattedTextField Intervention2SodiumField;
    private javax.swing.JRadioButton Intervention2SodiumInputRadio;
    private javax.swing.JLabel Intervention2SodiumLabel;
    private javax.swing.ButtonGroup Intervention2SodiumRadioGroup;
    private javax.swing.JSeparator Intervention2SodiumSeparator;
    private javax.swing.JPanel InterventionPanel;
    private javax.swing.JRadioButton KilogramsRadio;
    private javax.swing.JRadioButton KilojoulesRadio;
    private javax.swing.JFormattedTextField MaintCalsField;
    private javax.swing.JLabel MaintCalsLabel;
    private javax.swing.JPanel OptionsPanel;
    private javax.swing.JButton OverviewButton;
    private javax.swing.JLabel PALLabel;
    private javax.swing.JRadioButton PoundsRadio;
    private javax.swing.JRadioButton RMRCalculatedRadio;
    private javax.swing.JRadioButton RMRInputRadio;
    private javax.swing.ButtonGroup RMRRadioGroup;
    private javax.swing.JSeparator RMRSeparator;
    private javax.swing.JButton RunGoalButton;
    private javax.swing.JButton RunInterventionButton;
    private javax.swing.JButton SaveButton;
    private javax.swing.JCheckBox ShowLegendCheckBox;
    private javax.swing.JFormattedTextField SimulationLengthField;
    private javax.swing.JLabel SimulationLengthLabel;
    private javax.swing.JLabel SpaceLabel;
    private javax.swing.JLabel StatusLabel;
    private javax.swing.JLabel TimeStartLabel;
    private javax.swing.JLabel TimeStartMaxLabel;
    private javax.swing.JLabel TimeStartMinLabel;
    private javax.swing.JSlider TimeStartSlider;
    private javax.swing.JLabel TimeZoomLabel;
    private javax.swing.JLabel TimeZoomMaxLabel;
    private javax.swing.JLabel TimeZoomMinLabel;
    private javax.swing.JSlider TimeZoomSlider;
    private javax.swing.JComboBox UncertaintyBox;
    private javax.swing.JCheckBox UncertaintyCheckBox;
    private javax.swing.JLabel UncertaintyLabel;
    private javax.swing.JLabel VerticalZoomLabel;
    private javax.swing.JLabel VerticalZoomMaxLabel;
    private javax.swing.JLabel VerticalZoomMinLabel;
    private javax.swing.JSlider VerticalZoomSlider;
    private javax.swing.ButtonGroup WeightUnitsRadioGroup;
    private javax.swing.JPanel ZoomPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    // End of variables declaration//GEN-END:variables

    private void EnergyUnitsChange() {
        if (CaloriesRadio.isSelected()){
            
            energyunits=1;
            energystring="Calories";
            
        }else if(KilojoulesRadio.isSelected()){
            energyunits=4.184;
            energystring="kJ";
        }
        calcflag=true;
        
        MaintCalsField.setValue(Baseline.getMaintCals()*energyunits);
        InitialRMRLabel.setText("Initial RMR ("+energystring+"/day)");
        InitialRMRField.setValue(Baseline.getRMR()*energyunits);
        Intervention1CaloriesLabel.setText("New Diet ("+energystring+"/day)");
        Intervention2CaloriesLabel.setText("New Diet ("+energystring+"/day)");
        Intervention1CaloriesField.setValue(Intervention1.getcalories()*energyunits);
        Intervention2CaloriesField.setValue(Intervention2.getcalories()*energyunits);
        GoalCalsField.setValue(GoalIntervention.getcalories()*energyunits);
        GoalMaintCalsField.setValue(GoalMaintenanceIntervention.getcalories()*energyunits);
        GoalCalsChangeField.setValue((GoalIntervention.getcalories()-Baseline.getMaintCals())*energyunits);
        GoalCalsChangeLabel.setText(energystring+ "/day, which is a change of");
        GoalMaintCalsChangeField.setValue((GoalMaintenanceIntervention.getcalories()-Baseline.getMaintCals())*energyunits);
        GoalMaintCalsChangeLabel.setText(energystring+ "/day, which is a change of");
        calcflag=false;
        gpanel.setenergyunits(energyunits);
    }

    private void simplify() {
        InitialBfpField.setVisible(false);
           InitialBfpLabel.setVisible(false);
           InitialBfpCalculatedRadio.setVisible(false);
           InitialBfpInputRadio.setVisible(false);
           BfpSeparator.setVisible(false);

           InitialRMRField.setVisible(false);
           InitialRMRLabel.setVisible(false);
           RMRCalculatedRadio.setVisible(false);
           RMRInputRadio.setVisible(false);
           RMRSeparator.setVisible(false);
           UncertaintyLabel.setVisible(false);
           UncertaintyBox.setVisible(false);

           InitialSodiumField.setVisible(false);
           InitialSodiumLabel.setVisible(false);
           InitialCarbInPercField.setVisible(false);
           InitialCarbInPercLabel.setVisible(false);


           Intervention1SodiumField.setVisible(false);
           Intervention1SodiumLabel.setVisible(false);
           Intervention1SodiumCalculatedRadio.setVisible(false);
           Intervention1SodiumInputRadio.setVisible(false);
           Intervention1SodiumSeparator.setVisible(false);
           Intervention1CarbInPercField.setVisible(false);
           Intervention1CarbInPercLabel.setVisible(false);
           Intervention1RampCheckBox.setVisible(false);



           Intervention2SodiumField.setVisible(false);
           Intervention2SodiumLabel.setVisible(false);
           Intervention2SodiumCalculatedRadio.setVisible(false);
           Intervention2SodiumInputRadio.setVisible(false);
           Intervention2SodiumSeparator.setVisible(false);
           Intervention2CarbInPercField.setVisible(false);
           Intervention2CarbInPercLabel.setVisible(false);
           Intervention2RampCheckBox.setVisible(false);

           RMRCalculatedRadio.setSelected(true);
           InitialRMRField.setBackground(Color.white);
           InitialBfpCalculatedRadio.setSelected(true);
           InitialBfpField.setBackground(Color.white);
           Intervention1SodiumField.setBackground(Color.white);
           Intervention2SodiumField.setBackground(Color.white);
           Intervention1SodiumCalculatedRadio.setSelected(true);
           Intervention2SodiumCalculatedRadio.setSelected(true);
           Intervention1RampCheckBox.setSelected(false);
           Intervention2RampCheckBox.setSelected(false);
           Intervention1DayLabel.setText("Start Change on Day");
           Intervention2DayLabel.setText("Start Change on Day");

           Baseline.setcalculatedRMR(true);
           Baseline.setcalculatedBfp(true);
           Intervention1.setrampon(false);
           Intervention2.setrampon(false);
           recalc();

           if (Mainframe!=null && Mainframe.getExtendedState()!=JFrame.MAXIMIZED_BOTH) Mainframe.pack();
           this.repaint();
    }

    private void advanced() {

        InitialWeightField.setBounds(InitialWeightField.getX(), InitialWeightField.getY(), InitialWeightField.getWidth()*10,InitialWeightField.getHeight());
        
        
        InitialBfpField.setVisible(true);
        
           InitialBfpLabel.setVisible(true);
           InitialBfpCalculatedRadio.setVisible(true);
           InitialBfpInputRadio.setVisible(true);
           BfpSeparator.setVisible(true);

           InitialRMRField.setVisible(true);
           InitialRMRLabel.setVisible(true);
           
           RMRCalculatedRadio.setVisible(true);
           RMRInputRadio.setVisible(true);
           RMRSeparator.setVisible(true);
           //**
           UncertaintyLabel.setVisible(true);
           UncertaintyBox.setVisible(true);
         //**/

           //**
           InitialSodiumField.setVisible(true);
           InitialSodiumLabel.setVisible(true);
           InitialCarbInPercField.setVisible(true);
           InitialCarbInPercLabel.setVisible(true);

           Intervention1SodiumField.setVisible(true);
           Intervention1SodiumLabel.setVisible(true);
           Intervention1SodiumCalculatedRadio.setVisible(true);
           Intervention1SodiumInputRadio.setVisible(true);
           Intervention1SodiumSeparator.setVisible(true);
           Intervention1CarbInPercField.setVisible(true);
           Intervention1CarbInPercLabel.setVisible(true);
           Intervention1RampCheckBox.setVisible(true);


           Intervention2SodiumField.setVisible(true);
           Intervention2SodiumLabel.setVisible(true);
           Intervention2SodiumCalculatedRadio.setVisible(true);
           Intervention2SodiumInputRadio.setVisible(true);
           Intervention2SodiumSeparator.setVisible(true);
           Intervention2CarbInPercField.setVisible(true);
           Intervention2CarbInPercLabel.setVisible(true);
           Intervention2RampCheckBox.setVisible(true);
            //**/


           
           if (Mainframe!=null && Mainframe.getExtendedState()!=JFrame.MAXIMIZED_BOTH) Mainframe.pack();
           //InitialWeightField.setBounds(InitialWeightField.getX(), InitialWeightField.getY(), InitialWeightField.getWidth()*10,InitialWeightField.getHeight());
           this.setBounds(this.getX(), this.getY(), this.getWidth()+150,this.getHeight()+150 );
           this.repaint();
           //((JFrame) SwingUtilities.getRoot(this)).pack();

    }







}
