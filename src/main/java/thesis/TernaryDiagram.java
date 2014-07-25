package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import termo.activityModel.NRTLActivityModel;
import termo.binaryParameter.ActivityModelBinaryParameter;
import termo.binaryParameter.InteractionParameter;
import termo.component.Compound;
import termo.data.Experimental;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataBinary;
import termo.data.ExperimentalDataBinaryType;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.Alphas;
import termo.eos.mixingRule.MixingRule;
import termo.eos.mixingRule.VDWMixingRule;
import termo.eos.mixingRule.WongSandlerMixingRule;
import termo.equations.Eqn101VaporPressure;
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.Parameters_Error;
import termo.optimization.errorfunctions.TemperatureMixtureErrorData;
import compounds.CompoundReader;

public class TernaryDiagram extends FileGenerator {
	
	public List<Double[]> readTernaryData() throws IOException{
		Path path = Paths.get("/home/hugo/Documents/repositories/MateriaLatex/data/acetonemethanolcyclohexaneVL45.dat");
		List<String> lines = Files.readAllLines(path);
		double temperature =273.15+ Double.valueOf(lines.get(0).split("\\s+")[1]);
		lines.remove(0);
		List<Double[]> list = new ArrayList();
		for(String line: lines){
			 String[] lineWords = line.split("\\s+");
			 
			 try{
				 //System.out.println(lineWords[0]);
				 double x1 =Double.valueOf( lineWords[0]);
				 double x2=Double.valueOf( lineWords[1]);
				 double x3 =1-x1-x2;
				 Double[] a ={x1,x2,x3}; 
				 list.add(a);
			 }catch(Exception ex){
				 System.out.println("Linea sin datos");
			 }
			 
		}
		return list;
	}
	
	public void generateTernaryDataFiles(String folderPath) throws IOException{
//		prepareFolder(folderPath);
//		
//		List<Double[]> compositionLine  = readTernaryData();
//		PrintWriter writer = new PrintWriter(folderPath + "graph.dat");
//		
//		writer.println(" x1 x2 x3");
//		
//		for(Double[] point : compositionLine){
//			writer.println(" "  + point[0] + " " + point[1] + " " + point[2]);
//		}
//		writer.close();
		CompoundReader reader = new CompoundReader();
		
		Compound ethylene =reader.getCompoundByExactName("ethylene"); 
		Compound water = reader.getCompoundByExactName("water");
		Compound ethanol = reader.getCompoundByExactName("ethanol");
		
		
		Cubic eos = EquationsOfState.pengRobinson();
		Alpha alpha = Alphas.getPengAndRobinsonExpression();
		
		
		optimizeAlphaExpression(ethylene, alpha,
				eos,getEqn101Experimental(ethylene) ,folderPath + "ethylene/");
		
		optimizeAlphaExpression(water, alpha,
				eos,getEqn101Experimental(water) ,folderPath + "water/");
		optimizeAlphaExpression(ethanol, alpha,
				eos,getEqn101Experimental(ethanol) ,folderPath + "ethanol/");
		
	
		//NRTLActivityModel nrtl = new NRTLActivityModel();
		MixingRule mixingrule = new VDWMixingRule();
		Set<Compound> components = new HashSet();
		
		components.add(ethylene);
		components.add(water);
		components.add(ethanol);
		
		InteractionParameter k = new InteractionParameter();
		
		List<ExperimentalDataBinary> binaryExperimental = 
				readExperimentalDataBinary("/home/hugo/Documents/repositories/MateriaLatex/data/ethylenewater200",273.15+ 200);
		
		
		optimizeMixingRuleWith(ethylene,water,k,binaryExperimental,eos,alpha,mixingrule,
				folderPath+ "ethylenewater/");
		
		
//		ActivityModelBinaryParameter params = new ActivityModelBinaryParameter();
//		params.getAlpha().setSymmetric(true);
//		params.getAlpha().setValue(ethylene, water, 0.3);
//		
//		params.getK().setSymmetric(true);
//		params.getK().setValue(ethylene, water, 1);
		
		
//		optimizeMixingRuleWith(ethylene,water,params,binaryExperimental,eos,alpha,mixingrule,
//				folderPath+ "ethylenewater/");
		
		
		HeterogeneousMixture mixture = new HeterogeneousMixture(eos,
				alpha, mixingrule, components, k);
			
		int n = 6;
		
		
		PrintWriter writer = new PrintWriter(folderPath + "graph.dat");
		writer.println(" x1 x2 x3 y1 y2 y3 ");
		
		
		for(int i =0; i < n; i++){
			for(int j =0; j<n; j++){
				double z1 =Double.valueOf(i)/10d; 
				double z2 = Double.valueOf(j)/10d;
				double z3 = 1-z1-z2;
				
				double sum = z1+z2+z3;
				System.out.println(sum);
				if(sum > 1){
					
					System.out.println("error");
				}
		
				mixture.setZFraction(ethylene,z1);
				mixture.setZFraction(water, z2);
				mixture.setZFraction(ethanol, z3);
			
			
				mixture.flash(200+273.15,120*101325);
			
				
				double x1 = mixture.getLiquid().getReadOnlyFractions().get(water);
				double x2 = mixture.getLiquid().getReadOnlyFractions().get(ethylene);
				double x3 = mixture.getLiquid().getReadOnlyFractions().get(ethanol);
				
				double y1 = mixture.getVapor().getReadOnlyFractions().get(water);
				double y2 = mixture.getVapor().getReadOnlyFractions().get(ethylene); 
				double y3 = mixture.getVapor().getReadOnlyFractions().get(ethanol);
				
				if(!Double.isNaN(x1)){
				
					writer.println(" " + x1+" " + x2+" " + x3+" " + y1+" " + y2+" " + y3  );
				}
			}
		}
	
		writer.close();	
	}
	
