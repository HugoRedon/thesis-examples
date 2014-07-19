package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import termo.data.ExperimentalData;
import termo.data.ExperimentalDataList;
import termo.eos.alpha.AlphaFactory;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.Parameters_Error;

public class OptimizationFileGenerator extends FileGenerator{
	public void alphaOptimization2variableFile(String folderName) throws FileNotFoundException, UnsupportedEncodingException{
		File directory = new File(folderName);
		if(!directory.exists()){
			directory.mkdir();
		}
		PrintWriter writer = new PrintWriter(folderName + "error.dat","UTF-8");
		writer.println(" temperature exppressure calcpressure error");
		HeterogeneousSubstance substance = prepareSubstance();
		substance.setAlpha(AlphaFactory.getSoave2Parameters());
		Set<ExperimentalDataList> set =(Set<ExperimentalDataList>)substance.getComponent().getExperimentalLists();
		List<ExperimentalData> list = ((ExperimentalDataList)set.iterator().next()).getList();
		substance.getErrorFunction().setExperimental(list);
				
		
		List<ErrorData> errors =substance.getErrorFunction().getErrorForEachExperimentalData();
		for(ErrorData error:errors){
			double temperature =error.getTemperature();
			double experimentalPressure = error.getExperimentalPressure();
			double calculatedPressur = error.getCalculatedPressure();
			double err = error.getError();
			writer.println(" " + temperature + " " + experimentalPressure + " " + calculatedPressur + " " + err);
		}
		writer.close();
		substance.getErrorFunction().getOptimizer().setApplyErrorDecreaseTechnique(true);
		//substance.getErrorFunction().getOptimizer().setErrorDiferenceCriterion(false);
		//substance.getErrorFunction().getOptimizer().setGradientCriterion(true);
		
		substance.getErrorFunction().minimize();
		
		PrintWriter mWriter = new PrintWriter(folderName + "minError.dat","UTF-8");
		mWriter.println(" temperature exppressure calcpressure error");
		List<ErrorData> minErrors = substance.getErrorFunction().getErrorForEachExperimentalData();
		for(ErrorData error: minErrors){
			double temperature =error.getTemperature();
			double experimentalPressure = error.getExperimentalPressure();
			double calculatedPressur = error.getCalculatedPressure();
			double err = error.getError();
			mWriter.println(" " + temperature + " " + experimentalPressure + " " + calculatedPressur + " " + err);
		}
		mWriter.close();
		
		
		
		
		
		
		double a = substance.getErrorFunction().getParameter(0);
		double b = substance.getErrorFunction().getParameter(1);
		
		System.out.println("a: "+ a);
		System.out.println("b: "+ b);
		
		double square = 0.6;
		
		double min_a=0; //a- Math.abs(a*square);
		double max_a =2; //a + Math.abs(a*square);
		
		System.out.println("min_a: "+ min_a);
		System.out.println("max_a: "+ max_a);
		
		
		double min_b=-0.6;// b- Math.abs(b*square);
		double max_b = b + Math.abs(b*square);
		
		double n  =20;
		double aPass = (max_a - min_a)/n;
		double bPass= (max_b -min_b)/n;
		
		PrintWriter opwriter = new PrintWriter(folderName + "params.dat", "UTF-8");
		
		for(int i = 0 ;i <n; i++){
			double actual_a = min_a + Double.valueOf(i)*aPass;
			
			for(int j = 0; j <n ; j++){
				double actual_b = min_b + Double.valueOf(j)*bPass;
				
				substance.getErrorFunction().setParameter(actual_a, 0);
				substance.getErrorFunction().setParameter(actual_b, 1);
				
				double error =substance.getErrorFunction().error();
				error =(error > 614983.3952123278 || (actual_a <1.5 && actual_b <-0.5))?615000:error;
				
				
				opwriter.println(" " +actual_a + " "+actual_b+ " " +error );
			}
			opwriter.println();
		}
		opwriter.close();
		
		
		
		PrintWriter twriter= new PrintWriter(folderName + "trayectory.dat", "UTF-8");
		twriter.println(" iteration a b error");;
		for(Parameters_Error pe:substance.getErrorFunction().getOptimizer().getConvergenceHistory() ){
			double error = pe.getError();
			double ta = pe.getParameters()[0];
			double tb = pe.getParameters()[1];
			//if(ta > 2.3 && error <200){
				twriter.println(" "+ pe.getIteration()+" " +ta + " " + tb + " " +error);
			//}
		}
		twriter.close();		
		
//		PrintWriter t2writer = new PrintWriter(folderName+ "trayectory2dimension.dat", "UTF-8");
//		t2writer.println(" iteration a b error");
//		for(Parameters_Error pe:substance.getErrorFunction().getOptimizer().getConvergenceHistory() ){
//			double error = pe.getError();
//			double ta = pe.getParameters()[0];
//			double tb = pe.getParameters()[1];
//			
//			t2writer.println(" "+ pe.getIteration() + " " +ta + " " + tb + " " +error);
//		
//		}
//		t2writer.close();
	}
	
	
	
	public void alphaOptimization(String fileName){
		
		
	}
	
	
}
