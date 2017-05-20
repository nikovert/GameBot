package HALclean;

public class NegaScout {
  
  // Class objects
  private final BitBoard board;
  private final Eval eval;
  
  // Constants
  private int MAXDEPTH;
  private final long TIMEBUFFER;
  
  // Timing
  private long start;
  private final long timeout;
  
  // Game information
  private final int PLAYER;
  private final int OPPONENT;
  
  // Search information
  private int maxDepthReached;
  private int move_counter;
  
  
  // Debug
  public int node_counter;
  
  
  /**
   * 
   * @param board The game board representation.
   * @param timeout The maximum time available to calculate the best move.
   */
  public NegaScout(BitBoard board, long timeout, int player, int opponent){
    
    // Class objects
    this.board = board;
    eval = new Eval();
    
    // Timing
    this.timeout = timeout;
    
    // Constants
    MAXDEPTH = GameConstants.MAXDEPTH;
    TIMEBUFFER = GameConstants.TIMEBUFFER;
    
    // Game information
    this.PLAYER = player;
    this.OPPONENT = opponent;
    
    // Search information
    maxDepthReached = 0;
  }
  
  
  /**
   * 
   * IMPORTANT!
   * This function must be called before using NegaScout for every new game board.
   * 
   * @param start
   */
  public void initialize(long start){
    
    // Timing
    this.start = start;
    
    // Search information
    maxDepthReached = 0;
  }
  
  
  /**
   * 
   * IMPORTANT!
   * This function needs to be called before every new stage of iterative deepening.
   * Needed to undo moves. Otherwise things become very very ugly.
   * 
   */
  public void resetCounter(){
    move_counter = 0;
  }

  
  /**
   * 
   * @param maxDepth The new maximum depth.
   */
  public void setMaximumDepth(int maxDepth){
    MAXDEPTH = maxDepth;
  }
  
  
  /**
   * 
   * @return Returns the maximum depth reached.
   */
  public int getMaxDepthReached(){
    return maxDepthReached;
  }
  
  
  /**
   * 
   * @param alpha Current lower bound.
   * @param beta Current upper bound.
   * @param currentdepth The current depth.
   * @return Returns the negamax value of the current game board.
   */
  public int negascout(int alpha, int beta, int currentdepth) throws OutOfTimeException {
    int player;
    
    // Debug
    ++node_counter;
    
    // Check if we ran out of time.
    if(System.currentTimeMillis() - start > timeout - TIMEBUFFER) throw new OutOfTimeException(move_counter);
    
    // Check if got deeper than ever before.
    if(currentdepth > maxDepthReached)
      maxDepthReached = currentdepth;
    
    // Calculate player
    if(currentdepth % 2 == 0)
      player = PLAYER;
    else
      player = OPPONENT;
      
    // Leaf
    if(currentdepth == MAXDEPTH)                                        // Leaf evaluation
      return eval.evaluate(board, player);
    
    // Move generation
    board.generate_all(player);
    int[] Moves = board.getAllMoves();
    
    // Search parameters
    int lo_value = Integer.MIN_VALUE;
    int hi_value = beta;
    
    // No move possible
    if (Moves.length == 0){
      board.pass();
      ++move_counter;
      int t = -negascout(-hi_value,-Math.max(lo_value, alpha), currentdepth + 1);
      board.undo_move();
      --move_counter;
      return t;
    }
    
    // Single move possible
    if(Moves.length == 1){
	 board.make_move(Moves[0], player);
	 ++move_counter;
	 int t = -negascout(-hi_value,-Math.max(lo_value, alpha), currentdepth + 1);
	 board.undo_move();
	 --move_counter;
	 return t;
    }
    
    /*
     * need to add call of method checkPrune!
     */
      
    // Multiple moves
    for(int j = 0; j < Moves.length; ++j){
     
      // First search with open window.
      board.make_move(Moves[0], player);
      ++move_counter;
      int t = -negascout(-hi_value,-Math.max(lo_value, alpha), currentdepth + 1);
      
      // Conditions for research.
      if(t > Math.max(lo_value, alpha) && t < beta && j > 0 && currentdepth < MAXDEPTH - 2)
        t = -negascout(-hi_value, -t, currentdepth + 1);
      
      board.undo_move();
      --move_counter;
      
      // Adjust bounds.
      lo_value = Math.max(lo_value, t);
      if(lo_value >= beta)
        return lo_value;                                            // Cut
      
      hi_value = Math.max(lo_value, alpha) + 1;                     // New null window
    }

    return lo_value;
  }
  
}
