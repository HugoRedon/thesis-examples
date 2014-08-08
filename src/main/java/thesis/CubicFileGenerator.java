package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import termo.component.Compound;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alphas;
import termo.matter.Mixture;
import termo.matter.Substance;
import termo.phase.Phase;

import compounds.CompoundReader;

public class CubicFileGenerator extends FileGenerator {
	Substance substance;
	public CubicFileGenerator(){
		CompoundReader reader = new CompoundReader();
		Compound heptane = reader.getCompoundByExactName("N-heptane");
		substance = new Substance(EquationsOfState.vanDerWaals()
				,Alphas.getVanDerWaalsIndependent()
				,heptane,Phase.VAPOR);
	}

	public void cubicEquationPressureVolumeTemperatureFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException{		
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(" Volumen Presion Temperatura");
		
		double min_volume = 0.245;
		double max_volume = 1.2;
		int n = 60;
		double pass = (max_volume- min_volume)/n;
		

		
		int nt =20;
		double min_temperature = 150;
		double max_temperature = 400;
		
		double tempPass = (max_temperature - min_temperature)/nt;
		
		for(int j = 0; j < nt; j++){
			double temperature = min_temperature + tempPass *j ;
			for(int i=0;i < n; i++){
				double volume = min_volume + pass*i;
				double pressure = calculatePressure(volume, temperature);
				writer.println(" "+ volume + " " + temperature + " " + pressure);
				
			}
			writer.println();
			
		}
		writer.close();			
	}
	public double calculatePressure(double volume, double temperature){
		
		return substance.calculatePressure(temperature,volume);
		//parametros de van der waals para el heptano
	//	double a = 3107000.0;
		//double b = 0.2049;
//		return cubic.calculatePressure(temperature, volume,a,b);
	}
	
	public void cubicEquationPressureVolumeFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(" Volumen Presion");
		
		double min_volume = 0.245;
		double max_volume = 1.2;
		int n = 100;
		double pass = (max_volume- min_volume)/n;

		for(int i=0;i < n; i++){
			double volume = min_volume + pass*i;
			double pressure =calculatePressure(volume, 300);
			writer.println(" "+ volume + " " + pressure);
		}
		writer.close();		
	}
public void cubicEquationCompresibilitiFactorFiles(String folderName) throws FileNotFoundException, UnsupportedEncodingException{
		
		File directory = new File(folderName);
		if(!directory.exists()){
			directory.mkdir();
		}
		
		Cubic cubic = EquationsOfState.vanDerWaals();
		
		double min_reducedPressure = 0.1;
		double max_reducedPressure= 7;
		double pressurepass =( max_reducedPressure- min_reducedPressure)/ 100;
		
		double min_reducedTemperature= 1 ;
		double max_reducedTemperature=2;
		
		double criticalTemperature = 540.2;
		double criticalPressure = 2.74000E+06;
		
		double a = 3107000.0;
		double b = 0.2049;
	
		
		PrintWriter writer= new PrintWriter(folderName + "pz_temp.dat", "UTF-8");
		writer.println(" p z rt");
		
		for(double reducedTemperature = min_reducedTemperature; reducedTemperature <= max_reducedTemperature; reducedTemperature +=0.1){
			
			for(double reducedPressure = min_reducedPressure ; reducedPressure <= max_reducedPressure; reducedPressure+= pressurepass){	
				double temperature = criticalTemperature * reducedTemperature;
				double pressure = criticalPressure * reducedPressure;
//				double A =cubic.get_A(temperature, pressure, a);
//				double B = cubic.get_B(temperature, pressure, b);
				
				substance.setPressure(pressure);
				substance.setTemperature(temperature);
				double z = substance.calculateCompresibilityFactor();
				//double z =cubic.calculateCompresibilityFactor(A, B, Phase.LIQUID);
				writer.println(" " + reducedPressure + " " + z + " " + reducedTemperature);
			}
			writer.println();
			
		}
		writer.close();
	
	}
	
}
