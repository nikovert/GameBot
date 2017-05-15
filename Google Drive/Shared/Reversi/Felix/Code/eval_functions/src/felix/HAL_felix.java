package felix;

import reversi.Coordinates;
import reversi.ReversiPlayer;

public class HAL_felix implements ReversiPlayer {

  // Game information
  private long timeout;
  private int player;
  private GameBoard gb;

  private int color = 0;
  private int othercolor = 0;
  protected int maxdepth;
  protected int traveldepth;
  private final int passvalue = 100;

  /**
   * Constructor, prints message on creation.
   * 
   */
  public HAL_felix() {
    System.out.println("A.I. erstellt.");
  }

  /**
   * Initializes timeout & player. Is called ate the beginning of every game of
   * {@link Arena}.
   * 
   * @see reversi.ReversiPlayer
   */
  public void initialize(int player, long timeout) {

    int opponent;

    // Opponent color
    if (player == reversi.GameBoard.RED)
      opponent = reversi.GameBoard.GREEN;
    else
      opponent = reversi.GameBoard.RED;

    // Timeout
    this.timeout = timeout;
    if (timeout > 5000)
      this.timeout = 5000;

    // Player
    if (player == reversi.GameBoard.RED) {
      System.out.println("HAL ist Spieler RED.");
      this.player = GameConstants.RED;
    } else {
      System.out.println("HAL ist Spieler GREEN.");
      this.player = GameConstants.GREEN;
    }

    // Initialize GameBoard
    gb = new GameBoard(this.player, player, opponent);
  }

  /**
   * Return the next move our player makes.
   */
  public Coordinates nextMove(reversi.GameBoard gb) {
    return bestMove(gb);
  }

  /**
   * 
   * @param reversi.Gameboard
   *          gb
   * @return The Coordinates for the best move.
   */
  private Coordinates bestMove(reversi.GameBoard gb) {

    // Get time-stamp.
    long start = System.currentTimeMillis();

    // Update the GameBoard.
    this.gb.update_opp_move(gb);

    // Generate all moves for this position.
    this.gb.generate_all(player);

    int at = 0;

    try {
      at = max(gb.clone(), start, -64, 64, 0, null);
    } catch (RuntimeException e) {
      traveldepth--;
    }

    long timediff = System.currentTimeMillis() - start;
    int traveldepthtmp = traveldepth;
    int prevat = at;

    while (timediff < (timeout - 1000)) {
      traveldepth++;
      try {
        at = max(gb.clone(), start, -64, 64, 0, null);
      } catch (RuntimeException e) {
        at = prevat;
        timediff = System.currentTimeMillis() - start;
        traveldepth--;
        break;
      }
      prevat = at;
      timediff = System.currentTimeMillis() - start;
    }

    System.out.println("timediff: " + timediff + " traveldepth: " + traveldepth);
    traveldepth = traveldepthtmp;
    at = Math.abs(at);
    at = (at - at % 1000) / 1000;
    return possible.get(at);
  }

