package org.aksw.simba.lemming.metrics.single;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;

/**
 * This metric is the diameter of the graph.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DiameterMetric extends AbstractMetric implements SingleValueMetric {

	public DiameterMetric() {
		super("diameter");
	}

	@Override
	public UpdatableMetricResult apply(ColouredGraph graph) {
		try {
			return new SingleValueMetricResult(this.name, graph.getDiameter());
		} catch (Exception e) {
			return new SingleValueMetricResult(this.name, Double.NaN);
		}
	}

}
