package org.aksw.simba.lemming.metrics.single;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.single.edgemanipulation.VertexDegrees;
import org.aksw.simba.lemming.mimicgraph.constraints.TripleBaseSingleID;
import grph.Grph.DIRECTION;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class StdDevVertexDegree extends AvgVertexDegreeMetric {

	protected DIRECTION direction;

	public StdDevVertexDegree(DIRECTION direction) {
		super(direction == DIRECTION.in ? "stdDevInDegree" : "stdDevOutDegree");
		this.direction = direction;
	}

	@Override
	public double apply(ColouredGraph graph) {
		IntArrayList degrees = null;
		if (direction == DIRECTION.in) {
			degrees = graph.getGraph().getAllInEdgeDegrees();
		} else {
			degrees = graph.getGraph().getAllOutEdgeDegrees();
		}
		return calculateStdDev(degrees, calculateAvg(degrees));
	}

	protected double calculateStdDev(IntArrayList degrees, double avg) {
		double temp, sum = 0;
		for (int i = 0; i < degrees.size(); ++i) {
			temp = avg - degrees.getInt(i);
			temp *= temp;
			sum += temp;
		}
		return Math.sqrt(sum / degrees.size());
	}

	/**
	 * The method calculates the StdDev of an array of updated degrees.
	 * 
	 * @param triple         - edge on which graph operation is performed.
	 * @param graph          - input graph.
	 * @param graphOperation - boolean value indicating graph operation. ("true" for
	 *                       adding an edge and "false" for removing an edge)
	 * @param previousResult - UpdatableMetricResult object containing the previous
	 *                       computed results.
	 * @return SimpleMetricResult object.
	 */
	@Override
	public SimpleMetricResult update(TripleBaseSingleID triple, ColouredGraph graph, boolean graphOperation,
			UpdatableMetricResult previousResult, VertexDegrees mVertexDegrees) {
		SimpleMetricResult newMetricResult = (SimpleMetricResult) previousResult;
		int[] degreesArray;
		IntArrayList degreesList;
		switch (getName()) {
		case "stdDevInDegree":
			degreesArray = mVertexDegrees.getmMapVerticesinDegree();
			degreesList = new IntArrayList(degreesArray);// Arrays.asList(ArrayUtils.toObject(degreesArray));
			newMetricResult.setResult(calculateStdDev(degreesList, calculateAvg(degreesList)));
			break;

		case "stdDevOutDegree":
			degreesArray = mVertexDegrees.getmMapVerticesoutDegree();
			degreesList = new IntArrayList(degreesArray);
			newMetricResult.setResult(calculateStdDev(degreesList, calculateAvg(degreesList)));
			break;

		default:// If metric is other than maxInDegree and maxOutDegree then apply the metric
			newMetricResult = applyUpdatable(graph, previousResult);
		}

		return newMetricResult;
	}

	/**
	 * Stores the previously computed values in UpdateMetricResult object
	 * 
	 * @param graph
	 *            - input graph.
	 * @param newMetricResult
	 *            - UpdatableMetricResult object containing the results that should
	 *            be updated.
	 * @return UpdatableMetricResult object with updated sum that can be used in
	 *         further iterations.
	 */
	@Override
	public SimpleMetricResult applyUpdatable(ColouredGraph graph, UpdatableMetricResult previousResult) {

		SimpleMetricResult metricResultTempObj = new SimpleMetricResult(getName(), 0.0);
		if (previousResult instanceof SimpleMetricResult) {
			// Copying previously computed values in temporary variables
			metricResultTempObj.setResult(((SimpleMetricResult) previousResult).result);
		}

		return metricResultTempObj;
	}
}
