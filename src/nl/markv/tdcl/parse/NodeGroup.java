package nl.markv.tdcl.parse;

import java.util.List;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

public final class NodeGroup {

//	public enum Order {
//		Unrestrained,
//		Downwards,
//		Upwards,
//	}

	@Nonnull
	public final List<Node> nodes;
//	@Nonnull
//	public final Order order;

	public final boolean canDownwards;
	public final boolean canUpwards;

	private NodeGroup(
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
}
