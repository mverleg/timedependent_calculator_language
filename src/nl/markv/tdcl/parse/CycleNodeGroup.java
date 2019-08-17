package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

public final class CycleNodeGroup implements NodeGroup {

	@Nonnull
	public final List<Node> nodes;

	public final boolean canDownwards;
	public final boolean canUpwards;

	public CycleNodeGroup(
			@Nonnull List<Node> nodes,
			boolean canDownwards,
			boolean canUpwards
	) {
		if (nodes.size() == 0) {
			throw new IllegalArgumentException("Cannot create a group without any nodes.");
		}
		if (new HashSet<>(nodes).size() != nodes.size()) {
			throw new IllegalArgumentException("A node cycle group should not contain duplicates.");
		}
		this.nodes = nodes;
		this.canDownwards = canDownwards;
		this.canUpwards = canUpwards;
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
	public CycleNodeGroup merge(@Nonnull CycleNodeGroup mergeGroup) {

		ArrayList<Node> mergedNodes = new ArrayList<>(this.nodes);
		mergedNodes.addAll(mergeGroup.nodes);
		return new CycleNodeGroup(
				mergedNodes,
				this.canDownwards && mergeGroup.canDownwards,
				this.canUpwards && mergeGroup.canUpwards
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CycleNodeGroup that = (CycleNodeGroup) o;
		return canDownwards == that.canDownwards &&
				canUpwards == that.canUpwards &&
				nodes.equals(that.nodes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes, canDownwards, canUpwards);
	}
}
