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
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.errorfunctions.TemperatureMixtureErrorData;

public class BinaryOptimizationFileGenerator extends FileGenerator{
	public ExperimentalDataBinaryList getBinaryExperimentalListFromFile() throws IOException{
		Path path = Paths.get("/home/hugo/Documents/repositories/MateriaLatex/data/binary.dat");
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
	
	
	
	public void generateFiles(String folderName) throws IOException{
		prepareFolder(folderName);
		
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFile();
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
		
		mix.getErrorfunction().minimize();
		generateBinaryTemperatureDiagramDataInFile(mix,folderName, "errorAfterOptim.dat");
		
		
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
