package bufmgr.policies;

import java.util.LinkedList;
import java.util.Queue;

import global.PageId;
import global.SystemDefs;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class LruPolicy extends Policy{
	Queue<PageId> unPinnedQueue = new LinkedList<PageId>();
	
	
	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedQueue.add(pid);
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		PageId currPageId = null;
		boolean f = false;
		
		while(!unPinnedQueue.isEmpty()){
			currPageId = unPinnedQueue.poll();
			
			if(SystemDefs.JavabaseBM.isZeroPin(currPageId)){
				f = true;
				break;
			}
			
		}
		if(!f)
			throw new BufferPoolExceededException();
		else
			return currPageId;
	}

}
