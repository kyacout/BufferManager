package bufmgr.policies;

import bufmgr.BufMgr;
import global.PageId;
import diskmgr.Page;

public abstract class Policy {
	static enum policies  {
		LOVE_HATE,
		MRU,
		LRU,
		FIFo;
	}
	
	abstract void replaceCand(PageId pid);
	
	abstract PageId getUnPinned();
	
	
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
