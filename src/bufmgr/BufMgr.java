package bufmgr;

import global.GlobalConst;
import global.Minibase;
import global.Page;
import global.PageId;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

/**
 * <h3>Minibase Buffer Manager</h3>
 * The buffer manager manages an array of main memory pages.  The array is
 * called the buffer pool, each page is called a frame.  
 * It provides the following services:
 * <ol>
 * <li>Pinning and unpinning disk pages to/from frames
 * <li>Allocating and deallocating runs of disk pages and coordinating this with
 * the buffer pool
 * <li>Flushing pages from the buffer pool
 * <li>Getting relevant data
 * </ol>
 * The buffer manager is used by access methods, heap files, and
 * relational operators.
 */
public class BufMgr implements GlobalConst {

  protected FrameDesc[] frametable; //Describes info about a page.
  protected Page[] bufpool; // Array of pages
  protected HashMap<PageId, FrameDesc> pagemap; //Link between the a Page and a frame
  
  Clock clock;
  
  /**
   * Constructs a buffer manager by initializing member data.  
   * 
   * @param numframes number of frames in the buffer pool
   */
  public BufMgr(int numframes) {

    frametable = new FrameDesc[numframes];
    bufpool = new Page[numFrames];    
    
    //initializes
    for(int x =0; x < numframes; x++)
    {
      frametable[x] = new FramDesc();
      bufpool = new Page();
    }
    
    pagemap = new HashMap<Integer, FrameDesc>();
    clock = new Clock();

  } // public BufMgr(int numframes)

  /**
   * The result of this call is that disk page number pageno should reside in
   * a frame in the buffer pool and have an additional pin assigned to it, 
   * and mempage should refer to the contents of that frame. <br><br>
   * 
   * If disk page pageno is already in the buffer pool, this simply increments 
   * the pin count.  Otherwise, this<br> 
   * <pre>
   *  uses the replacement policy to select a frame to replace
   *  writes the frame's contents to disk if valid and dirty
   *  if (contents == PIN_DISKIO)
   *    read disk page pageno into chosen frame
   *  else (contents == PIN_MEMCPY)
   *    copy mempage into chosen frame
   *  [omitted from the above is maintenance of the frame table and hash map]
   * </pre>   
   * @param pageno identifies the page to pin
   * @param mempage An output parameter referring to the chosen frame.  If
   * contents==PIN_MEMCPY it is also an input parameter which is copied into
   * the chosen frame, see the contents parameter. 
   * @param contents Describes how the contents of the frame are determined.<br>  
   * If PIN_DISKIO, read the page from disk into the frame.<br>  
   * If PIN_MEMCPY, copy mempage into the frame.<br>  
   * If PIN_NOOP, copy nothing into the frame - the frame contents are irrelevant.<br>
   * Note: In the cases of PIN_MEMCPY and PIN_NOOP, disk I/O is avoided.
   * @throws IllegalArgumentException if PIN_MEMCPY and the page is pinned.
   * @throws IllegalStateException if all pages are pinned (i.e. pool is full)
   */
  public void pinPage(PageId pageno, Page mempage, int contents) {

      FrameDesc frame = pagemap.get(pageno);    
      //frame exists
      if (null != frame) 
      {
      // page is already loaded
      frame.incrementPinCount();
    } 
  
      else
      {
        int victim_no;
            victim_no = clock.pick_victim();
                          
        if (-1 != victim_no)
        {       
          FrameDesc victim = hashmap.get(victim_no);
        if (victim != null && victimFrame.isDirty()) 
        {
          Minibase.DiskManager.write_page(victim.getDiskPageNum(), bufpool[victim]);
        }
        if (contents == PIN_DISKIO)
        {
          Page new_page = new Page();
          Minibase.DiskManager.read_page(pageno, new_page);
          bufpool[victim_no].copyPage(new_page);
          mempage.setPage(buffer_pool[victim_no]);
          Minibase.DiskManager.read_page(pageno,bufpool[victim_no]);
          frametable[victim_no].incrementPinCount;  
          frametable[victim_no].setValidDataIndicator(true) 
          frametable[victim_no].setDiskPageNum(pageno.pid); 
          pagemap.remove(victim);
          pagemap.put(pageno.pid, new_page);
          
        }
        
        else if (contents == PIN_MEMCPY)
        {
          
          bufpool[victim_no].copyPage(mempage);
          mempage.setPage(buffer_pool[victim_no]);
          frametable[victim_no].incrementPinCount;  
          frametable[victim_no].setValidDataIndicator(true) 
          frametable[victim_no].setDiskPageNum(pageno.pid); 
          pagemap.put(pageno.pid, new_page);          
        } 
        else if (contents == NOOP)
        {}
        else
        {
          throw new IllegalArgumentException();
        }
        } 
      }
  } // public void pinPage(PageId pageno, Page page, int contents)
  
