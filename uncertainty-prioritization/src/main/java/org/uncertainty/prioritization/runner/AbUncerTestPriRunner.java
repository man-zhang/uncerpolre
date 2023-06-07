package org.uncertainty.prioritization.runner;

import java.io.FileNotFoundException;

import org.uma.jmetal.runner.AbstractAlgorithmRunner;

import no.simula.se.testing.execution.analysis.domain.UTestSetResult;

public abstract class AbUncerTestPriRunner extends AbstractAlgorithmRunner {


	public abstract void run(UTestSetResult set, int[] objs, String pName, double tb, String referenceParetoFront) throws FileNotFoundException ;

	public abstract void run(UTestSetResult set, int[] objs, String pName, double tb,  String problemName, String referenceParetoFront) throws FileNotFoundException;

	public abstract String getAlgoName();
}
