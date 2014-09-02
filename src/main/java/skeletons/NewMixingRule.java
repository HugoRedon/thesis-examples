package skeletons;

import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.eos.mixingRule.MixingRule;
import termo.matter.Mixture;
import termo.matter.Substance;

public class NewMixingRule extends MixingRule {

	@Override
	public double a(Mixture arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double b(Mixture arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getParameter(Compound arg0, Compound arg1,
			InteractionParameter arg2, int arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numberOfParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double oneOverNParcial_aN2RespectN(Substance arg0, Mixture arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setParameter(double arg0, Compound arg1, Compound arg2,
			InteractionParameter arg3, int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public double temperatureParcial_a(Mixture arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
