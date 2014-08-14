package thesis;

import java.io.IOException;
import java.io.PrintWriter;

import termo.component.Compound;
import termo.data.ExperimentalDataBinaryList;
import termo.matter.HeterogeneousMixture;
import termo.matter.HeterogeneousSubstance;

public class HeterogeneousFilesGenerator extends FileGenerator{
	
	public void generate3dDiagram(String folderpath)throws IOException{			
		
		prepareFolder(folderpath);
		PrintWriter writer = new PrintWriter(folderpath + "et.dat","UTF-8");
		
		HeterogeneousSubstance substance = prepareSubstance();
		
		double minTemperature = 350;
		double maxTemperature = substance.getComponent().getCriticalTemperature();
		
		Integer n = 30;
		double temperatureStep = (maxTemperature -minTemperature)/n.doubleValue();
		
		writer.println(" temperature liquidVolume vaporVolume pressure");
		for(Integer i = 0 ; i < n ; i++){
			double temperature = minTemperature  + temperatureStep * i;
			System.out.println("temp:" + temperature);
			
			substance.setTemperature(temperature);
			substance.saturationPressure();
			Double pressure = substance.getPressure();
			
			Double liquidVolume = substance.getLiquid().calculateMolarVolume();
			Double vaporVolume = substance.getVapor().calculateMolarVolume();
			
			writer.println(" " + temperature  + " " + liquidVolume + " " + vaporVolume+ " " + pressure);
		}
		
		writer.close();
	}
	
	public void generateBinaryEquilibriaFile(String folderName)throws IOException{
		prepareFolder(folderName);
		ExperimentalDataBinaryList blist = getBinaryExperimentalListFromFileTxy(
				DataFilesGenerator.repoPath + "data/binary.dat");
		HeterogeneousMixture mix = prepareWaterMethanolMixture(blist);
		
		for(Compound compound: mix.getComponents()){
			optimizeAlphaForMixture(mix, compound);
		}
		mix.getErrorfunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		mix.getErrorfunction().minimize();
		
		
		Compound referenceCompound = mix.getErrorfunction().getReferenceComponent();
		Compound nonReferenceCompound = mix.getErrorfunction().getNonReferenceComponent();
		
		Double minTemperature =  330d;
		Double maxTemperature = 370d;//referenceCompound.getCriticalTemperature();
		
		Double n = 25d;
		Double tempStep = (maxTemperature-minTemperature)/n;
		
		PrintWriter writer =new  PrintWriter(folderName  + "hp.dat","UTF-8");
		
		writer.println(" liquidFraction vaporFraction temperature pressure");
		for(Double t=0d; t < n;t++){
		Double temperature = minTemperature + t*tempStep;
			for(Double i = 0d; i < 1; i += 0.1d){
				mix.setZFraction(referenceCompound, i);
				mix.setZFraction(nonReferenceCompound, 1-i);
				
				mix.setTemperature(temperature);
				
				mix.bubblePressure();
				Double pressure = mix.getPressure();
				Double y = mix.getVapor().getReadOnlyFractions().get(referenceCompound);
				writer.println(" " + i + " " + y + " " + temperature + " " + pressure);
			}
			writer.println();
			
		}
		writer.close();
		
		
	}

}
