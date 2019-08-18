package nl.markv.tdcl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nl.markv.tdcl.util.CollectionUtil.listOf;

public final class Node {

	//TODO: I'd like this to be immutable, but I can't create cycles of immutable objects.

	@Nonnull
	public final String name;
	@Nonnull
	public List<Dependency> directDependencies;

	@Nonnull
	public final int cachedHashCode;

	public Node(
			@Nonnull String name,
			@Nonnull Dependency... dependencies
	) {
		this.name = name.intern();
		this.directDependencies = listOf(dependencies);
		this.cachedHashCode = Objects.hash(name);
	}

	@Nonnull
	public Node selfRef(Dependency.Direction direction) {
		if (direction == Dependency.Direction.Current) {
			throw new IllegalStateException();
		}
		addDependency(new Dependency(direction, this));
		return this;
	}

	public boolean nodeEquals(@Nonnull Node other) {
		return this.cachedHashCode == other.cachedHashCode
				&& name.equals(other.name);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return nodeEquals(node);
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	@Override
	public String toString() {
		return name;
	}

	public void addDependency(@Nonnull Dependency dep) {
		directDependencies.add(dep);
	}
}
