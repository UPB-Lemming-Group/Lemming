package org.aksw.simba.lemming.metrics.single.expressions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.creation.GeologyDataset;
import org.aksw.simba.lemming.creation.GraphCreator;
import org.aksw.simba.lemming.creation.IDatasetManager;
import org.aksw.simba.lemming.creation.SemanticWebDogFoodDataset;
import org.aksw.simba.lemming.metrics.single.AvgVertexDegreeMetric;
import org.aksw.simba.lemming.metrics.single.MaxVertexDegreeMetric;
import org.aksw.simba.lemming.metrics.single.NumberOfEdgesMetric;
import org.aksw.simba.lemming.metrics.single.NumberOfVerticesMetric;
import org.aksw.simba.lemming.metrics.single.SingleValueMetric;
import org.aksw.simba.lemming.metrics.single.StdDevVertexDegree;
import org.aksw.simba.lemming.metrics.single.UpdatableMetricResult;
import org.aksw.simba.lemming.metrics.single.edgemanipulation.Operation;
import org.aksw.simba.lemming.metrics.single.edgetriangles.EdgeTriangleMetric;
import org.aksw.simba.lemming.metrics.single.nodetriangles.NodeTriangleMetric;
import org.aksw.simba.lemming.metrics.single.updateDegree.UpdateMetricTest;
import org.aksw.simba.lemming.mimicgraph.constraints.TripleBaseSingleID;
import org.aksw.simba.lemming.mimicgraph.generator.GraphGenerationRandomly;
import org.aksw.simba.lemming.mimicgraph.generator.GraphOptimization;
import org.aksw.simba.lemming.mimicgraph.generator.IGraphGeneration;
import org.aksw.simba.lemming.mimicgraph.metricstorage.ConstantValueStorage;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

import grph.Grph.DIRECTION;

public class EdgeTriangleExpressionTest extends UpdateMetricTest {
    private static final String GRAPH_FILE = "graph1_1.n3"; // graph_loop_2.n3 graph1_1.n3 email-Eu-core.n3
    // private static final String GRAPH_FILE1 = "graph1_1.n3";
    private static ColouredGraph inputGraph;

    public EdgeTriangleExpressionTest() {
        inputGraph = getGraph();
    }

    public ColouredGraph getGraph() {
        int mNumberOfDesiredVertices = 1281;

        ColouredGraph graphs[] = new ColouredGraph[20];
        IDatasetManager mDatasetManager;
        mDatasetManager = new GeologyDataset();
        String datasetPath = "GeologyGraphs/";
        graphs = mDatasetManager.readGraphsFromFiles(datasetPath);

        int iNumberOfThreads = -1;
        long seed = System.currentTimeMillis();

        IGraphGeneration mGrphGenerator;
        mGrphGenerator = new GraphGenerationRandomly(mNumberOfDesiredVertices, graphs, iNumberOfThreads, seed);

        mGrphGenerator.generateGraph();

        // ColouredGraph clonedGraph = mGrphGenerator.getMimicGraph().clone();

        long secSeed = mGrphGenerator.getSeed() + 1;
        ConstantValueStorage valuesCarrier = new ConstantValueStorage(datasetPath);

        List<SingleValueMetric> metrics = new ArrayList<>();
        // these are two fixed metrics: NodeTriangleMetric and EdgeTriangleMetric
        metrics.add(new NodeTriangleMetric());
        metrics.add(new EdgeTriangleMetric());

        // these are optional metrics
        metrics.add(new MaxVertexDegreeMetric(DIRECTION.in));
        metrics.add(new MaxVertexDegreeMetric(DIRECTION.out));
        metrics.add(new AvgVertexDegreeMetric());
        metrics.add(new StdDevVertexDegree(DIRECTION.in));
        metrics.add(new StdDevVertexDegree(DIRECTION.out));
        metrics.add(new NumberOfEdgesMetric());
        metrics.add(new NumberOfVerticesMetric());

        GraphOptimization grphOptimizer = new GraphOptimization(graphs, mGrphGenerator, metrics, valuesCarrier,
                secSeed);
        ColouredGraph clonedGraph = grphOptimizer.getmEdgeModifier().getGraph();
        
        return clonedGraph;
    }

    public ColouredGraph getGraphFile() {
        Model model = ModelFactory.createDefaultModel();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(GRAPH_FILE);
        model.read(is, null, "N3");
        IOUtils.closeQuietly(is);

        GraphCreator creator = new GraphCreator();
        ColouredGraph graph = creator.processModel(model);
        return graph;
    }

