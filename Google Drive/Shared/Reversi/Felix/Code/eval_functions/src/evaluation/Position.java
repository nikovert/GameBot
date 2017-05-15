package evaluation;

public class Position {

  /**
   * 
   * @return (Inner node):  The approximate value of this game situation.
   *         <p>
   *         (Leaf):        1 for a win, 0 otherwise.
   */
  // Siehe class Tree_Search!
  public int eval(){
    return -1;
  }
  
  /**
   * 
   * @return Returns an Array holding all further games states.
   *         <p>
   *         Returns null if J is a leaf.
   */
  // Wir verwenden einen Array aus Performance-Gründen.
  // Sollten die Funktionalitäten einer Array-List benütigt werden -> Wechsel.
  public Position[] successors(){
    return null;
  }
}

