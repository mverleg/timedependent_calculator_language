package nl.markv.tdcl.visualize;

import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Node;
import nl.markv.tdcl.parse.CycleFinder;
import nl.markv.tdcl.parse.NodeGroup;

import static java.util.Collections.singletonList;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.prev;
import static nl.markv.tdcl.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphVizGeneratorTest {

	@Test
	void generateGraphViz() {

		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", prev(beta));
		var delta = new Node("Delta", cur(gamma));
		var epsilon = new Node("Epsilon", cur(gamma));
		var zeta = new Node("Zeta", cur(epsilon));
		var eta = new Node("Eta");
		var theta = new Node("Theta", cur(beta), prev(gamma));
		var iota = new Node("Iota");
		gamma.addDependency(prev(epsilon));
		eta.addDependency(prev(eta));
		zeta.addDependency(prev(eta));
		beta.addDependency(prev(delta));

		Set<Node> nodes = setOf(alpha, beta, gamma, delta, epsilon, zeta, eta, theta);
		Set<NodeGroup> groups = CycleFinder.distributeIntoCycles(singletonList(zeta));

		String graph = GraphVizGenerator.generateGraphViz(nodes, groups);

		System.out.println(graph);
		assertTrue(graph.contains("digraph"));
		assertTrue(graph.contains(delta.name));
		assertTrue(graph.contains(theta.name));
		assertTrue(graph.contains(iota.name));
	}
}
