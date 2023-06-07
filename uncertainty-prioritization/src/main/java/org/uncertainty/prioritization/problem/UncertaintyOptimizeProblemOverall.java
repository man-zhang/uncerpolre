package org.uncertainty.prioritization.problem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import no.simula.se.testing.execution.analysis.domain.Overall;
import no.simula.se.testing.execution.analysis.domain.USPInfo;
import no.simula.se.testing.execution.analysis.domain.UTestCaseResult;
import no.simula.se.testing.execution.analysis.domain.UTestSetResult;
import no.simula.se.testing.execution.analysis.domain.UnResult;
import no.simula.se.testing.execution.analysis.domain.UnVerdic;

public class UncertaintyOptimizeProblemOverall extends AbstractDoubleProblem {

	private static final long serialVersionUID = 1920436901054634324L;

	public  static int OBJS;
	private List<UTestCaseResult> tcs;
	private Overall overall;
	private int[] objs;
	private double all_execuTime;
	private double timebudget;
	private int maxU;
	private int minU;
	public double getTimebudget() {
		return timebudget;
	}

	public void setTimebudget(double timebudget) {
		this.timebudget = timebudget;
	}

	public UncertaintyOptimizeProblemOverall(){

	}

	public void initial(UTestSetResult set, double timebudget, int[] configs){
		this.tcs = set.getExecutedUTCs();
		this.objs = configs;
		this.maxU = set.getMaxUofTC();
		this.minU = set.getMinUofTC();
		int i = 0;
		for(int ob: configs){
			if(ob == 1) i++;
		}
		OBJS = i;
		this.overall = set.getOverall();

		this.timebudget = timebudget;
		this.all_execuTime = set.getTotalET();

		setNumberOfVariables(tcs.size());
		setNumberOfObjectives(OBJS);
		setName("UncertaintyOptimizeProblemOverall");

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

		for (int j = 0; j < getNumberOfVariables(); j++) {
			lowerLimit.add(0.0);
			upperLimit.add(1.0);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	public UncertaintyOptimizeProblemOverall(String solutionType, UTestSetResult set, double timebudget, int[] configs){
		this.tcs = set.getExecutedUTCs();
		this.objs = configs;
		this.maxU = set.getMaxUofTC();
		this.minU = set.getMinUofTC();
		int i = 0;
		for(int ob: configs){
			if(ob == 1) i++;
		}
		OBJS = i;
		this.overall = set.getOverall();

		this.timebudget = timebudget;
		this.all_execuTime = set.getTotalET();

		setNumberOfVariables(tcs.size());
		setNumberOfObjectives(OBJS);
		setName("UncertaintyOptimizeProblemOverall");

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

		for (int j = 0; j < getNumberOfVariables(); j++) {
			lowerLimit.add(0.0);
			upperLimit.add(1.0);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	@Override
	public void evaluate(DoubleSolution solution) {

		// random solution for the sequence of test case execution
		DecimalFormat df = new DecimalFormat("#.###");


		double obj0 = 0; // number of observed uncertainty
		double obj1 = 0; // execution time
		double obj2 = 0; // transition coverage

		double obj3 = 0; // um
		double obj4 = 0; // uncertainty space
		double obj5 = 0; // number of uncertainty
		double obj6 = 0; // unique number of uncertainty

		double time_flag = 0;

		Set<String> included_t = new HashSet<String>();//transition coverage
		Set<String> included_us = new HashSet<String>();//uncertainty space coverage
		Set<String> included_unu = new HashSet<String>();//transition coverage

	    Map<Double, HashSet<Integer>> mapPos=new HashMap<Double, HashSet<Integer>>();

	    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			HashSet<Integer> current=null;
	    	if((current=(mapPos.get(solution.getVariableValue(i))))==null){
	    		current=new HashSet<Integer>();
	    		current.add(new Integer(i));
		    	mapPos.put(solution.getVariableValue(i), current);
	    	}else{
	    		current.add(new Integer(i));
	    	}
	    }
	    List<Double> keys=new ArrayList<Double>(mapPos.keySet());
		Collections.sort(keys);
		int indexAll[]=new int[solution.getNumberOfVariables()];
		int currentPos=0;
		for(int i=0;i<keys.size();i++){
			HashSet<Integer> hashset=mapPos.get(keys.get(i));
			for(Integer testNo:hashset){
				indexAll[currentPos]=testNo;
				currentPos++;
			}
		}

		if(currentPos != solution.getNumberOfVariables()) System.err.println(this.getClass().getName()+" inconsistent number of variables!");

		Set<String> addeds = new HashSet<String>();
		int i = 0;
	    for (; i < indexAll.length;i++){
	    	int index =indexAll[i];
	    	if(addeds.contains(String.valueOf(index))){
	    		System.err.println(index);
	    	}
	    	addeds.add(String.valueOf(index));
	    	UTestCaseResult tc = this.tcs.get(i);
	    	time_flag = Double.parseDouble(df.format(time_flag + tc.getExecutionTime()));
	    	if(time_flag/this.all_execuTime > this.timebudget){
	    		break;
	    	}
	    }
	    int record_numberOfObserverd = 0;
	    double record_executTime = 0.0;
	    double record_tranCoverage = 0.0;

	    double record_aveUM = 0.0;
	    double record_usCovergae = 0.0;
	    int record_nu = 0;
	    double record_unCoverage = 0.0;

	    for(int j = 0; j < i; j++){
	    	int index = indexAll[j];
	    	UTestCaseResult tc = this.tcs.get(index);
	    	double p = getPositionValueMax(j+1,i);

	    	// number of observed uncertainty
	    	if(this.objs[0] == 1){
	    		int numOfOU = 0;
	    		for(UnResult ur : tc.getUnExecutedSeq()){
	    			if(ur.getVerdic().equals(UnVerdic.KnOccurred_UkInS)
	    					|| ur.getVerdic().equals(UnVerdic.KnNotOccurred_With_InS)
	    					|| ur.getVerdic().equals(UnVerdic.KnOccurred_Without_InS) ){
	    				numOfOU++;
	    			}
	    		}
	    		obj0 = obj0 + (1.0 - (numOfOU - this.minU)/ ((this.maxU - this.minU) * 1.0)) * p;
	    		record_numberOfObserverd = record_numberOfObserverd + numOfOU;
		    }
		    //execution time
		    if(this.objs[1] == 1){
		    	obj1 = obj1 + tc.getExecutionTime()/this.all_execuTime * p;
		    	record_executTime = record_executTime + tc.getExecutionTime();
		    }
		    // transition coverage
		    if(this.objs[2] == 1){
		    	int current = included_t.size();
		    	included_t.addAll(tc.getTransitions());
		    	int added = included_t.size();
		    	obj2 = obj2 - ((double)(added - current)/this.overall.getTransitions().size()) * p;

		    }
		    // uncertainty measure
		    if(this.objs[3] == 1){
		    	obj3 = obj3 + (1-tc.getUmTC()) * p;
		    	record_aveUM = record_aveUM + tc.getUmTC();
		    }

		    if(this.objs[4] == 1){
		    	int current_us = included_us.size();
		    	for(String unR : tc.getUnSpecified()){
		    		for(USPInfo usp : this.overall.getAllUSPs()){
			    		if(usp.getUnCovered().contains(unR)){
			    			included_us.add(usp.getUspId());
			    		}
			    	}
		    	}
		    	int added_us = included_us.size();
		    	obj4 = obj4 - ((double)(added_us - current_us)/this.overall.getAllUSPs().size()) * p;

		    }

		    // number of uncertainty
		    if(this.objs[5] == 1){
		    	obj5 = obj5 + (1.0/(tc.getUnSpecified().size() + 1.0 )) * p;
		    	record_nu = record_nu + tc.getUnSpecified().size();
		    }

		    // unique number of uncertainty
		    if(this.objs[6] == 1){
		    	int current_unu = included_unu.size();
		    	included_unu.addAll(tc.getUnSpecified());
		    	int added_unu = included_unu.size();
		    	obj6 = obj6 - ((double)(added_unu - current_unu)/this.overall.getAlluncertainites().size()) * p;
		    }
	    }

	    ((UnOrderDoubleSolution)solution).setComment("@");
	    int obj_index = 0;
	    if(this.objs[0] == 1){
	    	solution.setObjective(obj_index, (obj0/i));
	    	obj_index++;
	    	 ((UnOrderDoubleSolution)solution).appendComment("numOfObUn:"+record_numberOfObserverd+" ");
	    }

	    if(this.objs[1] == 1){
	    	solution.setObjective(obj_index, obj1);
	    	 ((UnOrderDoubleSolution)solution).appendComment("totalExe:"+df.format(record_executTime)+" ");
	    	obj_index++;
	    }

	    if(this.objs[2] == 1){
	    	solution.setObjective(obj_index, obj2 + 1.0);
	    	record_tranCoverage = (1.0*included_t.size())/this.overall.getTransitions().size();
	    	 ((UnOrderDoubleSolution)solution).appendComment("transCover:"+df.format(record_tranCoverage)+" ");
	    	obj_index++;
	    }

	    if(this.objs[3] == 1){
	    	solution.setObjective(obj_index, (obj3/i));
	    	record_aveUM = (1.0 * record_aveUM)/i;
	    	 ((UnOrderDoubleSolution)solution).appendComment("averUM:"+df.format(record_aveUM)+" ");
	    	obj_index++;
	    }

	    if(this.objs[4] == 1){
	    	solution.setObjective(obj_index, obj4 + 1.0);
	    	record_usCovergae = (1.0*included_us.size())/this.overall.getAllUSPs().size();
	    	 ((UnOrderDoubleSolution)solution).appendComment("usCover:"+df.format(record_usCovergae)+" ");
	    	obj_index++;
	    }
	    if(this.objs[5] == 1){
	    	solution.setObjective(obj_index, (obj5/i));
	    	 ((UnOrderDoubleSolution)solution).appendComment("numOfSpUn:"+record_nu+" ");
	    	obj_index++;
	    }
	    if(this.objs[6] == 1){
	    	solution.setObjective(obj_index, obj6 + 1.0);
	    	record_unCoverage = (1.0*included_unu.size())/this.overall.getAlluncertainites().size();
	    	 ((UnOrderDoubleSolution)solution).appendComment("ubCover:"+df.format(record_unCoverage)+" ");
	    }

	    //set additional information
	    ((UnOrderDoubleSolution)solution).setValidLength(i);
	    ((UnOrderDoubleSolution)solution).setSort(true);
	    ((UnOrderDoubleSolution)solution).setSortposition(indexAll);

	    double ofv=0;
        for( i=0;i<solution.getNumberOfObjectives();i++)
        	ofv+=solution.getObjective(i);
        ((UnOrderDoubleSolution)solution).setOfv(ofv/solution.getNumberOfObjectives());
	}

	public double getPostionValue(int i){
		return i/(this.getNumberOfVariables() * 1.0);
	}

	public double getPositionValueMax(int i){
		return ((double) this.getNumberOfVariables() - i +1.0)/this.getNumberOfVariables();
	}
	public double getPositionValueMax(int i, int n){
		return ((double) n - i +1.0)/n;
	}
	public double getPositionValueMin(int i, int n){
		return ((double) i)/n;
	}

	@Override
	public DoubleSolution createSolution() {
		return new UnOrderDoubleSolution(this);
	}

	public List<UTestCaseResult> getTcs() {
		return tcs;
	}

	public void setTcs(List<UTestCaseResult> tcs) {
		this.tcs = tcs;
	}

	public Overall getOverall() {
		return overall;
	}

	public void setOverall(Overall overall) {
		this.overall = overall;
	}

	public int[] getObjs() {
		return objs;
	}

	public void setObjs(int[] objs) {
		this.objs = objs;
	}

	public double getAll_execuTime() {
		return all_execuTime;
	}

	public void setAll_execuTime(double all_execuTime) {
		this.all_execuTime = all_execuTime;
	}

	public int getMaxU() {
		return maxU;
	}

	public void setMaxU(int maxU) {
		this.maxU = maxU;
	}

	public int getMinU() {
		return minU;
	}

	public void setMinU(int minU) {
		this.minU = minU;
	}
}
