package bufmgr;



public class Clock extends Replacer {

  private int current; //current frame the clock is pointing to


  public Clock(BufMgr bufmgr) {
  	super(bufmgr);
    current = 0;
  }

  /**
   * Notifies the replacer of a new page.
   */
  public void newPage(FrameDesc fdesc) {

  }

  /**
   * Notifies the replacer of a free page.
   */
  public void freePage(FrameDesc fdesc) {

  }

  /**
   * Notifies the replacer of a pined page.
   */
  public void pinPage(FrameDesc fdesc) {

  }

  /**
   * Notifies the replacer of an unpinned page.
   */
  public void unpinPage(FrameDesc fdesc) {

  }

  /**
   * Selects the best frame to use for pinning a new page.
   * 
   * @return victim frame number, or -1 if none available
   */
  public int pickVictim() {

    for(int counter = 0; counter < frametable.length*2; counter++)
    {
      if(frametable[current].getValidDataIndicator() == false)
        return current; //frame doesn't have valid data, use it

      if(frametable[current].getPinCount() == 0)
      {
        if(frametable[current].getRefBit() == true)
          frametable[current].setRefBit(false);
        else
          return current; //not referenced recently, use it
      }

      current = (current + 1) % frametable.length;

    }
  	
    //If you looped through twice and couldn't find an available frame, return error num
    return -1;
  }


}