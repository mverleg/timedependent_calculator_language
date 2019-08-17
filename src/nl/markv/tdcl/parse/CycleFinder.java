package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.cur;

/**
 * Distribute the nodes into groups, with the smallest possible cycle per group.
 *
 * @implNote This should return a best-effort result even if there are cyclic dependencies or directionality problems.
 */
public final class CycleFinder {

	private CycleFinder() {
		// Do not make instances
	}

	private static final class CycleSearchState {
		@Nonnull
		final Map<Node, Set<Node>> recursiveDeps = new HashMap<>();
		@Nonnull
		final Map<Node, NodeGroup> nodeGroups = new HashMap<>();
	}

	@Nonnull
	public static List<NodeGroup> distributeIntoCycles(@Nonnull List<Node> outputNodes) {
		// There is a Set for quickly checking whether there is a cycle,
		// and a Cycle tree/graph for finding the shortest cycle including
		// dependencies in case the Set says there is one.

		CycleSearchState state = new CycleSearchState();
		for (Node node : outputNodes) {
			findDependencyCycles(node, new Chain(null, cur(node)), state);
		}

		//TODO @mark: ordering? later?
		return new ArrayList<>(state.nodeGroups.values());
	}

	@Nonnull
	private static void findDependencyCycles(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull CycleSearchState state
	) {
		if (state.recursiveDeps.get(currentNode) != null) {
			System.out.println("Node already visited!");
			return;
		}
		Set<Node> currentNodeRecDeps = new HashSet<>();
		state.recursiveDeps.put(currentNode, currentNodeRecDeps);

		for (Dependency dep : currentNode.directDependencies) {

//			Set<Node> currentRecDeps = state.recursiveDeps.get(dep.node);
//			if (currentRecDeps != null && currentRecDeps.contains(dep)) {
//				// A cycle was found!
//				throw new IllegalStateException("cycle!");
//			}

			// Find dependencies of the processed node.
			findDependencyCycles(
					dep.node,
					new Chain(chain, dep),
					state
			);

			// Add dependencies of the processed node to this node.
			Set<Node> indirectDeps = state.recursiveDeps.get(dep.node);
			currentNodeRecDeps.addAll(indirectDeps);
		}
	}
}
