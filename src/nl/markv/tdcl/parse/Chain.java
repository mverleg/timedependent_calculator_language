package nl.markv.tdcl.parse;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;

/**
 * A chain of dependencies, to report cycles.
 */
public class Chain {

	@Nullable
	public final Chain parent;
	@Nonnull
	public final Dependency dependency;

	public Chain(
			@Nullable Chain parent,
			@Nonnull Dependency dependency
	) {
		this.parent = parent;
		this.dependency = dependency;
	}

	@Nonnull
	public Optional<Dependency> findNode(@Nonnull Node node) {
		if (dependency.node.equals(node)) {
			return Optional.of(dependency);
		}
		if (parent == null) {
			return Optional.empty();
		}
		return parent.findNode(node);
	}
}
