package bufmgr;

import global.PageId;

public class BufferDescriptor {
	private PageId pagenumber;
	private int pin_count;
	private boolean dirtybit;
	private boolean isLoved;
	
	BufferDescriptor(PageId pagenumber, boolean loved) {
		pin_count = 1;
		this.pagenumber = pagenumber;
		isLoved = loved;
	}

	public PageId getPagenumber() {
		return pagenumber;
	}

	public boolean isZeroPin() {
		return pin_count == 0 ? true : false;
	}

	public void incrementPin_count(boolean loved) {
		if (isZeroPin())
			isLoved = loved;
		else
			isLoved |= loved;
		pin_count++;
	}
	
	public void decrementPin_count(boolean dirty) throws PageUnpinnedExcpetion {
		if (isZeroPin())
			throw new PageUnpinnedExcpetion();
		
		pin_count--;
		dirtybit |= dirty;
	}

	public boolean isDirty() {
		return dirtybit;
	}

	public boolean isLoved() {
		return isLoved;
	}
}
