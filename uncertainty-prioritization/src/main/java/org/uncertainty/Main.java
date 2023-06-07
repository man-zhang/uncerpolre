package org.uncertainty;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import org.uncertainty.prioritization.UncerPrioritization;
import org.uncertainty.prioritization.runner.UnPriRunner2;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;

public class Main {

	private static Gson utilGson = new Gson();

	public static void main(String[] args) throws FileNotFoundException {

		Options options = new Options();
		options.addOption(Option.builder("r").longOpt("repetition").hasArg(true)
				.desc("how many times you want to repeat the experiment (default is 5)")
				.required(false).build());

		options.addOption(Option.builder("f").longOpt("testsfile").hasArg(true)
				.desc("[REQUIRED] where you have tests to prioritize").required(true).build());

		options.addOption(Option.builder("t").longOpt("timeContraint").hasArg(true).desc(
				"what maximum percentage of time budget you want to apply for (default is 1)")
				.required(false).build());

		options.addOption(Option.builder("s").longOpt("strategy").hasArg(true)
				.desc("which strategy you want to employ for priortizing tests (default is 6)")
				.required(false).build());

		options.addOption(Option.builder("a").longOpt("algorithm").hasArg(true)
				.desc("which algorithm you want to employ for priortizing tests (default is SPEA2)")
				.required(false).build());

		options.addOption(Option.builder("d").longOpt("dir").hasArg(true)
				.desc("where you want to save results (default is `uncertainty_prioritization_solutions`)")
				.required(false).build());

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);

			String filePath = cmd.getOptionValue("f");
			Path path = Paths.get(filePath);
			if(!Files.exists(path)) {
				System.err.println("cannot find a file with specified file "+ filePath);
				System.exit(1);
			}
			
			UTestSetResult setR = null;
			
			try {
				setR = utilGson.fromJson(new FileReader(path.toFile()), UTestSetResult.class);
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				System.err.println("a provided file is not valid for test prioritization");
				System.exit(1);
			}
			
			
			int repetition = 5;
			int strategy = 5;
			double tb = 1.0;
			int algoIndex = 2;
			UncerPrioritization eng = null;
			
			
			if(cmd.hasOption("r")) {
				repetition = Integer.parseInt(cmd.getOptionValue("r"));
			}
			
			
			
			if(cmd.hasOption("s")) {
				strategy = Integer.parseInt(cmd.getOptionValue("s")) - 1;
				if(strategy < 0 || strategy >= UncerPrioritization.getNumberOfStrategy())
					throw new IllegalArgumentException("the strategy must be 1..10 representing a valid priortization problem");
			}
			
			
			if(cmd.hasOption("t")) {
				
				String tbOption = cmd.getOptionValue("t");
				boolean predefined = false;
				String[] prefinedTB = UncerPrioritization.getTBs();
				for(int i = 0; i < prefinedTB.length; i++) {
					if(tbOption.equalsIgnoreCase(prefinedTB[i])) {
						tb = UncerPrioritization.TBs[i];
						predefined = true;
					}
				}
				if(!predefined)
					tb = UncerPrioritization.getTBByStr(cmd.getOptionValue("t"));
			}
			
			if(cmd.hasOption("a")) {
				String algo = cmd.getOptionValue("t");
				String[] supportAlgorithms = UncerPrioritization.getAlgos();
				for(int i = 0; i < supportAlgorithms.length; i++) {
					if(algo.equals(supportAlgorithms[i])) {
						algoIndex = i;
					}
				}
			}			
			
			eng = new UncerPrioritization(repetition);
			int[] config = UncerPrioritization.problems_array[strategy]; 

			eng.run("foo", "uc1", setR, config, algoIndex, tb);

			System.out.println("Done, solutions can be found under " + UnPriRunner2.getOutputPath());

		} catch (ParseException pe) {
			System.out.println("Cannot parse options you specified. Please check.");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("options", options);
			System.exit(1);
		}

	}
}