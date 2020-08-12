/*
 * 
 * The DailyParams class packages the daily parameters required by the model:
 * calories, percent carbohydrate intake, sodium intake, and activity
 *
 * It also keeps track of whether the paramaters are supposed to be ramping or not
 * This has on effect on some of the Runge-Katta integration methods in the BodyModel class
 */

package weightapplet;

import weightapplet.*;

/**
 *
 * @author Dhruva
 */
public class DailyParams {
    private double calories;
    private double carbpercent;
    private double sodium;
    private double actparam;
    public boolean flag=false;
    private boolean ramped=false;


    public DailyParams(){

    }

    public DailyParams(double calories, double carbpercent, double sodium, double actparam){
        //Create a DailyParams with the given values

        if (calories<0) calories=0;
        if (carbpercent<0 ) carbpercent=0;
        if (carbpercent>100) carbpercent=100;
        if (sodium<0) sodium=0;
        if (actparam<0) actparam=0;

        
        this.calories=calories;
        this.carbpercent=carbpercent;
        this.sodium=sodium;
        this.actparam=actparam;
    }

    public DailyParams(Baseline base) {
        //Create DailyParams with the values of the baseline supplied

        calories = base.getMaintCals();
        carbpercent = base.getCarbInPercent();
        sodium = base.getSodium();
        actparam = base.getActParam();
    }

    public DailyParams(Intervention inter, Baseline base) {
        //Create a DailyParams with the values set by the Intervention argument
        //Since the intervention only describes a percentage change in activity, we need the baseline to generate the actual paramter value
        
        calories = inter.getcalories();
        carbpercent = inter.getcarbinpercent();
        sodium = inter.getsodium();
        
        actparam = inter.getAct(base);
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories){
        //Sets the caloric intake, with a floor of zero

        if (calories<0) calories=0;
        this.calories=calories;
    }

    public double getSodium(){
        return sodium;
    }

    public void setSodium(double sodium){
        if (sodium<0) sodium=0;
        this.sodium=sodium;
    }

    public double getCarbPercent(){
        return carbpercent;
    }


    public void setCarbPercent(double carbpercent){
        if (carbpercent>=0 && carbpercent<=100){
            this.carbpercent=carbpercent;
        }
    }

    public double getCarbIntake(){
        return carbpercent/100*calories;
    }

    public double getActParam(){
        return actparam;
    }

    public void setActParam(double actparam) {
        if (actparam<0) actparam=0;
        this.actparam = actparam;
    }

    public boolean isramped(){
        return ramped;
    }

    public void setramped(boolean ramped){
        this.ramped=ramped;
    }

    public DailyParams makeCaloricCopy(double calorie){
        
        double ncals=getCalories()+calorie;
        if (ncals<0) ncals=0;
        DailyParams calcopy=new DailyParams(ncals, getCarbPercent(), getSodium(), getActParam());
        return calcopy;
    }



    public static DailyParams avg(DailyParams dparams1, DailyParams dparams2) {
        double calories = (dparams1.getCalories() + dparams2.getCalories()) / 2;
        double sodium = (dparams1.getSodium() + dparams2.getSodium()) / 2;
        double carbpercent = (dparams1.getCarbPercent() + dparams2.getCarbPercent()) / 2;
        double actparam = (dparams1.getActParam() + dparams2.getActParam()) / 2;
        
        DailyParams ndparams = new DailyParams(calories, carbpercent, sodium, actparam);

        return ndparams;


    }

