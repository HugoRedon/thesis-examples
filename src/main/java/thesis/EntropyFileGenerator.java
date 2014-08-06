package thesis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import termo.matter.HeterogeneousSubstance;

public class EntropyFileGenerator extends FileGenerator {
	
	public void entropyDiagram3dFile(String folderName) throws FileNotFoundException, UnsupportedEncodingException{
		prepareFolder(folderName);
		HeterogeneousSubstance substance = prepareSubstance();
		double min_temp = 382.57;
		double max_temp  = substance.getComponent().getCriticalTemperature();
		
		double min_pressure =0;
		double max_pressure = 0;
		
			
		PrintWriter writer = new PrintWriter(folderName + "lv3d.dat" , "UTF-8");
		writer.println(" entropy volume pressure temperature");
		
		double tempPass = (max_temp - min_temp)/40;
		
		for(double temp = min_temp; temp <= max_temp; temp+=  tempPass ){
			substance.setTemperature(temp);
			substance.dewPressure();
			double pressure = substance.getPressure();
			min_pressure = (min_pressure == 0)?pressure: min_pressure;
			min_pressure = (pressure < min_pressure)? pressure: min_pressure;
			max_pressure = (pressure > max_pressure)? pressure: max_pressure;
			
			double liquidEntropy = substance.getLiquid().calculateEntropy();
			double vaporEntropy = substance.getVapor().calculateEntropy();
			double liquidVolume = substance.getLiquid().calculateMolarVolume();
			double vaporVolume = substance.getVapor().calculateMolarVolume();
			
			writer.println(" " + liquidEntropy  + " " +liquidVolume + " " + pressure+ " " +temp);
			writer.println(" " + vaporEntropy + " " +vaporVolume + " " + pressure + " "+  temp);
			
			writer.println();
		}
		
		writer.close();
		
		PrintWriter lwriter = new PrintWriter(folderName + "l3d.dat" , "UTF-8");
		lwriter.println(" entropy volume pressure temperature");
		double min_pressureSaved = min_pressure;
		max_pressure = max_pressure *1.3;
		min_pressure = 0.9* min_pressure;
		System.out.println("maxpressure : " + max_pressure);
		System.out.println("minpressure : " + min_pressure);
		
		for(double temp = min_temp; temp <= max_temp; temp+= tempPass ){
			substance.setTemperature(temp);
			substance.dewPressure();
			double pressure = substance.getPressure();
			
			int n = 10;
			double pressPass = (max_pressure- pressure) / Double.valueOf(n);
			for(int i = 0 ; i < n;i++){
				double press= pressure + i * pressPass;
				substance.setPressure(press);
				double liquidEntropy = substance.getLiquid().calculateEntropy();
				double liquidVolume = substance.getLiquid().calculateMolarVolume();
				lwriter.println(" " + liquidEntropy+ " "+ liquidVolume + " " + press + " "+temp);
			}
			lwriter.println();

		}
		lwriter.close();
		
		
		PrintWriter vwriter = new PrintWriter(folderName + "v3d.dat" , "UTF-8");
		vwriter.println(" entropy volume pressure temperature");
		double aboveCritical = max_temp * 1.05; 
		
		for(double temp = min_temp; temp <= aboveCritical; temp+= tempPass ){
			substance.setTemperature(temp);
			substance.dewPressure();
			double pressure = substance.getPressure();
			
			int n = 10;
			double pressPass= (pressure- min_pressure)/Double.valueOf(n);	
			for(int i=0 ; i < n; i++){
					double press = pressure - i * pressPass;
					substance.setPressure(press);
					double vaporEntropy = substance.getVapor().calculateEntropy();	
					double vaporVolume = substance.getVapor().calculateMolarVolume();
					vwriter.println(" " + vaporEntropy + " " +vaporVolume+ " " + press+ " "+temp);
			}
			vwriter.println();
		}
		vwriter.close();
		System.out.println("Region supercritica");
		
		
//		PrintWriter swriter = new PrintWriter(folderName + "s3d.dat", "UTF-8");
//		swriter.println(" enthalpy volume pressure");
		
		
		
//		for(double temp= max_temp ; temp <= aboveCritical; temp+= tempPass){
//			
//			substance.setTemperature(temp);
//			int n = 20;
//			double pressPas = (max_pressure - min_pressureSaved)/n;
//			
//			for(int i =0; i< n; i++){
//				double press = min_pressureSaved + Double.valueOf(i) * pressPas;
//				substance.setPressure(press);
//				double vaporEnthalpy = substance.getVapor().calculateEnthalpy();
//				double vaporVolume = substance.getVapor().calculateMolarVolume();
//				swriter.println(" " + vaporEnthalpy + " "+vaporVolume+ " " + press);
//			}
//			swriter.println();
//				
//		}
//		
//		System.out.println("fin");
//		swriter.close();
		
		
		
	}
	
