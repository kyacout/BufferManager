package bufmgr.policies;

import bufmgr.BufMgr;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundExcpetion;
import global.PageId;

public abstract class Policy {
	static enum policies  {
		LOVE_HATE,
		MRU,
		LRU,
		FIFo;
	}
	public abstract void replaceCand(PageId pid, long idx);
	
	public abstract PageId getUnPinned() throws BufferPoolExceededException, HashEntryNotFoundExcpetion;

	public Policy getPolicy(String p) {
		if(p.matches("love hate"))
			return new LoveHatePolicy();
		else if(p.matches("mru"))
			return new MruPolicy();
		else if(p.matches("lru"))
			return new LruPolicy();
		else if(p.matches("fifo"))
			return new FifoPolicy();
		else
			throw new IllegalArgumentException();
	}
}
