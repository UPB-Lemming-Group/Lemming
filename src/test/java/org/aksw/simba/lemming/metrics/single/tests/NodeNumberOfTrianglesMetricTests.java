package org.aksw.simba.lemming.metrics.single.tests;

import org.aksw.simba.lemming.ColouredGraph;
import org.aksw.simba.lemming.metrics.single.NumberOfTrianglesMetricTest;
import org.aksw.simba.lemming.metrics.single.nodetriangles.*;
import org.aksw.simba.lemming.metrics.single.nodetriangles.ayz.ListingAyzMetric;
import org.aksw.simba.lemming.metrics.single.nodetriangles.forward.ForwardMetric;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@RunWith(Parameterized.class)
public class NodeNumberOfTrianglesMetricTests extends NumberOfTrianglesMetricTest {
    private int expectedTriangles;
    private static final double DOUBLE_COMPARISON_DELTA = 0.000001;
    private ColouredGraph graph;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();
        testConfigs.add(new Object[] { "graph1.n3", 1 });
        testConfigs.add(new Object[] { "graph_loop.n3", 1 });
        testConfigs.add(new Object[] { "graph_loop_2.n3", 3 });
        testConfigs.add(new Object[] { "email-Eu-core.n3", 105461 });

        return testConfigs;
    }

    public NodeNumberOfTrianglesMetricTests(String graphFile, int expectedTriangles) {
        super();
        this.expectedTriangles = expectedTriangles;
        if (this.graph != null)
            this.graph = null;

        this.graph = getColouredGraph(graphFile);
    }

    @Test
    public void ListingAyzMetricTest() {
        Assert.assertNotNull(graph);

        final double delta = 3.0;
        ListingAyzMetric metric = new ListingAyzMetric(delta);
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void ForwardMetricTest() {
        Assert.assertNotNull(graph);

        ForwardMetric metric = new ForwardMetric();
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void NodeIteratorCoreMetricTest() {
        Assert.assertNotNull(graph);

        NodeIteratorCoreMetric metric = new NodeIteratorCoreMetric();
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void NodeIteratorMetricTest() {
        Assert.assertNotNull(graph);

        NodeIteratorMetric metric = new NodeIteratorMetric();
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void NumberOfSimpleTrianglesTest() {
        Assert.assertNotNull(graph);

        NumberOfSimpleTrianglesMetric metric = new NumberOfSimpleTrianglesMetric();
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void DuolionMetricTest() {
        Assert.assertNotNull(graph);

        final double edgeSurvivalProbability = 0.9;
        DuolionMetric metric = new DuolionMetric(new ForwardMetric(), edgeSurvivalProbability, new Random().nextLong());
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }

    @Test
    public void EdgeIteratorMetricTest() {
        Assert.assertNotNull(graph);

        EdgeIteratorMetric metric = new EdgeIteratorMetric();
        double countedTriangles = metric.apply(graph);

        Assert.assertEquals(expectedTriangles, countedTriangles, DOUBLE_COMPARISON_DELTA);
    }
}
