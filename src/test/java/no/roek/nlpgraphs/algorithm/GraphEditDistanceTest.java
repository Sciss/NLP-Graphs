package no.roek.nlpgraphs.algorithm;

import static no.roek.nlpgraphs.application.GED.getEditPath;
import static org.junit.Assert.assertEquals;

import edu.stanford.nlp.util.ArrayMap;
import no.roek.nlpgraphs.detailedanalysis.GraphEditDistance;
import no.roek.nlpgraphs.graph.Edge;
import no.roek.nlpgraphs.graph.Graph;
import no.roek.nlpgraphs.graph.Node;

import org.junit.Test;


public class GraphEditDistanceTest {

	private GraphEditDistance ged;
	private Graph g1;
	private Graph g2;

	@Test
	public void testDistance() {
		g1 = new Graph();
		g2 = new Graph();

		Node n1 = new Node("1", "one", new String[] {"NN", "one"});
		Node n2 = new Node("2", "two", new String[] {"GG", "two"});
		Node n3 = new Node("3", "three", new String[] {"TT", "three"});

		g1.addNode(n1);
		g1.addNode(n2);
		g1.addNode(n3);
		g1.addEdge(new Edge("1-2", n1, n2, "1-2"));
		g1.addEdge(new Edge("2-3", n2, n3, "2-3"));

		Node n4 = new Node("4", "four", new String[] {"NN", "one"});
		Node n5 = new Node("5", "five", new String[] {"GG", "two"});
		g2.addNode(n4);
		g2.addNode(n5);
		g2.addEdge(new Edge("4-5", n4, n5, "4-5"));

        ArrayMap<String, Double> mRel = new ArrayMap<>();
//        mRel.put("NN,NN", 0.0);
        mRel.put("NN,GG", 0.0);
        mRel.put("GG,NN", 0.0);
		ged = new GraphEditDistance(g1, g2, 1d, 1d, 1d, mRel, new ArrayMap<String, Double>());

		double dist = ged.getNormalizedDistance();
//		assertEquals(2, (int)dist);

        System.out.println("Edit path:");
        for(String editPath : getEditPath(g1, g2, ged.getCostMatrix(), true)) {
            System.out.println(editPath);
        }
	}

//	@Test
//	public void testCostMatrix() {
//
//	}
//
//	@Test
//	public void testEdgeDiff() {
//
//	}

//TODO: uncomment to test execution time for graph edit distance algorithm.
//	@Test
//	public void testExecutionTime() {
//		g1 = new Graph();
//		g2 = new Graph();
//
//		for (int i = 0; i < 1000; i++) {
//			g1.addNode(new Node(String.valueOf(i), new String[] {String.valueOf(i)}));
//			g2.addNode(new Node(String.valueOf(i), new String[] {String.valueOf(i)}));
//			if(i>0) {
//				g1.addEdge(new Edge(String.valueOf(i-1)+"_"+String.valueOf(i), g1.getNode(i-1), g1.getNode(i)));
//				g2.addEdge(new Edge(String.valueOf(i-1)+"_"+String.valueOf(i), g1.getNode(i-1), g1.getNode(i)));
//			}
//		}
//
//		ged = new GraphEditDistance(g1, g2, 1, 1, 1);
//		double dist = ged.getDistance();
//		assertEquals(0, (int)dist);
//	}
}
