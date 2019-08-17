package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.List;
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

	@Nonnull
	public List<Dependency> findUptoNode(@Nonnull Node node) {

		List<Dependency> uptoList = new ArrayList<>();
		Chain current = this;
		while (true) {
			uptoList.add(current.dependency);
			current = current.parent;
			if (current == null) {
				break;
			}
			if (current.dependency.node.equals(node)) {
				uptoList.add(current.dependency);
				break;
			}
		}

		return uptoList;
	}
}
