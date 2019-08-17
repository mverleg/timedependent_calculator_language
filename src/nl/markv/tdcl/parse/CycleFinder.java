package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Dependency.Direction;
import nl.markv.tdcl.data.Dependency.Direction;
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

		//TODO @mark: ordering? later? make unique as well!
		return new ArrayList<>(state.nodeGroups.values());
	}

	@Nonnull
	private static void findDependencyCycles(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull CycleSearchState state
	) {
		// Check whether dependencies are already known.
		if (state.recursiveDeps.get(currentNode) != null) {

			makeOrJoinGroup(currentNode, chain, state);
			return;
		}
		Set<Node> currentNodeRecDeps = new HashSet<>();
		state.recursiveDeps.put(currentNode, currentNodeRecDeps);

		for (Dependency dep : currentNode.directDependencies) {

			// Find dependencies of the processed node.
			findDependencyCycles(
					dep.node,
					new Chain(chain, dep),
					state
			);

			// Add direct dependency.
			currentNodeRecDeps.add(dep.node);

			// Add dependencies of the processed node to this node.
			Set<Node> indirectDeps = state.recursiveDeps.get(dep.node);
			currentNodeRecDeps.addAll(indirectDeps);
		}
	}

	@Nonnull
	private static NodeGroup makeOrJoinGroup(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull CycleSearchState state
	) {
		// Find the cycle nodes.
		List<Dependency> cycle = chain.findUptoNode(currentNode);

		// Determine the direction(s).
		boolean canDownwards = true;
		boolean canUpwards = true;
		for (Dependency dependency : cycle) {
			if (dependency.direction == Direction.Previous) {
				canUpwards = false;
			} else if (dependency.direction == Direction.Next) {
				canDownwards = false;
			}
		}

		// Make the group.
		List<Node> nodes = cycle.stream()
				.map(dep -> dep.node)
				.collect(Collectors.toList());
		NodeGroup newGroup = new NodeGroup(
				nodes, canDownwards, canUpwards);

		// Find any existing groups to merge.
		List<NodeGroup> mergeGroups = nodes.stream()
				.map(node -> state.nodeGroups.get(node))
				.filter(grp -> grp != null)
				.collect(Collectors.toList());
		for (NodeGroup mergeGroup : mergeGroups) {
			newGroup = newGroup.merge(mergeGroup);
		}

		// Register as group of the nodes.
		for (Node node : newGroup.nodes) {
			state.nodeGroups.put(node, newGroup);
		}

		return newGroup;
	}
}