	public List<ExperimentalDataBinary> readExperimentalDataBinary(String partialFileName,double temperature) throws IOException{
		List<ExperimentalDataBinary> result = new ArrayList();
		
		
		Path path = Paths.get(partialFileName + "liquid.txt");
		Path path2 = Paths.get(partialFileName + "vapor.txt");
		List<String> lines = Files.readAllLines(path);
		List<String> lines2 = Files.readAllLines(path2);
		lines.remove(0);
		lines2.remove(0);
		int n = lines.size();
		for(int i = 0; i < n ; i++){
			String[] liquidwords = lines.get(i).split(",");
			String[] vaporwords = lines2.get(i).split(",");
			
			
			double x1 = Double.valueOf(liquidwords[0]);
			double x2 = 1-x1;
			
			double y1 = Double.valueOf(vaporwords[0]);
			double y2 = 1-y1;
			
			
			double pressure = 101325*Double.valueOf(liquidwords[1]);
			double pressure2 = 101325*Double.valueOf(vaporwords[1]);
			
			if(pressure-pressure2 > 1e-2){
				System.out.println("datos pobremente leidos");
			}
			
			ExperimentalDataBinary data = new ExperimentalDataBinary(temperature, pressure, x1, y1);
			result.add(data);
		}
		
		return result;
	}
	
	public void optimizeMixingRuleWith(Compound reference,Compound c2,
			InteractionParameter k,List<ExperimentalDataBinary> experimental,
			Cubic eos,Alpha alpha,MixingRule mr,String folderpath) throws FileNotFoundException{
		prepareFolder(folderpath);
		Set<Compound> components = new HashSet();
		components.add(reference);
		components.add(c2);
		
		HeterogeneousMixture mix = new HeterogeneousMixture(eos,alpha,mr, components, k);
		
		k.setSymmetric(true);
		mix.getErrorfunction().setReferenceComponent(reference);
		mix.getErrorfunction().setNonReferenceComponent(c2);
		mix.getErrorfunction().setExperimental(experimental, ExperimentalDataBinaryType.isothermic);
		mix.getErrorfunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		mix.getErrorfunction().getOptimizer().setTolerance(1e-8);
		
		graphHeterogeneousMixture(mix,folderpath + "beforeOptim.dat");
		mix.getErrorfunction().minimize();
		System.out.println("k: " + k.getValue(reference, c2));
		System.out.println("k: " + k.getValue(c2, reference));
		graphHeterogeneousMixture(mix,folderpath + "afterOptim.dat");
		
	}
	
	
	public void graphHeterogeneousMixture(HeterogeneousMixture mix,String fileName) throws FileNotFoundException{
		PrintWriter writer =new PrintWriter(fileName);
		writer.println(" x1 x2 y1 y2 pressure y1calc calcpressure error ");
		
		for(TemperatureMixtureErrorData error: mix.getErrorfunction().getErrorForEachExperimentalData()){
			double x1 = error.getLiquidFraction();
			double x2 = 1-x1;
			double y1 = error.getExperimentalVaporFraction();
			double y2 = 1-y1;
			
			double ycalc = error.getCalculatedVaporFraction();
			double pcalc = error.getCalculatedPressure();
			
			double pressure = error.getExperimentalPressure();
			double err = error.getRelativeError();
					
			writer.println(" " + x1 + " "+ x2 + " "+ y1 + " "+ y2 + " "+ pressure 
					+ " "+ ycalc + " "+ pcalc + " "+ err);
		}
		
		writer.close();
		
	}
	
