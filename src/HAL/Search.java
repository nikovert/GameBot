package HAL;

public class Search {
  
  //Class objects
  private Eval eval;
  private BitBoard board;

  // Constants
   private final static int passvalue = -64;
   private long timeout = 500;
  // private int maxdepth;
   private int traveldepth = 7;
   
   // Game information
   private int player;
   private int opponent;
   
   
   /**
    * Constructor
    * 
    */
   Search(BitBoard board, int player, int opponent){
     
     //Class objects
     eval = new Eval();
     this.board = board;
     
     // Game information
     this.player = player;
     this.opponent = opponent;
   }

  
  /**
   * 
   * @param possibleMoves An array containing all moves available.
   * @param start The time we started our search.
   * @param board The board.
   * @return
   * 
   * Main function to search the best move. Uses iterative deepening.
   * 
   */
  public SearchResults deepen(int[] possibleMoves, long start, BitBoard board) {
    
    int at = 0;
    try {
      at = max(start, -64, 64, 0, 0, possibleMoves.length);
    } catch (RuntimeException e) {
      traveldepth--;
    }

    /*
     * go as deep as possible
     */
    long timediff = System.currentTimeMillis() - start;
    int traveldepthtmp = traveldepth;
    int prevat = at;

    while (timediff < (timeout - 1000)) {
      traveldepth++;
      try {
        at = max(start, -64, 64, 0, 0, possibleMoves.length);
      } catch (RuntimeException e) {
        at = prevat;
        timediff = System.currentTimeMillis() - start;
        traveldepth--;
        break;
      }
      prevat = at;
      timediff = System.currentTimeMillis() - start;
    }
    

    traveldepth = traveldepthtmp;
    at = Math.abs(at);
    at = (at - at % 1000) / 1000;
    
    System.out.println("Move is: " + possibleMoves[at]);
    
    ////    Search-Information    ////
    SearchResults info = new SearchResults();
    info.bestMove = board.ArraytoCoordinate(possibleMoves[at]);
    info.traveldepth = traveldepth;
    info.timediff = timediff;

    return info;
  }
  

  /**
   * 
   * @param the
   *          Gameboard
   * @param the
   *          start time
   * @param alpha
   *          value
   * @param beta
   *          value
   * @param depth
   *          the depth (used to see how deep we go)
   * @param last
   *          (the last made move)
   * @return
   */
  int min(long start, int alpha, int beta, int depth, int lastCoordinate, int lastmovescount) throws RuntimeException {
    long timediff = System.currentTimeMillis() - start;
    if (timediff > timeout - 500) {
      throw new RuntimeException("time ran out");
    }

    if (depth == traveldepth) {
      return eval.getWeight(board, lastCoordinate, depth, lastmovescount);
    }

    board.generate_all(opponent);
    int[] possible = board.getAllMoves();

    if (possible.length == 0) {
      return passvalue;
    }

    // GameBoard test;
    int min = beta, at = 0, tmp = 0;

    for (int i = 0; i < possible.length; i++) {

      // should we prune
      if (eval.checkPrune(board, possible[i], (board.countStones(player) + board.countStones(opponent)), opponent)) {
        if (++i >= possible.length) {
          break;
        }
      }

      board.make_move(possible[i], opponent); // make the move
      tmp = max(start, alpha, min, depth + 1, possible[i], possible.length);

      tmp = tmp % 1000;
      if (tmp < min) { // see if the move is good
        min = tmp; // save the move if it is
        at = i;
        if (min <= alpha) {
          break;
        }
      }
      board.undo_move();
    }
    min = min % 1000;
    if (min < 0) {
      min = min * (-1);
      min = at * 1000 + min; // add at to the number
      min = min * (-1);
    } else {
      min = at * 1000 + min; // add at to the number
    }

    return min;
  }

  /**
   * 
   * @param the
   *          Gameboard
   * @param the
   *          start time
   * @param alpha
   *          value
   * @param beta
   *          value
   * @param depth
   *          the depth (used to see how deep we go)
   * @param last
   *          (the last made move)
   * @return
   */
  int max(long start, int alpha, int beta, int depth, int lastCoordinate, int lastmovescount) throws RuntimeException {
    long timediff = System.currentTimeMillis() - start;
    if (timediff > timeout - 500) {
      throw new RuntimeException("time ran out");
    }

    if (depth == traveldepth) {
      return eval.getWeight(board, lastCoordinate, depth, lastmovescount);
    }

    int[] possible = board.getAllMoves();

    if (possible.length == 0) {
      return passvalue;
    }

    int max = alpha, at = 0, tmp = 0;

    for (int i = 0; i < possible.length; i++) {

      // should We prune
      if (eval.checkPrune(board, possible[i], (board.countStones(player) + board.countStones(opponent)), player)) {
        if (++i >= possible[i]) {
          break;
        }
      }

      board.make_move(possible[i], player); // make the move
      tmp = min(start, max, beta, depth + 1, possible[i], possible.length);

      tmp = tmp % 1000;
      if (tmp > max) { // see if the move is good
        max = tmp; // save the move if it is
        at = i;
        if (max >= beta) {
          break;
        }
      }
      board.undo_move();
    }
    max = max % 1000;
    if (max < 0) {
      max = max * (-1);
      max = at * 1000 + max; // add at to the number
      max = max * (-1);
    } else {
      max = at * 1000 + max; // add at to the number
    }

    return max;
  }

}
