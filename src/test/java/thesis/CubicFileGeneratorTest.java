package thesis;

import static org.junit.Assert.*;

import org.junit.Test;

import termo.component.Compound;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alphas;
import termo.matter.Substance;
import termo.phase.Phase;

public class CubicFileGeneratorTest {

	@Test
	public void test() {
		Compound heptane = 	new Compound();
		double criticalTemperature = 540.2;
		double criticalPressure = 2.74000E+06;
		heptane.setCriticalPressure(criticalPressure);
		heptane.setCriticalTemperature(criticalTemperature);
		
		
		Substance  substance = new Substance(
				EquationsOfState.vanDerWaals(), 
				Alphas.getVanDerWaalsIndependent(), 
				heptane, 
				Phase.LIQUID);
		
		double expected =3107000.0;
		
		
		double result = substance.calculate_a_cubicParameter();
		
		
		assertEquals(expected,result,1e4);
	}

}
