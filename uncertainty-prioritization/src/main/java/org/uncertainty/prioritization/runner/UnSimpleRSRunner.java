package org.uncertainty.prioritization.runner;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uncertainty.prioritization.problem.UncertaintyOptimizeProblemOverall;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;
import no.simula.se.testing.minimization.rs.SimpleRSBuilder;

public class UnSimpleRSRunner extends AbUncerTestPriRunner {

	public final String algoName = "SimpleRS";

	public void run(UTestSetResult set, int[] objs, String pName, double tb, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;
		run(set, objs, pName, tb, UnPriRunner2.UNCERTEST_PRI_PROBLEM, referenceParetoFront);
	}

	public void run(UTestSetResult set, int[] objs, String pName,  double tb, String problemName, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;


		Problem<DoubleSolution> problem;
		Algorithm<List<DoubleSolution>> algorithm;

		problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);

		if (problem instanceof UncertaintyOptimizeProblemOverall) {
			((UncertaintyOptimizeProblemOverall) problem).initial(set, tb, objs);
		}


		    algorithm = new SimpleRSBuilder<DoubleSolution>(problem)
		            .setMaxEvaluations(UnPriRunner2.maxIteration)
		            .build() ;

		    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
		            .execute() ;


		List<DoubleSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();

//		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		UnPriRunner2.printDoubleSolution(population, pName, computingTime);
		//printFinalSolutionSet(population);
		if (!referenceParetoFront.equals("")) {
			printQualityIndicators(population, referenceParetoFront);
		}
	}

	@Override
	public String getAlgoName() {
		return algoName;
	}
}
