package nl.markv.tdcl.parse;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.util.CollectionUtil.listOf;

public final class SingleNodeGroup implements NodeGroup {

	@Nonnull
	public final Node node;

	public SingleNodeGroup(@Nonnull Node node) {
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SingleNodeGroup that = (SingleNodeGroup) o;
		return node.equals(that.node);
	}

	@Override
	public int hashCode() {
		return Objects.hash(node) + 93457;
	}

	@Nonnull
	@Override
	public Collection<Node> nodes() {
		return listOf(node);
	}

	@Override
	public int size() {
		return 1;
	}

	@Nonnull
	@Override
	public Order order() {
		return Order.Any;
	}

	@Override
	public boolean hasConflict() {
		return false;
	}

	@Override
	public String toString() {
		return "SingleNodeGroup: " + node;
	}
}
