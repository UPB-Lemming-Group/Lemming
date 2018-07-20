package org.aksw.simba.lemming.metrics.single.nodetriangles;

import grph.Grph;
import grph.algo.MultiThreadProcessing;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.AbstractMetric;

import org.aksw.simba.lemming.metrics.single.SingleValueMetric;
import toools.set.IntHashSet;
import toools.set.IntSet;
import toools.set.IntSets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This metric is the number of triangles of the graph.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class NumberOfSimpleTrianglesMetric extends AbstractMetric implements SingleValueMetric {
	private Boolean calculateClusteringCoefficient = false;
	private List<Double> clusteringCoefficient = new ArrayList<>();

	public NumberOfSimpleTrianglesMetric() {
		super("#simpleTriangles");
	}

	public NumberOfSimpleTrianglesMetric(Boolean calculateClusteringCoefficient) {
		super("#simpleTriangles");
		this.calculateClusteringCoefficient = calculateClusteringCoefficient;
	}

	public List<Double> getClusteringCoefficient() {
		return clusteringCoefficient;
	}

	@Override
	public double apply(ColouredGraph graph) {
		MultiThreadedTriangleCountingProcess process;
		if (calculateClusteringCoefficient) {
			process = new MultiThreadedTriangleCountingProcess(graph, true);
			this.clusteringCoefficient = process.getClusteringCoefficient();
		}
		else
			process = new MultiThreadedTriangleCountingProcess(graph);
		return process.calculate();
	}

	private static class MultiThreadedTriangleCountingProcess {

		private ColouredGraph graph;
		private int trianglesSum = 0;
		private IntSet edgesOfVertex[];
		private List<Double> clusteringCoefficient = new ArrayList<>();
		private Boolean calculateClusteringCoefficient = false;

		public MultiThreadedTriangleCountingProcess(ColouredGraph graph) {
			this.graph = graph;
			edgesOfVertex = new IntSet[graph.getGraph().getNumberOfVertices()];
		}

		public MultiThreadedTriangleCountingProcess(ColouredGraph graph, Boolean calculateClusteringCoefficient) {
			this.graph = graph;
			edgesOfVertex = new IntSet[graph.getGraph().getNumberOfVertices()];
			this.calculateClusteringCoefficient = calculateClusteringCoefficient;
		}

		public List<Double> getClusteringCoefficient() {
			return clusteringCoefficient;
		}

		protected double calculate() {
			Grph grph = graph.getGraph();
			List<Double> clusteringCoefficient = new ArrayList<>();
			for (int i = 0; i < edgesOfVertex.length; ++i) {
				edgesOfVertex[i] = grph.getOutEdges(i);
				edgesOfVertex[i].addAll(grph.getInEdges(i));
			}
			/*
			 * A triangle is handled by the thread which handles the node with the lowest id in that triangle.
			 */
			new MultiThreadProcessing(graph.getGraph().getVertices()) {
				@Override
				protected void run(int threadID, int sourceId) {
					int count = 0;
					int sourceEdges[] = edgesOfVertex[sourceId].toIntArray();
					IntSet connectedNodesSet = new IntHashSet();
                    int n;

					IntSet vertexNeighbors = null;
					if (calculateClusteringCoefficient) {
						vertexNeighbors = grph.getInNeighbors(sourceId);
						vertexNeighbors.addAll(grph.getOutNeighbors(sourceId));
					}

					for (int i = 0; i < sourceEdges.length; ++i) {
						n = grph.getDirectedSimpleEdgeHead(sourceEdges[i]);
						if (n > sourceId) {
							connectedNodesSet.add(n);
							continue;
						}
						if (n == sourceId) {
							n = grph.getDirectedSimpleEdgeTail(sourceEdges[i]);
							if (n > sourceId)
								connectedNodesSet.add(n);
						}

//						for loop / self edge
						if (n != sourceId && calculateClusteringCoefficient)
							connectedNodesSet.add(n);
					}

                    int connectedNodes[] = connectedNodesSet.toIntArray();
                    for (int i = 0; i < connectedNodes.length; i++) {
                        for (int j = i + 1; j < connectedNodes.length; j++) {
                            if(IntSets.intersection(edgesOfVertex[connectedNodes[i]], edgesOfVertex[connectedNodes[j]]).size() > 0) {
                                ++count;
                            }
                        }
                    }
                    if (count > 0 && calculateClusteringCoefficient) {
//						double degree = connectedNodesSet.size();
						double degree = vertexNeighbors.size();
						clusteringCoefficient.add((2 * count) / (degree * (degree - 1)));
					}
					addCount(count);
				}
			};
			double ccSum = 0.0;
			for (double cc : clusteringCoefficient)
				ccSum += cc;
			System.out.println((ccSum / grph.getNumberOfVertices()));
			return trianglesSum;
		}

		private synchronized void addCount(int count) {
			trianglesSum += count;
		}
	}

}
