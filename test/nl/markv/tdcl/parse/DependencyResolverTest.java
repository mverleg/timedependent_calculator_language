package nl.markv.tdcl.parse;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.Direction.Current;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.prev;

class DependencyResolverTest {

	@Test
	void testLinear() {
		var n1 = new Node("Alpha");
		var n2 = new Node("Beta", cur(n1));
		var n3 = new Node("Gamma", cur(n2));
		List<Node> finals = Collections.singletonList(n3);

		new DependencyResolver(finals)
	}

	@Test
	void testSmall() {
		var n1 = new Node("Input1");
		var n2 = new Node("Input2");
		var n3 = new Node("Comp3", cur(n1), prev(n2));
		var n4 = new Node("Comp4", cur(n3)).selfRef(Current);
		var n5 = new Node("Final5", cur(n4));
		List<Node> finals = Collections.singletonList(n5);
	}
}
