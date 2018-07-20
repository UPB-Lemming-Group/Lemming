package org.aksw.simba.lemming.metrics.single.nodetriangles;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;

import com.carrotsearch.hppc.cursors.IntCursor;

import grph.Grph;
import toools.set.IntSet;
import toools.set.IntSets;

import java.util.ArrayList;
import java.util.List;


/**
 * This class models an algorithm for counting the amount of node triangles in a given graph. This
 * is done using the so called node-iterator-core algorithm explained by Schank and Wagner in their
 * work "Finding, Counting and Listing all Triangles in Large Graphs, An Experimental Study".
 *
 * @see <a href=
 *      "https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study">https://www.researchgate.net/publication/221131490_Finding_Counting_and_Listing_All_Triangles_in_Large_Graphs_an_Experimental_Study</a>).
 *
 * @author Tanja Tornede
 * https://github.com/BlackHawkLex/Lemming/blob/master/src/main/java/org/aksw/simba/lemming/metrics/single/triangle/NodeIteratorCoreNumberOfTrianglesMetric.java
 *
 */
public class NodeIteratorCoreMetric extends AbstractMetric implements SingleValueMetric {
    private List<Double> clusteringCoefficient = new ArrayList<>();
    private Boolean calculateClusteringCoefficient = false;

    public NodeIteratorCoreMetric() {
        super("node-iterator-core #node triangles");
    }

    public NodeIteratorCoreMetric(Boolean calculateClusteringCoefficient) {
        super("node-iterator-core #node triangles");
        this.calculateClusteringCoefficient = calculateClusteringCoefficient;
    }

    public List<Double> getClusteringCoefficient() {
        return clusteringCoefficient;
    }


    @Override
    public double apply(ColouredGraph graph) {
        IntSet visitedVertices = IntSets.from(new int[] {});
        Grph grph = graph.getGraph();

        int[] degrees = new int[grph.getVertices().size()];
        for (IntCursor vertex : grph.getVertices()) {
            degrees[vertex.value] = IntSets.union(grph.getInNeighbors(vertex.value), grph.getOutNeighbors(vertex.value)).size();
        }

        int numberOfTriangles = 0;
        int vertexWithMinimumDegree = getNodeWithMinimumDegree(degrees);
        while (vertexWithMinimumDegree < Integer.MAX_VALUE && visitedVertices.size() < grph.getVertices().size() - 2) {
            int triangleCount = 0;
            IntSet neighbors = IntSets.difference(
                    IntSets.union(grph.getInNeighbors(vertexWithMinimumDegree), grph.getOutNeighbors(vertexWithMinimumDegree)), visitedVertices);
            for (IntCursor neighbor1 : neighbors) {
                IntSet neighbors1 = IntSets
                        .difference(IntSets.union(grph.getInNeighbors(neighbor1.value), grph.getOutNeighbors(neighbor1.value)), visitedVertices);
                for (IntCursor neighbor2 : neighbors) {
                    if (vertexWithMinimumDegree != neighbor1.value && vertexWithMinimumDegree != neighbor2.value
                            && neighbor1.value < neighbor2.value && neighbors1.contains(neighbor2.value)) {
                        numberOfTriangles++;
                        triangleCount++;
//                        numberOfTriangles = numberOfTriangles + IntSets.union(graph.getOutEdges(neighbor1.value), graph.getInEdges(neighbor1.value));
                    }
                }
            }

            if (!this.calculateClusteringCoefficient)
                visitedVertices.add(vertexWithMinimumDegree);

            if (triangleCount > 0 && this.calculateClusteringCoefficient) {
                IntSet vertexNeighbors = grph.getInNeighbors(vertexWithMinimumDegree);
                vertexNeighbors.addAll(grph.getOutNeighbors(vertexWithMinimumDegree));
                double degree = vertexNeighbors.size();
                clusteringCoefficient.add((2 * triangleCount) / (degree * (degree - 1)));
            }

            degrees[vertexWithMinimumDegree] = 0;
            for (IntCursor vertex : neighbors) {
                if (degrees[vertex.value] > 0) {
                    if (!this.calculateClusteringCoefficient)
                        degrees[vertex.value]--;
                }
            }

            vertexWithMinimumDegree = getNodeWithMinimumDegree(degrees);
        }
        return numberOfTriangles;
    }


    private int getNodeWithMinimumDegree(int[] degrees) {
        int nodeWithMinimumDegree = Integer.MAX_VALUE;
        int minimalDegree = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < degrees.length; vertex++) {
            if (degrees[vertex] > 0 && degrees[vertex] < minimalDegree) {
                minimalDegree = degrees[vertex];
                nodeWithMinimumDegree = vertex;
            }
        }
        return nodeWithMinimumDegree;
    }

}
