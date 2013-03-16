package bufmgr.policies;

import java.nio.channels.Pipe;

import global.PageId;
import global.SystemDefs;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;

public class LoveHatePolicy extends Policy{
	private MruPolicy mru = new MruPolicy();
	private LruPolicy lru = new LruPolicy();
	
	
	@Override
	public void replaceCand(PageId pid, long idx){
		try {
			if(SystemDefs.JavabaseBM.pageDescriptor(pid).isLoved()){
				mru.replaceCand(pid, idx);
			}else
				lru.replaceCand(pid, idx);
		} catch (HashEntryNotFoundExcpetion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public PageId getUnPinned() throws BufferPoolExceededException,
			HashEntryNotFoundExcpetion {
		PageId currPageId = null;
		boolean f = false;
		
		while(!mru.isEmpty()){
			currPageId = mru.getUnPinned();
			
			if(!SystemDefs.JavabaseBM.pageDescriptor(currPageId).isLoved()){
				f = true;
				break;
			}
		}
		
		if(f)
			return currPageId;
		else{
			while(!mru.isEmpty()){
				currPageId = mru.getUnPinned();
				
				if(SystemDefs.JavabaseBM.pageDescriptor(currPageId).isLoved()){
					f = true;
					break;
				}
			}
			if(f)
				return currPageId;
			else
				throw new BufferPoolExceededException();
		}
	}
	
}