	public List<Experimental> getEqn101Experimental(Compound compound){
		List<Experimental> result =new ArrayList();
		
		int n = 40;
		
		Eqn101VaporPressure eqn =compound.getEqn101VaporPressure(); 
		double minTemp = eqn.getMinTemperature();
		double maxTemp = eqn.getMaxTemperature();
		
		double tempPass = (maxTemp- minTemp)/Double.valueOf(n);
		for(int i =0; i < n; i++){
			double temperature = minTemp+  Double.valueOf(i)*tempPass;
			double vaporPressure = eqn.vaporPressure(temperature);
			ExperimentalData data = new ExperimentalData(temperature, vaporPressure);
			result.add(data);
		}
		
		return result;
	}
	
	public void optimizeAlphaExpression(Compound compound , Alpha alpha,Cubic eos,
		List<Experimental> experimental,String folderPath) throws FileNotFoundException{
		
		prepareFolder(folderPath);
		HeterogeneousSubstance substance = new HeterogeneousSubstance(eos, alpha, compound);
		substance.getErrorFunction().setExperimental(experimental);
		graphAlpha(substance, folderPath + "beforeOptim.dat");
		substance.getErrorFunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		substance.getErrorFunction().minimize();
		System.out.println("Mensaje: " + substance.getErrorFunction().getOptimizer().getMessage());
		
		double a =compound.getA_Mathias_Copeman();
		double b = compound.getB_Mathias_Copeman();
		double c = compound.getC_Mathias_Copeman();

		System.out.println("a: " +a );
		System.out.println("b: " +b );
		System.out.println("c: " +c );
		
		
		
		graphAlpha(substance, folderPath +"afterOptim.dat");
		graphAlphaConvergenceHistory(substance , folderPath +  "history.dat");
	}
	public void graphAlphaConvergenceHistory(HeterogeneousSubstance substance, String fileName) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(fileName);
		writer.append(" iteration");
		for(int i = 0; i < substance.getErrorFunction().getOptimizer().numberOfVariablesToOptimize();i++){
			writer.append(" " + substance.getAlpha().getParameterName(i) );
		}
		writer.append(" Error");
		writer.println();
		for(Parameters_Error error: substance.getErrorFunction().getOptimizer().getConvergenceHistory()){
			writer.append(" " + error.getIteration());
			for(int i =0; i< error.getParameters().length;i++){
				double[] parameters = error.getParameters();
				writer.append(" " + parameters[i]);
			}
			writer.append(" " + error.getError());
			writer.println();
		}
		writer.close();
	}
	public void graphAlpha(HeterogeneousSubstance substance, String fileName) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(fileName);
		writer.println(" Temperature experimentalPressure calculatedPressure error");
		for(ErrorData error:substance.getErrorFunction().getErrorForEachExperimentalData()){
			double temperature = error.getTemperature();
			double calcP = error.getCalculatedPressure();
			double expP= error.getExperimentalPressure();
			double err =error.getError(); 
			writer.println(" " +temperature +" " +expP+ " " + calcP + " " + err ) ;
		}
		
		writer.close();
	}
	
	
	
}