    public static DailyParams[] makeparamtrajectory(Baseline base, Intervention inter1, Intervention inter2, double simlength) {

        //This method creates a trajectory of parameter values over a course of simlength days, starting with the baseline values,
        // and progressing as dictated by the intervention values

        base.print();
        //System.out.println("check");
        inter1.print();
        inter2.print();

        //We first retrieve the baseline values
        double maintcals = base.getMaintCals();
        double carbinp = base.getCarbInPercent();
        double act = base.getActParam();
        double Na = base.getSodium();

        //We first create the array that will hold the trajectory
        DailyParams[] paramtraj = new DailyParams[(int) simlength];

        //The first set of parameters is generated from the baseline
        paramtraj[0] = new DailyParams(base);


        //We check to see whether the interventions have any effect
        //There's no effect if the intervention is off, or it occurs after the length of the simulation and isn't ramped
        boolean noeffect1 = !inter1.ison() || (inter1.ison() && inter1.getday() > simlength && !inter1.rampon());
        boolean noeffect2 = !inter2.ison() || (inter2.ison() && inter2.getday() > simlength && !inter2.rampon());
        boolean noeffect = noeffect1 && noeffect2;


        //Check to see if the interventions occur on the same day
        boolean sameday = inter1.getday() == inter2.getday();

        boolean oneon = (inter1.ison() && !inter2.ison()) || (!inter1.ison() && inter2.ison());
        boolean bothon = inter1.ison() && inter2.ison();


        double dcal, dcarb, dsodium, dact;

        //Here we generate the trajectories for each of the parameter values;
        if (noeffect) {
            //If the interventions have no effect, we just keep the baseline values for the entire simulation
            for (int i = 1; i < simlength; i++) {
                //if (i==1) System.out.println("NO EFFECT");
                paramtraj[i] = new DailyParams(base);

            }
        } else if (oneon || (bothon && sameday && inter2.rampon())) {

            //If only one intervention is on, we simplify our work and just use that one
            //if they're both on and take effect at the same time, if the second is ramped, we just use that one

            Intervention inter;

            if (oneon) {
                inter = inter1.ison() ? inter1 : inter2;
            } else {
                inter = inter2;
            }


            if (inter.rampon()) {
                //If the intervention is ramped, we move linearly between the baseline
                //and intervention values before the intervention day
                //and for the rest of the time, just use the intervention values

                for (int i = 1; i < inter.getday(); i++) {

                    dcal = maintcals + i / ((double) inter.getday()) * (inter.getcalories() - maintcals);
                    dact = act + i / ((double) inter.getday()) * (inter.getAct(base) - act);
                    dcarb = carbinp + i / ((double) inter.getcarbinpercent()) * (inter.getcarbinpercent() - carbinp);
                    dsodium = Na + i / ((double) inter.getday()) * (inter.getsodium() - Na);

                    paramtraj[i] = new DailyParams(dcal, dcarb, dsodium, dact);
                    paramtraj[i].setramped(true);
                }
                for (int i = inter.getday(); i < simlength; i++) {



                    paramtraj[i] = new DailyParams(inter, base);
                }
            } else {
                //If the intervention isn't ramped we first use the baeline vales, then
                //just hop up to the intervention
                for (int i = 1; i < inter.getday(); i++) {
                    paramtraj[i] = new DailyParams(base);
                }

                for (int i = inter.getday(); i < simlength; i++) {
                    paramtraj[i] = new DailyParams(inter, base);
                }
            }

        } else {
            //If we're dealing with both interventions, then we first sort them by which takes effect first
            //If they're on the same day, we should only be here if the second intervention isn't ramped
            //In that case, if the first intervention is ramped, it'll ramp to the first, then hop to the second

            Intervention firstinter = inter1.getday() < inter2.getday() ? inter1 : inter2;
            Intervention secondinter = inter1.getday() < inter2.getday() ? inter2 : inter1;

            //First we deal with the days before the first intervention
            //We either ramp the values or stay at baseline
            if (firstinter.rampon()) {

                for (int i = 1; i < firstinter.getday(); i++) {
                    dcal = maintcals + i / ((double) firstinter.getday()) * (firstinter.getcalories() - maintcals);
                    dact = act + i / ((double) firstinter.getday()) * (firstinter.getAct(base) - act);
                    dcarb = carbinp + i / ((double) firstinter.getcarbinpercent()) * (firstinter.getcarbinpercent() - carbinp);
                    dsodium = Na + i / ((double) firstinter.getday()) * (firstinter.getsodium() - Na);

                    paramtraj[i] = new DailyParams(dcal, dcarb, dsodium, dact);
                    paramtraj[i].setramped(true);
                }

            } else {
                for (int i = 1; i < firstinter.getday(); i++) {
                    paramtraj[i] = new DailyParams(base);
                }
            }

            //Next we deal with the time between the first and second intervention
            //We either ramp between their values
            //Or just use the first intervention values
            if (secondinter.rampon()) {
                for (int i = firstinter.getday(); i < secondinter.getday(); i++) {
                    dcal = firstinter.getcalories() + (i - firstinter.getday()) /
                            (((double) secondinter.getday() - firstinter.getday())) * (secondinter.getcalories() - firstinter.getcalories());
                    dact = firstinter.getAct(base) + (i - firstinter.getday()) /
                            (((double) secondinter.getday() - firstinter.getday())) * (secondinter.getAct(base) - firstinter.getAct(base));
                    dsodium = firstinter.getsodium() + (i - firstinter.getday()) /
                            (((double) secondinter.getday() - firstinter.getday())) * (secondinter.getsodium() - firstinter.getsodium());
                    dcarb = firstinter.getcarbinpercent() + (i - firstinter.getday()) /
                            (((double) secondinter.getday() - firstinter.getday())) * (secondinter.getcarbinpercent() - firstinter.getcarbinpercent());

                    paramtraj[i] = new DailyParams(dcal, dcarb, dsodium, dact);
                    paramtraj[i].setramped(true);

                }
            } else {
                int endfirst = Math.min(secondinter.getday(), (int) simlength);
                for (int i = firstinter.getday(); i < endfirst; i++) {
                    paramtraj[i] = new DailyParams(firstinter, base);
                }
            }

            //Finally, if the simulation goes long enough,
            //we use the second intervention values until the end of the simulation

            if (simlength > secondinter.getday()) {
                for (int i = secondinter.getday(); i < simlength; i++) {
                    paramtraj[i] = new DailyParams(secondinter, base);
                }
            }


        }

        return paramtraj;
    }

        public void print(){
            System.out.println("Calories="+calories);
            System.out.println("CarbPercent="+carbpercent);
            System.out.println("Sodium="+sodium);
            System.out.println("ActParam="+actparam);
        }

}
