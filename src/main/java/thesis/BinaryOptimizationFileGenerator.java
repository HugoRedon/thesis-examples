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

import termo.activityModel.ActivityModel;
import termo.activityModel.NRTLActivityModel;
import termo.binaryParameter.ActivityModelBinaryParameter;
import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataBinary;
import termo.data.ExperimentalDataBinaryList;
import termo.data.ExperimentalDataBinaryType;
import termo.data.ExperimentalDataList;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.Alphas;
import termo.eos.mixingRule.MixingRule;
import termo.eos.mixingRule.VDWMixingRule;
import termo.eos.mixingRule.WongSandlerMixingRule;
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.errorfunctions.TemperatureMixtureErrorData;
import compounds.CompoundReader;

public class BinaryOptimizationFileGenerator extends FileGenerator {
	
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
			 
			 //System.out.println(line);
			 
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
	
	
	

	 

	public void carbonDioxideMethanolFiles(String folderName) throws IOException{
		prepareFolder(folderName);
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFilePxy("/home/hugo/Documents/repositories/MateriaLatex/data/co2Met25.dat");
		Cubic eos= EquationsOfState.pengRobinson();
		Alpha alpha = Alphas.getPengAndRobinsonExpression();
		NRTLActivityModel activityModel = new NRTLActivityModel();
		MixingRule mr = new WongSandlerMixingRule(activityModel, eos);
		
		
		CompoundReader reader = new CompoundReader();
		
		Compound carbonDioxide =reader.getCompoundByExactName("Carbon dioxide");
		Compound methanol = reader.getCompoundByExactName("Methanol");
		
		
		Set<Compound> compounds = new HashSet();
		compounds.add(carbonDioxide);
		compounds.add(methanol);
		
		ActivityModelBinaryParameter k = new ActivityModelBinaryParameter();
		
		HeterogeneousMixture mix = new HeterogeneousMixture(eos, alpha, mr, compounds, k);
		
		
		mix.getErrorfunction().setReferenceComponent(carbonDioxide);
		mix.getErrorfunction().setNonReferenceComponent(methanol);
		mix.getErrorfunction().setExperimental(blist.getList(), ExperimentalDataBinaryType.isothermic);

		k.getA().setValue(carbonDioxide, methanol, 0);
		k.getA().setValue(methanol, carbonDioxide, 0);
		
		k.getB().setValue(carbonDioxide, methanol, 8314*0.5013);
		k.getB().setValue(methanol, carbonDioxide, 8314*0.1187);
		
		k.getAlpha().setSymmetric(true);
		k.getAlpha().setValue(methanol, carbonDioxide, 0.3);
		
		k.getK().setSymmetric(true);
		k.getK().setValue(carbonDioxide, methanol, 0.3972 );
		
		
		
		
		boolean[]fixParameters = {true,true,false,false,true,false,true};
		mix.getErrorfunction().getOptimizer().setFixParameters(fixParameters);
		mix.getErrorfunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		mix.getErrorfunction().minimize();
		System.out.println("mensaje: " + mix.getErrorfunction().getOptimizer().getMessage());

		
		
		
		double alpha12 =k.getAlpha().getValue(carbonDioxide, methanol);
		System.out.println("alpha12: " + alpha12);
		double alpha21 =k.getAlpha().getValue( methanol,carbonDioxide);
		System.out.println("alpha21: " + alpha21);
		
		
		double A12 =k.getA().getValue(carbonDioxide, methanol);
		System.out.println("A12: " + A12);
		double A21 =k.getA().getValue( methanol,carbonDioxide);
		System.out.println("A21: " + A21);

		
		double B12 =k.getB().getValue(carbonDioxide, methanol);
		System.out.println("B12: " + B12);
		double B21 =k.getB().getValue( methanol,carbonDioxide);
		System.out.println("B21: " + B21);
		
		
		double k12 =k.getK().getValue(carbonDioxide, methanol);
		System.out.println("K12: " + k12);
		double k21 =k.getK().getValue( methanol,carbonDioxide);
		System.out.println("K21: " + k21);
		
		double tau12 =activityModel.tau(carbonDioxide, methanol, k, 25+273.15);
		System.out.println("tau12: " + tau12);
		double tau21 =activityModel.tau( methanol,carbonDioxide, k, 25+273.15);
		System.out.println("tau21: " + tau21);
		
	
		
		double tau11 =activityModel.tau( carbonDioxide,carbonDioxide, k, 25+273.15);
		System.out.println("tau11: " + tau11);
		double tau22 =activityModel.tau( methanol,methanol, k, 25+273.15);
		System.out.println("tau22: " + tau22);
		
		
		
		
		double g12 =activityModel.G(carbonDioxide, methanol, k, 25+273.15);
		System.out.println("G12: " + g12);
		double g21 =activityModel.G( methanol,carbonDioxide, k, 25+273.15);
		System.out.println("G21: " + g21);
		

		double g11 =activityModel.G(carbonDioxide, carbonDioxide, k, 25+273.15);
		System.out.println("G11: " + g11);
		double g22 =activityModel.G( methanol,methanol, k, 25+273.15);
		System.out.println("G22: " + g22);
	
		
		
		System.out.println("error: " + mix.getErrorfunction().error());
		
		PrintWriter writer = new PrintWriter(folderName + "co2Met25.dat","UTF-8");
		writer.println(" pressure x y calcPressure ycalc");
		
		double temperature = blist.getList().get(0).getTemperature();
		mix.setTemperature(temperature);

		
		for (ExperimentalDataBinary data: blist.getList()){
			double x = data.getLiquidFraction();
			double y = data.getVaporFraction();
			double pressure = data.getPressure();
			
			mix.setZFraction(carbonDioxide, x);
			mix.setZFraction(methanol, 1-x);
			mix.bubblePressure();
			double calcpressure = mix.getPressure();
			double ycalc=mix.getVapor().getReadOnlyFractions().get(carbonDioxide); 
			String line=" " + pressure  + " "+ x + " " + y+ " " + calcpressure + " " +  ycalc;
			System.out.println(line);
			writer.println(line);
		}
		writer.close();
		
	}
	

	
	public void methanolWaterFiles(String folderName) throws IOException{
		prepareFolder(folderName);
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFileTxy("/home/hugo/Documents/repositories/MateriaLatex/data/binary.dat");
		HeterogeneousMixture mix = prepareWaterMethanolMixture(blist);
		
		Compound water = blist.getNonReferenceComponent();
		Compound methanol = blist.getReferenceComponent();
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
		
		HeterogeneousSubstance substance =createSubstanceForComponent(mix, compound);
		printAlphaOptimization(substance, folderPath, fileNameWithouthExtension + ".dat");
		optimizeAlphaForMixture(mix, compound);
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
