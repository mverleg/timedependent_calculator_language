package nl.markv.tdcl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Node {

	@Nonnull
	public final String name;
	@Nonnull
	public final List<Dependency> directDependencies;

	public Node(
			@Nonnull String name,
			@Nonnull Dependency... dependencies
	) {
		this.name = name.intern();
		this.directDependencies = List.of(dependencies);
	}

	@Nonnull
	public Node selfRef(Dependency.Direction direction) {
		List<Dependency> newDependencies = new ArrayList<>(directDependencies);
		newDependencies.add(new Dependency(direction, this));
		return new Node(name, newDependencies.toArray(new Dependency[0]));
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return name.equals(node.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
