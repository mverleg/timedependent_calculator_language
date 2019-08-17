package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

public final class NodeCycleGroup implements NodeGroup {

	private static int nextIdentity;
	public final int identity;

	@Nonnull
	public final List<Node> nodes;

	public final boolean canDownwards;
	public final boolean canUpwards;

	public NodeCycleGroup(
			@Nonnull List<Node> nodes,
			boolean canDownwards,
			boolean canUpwards
	) {
		if (nodes.size() == 0) {
			throw new IllegalArgumentException("Cannot create a group without any nodes.");
		}
		this.nodes = nodes;
		this.canDownwards = canDownwards;
		this.canUpwards = canUpwards;
		identity = nextIdentity++;
	}

	public boolean hasConflict() {
		if (!canDownwards && !canUpwards) {
			return true;
		}
		if (canDownwards && canUpwards && nodes.size() > 1) {
			// ("Groups of more than 1 node must have an order restraint. " +
			// "This is the case because only a cycle with only 'current' dependencies (no 'prev' or " +
			// "'next') would be unrestrained. But such a cycle would be cyclic and impossible to compute.");
			return true;
		}
		return false;
	}

	@Nonnull
	public NodeCycleGroup merge(@Nonnull NodeCycleGroup mergeGroup) {

		ArrayList<Node> mergedNodes = new ArrayList<>(this.nodes);
		mergedNodes.addAll(mergeGroup.nodes);
		return new NodeCycleGroup(
				mergedNodes,
				this.canDownwards && mergeGroup.canDownwards,
				this.canUpwards && mergeGroup.canUpwards
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NodeCycleGroup nodeCycleGroup = (NodeCycleGroup) o;
		return identity == nodeCycleGroup.identity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identity);
	}
}
