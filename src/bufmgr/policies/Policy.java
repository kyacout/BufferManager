package bufmgr.policies;

import bufmgr.BufferPoolExceededException;
import global.PageId;

public abstract class Policy {
	static enum policies  {
		LOVE_HATE,
		MRU,
		LRU,
		FIFo;
	}
	
	public abstract void replaceCand(PageId pid, int idx);
	
	public abstract PageId getUnPinned() throws BufferPoolExceededException;
	
	
	public Policy getPolicy(policies p) {
		switch (p) {
		case LOVE_HATE:
			return new LoveHatePolicy();
			
		case MRU:
			return new MruPolicy();
			
		case LRU:
			return new LruPolicy();
			
		case FIFo:
			return new FifoPolicy();

		}
		throw new IllegalArgumentException();
	}
}
