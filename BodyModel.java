/*
 * The BodyModel class represents a human body, holding the state variables the correspond to the ones used in the model
 * The current state variables are fat mass, lean mass, glycogen mass, extracellular fluid correction (dECW), and adaptive thermogenisis
 *
 * By supplying baseline and current daily parameter conditions the following can be returned: Weight, TEE, BMI, FatFree,
 *
 * The class also contains methods that implement the model: dfdt, dldt, dgdt, dDecwdt, dthermdt.
 * Each of these also require a baseline and daily paramteres argument
 *
 * The inner class BodyChange allows the changes that occur in the state variables to be packaged together
 * The dt method is the simplest
 */

package weightapplet;

import weightapplet.*;

/**
 *
 * @author Dhruva
 */
public class BodyModel {



    double fat;
    double lean;
    double glyc;
    double decw;
    double therm;

    
    public static int[] RK4wt={ 1, 2, 2, 1};
    

    public BodyModel(){


    }

    public BodyModel(double fat, double lean, double glyc, double decw, double therm){
        //Create a BodyModel with the input state variable values

        this.fat=fat;
        this.lean=lean;
        this.glyc=glyc;
        this.decw=decw;
        this.therm=therm;

    }

    public BodyModel(Baseline base){
        //Creates a BodyModel that matches the state of the input Baseline

        this.fat=base.getFatWt();
        this.lean=base.getLeanWt();
        this.glyc=base.getGlycogen();
        this.decw=base.getdECW();
        this.therm=base.gettherm();

    }


    public static BodyModel projectFromBaseline(Baseline base, DailyParams dparams, double simlength) { //Returns the final composition of a given trajectory.
        //Creates a BodyModel projected from the given baseline, using the input DailyParameters for simlength days

        BodyModel next;

        BodyModel loop = new BodyModel(base);

        for (int i = 0; i < (int) simlength; i++) {

            next = BodyModel.RungeKatta(loop, base, dparams);

            if (i == 1) {
                //System.out.println("IN RKlast, first day fat="+next[0]+", first day lean="+next[1]);
            }

            loop = next;

        }
        return loop;
    }

    public static BodyModel projectFromBaseline(Baseline base, Intervention inter, double simlength) { //Returns the final composition of a given trajectory.
        //Calls the previous function, generating the parameter values from the given Intervention

        return projectFromBaseline(base, new DailyParams(inter, base), simlength);


    }

    public static void main(String[] args){
        Baseline base=new Baseline();
        base.setAge(49);
        base.setcalculatedBfp(false);
        base.setBfp(18);
        base.setCarbInPercent(50);
        base.setPAL(1.5);
        base.setWeight_kgs(100);
        base.setMale(false);
        base.setHeight(157);

        BodyModel bstate =new BodyModel(base);

        DailyParams dparams1=new DailyParams(base);
        DailyParams dparams2=new DailyParams(1000.26, 50, base.proportionalSodium(1000.26),base.getActParam() );
        BodyModel nstate=RungeKatta(bstate, base, dparams1, dparams2);



        nstate.print();
        nstate.printdetailed(base, dparams2);
        System.out.println("\n\nSimple test\n\n");
        BodyModel nstate2=RungeKatta(new BodyModel(base), base, dparams2);
        nstate2.print();
        nstate2.printdetailed(base, dparams2);

        System.out.println("\n\nLong test\n\n");

        for (int i=0;i<200;i++){
            BodyModel nextstate=RungeKatta(nstate2,base, dparams2);
            System.out.printf("Fat at step %d is %f\n", i,nextstate.getFat());
            nstate2=nextstate;
        }
    }


    public double getFat(){
        //Returns the fat value

        return fat;
    }

    public double getLean(){
        return lean;
    }

    public double getGlyc(){
        return glyc;
    }

    public double getDecw(){
        return decw;
    }

    public double getTherm(){
        return therm;
    }


    public double getWeight(Baseline base) {

        //To get the exact weight weight, we need a baseline state to compare the glycogen to

        return getFat() + getLean() + base.getgH2O(glyc) + getDecw();
    }

