/*
 * This file contains all physiological and metabolic constants used in the model
 * 
 */

package weightapplet;

/**
 *
 * @author Dhruva
 */
public class Constants {

    
    public static final double rho_f= 9440; //Energy density of fat, cals/kg
    public static final double rho_l=1807;  //Erergy density of lean tisue, cals/kg
    public static final double rho_c = 4180; //Energy density of carbohydrates and glycogen, cals/kg
    public static final double hg = 2.7;	//Hyrdation coefficient for glycogen
    public static final double Na_conc = 3220; //Standard body sodium concentrationin, mg/L
    public static final double Na_zero_CIn = 4000;//Sodium concentraion with no carbohydate intake, mg/d
    public static final double Na_ecw = 3000; //Sodium concentration of extracellular fluid
    public static final double eta_F = 180; //Metabolic cost for laying down fat tissue, cals/kg
    public static final double eta_L = 230; //Metabolic cost for laying down lean tissue, cals/kg
    public static final double gamma_F = 3.2; //Metabolic cost of fat tissue, cals/kg
    public static final double gamma_L = 22;//Metabolic cost of lean tissue, cals/kg
    public static final double beta = 0.24; //Stable coefficient value for thermic effect of feeding, dimensionless
    public static final double beta_tef = 0.1; //Persistent coefficient for thermic effect of feeding, dimensionless
    public static final double  beta_therm = beta - beta_tef; //Coefficient value for adaptive thermogenisis, dimensionless
    public static final double tau_therm = 14; //Time scale for adaptive thermogenisis, days
    public static final double c=10.4*rho_l/rho_f; //Useful constant
    public static final double carb_power=2;  //Power used for glycogen equation

}
