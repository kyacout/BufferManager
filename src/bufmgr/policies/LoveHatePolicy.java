package bufmgr.policies;

import global.PageId;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class LoveHatePolicy extends Policy{
	MruPolicy mru = new MruPolicy();
	LruPolicy lru = new LruPolicy();
	
	
	@Override
	public void replaceCand(PageId pid, long idx) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		// TODO Auto-generated method stub
		return null;
	}
	
}
