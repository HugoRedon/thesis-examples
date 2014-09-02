package tests;

import org.junit.Test;

import termo.component.Compound;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alphas;
import termo.matter.HeterogeneousSubstance;

import compounds.CompoundReader;

public class EnthalpyTest {

	@Test
	public void test() {
		CompoundReader reader = new CompoundReader();
		Compound water = reader.getCompoundByExactName("Water");
		HeterogeneousSubstance substance = new HeterogeneousSubstance(
				EquationsOfState.pengRobinson(),
				Alphas.getStryjekAndVeraExpression(), water);

		double min_temp = 0.4 * substance.getComponent().getCriticalTemperature();
		double max_temp = substance.getComponent().getCriticalTemperature();
		
		double min_pressure = 0;
		double max_pressure =0;
		
		Integer n = 40;
		double tempPass = (max_temp - min_temp)/n.doubleValue();
		
		
		
		for(Integer i = 0; i <= n; i++){
			double temp = min_temp + i.doubleValue()* tempPass;
			substance.setTemperature(temp);
			substance.dewPressure();
			
			Double pressure = substance.getPressure();
			min_pressure = (min_pressure == 0)?pressure: min_pressure;
			min_pressure = (pressure < min_pressure)? pressure: min_pressure;
			max_pressure = (pressure > max_pressure)? pressure: max_pressure;
			
			Double liquidEnthalpy = substance.getLiquid().calculateEnthalpy();
			Double vaporEnthalpy = substance.getVapor().calculateEnthalpy();
//			double liquidVolume = substance.getLiquid().calculateMolarVolume();
//			double vaporVolume = substance.getVapor().calculateMolarVolume();
			//list.add(new Position(pressure,liquidEnthalpy,  temp));
			
			if(!Double.isFinite(pressure)){
				System.out.println("pressure not finit for temp" + temp);
			}
			if(!Double.isFinite(liquidEnthalpy)){
				System.out.println("liquid enthalpy not finit for temp" + temp);
			}
			if(!Double.isFinite(vaporEnthalpy)){
				System.out.println("vapor enthalpy not finit for temp" + temp);
			}
			
			
			double enthalpyStep  = (vaporEnthalpy- liquidEnthalpy)/n.doubleValue(); 
			for(Integer j = 0; j< n; j++){
				double enthalpy = liquidEnthalpy + j * enthalpyStep;
				System.out.println(pressure+" "+enthalpy+ " " +temp);
			}
			//list.add(new Position(pressure,vaporEnthalpy,  temp));
		}
		
	}

}
