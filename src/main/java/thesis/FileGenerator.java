package thesis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import termo.component.Compound;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataList;
import termo.eos.Cubic;
import termo.eos.EquationOfStateFactory;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.AlphaFactory;
import termo.equations.Eqn101VaporPressure;
import termo.matter.HeterogeneousSubstance;

public class FileGenerator {
	
	protected Cubic cubic = new Cubic();
	public void prepareFolder(String folderName){
		File directory = new File(folderName);
		if(!directory.exists()){
			directory.mkdir();
		}		
	}
	
	public HeterogeneousSubstance prepareSubstance(){
		Cubic eos = EquationOfStateFactory.pengRobinsonBase();
		Alpha alpha = AlphaFactory.getPengAndRobinsonExpression();
        Compound compound = new Compound("heptane");
//       // ethanol.setName("heptane");
//        compound.setCriticalTemperature(540.2);
//        compound.setCriticalPressure(2.74000E+06);
//        compound.setAcentricFactor(0.349469);
//        
//        compound.setEnthalpyofFormationofIdealgasat298_15Kand101325Pa(-1.87650E+08);
//        compound.setAbsoluteEntropyofIdealGasat298_15Kand101325Pa(-2.24050E+08);
//        
//        compound.setA_dippr107Cp(1.2015E+05);
//        compound.setB_dippr107Cp(4.0010E+05);
//        compound.setC_dippr107Cp(1.6766E+03);
//        compound.setD_dippr107Cp(2.7400E+05);
//        compound.setE_dippr107Cp(7.5640E+02);
//        
//        
//        Eqn101VaporPressure eqn = new Eqn101VaporPressure();
//        eqn.setA(109.69);
//        eqn.setB(-7941.7);
//        eqn.setC(-13.213);
//        eqn.setD(0.000010176);
//        eqn.setE(2);
//        
//        eqn.setMinTemperature(182.57);
//        eqn.setMaxTemperature(540.2);
//        compound.setEqn101VaporPressure(eqn);
        
        //chempsep
        compound.setCriticalTemperature(647.14);
        compound.setCriticalPressure(2.2064E+07);
        compound.setAcentricFactor(0.344);
        
        
        
        
        compound.setEnthalpyofFormationofIdealgasat298_15Kand101325Pa(-2.41814E+08);
	      compound.setAbsoluteEntropyofIdealGasat298_15Kand101325Pa(188724);
	      //chempsep
	      
	      //dippr
	      compound.setA_dippr107Cp(3.3363E+04);
	      compound.setB_dippr107Cp(2.6790E+04);
	      compound.setC_dippr107Cp(2.6105E+03);
	      compound.setD_dippr107Cp(8.8960E+03);
	      compound.setE_dippr107Cp(1.1690E+03);
	      //dippr
	      
	      Eqn101VaporPressure eqn = new Eqn101VaporPressure();
	      //chempsep
	      eqn.setA(98.515);
	      eqn.setB(-8530.7);
	      eqn.setC(-10.984);
	      eqn.setD(0.0000063663);
	      eqn.setE(2);
	      
	      eqn.setMinTemperature(263.15);
	      eqn.setMaxTemperature(647.29);
	      //chempsep
	      compound.setEqn101VaporPressure(eqn);
        
        
    	List<ExperimentalData> list = new ArrayList();
    	ExperimentalDataList dataList = new ExperimentalDataList();
    	dataList.setName("Con ecuación 101");
    	dataList.setSource("base de datos ChemSep");
    	
    	
		double min = eqn.getMinTemperature();
		double max = eqn.getMaxTemperature();
		
		double n = 60;
		double tempPass = (max- min)/n;
		for (int i = 0; i < n ; i++ ){
			double temperature = min + i * tempPass;
			double pressure =eqn.vaporPressure(temperature);
			
			ExperimentalData data = new ExperimentalData(temperature, pressure);
			list.add(data);
		}
		
		dataList.setList(list);
		compound.getExperimentalLists().add(dataList);
        
        
		
		HeterogeneousSubstance substance = new HeterogeneousSubstance(eos, alpha, compound);
		return substance;
	}
}
