/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package weightapplet;

import weightapplet.Baseline;

/**
 *
 * @author Dhruva
 */
public class Intervention {

    public static Intervention forgoal(Baseline base, double goalwt, double goaltime, double actchangepercent, double mincals, double eps) throws Exception {
         //System.out.println("calsforgoal starting");
                /**
                    System.out.println("STARTING interventionforgoal");
                    base.print();
                    System.out.println("    mincals is "+mincals);

                    System.out.println("    goalfat is "+goalwt);

                    System.out.println("    time is "+goaltime);

                    System.out.println("    act is "+actchangepercent);
                    System.out.println("    eps is "+eps);
                 **/



                double holdcals = 0, testwt, testdir, holderror;
                BodyModel testbc;



                //System.out.println("goalwt="+goalwt);
                double currentwt=base.getWeight_kgs();
                //System.out.println("currentwt="+currentwt);
                //double dir=Math.signum(goalwt-currentwt); //Check if we're gaining or losing weight


                //We create the Intervention
                Intervention goalinter=new Intervention();

                //We then set it's title and to start immediately
                goalinter.setTitle("Goal Intervention");
                goalinter.setday(1);

                //We set the calories to their minimum
                goalinter.setcalories(mincals);

                //Enter in the activity change level
                goalinter.setactchangepercent(actchangepercent);

                //We use the baseline values for carbs and sodium)
                goalinter.setcarbinpercent(base.getCarbInPercent());
                goalinter.setproportionalsodium(base);

                if (base.getWeight_kgs()==goalwt && actchangepercent==0 ){

                    //If the person wants to maintain their weight, and isn't changing their activity, then their calories stay the same
                    goalinter.setcalories(base.getMaintCals());
                    goalinter.setproportionalsodium(base);
                }else{
                    BodyModel starvtest=BodyModel.projectFromBaseline(base,goalinter, goaltime);  //Check starvation result
                    System.out.println("Starvation test complete: "+starvtest.getFat()+","+starvtest.getLean());

                    double starvwt=starvtest.getWeight(base);
                    if (starvwt<0){
                        starvwt=0;
                    }
                    /**System.out.println("Starvwt is"+starvwt);
                    System.out.println("    currentfat"+currentfat);
                    System.out.println("    currentlean"+currentlean);
                    System.out.println("    time"+time);
                    System.out.println("    mincals"+mincals);
                    System.out.println("    finp"+finp);
                    System.out.println("    act"+act);**/
                    //System.out.println("starvwt="+starvwt);
                    double error=Math.abs(starvwt-goalwt);
                    if (error<eps || goalwt<=starvwt) {
                        System.out.println("PROBLEM in calsforgoal");
                        System.out.println("    error is "+error);
                        System.out.println("    starvwt is"+starvwt);
                        System.out.println("    starv[0] is"+starvtest.getFat());
                        System.out.println("    starv[1] is"+starvtest.getLean());
                        System.out.println("    starv[2] is"+starvtest.getDecw());
                        System.out.println("    goalwt is"+goalwt);
                        System.out.println("    mincals is "+mincals);
                        System.out.println("    goalwt is "+goalwt);
                        System.out.println("    goaltime is "+goaltime);
                        //System.out.println("    finp is "+finp);
                        System.out.println("    eps is "+eps);
                        base.print();
                        goalinter.print();
                        goalinter.setcalories(0);                    //Return garbage value if goal weight is too similar to starvation
                        throw new Exception();
                    }

                    double checkcals=mincals;
                    //double calstep=10+Math.abs((goalwt*base.getBfp()/100-Math.abs(starvtest.getFat())))*fendense
                      //      +(goalwt*(100-base.getBfp())/100-Math.abs(starvtest.getLean())*lendense)
                       //     /goaltime;
                    double calstep=200;
                    System.out.println("Entering loop");
                    //System.out.println("error="+error);
                    //System.out.println("eps="+eps);
                    //System.out.println("checkcals="+checkcals);

                    System.out.println("first calstep in cals for goal ="+calstep);
                    //System.out.println("dir="+dir);
                    int i=0;
                    do {
                            i++;
                            holdcals=checkcals;
                            //holderror=error;
                            checkcals=checkcals+calstep;
                            goalinter.setcalories(checkcals);
                            goalinter.setproportionalsodium(base);
                            testbc=BodyModel.projectFromBaseline(base,goalinter, goaltime);
                            testwt=testbc.getWeight(base);
                            if (testwt<0){
                                System.out.println("NEGATIVE testwt");
                                base.print();
                                goalinter.print();
                                //throw new Exception();
                            }
                            //testdir=Math.signum(goalwt-testwt);
                            error=Math.abs(goalwt-testwt);
                            if (Math.IEEEremainder(i, 100)==0){
                                System.out.println("Loop report "+i);
                                System.out.println("    "+"error="+error);
                                System.out.println("    "+"bc="+testbc.getFat()+","+testbc.getLean());
                                System.out.println("    "+"testwt="+testwt);
                                System.out.println("    "+"calstep="+calstep);
                                //System.out.println("    "+"testdir="+testdir);
                                System.out.println("    "+"holdcals="+holdcals);
                            }
                            if (error>eps && testwt>goalwt){
                                    calstep=calstep/2;          //Reduce step size by half if we overshoot
                                    checkcals=holdcals;         //Return to held value
                                    //error=holderror;
                                    //System.out.println("i="+i+", New calstep="+calstep+", Holdcals="+holdcals+", testwt="+testwt+", error="+error);

                                }


                            }while (error>eps);

                    System.out.printf("Exiting loop after %d iterations, result is %f, error=%f, calstep=%f", i, holdcals, error, calstep);
                    //System.out.println("Goalfat= "+goalfat+", lastfat= "+testbc[0]+", goallean="+goallean+", lastlean="+testbc[1]);
                    //System.out.println(", error="+error);
                }
                return goalinter;

    }

