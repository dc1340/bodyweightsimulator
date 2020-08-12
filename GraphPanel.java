package weightapplet;

import weightapplet.MainPanel;
import weightapplet.Intervention;
import weightapplet.BodyModel;
import weightapplet.Baseline;
import weightapplet.DailyParams;
import java.io.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.AffineTransform.*;

/**
 * Panel that graphs the desired weight trajectory
 * @author Dhruva
 */
public class GraphPanel extends javax.swing.JTabbedPane {

    public Color[] colors = {Color.yellow, Color.blue,
        Color.cyan, Color.green,
        Color.magenta, Color.orange,
        Color.pink, Color.red,
        Color.lightGray, Color.white,
        Color.gray, Color.darkGray};
    private boolean defaultcolors = true;
    private Color graphbackground = Color.black;
    private Color graphforeground = Color.white;
    private Color graphgridlines = Color.darkGray;
    private int fontSize = 10;
    private int width = 0;
    private int height = 0;
    // The default values for the graph.
    private double xMin = 0;
    private double xMax;
    private double graphedweightMax;
    private double graphedweightMin;
    private double graphedbfpMax;
    private double graphedbfpMin;
    private double graphedenergyMax;
    private double graphedenergyMin;
    private double startwt_pos = .5;
    private double startbfp_pos = .5;
    private double startenergy_pos = .5;
    private double maximumbfp_fordisplay = 70;
    private double eps = .001;
    private double y0;
    private double x0;
    private double goaltime;
    private BodyModel[] bodytraj;
    private BodyModel[] upperbodytraj;
    private BodyModel[] lowerbodytraj;
    private DailyParams[] paramtraj;
    private double calorie_spread = 200;
    private boolean showspread = false;
    private boolean showgrid = false;
    private int gridspacing = 10;
    private boolean showlegend = true;
    private int legendwidth = 150;
    private int legendheight = 80;
    private int legendgap = 20;
    private boolean longoutput = false;
    private Object[][] tabledata;
    private double simlength;
    public WeightPanel wpanel;
    public BfpPanel bpanel;
    public EnergyPanel epanel;
    public JScrollPane datapanel;
    public JTable datatable;
    private double maxweight_kgs;
    private double minweight_kgs;
    private double maxweight_fordisplay;
    private double maxenergy_fordisplay;
    private double minweight_inrange;
    private double maxweight_inrange;
    private double averageweight_inrange;
    private double averagebfp_inrange;
    private double averageenergy_inrange;
    private double weightunits = 1;
    private String weightunitsstring = "kg";
    private double energyunits = 1;
    private String energyunitsstring = "kJ";
    private Color maincurve = Color.RED;
    private Color uppercurvedefault = Color.CYAN;
    private Color uppercurvealt = Color.BLUE;
    private Color lowercurvedefault = Color.GREEN;
    private Color lowercurvealt = Color.GREEN;
    private String[] columnNames = {"Day",
        "Weight (" + weightunitsstring + ")",
        "Upper weight (" + weightunitsstring + ")",
        " Lower weight (" + weightunitsstring + ")",
        "Body Fat %",
        "BMI",
        "Fat Mass (" + weightunitsstring + ")",
        "Fat Free Mass (" + weightunitsstring + ")",
        "Intake (" + energyunitsstring + "/day)",
        "Expenditure (" + energyunitsstring + "/day)"};
    private int yaxis_space = 50;
    private int xaxis_space = 30;
    private Font rotatedfont;
    private boolean warningdisplayed;
    private MainPanel MainPanel;

    public GraphPanel(Baseline base, Intervention inter1, Intervention inter2, double simlength, int[] graphparams) {
        wpanel = new WeightPanel();
        bpanel = new BfpPanel();

        AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2);
        rotatedfont = new Font("Lucinda Grande", Font.PLAIN, 13).deriveFont(rotation);

        try{
            calculate(base, inter1, inter2, simlength);
        }catch (Exception e){
            e.printStackTrace();
        }
        tabledata = maketabledata_new();




        datatable = new JTable(tabledata, columnNames);
        datatable.setPreferredScrollableViewportSize(new Dimension(300, 300));
        datapanel = new JScrollPane();
        datapanel.setViewportView(datatable);

        this.addTab("Weight", wpanel);
        this.addTab("Body Fat %", bpanel);

