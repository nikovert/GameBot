package HAL;

import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;

public class HALTemplate implements ReversiPlayer {

  // Class objects
  private BitBoard board;
  private Search search;

  // Game information
  private int player;
  private int opponent;

  /**
   * Konstruktor, der bei der Gründung eines Bots eine Meldung auf den
   * Bildschirm ausgibt.
   */
  public HALTemplate() {
    System.out.println("A.I. erstellt.");
  }

  /**
   * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
   * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
   * 
   * @see reversi.ReversiPlayer
   */
  public void initialize(int player, long timeout) {

    // Player
    if (player == reversi.GameBoard.RED) {
      System.out.println("HALTemplate ist Spieler RED.");
      this.player = GameConstants.RED;
    } else {
      System.out.println("HALTemplate ist Spieler GREEN.");
      this.player = GameConstants.GREEN;
    }

    // Opponent player
    if (player == reversi.GameBoard.RED)
      opponent = reversi.GameBoard.GREEN;
    else
      opponent = reversi.GameBoard.RED;

    // Timeout
    // this.timeout = timeout;

    // Initialize class objects
    board = new BitBoard(this.player, player, opponent);
    search = new Search(board, player, opponent);
  }

  
  /**
   * Diese Methode wird von {@link reversi.Arena} abwechselnd aufgerufen.
   * 
   * @see reversi.ReversiPlayer
   * @return Der Zug des Spielers.
   */
  public Coordinates nextMove(GameBoard gb) {

    // Timing
    long start = System.currentTimeMillis();

    // Update board
    board.update_opp_move(gb);

    //// Check for pass ////
    board.generate_all(player);
    int[] possibleMoves = board.getAllMoves();

    if (possibleMoves.length == 0) {
      return null;
    }

    //// Use iterative deepening ////
    SearchResults info = search.deepen(possibleMoves, start, board);

    ////    Log-Info    ////
    System.out.print("HAL ");
    if (player == GameConstants.RED) {
      System.out.print("(RED) ");
    } else if (player == GameConstants.GREEN) {
      System.out.print("(GREEN) ");
    }

    System.out.println("timediff: " + info.timediff + " traveldepth: " + info.traveldepth);
    
    return info.bestMove;
  }
}