    public double getapproxWeight() {

        //If we ignore the glycogen, we can get the approximate weight from the other variables

        return getFat() + getLean() + getDecw();

    }

    public double getFatFree(Baseline base) {

        //Need baseline state to get the exact weight

        return getWeight(base) - getFat();
    }
   public double getFatPercent(Baseline base) {

       //Need baseline state to get the exact weight
       return getFat()/getWeight(base) *100;
    }

    public double getBMI(Baseline base) {
        
        //Need baseline state to get the exact weight

        return base.getnewBMI(getWeight(base));
    }

    public BodyChange dt(Baseline base, DailyParams dparams){

        //This is a critical function that functions as a Bodystate derivative


        // Each variable (fat, lean, etc) has its own equation which is called in turn


        double df=dfdt(base,dparams);
        double dl=dldt(base,dparams);
        double dg=dgdt(base,dparams);
        double dDecw=dDecwdt(base,dparams);
        double dtherm=dthermdt(base,dparams);

        //The values are then used to create the BodyChange object
        //A Bodychange contains the changes for all the body state variables
        //Note that order matters

        BodyChange dt=new BodyChange(df, dl, dg, dDecw, dtherm);

        return dt;
    }

    public static BodyModel RungeKatta(BodyModel bstate, Baseline base, DailyParams dparams1, DailyParams dparams2){

        //This is a RungeKatta method that calculates the new BodyModel after one day from the given parameter values and baseline
        //Two sets of parameters are needed to capture the possibility of changing parameter values over the course of the day

        DailyParams midparams=dparams2.isramped()? dparams2:DailyParams.avg(dparams1, dparams2);
        
        BodyChange dt1= bstate.dt(base, dparams1);

        BodyModel b2=bstate.addchange(dt1, 0.5);
        BodyChange dt2=b2.dt(base, midparams);

        BodyModel b3=bstate.addchange(dt2, 0.5);
        BodyChange dt3=b3.dt(base, midparams);

        BodyModel b4=bstate.addchange(dt3, 1);
        BodyChange dt4=b4.dt(base, dparams2);

        BodyChange finaldt=bstate.avgdt_weighted(RK4wt, dt1, dt2, dt3, dt4);

        BodyModel finalstate=bstate.addchange(finaldt, 1);

        return finalstate;
    }

    public static BodyModel RungeKatta(BodyModel bstate, Baseline base, DailyParams dparams) {

        //Similar to the above method, but only uses one set of parameter values, which are assumed to be roughly constant

        BodyChange dt1 = bstate.dt(base, dparams);
        BodyModel b2 = bstate.addchange(dt1, 0.5);

        BodyChange dt2 = b2.dt(base, dparams);
        BodyModel b3 = bstate.addchange(dt2, 0.5);

        BodyChange dt3 = b3.dt(base, dparams);
        BodyModel b4 = bstate.addchange(dt3, 1);

        BodyChange dt4 = b4.dt(base, dparams);

        BodyChange finaldt = bstate.avgdt_weighted(RK4wt, dt1, dt2, dt3, dt4);

        BodyModel finalstate = bstate.addchange(finaldt, 1);

        return finalstate;
    }
    public static BodyModel Euler(BodyModel bstate, Baseline base, DailyParams dparams) {

        //The simplest possible integration method


        BodyChange dt1 = bstate.dt(base, dparams);
        BodyModel nextstate = bstate.addchange(dt1, 1);


        return nextstate;
    }

    public double getTEE(Baseline base, DailyParams dparams) {

        //Calculates the energy expenditure based on the baseline and parameter values

        double p = getp();
        double calin = dparams.getCalories();

        double carbflux = carbflux(base, dparams);

        double Expend = getExpend(base, dparams);

        double TEE=(Expend + (calin-carbflux)*((1-p)*Constants.eta_F/Constants.rho_f + p*Constants.eta_L/Constants.rho_l))
                /(1+p*Constants.eta_L/Constants.rho_l+(1-p)*Constants.eta_F/Constants.rho_f);

        return TEE;

    }