    public ColouredGraph getGraphSWDF() {
        int mNumberOfDesiredVertices = 45420;

        ColouredGraph graphs[] = new ColouredGraph[20];
        IDatasetManager mDatasetManager;
        mDatasetManager = new SemanticWebDogFoodDataset();
        String datasetPath = "SemanticWebDogFood/";
        graphs = mDatasetManager.readGraphsFromFiles(datasetPath);

        int iNumberOfThreads = -1;
        long seed = System.currentTimeMillis();

        IGraphGeneration mGrphGenerator;
        mGrphGenerator = new GraphGenerationRandomly(mNumberOfDesiredVertices, graphs, iNumberOfThreads, seed);

        mGrphGenerator.generateGraph();

        // ColouredGraph clonedGraph = mGrphGenerator.getMimicGraph().clone();

        long secSeed = mGrphGenerator.getSeed() + 1;
        ConstantValueStorage valuesCarrier = new ConstantValueStorage(datasetPath);

        List<SingleValueMetric> metrics = new ArrayList<>();
        // these are two fixed metrics: NodeTriangleMetric and EdgeTriangleMetric
        metrics.add(new NodeTriangleMetric());
        metrics.add(new EdgeTriangleMetric());

        // these are optional metrics
        metrics.add(new MaxVertexDegreeMetric(DIRECTION.in));
        metrics.add(new MaxVertexDegreeMetric(DIRECTION.out));
        metrics.add(new AvgVertexDegreeMetric());
        metrics.add(new StdDevVertexDegree(DIRECTION.in));
        metrics.add(new StdDevVertexDegree(DIRECTION.out));
        metrics.add(new NumberOfEdgesMetric());
        metrics.add(new NumberOfVerticesMetric());

        GraphOptimization grphOptimizer = new GraphOptimization(graphs, mGrphGenerator, metrics, valuesCarrier,
                secSeed);
        ColouredGraph clonedGraph = grphOptimizer.getmEdgeModifier().getGraph();
        
        return clonedGraph;
    }

    @Test
    public void testcase1() {

        // ColouredGraph graph = inputGraph;

        ColouredGraph graph = inputGraph;

        EdgeTriangleMetric edgeTriangleMetric = new EdgeTriangleMetric();
        UpdatableMetricResult edgeTriangleMetricResult = edgeTriangleMetric.applyUpdatable(inputGraph);
        double oldMetricResult = edgeTriangleMetricResult.getResult();
        System.out.println("Select a triple to reduce the metric : ");
        System.out.println("Edge Triangle metric (Old Result) : " + edgeTriangleMetricResult.getResult());

        long seed = System.currentTimeMillis();
        TripleBaseSingleID triple = edgeTriangleMetric.getTripleRemove(graph, edgeTriangleMetricResult, seed, true);

        graph = removeEdge(graph, triple.edgeId);

        edgeTriangleMetricResult = edgeTriangleMetric.update(graph, triple, Operation.REMOVE, edgeTriangleMetricResult);
        System.out.println("Triple details: head - " + triple.headId + ", tail - " + triple.tailId);
        System.out.println("Edge Triangle metric (New Result) : " + edgeTriangleMetricResult.getResult());
        System.out.println();

        Assert.assertTrue("Metric not decreased!", edgeTriangleMetricResult.getResult() < oldMetricResult);

    }

    @Test
    public void testcase2() {

        ColouredGraph graph = inputGraph;
        //ColouredGraph graph = buildGraph1();

        EdgeTriangleMetric edgeTriangleMetric = new EdgeTriangleMetric();
        UpdatableMetricResult edgeTriangleMetricResult = edgeTriangleMetric.applyUpdatable(inputGraph);
        double oldMetricResult = edgeTriangleMetricResult.getResult();
        System.out.println("Select a random triple : ");
        System.out.println("Edge Triangle metric (Old Result) : " + edgeTriangleMetricResult.getResult());

        long seed = System.currentTimeMillis();
        TripleBaseSingleID triple = edgeTriangleMetric.getTripleRemove(graph, edgeTriangleMetricResult, seed, false);

        graph = removeEdge(graph, triple.edgeId);

        edgeTriangleMetricResult = edgeTriangleMetric.update(graph, triple, Operation.REMOVE, edgeTriangleMetricResult);
        System.out.println("Triple details: head - " + triple.headId + ", tail - " + triple.tailId);
        System.out.println("Edge Triangle metric (New Result) : " + edgeTriangleMetricResult.getResult());
        System.out.println();

        Assert.assertTrue("Metric decreased!", edgeTriangleMetricResult.getResult() >= oldMetricResult);

    }
}
