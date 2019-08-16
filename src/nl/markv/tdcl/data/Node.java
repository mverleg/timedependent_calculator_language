package nl.markv.tdcl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

public final class Node {

	@Nonnull
	public final String name;
	@Nonnull
	public final List<Dependency> dependencies;

	public Node(
			@Nonnull String name,
			@Nonnull Dependency... dependencies
	) {
		this.name = name;
		this.dependencies = List.of(dependencies);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return Objects.equals(name, node.name) &&
				Objects.equals(dependencies, node.dependencies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, dependencies);
	}

	@Nonnull
	public Node selfRef(Dependency.Direction direction) {
		List<Dependency> newDependencies = new ArrayList<>(dependencies);
		newDependencies.add(new Dependency(direction, this));
		return new Node(name, newDependencies.toArray(new Dependency[0]));
	}
}
