package org.uncertainty.prioritization;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.uncertainty.prioritization.runner.AbUncerTestPriRunner;
import org.uncertainty.prioritization.runner.UnCellDERunner;
import org.uncertainty.prioritization.runner.UnMOCellRunner;
import org.uncertainty.prioritization.runner.UnNSGAIIRunner;
import org.uncertainty.prioritization.runner.UnPriRunner2;
import org.uncertainty.prioritization.runner.UnSPEA2Runner;
import org.uncertainty.prioritization.runner.UnSimpleRSRunner;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;


public class UncerPrioritization {

	/**
	 * specify objectives names
	 */
	public final static String[] OBJS_LIST = { "NUO", "ET", "CTR", "UM", "USP", "NU", "NUU" };

	/**
	 * configure time budgets
	 */
	public final static double[] TBs = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

	/**
	 * configure the problems based on which objectives {@link UncerPrioritization#OBJS_LIST} are involved, i.e., 1
	 */
	public final static int[][] problems_array = {
			
			{0,1,1,1,0,0,0}, // f(PET, PTR, AUM)
			{0,1,1,0,1,0,0}, // f(PET, PTR, PUS)
			{0,1,1,0,0,1,0}, // f(PET, PTR, ANU)
			{0,1,1,0,0,0,1}, // f(PET, PTR, PUU)

			{0,1,1,1,1,0,0}, // f(PET, PTR, AUM, PUS)
			{0,1,1,1,0,1,0}, // f(PET, PTR, AUM, ANU)
			{0,1,1,1,0,0,1}, // f(PET, PTR, AUM, PUU)
			{0,1,1,0,1,1,0}, // f(PET, PTR, PUS, ANU)
			{0,1,1,0,1,0,1}, // f(PET, PTR, PUS, PUU)
			{0,1,1,0,0,1,1}  // f(PET, PTR, ANU, PUU)

	};

	public int RUNs = 100;

	public UncerPrioritization(){
	}

	public UncerPrioritization(int runs){
		this.RUNs = runs;
	}

	/**
	 * configure different algorithms here
	 */
	public static AbUncerTestPriRunner[] runners = {
			new UnNSGAIIRunner(),
			new UnMOCellRunner(),
			new UnSPEA2Runner(),
			new UnCellDERunner(),
			new UnSimpleRSRunner()
	};

	
	public final static String Sep = "_";
	
	private final static String TB_PATTERN = "^TB\\\\d{1,3}$";

	public String generatepName(int[] objs) {
		String name = "";
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] == 1){
				if(name.equals("")){
					name = name + OBJS_LIST[i];
				}else{
					name = name + Sep + OBJS_LIST[i];
				}

			}
		}
		return name;
	}

	public void run(String caseStudy, String uc, UTestSetResult set, int[] objs) throws FileNotFoundException{

		UnPriRunner2.CASE  = caseStudy;
		UnPriRunner2.USE_CASE = uc;

		for(double tb : TBs){
			UnPriRunner2.TIME_BUDGET = getTBStr(tb);
			for(AbUncerTestPriRunner runner : runners){
				for(int time = 1; time <= RUNs ; time++){
					UnPriRunner2.times = time;
					runner.run(set, objs, generatepName(objs), tb,"");
				}
			}
		}
	}

	public void run(String caseStudy, String uc, UTestSetResult set, int[] objs, double tb) throws FileNotFoundException{

		UnPriRunner2.CASE  = caseStudy;
		UnPriRunner2.USE_CASE = uc;

		UnPriRunner2.TIME_BUDGET = getTBStr(tb);
		for(AbUncerTestPriRunner runner : runners){
			for(int time = 1; time <= RUNs ; time++){
				UnPriRunner2.times = time;
				runner.run(set, objs, generatepName(objs), tb,"");
			}
		}

	}

	public void run(String caseStudy, String uc, UTestSetResult set, int[] objs, int algo, int tbIndex) throws FileNotFoundException{

		double tb = TBs[tbIndex];
		run(caseStudy, uc, set, objs, algo, tb);

	}
	
	public void run(String caseStudy, String uc, UTestSetResult set, int[] objs, int algo, double tb) throws FileNotFoundException{

		UnPriRunner2.CASE  = caseStudy;
		UnPriRunner2.USE_CASE = uc;

		UnPriRunner2.TIME_BUDGET = getTBStr(tb);

		for(int time = 1; time <= RUNs ; time++){
			UnPriRunner2.times = time;
			runners[algo].run(set, objs, generatepName(objs), tb,"");
		}

	}

	public static double getTBByStr(String tbStr) {
		boolean matched = tbStr.toUpperCase().matches(TB_PATTERN);
		if(!matched) {
			throw new IllegalArgumentException("The specified timebudgetOption is not valid");
		}
		
		double tb = (Integer.parseInt(tbStr.toUpperCase().replaceAll("TB", "")))/ 100.0;
		if(tb > 1.0 || tb <= 0)
			throw new IllegalArgumentException("the time constraint must be (0.0, 1.0].");
		
		return tb;
	}
	
	
	
	public static String getTBStr(double tb){
		return String.format("TB%s", String.format("%03d", (int)(tb*100)));
	}

	public String getRunStr(int run){
		return String.format("%03d", run);
	}

	public static String[] getAlgos(){
		String[] algos= new String[runners.length];
		int i = 0;
		for(AbUncerTestPriRunner runner : runners){
			algos[i] = runner.getAlgoName();
			i++;
		}
		return algos;

	}

	public static String[] getTBs(){
		String[] algos= new String[TBs.length];
		int i = 0;
		for(double tb : TBs){
			algos[i] = getTBStr(tb);
			i++;
		}
		return algos;

	}
	
	public final static int getNumberOfStrategy() {
		return problems_array.length;
	}
}
