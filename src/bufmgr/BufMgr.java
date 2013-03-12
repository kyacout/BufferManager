package bufmgr;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import diskmgr.DB;
import diskmgr.DiskMgrException;
import diskmgr.FileIOException;
import diskmgr.InvalidPageNumberException;
import diskmgr.InvalidRunSizeException;
import diskmgr.Page;
import global.PageId;

public class BufMgr {

	private Page[] frames;
	private BufferDescriptor[] descriptors;
	private HashMap<PageId, Integer> pagesUsed;
	private LinkedList<Integer> hatedMru;
	private LinkedList<Integer> lovedLru;
	private DB dataBase;
	private int p;

	/**
	 * Create the BufMgr object Allocate pages (frames) for the buffer pool in
	 * main memory and make the buffer manager aware that the replacement policy
	 * is specified by replaceArg (i.e. FIFO, LRU, MRU, love/hate)
	 * 
	 * @param numbufs
	 *            number of buffers in the buffer pool
	 * @param replaceArg
	 *            name of the buffer replacement policy
	 */
	public BufMgr(int numBufs, String replaceArg) {
		frames = new Page[numBufs];
		descriptors = new BufferDescriptor[numBufs];
		pagesUsed = new HashMap<PageId, Integer>();
		hatedMru = new LinkedList<Integer>();
		lovedLru = new LinkedList<Integer>();
		dataBase = new DB();
		p = 0;
	}

	/**
	 * Pin a page First check if this page is already in the buffer pool. If it
	 * is, increment the pin_count and return pointer to this page. If the
	 * pin_count was 0 before the call, the page was a replacement candidate,
	 * but is no longer a candidate. If the page is not in the pool, choose a
	 * frame (from the set of replacement candidates) to hold this page, read
	 * the page (using the appropriate method from diskmgr package) and pin it.
	 * Also, must write out the old page in chosen frame if it is dirty before
	 * reading new page. (You can assume that emptyPage == false for this
	 * assignment.)
	 * 
	 * @param pgid
	 *            page number in the minibase.
	 * @param page
	 *            the pointer point to the page.
	 * @param emptyPage
	 *            true (empty page), false (non-empty page).
	 * @throws IOException
	 * @throws FileIOException
	 * @throws InvalidPageNumberException
	 * @throws BufferPoolExceededException
	 */
	public void pinPage(PageId pgid, Page page, boolean emptyPage, boolean loved)
			throws InvalidPageNumberException, FileIOException, IOException,
			BufferPoolExceededException {
		// If page is found in buffer.
		if (pagesUsed.containsKey(pgid)) {
			int frame = pagesUsed.get(pgid); // The position of the frame from
												// the hashmap.

			// If the pin count is zero, remove it from the replacement list.
			if (descriptors[frame].isZeroPin()) {
				if (descriptors[frame].isLoved())
					lovedLru.remove(pgid.pid);
				else
					hatedMru.remove(pgid.pid);
			}

			// update the frame, and make the page reference the page with the
			// given id.
			descriptors[frame].incrementPin_count(loved);
			page = frames[frame];
		}

		// If the page is not found in the buffer.
		else {
			// The frame to replace.
			int frame;
			
			if (p < frames.length)
				frames[p++]

			// Remove the old frame, and replace it with the new frame. Flush
			// the page if dirty.
			if (descriptors[frame].isDirty())
				flushPage(new PageId(frame));
			pagesUsed.remove(frame);

			dataBase.read_page(pgid, page);
			frames[frame] = page;
			descriptors[frame] = new BufferDescriptor(pgid, loved);
			pagesUsed.put(pgid, frame);
		}
	}

	/**
	 * Unpin a page specified by a pageId. This method should be called with
	 * dirty == true if the client has modified the page. If so, this call
	 * should set the dirty bit for this frame. Further, if pin_count > 0, this
	 * method should decrement it. If pin_count = 0 before this call, throw an
	 * excpetion to report error. (for testing purposes, we ask you to throw an
	 * exception named PageUnpinnedExcpetion in case of error.)
	 * 
	 * @param pgid
	 *            page number in the minibase
	 * @param dirty
	 *            the dirty bit of the frame.
	 * @throws HashEntryNotFoundExcpetion
	 */
	public void unpinPage(PageId pgid, boolean dirty, boolean loved)
			throws PageUnpinnedExcpetion, HashEntryNotFoundExcpetion {

		if (!pagesUsed.containsKey(pgid.pid))
			throw new HashEntryNotFoundExcpetion();

		int frame = pagesUsed.get(pgid.pid);
		descriptors[frame].decrementPin_count(dirty);

		if (descriptors[frame].isZeroPin()) {
			if (descriptors[frame].isLoved())
				lovedLru.add(pgid.pid);
			else
				hatedMru.add(pgid.pid);
		}
	}

	/**
	 * Allocate new page(s). * Call DB Object to allocate a run of new pages and
	 * find a frame in the buffer pool for the first page and pin it. (This call
	 * allows a client to allocate pages on disk.) If buffer is full, i.e. you
	 * can't find a frame for the first page, ask DB to deallocate all these
	 * pages, and return null.
	 * 
	 * @param firstPage
	 *            the address of the first page.
	 * @param howmany
	 *            total number of allocated new pages.
	 * 
	 * @return the first page id of the new pages. null, if error.
	 */
	public PageId newPage(Page firstPage, int howmany) {
		return null;
	}

	/**
	 * This method should be called to delete a page that is on disk. This
	 * routine must call the method in diskmgr package to deallocate the page.
	 * 
	 * @param pgid
	 *            the page number in the database.
	 * @throws IOException
	 * @throws DiskMgrException
	 * @throws FileIOException
	 * @throws InvalidPageNumberException
	 * @throws InvalidRunSizeException
	 */
	public void freePage(PageId pgid) throws InvalidRunSizeException,
			InvalidPageNumberException, FileIOException, DiskMgrException,
			IOException {
		dataBase.deallocate_page(pgid);

		int frame = pagesUsed.get(pgid);

		if (descriptors[frame].isDirty())
			flushPage(new PageId(frame));
		pagesUsed.remove(frame);
	}

	/**
	 * Used to flush a particular page of the buffer pool to disk. This method
	 * calls the write_page method of the diskmgr package.
	 * 
	 * @param pgid
	 *            the page number in the database.
	 * @throws IOException
	 * @throws FileIOException
	 * @throws InvalidPageNumberException
	 */
	public void flushPage(PageId pgid) throws InvalidPageNumberException,
			FileIOException, IOException {
		int frame = pagesUsed.get(pgid);
		dataBase.write_page(pgid, frames[frame]);
	}
	
	public int getNumUnpinnedBuffers() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isZeroPin(PageId pid) throws HashEntryNotFoundExcpetion {
		if (!pagesUsed.containsKey(pid))
			throw new HashEntryNotFoundExcpetion();
		
		return descriptors[pagesUsed.get(pid)].isZeroPin();
	}
}
