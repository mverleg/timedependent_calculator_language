package nl.markv.tdcl.parse;

import java.util.Objects;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;

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
		return Objects.hash(node);
	}
}
