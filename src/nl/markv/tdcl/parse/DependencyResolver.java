package nl.markv.tdcl.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import jdk.jshell.spi.ExecutionControl;
import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.cur;

public final class DependencyResolver {

	private DependencyResolver() {
		// Do not make instances
	}

	@Nonnull
	public static EvalOrder solve(@Nonnull List<Node> finalNodes) {
		//TODO @mark: steps:
		//TODO @mark: 1) linearize / find all dependencies
		//TODO @mark: 2) which final node is final
		//TODO @mark: 3) find cycles to detect order
		//TODO @mark: 4) find time zigzag to detect linear
		//TODO @mark: current strategy idea:
		//TODO @mark: 0) find if one start node depends on another
		//TODO @mark: 1) find node cycles, then
		//TODO @mark: 1a) if same node version, it's an error
		//TODO @mark: 1b) if another version, cycle is a group with that direction
		//TODO @mark: 2) if a node is part of two groups, merge them if same direction
		//TODO @mark: 3) all leftover vars are their own group
		//TODO @mark: 4) each group has a direction based on INTERNAL prev/next refs,
		//TODO @mark:     if both prev and next are needed, its an error
		//TODO @mark: 5) put groups in correct order
		//TODO @mark: 6) put items in groups in correct order

		//TODO @mark: another note: if I add depth instead of prev/next to Chain, then is the in-group order just determined by ordering depth?

		Map<Node, Set<Node>> recursiveDeps = new HashMap<>();
		for (Node node : finalNodes) {
			findDependencyCycles(node, new Chain(null, cur(node)), recursiveDeps);
		}



		EvalOrder order = new EvalOrder();


		throw new NotImplementedException("todo: ");  //TODO @mark:
	}

	@Nonnull
	private static Optional<Chain> findDependencyCycles(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull Map<Node, Set<Node>> recursiveDeps
	) {
		for (Dependency dep : currentNode.directDependencies) {
			Set<Node> currentRecDeps = recursiveDeps.get(dep.node);
			if (currentRecDeps != null && currentRecDeps.contains(dep)) {
				// A cycle was found!
				throw new IllegalStateException("cycle!");
			}
			findDependencyCycles(
					dep.node,
					new Chain(chain, dep),
					recursiveDeps
			);
		}
	}

}
