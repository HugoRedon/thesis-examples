package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import compounds.CompoundReader;

import termo.component.Compound;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alphas;
import termo.matter.Substance;
import termo.phase.Phase;

public class VolumeFileGenerator extends FileGenerator{
	Substance substance;
	
	public VolumeFileGenerator(){
		CompoundReader reader = new CompoundReader();
		Compound heptane = reader.getCompoundByExactName("N-heptane");
		substance = new Substance(EquationsOfState.vanDerWaals()
				,Alphas.getVanDerWaalsIndependent()
				,heptane,Phase.LIQUID);
	}
	
	public void generateDiagram(String folderName) throws FileNotFoundException, UnsupportedEncodingException{
		File directory = new File(folderName);
		if(!directory.exists()){
			directory.mkdir();
		}
		
		Cubic cubic = EquationsOfState.vanDerWaals();
		
		double min_reducedPressure = 0.5;
		double max_reducedPressure= 7;
		double pressurepass =( max_reducedPressure- min_reducedPressure)/ 100;
		
		double min_reducedTemperature= 1 ;
		double max_reducedTemperature=2;
		
		double criticalTemperature = substance.getComponent().getCriticalTemperature();
		double criticalPressure = substance.getComponent().getCriticalPressure();
		double criticalVolume=substance.getComponent().getCriticalVolume();
		

	
		
		PrintWriter writer= new PrintWriter(folderName + "pz_vr.dat", "UTF-8");
		writer.println(" p z rt vr");
		
		for(double reducedTemperature = min_reducedTemperature; reducedTemperature <= max_reducedTemperature; reducedTemperature +=0.1){
			
			for(double reducedPressure = min_reducedPressure ; reducedPressure <= max_reducedPressure; reducedPressure+= pressurepass){	
				double temperature = criticalTemperature * reducedTemperature;
				double pressure = criticalPressure * reducedPressure;
				substance.setTemperature(temperature);
				substance.setPressure(pressure);
				
				
				double z =substance.calculateCompresibilityFactor();
				double volume = substance.calculateMolarVolume();
				double vr = volume/criticalVolume;
				writer.println(" " + reducedPressure + " " + z + " " + reducedTemperature + " " + vr);
			}
			writer.println();
			
		}
		writer.close();
	
		
	}

}
