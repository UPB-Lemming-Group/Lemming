package org.aksw.simba.lemming.metrics.single.edgetriangles.forward;

import com.carrotsearch.hppc.cursors.IntCursor;
import com.google.common.collect.Sets;
import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;
import toools.set.IntSet;
import toools.set.IntSets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * This class models an algorithm for computing the amount of node triangles in a given graph. This
 * is done using the so called forward algorithm proposed by Schank and Wagner in their work
 * "Finding, Counting and Listing all Triangles in Large Graphs, An Experimental Study" (see below).
 *
 * @see <a href=
 *      "https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study">https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study</a>.
 *
 * @author Alexander Hetzer
 * https://github.com/BlackHawkLex/Lemming/blob/master/src/main/java/org/aksw/simba/lemming/metrics/single/triangle/forward/ForwardNumberOfTriangleMetric.java
 *
 */
public class ForwardMetric extends AbstractMetric implements SingleValueMetric {
    /**
     * Creates a new {@link ForwardMetric}.
     */
    public ForwardMetric() {
        super("#edgetriangles");
    }


    @Override
    public double apply(ColouredGraph coloredGraph) {
        List<HashSet<Integer>> adjacencyDatastructure = new ArrayList<>(coloredGraph.getVertices().size());
        DegreeBasedDecreasingNodeOrdering nodeOrdering;
        int amountOfTriangles = 0;

        for (int i = 0; i < coloredGraph.getVertices().size(); i++) {
            adjacencyDatastructure.add(new HashSet<>());
        }
        nodeOrdering = new DegreeBasedDecreasingNodeOrdering(coloredGraph);

        HashSet<Integer> visitedNodes = new HashSet<>();
        for (Integer nodeId : nodeOrdering.getOrderedNodes()) {
            if (!visitedNodes.contains(nodeId))
                amountOfTriangles += processNeighborsOf(nodeId, coloredGraph, adjacencyDatastructure, nodeOrdering);
            visitedNodes.add(nodeId);
        }
        return amountOfTriangles;
    }


    /**
     * Processes the given neighbors of the given node.
     *
     * @param nodeId The id of the node whose neighbors should be processed.
     */
    private int processNeighborsOf(int nodeId, ColouredGraph coloredGraph, List<HashSet<Integer>> adjacencyDatastructure, DegreeBasedDecreasingNodeOrdering nodeOrdering) {
        int triangles = 0;
        IntSet neighborSet = IntSets.union(coloredGraph.getOutNeighbors(nodeId), coloredGraph.getInNeighbors(nodeId));
        for (IntCursor adjacentNodeIdCursor : neighborSet) {
            int adjacentNodeId = adjacentNodeIdCursor.value;
            if (nodeOrdering.isFirstSmallerWithRespectToOrder(nodeId, adjacentNodeId)) {
                Sets.SetView<Integer> intersection = Sets.intersection(adjacencyDatastructure.get(nodeId), adjacencyDatastructure.get(adjacentNodeId));
                if (intersection.size() > 0) {
                    for (int intersect : intersection) {
                        triangles = triangles +
                                IntSets.intersection(nodeOrdering.getEdges(nodeId), nodeOrdering.getEdges(adjacentNodeId)).size() *
                                IntSets.intersection(nodeOrdering.getEdges(adjacentNodeId), nodeOrdering.getEdges(intersect)).size() *
                                IntSets.intersection(nodeOrdering.getEdges(intersect), nodeOrdering.getEdges(nodeId)).size();
                    }
                }
                adjacencyDatastructure.get(adjacentNodeId).add(nodeId);
            }
        }
        return triangles;
    }

}
