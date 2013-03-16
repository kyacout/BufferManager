package bufmgr.policies;

import java.util.HashMap;
import java.util.PriorityQueue;

import global.PageId;
import global.SystemDefs;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class FifoPolicy extends Policy {
	static class Node implements Comparable<Node> {
		Long idx;
		PageId pid;

		public Node(PageId p, long i) {
			this.idx = i;
			this.pid = p;
		}

		@Override
		public int compareTo(Node o) {
			return this.idx.compareTo(o.idx);
		}

	}

	private PriorityQueue<Node> unPinnedPQ = new PriorityQueue<FifoPolicy.Node>();
	HashMap<PageId, Integer> numOfOcc = new HashMap<PageId, Integer>();

	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedPQ.add(new Node(pid, idx));
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		Node currPageId = null;
		boolean f = false;

		while (!unPinnedPQ.isEmpty()) {
			currPageId = unPinnedPQ.poll();

			if (numOfOcc.get(currPageId) == 1) {
				if (SystemDefs.JavabaseBM.pageDescriptor(currPageId.pid)
						.isZeroPin()) {
					f = true;
					break;
				}
			}
			numOfOcc.put(currPageId.pid, numOfOcc.get(currPageId) - 1);

		}
		if (!f)
			throw new BufferPoolExceededException();
		else
			return currPageId.pid;
	}

	@Override
	public int numOfUnPinned() {
		int counter = 0;
		for (Integer value : numOfOcc.values())
			if (value > 0)
				counter++;

		return counter;
	}

	@Override
	public boolean isEmpty() {
		return unPinnedPQ.isEmpty();
	}

}
