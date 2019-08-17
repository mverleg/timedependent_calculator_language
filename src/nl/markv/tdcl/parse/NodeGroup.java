package nl.markv.tdcl.parse;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Order;

import nl.markv.tdcl.data.Node;

public interface NodeGroup {
	@Nonnull
	Collection<Node> nodes();

	int size();

	enum Order {
		Up,
		Down,
		Any,
		Conflict,
	}

	@Nonnull
	Order order();

	boolean hasConflict();
}
