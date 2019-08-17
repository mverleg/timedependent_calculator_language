package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

public final class EvalOrder implements Iterable<NodeCycleGroup> {

	@Nonnull
	private final List<NodeCycleGroup> reverseEvalList;

	public EvalOrder() {
		this.reverseEvalList = new ArrayList<>();
	}

	public void add(@Nonnull NodeCycleGroup group) {
		reverseEvalList.add(group);
	}

	public boolean forwardOrder() {
		return reverseEvalList.size() == 1;
	}

	@Override
	public Iterator<NodeCycleGroup> iterator() {
		return new EvalOrderIterator(reverseEvalList);
	}

	private static class EvalOrderIterator implements Iterator<NodeCycleGroup> {

		@Nonnull
		private final List<NodeCycleGroup> reverseEvalList;
		private int i;

		private EvalOrderIterator(@Nonnull List<NodeCycleGroup> reverseEvalList) {
			this.reverseEvalList = reverseEvalList;
			this.i = reverseEvalList.size() - 1;
		}

		@Override
		public boolean hasNext() {
			return i >= 0;
		}

		@Override
		public NodeCycleGroup next() {
			NodeCycleGroup item = reverseEvalList.get(i);
			i -= 1;
			return item;
		}
	}
}
