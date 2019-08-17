package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

public final class EvalOrder implements Iterable<CycleNodeGroup> {

	@Nonnull
	private final List<CycleNodeGroup> reverseEvalList;

	public EvalOrder() {
		this.reverseEvalList = new ArrayList<>();
	}

	public void add(@Nonnull CycleNodeGroup group) {
		reverseEvalList.add(group);
	}

	public boolean forwardOrder() {
		return reverseEvalList.size() == 1;
	}

	@Override
	public Iterator<CycleNodeGroup> iterator() {
		return new EvalOrderIterator(reverseEvalList);
	}

	private static class EvalOrderIterator implements Iterator<CycleNodeGroup> {

		@Nonnull
		private final List<CycleNodeGroup> reverseEvalList;
		private int i;

		private EvalOrderIterator(@Nonnull List<CycleNodeGroup> reverseEvalList) {
			this.reverseEvalList = reverseEvalList;
			this.i = reverseEvalList.size() - 1;
		}

		@Override
		public boolean hasNext() {
			return i >= 0;
		}

		@Override
		public CycleNodeGroup next() {
			CycleNodeGroup item = reverseEvalList.get(i);
			i -= 1;
			return item;
		}
	}
}