    public double getExpend(Baseline base, DailyParams dparams) {

        //Calculates the energy expenditure w/o considering the energy costs of laying down or extracting tissue

        double TEF = Constants.beta_tef * dparams.getCalories();

        double weight = base.getnewWeight(this);
        double Expend = base.getK() + Constants.gamma_L * getLean() + Constants.gamma_F * getFat() + dparams.getActParam() * weight + getTherm() + TEF;

        if (dparams.flag) {
            System.out.println("Printing in Expend");
            System.out.println("TEF=" + TEF);
            System.out.println("Weight=" + weight);
            System.out.println("Expend=" + Expend);
            //dparams.print();
        }
        return Expend;
    }


    public double getp() {
        //Returns p ratio

        double p = Constants.c / (Constants.c + fat);
        return p;
    }


    public double carbflux(Baseline base, DailyParams dparams) {

        //Energy flux in the carbohydrate compartment (cal/day)

        
        double k_carb = base.getCarbsIn() / (Math.pow(base.getGlycogen(), Constants.carb_power));

        double carbflux = dparams.getCarbIntake() - k_carb * Math.pow(glyc, Constants.carb_power);

        return carbflux;
    }


    public double Na_imbal(Baseline base, DailyParams dparams) {
        
        //Returns sodium flux (mg/d)

        double Na_imbal = dparams.getSodium() - base.getSodium() - Constants.Na_ecw * decw - Constants.Na_zero_CIn * (1 - dparams.getCarbIntake() / base.getCarbsIn());
        return Na_imbal;
    }

    public double dfdt(Baseline base, DailyParams dparams) {
        
        //Returns change in fat mass (kg/d)

        double dfdt = (1 - getp()) * (dparams.getCalories() - getTEE(base, dparams) - carbflux(base, dparams)) / Constants.rho_f;
        return dfdt;
    }

    public double dldt(Baseline base, DailyParams dparams) {

        //Returns  change in lean mass (kg/d)

        double dldt = getp() * (dparams.getCalories() - getTEE(base, dparams) - carbflux(base, dparams)) / Constants.rho_l;
        return dldt;
    }

    public double dgdt(Baseline base, DailyParams dparams) {
        
        //Return change in glycogen mass (kg/d)

        double dgdt = carbflux(base, dparams) / Constants.rho_c;
        return dgdt;
    }

    public double dDecwdt(Baseline base, DailyParams dparams) {

        //Returns change in extracellular fluid (kg/d)

        double decw_dt = Na_imbal(base, dparams) / Constants.Na_conc;
        return decw_dt;
    }

    public double dthermdt(Baseline base, DailyParams dparams) {

        //Return changes in adaptive thermogenisis (kcals/day^2)

        double dthermdt = (Constants.beta_therm * dparams.getCalories() - therm) / Constants.tau_therm;
        return dthermdt;
    }

    public BodyModel addchange(BodyChange bchange, double tstep) {

        //Generates a new BodyModel from the current one by adding the BodyChange components
        //to each state variable, extrapolating using the given timestep

        BodyModel nstate = new BodyModel(fat + tstep * bchange.df(),
                lean + tstep * bchange.dl(),
                glyc + tstep * bchange.dg(),
                decw + tstep * bchange.dDecw(),
                therm + tstep * bchange.dtherm());

        return nstate;
    }


       public double cals4balance(Baseline base, double act) { 
           
           //Calories needed to maintain a person at a certain weight

        double weight = getWeight(base);
        double Expend_no_food = base.getK() + Constants.gamma_L * getLean() + Constants.gamma_F * getFat() + act * (weight);

        double p = getp();
        double p_d = (1 + p * Constants.eta_L / Constants.rho_l + (1 - p) * Constants.eta_F / Constants.rho_f);
        double p_n = ((1 - p) * Constants.eta_F / Constants.rho_f + p * Constants.eta_L / Constants.rho_l);


        double maint_nocflux = Expend_no_food /
                (p_d - p_n - Constants.beta);

        return maint_nocflux;
    }


