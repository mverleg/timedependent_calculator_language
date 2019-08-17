package nl.markv.tdcl.parse;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.next;
import static nl.markv.tdcl.data.Dependency.prev;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainTest {

	@Test
	void findNode() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", cur(beta));
		var delta = new Node("Delta", cur(beta));
		Chain chain = new Chain(new Chain(new Chain(null, cur(alpha)), prev(beta)), next(gamma));
		assertTrue(chain.findNode(alpha).isPresent());
		assertEquals(cur(alpha), chain.findNode(alpha).get());
		assertTrue(chain.findNode(beta).isPresent());
		assertEquals(prev(beta), chain.findNode(beta).get());
		assertTrue(chain.findNode(gamma).isPresent());
		assertEquals(next(gamma), chain.findNode(gamma).get());
		assertTrue(chain.findNode(delta).isEmpty());
	}

	@Test
	void findUptoNode() {
		var alpha = new Node("Alpha");
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", cur(beta));
		new Node("Delta", cur(beta));
		Chain chain = new Chain(new Chain(new Chain(null, cur(alpha)), prev(beta)), next(gamma));
		List<Dependency> linear = chain.findUptoNode(beta);
		assertEquals(2, linear.size());
		assertEquals(gamma, linear.get(0).node);
		assertEquals(beta, linear.get(1).node);
	}

	@Test
	void findUptoNodeCycle() {
		var alpha = new Node("Alpha" /*gamma*/);
		var beta = new Node("Beta", cur(alpha));
		var gamma = new Node("Gamma", cur(beta));
		alpha.addDependency(cur(gamma));
		Chain chain = new Chain(new Chain(new Chain(new Chain(null, cur(gamma)), cur(alpha)), prev(beta)), next(gamma));
		List<Dependency> linear = chain.findUptoNode(beta);
		assertEquals(2, linear.size());
		assertEquals(gamma, linear.get(0).node);
		assertEquals(beta, linear.get(1).node);
	}
}
