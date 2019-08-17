package nl.markv.tdcl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class CollectionUtil {

	/**
	 * Produces a list that contains the items in the original in the same order, but with any
	 * duplicates after the first instance removed.
	 * <p>
	 * Items should implement {@link Object#equals(Object)} and {@link Object#hashCode()}.
	 */
	@Nonnull
	public static <T> List<T> deduplicate(@Nonnull Collection<T> original) {

		Set<T> seen = new HashSet<>();
		List<T> unique = new ArrayList<>();

		for (T item : original) {
			if (seen.contains(item)) {
				continue;
			}
			seen.add(item);
			unique.add(item);
		}

		return unique;
	}

	@Nonnull
	public static <T> List<T> listOf(@Nonnull T... items) {
		List<T> li = new ArrayList<>(items.length);
		for (T item : items) {
			li.add(item);
		}
		return li;
	}

	@Nonnull
	public static <T> Set<T> setOf(@Nonnull T... items) {
		Set<T> set = new HashSet<>(items.length);
		for (T item : items) {
			set.add(item);
		}
		return set;
	}
}