  /**
   * @param the
   *          Gameboard gb
   * @param the
   *          current move (that we plan on making)
   * @return how good the move is (+inf == very good, -inf == shit)
   */
  private int getWeight(GameBoard gb, Coordinates last, int depth, int mobility) {
    if (last == null)
      return 0;
    int totalstones = (gb.countStones(color) + gb.countStones(othercolor));
    int col = last.getCol(), row = last.getRow();

    int mobilityAdvantage = mobility;
    int mA = (128 - totalstones) / 64; // the coefficient for the
                                       // mobilityAdvantage

    // number of tiles
    int occupiedSquareAdvantage = (gb.countStones(color) - gb.countStones(othercolor));
    double oSA; // the coefficient for the OccupiedSquareAdvantage

    int fieldAdvantage = 0;
    double fA = 1.5; // the coefficient for the fieldAdvantage

    if (totalstones < 10) {
      oSA = -(100 - totalstones) / 30.0; // 9 < oSA < 10 => 0 <
                                         // occupiedSquareAdvantage 140
    } else {
      if (totalstones < 20) {
        oSA = -(90 - 2 * (totalstones - 10)) / 30.0; // 7 < oSA < 9 => 0 <
                                                     // occupiedSquareAdvantage
                                                     // 220
      } else {
        if (totalstones < 40) {
          oSA = -(90 - 3 * (totalstones - 20)) / 30.0; // 3 < oSA < 6 => 0 <
                                                       // occupiedSquareAdvantage
                                                       // 400
        } else {
          if (totalstones < 50) {
            oSA = -(50 - 5 * (totalstones - 40)) / 50.0; // 0 < oSA < 7 => 0 <
                                                         // occupiedSquareAdvantage
                                                         // 350
          } else
            oSA = 3;

        }
      }
    }
    // System.out.println("oSA: " + oSA + " totalstones: " + totalstones);

    /*
     * weight of fields: 64 -8 8 6 6 8 -8 64 -8 -24 -4 -3 -3 -4 -24 -8 8 -4 7 4
     * 4 7 -4 8 6 -3 4 0 0 4 -3 6 6 -3 4 0 0 4 -3 6 8 -4 7 4 4 7 -4 8 -8 -24 -4
     * -3 -3 -4 -24 -8 64 -8 8 6 6 8 -8 64
     */

    switch (col) {
    case 1:
      col = 8;
    case 8:
      if (row == 1 || row == 8)
        fieldAdvantage += 64;
      else {
        if (row == 2 || row == 7)
          fieldAdvantage += -8;
        else {
          if (row == 3 || row == 6)
            fieldAdvantage += 8;
          else
            fieldAdvantage += 6;
        }
      }
      break;
    case 2:
      col = 7;
    case 7:
      if (row == 1 || row == 8)
        fieldAdvantage += -8;
      else {
        if (row == 2 || row == 7)
          fieldAdvantage += -24;
        else {
          if (row == 3 || row == 6)
            fieldAdvantage += -4;
          else
            fieldAdvantage += -3;
        }
      }

      break;
    case 3:
      col = 6;
    case 6:
      if (row == 1 || row == 8)
        fieldAdvantage += 8;
      else {
        if (row == 2 || row == 7)
          fieldAdvantage += -4;
        else {
          if (row == 3 || row == 6)
            fieldAdvantage += 7;
          else
            fieldAdvantage += 4;
        }
      }
      break;
    case 4:
      col = 5;
    case 5:
      if (row == 1 || row == 8)
        fieldAdvantage += 6;
      else {
        if (row == 2 || row == 7)
          fieldAdvantage += -3;
        else {
          if (row == 3 || row == 6)
            fieldAdvantage += 4;
          else
            fieldAdvantage += 0;
        }
      }
      break;
    default:
      fieldAdvantage += 0;
    }

    int weight = (int) (fieldAdvantage * fA + occupiedSquareAdvantage * oSA + mobilityAdvantage * mA);
    // System.out.println("fieldAdvantage: \t" + fieldAdvantage+ " fA: \t" + fA+
    // " occupiedSquareAdvantage: \t"+ occupiedSquareAdvantage + " oSA: \t" +
    // oSA +" mobilityAdvantage: \t"+ mobilityAdvantage + " mA: \t" + mA);
    // System.out.println("weight total: \t" + weight);
    return weight;
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
  private int min(GameBoard gb, long start, int alpha, int beta, int depth, Coordinates last) throws RuntimeException {
    long timediff = System.currentTimeMillis() - start;
    if (timediff > timeout - 500) {
      throw new RuntimeException("time ran out");
    }

    if (depth == traveldepth) {
      return getWeight(gb, last, depth, possibleMax - possible.size()); // go
                                                                        // back
                                                                        // up
                                                                        // from
                                                                        // here
    }

    if (possible.isEmpty()) {
      return passvalue;
    }

    GameBoard test;
    int min = beta, at = 0, tmp = 0;

    for (int i = 0; i < possible.size(); i++) {
      test = gb.clone(); // make a new copy of the board
      if (!test.checkMove(othercolor, possible.get(i)))
        System.err.println(tab(depth) + "Whoops, move don't work"); // test the
                                                                    // move

      test.makeMove(othercolor, possible.get(i)); // make the move

      tmp = max(test, start, alpha, min, depth + 1, possible.get(i));

      tmp = tmp % 1000;
      if (tmp < min) { // see if the move is good
        min = tmp; // save the move if it is
        at = i;
        if (min <= alpha) {
          break;
        }
      }
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
  private int max(GameBoard gb, long start, int alpha, int beta, int depth, Coordinates last) throws RuntimeException {
    long timediff = System.currentTimeMillis() - start;
    if (timediff > timeout - 500) {
      throw new RuntimeException("time ran out");
    }

    int possibleMin = possibleMoves(gb, othercolor).size();

    ArrayList<Coordinates> possible = possibleMoves(gb, color);

    if (depth == traveldepth) {
      return getWeight(gb, last, depth, possibleMin - possible.size()); // go
                                                                        // back
                                                                        // up
                                                                        // from
                                                                        // here
    }

    if (possible.isEmpty()) {
      return passvalue;
    }

    GameBoard test;
    int max = alpha, at = 0, tmp = 0;

    for (int i = 0; i < possible.size(); i++) {
      test = gb.clone(); // make a new copy of the board
      if (!test.checkMove(color, possible.get(i)))
        System.err.println(tab(depth) + "Whoops, move don't work"); // test the
                                                                    // move

      test.makeMove(color, possible.get(i)); // make the move

      tmp = min(test, start, max, beta, depth + 1, possible.get(i));

      tmp = tmp % 1000;
      if (tmp > max) { // see if the move is good
        max = tmp; // save the move if it is
        at = i; // eventuel nur bei depth == 0
        if (max >= beta) {
          break;
        }
      }
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

  /**
   * Checks if a corner move is possible
   * 
   * @return the corner, or null if no corner move is possible
   */
  private Coordinates checkCorners(GameBoard gb) {
    Coordinates result = null;
    int max = 0, newmax = 0;

    if (gb.checkMove(color, new Coordinates(1, 1)))
      result = new Coordinates(1, 1);

    if (gb.checkMove(color, new Coordinates(1, 8)))
      if (result != null) {
        GameBoard test = gb.clone();
        test.checkMove(color, result);
        test.makeMove(color, result);
        try {
          max = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        test = gb.clone();
        test.checkMove(color, new Coordinates(1, 8));
        test.makeMove(color, new Coordinates(1, 8));
        try {
          newmax = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        if (newmax % 1000 >= max % 1000)
          result = new Coordinates(1, 8);
      } else
        result = new Coordinates(1, 8);

    if (gb.checkMove(color, new Coordinates(8, 1)))
      if (result != null) {
        GameBoard test = gb.clone();
        test.checkMove(color, result);
        test.makeMove(color, result);
        try {
          max = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        test = gb.clone();
        test.checkMove(color, new Coordinates(8, 1));
        test.makeMove(color, new Coordinates(8, 1));
        try {
          newmax = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        if (newmax % 1000 >= max % 1000)
          result = new Coordinates(1, 8);
      } else
        result = new Coordinates(8, 1);
    if (gb.checkMove(color, new Coordinates(8, 8)))
      if (result != null) {
        GameBoard test = gb.clone();
        test.checkMove(color, result);
        test.makeMove(color, result);
        try {
          max = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        test = gb.clone();
        test.checkMove(color, new Coordinates(8, 8));
        test.makeMove(color, new Coordinates(8, 8));
        try {
          newmax = max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
        } catch (RuntimeException e) {

        }
        if (newmax % 1000 >= max % 1000)
          result = new Coordinates(8, 8);
      } else
        result = new Coordinates(8, 8);
    return result;
  }

  /**
   * Used for the System output
   */
  private String tab(int node) {
    String tmp = "";
    for (int i = 0; i < node; i++)
      tmp += "\t" + "*";
    return tmp;
  }
}