        this.addTab("Model Data", datapanel);
        repaint();
    }

    public GraphPanel(MainPanel mpanel, Baseline base, Intervention inter1, Intervention inter2, double simlength, int[] graphparams) {


        this.MainPanel = mpanel;

        //Create the panels for each graph
        wpanel = new WeightPanel();
        bpanel = new BfpPanel();
        epanel = new EnergyPanel();

        AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2);

        //Create the rotated font for the yaxis
        rotatedfont = new Font("Lucinda Grande", Font.PLAIN, 13).deriveFont(rotation);


        try{
            calculate(base, inter1, inter2, simlength);
        }catch (Exception e){
            e.printStackTrace();
        }
        tabledata = maketabledata_new();
        datatable = new JTable(tabledata, columnNames);
        datatable.setPreferredScrollableViewportSize(new Dimension(300, 300));
        datapanel = new JScrollPane();
        datapanel.setViewportView(datatable);

        this.addTab("Weight", wpanel);
        this.addTab("Body Fat %", bpanel);
        this.addTab("Energy Intake & Expenditure", epanel);
        this.addTab("Tabulated Data", datapanel);
        repaint();
    }

    public void setcalspread(double newspread) {
        calorie_spread = newspread;
    }

    public Object[][] maketabledata_new() {
        Object[][] data;
        double weight, upper_weight, lower_weight, fat, upperfat, lowerfat, fatpercent, upperfatp, lowerfatp, fatfree, bmi, upperweight, lowerweight, TEE, calin;

        Baseline base = MainPanel.getBaseline();

        //paramtraj[1].flag=true;
        data = new Object[(int) simlength][columnNames.length];
        for (int i = 0; i < (int) simlength; i++) {

            weight = bodytraj[i].getWeight(base);
            upper_weight = upperbodytraj[i].getWeight(base);
            lower_weight = lowerbodytraj[i].getWeight(base);
            bmi = bodytraj[i].getBMI(base);
            fat = bodytraj[i].getFat();
            upperfat = upperbodytraj[i].getFat();
            lowerfat = lowerbodytraj[i].getFat();
            fatpercent = bodytraj[i].getFatPercent(base);
            upperfatp = upperbodytraj[i].getFatPercent(base);
            lowerfatp = lowerbodytraj[i].getFatPercent(base);
            fatfree = bodytraj[i].getFatFree(base);

            TEE = bodytraj[i].getTEE(base, paramtraj[i]);
            calin = paramtraj[i].getCalories();

            //System.out.println("i="+i+", dF="+(fat-oldfat)+", dL="+(lean-oldlean)+", dECW/dt="+(decw-olddecw));





            data[i][0] = i;

            if (!longoutput) {
                data[i][1] = (Math.round(weight * weightunits * 10)) / ((double) 10);
                data[i][2] = (Math.round(upper_weight * weightunits * 10)) / ((double) 10);
                data[i][3] = (Math.round(lower_weight * weightunits * 10)) / ((double) 10);
                data[i][4] = (Math.round(fatpercent * 10)) / ((double) 10);



                data[i][5] = (Math.round(bmi * 10)) / ((double) 10);
                data[i][6] = (Math.round(fat * weightunits * 10)) / ((double) 10);
                data[i][7] = (Math.round(fatfree * weightunits * 10)) / ((double) 10);
                //data[i][7]=(Math.round((lean)*weightunits*10))/((double)10)+", "+(Math.round((decw)*weightunits*10))/((double)10);
                data[i][8] = (Math.round(calin * energyunits * 10)) / ((double) 10);
                data[i][9] = (Math.round(TEE * energyunits * 10)) / ((double) 10);
            } else {
                data[i][1] = weight * weightunits;
                data[i][2] = upper_weight * weightunits;
                data[i][3] = lower_weight * weightunits;
                data[i][4] = fatpercent;
                data[i][5] = bmi;
                data[i][6] = fat * weightunits;
                data[i][7] = fatfree * weightunits;
                data[i][8] = calin * energyunits;
                data[i][9] = TEE * energyunits;
            }




        }
        return data;
    }

    public Object[][] gettabledata() {
        return tabledata;
    }

    public String[] getcolumnnames() {
        return columnNames;
    }

    public void setdefaultcolors(boolean isdefault) {
        if (isdefault) {
            defaultcolors = true;
            graphbackground = Color.black;
            graphforeground = Color.white;
            graphgridlines = Color.darkGray;
        } else {
            defaultcolors = false;
            graphbackground = Color.white;
            graphforeground = Color.black;
            graphgridlines = Color.lightGray;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //rotated=g.getFont().deriveFont(TOP, new AffineTransform());
        // synchronize the width & height
        width = getWidth();
        height = getHeight();

        //xMax=simlength-1;

        //y_stretchfactor = height/(yMax-yMin); // Stretchfactor towards y
        //x_stretchfactor = width/(xMax-xMin);  // Stretchfactor towards x

        //x0=yaxis_space;
        //y0=height-xaxis_space;

        // Lets draw the Axes and Tics of them now
        //drawAxes(g2);
        //drawTics(g2);

        // now we draw the graph of the equation


        bpanel.repaint();
        wpanel.repaint();
        epanel.repaint();
        datapanel.remove(datatable);


        tabledata = maketabledata_new();


        datatable = new JTable(tabledata, columnNames) {

            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        //datatable.setPreferredScrollableViewportSize(new Dimension(200, 200));
        datatable.setPreferredScrollableViewportSize(this.getPreferredSize());

        datapanel.add(datatable);
        datapanel.setViewportView(datatable);
        System.out.println("END of gpanel paint");

    }

    public void setparams(double timestart, double timezoom, double verticalzoom) {
        if (timestart > 1 || timestart < 0 || timezoom > 1 || timezoom < 0 || verticalzoom > 1 || verticalzoom < 0) {
        } else {
            System.out.println("timestart" + timestart + "timezoom=" + timezoom + "verticalzoom" + verticalzoom);

            //reset_max();

            //First we use the timestart argument to set xMin somewhere between first and next to last day
            //We need to subtract two to get the next to last day since java arrays are indexed from 0
            xMin = (int) (timestart * simlength);
            if (xMin == (int) simlength) {
                xMin = (int) simlength - 2;
            }

            //We now use the timezoom parameter to set xMax somewhere between xMin+1 and the last day, simlength-1
            xMax = (int) (simlength - 1 + timezoom * (xMin + 1 - (simlength - 1)));
            if (xMax == xMin) {
                xMax = xMin + 1;
            }
            if (xMax >= (int) simlength) {
                xMax = simlength - 1;
            }

            //We reset the maximum values for the quantities and maximum graph values so that we know
            //how far we need to be able to zoom out vertically
            reset_max();

            Baseline base = MainPanel.getBaseline();

            //To set the lower bounds for the weight and fat% vertical zoom, we check the difference between the upper and lower
            //trajectories, at both the first and second time points
            double firstwt_upper = upperbodytraj[0].getWeight(base);
            double firstbfp_upper = upperbodytraj[0].getFatPercent(base);

            double firstwt_lower = lowerbodytraj[0].getWeight(base);
            double firstbfp_lower = lowerbodytraj[0].getFatPercent(base);

            double firstwtspan = Math.abs(firstwt_upper - firstwt_lower);
            double firstbfpspan = Math.abs(firstbfp_upper - firstbfp_lower);

            double nextwt_upper = upperbodytraj[(int) xMin + 1].getWeight(base);
            double nextbfp_upper = upperbodytraj[(int) xMin + 1].getFatPercent(base);

            double nextwt_lower = lowerbodytraj[(int) xMin + 1].getWeight(base);
            double nextbfp_lower = lowerbodytraj[(int) xMin + 1].getFatPercent(base);

            double nextwtspan = Math.abs(nextwt_upper - nextwt_lower);
            double nextbfpspan = Math.abs(nextbfp_upper - nextbfp_lower);



            //We set the minimum span by taking the maximum of the first and next wtspan
            double minwtspan = Math.max(firstwtspan, nextwtspan);
            double minbfpspan = Math.max(firstbfpspan, nextbfpspan);



            //Here we set the maximim and minimum weights that will be graphed
            //Note that we work with the internal kg units, until we need to set values
            //that will affect the graph

            //First we use the vertical zoom to set the graphed weight span between the minimum and maximum
            double wtspan = maxweight_fordisplay + verticalzoom * (minwtspan - maxweight_fordisplay);

            //We grab the first weight for positioning purposes
            double firstwt = bodytraj[(int) xMin].getWeight(base);
            //double lastwt=weighttraj[(int)xMax][0]+weighttraj[(int)xMax][1]+weighttraj[(int)xMax][2];

            System.out.println("Average weight in range=" + averageweight_inrange);

            //First we try to just split the span equally about the average weight
            graphedweightMax = (averageweight_inrange + wtspan / 2) * weightunits;
            graphedweightMin = (averageweight_inrange - wtspan / 2) * weightunits;


            //If the firstweight we want to graph is either not in the range, or too close to
            //the edge, we force it to be at either the .9 or .1 position on the yaxis
            if (firstwt * weightunits < graphedweightMin || (firstwt * weightunits > graphedweightMin && (firstwt * weightunits - graphedweightMin) / wtspan < .1)) {

                System.out.println("Graph low");
                startwt_pos = .9;
                graphedweightMax = (firstwt + startwt_pos * wtspan) * weightunits;
                graphedweightMin = (firstwt - (1 - startwt_pos) * wtspan) * weightunits;

            } else if (firstwt * weightunits > graphedweightMax || (firstwt * weightunits < graphedweightMax && (graphedweightMax - firstwt * weightunits) / wtspan < .1)) {

                System.out.println("Graph high");
                startwt_pos = .1;
                graphedweightMax = (firstwt + startwt_pos * wtspan) * weightunits;
                graphedweightMin = (firstwt - (1 - startwt_pos) * wtspan) * weightunits;

            }

            //Finally we check to see if we've either hit the bottom or top of the allowable range
            boolean wthittop = graphedweightMax > (maxweight_fordisplay) * weightunits;
            boolean wthitbottom = graphedweightMin < 0;

            if (wthitbottom) {
                System.out.println("Hit bottom");
                graphedweightMin = 0;
                graphedweightMax = graphedweightMin + wtspan * weightunits;

            } else if (wthittop) {
                System.out.println("Hit top");
                graphedweightMax = (maxweight_fordisplay) * weightunits;
                graphedweightMin = graphedweightMax - wtspan * weightunits;

            }


            System.out.println("In setparams, weightmax=" + graphedweightMax + " ,weightmin=" + graphedweightMin);


            //We follow a similar procedure for the fat%, without needing to keep track of units

            //Set the span between the minimum and maximum
            double bfpspan = maximumbfp_fordisplay + verticalzoom * (minbfpspan - maximumbfp_fordisplay);

            //We grab the first fat% for positioning
            double firstbfp = bodytraj[(int) xMin].getFatPercent(base);


            //First we try to split the span about the average
            graphedbfpMax = (averagebfp_inrange + bfpspan / 2);
            graphedbfpMin = (averagebfp_inrange - bfpspan / 2);


            //If the firt bfp is not in the range, or is too close to the edge, we force it to the .9 or .1 position
            if (firstbfp < graphedbfpMin || (firstbfp > graphedbfpMin && (firstbfp - graphedbfpMin) / bfpspan < .1)) {

                //System.out.println("Graph low");
                startbfp_pos = .9;
                graphedbfpMax = firstbfp + startbfp_pos * bfpspan;
                graphedbfpMin = firstbfp - (1 - startbfp_pos) * bfpspan;

            } else if (firstbfp > graphedbfpMax || (firstbfp < graphedbfpMax && (graphedbfpMax - firstbfp) / bfpspan < .1)) {

                //System.out.println("Graph high");
                startbfp_pos = .1;
                graphedbfpMax = firstbfp + startbfp_pos * bfpspan;
                graphedbfpMin = firstbfp - (1 - startbfp_pos) * bfpspan;

            }

            //Finally we check if we've gone below 0 or above the maximum, and set the graph accordingly;
            boolean bfphittop = graphedbfpMax > maximumbfp_fordisplay;
            boolean bfphitbottom = graphedbfpMin < 0;

            if (bfphitbottom) {
                graphedbfpMin = 0;
                graphedbfpMax = graphedbfpMin + bfpspan;
            } else if (bfphittop) {
                graphedbfpMax = 70;
                graphedbfpMin = graphedbfpMax - bfpspan;
            }



            //Since the energy has both the intake and TEE, we use the difference between them
            //at the first two time points to set the minimum span
            double firstenergyspan = Math.abs(paramtraj[(int) xMin].getCalories() - getTEE((int) xMin));
            double nextenergyspan = Math.abs(paramtraj[(int) xMin + 1].getCalories() - getTEE((int) xMin + 1));
            double minenergyspan = Math.max(firstenergyspan, nextenergyspan);

            //We use the vertical zoom to set the span between the minimum and maximum
            double energyspan = maxenergy_fordisplay + verticalzoom * (minenergyspan - maxenergy_fordisplay);

            //We grab the first energy, an average between the intake and the TEEl, for positioning
            double firstenergy = (paramtraj[(int) xMin].getCalories() + getTEE((int) xMin)) / 2;

            //First we try to split the span about the average energy
            graphedenergyMax = (averageenergy_inrange + energyspan / 2) * energyunits;
            graphedenergyMin = (averageenergy_inrange - energyspan / 2) * energyunits;


            //If the firstenergy is outside the range or too close to the edge, we force it into the
            //.9 or .1 positions

            if (firstenergy * energyunits < graphedenergyMin || (firstenergy * energyunits > graphedenergyMin && (firstenergy * energyunits - graphedenergyMin) / energyspan < .1)) {

                System.out.println("Energy graph low");
                startenergy_pos = .9;
                graphedenergyMax = (firstenergy + startenergy_pos * energyspan) * energyunits;
                graphedenergyMin = (firstenergy - (1 - startenergy_pos) * energyspan) * energyunits;

            } else if (firstenergy > graphedenergyMax || (firstenergy * energyunits < graphedenergyMax && (graphedenergyMax - firstenergy * energyunits) / energyspan < .1)) {

                System.out.println("Energy graph high");
                startenergy_pos = .1;
                graphedenergyMax = (firstenergy + startenergy_pos * energyspan) * energyunits;
                graphedenergyMin = (firstenergy - (1 - startenergy_pos) * energyspan) * energyunits;

            }

            //Finally if the minimum is below 0, or the maximum is above the allowed value
            //we reset the graphed range appropriately


            boolean energyhittop = graphedenergyMax > maxenergy_fordisplay * energyunits;
            boolean energyhitbottom = graphedenergyMin < 0;

            if (energyhitbottom) {
                System.out.println("Energy hit bottom");
                graphedenergyMin = 0;
                graphedenergyMax = (graphedenergyMin + energyspan) * energyunits;
            } else if (energyhittop) {
                System.out.println("Energy hit top");
                graphedenergyMax = maxenergy_fordisplay * energyunits;
                graphedenergyMin = graphedenergyMax - energyspan * energyunits;
            }



            //We now repaint all the panels except the table
            wpanel.repaint();
            bpanel.repaint();
            epanel.repaint();



        }
    }

    public void setspread(boolean spread) {
        showspread = spread;
        wpanel.repaint();
        bpanel.repaint();
        epanel.repaint();
    }

    public void setgrid(boolean grid) {
        showgrid = grid;
        wpanel.repaint();
        bpanel.repaint();
        epanel.repaint();
    }

    public void setlegend(boolean legend) {
        showlegend = legend;
        wpanel.repaint();
        bpanel.repaint();
        epanel.repaint();
    }

    public void remaketable() {
        System.out.println("Remaking table in GraphPanel");

        //We first remove the current table.
        datapanel.remove(datatable);

        //We then remake the data, put it into a table, and
        //add it back
        tabledata = maketabledata_new();


        datatable = new JTable(tabledata, columnNames);
        datatable.setPreferredScrollableViewportSize(new Dimension(200, 200));
        datapanel.add(datatable);
        datapanel.setViewportView(datatable);
    }

    /**
     * Method that draws the X and Y Axis of the Graph if they are
     * in the visible area depending on the graph values.
     *
     * @param g reference to the graphics area we can paint at.
     */
    public BodyModel getfinalbc() {
        return bodytraj[(int) simlength - 1];

    }

    public String getweightstring() {
        return weightunitsstring;
    }

    public BodyModel getgoalbc() throws Exception {
        if (goaltime < simlength) {
            return bodytraj[(int) goaltime];
        } else {
            throw new Exception();
        }

    }

    public void reset_max() {
        Baseline base = MainPanel.getBaseline();
        double firstwt = upperbodytraj[0].getWeight(base);

        maxweight_kgs = firstwt;
        minweight_kgs = firstwt;
        minweight_inrange = firstwt;
        maxweight_inrange = firstwt;
        double maxenergy = Math.max(paramtraj[0].getCalories(), bodytraj[0].getTEE(base, paramtraj[0]));

        //Need to include the day 0 values in the sums if xMin=0, else they can just be at 0
        double wtsum = xMin == 0 ? firstwt : 0;
        double bfpsum = xMin == 0 ? bodytraj[0].getFatPercent(base) : 0;
        double energysum = xMin == 0 ? (paramtraj[0].getCalories() + bodytraj[0].getTEE(base, paramtraj[0])) / 2 : 0;

        double newweight, newweight_upper, newbfp, newcalin, newTEE;
        //System.out.println("Upper"+weighttraj_upper.length+", normal "+weighttraj.length);
        for (int i = 1; i < upperbodytraj.length; i++) {


            //We get the values for the current day
            newweight_upper = upperbodytraj[i].getWeight(base);
            newweight = bodytraj[i].getWeight(base);
            newbfp = bodytraj[i].getFatPercent(base);
            newcalin = paramtraj[i].getCalories();
            newTEE = bodytraj[i].getTEE(base, paramtraj[i]);


            //We change the maximum values if the current value if higher than the maximum
            if (newweight_upper > maxweight_kgs) {
                maxweight_kgs = newweight_upper;
            }
            if (newweight < maxweight_kgs) {
                minweight_kgs = newweight;
            }

            if (newcalin > maxenergy || newTEE > maxenergy) {
                maxenergy = Math.max(newcalin, newTEE);
            }

            if (i >= xMin && i <= xMax) {
                //If we're in the range that will be graphed, we add the values to their sums
                wtsum = wtsum + newweight;
                bfpsum = bfpsum + newbfp;

                //For the energy we average the intake and the TEE
                energysum = energysum + (newcalin + newTEE) / 2;

                if (newweight_upper > maxweight_inrange) {
                    maxweight_inrange = newweight_upper;
                }
                if (newweight < minweight_inrange) {
                    minweight_inrange = newweight;
                }
            }

        }
        double minbmi = MainPanel.getBaseline().getnewBMI(minweight_kgs);


        //We average the values that are in range so that they can be used for graphing
        averageweight_inrange = wtsum / (int) (xMax - xMin + 1);
        averagebfp_inrange = bfpsum / (int) (xMax - xMin + 1);
        averageenergy_inrange = energysum / (int) (xMax - xMin + 1);

        //We add some to the maximum values to give some room to zoom out
        maxweight_fordisplay = maxweight_kgs + 10;
        maxenergy_fordisplay = maxenergy + 1000;

    }

    public double getminweight_kgs() {
        return minweight_kgs;
    }

    public void setweightunits(double newunits) {

        graphedweightMax = graphedweightMax / weightunits;
        graphedweightMin = graphedweightMin / weightunits;
        if (newunits == 1 || newunits == 2.20462) {
            weightunits = newunits;
        }
        if (weightunits == 1) {
            weightunitsstring = "kg";

        } else {
            weightunitsstring = "lbs";
        }
        //System.out.println("New units string is"+unitsstring);
        //columnNames= new String[5];
        String[] newcolumns = {"Day",
            "Weight " + "(" + weightunitsstring + ")",
            "Upper weight " + "(" + weightunitsstring + ")",
            "Lower weight " + "(" + weightunitsstring + ")",
            "Body Fat %",
            "BMI",
            "Fat Mass " + "(" + weightunitsstring + ")",
            "Fat Free Mass " + "(" + weightunitsstring + ")",
            "Intake " + "(" + energyunitsstring + "/day)",
            "Expenditure " + "(" + energyunitsstring + "/day)"};
        columnNames = newcolumns;

        graphedweightMax = graphedweightMax * weightunits;
        graphedweightMin = graphedweightMin * weightunits;

        repaint();
    }

    public void setenergyunits(double newenergyunits) {

        graphedenergyMax = graphedenergyMax / energyunits;
        graphedenergyMin = graphedenergyMin / energyunits;
        if (newenergyunits == 1 || newenergyunits == 4.184) {
            energyunits = newenergyunits;
        }
        if (energyunits == 1) {
            energyunitsstring = "Calories";

        } else {
            energyunitsstring = "kJ";
        }
        //System.out.println("New units string is"+unitsstring);
        //columnNames= new String[5];
        String[] newcolumns = {"Day",
            "Weight (" + weightunitsstring + ")",
            "Upper weight (" + weightunitsstring + ")",
            "Lower weight (" + weightunitsstring + ")",
            "Body Fat %",
            "BMI",
            "Fat Mass (" + weightunitsstring + ")",
            "Fat Free Mass (" + weightunitsstring + ")",
            "Intake (" + energyunitsstring + "/day)",
            "Expenditure (" + energyunitsstring + "/day)"};
        columnNames = newcolumns;

        graphedenergyMax = graphedenergyMax * energyunits;
        graphedenergyMin = graphedenergyMin * energyunits;

        repaint();
    }

    public void calculate(Baseline base, Intervention inter1, Intervention inter2, double simlength) throws Exception {

        DailyParams dparams;
        Baseline upperbase, lowerbase;


        this.simlength = simlength;

        paramtraj = DailyParams.makeparamtrajectory(base, inter1, inter2, simlength);

        upperbase=(Baseline) base.clone();
        upperbase.setdeltaE(calorie_spread);

        lowerbase=(Baseline) base.clone();
        lowerbase.setdeltaE(-calorie_spread);

        bodytraj = new BodyModel[(int) simlength];
        upperbodytraj = new BodyModel[(int) simlength];
        lowerbodytraj = new BodyModel[(int) simlength];

        bodytraj[0] = new BodyModel(base);
        upperbodytraj[0] = new BodyModel(upperbase);
        lowerbodytraj[0] = new BodyModel(lowerbase);

        boolean useRK = true;

        for (int i = 1; i < simlength; i++) {
            //bodytraj[i] = BodyModel.RungeKatta(bodytraj[i - 1], base, paramtraj[i]);


            dparams=paramtraj[i];
          

            //if (i==10) paramtraj[i].print();
            //if (i==10) upperparam2.print();
            //if (i==10) lowerparam2.print();
            if (useRK) {


                //bodytraj[i] = BodyModel.RungeKatta(bodytraj[i - 1], base, paramtraj[i - 1], paramtraj[i]);
                //upperbodytraj[i] = BodyModel.RungeKatta(upperbodytraj[i - 1], base, upperparam1, upperparam2);
                //lowerbodytraj[i] = BodyModel.RungeKatta(lowerbodytraj[i - 1], base, lowerparam1, lowerparam2);

                bodytraj[i] = BodyModel.RungeKatta(bodytraj[i - 1], base, dparams);
                upperbodytraj[i] = BodyModel.RungeKatta(upperbodytraj[i - 1], upperbase, dparams);
                lowerbodytraj[i] = BodyModel.RungeKatta(lowerbodytraj[i - 1], lowerbase, dparams);
            } else {


                bodytraj[i] = BodyModel.Euler(bodytraj[i - 1], base, paramtraj[i]);
                upperbodytraj[i] = BodyModel.Euler(upperbodytraj[i - 1], upperbase, dparams);
                lowerbodytraj[i] = BodyModel.Euler(lowerbodytraj[i - 1], lowerbase, dparams);
            }


        }

        reset_max();

    }

    public double getTEE(int i) {

        return bodytraj[i].getTEE(MainPanel.getBaseline(), paramtraj[i]);

    }

    public class WeightPanel extends JPanel {

        private int wpanel_width;
        private int wpanel_height;
        private int wpanel_graph_width;
        private int wpanel_graph_height;
        private double weight_x_stretch;
        private double weight_y_stretch;
        private String weight_ylabel = "Weight (" + weightunitsstring + ")";

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            //yaxis_space=10+rotatedfont.getSize() + fontSize * String.valueOf((int)simlength).length();
            yaxis_space = 10 + rotatedfont.getSize() + 2 * fontSize;

            x0 = yaxis_space;
            y0 = height - xaxis_space;
            // synchronize the width & height
            wpanel_width = this.getWidth();
            wpanel_height = this.getHeight();

            wpanel_graph_width = wpanel_width - yaxis_space;
            wpanel_graph_height = wpanel_height - xaxis_space;

            weight_y_stretch = wpanel_graph_height / (graphedweightMax - graphedweightMin); // Stretchfactor towards y
            //System.out.println("Weight graph max=" + graphedweightMax + ", min=" + graphedweightMin + "");
            weight_x_stretch = wpanel_graph_width / (xMax - xMin);  // Stretchfactor towards x




            weight_ylabel = "Weight (" + weightunitsstring + ")";


            // create an image to avoid flicks
            Image image = createImage(this.getWidth(), this.getHeight());
            Graphics g2 = image.getGraphics();

            // lets draw the entire area black
            g2.setColor(graphbackground);
            g2.fillRect(0, 0, wpanel_width, wpanel_height);

            //double test1 = wpanel_height-(weighttraj[0][0]+weighttraj[0][1])*units*weight_y_stretch;
            //double test2 =wpanel_height-7*10*weight_y_stretch;
            //System.out.println("With stwt=70kg, curve starts at "+test1);
            //System.out.println("7th tic is at "+test2);
            //System.out.println("Difference is "+(test1-test2));
            // Lets draw the Axes and Tics of them now
            drawAxes(g2, wpanel_height, wpanel_width, weight_ylabel);
            drawXtics(g2, xMin, xMax, wpanel_width, wpanel_height);
            //System.out.println("Drawing Weight Y-tics with:");
            drawYtics(g2, graphedweightMin, graphedweightMax, weight_y_stretch, wpanel_height, wpanel_width);

            // now we draw the graph of the equation
            drawWeight(g2);

            g.drawImage(image, 0, 0, this);




        }

        private void drawWeight(Graphics g) {

            Stroke oldStroke = ((Graphics2D) g).getStroke();
            ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            //Stroke check =new BasicStroke();

            //System.out.println("Weight y_stretch="+weight_y_stretch);
            // now lets get the first value so that we can draw a whole line

            Baseline base = MainPanel.getBaseline();

            double x0 = yaxis_space;
            double y0 = wpanel_height - xaxis_space;

            double x1 = x0;
            double weight0 = bodytraj[(int) xMin].getWeight(base);
            double y1 = y0 - (weight0 - graphedweightMin / weightunits) * weightunits * weight_y_stretch;

            /**System.out.println("Drawing weight, x0:"+x0);
            System.out.println("wpanel_height="+wpanel_height);
            System.out.println("wpanel_width="+wpanel_width);
            System.out.println("x0="+x0);
            System.out.println("y0="+y0);
            System.out.println("x1="+x1);
            System.out.println("y1="+y1);
             * **/
            double upweight0 = upperbodytraj[(int) xMin].getWeight(base);
            double y1up = y0 - (upweight0 * weightunits - graphedweightMin) * weight_y_stretch;
            double lowweight0 = lowerbodytraj[(int) xMin].getWeight(base);
            double y1low = y0 - (lowweight0 * weightunits - graphedweightMin) * weight_y_stretch;


            //double[] finalbc=getfinalbc();
            //double xfin=x0+x*weight_x_stretch



            // lets draw the graph we actually have
            for (int x = (int) xMin + 1; x <= (int) xMax; x++) {


                double weight = bodytraj[x].getWeight(base);
                double upweight = upperbodytraj[x].getWeight(base);

                double lowweight = lowerbodytraj[x].getWeight(base);


                double x2 = x0 + (x - xMin) * weight_x_stretch;
                double y2 = y0 - (weight * weightunits - graphedweightMin) * weight_y_stretch;

                double y2up = y0 - (upweight * weightunits - graphedweightMin) * weight_y_stretch;
                double y2low = y0 - (lowweight * weightunits - graphedweightMin) * weight_y_stretch;

                // lets draw the line regardless if we draw outside because
                // java will care anyway.


                if (y2 < wpanel_height - xaxis_space) {
                    //System.out.println("x="+x+", x1="+x1+", y1="+y1+", x2="+x2+", y2="+y2);
                    ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.setColor(maincurve);

                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    ((Graphics2D) g).setStroke(oldStroke);
                }

                if (showspread) {

                    if (y2up < wpanel_height - xaxis_space) {
                        g.setColor(MainPanel.defaultcolors() ? uppercurvedefault : uppercurvealt);
                        g.drawLine((int) x1, (int) y1up, (int) x2, (int) y2up);
                    }
                    if (y2low < wpanel_height - xaxis_space) {

                        g.setColor(MainPanel.defaultcolors() ? lowercurvedefault : lowercurvealt);
                        g.drawLine((int) x1, (int) y1low, (int) x2, (int) y2low);
                    }

                }

                y1 = y2;
                x1 = x2;

                y1up = y2up;
                y1low = y2low;


            }


            if (showspread && showlegend) {
                drawlegend(g, wpanel_width, wpanel_height);
            }

            //g.setColor(colors[2]);
            //g.drawLine(0,(int)wpanel_height/2,wpanel_width,(int)wpanel_height/2);
            //g.setColor(colors[3]);
            //g.drawLine(0,(int)(yMax*weight_y_stretch/2),wpanel_width,(int)(yMax*weight_y_stretch/2));
        }
    }

    public class BfpPanel extends JPanel {

        public int bpanel_width;
        public int bpanel_height;
        private double bfp_x_stretch;
        private double bfp_y_stretch;
        private int bpanel_graph_width;
        private int bpanel_graph_height;
        String bfp_ylabel = "Body Fat %";

        @Override
        public void paintComponent(Graphics g) {

            yaxis_space = 3 + g.getFont().getSize() + fontSize * String.valueOf((int) simlength).length();

            x0 = yaxis_space;
            y0 = height - xaxis_space;

            bpanel_width = this.getWidth();
            bpanel_height = this.getHeight();

            bpanel_graph_width = bpanel_width - yaxis_space;
            bpanel_graph_height = bpanel_height - xaxis_space;

            bfp_x_stretch = bpanel_graph_width / (xMax - xMin);
            bfp_y_stretch = bpanel_graph_height / (graphedbfpMax - graphedbfpMin);



            // create an image to avoid flicks
            Image image = createImage(bpanel_width, bpanel_height);
            Graphics g2 = image.getGraphics();

            // lets draw the entire area black
            g2.setColor(graphbackground);
            g2.fillRect(0, 0, bpanel_width, bpanel_height);

            // Lets draw the Axes and Tics of them now

            drawAxes(g2, bpanel_height, bpanel_width, bfp_ylabel);

            drawXtics(g2, xMin, xMax, bpanel_width, bpanel_height);
            System.out.println("Drawing Bfp Y-tics with:");
            drawYtics(g2, graphedbfpMin, graphedbfpMax, bfp_y_stretch, bpanel_height, bpanel_width);

            // now we draw the graph of the equation
            drawBfp(g2);

            g.drawImage(image, 0, 0, this);

        }

        private void drawBfp(Graphics g) {

            Baseline base = MainPanel.getBaseline();
            //y_stretchfactor=height/100;
            Stroke oldStroke = ((Graphics2D) g).getStroke();
            double x0 = yaxis_space;
            double y0 = bpanel_height - xaxis_space;
            // now lets get the first value so that we can draw a whole line
            double x1 = x0;
            double y1 = y0 - (bodytraj[(int) xMin].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;

            double y1up = y0 - (upperbodytraj[(int) xMin].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;
            double y1low = y0 - (lowerbodytraj[(int) xMin].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;


            // lets draw the graph we actually have
            for (int x = (int) xMin; x <= xMax; x++) {


                double x2 = x0 + (x - xMin) * bfp_x_stretch;
                double y2 = y0 - (bodytraj[x].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;
                double y2up = y0 - (upperbodytraj[x].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;
                double y2low = y0 - (lowerbodytraj[x].getFatPercent(base) - graphedbfpMin) * bfp_y_stretch;

                // lets draw the line regardless if we draw outside because
                // java will care anyway.

                if (y2 < bpanel_height - xaxis_space) {
                    g.setColor(maincurve);
                    ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    ((Graphics2D) g).setStroke(oldStroke);
                }

                if (showspread) {
                    if (y2up < bpanel_height - xaxis_space) {
                        g.setColor(MainPanel.defaultcolors() ? uppercurvedefault : uppercurvealt);
                        g.drawLine((int) x1, (int) y1up, (int) x2, (int) y2up);
                    }

                    if (y2low < bpanel_height - xaxis_space) {
                        g.setColor(MainPanel.defaultcolors() ? lowercurvedefault : lowercurvealt);
                        g.drawLine((int) x1, (int) y1low, (int) x2, (int) y2low);
                    }
                }

                y1 = y2;
                y1up = y2up;
                y1low = y2low;
                x1 = x2;

            }

            if (showspread && showlegend) {
                drawlegend(g, bpanel_width, bpanel_height);
            }
        }
    }

    public class EnergyPanel extends JPanel {

        private int epanel_width;
        private int epanel_height;
        private int epanel_graph_width;
        private int epanel_graph_height;
        private double energy_x_stretch;
        private double energy_y_stretch;
        private String energy_ylabel = "Intake and Expenditure (" + energyunitsstring + "/day)";
        private Color intakedefault = colors[0];
        private Color intakealt = Color.BLUE;
        private Color expenddefault = colors[7];
        private Color expendalt = colors[7];

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            //yaxis_space=10+rotatedfont.getSize() + fontSize * String.valueOf((int)simlength).length();
            yaxis_space = 10 + rotatedfont.getSize() + 4 * fontSize;

            x0 = yaxis_space;
            y0 = height - xaxis_space;
            // synchronize the width & height
            epanel_width = this.getWidth();
            epanel_height = this.getHeight();

            epanel_graph_width = epanel_width - yaxis_space;
            epanel_graph_height = epanel_height - xaxis_space;

            energy_y_stretch = epanel_graph_height / (graphedenergyMax - graphedenergyMin); // Stretchfactor towards y
            System.out.println("energy graph max=" + graphedenergyMax + ", min=" + graphedenergyMin + "");
            energy_x_stretch = epanel_graph_width / (xMax - xMin);  // Stretchfactor towards x




            energy_ylabel = energyunitsstring + "/day";


            // create an image to avoid flicks
            Image image = createImage(this.getWidth(), this.getHeight());
            Graphics g2 = image.getGraphics();

            // lets draw the entire area black
            g2.setColor(graphbackground);
            g2.fillRect(0, 0, epanel_width, epanel_height);

            //double test1 = epanel_height-(energytraj[0][0]+energytraj[0][1])*units*energy_y_stretch;
            //double test2 =epanel_height-7*10*energy_y_stretch;
            //System.out.println("With stwt=70kg, curve starts at "+test1);
            //System.out.println("7th tic is at "+test2);
            //System.out.println("Difference is "+(test1-test2));
            // Lets draw the Axes and Tics of them now
            drawAxes(g2, epanel_height, epanel_width, energy_ylabel);
            drawXtics(g2, xMin, xMax, epanel_width, epanel_height);
            //System.out.println("Drawing energy Y-tics with:");
            drawYtics(g2, graphedenergyMin, graphedenergyMax, energy_y_stretch, epanel_height, epanel_width);

            // now we draw the graph of the equation
            drawEnergy(g2);

            g.drawImage(image, 0, 0, this);




        }

        private void drawEnergy(Graphics g) {


            //System.out.println("energy y_stretch="+energy_y_stretch);
            // now lets get the first value so that we can draw a whole line



            Baseline base = MainPanel.getBaseline();


            double x0 = yaxis_space;
            double y0 = epanel_height - xaxis_space;

            double x1 = x0;
            double y1i = y0 - (paramtraj[(int) xMin].getCalories() * energyunits - graphedenergyMin) * energy_y_stretch;
            double y1e = y0 - (getTEE((int) xMin) * energyunits - graphedenergyMin) * energy_y_stretch;

            /**System.out.println("Drawing energy, x0:"+x0);
            System.out.println("epanel_height="+epanel_height);
            System.out.println("epanel_width="+epanel_width);
            System.out.println("x0="+x0);
            System.out.println("y0="+y0);
            System.out.println("x1="+x1);
            System.out.println("y1="+y1);
             * **/
            //double y1up=y0-((calintraj_upper[(int)xMin][0]+calintraj_upper[(int)xMin][1]+calintraj_upper[(int)xMin][2])*energyunits-graphedenergyMin)*energy_y_stretch;
            //double y1low=y0-((calintraj_lower[(int)xMin][0]+calintraj_lower[(int)xMin][1]+calintraj_lower[(int)xMin][2])*energyunits-graphedenergyMin)*energy_y_stretch;
            //double[] finalbc=getfinalbc();
            //double xfin=x0+x*energy_x_stretch
            //Change the stroke to make thicker lines, while saving the oldstroke
            Stroke oldStroke = ((Graphics2D) g).getStroke();
            ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // lets draw the graph we actually have
            for (int x = (int) xMin + 1; x <= (int) xMax; x++) {


                double x2 = x0 + (x - xMin) * energy_x_stretch;
                double y2i = y0 - (paramtraj[x].getCalories() * energyunits - graphedenergyMin) * energy_y_stretch;
                double y2e = y0 - (getTEE(x) * energyunits - graphedenergyMin) * energy_y_stretch;

                //double y2up = y0-((calintraj_upper[x][0]+calintraj_upper[x][1]+calintraj_upper[x][2])*energyunits-graphedenergyMin)*energy_y_stretch;
                //double y2low = y0-((calintraj_lower[x][0]+calintraj_lower[x][1]+calintraj_lower[x][2])*energyunits-graphedenergyMin)*energy_y_stretch;

                // lets draw the line regardless if we draw outside because
                // java will care anyway.
                if (y1i < epanel_height - xaxis_space && y2i < epanel_height - xaxis_space) {
                    //System.out.println("x="+x+", x1="+x1+", y1="+y1+", x2="+x2+", y2="+y2);
                    g.setColor(MainPanel.defaultcolors() ? intakedefault : intakealt);
                    g.drawLine((int) x1, (int) y1i, (int) x2, (int) y2i);

                } else if (y1i < epanel_height - xaxis_space && y2i >= epanel_height - xaxis_space) {

                    g.setColor(MainPanel.defaultcolors() ? intakedefault : intakealt);
                    g.drawLine((int) x1, (int) y1i, (int) x2, (int) epanel_height - xaxis_space - 1);

                } else if (y1i >= epanel_height - xaxis_space && y2i < epanel_height - xaxis_space) {

                    g.setColor(MainPanel.defaultcolors() ? intakedefault : intakealt);
                    g.drawLine((int) x1, (int) epanel_height - xaxis_space - 1, (int) x2, (int) y2i);

                } else if (y1i == epanel_height - xaxis_space && y2i == epanel_height - xaxis_space) {

                    g.setColor(MainPanel.defaultcolors() ? intakedefault : intakealt);
                    g.drawLine((int) x1, (int) y1i - 1, (int) x2, (int) y2i - 1);
                }

                if (y2e < epanel_height - xaxis_space && y1e < epanel_height - xaxis_space) {
                    //System.out.println("x="+x+", x1="+x1+", y1="+y1+", x2="+x2+", y2="+y2);
                    g.setColor(MainPanel.defaultcolors() ? expenddefault : expendalt);
                    g.drawLine((int) x1, (int) y1e, (int) x2, (int) y2e);
                } else if (y1e == epanel_height - xaxis_space && y2e == epanel_height - xaxis_space) {
                    g.setColor(MainPanel.defaultcolors() ? expenddefault : expendalt);
                    g.drawLine((int) x1, (int) y1e - 1, (int) x2, (int) y2e - 1);
                }



                y1i = y2i;
                y1e = y2e;

                x1 = x2;

                //y1up=y2up;
                //y1low=y2low;


            }

            //Revert to the old stroke
            ((Graphics2D) g).setStroke(oldStroke);

            if (showlegend) {
                drawenergylegend(g, epanel_width, epanel_height);
            }

            //g.setColor(colors[2]);
            //g.drawLine(0,(int)wpanel_height/2,wpanel_width,(int)wpanel_height/2);
            //g.setColor(colors[3]);
            //g.drawLine(0,(int)(yMax*weight_y_stretch/2),wpanel_width,(int)(yMax*weight_y_stretch/2));
        }

        private void drawenergylegend(Graphics g, int width, int height) {
            g.setColor(graphbackground);
            g.fillRect(width - legendwidth - legendgap, legendgap, legendwidth, legendheight - 30);
            g.setColor(graphforeground);
            g.drawRect(width - legendwidth - legendgap, legendgap, legendwidth, legendheight - 30);
            int toprightx = width - legendwidth - legendgap;

            g.setColor(graphforeground);
            //g.drawString("Legend", toprightx+legendwidth/2-15, legendgap+20);
            g.drawString("Intake", toprightx + 60, legendgap + 20);
            g.drawString("Expenditure", toprightx + 60, legendgap + 40);

            Stroke oldStroke = ((Graphics2D) g).getStroke();
            ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.setColor(MainPanel.defaultcolors() ? intakedefault : intakealt);
            g.drawLine(toprightx + 20, legendgap + 20 - 3, toprightx + 40, legendgap + 20 - 3);

            g.setColor(MainPanel.defaultcolors() ? expenddefault : expendalt);
            g.drawLine(toprightx + 20, legendgap + 40 - 3, toprightx + 40, legendgap + 40 - 3);

            ((Graphics2D) g).setStroke(oldStroke);



            //g.drawRect(width-legendwidth-legendgap, legendgap+legendheight, width-legendgap, legendgap);
            //g.drawRect(width-legendwidth,legendheight, width, 0);
            //g.filRect(width/2,height/2, width, height-300);

        }
    }

    private void drawXtics(Graphics g, double xMin, double xMax, int width, int height) {

        g.setColor(graphforeground);
        g.setFont(new Font("Helvetica", Font.PLAIN, fontSize));

        // lets draw the Tics for the X-Axis
        int xpos = (int) xMin;
        double x_stretchfactor = (width - yaxis_space) / (xMax - xMin);
        int tic_max = String.valueOf((int) xMax).length();

        for (double x = yaxis_space, last_pos = 0; x <= width; x += x_stretchfactor, xpos++) {
            // check if we have enough room for drawing the line & string
            if (x - last_pos > (fontSize * tic_max) + 1 || x == 0) {
                g.setColor(graphforeground);
                g.drawLine((int) x, (int) height - xaxis_space, (int) x, (int) height - 4 - xaxis_space);
                g.drawString(String.valueOf(xpos), (int) x + 1, (int) height - xaxis_space + fontSize);

                if (showgrid && x != yaxis_space) {
                    //g.setColor(new Color(102,102,102));
                    g.setColor(graphgridlines);
                    int j = height - xaxis_space - gridspacing;
                    do {
                        g.drawLine((int) x, j, (int) x, j - gridspacing);
                        j = j - 1 * gridspacing;

                    } while (j > 0);
                }

                last_pos = x;

            }
        }


    }

    private void drawYtics(Graphics g, double yMin, double yMax, double y_stretch, int height, int width) {



        g.setColor(graphforeground);
        g.setFont(new Font("Helvetica", Font.PLAIN, fontSize));


        // lets draw the Tics for the Y-Axis
        int last_ypos = height;


        //System.out.println("    Y tics y_stretchfactor="+y_stretch);

        double yspan = Math.abs(yMax - yMin);
        double ytic_incr;
        if (yspan < 1) {
            ytic_incr = 1;
            //ytic_incr=.1;
        } else if (yspan >= 1 && yspan < 15) {
            ytic_incr = 1;
        } else if (yspan >= 15 && yspan < 30) {
            ytic_incr = 2;
        } else if (yspan >= 30 && yspan < 60) {
            ytic_incr = 5;
        } else if (yspan >= 60 && yspan < 150) {
            ytic_incr = 10;
        } else if (yspan >= 150 && yspan < 300) {
            ytic_incr = 20;
        } else if (yspan >= 300 && yspan < 600) {
            ytic_incr = 50;
        } else if (yspan >= 600 && yspan < 1000) {
            ytic_incr = 100;
        } else if (yspan >= 1000 && yspan < 2000) {
            ytic_incr = 200;
        } else if (yspan >= 2000 && yspan < 5000) {
            ytic_incr = 500;
        } else if (yspan >= 5000 && yspan < 10000) {
            ytic_incr = 1000;
        } else if (yspan >= 10000 && yspan < 20000) {
            ytic_incr = 2000;
        } else if (yspan >= 20000 && yspan < 50000) {
            ytic_incr = 5000;
        } else {
            ytic_incr = 10000;
        }


        //double ytic_min=((int)(yMin*10))/((double)10);
        double ytic_min = yMin - yMin % ytic_incr + ytic_incr;
        double yticgap = y_stretch * (ytic_min - yMin);
        //double ytic_max=((int)(yMax*10))/((double)10);
        double ytic_max = (int) (yMax);
        String maxystring = String.valueOf((int) ytic_max);
        int maxystringlength = maxystring.length();

        //yaxis_space=20+rotatedfont.getSize()+10+g.getFontMetrics().stringWidth(maxystring);

        //System.out.println("y_tic inc=" + ytic_incr + ", maxysting=" + maxystring);

        double yticwidth = g.getFontMetrics().stringWidth(maxystring);


        for (double y = height - xaxis_space - yticgap, i = ytic_min; y > 0; y -= ytic_incr * y_stretch, i += ytic_incr) {
            g.setColor(graphforeground);
            g.drawLine((int) x0, (int) y, (int) x0 + 4, (int) y);

            if (last_ypos - y > 10 * y_stretch + 1) {
                last_ypos = (int) y;

                g.drawString(String.valueOf((int) i), (int) (x0 - yticwidth), (int) y - 1);

                //g.drawString(String.valueOf(i),(int)xaxis_space-g.getFontMetrics().stringWidth(maxystring), (int)y-1);
                //g.drawString(String.valueOf(i), 0, (int)y-1);
                //g.drawString(String.valueOf((int)10*i), (int)x0, (int)y-xaxis_space-1);

            } else {
                g.drawString(String.valueOf((int) i), (int) (x0 - yticwidth), (int) y - 1);
                //g.drawString(String.valueOf(i),(int)xaxis_space-g.getFontMetrics().stringWidth(maxystring), (int)y-1);
                //g.drawString(String.valueOf((int)10*i), (int)x0, (int)y-xaxis_space-1);
            }

            if (showgrid && y != height - xaxis_space) {
                //g.setColor(new Color(102,102,102));
                g.setColor(graphgridlines);
                int j = yaxis_space + gridspacing;
                do {
                    g.drawLine(j, (int) y, j + gridspacing, (int) y);
                    j = j + 1 * gridspacing;

                } while (j < width);
            }



        }


    }

    private void drawAxes(Graphics g, int height, int width, String ylabel) {

        g.setColor(graphforeground);
        //g.drawString("Origin", (int) x0, (int)y0);
        //draw X-axis
        String xlabel = "Time (days)";
        g.drawLine((int) x0, (int) height - xaxis_space, width, (int) height - xaxis_space);
        g.drawString(xlabel, (int) (width - yaxis_space - xlabel.length()) / 2, height - 2);

        //draw Y-axis
        Font defaultFont = g.getFont();

        g.setFont(rotatedfont);
        /**System.out.println("ylabel font is:"+g.getFont().toString());
        System.out.println("Transform of ylabel font is:"+g.getFont().getTransform().toString());
        System.out.println("rotated font is:"+rotatedfont.toString());
        System.out.println("Transform of rotated font is:"+rotatedfont.getTransform().toString());
        System.out.println("default font is:"+defaultFont.toString());
        System.out.println("Transform of default font is:"+defaultFont.getTransform().toString());
         * **/
        g.drawLine((int) x0, (int) height - xaxis_space, (int) x0, 0);
        //g.drawString(ylabel, (int) yaxis_space/2,(height-xaxis_space)/2);
        //g.drawString(ylabel, 15,(height-xaxis_space)/2);
        g.drawString(ylabel, 15, height / 2);
        g.setFont(defaultFont);

    }

    private void drawlegend(Graphics g, int width, int height) {



        g.setColor(graphbackground);
        g.fillRect(width - legendwidth - legendgap, legendgap, legendwidth, legendheight);
        g.setColor(graphforeground);
        g.drawRect(width - legendwidth - legendgap, legendgap, legendwidth, legendheight);
        int toprightx = width - legendwidth - legendgap;

        g.setColor(graphforeground);
        //g.drawString("Legend", toprightx+legendwidth/2-15, legendgap+20);
        g.drawString("Upper Estimate", toprightx + 60, legendgap + 20);
        g.drawString("Best Estimate", toprightx + 60, legendgap + 40);
        g.drawString("Lower Estimate", toprightx + 60, legendgap + 60);

        g.setColor(MainPanel.defaultcolors() ? uppercurvedefault : uppercurvealt);
        g.drawLine(toprightx + 20, legendgap + 20 - 3, toprightx + 40, legendgap + 20 - 3);

        Stroke oldStroke = ((Graphics2D) g).getStroke();
        ((Graphics2D) g).setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(maincurve);
        g.drawLine(toprightx + 20, legendgap + 40 - 3, toprightx + 40, legendgap + 40 - 3);
        ((Graphics2D) g).setStroke(oldStroke);

        g.setColor(MainPanel.defaultcolors() ? lowercurvedefault : lowercurvealt);
        g.drawLine(toprightx + 20, legendgap + 60 - 3, toprightx + 40, legendgap + 60 - 3);

        //g.drawRect(width-legendwidth-legendgap, legendgap+legendheight, width-legendgap, legendgap);
        //g.drawRect(width-legendwidth,legendheight, width, 0);
        //g.filRect(width/2,height/2, width, height-300);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
