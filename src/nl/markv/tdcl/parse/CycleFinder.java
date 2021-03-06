package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Dependency.Direction;
import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.util.CollectionUtil.setOf;

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
		final Set<Node> everSeenNodes;
		@Nonnull
		final Map<Node, CycleNodeGroup> cycleNodeGroups;

		private CycleSearchState() {
			cycleNodeGroups = new HashMap<>();
			everSeenNodes = new HashSet<>();
		}

		private CycleSearchState(
				@Nonnull Set<Node> everSeenNodes,
				@Nonnull Map<Node, CycleNodeGroup> cycleNodeGroups
		) {
			this.everSeenNodes = everSeenNodes;
			this.cycleNodeGroups = cycleNodeGroups;
		}

		@Nonnull
		private CycleSearchState refresh() {
			return new CycleSearchState(this.everSeenNodes, this.cycleNodeGroups);
		}
	}

	@Nonnull
	public static Set<NodeGroup> distributeIntoCycles(@Nonnull List<Node> outputNodes) {
		// There is a Set for quickly checking whether there is a cycle,
		// and a Cycle tree/graph for finding the shortest cycle including
		// dependencies in case the Set says there is one.

		Set<NodeGroup> nodeGroups;
		Set<Node> stopOnNodes = setOf();

		// Find all cycles of nodes that refer to themselves.
		CycleSearchState state = new CycleSearchState();
		for (Node node : outputNodes) {
			// Create a fresh state, including cycles but excluding known nodes.
			state = state.refresh();

			// Recursively find cycles in dependencies.
			findDependencyCycles(node, new Chain(null, cur(node)), state, stopOnNodes);

			// Add this start node, so that next finals quit when encountering it.
			stopOnNodes.add(node);
		}
		nodeGroups = new HashSet<>(state.cycleNodeGroups.values());

		// Create single-member groups for any nodes not in a cycle.
		List<SingleNodeGroup> singleGroups = createSingleNodeGroupsForLeftoverNodes(state);
		nodeGroups.addAll(singleGroups);

		return nodeGroups;
	}

	@Nonnull
	private static void findDependencyCycles(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull CycleSearchState state,
			@Nonnull Set<Node> stopOnNodes
	) {
		// Check whether dependencies are already known.
		if (state.recursiveDeps.get(currentNode) != null) {

			makeOrJoinGroup(currentNode, chain, state);
			return;
		}
		Set<Node> currentNodeRecDeps = new HashSet<>();
		state.recursiveDeps.put(currentNode, currentNodeRecDeps);
		state.everSeenNodes.add(currentNode);

		for (Dependency dep : currentNode.directDependencies) {

			// Find dependencies of the processed node.
			findDependencyCycles(
					dep.node,
					new Chain(chain, dep),
					state,
					stopOnNodes
			);

			// Add direct dependency.
			currentNodeRecDeps.add(dep.node);

			// Add dependencies of the processed node to this node.
			Set<Node> indirectDeps = state.recursiveDeps.get(dep.node);
			currentNodeRecDeps.addAll(indirectDeps);
		}
	}

	@Nullable
	@SuppressWarnings("UnusedReturnValue")
	private static CycleNodeGroup makeOrJoinGroup(
			@Nonnull Node currentNode,
			@Nonnull Chain chain,
			@Nonnull CycleSearchState state
	) {
		// Find the cycle nodes.
		Optional<List<Dependency>> cycleOption = chain.findUptoNode(currentNode);
		if (cycleOption.isEmpty()) {
			return null;
		}
		List<Dependency> cycle = cycleOption.get();

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
		Set<Node> nodes = cycle.stream()
				.map(dep -> dep.node)
				.collect(Collectors.toSet());
		CycleNodeGroup newGroup = new CycleNodeGroup(
				nodes, canDownwards, canUpwards);

		// Find any existing groups to merge.
		Set<CycleNodeGroup> mergeGroups = nodes.stream()
				.map(node -> state.cycleNodeGroups.get(node))
				.filter(grp -> grp != null)
				.collect(Collectors.toSet());
		for (CycleNodeGroup mergeGroup : mergeGroups) {
			newGroup = newGroup.merge(mergeGroup);
		}

		// Register as group of the nodes.
		for (Node node : newGroup.cycleNodes) {
			state.cycleNodeGroups.put(node, newGroup);
		}

		return newGroup;
	}

	@Nonnull
	private static List<SingleNodeGroup> createSingleNodeGroupsForLeftoverNodes(
			@Nonnull CycleSearchState state
	) {

		List<SingleNodeGroup> singleNodeGroups = new ArrayList<>();

		for (Node node : state.everSeenNodes) {

			// Only continue if the node is not in any group.
			NodeGroup existingGroup = state.cycleNodeGroups.get(node);
			if (existingGroup != null) {
				continue;
			}

			// Add the node to a single-node group.
			SingleNodeGroup newGroup = new SingleNodeGroup(node);
			singleNodeGroups.add(newGroup);
		}

		return singleNodeGroups;
	}
}
