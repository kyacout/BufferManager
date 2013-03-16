package bufmgr.policies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import global.PageId;
import global.SystemDefs;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class LruPolicy extends Policy{
	private Queue<PageId> unPinnedQueue = new LinkedList<PageId>();
	HashMap<PageId, Integer> numOfOcc = new HashMap<PageId, Integer>();

	
	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedQueue.add(pid);
		if(numOfOcc.containsKey(pid))
			numOfOcc.put(pid, numOfOcc.get(pid)+ 1);
		else
			numOfOcc.put(pid, 1);
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		PageId currPageId = null;
		boolean f = false;
		
		while(!unPinnedQueue.isEmpty()){
			currPageId = unPinnedQueue.poll();
			
			if(numOfOcc.get(currPageId) == 1){
				if(SystemDefs.JavabaseBM.pageDescriptor(currPageId).isZeroPin()){
					f = true;
					break;
				}
			}
			numOfOcc.put(currPageId, numOfOcc.get(currPageId) - 1);
			
			
		}
		if(!f)
			throw new BufferPoolExceededException();
		else
			return currPageId;
	}
	
	public boolean isEmpty(){
		return unPinnedQueue.isEmpty();
	}

}
