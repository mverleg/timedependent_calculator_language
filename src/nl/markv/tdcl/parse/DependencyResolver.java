package nl.markv.tdcl.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;

public final class DependencyResolver {

	private DependencyResolver() {
		// Do not make instances
	}

	@Nonnull
	public static EvalOrder solve(@Nonnull List<Node> finalNodes) {
		//TODO @mark: steps:
		//TODO @mark: 1) linearize
		//TODO @mark: 2) find cycles to detect order
		//TODO @mark: 3) find time zigzag to detect linear

		HashMap<Node, Set<Dependency>> recursiveDeps = new HashMap<>();



		EvalOrder order = new EvalOrder();



	}

	private void fillRecursiveDependencies(
			@Nonnull HashMap<Node, Set<Dependency>> recursiveDeps,
			@Nonnull Node currentNode
	) {
		for (Dependency dep : currentNode.directDependencies) {

		}
	}

}
