package thesis;

import java.io.IOException;
import java.io.PrintWriter;

import termo.component.Compound;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.Alphas;
import termo.matter.HeterogeneousSubstance;
import termo.matter.Mixture;
import termo.matter.Substance;

import compounds.CompoundReader;

public class FugacityFileGenerator extends FileGenerator{
		HeterogeneousSubstance substance;
		public void prepareSub(){
			CompoundReader reader = new CompoundReader();	
			Compound water = reader.getCompoundByExactName("Water");
			fillExperimentalListWithEqn101(water);
			Cubic eos = EquationsOfState.pengRobinson();
			Alpha alpha = Alphas.getStryjekAndVeraExpression();
			substance = new HeterogeneousSubstance(eos,alpha,water);
			substance.getErrorFunction().setExperimental(water.getExperimentalLists().iterator().next().getList());
			substance.getErrorFunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
			substance.getErrorFunction().minimize();
			
		}
	
		public void generateFugacityDiagram(String folderName)throws IOException{
			prepareFolder(folderName);
			prepareSub();
			PrintWriter writer = new PrintWriter(folderName + "fug.dat");
			writer.println(" liquidfug vaporfug temperature");
			appendLinesForPressure(writer, 101325d);
			writer.close();
		}
		public void generateFugacity3dDiagram(String folderName)throws IOException{
			prepareFolder(folderName);
			prepareSub();
			PrintWriter writer = new PrintWriter(folderName + "fug3d.dat");
			writer.println(" liquidfug vaporfug temperature pressure");
			Double minPressure = 101325d;
			Double maxPressure = 2760908.0625;//0.95 *substance.getComponent().getCriticalPressure();
			
			Integer n = 20;
			Double pressureStep = (maxPressure-minPressure)/n.doubleValue();
			
			for(Integer i = 0; i < n ; i++){
				Double pressure = minPressure + i.doubleValue()* pressureStep;
				appendLinesForPressure(writer, pressure);
				writer.println();
			}
			writer.close();
			
			Integer nl= 25;
			pressureStep = (maxPressure-minPressure)/nl.doubleValue();
			PrintWriter wri = new PrintWriter(folderName + "linefug3d.dat");
			wri.println(" fug temperature pressure");
			for(Integer i = 0; i < nl ; i++){
				Double pressure = minPressure + i.doubleValue()* pressureStep;
				substance.setPressure(pressure);
				substance.bubbleTemperature();
				Double temp = substance.getTemperature();
				Double fugacity = substance.getLiquid().calculateFugacity();
				wri.println(" " + fugacity + " " + temp + " " + pressure);
			}
			wri.close();
		}
		
		public void appendLinesForPressure(PrintWriter writer,Double pressure){
			substance.setPressure(pressure);
			
			substance.bubbleTemperature();
			Double temp = substance.getTemperature();
			
			Double maxTemp = temp * 1.1;
			Double minTemp = temp * 0.7;
			
			Integer n = 20;
			Double tempStep = (maxTemp -minTemp)/n.doubleValue();
			for(Integer i =0 ; i < n ; i++){
				Double temperature = minTemp + i* tempStep;
				substance.setTemperature(temperature);
				Double liquidFug = substance.getLiquid().calculateFugacity();
				Double vaporFug = substance.getVapor().calculateFugacity();
				writer.println(" " + liquidFug + " " + vaporFug + " " + temperature + " " + pressure);
			}
			
		}		
}
