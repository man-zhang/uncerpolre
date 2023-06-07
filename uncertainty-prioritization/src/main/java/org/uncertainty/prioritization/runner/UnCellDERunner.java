package org.uncertainty.prioritization.runner;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.cellde.CellDE45;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uncertainty.prioritization.problem.UncertaintyOptimizeProblemOverall;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;

public class UnCellDERunner extends AbUncerTestPriRunner {

	public final String algoName = "CellDE";

	public void run(UTestSetResult set, int[] objs, String pName, double tb, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;
		run(set, objs, pName, tb, UnPriRunner2.UNCERTEST_PRI_PROBLEM, referenceParetoFront);
	}

	public void run(UTestSetResult set, int[] objs, String pName,  double tb, String problemName, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;

		Problem<DoubleSolution> problem;
		Algorithm<List<DoubleSolution>> algorithm;
		SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
		DifferentialEvolutionCrossover crossover;

		problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);

		if (problem instanceof UncertaintyOptimizeProblemOverall) {
			((UncertaintyOptimizeProblemOverall) problem).initial(set, tb, objs);
		}

		double cr = 0.5;
		double f = 0.5;

		crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin");

		selection = new BinaryTournamentSelection<DoubleSolution>(
				new RankingAndCrowdingDistanceComparator<DoubleSolution>());

		algorithm = new CellDE45(problem, UnPriRunner2.maxIteration, UnPriRunner2.population, new CrowdingDistanceArchive<DoubleSolution>(UnPriRunner2.population),
				new C9<DoubleSolution>((int) Math.sqrt(UnPriRunner2.population), (int) Math.sqrt(UnPriRunner2.population)), selection, crossover, 20,
				new SequentialSolutionListEvaluator<DoubleSolution>());

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		List<DoubleSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();

//		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		UnPriRunner2.printDoubleSolution(population, pName, computingTime);
		// printFinalSolutionSet(population);
		if (!referenceParetoFront.equals("")) {
			printQualityIndicators(population, referenceParetoFront);
		}
	}
	@Override
	public String getAlgoName() {
		return algoName;
	}
}
