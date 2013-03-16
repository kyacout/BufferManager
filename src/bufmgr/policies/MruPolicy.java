package bufmgr.policies;

import global.PageId;
import global.SystemDefs;

import java.util.HashMap;
import java.util.Stack;

import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class MruPolicy extends Policy{
	private Stack<PageId> unPinnedStack = new Stack<PageId>();
	HashMap<PageId, Integer> numOfOcc = new HashMap<PageId, Integer>();

	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedStack.push(pid);
		if(numOfOcc.containsKey(pid))
			numOfOcc.put(pid, numOfOcc.get(pid)+ 1);
		else
			numOfOcc.put(pid, 1);
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException, HashEntryNotFoundExcpetion{
		PageId currPageId = null;
		boolean f = false;
		
		while(!unPinnedStack.isEmpty()){
			currPageId = unPinnedStack.pop();
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
		return unPinnedStack.isEmpty();
	}
	
	
}
