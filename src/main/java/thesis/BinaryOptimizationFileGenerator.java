package thesis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import compounds.CompoundReader;
import termo.activityModel.ActivityModel;
import termo.activityModel.NRTLActivityModel;
import termo.binaryParameter.ActivityModelBinaryParameter;
import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataBinary;
import termo.data.ExperimentalDataBinaryList;
import termo.data.ExperimentalDataList;
import termo.eos.Cubic;
import termo.eos.EquationOfStateFactory;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.AlphaFactory;
import termo.eos.mixingRule.MixingRule;
import termo.eos.mixingRule.VDWMixingRule;
import termo.eos.mixingRule.WongSandlerMixingRule;
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.errorfunctions.TemperatureMixtureErrorData;

public class BinaryOptimizationFileGenerator extends FileGenerator{
	
	public void generateFiles(String folderName) throws IOException{
		//methanolWaterFiles(folderName+ "methanolWater/");
		carbonDioxideMethanolFiles(folderName+"co2Met/");
	}
	
	public ExperimentalDataBinaryList getBinaryExperimentalListFromFilePxy(String filePath) throws IOException{
		Path path = Paths.get(filePath);
		List<String> lines =Files.readAllLines(path);
		
		ExperimentalDataBinaryList blist = new ExperimentalDataBinaryList();
		blist.setName("Ejemplo");
		
		
		double temperature =273.15+ Double.valueOf(lines.get(0).split("\\s+")[1]);

		List<ExperimentalDataBinary> list = new ArrayList();
		
		for(String line: lines){
			 String[] lineWords = line.split("\\s+");
			 
			 System.out.println(line);
			 
			 try{
				 double pressure =101325*Double.valueOf( lineWords[0]);
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
	 
	public void selectBestAlpha(HeterogeneousSubstance substance){
		Set<ExperimentalDataList> set =(Set<ExperimentalDataList>)substance.getComponent().getExperimentalLists();
		List<ExperimentalData> list = ((ExperimentalDataList)set.iterator().next()).getList();
		substance.getErrorFunction().setExperimental(list);

		substance.setAlpha(AlphaFactory.getStryjekAndVeraExpression());
		substance.getErrorFunction().minimize();
		

	}
	public void carbonDioxideMethanolFiles(String folderName) throws IOException{
		prepareFolder(folderName);
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFilePxy("/home/hugo/Documents/repositories/MateriaLatex/data/co2Met25.dat");
		Cubic eos= EquationOfStateFactory.pengRobinsonBase();
		Alpha alpha = AlphaFactory.getPengAndRobinsonExpression();
		ActivityModel activityModel = new NRTLActivityModel();
		MixingRule mr = new WongSandlerMixingRule(activityModel, eos);
		
		
		CompoundReader reader = new CompoundReader();
		
		Compound cargonDioxide =reader.getCompoundByExactName("Carbon dioxide");
		Compound methanol = reader.getCompoundByExactName("Methanol");
		
		
		Set<Compound> compounds = new HashSet();
		compounds.add(cargonDioxide);
		compounds.add(methanol);
		
		ActivityModelBinaryParameter k = new ActivityModelBinaryParameter();
		
		HeterogeneousMixture mix = new HeterogeneousMixture(eos, alpha, mr, compounds, k);
		
		PrintWriter writer = new PrintWriter(folderName + "co2Met25.dat","UTF-8");
		writer.println(" pressure x y calcPressure ycalc");
		
		double temperature = blist.getList().get(0).getTemperature();
		mix.setTemperature(temperature);
		
//		0.5013 
//		0.1187 
//		0.3972 
		//activityModel.setParameter(0.5013, methanol, cargonDioxide, k, index);
		
		k.getK().setSymmetric(true);
		k.getK().setValue(cargonDioxide, methanol, 0.3972 );
		
		for (ExperimentalDataBinary data: blist.getList()){
			double x = data.getLiquidFraction();
			double y = data.getVaporFraction();
			double pressure = data.getPressure();
			
			mix.setZFraction(cargonDioxide, x);
			mix.setZFraction(methanol, 1-x);
			mix.bubblePressure();
			double calcpressure = mix.getPressure();
			double ycalc=mix.getVapor().getReadOnlyFractions().get(cargonDioxide); 
			
			writer.println(" " + pressure  + " "+ x + " " + y+ " " + calcpressure + " " +  ycalc);
		}
		writer.close();
		
	}
	public void methanolWaterFiles(String folderName) throws IOException{
		prepareFolder(folderName);
		
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFileTxy("/home/hugo/Documents/repositories/MateriaLatex/data/binary.dat");
		List<ExperimentalDataBinary> list = blist.getList(); 
		
		Cubic eos = EquationOfStateFactory.pengRobinsonBase();
		Alpha alpha = AlphaFactory.getStryjekAndVeraExpression();
		MixingRule mixingRule = new VDWMixingRule();
		Compound water = blist.getNonReferenceComponent();
		Compound methanol = blist.getReferenceComponent();
		
		
		
		Set<Compound> compounds =new HashSet();
		compounds.add(water);
		compounds.add(methanol);
		
		InteractionParameter k = new InteractionParameter();
		HeterogeneousMixture mix = new HeterogeneousMixture(eos, alpha, mixingRule, compounds, k);
		mix.getErrorfunction().setReferenceComponent(methanol);
		mix.getErrorfunction().setNonReferenceComponent(water);
		mix.getErrorfunction().setExperimental(list);
		
	
		generateBinaryTemperatureDiagramDataInFile(mix,folderName, "error.dat");
		
		generateAlphaOptim(mix,water,folderName,"errorWaterAlpha");
		generateAlphaOptim(mix,methanol,folderName,"errorMethanolAlpha");
		
		System.out.println("stryjekMethanol k: "+ methanol.getK_StryjekAndVera());
		System.out.println("stryjekWater k: "+ water.getK_StryjekAndVera());
		
		
		mix.getErrorfunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		mix.getErrorfunction().minimize();
		generateBinaryTemperatureDiagramDataInFile(mix,folderName, "errorAfterOptim.dat");
		
	}
	
	


	
	
	
	public void generateAlphaOptim(HeterogeneousMixture mix,Compound compound, String folderPath,String fileNameWithouthExtension) throws FileNotFoundException, UnsupportedEncodingException{
		Cubic eos = mix.getEquationOfState();
		Alpha alpha = mix.getAlpha();
		
			
		HeterogeneousSubstance substance = new HeterogeneousSubstance(eos,alpha,compound);
		substance.getErrorFunction().setExperimental(compound.getExperimentalLists().iterator().next().getList());
		substance.getErrorFunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		
		printAlphaOptimization(substance, folderPath, fileNameWithouthExtension + ".dat");
		substance.getErrorFunction().minimize();
		printAlphaOptimization(substance, folderPath, fileNameWithouthExtension + "AfterOptim.dat");
		
		
	}
	
	
	public void printAlphaOptimization(HeterogeneousSubstance substance,String folderPath,String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(folderPath + fileName,"UTF-8");
		writer.println(" temperature expPressure calcPressure");
		for(ErrorData error: substance.getErrorFunction().getErrorForEachExperimentalData()){
			double temperature = error.getTemperature();
			double expPressure = error.getExperimentalPressure();
			double calcPressure =error.getCalculatedPressure();
			writer.println(" "+temperature + " "+ expPressure + " "+calcPressure );
		}
		writer.close();
	}
	
	
	protected void generateBinaryTemperatureDiagramDataInFile(HeterogeneousMixture mix, String folderPath,String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(folderPath + fileName, "UTF-8");
		writer.println( " x1 yExp expTemp calcTemp yCalc");
		for(TemperatureMixtureErrorData error: mix.getErrorfunction().getErrorForEachExperimentalData()){
			double x1 = error.getLiquidFraction();
			double yExp=error.getExperimentalVaporFraction();
			
			double yCalc = error.getCalculatedVaporFraction();
			double expTemp = error.getExperimentalTemperature();
			double calcTemp =error.getCalculatedTemperature();
			
			writer.println(" "+ x1 + " "+ yExp + " "+ expTemp + " "+ calcTemp +" "+ yCalc);
		}
		writer.close();
	}
	
	
}
