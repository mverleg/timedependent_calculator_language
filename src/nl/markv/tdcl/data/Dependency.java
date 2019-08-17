package nl.markv.tdcl.data;

import java.util.Objects;

import javax.annotation.Nonnull;

public final class Dependency {

	public enum Direction {
		Previous(-1),
		Current(0),
		Next(+1),
		;

		public final int rowStep;

		Direction(int rowStep) {
			this.rowStep = rowStep;
		}
	}

	@Nonnull
	public final Direction direction;
	@Nonnull
	public final Node node;

	public Dependency(
			@Nonnull Direction direction,
			@Nonnull Node node
	) {
		this.direction = direction;
		this.node = node;
	}

	public static Dependency prev(@Nonnull Node node) {
		return new Dependency(Direction.Previous, node);
	}

	public static Dependency cur(@Nonnull Node node) {
		return new Dependency(Direction.Current, node);
	}

	public static Dependency next(@Nonnull Node node) {
		return new Dependency(Direction.Next, node);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Dependency that = (Dependency) o;
		return direction == that.direction &&
				Objects.equals(node, that.node);
	}

	@Override
	public int hashCode() {
		return Objects.hash(direction, node);
	}
}
