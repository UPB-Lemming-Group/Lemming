package org.aksw.simba.lemming.metrics.single.edgemanipulation;

import org.aksw.simba.lemming.ColouredGraph;

import grph.Grph.DIRECTION;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * Class for storing in and out degree of vertices.
 * 
 * @author Atul
 *
 */
public class VertexDegrees {

	// Maps for storing in-degree and out-degree for different vertices
	private int mMapVerticesinDegree[];
	private int mMapVerticesoutDegree[];

	/**
	 * Initializing the Maps and calling computeVerticesDegree method.
	 * 
	 * @param clonedGraph
	 */
	public VertexDegrees(ColouredGraph clonedGraph) {
		// Initialize Hash Map for storing degree of vertices
		computeVerticesDegree(clonedGraph);
	}

	/**
	 * Computing the in degree and out degree for all vertices in graph.
	 * 
	 * @param graph
	 */
	private void computeVerticesDegree(ColouredGraph graph) {
		IntSet vertices = graph.getVertices();
		mMapVerticesinDegree = new int[vertices.size()];
		mMapVerticesoutDegree = new int[vertices.size()];
		int index = 0;
		IntIterator iterator = vertices.iterator();
		while (iterator.hasNext()) {
			int nextInt = iterator.nextInt();
			int inVertexDegree = graph.getGraph().getInVertexDegree(nextInt);
			int outVertexDegree = graph.getGraph().getOutVertexDegree(nextInt);

			mMapVerticesinDegree[index] = inVertexDegree;
			mMapVerticesoutDegree[index] = outVertexDegree;
			index++;
		}
	}

	/**
	 * Returns in degree for input vertex id.
	 * 
	 * @param vertexId
	 * @return
	 */
	public int getVertexIndegree(int vertexId) {
		return mMapVerticesinDegree[vertexId];
	}

	/**
	 * Returns out degree for input vertex id.
	 * 
	 * @param vertexId
	 * @return
	 */
	public int getVertexOutdegree(int vertexId) {
		return mMapVerticesoutDegree[vertexId];
	}

	/**
	 * Returns out degree for input vertex id.
	 * 
	 * @param vertexId
	 * @return
	 */
	public int getVertexdegree(int vertexId, DIRECTION direction) {

		int degree;
		if (direction == DIRECTION.in) {
			degree = mMapVerticesinDegree[vertexId];
		} else {
			degree = mMapVerticesoutDegree[vertexId];
		}

		return degree;
	}

	/**
	 * Returns all vertices with input in degree.
	 * 
	 * @param degree
	 * @return
	 */
	public IntSet getVerticesForIndegree(int degree) {
		IntSet setOfVertices = new IntOpenHashSet();

		for (int j = 0; j < mMapVerticesinDegree.length; ++j) {
			if (mMapVerticesinDegree[j] == degree) {
				setOfVertices.add(j);
			}
		}

		return setOfVertices;
	}

	/**
	 * Returns all vertices with input out degree.
	 * 
	 * @param degree
	 * @return
	 */
	public IntSet getVerticesForOutdegree(int degree) {
		IntSet setOfVertices = new IntOpenHashSet();

		for (int j = 0; j < mMapVerticesoutDegree.length; ++j) {
			if (mMapVerticesoutDegree[j] == degree) {
				setOfVertices.add(j);
			}
		}

		return setOfVertices;
	}

	/**
	 * Returns all vertices with input degree and direction.
	 * 
	 * @param degree
	 * @return
	 */
	public IntSet getVerticesForDegree(int degree, DIRECTION direction) {
		IntSet setOfVertices = new IntOpenHashSet();

		int mMapVerticesDegreeTemp[];

		if (direction == DIRECTION.in) {
			mMapVerticesDegreeTemp = mMapVerticesinDegree;
		} else {
			mMapVerticesDegreeTemp = mMapVerticesoutDegree;
		}

		for (int j = 0; j < mMapVerticesDegreeTemp.length; ++j) {
			if (mMapVerticesDegreeTemp[j] == degree) {
				setOfVertices.add(j);
			}
		}

		return setOfVertices;
	}

	/**
	 * Updates the in-degree for input vertex id with specified additionValue.
	 * 
	 * @param vertexId
	 * @param additionValue
	 */
	public void updateVertexIndegree(int vertexId, int additionValue) {
		mMapVerticesinDegree[vertexId] = mMapVerticesinDegree[vertexId] + additionValue;
	}

	/**
	 * Updates the out-degree for input vertex id with specified additionValue.
	 * 
	 * @param vertexId
	 * @param additionValue
	 */
	public void updateVertexOutdegree(int vertexId, int additionValue) {
		mMapVerticesoutDegree[vertexId] = mMapVerticesoutDegree[vertexId] + additionValue;
	}

	public int[] getmMapVerticesinDegree() {
		return mMapVerticesinDegree;
	}

	public int[] getmMapVerticesoutDegree() {
		return mMapVerticesoutDegree;
	}
}
