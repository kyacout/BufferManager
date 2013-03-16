package bufmgr.policies;

import java.util.PriorityQueue;

import global.PageId;
import global.SystemDefs;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;


public class FifoPolicy extends Policy{
	static class Node implements Comparable<Node>{
		Long idx;
		PageId pid;
		
		public Node(PageId p , long i) {
			this.idx = i;
			this.pid = p;
		}
		@Override
		public int compareTo(Node o) {
			return this.idx.compareTo(o.idx);
		}
		
	}
	
	PriorityQueue<Node> unPinnedPQ = new PriorityQueue<FifoPolicy.Node>();
	
	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedPQ.add(new Node(pid, idx));
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		Node currPageId = null;
		boolean f = false;
		
		while(!unPinnedPQ.isEmpty()){
			currPageId = unPinnedPQ.poll();
			
			if(SystemDefs.JavabaseBM.isZeroPin(currPageId.pid)){
				f = true;
				break;
			}
			
		}
		if(!f)
			throw new BufferPoolExceededException();
		else
			return currPageId.pid;
	}

}
