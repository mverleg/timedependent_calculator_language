package nl.markv.tdcl.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Node;
import nl.markv.tdcl.visualize.GraphVizGenerator;

import static java.util.Collections.singletonList;
import static nl.markv.tdcl.data.Dependency.Direction.Current;
import static nl.markv.tdcl.data.Dependency.Direction.Previous;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.prev;
import static nl.markv.tdcl.util.CollectionUtil.listOf;
import static nl.markv.tdcl.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO @mark: multiple starting points
class CycleFinderTest {

	@Test
	void testLinear() {
		var n1 = new Node("Alpha");
		var n2 = new Node("Beta", cur(n1));
		var n3 = new Node("Gamma", cur(n2));
		List<Node> finals = singletonList(n3);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(3, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(n1)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
		assertTrue(cycles.contains(new SingleNodeGroup(n3)));
	}

	@Test
	void testSelfReferentialAtFinal() {
		var n1 = new Node("Input1").selfRef(Previous);
		List<Node> finals = singletonList(n1);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(1, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(n1), true, false)));
	}

	@Test
	void testSelfReferentialAfterFinal() {
		var n1 = new Node("Input1").selfRef(Previous);
		var n2 = new Node("Input2", cur(n1));
		List<Node> finals = singletonList(n2);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(2, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(n1), true, false)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
	}

	@Test
	void testLongLoop() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", prev(beta));
		var delta = new Node("Delta", cur(gamma));
		alpha.addDependency(cur(gamma));
		List<Node> finals = singletonList(delta);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(2, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(gamma, alpha, beta), true, false)));
		assertTrue(cycles.contains(new SingleNodeGroup(delta)));
	}

	@Test
	void testSmall() {
		var n1 = new Node("Input1");
		var n2 = new Node("Input2");
		var n3 = new Node("Comp3", cur(n1), prev(n2));
		var n4 = new Node("Comp4", cur(n3)).selfRef(Previous);
		var n5 = new Node("Final5", cur(n4));
		List<Node> finals = singletonList(n5);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(5, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(n1)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
		assertTrue(cycles.contains(new SingleNodeGroup(n3)));
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(n4), true, false)));
		assertTrue(cycles.contains(new SingleNodeGroup(n5)));
	}

	@Test
	void testMergeCycles() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", prev(beta));
		var delta = new Node("Delta", cur(gamma));
		var epsilon = new Node("Epsilon", cur(gamma));
		var zeta = new Node("Zeta", cur(epsilon));
		gamma.addDependency(prev(epsilon));
		beta.addDependency(prev(delta));
		List<Node> finals = singletonList(zeta);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(3, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(alpha)));
		assertTrue(cycles.contains(new SingleNodeGroup(zeta)));
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(delta, beta, gamma, epsilon), true, false)));
	}

	//TODO @mark: test over-aggressive merging of groups

	//TODO @mark: in testMultipleFinals when iota depended on current beta, no error was raised

	//TODO @mark: finals are always cycle groups, never single nodes

	@Test
	void testMultipleFinals() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", cur(beta));
		var delta = new Node("Delta", cur(alpha));
		var epsilon = new Node("Epsilon");
		var theta = new Node("Theta").selfRef(Previous);
		var zeta = new Node("Zeta", cur(theta));
		var eta = new Node("Eta", cur(zeta)).selfRef(Previous);
		var iota = new Node("Iota", prev(beta), cur(epsilon));
		alpha.addDependency(prev(gamma));
		beta.addDependency(cur(iota));
		beta.addDependency(cur(eta));
		beta.addDependency(cur(eta));
		List<Node> finals = listOf(gamma, epsilon, zeta);

		Set<NodeGroup> groups = CycleFinder.distributeIntoCycles(finals);  //TODO @mark: TEMPORARY! REMOVE THIS!
		String graph = GraphVizGenerator.generateGraphViz(setOf(alpha, beta, gamma, delta, epsilon, theta, zeta, eta, iota), groups, new HashSet<>(finals));  //TODO @mark: TEMPORARY! REMOVE THIS!
		System.out.println(graph);  //TODO @mark: TEMPORARY! REMOVE THIS!

		//TODO @mark: eta incorrectly gets pulled into the bigger group

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(5, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(epsilon)));
		assertTrue(cycles.contains(new SingleNodeGroup(zeta)));
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(theta), true, false)));
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(eta), true, false)));
		assertTrue(cycles.contains(new CycleNodeGroup(setOf(alpha, beta, gamma, iota), true, false)));
		assertFalse(cycles.contains(new SingleNodeGroup(delta)));
	}
}
