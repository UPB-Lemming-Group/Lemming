package org.aksw.simba.lemming.metrics.single;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.ColouredGraphDecorator;
import org.aksw.simba.lemming.metrics.AbstractMetric;
import org.aksw.simba.lemming.metrics.single.edgemanipulation.Operation;
import org.aksw.simba.lemming.mimicgraph.constraints.TripleBaseSingleID;

import grph.path.ArrayListPath;

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
    public double apply(ColouredGraph graph) {
        try {
            return graph.getDiameter();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public UpdatableMetricResult applyUpdatable(ColouredGraphDecorator graph) {
        DiameterMetricResult metricResult = new DiameterMetricResult(getName(), Double.NaN);
        metricResult.setResult(graph.getDiameter());
        metricResult.setDiameterPath(graph.getNodesInDiameter());
        return metricResult;
    }

    public UpdatableMetricResult update(ColouredGraphDecorator graph, TripleBaseSingleID triple,
            Operation graphOperation, UpdatableMetricResult previousResult) {
        DiameterMetricResult metricResult = ((DiameterMetricResult) previousResult);
        ArrayListPath path = metricResult.getDiameterPath();
        if (path.containsVertex(triple.tailId) && graphOperation == Operation.ADD) {
            // TODO: Check various cases of Adding/Removing an edge from the
            // diameter path
            int startNode = path.getSource();
            ArrayListPath newPath = graph.getDiameterFromVertex(startNode);
            metricResult.setResult(newPath.getLength());
            metricResult.setDiameterPath(newPath);
        } else if (path.containsVertex(triple.headId) && path.containsVertex(triple.tailId)
                && graphOperation == Operation.REMOVE) {
            metricResult = (DiameterMetricResult) applyUpdatable(graph);
        }
        return metricResult;

    }

}
