package nl.markv.tdcl.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

public final class EvalOrder implements Iterable<NodeGroup> {

	@Nonnull
	private final List<NodeGroup> reverseEvalList;

	public EvalOrder() {
		this.reverseEvalList = new ArrayList<>();
	}

	public void add(@Nonnull NodeGroup group) {
		reverseEvalList.add(group);
	}

	public boolean forwardOrder() {
		return reverseEvalList.size() == 1;
	}

	@Override
	public Iterator<NodeGroup> iterator() {
		return new EvalOrderIterator(reverseEvalList);
	}

	private static class EvalOrderIterator implements Iterator<NodeGroup> {

		@Nonnull
		private final List<NodeGroup> reverseEvalList;
		private int i;

		private EvalOrderIterator(@Nonnull List<NodeGroup> reverseEvalList) {
			this.reverseEvalList = reverseEvalList;
			this.i = reverseEvalList.size() - 1;
		}

		@Override
		public boolean hasNext() {
			return i >= 0;
		}

		@Override
		public NodeGroup next() {
			NodeGroup item = reverseEvalList.get(i);
			i -= 1;
			return item;
		}
	}
}
