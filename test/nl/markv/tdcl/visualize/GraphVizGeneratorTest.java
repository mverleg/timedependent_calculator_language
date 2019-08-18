package nl.markv.tdcl.visualize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Node;
import nl.markv.tdcl.parse.CycleFinder;
import nl.markv.tdcl.parse.NodeGroup;

import static java.util.Collections.singletonList;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.next;
import static nl.markv.tdcl.data.Dependency.prev;
import static nl.markv.tdcl.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphVizGeneratorTest {

	@Test
	void generateGraphViz() {

		var alpha = new Node("Alpha");
		var beta = new Node("Beta");
		var gamma = new Node("Gamma", prev(beta));
		var delta = new Node("Delta", cur(gamma));
		var epsilon = new Node("Epsilon", cur(gamma));
		var zeta = new Node("Zeta", cur(epsilon));
		var eta = new Node("Eta");
		var theta = new Node("Theta", cur(beta), prev(gamma));
		var iota = new Node("Iota");
		var kappa = new Node("Kappa");
		var lambda = new Node("Lambda", prev(kappa), cur(alpha));
		gamma.addDependency(prev(epsilon));
		eta.addDependency(prev(eta));
		zeta.addDependency(prev(eta));
		beta.addDependency(prev(delta));
		kappa.addDependency(next(lambda));
		beta.addDependency(next(kappa));

		Set<Node> nodes = setOf(alpha, beta, gamma, delta, epsilon, zeta, eta, theta, iota, kappa, lambda);
		List<Node> finals = singletonList(zeta);
		Set<NodeGroup> groups = CycleFinder.distributeIntoCycles(finals);

		String graph = GraphVizGenerator.generateGraphViz(nodes, groups, new HashSet<>(finals));

		System.out.println(graph);
		assertTrue(graph.contains("digraph"));
		assertTrue(graph.contains(delta.name));
		assertTrue(graph.contains(theta.name));
		assertTrue(graph.contains(iota.name));
	}
}