    public static BodyModel[] Bodytraj(Baseline base, DailyParams[] paramtraj) {

        int simlength = paramtraj.length;
        BodyModel[] bodytraj = new BodyModel[simlength];
        bodytraj[0] = new BodyModel(base);

        for (int i = 1; i < simlength; i++) {

            bodytraj[i] = RungeKatta(bodytraj[i - 1], base, paramtraj[i - 1], paramtraj[i]);

        }

        return bodytraj;
    }

    public BodyChange avgdt(BodyChange... bchange) {
        
        //Creates an average BodyChange object from a set of input BodyChanges

        double sumf = 0, suml = 0, sumg = 0, sumdecw = 0, sumtherm = 0;

        for (int i = 0; i < bchange.length; i++) {
            sumf = sumf + bchange[i].df();
            suml = suml + bchange[i].dl();
            sumg = sumg + bchange[i].dg();
            sumdecw = sumdecw + bchange[i].dDecw();
            sumtherm = sumtherm + bchange[i].dtherm();

        }

        double nf = sumf / bchange.length;
        double nl = suml / bchange.length;
        double ng = sumg / bchange.length;
        double ndecw = sumdecw / bchange.length;
        double ntherm = sumtherm / bchange.length;

        BodyChange nchange = new BodyChange(nf, nl, ng, ndecw, ntherm);

        return nchange;
    }

    public BodyChange avgdt_weighted(int[] wt, BodyChange... bchange) {

        //Creates a weighted average of an input set of BodyChange objects
        //Used in the Runge-Katta methods to do a weighted sum of the body changes

        double sumf = 0, suml = 0, sumg = 0, sumdecw = 0, sumtherm = 0;
        int wti = 0, wtsum = 0;

        for (int i = 0; i < bchange.length; i++) {
            try {
                wti = wt[i];
            } catch (Exception e) {

                //If no weight is given, we set it to one
                wti = 1;
            }
            if (wti < 0) {
                //If the  weight is negative, we set it to one
                wti = 1;
            }

            wtsum = wtsum + wti;

            sumf = sumf + wti * bchange[i].df();
            suml = suml + wti * bchange[i].dl();
            sumg = sumg + wti * bchange[i].dg();
            sumdecw = sumdecw + wti * bchange[i].dDecw();
            sumtherm = sumtherm + wti * bchange[i].dtherm();

        }

        double nf = sumf / wtsum;
        double nl = suml / wtsum;
        double ng = sumg / wtsum;
        double ndecw = sumdecw / wtsum;
        double ntherm = sumtherm / wtsum;

        BodyChange nchange = new BodyChange(nf, nl, ng, ndecw, ntherm);

        return nchange;
    }

    public void print(){
        System.out.println("Fat="+fat);
        System.out.println("Lean="+lean);
        System.out.println("Glycogen="+glyc);
        System.out.println("Decw="+decw);
        System.out.println("Therm="+therm);
    }


    public void printdetailed(Baseline base, DailyParams dparams){
        System.out.println("Fat="+fat);
        System.out.println("Lean="+lean);
        System.out.println("Glycogen="+glyc);
        System.out.println("Decw="+decw);
        System.out.println("Therm="+therm);
        System.out.printf("TEE=%f\n",getTEE(base, dparams));
        System.out.printf("Expend=%f\n",getExpend(base, dparams));
        System.out.printf("p=%f\n",getp());
    }

    public class BodyChange {

        //Inner class that functions as a derivative for the BodyModel

        private double df;
        private double dl;
        private double dg;
        private double dDecw;
        private double dtherm;

        public BodyChange(double df, double dl, double dg, double dDecw, double dtherm) {
            this.df = df;
            this.dl = dl;
            this.dg = dg;
            this.dDecw = dDecw;
            this.dtherm = dtherm;

        }

        public double df() {
            return df;
        }

        public double dl() {
            return dl;
        }

        public double dg() {
            return dg;
        }

        public double dDecw() {
            return dDecw;
        }

        public double dtherm() {
            return dtherm;
        }
    }



}
