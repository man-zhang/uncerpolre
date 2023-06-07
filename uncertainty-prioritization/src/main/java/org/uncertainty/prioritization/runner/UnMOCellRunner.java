package org.uncertainty.prioritization.runner;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uncertainty.prioritization.problem.UncertaintyOptimizeProblemOverall;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;

public class UnMOCellRunner extends AbUncerTestPriRunner {

	public final String algoName = "MoCell";

	public void run(UTestSetResult set, int[] objs, String pName, double tb, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;
		run(set, objs, pName, tb, UnPriRunner2.UNCERTEST_PRI_PROBLEM, referenceParetoFront);
	}

	public void run(UTestSetResult set, int[] objs, String pName,  double tb, String problemName, String referenceParetoFront) throws FileNotFoundException{
		UnPriRunner2.ALGO = algoName;


		Problem<DoubleSolution> problem;
		Algorithm<List<DoubleSolution>> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

		problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);

		if (problem instanceof UncertaintyOptimizeProblemOverall) {
			((UncertaintyOptimizeProblemOverall) problem).initial(set, tb, objs);
		}

		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 20.0;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

		selection = new BinaryTournamentSelection<DoubleSolution>(
				new RankingAndCrowdingDistanceComparator<DoubleSolution>());

		algorithm = new MOCellBuilder<DoubleSolution>(problem, crossover, mutation).setSelectionOperator(selection)
				.setMaxEvaluations(UnPriRunner2.maxIteration).setPopulationSize(UnPriRunner2.population)
				.setArchive(new CrowdingDistanceArchive<DoubleSolution>(UnPriRunner2.population)).build();

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
