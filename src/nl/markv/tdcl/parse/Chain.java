package nl.markv.tdcl.parse;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Dependency;

/**
 * A chain of dependencies, to report cycles.
 */
public class Chain {

	@Nonnull
	public final Chain parent;
	@Nonnull
	public final Dependency dependency;

	private Chain(
			@Nonnull Chain parent,
			@Nonnull Dependency dependency
	) {
		this.parent = parent;
		this.dependency = dependency;
	}
}
