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
		var n1 = new Node("Alpha");
		var n2 = new Node("Beta", cur(n1));
		var n3 = new Node("Gamma", cur(n2));
		var n4 = new Node("Delta", cur(n2));
		Chain chain = new Chain(new Chain(new Chain(null, cur(n1)), prev(n2)), next(n3));
		assertTrue(chain.findNode(n1).isPresent());
		assertEquals(cur(n1), chain.findNode(n1).get());
		assertTrue(chain.findNode(n2).isPresent());
		assertEquals(prev(n2), chain.findNode(n2).get());
		assertTrue(chain.findNode(n3).isPresent());
		assertEquals(next(n3), chain.findNode(n3).get());
		assertTrue(chain.findNode(n4).isEmpty());
	}

	@Test
	void findUptoNode() {
		var n1 = new Node("Alpha");
		var n2 = new Node("Beta", cur(n1));
		var n3 = new Node("Gamma", cur(n2));
		var n4 = new Node("Delta", cur(n2));
		Chain chain = new Chain(new Chain(new Chain(null, cur(n1)), prev(n2)), next(n3));
		List<Dependency> linear = chain.findUptoNode(n2);
		assertEquals(2, linear.size());
		assertEquals(n3, linear.get(0).node);
		assertEquals(n2, linear.get(1).node);
	}
}