  /**
   * Unpins a disk page from the buffer pool, decreasing its pin count.
   * 
   * @param pageno identifies the page to unpin
   * @param dirty UNPIN_DIRTY if the page was modified, UNPIN_CLEAN otherwise
   * @throws IllegalArgumentException if the page is not in the buffer pool
   *  or not pinned
   */
  public void unpinPage(PageId pageno, boolean dirty) throws IllegalArgumentException 
  {
    if (!pagemap.containsKey(pageno))
    {
      throw new IllegalArgumentException();
    }
    
    int frame = pagemap.get(pageno);
    frametable[frame].decrementPinCount();
    frametable[frame].setDirtyBit(dirty);
    
    if(frametable[frame].getPinCount() == 0)
    {
      frametab[FrameNum].setRefbit(true);
    }

  } // public void unpinPage(PageId pageno, boolean dirty)
  
  /**
   * Allocates a run of new disk pages and pins the first one in the buffer pool.
   * The pin will be made using PIN_MEMCPY.  Watch out for disk page leaks.
   * 
   * @param firstpg input and output: holds the contents of the first allocated page
   * and refers to the frame where it resides
   * @param run_size input: number of pages to allocate
   * @return page id of the first allocated page
   * @throws IllegalArgumentException if firstpg is already pinned
   * @throws IllegalStateException if all pages are pinned (i.e. pool exceeded)
   */
  public PageId newPage(Page firstpg, int run_size)
  {
    
    if( 0 == getNumUnpinned())
    {
      throw new IllegalStateException();
    }
    
    else
    {   
       PageId new_page =new PageId();
       new_page = Minibase.DiskManager.allocate_page(run_size);
       int frame = pagemap.get(pgid);

       if(-1 != frame && bufpool[frame].getPinCount > 0)
      {
        throw new IllegalArgumentException();
      }
      else
      {
            pinPage(new_page, firstpg, PIN_MEMCPY);
      }
    }
    return new_page;
  }

  /**
   * Deallocates a single page from disk, freeing it from the pool if needed.
   * 
   * @param pageno identifies the page to remove
   * @throws IllegalArgumentException if the page is pinned
   */
  public void freePage(PageId pageno) {

    int frame = pagemap.get(pgid);

    if(-1 != frame && bufpool[frame].getPinCount > 0)
    {
      throw new IllegalArgumentException();
    }
    else
    {
      Minibase.DiskManager.deallocate_page(pageno);
    }

  } // public void freePage(PageId firstid)

  /**
   * Write all valid and dirty frames to disk.
   * Note flushing involves only writing, not unpinning or freeing
   * or the like.
   * 
   */
  public void flushAllFrames() {
  
    // Getting a Set of Key-value pairs
      Set entrySet = pagemap.entrySet();
   
      // Obtaining an iterator for the entry set
      Iterator it = entrySet.iterator();
   
      // Iterate through HashMap entries(Key-Value pairs)
      while(it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         flushPage(me.getKey());

     }
  } // public void flushAllFrames()

  /**
   * Write all valid and dirty frames to disk.
   * Note flushing involves only writing, not unpinning or freeing
   * or the like.
   * 
   */
  public void flushAllPages() {

    flushAllFrames();
  } // public void flushAllPages()

  /**
   * Write a page in the buffer pool to disk, if dirty.
   * 
   * @throws IllegalArgumentException if the page is not in the buffer pool
   */
  public void flushPage(PageId pageno) 
  {
      int frame = pagemap.get(pageno);
      
      if(-1 != frame)
      {
       if(bufpool[frame].getDirtyBit())
       {
           Minibase.DiskManager.write_page(pageno, bufpool[frame]);
       }
      }
      else
      {
        throw new IllegalArgumentException();
      }
  }

  
  
   /**
   * Gets the total number of buffer frames.
   */
  public int getNumFrames() {
    return bufpool.length;
  }

  /**
   * Gets the total number of unpinned buffer frames.
   */
  public int getNumUnpinned() {
    int unpinned_count = 0;
    for (int i=0; i<frametable.length; i++)
    {
        if (0 == frametab[i].getPinCount())
        {
           unpinned_count++;
        }
    }
      
    return unpinned_count;
  }

  /**
  * Gets the total number of buffer frames.
  */
  public int getNumBuffers() {
    return bufpool.length;
  }

} // public class BufMgr implements GlobalConst