	public void entropyDiagramFile(String folderName) throws FileNotFoundException, UnsupportedEncodingException{
		prepareFolder(folderName);
		HeterogeneousSubstance substance = prepareSubstance();
		double min_temp = 382.57;
		double max_temp  = substance.getComponent().getCriticalTemperature();
		
		double min_pressure =0;
		double max_pressure = 0;
		
			
		PrintWriter writer = new PrintWriter(folderName + "lv.dat" , "UTF-8");
		writer.println(" entropy p");
		
		double n = 30;
		
		double tempPass = (max_temp - min_temp)/n;
		
		for(double temp = min_temp; temp <= max_temp; temp+=  tempPass ){
			substance.setTemperature(temp);
			substance.dewPressure();
			double pressure = substance.getPressure();
			min_pressure = (min_pressure == 0)?pressure: min_pressure;
			min_pressure = (pressure < min_pressure)? pressure: min_pressure;
			max_pressure = (pressure > max_pressure)? pressure: max_pressure;
			
			double liquidEntropy = substance.getLiquid().calculateEntropy();
			double vaporEntropy = substance.getVapor().calculateEntropy();
			writer.println(" " + liquidEntropy + " " + pressure);
			writer.println(" " + vaporEntropy + " " + pressure);
			
			writer.println();
		}
		max_pressure = max_pressure *1.3;
		min_pressure = 0.9* min_pressure;
		System.out.println("maxpressure : " + max_pressure);
		System.out.println("minpressure : " + min_pressure);
		
		for(double temp = min_temp; temp <= max_temp; temp+= tempPass ){
			substance.setTemperature(temp);
			substance.dewPressure();
			double pressure = substance.getPressure();
			double pressPass = (max_pressure- pressure) / n;
			for(double press = pressure; press <= max_pressure; press += pressPass){
				substance.setPressure(press);
				double liquidEntropy = substance.getLiquid().calculateEntropy();
				if(Double.isInfinite(liquidEntropy)|| Double.isNaN(liquidEntropy)){
					System.out.print("error");
				}
				writer.println(" " + liquidEntropy + " " + press);
			}
			writer.println();
			
			pressPass= (pressure- min_pressure)/n;
			
			for(double press = pressure ; press >= min_pressure; press -= pressPass){
					//System.out.println("region vapor" + press);
					substance.setPressure(press);
					double vaporEntropy = substance.getVapor().calculateEntropy();			
					writer.println(" " + vaporEntropy + " " + press);
			}
			writer.println();
		}
		
		System.out.println("Region supercritica");
		double aboveCritical = max_temp * 1.1; 
		
		
		for(double temp= max_temp ; temp <= aboveCritical; temp+= tempPass){
			double pressPas = (max_pressure - min_pressure)/n;
			substance.setTemperature(temp);
			System.out.println(temp);
			for(double press = min_pressure; press <= max_pressure;press+= pressPas ){
				substance.setPressure(press);
				double vaporEntropy = substance.getVapor().calculateEntropy();
				writer.println(" " + vaporEntropy + " " + press);
			}
			writer.println();
				
		}
		
		System.out.println("fin");
		writer.close();
	}
}