    private int day=100;
    private double calories=2200;
    private double carbinpercent=50;
    private double PAL=1.5;
    private double sodium=4000; //in mg
    private boolean on=true;
    private boolean rampon=false;
    private double actchangepercent=0;
    private boolean isdetailed=false;

    private String title;

    public Intervention(int day, double calories, double carbinpercent, double act, double sodium){
        setday(day);
        setcalories(calories);
        setcarbinpercent(carbinpercent);
        setactchangepercent(act);
        setsodium(sodium);
    }

    public Intervention(){
        
    }

    public void seton(boolean on){
        this.on=on;
    }

    public boolean ison(){
        return on;
    }

    public boolean rampon(){
        return rampon;
    }

    public int getday(){
        return day;
    }

    public double getcalories(){
        return calories;
    }

    public double getcarbinpercent(){
        return carbinpercent;
    }

    public double getPAL(){
        return PAL;
    }

    public double getPAL_Act(Baseline base){
        double act=base.getActParam()*(PAL-1)/(base.getPAL()-1);
        return act;
    }

    public double getAct(Baseline base){
        double act=base.getActParam()*(1+actchangepercent/100);
        //System.out.println("newact="+act+", base act="+base.getAct()+", percentchane="+actchangepercent);
        return act;
    }

    public double getactchangepercent(){
        return this.actchangepercent;
    }

    public double getsodium(){
        return sodium;
    }

    public void setday(int day){
        if (day>0){
        this.day=day;
        }
    }

    public void setrampon(boolean newrampon){
        rampon=newrampon;
    }

    public void setcalories(double calories){
        if (calories>=0){
            this.calories=calories;
        }
    }

    public void setcarbinpercent(double carbinp){
        if (carbinp>=0 && carbinp<=100){
            carbinpercent=carbinp;
        }
    }

    public void setPAL(double PAL){
        if (PAL>=1 && PAL<=3){
            this.PAL=PAL;
        }
    }

    public void setactchangepercent(double newactchange){
        if (newactchange>=-100){
            this.actchangepercent=newactchange;
        }
    }

    public void setsodium(double sodium){
        if (sodium>=0 && sodium<=50000){
        this.sodium=sodium;
        }
    }

    public void setproportionalsodium(Baseline base){
        sodium=base.getSodium()*calories/base.getMaintCals();
    }

    public void setdetailed(boolean detailed){
        isdetailed=detailed;
    }

    public boolean isdetailed(){
        return isdetailed;
    }

    public void print(){
        System.out.println((title==null? "intervention":title)+ " report:");
        System.out.println("    Day="+day);
        System.out.println("    Energy Intake (kcal/day)="+calories);
        System.out.println("    Carb Percent="+carbinpercent);
        //System.out.println("    PAL="+PAL);
        System.out.println("    Percent Activity Change="+actchangepercent);
        System.out.println("    Sodium (mg/day) ="+sodium);
        System.out.println("    on="+on);
        System.out.println("    rampon="+rampon);

}

    public void setTitle(String newtitle){
        title=newtitle;
    }

    public String getTitle(){
        return title;
    }


 

}
