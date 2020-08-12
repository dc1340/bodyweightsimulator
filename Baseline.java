/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weightapplet;

import weightapplet.*;
import java.io.PrintStream;

/**
 *
 * @author Dhruva
 */
public class Baseline implements Cloneable {

    boolean Male = true;
    double Age = 23;
    static final double maximumage = 250;
    double Height = 180;
    static final double maximumheight = 400;
    double Weight = 70;
    double Bfp = 18;
    double RMR = 1708;
    double PAL = 1.4;
    double CarbInPercent = 50;
    double Sodium = 4000; //in mg
    double delta_E=0;
    double dECW = 0;
    double Glycogen = .5; //kg
    boolean bfpcalc = true;
    static final double maximumbfp = 60;
    boolean rmrcalc = true;

    public Baseline(boolean Male, double Age, double Height, double Weight, double Bfp, double RMR, double PAL) {
        
        if (Height<0) Height=.1;
        if (Weight<0) Weight=.1;
        if (Bfp<0) Bfp=0;
        if (Bfp>100) Bfp=100;
        if (PAL<1) PAL=1;

        this.Male = Male;
        this.Age = Age;
        this.Height = Height;
        this.Weight = Weight;
        bfpcalc = false;
        this.Bfp = Bfp;
        rmrcalc = false;
        this.RMR = RMR;
        this.PAL = PAL;


    }

    public Baseline() {
    }

    public void setMale(boolean g) {
        this.Male = g;
    }

    public boolean getMale() {
        return this.Male;
    }

    public void setAge(double newage) {
        if (newage > 0 && newage <= maximumage) {
            this.Age = newage;
        }

    }

    public double getAge() {
        return this.Age;
    }

    public void setHeight(double height) {
        if (height > 0 && height <= maximumheight) {
            this.Height = height;
        }
    }

    public double getNewAct(Intervention inter) {
        double act = inter.getAct(this);
        return act;
    }

    public double getHeight_cms() {
        return this.Height;
    }

    public void setWeight_kgs(double weight) {
        if (weight > 0) {
            this.Weight = weight;
        }
    }

    public double getWeight_kgs() {
        return this.Weight;
    }

    public void setBfp(double bfp) {
        if (bfp >= 0 && bfp <= 100 && !bfpcalc) {
            this.Bfp = bfp;
        }
    }

    public double getBfp() {
        if (bfpcalc) {
            if (Male) {
                Bfp = .14 * Age + 37.31 * Math.log(getBMI()) - 103.94;
            } else {
                Bfp = .14 * Age + 39.96 * Math.log(getBMI()) - 102.01;
            }

            if (Bfp > maximumbfp) {
                Bfp = maximumbfp;
            }
            if (Bfp < 0) {
                Bfp = 0;
            }
        }
        return this.Bfp;

    }

    //
    public void setCarbInPercent(double carbinp) {
        if (carbinp >= 0 && carbinp <= 100) {
            this.CarbInPercent = carbinp;
        }
    }

    public double getCarbInPercent() {
        return this.CarbInPercent;
    }

    public double getK() {
        
        double K = (1 - Constants.beta) * getMaintCals()-delta_E - Constants.gamma_L * getLeanWt() - Constants.gamma_F * getFatWt() - getActParam() * Weight;
        return K;
    }

    public double getPAL() {
        return this.PAL;
    }

    public void setPAL(double pal) {
        if (pal >= 1 && pal <= 3) {
            this.PAL = pal;
        }
    }

    public void setRMR(double rmr) {
        if (rmr > 0 && !rmrcalc) {
            this.RMR = rmr;
        }
    }

    public double getRMR() {
        if (rmrcalc) {
            if (Male) {
                RMR = 9.99 * Weight + 625 * Height / 100 - 4.92 * Age + 5;
            } else {
                RMR = 9.99 * Weight + 625 * Height / 100 - 4.92 * Age - 161;
            }
        }
        return RMR;
    }

    public double getnewRMR(double newwt, double day) {
        double rmr;
        if (Male) {
            rmr = 9.99 * newwt + 625 * Height / 100 - 4.92 * (Age + day / 365) + 5;
        } else {
            rmr = 9.99 * newwt + 625 * Height / 100 - 4.92 * (Age + day / 365) - 161;
        }
        return rmr;
    }

    public double getMaintCals() {
        return PAL * getRMR();
    }

    public double getActParam() {
        double act = (0.9 * getRMR() * getPAL() - getRMR()) / getWeight_kgs();
        return act;
    }

    public double getActExpend() {
        double actexpend = getTEE() - getRMR();
        return actexpend;
    }

    public double getTEE() {
        return PAL * getRMR();
    }

    public double getFatWt() {
        return Weight * getBfp() / 100;
    }

    public double getLeanWt() {
        return Weight - getFatWt();
    }

    public double getBMI() {
        return Weight / (Math.pow(Height / 100, 2));
    }

    public double getnewBMI(double newweight) {
        return newweight / (Math.pow(Height / 100, 2));
    }

    public double getECW() {
        double ECW;
        if (Male) {
            ECW = 0.025 * Age + 9.57 * Height + 0.191 * Weight - 12.4;
        } else {
            ECW = -4 + 5.98 * Height + 0.167 * Weight;
        }
        return ECW;
    }

    public double getnewECW(double days, double new_weight) {
        double ECW;
        if (Male) {
            ECW = 0.025 * (Age + days / 365) + 9.57 * Height + 0.191 * new_weight - 12.4;
        } else {
            ECW = -4 + 5.98 * Height + 0.167 * new_weight;
        }
        return ECW;
    }

