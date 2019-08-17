package nl.markv.tdcl.parse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.Direction.Current;
import static nl.markv.tdcl.data.Dependency.Direction.Previous;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.prev;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CycleFinderTest {

	@Test
	void testLinear() {
		var n1 = new Node("Alpha");
		var n2 = new Node("Beta", cur(n1));
		var n3 = new Node("Gamma", cur(n2));
		List<Node> finals = Collections.singletonList(n3);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(3, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(n1)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
		assertTrue(cycles.contains(new SingleNodeGroup(n3)));
	}

	@Test
	void testSelfReferentialAtFinal() {
		var n1 = new Node("Input1").selfRef(Previous);
		List<Node> finals = Collections.singletonList(n1);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(1, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(Collections.singletonList(n1), true, false)));
	}

	@Test
	void testSelfReferentialAfterFinal() {
		var n1 = new Node("Input1").selfRef(Previous);
		var n2 = new Node("Input2", cur(n1));
		List<Node> finals = Collections.singletonList(n2);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(2, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(Collections.singletonList(n1), true, false)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
	}

	@Test
	void testLongLoop() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", prev(beta));
		var delta = new Node("Delta", cur(gamma));
		alpha.addDependency(cur(gamma));
		List<Node> finals = Collections.singletonList(delta);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(2, cycles.size());
		assertTrue(cycles.contains(new CycleNodeGroup(Arrays.asList(alpha, beta, gamma), true, false)));
		assertTrue(cycles.contains(new SingleNodeGroup(delta)));
	}

	@Test
	void testSmall() {
		var n1 = new Node("Input1");
		var n2 = new Node("Input2");
		var n3 = new Node("Comp3", cur(n1), prev(n2));
		var n4 = new Node("Comp4", cur(n3)).selfRef(Current);
		var n5 = new Node("Final5", cur(n4));
		List<Node> finals = Collections.singletonList(n5);

		Set<NodeGroup> cycles = CycleFinder.distributeIntoCycles(finals);
		assertEquals(5, cycles.size());
		assertTrue(cycles.contains(new SingleNodeGroup(n1)));
		assertTrue(cycles.contains(new SingleNodeGroup(n2)));
		assertTrue(cycles.contains(new SingleNodeGroup(n3)));
		assertTrue(cycles.contains(new CycleNodeGroup(Collections.singletonList(n4), true, true)));
		assertTrue(cycles.contains(new SingleNodeGroup(n5)));
	}
}
