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
import termo.eos.alpha.Alphas;
import termo.equations.Eqn101VaporPressure;
import termo.matter.HeterogeneousSubstance;
import termo.optimization.ErrorData;
import termo.optimization.Parameters_Error;
import termo.phase.Phase;

public class DataFilesGenerator {
	public static String repoPath = "/home/hugo/Documents/repositories/MateriaLatex/";
	
	public void generateCubicChapterFiles() throws FileNotFoundException, UnsupportedEncodingException{
		CubicFileGenerator cubicGen = new CubicFileGenerator();
		//cubicGen.cubicEquationPressureVolumeFile(repoPath + "plotdata/pressurevolume.dat");
		//cubicGen.cubicEquationPressureVolumeTemperatureFile(repoPath +"plotdata/pressurevolumetemperature.dat");
		cubicGen.cubicEquationCompresibilitiFactorFiles(repoPath + "plotdata/compresibilitiChart/");
	}
	public void generateEnthalpyChapterFiles () throws FileNotFoundException, UnsupportedEncodingException{
		EnthalpyFileGenerator enthalpyGen = new EnthalpyFileGenerator();
		
		enthalpyGen.enthalpyDiagramFile(repoPath + "plotdata/enthalpy/");
		enthalpyGen.enthalpyDiagram3dFile(repoPath + "plotdata/enthalpy/");
		
	}
	public void generateOptimizationChapterFiles() throws FileNotFoundException, UnsupportedEncodingException{
		OptimizationFileGenerator alphaGen = new OptimizationFileGenerator();
		
		alphaGen.alphaOptimization2variableFile(repoPath + "plotdata/alphaOptimization/");
	}
	
	public void generateBinaryOptimizationChapterFiles() throws IOException{
		BinaryOptimizationFileGenerator binaryGen = new BinaryOptimizationFileGenerator();
		
		binaryGen.generateFiles(repoPath + "plotdata/binaryOptim/");
	}
	
	public void generateTernaryDiagramsChapterDataFiles() throws IOException{
		TernaryDiagram ternary = new TernaryDiagram();
		
		ternary.generateTernaryDataFiles(repoPath + "plotdata/ternaryDiagram/");
	}
	
	public void generateEntropyDiagrams()throws IOException{
		EntropyFileGenerator entropy = new EntropyFileGenerator();
		entropy.entropyDiagramFile(repoPath + "plotdata/entropy/");
		entropy.entropyDiagram3dFile(repoPath + "plotdata/entropy/");
		
	}
	public void generateGibbsDiagrams()throws IOException{
		GibbsFileGenerator gibbs = new GibbsFileGenerator();
		gibbs.gibbsDiagramFile(repoPath + "plotdata/gibbs/");
		gibbs.gibbsDiagram3dFile(repoPath + "plotdata/gibbs/");
	}
	public void generateHeterogeneousFiles()throws IOException{
		HeterogeneousFilesGenerator het = new HeterogeneousFilesGenerator();
		
		//het.generate3dDiagram(repoPath + "plotdata/heterogeneous/");
		het.generateBinaryEquilibriaFile(repoPath + "plotdata/mixhet/");
	}
	
	public static void main(String... args){
		DataFilesGenerator generator = new DataFilesGenerator();
		try {
			
			//generator.generateCubicChapterFiles();
			//generator.generateEnthalpyChapterFiles();
			//generator.generateOptimizationChapterFiles();
			//generator.generateBinaryOptimizationChapterFiles();
//			generator.generateTernaryDiagramsChapterDataFiles();
			//generator.generateEntropyDiagrams();
			//generator.generateGibbsDiagrams();
			generator.generateHeterogeneousFiles();
			
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
