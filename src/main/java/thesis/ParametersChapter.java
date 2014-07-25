package thesis;

import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.Alphas;
import termo.eos.mixingRule.MixingRule;
import termo.eos.mixingRule.MixingRules;
import termo.matter.Heterogeneous;
import termo.matter.HeterogeneousMixture;
import termo.matter.Mixture;
import termo.matter.Substance;
import termo.matter.builder.MixtureBuilder;
import termo.phase.Phase;

public class ParametersChapter {

	
	public void generateFiles(){
		Cubic srk = EquationsOfState.redlichKwongSoave();
		
		
		
		
	}
	
	public void sameCubic(){
		Cubic srk = new Cubic();
		srk.setU(1);
        srk.setW(0);
        
        srk.setOmega_a(0.42748023);
        srk.setOmega_b(0.08664035);
	}
	
	public void substanceParameters(){
		Compound compound = new Compound("Cyclohexane");
		compound.setCriticalPressure(4073000);
		compound.setCriticalTemperature(553.5);
		compound.setAcentricFactor(0.211);

		Cubic srk = EquationsOfState.redlichKwongSoave();
		Alpha mathias = Alphas.getMathiasExpression();

		Substance substance = new Substance(srk,mathias, compound,Phase.LIQUID);

		double a = substance.calculate_a_cubicParameter();
		double b = substance.calculate_b_cubicParameter();
	}
	
	public void mixtureParameters(){
		Compound cyclohexane = new Compound("Cyclohexane");
		cyclohexane.setCriticalPressure(4073000);
		cyclohexane.setCriticalTemperature(553.5);
		cyclohexane.setAcentricFactor(0.211);
		
		Compound pentane = new Compound("N-pentane");
		pentane.setCriticalPressure(3370000);
		pentane.setCriticalTemperature(469.7);
		pentane.setAcentricFactor(0.251);
		
		Cubic equationOfState = EquationsOfState.pengRobinson();
		Alpha alpha = Alphas.getMathiasAndCopemanExpression();
		MixingRule mixingRule = MixingRules.vanDerWaals();
		
		InteractionParameter k = new InteractionParameter();
		
		Mixture mixture = new MixtureBuilder()
					.addCompounds(cyclohexane,pentane)
					.setAlpha(alpha)
					.setEquationOfState(equationOfState)
					.setPhase(Phase.VAPOR)
					.setMixingRule(mixingRule)
					.setInteractionParameter(k)
					.build();

		double a = mixture.calculate_a_cubicParameter();
		double b = mixture.calculate_b_cubicParameter();
		
	}
	
	public boolean isMixture(Heterogeneous heterogeneous){
		if ( heterogeneous instanceof HeterogeneousMixture){
			return true;
		}else{
			return false;
		}
	}
}
