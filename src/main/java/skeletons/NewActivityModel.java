package skeletons;

import java.util.ArrayList;
import java.util.HashMap;

import termo.activityModel.ActivityModel;
import termo.binaryParameter.ActivityModelBinaryParameter;
import termo.component.Compound;
import termo.matter.Mixture;
import termo.matter.Substance;

public class NewActivityModel extends ActivityModel {

	@Override
	public double activityCoefficient(Substance arg0, Mixture arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double excessGibbsEnergy(Mixture arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double parcialExcessGibbsRespectTemperature(
			ArrayList<Compound> arg0, HashMap<Compound, Double> arg1,
			ActivityModelBinaryParameter arg2, double arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
