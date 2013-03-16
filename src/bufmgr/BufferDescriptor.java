package bufmgr;

import global.PageId;

public class BufferDescriptor {
	private PageId pagenumber;
	private int pin_count;
	private boolean dirtybit;
	private boolean isLoved;
	private long counter;
	
	BufferDescriptor(PageId pagenumber, long counter) {
		pin_count = 1;
		this.pagenumber = pagenumber;
		isLoved = false;
		this.counter = counter;
	}

	public PageId getPagenumber() {
		return pagenumber;
	}

	public boolean isZeroPin() {
		return pin_count == 0 ? true : false;
	}

	public void incrementPin_count() {
		pin_count++;
	}
	
	public void decrementPin_count(boolean dirty, boolean loved) throws PageUnpinnedExcpetion {
		if (isZeroPin())
			throw new PageUnpinnedExcpetion();
		
		pin_count--;
		dirtybit |= dirty;
		isLoved |= loved;
	}

	public boolean isDirty() {
		return dirtybit;
	}

	public boolean isLoved() {
		return isLoved;
	}
	
	public long getCounter() {
		return counter;
	}
}
