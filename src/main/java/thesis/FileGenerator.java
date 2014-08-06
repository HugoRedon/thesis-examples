package thesis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataBinary;
import termo.data.ExperimentalDataBinaryList;
import termo.data.ExperimentalDataList;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.Alphas;
import termo.eos.mixingRule.MixingRule;
import termo.eos.mixingRule.VDWMixingRule;
import termo.equations.Eqn101VaporPressure;
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;

public class FileGenerator {
	
	protected Cubic cubic = new Cubic();
	public void prepareFolder(String folderName){
		File directory = new File(folderName);
		if(!directory.exists()){
			directory.mkdir();
		}		
	}
	
	protected Compound getMethanol(){
		Compound compound = new Compound("methanol");
		//from chemsep
		compound.setCriticalTemperature(512.64);
		compound.setCriticalPressure(8097000);
		compound.setAcentricFactor(0.565);
		
        compound.setEnthalpyofFormationofIdealgasat298_15Kand101325Pa(-2.0094E+08);
	    compound.setAbsoluteEntropyofIdealGasat298_15Kand101325Pa(239880);
		
		 Eqn101VaporPressure eqn = new Eqn101VaporPressure();
	      //chempsep
	      eqn.setA(123.6);
	      eqn.setB(-8660);
	      eqn.setC(-15.101);
	      eqn.setD(0.000013034);
	      eqn.setE(2);
	      
	      eqn.setMinTemperature(175.47);
	      eqn.setMaxTemperature(512.64);
	      //chempsep
	      compound.setEqn101VaporPressure(eqn);
	      
	      fillExperimentalListWithEqn101(compound);
	      return compound;
		
	}
	
	
	protected Compound getWater(){
		 Compound compound = new Compound("water");

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
	      
	      fillExperimentalListWithEqn101(compound);
	      
	      return compound;
	}
	public void fillExperimentalListWithEqn101(Compound compound){
		List<ExperimentalData> list = new ArrayList();
    	ExperimentalDataList dataList = new ExperimentalDataList();
    	dataList.setName("Con ecuación 101");
    	dataList.setSource("base de datos ChemSep");
    	Eqn101VaporPressure eqn = compound.getEqn101VaporPressure();
    	
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
	}
	
	
	
	
	
	
	
	public HeterogeneousSubstance prepareSubstance(){
		Cubic eos = EquationsOfState.pengRobinson();
		Alpha alpha = Alphas.getPengAndRobinsonExpression();
       
        Compound compound = getWater();
		
		HeterogeneousSubstance substance = new HeterogeneousSubstance(eos, alpha, compound);
		return substance;
	}
	
	
	public HeterogeneousMixture prepareWaterMethanolMixture(ExperimentalDataBinaryList blist) throws IOException{
		
		List<ExperimentalDataBinary> list = blist.getList(); 
		
		Cubic eos = EquationsOfState.pengRobinson();
		Alpha alpha = Alphas.getStryjekAndVeraExpression();
		MixingRule mixingRule = new VDWMixingRule();
		Compound water = blist.getNonReferenceComponent();
		Compound methanol = blist.getReferenceComponent();
		
		
		
		Set<Compound> compounds =new HashSet();
		compounds.add(water);
		compounds.add(methanol);
		
		InteractionParameter k = new InteractionParameter();
		k.setSymmetric(true);
		HeterogeneousMixture mix = new HeterogeneousMixture(eos, alpha, mixingRule, compounds, k);
		mix.getErrorfunction().setReferenceComponent(methanol);
		mix.getErrorfunction().setNonReferenceComponent(water);
		mix.getErrorfunction().setExperimental(list);
		
		return mix;
	}
	
	public ExperimentalDataBinaryList getBinaryExperimentalListFromFileTxy(String filePath) throws IOException{
		Path path = Paths.get(filePath);
		List<String> lines =Files.readAllLines(path);
		
		ExperimentalDataBinaryList blist = new ExperimentalDataBinaryList();
		blist.setName("Ejemplo");
		
		
		double pressure = Double.valueOf(lines.get(0).split("\\s+")[1]);

		List<ExperimentalDataBinary> list = new ArrayList();
		
		for(String line: lines){
			 String[] lineWords = line.split("\\s+");
			 
			 System.out.println(line);
			 
			 try{
				 double temperature =Double.valueOf( lineWords[0]);
				 double x =Double.valueOf( lineWords[1]);
				 double y=Double.valueOf( lineWords[2]);
				 
				 ExperimentalDataBinary data = new ExperimentalDataBinary(temperature, pressure, x, y);
				 list.add(data);
			 }catch(Exception ex){
				 System.out.println("Linea sin datos");
			 }
			 
		}
		blist.setReferenceComponent(getMethanol());
		blist.setNonReferenceComponent(getWater());
		blist.setList(list);
		return blist;
	}
	
	public HeterogeneousSubstance createSubstanceForComponent(HeterogeneousMixture mix, Compound compound){
		Cubic eos = mix.getEquationOfState();
		Alpha alpha = mix.getAlpha();
		HeterogeneousSubstance substance = new HeterogeneousSubstance(eos,alpha,compound);
		substance.getErrorFunction().setExperimental(compound.getExperimentalLists().iterator().next().getList());
		return substance;
	}
	
	public void optimizeAlphaForMixture(HeterogeneousMixture mix, Compound compound){
		HeterogeneousSubstance substance = createSubstanceForComponent(mix, compound);

		substance.getErrorFunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		substance.getErrorFunction().minimize();
	}
	
	
}
