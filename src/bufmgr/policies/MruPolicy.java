package bufmgr.policies;

import global.PageId;
import global.SystemDefs;

import java.util.Stack;

import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class MruPolicy extends Policy{
	Stack<PageId> unPinnedStack = new Stack<PageId>();

	@Override
	public void replaceCand(PageId pid, long idx) {
		unPinnedStack.push(pid);
	}

	@Override
	public PageId getUnPinned() throws BufferPoolExceededException, HashEntryNotFoundExcpetion{
		PageId currPageId = null;
		boolean f = false;
		
		while(!unPinnedStack.isEmpty()){
			currPageId = unPinnedStack.pop();
			
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
