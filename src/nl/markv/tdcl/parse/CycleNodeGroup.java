package nl.markv.tdcl.parse;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

public final class CycleNodeGroup implements NodeGroup {

	@Nonnull
	public final Set<Node> cycleNodes;

	public final boolean canDownwards;
	public final boolean canUpwards;

	public CycleNodeGroup(
			@Nonnull Set<Node> nodes,
			boolean canDownwards,
			boolean canUpwards
	) {
		if (nodes.size() == 0) {
			throw new IllegalArgumentException("Cannot create a group without any nodes.");
		}
		if (new HashSet<>(nodes).size() != nodes.size()) {
			throw new IllegalArgumentException("A node cycle group should not contain duplicates.");
		}
		this.cycleNodes = nodes;
		this.canDownwards = canDownwards;
		this.canUpwards = canUpwards;
	}

	public boolean hasConflict() {
		if (!canDownwards && !canUpwards) {
			return true;
		}
		if (canDownwards && canUpwards && cycleNodes.size() > 1) {
			// ("Groups of more than 1 node must have an order restraint. " +
			// "This is the case because only a cycle with only 'current' dependencies (no 'prev' or " +
			// "'next') would be unrestrained. But such a cycle would be cyclic and impossible to compute.");
			return true;
		}
		return false;
	}

	@Nonnull
	public CycleNodeGroup merge(@Nonnull CycleNodeGroup mergeGroup) {

		Set<Node> mergedNodes = new HashSet<>(mergeGroup.cycleNodes);
		mergedNodes.addAll(this.cycleNodes);
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
				cycleNodes.equals(that.cycleNodes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cycleNodes, canDownwards, canUpwards);
	}

	@Nonnull
	@Override
	public Collection<Node> nodes() {
		return cycleNodes;
	}

	@Override
	public int size() {
		return cycleNodes.size();
	}

	@Nonnull
	@Override
	public Order order() {
		if (!canUpwards && !canDownwards) {
			return Order.Conflict;
		}
		if (canUpwards && canDownwards) {
			//TODO @mark: how can a cycle not have an order preference? how is it a cycle then?
			return Order.Any;
		}
		if (canUpwards) {
			return Order.Up;
		}
		if (canDownwards) {
			return Order.Down;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		String nodeNames = cycleNodes.stream().map(it -> it.toString()).collect(Collectors.joining(", "));
		return order().name() + " CycleNodeGroup: " + nodeNames;
	}
}
