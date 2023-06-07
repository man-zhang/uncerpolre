package org.uncertainty.prioritization.problem;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

public class UnOrderDoubleSolution extends DefaultDoubleSolution {

	private static final long serialVersionUID = -2488501809693855502L;
	int validLength;
	private String comment;
	private int[] sortposition;//this attribute is not necessary
	private boolean isSort;
	private double ofv=-1;

	public UnOrderDoubleSolution(UnOrderDoubleSolution solution) {
		super(solution);
	}

	public UnOrderDoubleSolution(DoubleProblem problem) {
		super(problem);
	}

	@Override
	public UnOrderDoubleSolution copy() {
		return new UnOrderDoubleSolution(this);
	}

	public int getValidLength() {
		return validLength;
	}

	public void setValidLength(int validLength) {
		this.validLength = validLength;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void appendComment(String str){
		this.comment = this.comment + str;
	}

	public boolean isSort() {
		return isSort;
	}

	public void setSort(boolean isSort) {
		this.isSort = isSort;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int[] getSortposition() {
		return sortposition;
	}

	public void setSortposition(int[] sortposition) {
		this.sortposition = sortposition;
	}

	public double getOfv() {
		return ofv;
	}

	public void setOfv(double ofv) {
		this.ofv = ofv;
	}
}
