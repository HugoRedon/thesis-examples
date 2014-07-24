package thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import termo.component.Compound;
import termo.data.ExperimentalData;
import termo.data.ExperimentalDataList;
import termo.eos.Cubic;
import termo.eos.EquationsOfState;
import termo.eos.alpha.Alpha;
import termo.eos.alpha.AlphaFactory;
import termo.equations.Eqn101VaporPressure;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.Parameters_Error;
import termo.phase.Phase;

public class DataFilesGenerator {
	
	public void generateCubicChapterFiles() throws FileNotFoundException, UnsupportedEncodingException{
		CubicFileGenerator cubicGen = new CubicFileGenerator();
		cubicGen.cubicEquationPressureVolumeFile("/home/hugo/Documents/repositories/MateriaLatex/plotdata/pressurevolume.dat");
		cubicGen.cubicEquationPressureVolumeTemperatureFile("/home/hugo/Documents/repositories/MateriaLatex/plotdata/pressurevolumetemperature.dat");
		cubicGen.cubicEquationCompresibilitiFactorFiles("/home/hugo/Documents/repositories/MateriaLatex/plotdata/compresibilitiChart/");
	}
	public void generateEnthalpyChapterFiles () throws FileNotFoundException, UnsupportedEncodingException{
		EnthalpyFileGenerator enthalpyGen = new EnthalpyFileGenerator();
		
		enthalpyGen.enthalpyDiagramFile("/home/hugo/Documents/repositories/MateriaLatex/plotdata/enthalpy/");
		enthalpyGen.enthalpyDiagram3dFile("/home/hugo/Documents/repositories/MateriaLatex/plotdata/enthalpy/");
		
	}
	public void generateOptimizationChapterFiles() throws FileNotFoundException, UnsupportedEncodingException{
		OptimizationFileGenerator alphaGen = new OptimizationFileGenerator();
		
		alphaGen.alphaOptimization2variableFile("/home/hugo/Documents/repositories/MateriaLatex/plotdata/alphaOptimization/");
	}
	
	public void generateBinaryOptimizationChapterFiles() throws IOException{
		BinaryOptimizationFileGenerator binaryGen = new BinaryOptimizationFileGenerator();
		
		binaryGen.generateFiles("/home/hugo/Documents/repositories/MateriaLatex/plotdata/binaryOptim/");
	}
	
	public void generateTernaryDiagramsChapterDataFiles() throws IOException{
		TernaryDiagram ternary = new TernaryDiagram();
		
		ternary.generateTernaryDataFiles("/home/hugo/Documents/repositories/MateriaLatex/plotdata/ternaryDiagram/");
	}
	
	
	public static void main(String... args){
		DataFilesGenerator generator = new DataFilesGenerator();
		try {
			
//			generator.generateCubicChapterFiles();
			//generator.generateEnthalpyChapterFiles();
			//generator.generateOptimizationChapterFiles();
			//generator.generateBinaryOptimizationChapterFiles();
			generator.generateTernaryDiagramsChapterDataFiles();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
