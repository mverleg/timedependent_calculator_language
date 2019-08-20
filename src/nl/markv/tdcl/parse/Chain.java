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
 *
 * It's actually a tree, but the thing that matters is the path from the current leaf to the root, which is a chain.
 *
 * The chain should be cut off upon encountering a duplicate, but the first and last element could be the same.
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
//		if (parent != null && dependency.node.equals(parent.dependency.node)) {
//			throw new IllegalArgumentException("Chain node is the same as parent node");
//		}
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
	public Optional<List<Dependency>> findUptoNode(@Nonnull Node uptoNode) {

		List<Dependency> uptoList = new ArrayList<>();
		Chain current = this;
		while (true) {
			uptoList.add(current.dependency);
			current = current.parent;
			if (current == null) {
				return Optional.empty();
			}
			if (current.dependency.node.equals(uptoNode)) {
				if (!current.dependency.node.equals(dependency.node)) {
					uptoList.add(current.dependency);
				}
				break;
			}
		}

		return Optional.of(uptoList);
	}

	@Nonnull
	public String toString() {

		var isFirst = true;
		var text = new StringBuilder();
		Chain current = this;
		while (current != null) {
			if (isFirst) {
				isFirst = false;
			} else {
				text.append(" <- ");
			}
			text.append(current.dependency);
			current = current.parent;
		}

		return text.toString();
	}
}
