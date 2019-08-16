package nl.markv.tdcl.parse;

import java.util.List;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

//TODO @mark: TEMPORARY! REMOVE THIS!
public final class NodeGroup {

	public enum Order {
		Downwards,
		Upwards,
	}

	@Nonnull
	public final List<Node> nodes;
	@Nonnull
	public final Order order;

	private NodeGroup(
			@Nonnull List<Node> nodes,
			@Nonnull Order order
	) {
		this.nodes = nodes;
		this.order = order;
	}
}
