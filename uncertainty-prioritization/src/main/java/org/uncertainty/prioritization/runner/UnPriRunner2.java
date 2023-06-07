package org.uncertainty.prioritization.runner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uncertainty.prioritization.problem.UnPSolutionListOutput;

public class UnPriRunner2 {
	public final static String UNCERTEST_PRI_PROBLEM = "org.uncertainty.prioritization.problem.UncertaintyOptimizeProblemOverall";
	
	private static String output_path = "uncertainty_prioritization_solutions";

	public static String CASE = "";
	public static String USE_CASE = "";
	public static String ALGO = "";
	public static String TIME_BUDGET = "";

	public static int times;

	public static int maxIteration = 25000;
	public static int population = 100;

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void setOutputPath(String path) {
		output_path = path;
	}
	
	public static String getOutputPath() {
		return output_path;
	}
	
	public static void printDoubleSolution(List<DoubleSolution> population, String pName, long computingTime){

		String path = output_path + "/";
		File directory = new File(path);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		if(!CASE.equals("")){
			path = path + CASE +"_";
		}
		if(!USE_CASE.equals("")){
			path = path + USE_CASE +"_";
		}

		if(!TIME_BUDGET.equals("")){
			path = path + TIME_BUDGET +"_";
		}

		if(!ALGO.equals("")){
			path = path + ALGO +"_";
		}


		String var = path + pName+"_"+formatNum(times)+"_VAR.tsv";
		String fun = path + pName+"_"+formatNum(times)+"_FUN.tsv";
		String time = path + pName+"_TIME.tsv";


		DefaultFileOutputContext varContext = new DefaultFileOutputContext(var);
		varContext.setSeparator(" ");
		new UnPSolutionListOutput(population)
		.setVarFileOutputContext(varContext)
        .setFunFileOutputContext(new DefaultFileOutputContext(fun))
        .print();

		printRunTime(time,computingTime); 
		varContext=null;
	}

	public static void printDoubleSolution(List<DoubleSolution> population, String pName){

		String path = output_path + "/";
		if(!CASE.equals("")){
			path = path + CASE +"/";
		}
		if(!USE_CASE.equals("")){
			path = path + USE_CASE +"/";
		}
		if(!TIME_BUDGET.equals("")){
			path = path + TIME_BUDGET +"/";
		}
		if(!ALGO.equals("")){
			path = path + ALGO +"/";
		}

		File directory = new File(path);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }

		String var = path + pName+"_"+formatNum(times)+"_VAR.tsv";
		String fun = path + pName+"_"+formatNum(times)+"_FUN.tsv";


		DefaultFileOutputContext varContext = new DefaultFileOutputContext(var);
		varContext.setSeparator("");
		new UnPSolutionListOutput(population)
        .setSeparator("\t")
        .setVarFileOutputContext(varContext)
        .setFunFileOutputContext(new DefaultFileOutputContext(fun))
        .print();


	}

	public static void printRunTime(String file, long time) {
		 BufferedWriter writer=null;

			try {
				writer=  new BufferedWriter(new FileWriter(new File(file),true));
			    writer.write(time+"\n");

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(writer!=null)
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	}

	public static String formatNum(int number){
		return String.format("%03d", number);
	}
}