    public void setSodium(double newsodium) {
        if (newsodium >= 0 && newsodium < 50000) {
            Sodium = newsodium;
        }
    }

    public double getSodium() {
        return Sodium;
    }

    public double proportionalSodium(double newcals) {
        double newNA = getSodium() * newcals / getMaintCals();
        return newNA;
    }

    public double getCarbsIn() {
        return CarbInPercent / 100 * getMaintCals();
    }

    public void setcalculatedBfp(boolean Bfpcalc) {
        Bfp = getBfp();
        bfpcalc = Bfpcalc;



    }

    public void setcalculatedRMR(boolean Rmrcalc) {

        RMR = getRMR();
        rmrcalc = Rmrcalc;



    }

    public double getdECW() {
        return dECW;
    }

    public void setdECW(double newdecw) {
        dECW = newdecw;
    }

    public double getGlycogen() {
        return Glycogen;
    }

    public void setGlycogen(double newglyc) {
        if (newglyc >= 0) {
            Glycogen = newglyc;
        }
    }

    public double getgH2O(double newglyc) {
        double dGH2O = (1 + Constants.hg) * (newglyc - Glycogen);
        return dGH2O;
    }

    public double gettherm() {
        double therm = Constants.beta_therm * getTEE();
        return therm;
    }

    public double[] getbc() {
        double[] bc = {Weight * Bfp / 100, Weight * (100 - Bfp) / 100, dECW};
        return bc;
    }

    public void setdeltaE(double ndeltaE){
        delta_E=ndeltaE;
        return;
    }

    public double getdeltaE(){
        return delta_E;
    }

    public double getnewWeight(double fat, double lean, double glyc, double decw) {
        return fat + lean + getgH2O(glyc) + decw;
    }

    public double getnewWeight(BodyModel bstate) {
        return bstate.getFat() + bstate.getLean() + getgH2O(bstate.getGlyc()) + bstate.getDecw();
    }

    public double getstableWeight(double fat, double lean, double calin) {
        double glyce = glyce(calin);
        double decwe = decwe(calin);
        return fat + lean + getgH2O(glyce) + decwe;
    }

    public double decwe(double calin) {

        double decwe_num = (getSodium() / getMaintCals() + Constants.Na_zero_CIn * getCarbInPercent() / (100 * getCarbsIn())) * calin -
                (getSodium() + Constants.Na_zero_CIn);

        return decwe_num / Constants.Na_ecw;
    }

    public double glyce(double calin) {

        double ge = getGlycogen() * Math.sqrt(getCarbInPercent() / 100 * calin / getCarbsIn());

        return ge;

    }

    public BodyModel getBodyState(){
        return new BodyModel(this);
    }


    public double getnewTEE(BodyModel bstate, DailyParams dparams) {

        //Returns the energy expenditure of the input bodystate with the input paramaters

        return bstate.getTEE(this, dparams);

    }

    public Intervention makebaseintervention(double day) {
        //Create an intervention that maintains the baseline conditions

        Intervention baseinter;
        baseinter = new Intervention((int) day, getTEE(), CarbInPercent, PAL, Sodium);
        return baseinter;
    }

    @Override
  protected Object clone() throws CloneNotSupportedException {

    Baseline clone=(Baseline)super.clone();

    return clone;

  }


    public void printdetailed(PrintStream p) {
        p.println("Baseline report:");
        p.println("    Male=" + Male);
        p.println("    Age=" + Age);
        p.println("    Height (cm)=" + Height);
        p.println("    Weight (kg)=" + Weight);
        p.println("    Fat (kg)=" + getFatWt());
        p.println("    Percent Body Fat=" + Bfp);
        p.println("    RMR (kcal/day)=" + RMR);
        p.println("    PAL=" + PAL);
        p.println("    ActParam=" + getActParam());
        p.println("    TEE (kcal/day)=" + getTEE());
        p.println("    Sodium (mg/day)=" + Sodium);
        p.println("    Carb Percent=" + getCarbInPercent());
        p.println("    Glycogen (kg)=" + Glycogen);
        p.println("    therm (kcal/day)= " + gettherm());
        p.println("    K (kcal/day)=" + getK());
    }

    public void printdetailed(){
        printdetailed(System.out);
    }



    public void print() {
        print(System.out);
    }

        public void print(PrintStream p) {
        p.println("Baseline report:");
        p.println("    Male=" + Male);
        p.println("    Age=" + Age);
        p.println("    Height (cm)=" + Height);
        p.println("    Weight (kg)=" + Weight);
        p.println("    Fat (kg)=" + getFatWt());
        p.println("    Percent Body Fat=" + Bfp);
        p.println("    RMR (kcal/day)=" + RMR);
        p.println("    PAL=" + PAL);
        p.println("    TEE (kcal/day)=" + getTEE());
        p.println("    Sodium (mg/day)=" + Sodium);
        p.println("    Carb Percent=" + getCarbInPercent());
    }

    public void check() {
        System.out.println("Check values vs. calls:");
        System.out.println("    Male:" + (Male == getMale()));
        System.out.println("    Age:" + (Age == getAge()));
        System.out.println("    Height:" + (Height == getHeight_cms()));
        System.out.println("    Weight:" + (Weight == getWeight_kgs()));
        System.out.println("    Bfp:" + (Bfp == getBfp()));
        System.out.println("    RMR:" + (RMR == getRMR()));
        System.out.println("    PAL:" + (PAL == getPAL()));
        //System.out.println("    TEE:"+(PAL*RMR==getTEE()));

    }
}
