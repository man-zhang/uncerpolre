package org.uncertainty.prioritization.problem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;

public class UnPSolutionListOutput extends SolutionListOutput {

	public UnPSolutionListOutput(List<? extends Solution<?>> solutionList) {
		super(solutionList);
	}

	public void printVariablesToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
		BufferedWriter bufferedWriter = context.getFileWriter();

		try {
			if (solutionList.size() > 0) {
				int numberOfVariables = solutionList.get(0).getNumberOfVariables();
				for (int i = 0; i < solutionList.size(); i++) {
					if(solutionList.get(i) instanceof UnOrderDoubleSolution){
						UnOrderDoubleSolution cur = (UnOrderDoubleSolution)solutionList.get(i) ;

						if(!cur.isSort()){
							for (int j = 0; j < numberOfVariables; j++) {
								bufferedWriter.write(solutionList.get(i).getVariableValueString(j) + context.getSeparator());
							}
						}else if(cur.isSort() && cur.getSortposition() != null){
							for(int j = 0; j < cur.getValidLength(); j++){
								bufferedWriter.write(cur.getSortposition()[j] + context.getSeparator());
							}

						}else{
							System.err.println("Print Error:"+ this);
						}

						bufferedWriter.write("[" + cur.getValidLength() + "]" + context.getSeparator());
						bufferedWriter.write(cur.getComment() + context.getSeparator());
					}else{
						for (int j = 0; j < numberOfVariables; j++) {
							bufferedWriter.write(solutionList.get(i).getVariableValueString(j) + context.getSeparator());
						}
					}
					bufferedWriter.newLine();

				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			throw new JMetalException("Error writing data ", e);
		}

	}

	public void printObjectivesToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
		BufferedWriter bufferedWriter = context.getFileWriter();

		try {
			if (solutionList.size() > 0) {
				int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
				for (int i = 0; i < solutionList.size(); i++) {
					for (int j = 0; j < numberOfObjectives; j++) {
						bufferedWriter.write(solutionList.get(i).getObjective(j) + context.getSeparator());
					}
					if(solutionList.get(i) instanceof UnOrderDoubleSolution){
						if(((UnOrderDoubleSolution)solutionList.get(i)).getOfv() != -1.0){
							bufferedWriter.write(((UnOrderDoubleSolution)solutionList.get(i)).getOfv() + context.getSeparator());
						}
					}

					bufferedWriter.newLine();
				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			throw new JMetalException("Error printing objecives to file: ", e);
		}
	}

}
