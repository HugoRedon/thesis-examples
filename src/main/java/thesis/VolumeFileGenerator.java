package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.phase.Phase;

public class VolumeFileGenerator extends FileGenerator{
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
		
		double criticalTemperature = 540.2;
		double criticalPressure = 2.74000E+06;
		double criticalVolume=0.428;
		
		double a = 3107000.0;
		double b = 0.2049;
	
		
		PrintWriter writer= new PrintWriter(folderName + "pz_vr.dat", "UTF-8");
		writer.println(" p z rt vr");
		
		for(double reducedTemperature = min_reducedTemperature; reducedTemperature <= max_reducedTemperature; reducedTemperature +=0.1){
			
			for(double reducedPressure = min_reducedPressure ; reducedPressure <= max_reducedPressure; reducedPressure+= pressurepass){	
				double temperature = criticalTemperature * reducedTemperature;
				double pressure = criticalPressure * reducedPressure;
				double A =cubic.get_A(temperature, pressure, a);
				double B = cubic.get_B(temperature, pressure, b);
				
				double z =cubic.calculateCompresibilityFactor(A, B, Phase.LIQUID);
				double volume = cubic.calculateVolume(temperature, pressure, z);
				double vr = volume/criticalVolume;
				writer.println(" " + reducedPressure + " " + z + " " + reducedTemperature + " " + vr);
			}
			writer.println();
			
		}
		writer.close();
	
		
	}

}
