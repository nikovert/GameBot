package HALclean;

import java.util.Arrays;

import reversi.GameBoard;

public class Search {

  // Class objects
  private final BitBoard board;
  private final NegaScout scout;

  // Constants
  private final int MAXDEPTH;

  // Game information
  private final int PLAYER;
  private final int OPPONENT;
  private Pair[] Pairs;

  
  /**
   * Constructor
   * 
   */
  Search(long timeout, BitBoard board, int player, int opponent) {

    // Class objects
    this.board = board;
    scout = new NegaScout(board, timeout, player, opponent);

    // Constants
    MAXDEPTH = GameConstants.MAXDEPTH;

    // Game information
    this.PLAYER = player;
    this.OPPONENT = opponent;
  }

  
  /**
   * 
   * @author felixcrazzolara
   * 
   *         Local class used to sort moves with their values.
   * 
   */
  private class Pair implements Comparable<Pair> {

    // Search information
    private int move;
    private int value;

    public int compareTo(Pair p) {
      if (value > p.value)
        return 1;
      else
        return -1;
    }
  }

  
  /**
   * 
   * @return Returns an object of search results containing all information
   *         about this search.
   * 
   *         Main function to search the best move. Uses iterative deepening.
   * 
   */
  public SearchResults deepen(GameBoard gb) {

    // Timing
    long start = System.currentTimeMillis();

    // Update board
    board.update_opp_move(gb);

    // Generate all moves
    board.generate_all(PLAYER);
    int[] moves = board.getAllMoves();

    // Initialize Pairs
    Pairs = new Pair[moves.length];
    for(int i = 0; i < Pairs.length; ++i)
      Pairs[i] = new Pair();
    for (int i = 0; i < moves.length; ++i)
      Pairs[i].move = moves[i];
    for (int i = 0; i < Pairs.length; ++i)
      Pairs[i].value = Integer.MIN_VALUE;

    // Check for pass
    if (Pairs.length == 0) {
      board.pass();
      SearchResults info = new SearchResults();
      info.bestMove = null;
      info.maxDepth = 1;
      info.timediff = System.currentTimeMillis() - start;
      return info;
    }

    // Check for corner move
    for(int j = 0; j < Pairs.length; ++j){
    	switch(moves[j]){
    		case 11:
    		case 18:
    		case 81:
    		case 88:
				board.make_move(moves[j], PLAYER);
				SearchResults info = new SearchResults();
				info.bestMove = board.ArraytoCoordinate(moves[j]);
				info.maxDepth = 1;
				info.timediff = System.currentTimeMillis() - start;
				return info;
    	}
    }
    
    // Single move possible
    if (Pairs.length == 1) {
      board.make_move(Pairs[0].move, PLAYER);
      SearchResults info = new SearchResults();
      info.bestMove = board.ArraytoCoordinate(Pairs[0].move);
      info.maxDepth = 1;
      info.timediff = System.currentTimeMillis() - start;
      return info;
    }

    ////// Iterative deepening //////

    // Save default move to prevent ugly errors if everything fails.
    int bestMove = Pairs[0].move;
    
    // NegaScout
    scout.initialize(start);

    // Debug
    scout.node_counter = 0;
    
    // Iterative search
    for (int d = 1; d <= MAXDEPTH; ++d) {

      // Get best move.
      try {
        bestMove = search(d);
      } catch (OutOfTimeException e) {
        bestMove = Pairs[Pairs.length - 1].move;
        break;
      }
    }
    
    // Debug
    System.out.println("Node_Counter is: " + scout.node_counter);
    
    // Make best move.
    board.make_move(bestMove, PLAYER);

    //// Search-Information ////
    SearchResults info = new SearchResults();
    info.bestMove = board.ArraytoCoordinate(bestMove);
    info.maxDepth = scout.getMaxDepthReached();
    info.timediff = System.currentTimeMillis() - start;

    return info;
  }

  
  /**
   * 
   * @param currentdepth The maximum depth to search.
   * @return
   */
  private int search(int currentdepth) {
    boolean interrupted = false;
    
    try {

      // Needed to undo moves.
      scout.resetCounter();
      scout.setMaximumDepth(currentdepth);

      // Compute for each move it's negamax value using NegaScout.
      for (int i = Pairs.length - 1; i >= 0; --i) {
        board.make_move(Pairs[i].move, PLAYER);
        Pairs[i].value = scout.negascout(Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        board.undo_move();
      }
    } catch (OutOfTimeException e) {
      
      // Undo all moves that haven't been undone yet.
      for (int i = 0; i < 1 + e.movecounter; ++i)
        board.undo_move();
      
      interrupted = true;
    } finally {
      
      // Sort all moves according to their values.
      Arrays.sort(Pairs);
      
      // If the search was interrupted throw another exception.
      if(interrupted) throw new OutOfTimeException();
    }
    
    // Return the best move found.
    return Pairs[Pairs.length - 1].move;
  }
  
}
