package HALclean;

import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;

public class HALTemplate implements ReversiPlayer {

  // Class objects
  private BitBoard board;
  private Search search;

  // Timing
  private long timeout;
  
  // Game information
  private int player;
  private int opponent;
  
  // Debug
  int player_reversi;
  
  
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
  public void initialize(int player_reversi, long timeout) {
    int opponent_reversi;
    
    // Debug
    this.player_reversi = player_reversi;
    
    // Player
    if (player_reversi == reversi.GameBoard.RED) {
      System.out.println("HALTemplate ist Spieler RED.");
      player = GameConstants.RED;
    } else {
      System.out.println("HALTemplate ist Spieler GREEN.");
      player = GameConstants.GREEN;
    }

    // Opponent player
    if (player_reversi == reversi.GameBoard.RED){
      opponent = GameConstants.GREEN;
      opponent_reversi = reversi.GameBoard.GREEN;
    }
    else{
      opponent = GameConstants.RED;
      opponent_reversi = reversi.GameBoard.RED;
    }

    // Timing
    this.timeout = timeout;

    // Class objects
    board = new BitBoard(player, player_reversi, opponent_reversi);
    search = new Search(timeout, board, player, opponent);
  }

  
  /**
   * Diese Methode wird von {@link reversi.Arena} abwechselnd aufgerufen.
   * 
   * @see reversi.ReversiPlayer
   * @return Der Zug des Spielers.
   */
  public Coordinates nextMove(GameBoard gb) {

    //// Use iterative deepening ////
    SearchResults info = search.deepen(gb);

    ////    Log-Info    ////
    System.out.print("HAL ");
    if (player == GameConstants.RED) {
      System.out.print("(RED) ");
    } else if (player == GameConstants.GREEN) {
      System.out.print("(GREEN) ");
    }
    System.out.println("timediff: " + info.timediff + " maxDepth: " + info.maxDepth);
    
    if(!gb.checkMove(player_reversi, info.bestMove)){
      throw new RuntimeException();
    }
    
    return info.bestMove;
  }
}
