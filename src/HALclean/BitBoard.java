package HALclean;

import reversi.OutOfBoundsException;

public class BitBoard {

  // Constants
  private final int MOVE_ORDER_SIZE;
  private final int MAX_SEARCH_DEPTH;
  private final int MOVE_STACK_SIZE;
  private final int PLAYER;
  private final int PLAYER_REVERSI;
  private final int OPPONENT;
  private final int OPPONENT_REVERSI;
  private final int NORMAL_MOVE;
  private final int PASS;

  // Game-Constants
  private final int ILLEGAL;
  private final int GREENSQ;
  private final int EMPTY;
  private final int REDSQ;
  private final int OUTSIDE;
  private final int GREEN;
  private final int RED;

  // Patterns&Tables
  private final int move_check_order[]; // The order in which GameBoard fields
                                        // are checked whether they offer a
                                        // valid move.
  private final int flip_direction[][]; // Gives information about which
                                        // directions we have to check for
                                        // flips. For each field.

  // Game-Tables
  private int move_number[]; // The number of moves requested so far for a
  							// certain stage of the game.
  private final int move_list[][]; // Holds all possible moves for every stage
                                   // of the game.
  private int move_count[]; // Holds the number of possible moves for every
                            // stage of the game.
  private long move_stack[][]; // Stack holding all moves done.
  private int gain_red[]; // Contains the number of gained stones of this color
                          // in every stage of the game.
  private int gain_green[];

  // Table information
  private int stack_pointer;
  private int move_pointer;

  // Game information
  protected int disks_played;
  protected int board[][];
  protected int lastmove; //saves the last made move, can only be used during eval, as it is not always updated!

  /**
   * Constructor
   */
  public BitBoard(int player, int player_reversi, int opponent_reversi) {

    // Constants
    MOVE_ORDER_SIZE = GameConstants.MOVE_ORDER_SIZE;
    MAX_SEARCH_DEPTH = GameConstants.MAX_SEARCH_DEPTH;
    MOVE_STACK_SIZE = GameConstants.MOVE_STACK_SIZE;
    PLAYER = player;
    PLAYER_REVERSI = player_reversi;
    OPPONENT = 2 - PLAYER;
    OPPONENT_REVERSI = opponent_reversi;

    // Game-Constants
    ILLEGAL = GameConstants.ILLEGAL;
    GREENSQ = GameConstants.GREENSQ;
    EMPTY = GameConstants.EMPTY;
    REDSQ = GameConstants.REDSQ;
    OUTSIDE = GameConstants.OUTSIDE;
    GREEN = GameConstants.GREEN;
    RED = GameConstants.RED;
    NORMAL_MOVE = GameConstants.NORMAL_MOVE;
    PASS = GameConstants.PASS;

    // Patterns&Tables
    move_number = new int[MOVE_ORDER_SIZE];
    move_list = new int[MAX_SEARCH_DEPTH][MOVE_ORDER_SIZE];
    move_count = new int[MAX_SEARCH_DEPTH];
    move_stack = new long[MOVE_STACK_SIZE][2];
    move_check_order = new int[GameConstants.position_list.length];
    for (int i = 0; i < GameConstants.position_list.length; ++i)
      move_check_order[i] = GameConstants.position_list[i];
    flip_direction = new int[100][9];
    init_flip_direction();
    gain_red = new int[MAX_SEARCH_DEPTH];
    gain_green = new int[MAX_SEARCH_DEPTH];

    // Table information
    stack_pointer = 0;
    move_pointer = 0;

    // Game information
    disks_played = 0;
    board = new int[10][10];
    board_init();
  }

  /**
   * Initializes the GameBoard.
   */
  private void board_init() {
    for (int i = 0; i <= 9; ++i) {
      for (int j = 0; j <= 9; ++j) {
        if ((i == 0) || (i == 9) || (j == 0) || (j == 9))
          board[i][j] = OUTSIDE;
        else
          board[i][j] = EMPTY;
      }
    }

    board[4][4] = board[4][5] = REDSQ;
    board[5][4] = board[5][5] = GREENSQ;
  }

  /**
   * Updates the GameBoard after a move of our opponent.
   */
  public void update_opp_move(reversi.GameBoard gb) {

    generate_all(OPPONENT);

    for (int i = 0; i < move_count[disks_played]; ++i) {
      try {
        if (gb.getOccupation(ArraytoCoordinate(move_list[disks_played][i])) == OPPONENT_REVERSI)
          if (OPPONENT == RED) {
            make_move(move_list[disks_played][i], RED);
            break;
          } else {
            make_move(move_list[disks_played][i], GREEN);
            break;
          }
      } catch (OutOfBoundsException e) {
        System.out.println("Wrong coordinate in update_opp_move.");
      }
    }
  }

  /**
   * 
   * @param square
   * @return Returns a Coordinates object equivalent to square.
   */
  protected reversi.Coordinates ArraytoCoordinate(int square) {
    int column = (square % 10);
    int row = (square / 10);
    return new reversi.Coordinates(row, column);
  }

  /**
   * 
   * @param player
   * @return Returns the current board as bit board. For player.
   */
  private long toBitBoard(int player) {
    long BitBoard = 0;
    for (int row = 8; row >= 1; --row)
      for (int col = 8; col >= 1; --col) {
        if (board[row][col] == player)
          BitBoard = BitBoard | 1L;
        if (row == 1 && col == 1)
          break;
        BitBoard = BitBoard << 1;
      }
    return BitBoard;
  }

  /**
   * 
   * @param square
   *          The square to play.
   * @param player
   *          The player who plays.
   */
  public void make_move(int square, int player) {
    // Debug
    int stonesSet;
    long green;
    long red;

    pushCurrentBoard(NORMAL_MOVE);

    // Debug
    green = toBitBoard(GREEN);
    red = toBitBoard(RED);
    stonesSet = Long.bitCount(green) + Long.bitCount(red);

    setStones(square, player);
    
    //save in lastmove
    lastmove = square;

    green = toBitBoard(GREEN);
    red = toBitBoard(RED);
    if ((stonesSet + 1) != (Long.bitCount(green) + Long.bitCount(red))) {
      throw new RuntimeException("More or less than one stone set.");
    }

    ++disks_played;

    // Debug
    stonesSet = Long.bitCount(green) + Long.bitCount(red);

    if (stonesSet != disks_played + 4) {
      throw new RuntimeException("disks_played is not equal to stones set so far.");
    }
  }

  /**
   * 
   * @param player
   *          The player who plays.
   * 
   *          Just pushes the current board onto the stack.
   */
  public void pass() {
    pushCurrentBoard(PASS);
  }

  /**
   * 
   * @return Returns the next move, -1 if there was no move available.
   */
  /*
  private int getNextMove() {
    if (move_pointer >= move_count[disks_played])
      return -1;

    int move = move_list[disks_played][move_pointer];
    ++move_pointer;
    return move;
  } */

  /**
   * Prints the board to the standard output.
   */
  public void printBoard() {
    System.out.println("Board is: ");
    for (int i = 1; i <= 8; ++i) {
      for (int j = 1; j <= 8; ++j) {
        System.out.printf("%3d", board[i][j]);
      }
      System.out.println();
    }
    System.out.println();
  }

  /**
   * toString is useful for debugging purposes in the variable viewer.
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 1; i <= 8; ++i) {
      for (int j = 1; j <= 8; ++j) {
        s.append(String.format("%3d", board[i][j]));
      }
      s.append("\n");
    }
    s.append("\n");
    return s.toString();
  }

  /**
   * 
   * @return Returns an array containing all possible moves.
   */
  public int[] getAllMoves() {
    int[] moves = new int[move_count[disks_played]];
    for (int i = 0; i < move_count[disks_played]; ++i)
      moves[i] = move_list[disks_played][i];
    return moves;
  }

  /**
   * 
   * @param square
   *          The square to to check.
   * @return The occupation of this square.
   */
  public int getOccupation(int square) {
    int row = square / 10;
    int col = square % 10;
    return board[row][col];
  }

  /**
   * Undo last move;
   */
  public void undo_move() {
    int row;
    int col;
    long green_board = move_stack[stack_pointer - 1][0];
    long red_board = move_stack[stack_pointer - 1][1];

    // Check if the last move was a pass, then we don't have to change the
    // board.
    if (green_board == 0 && red_board == 0) {
      --stack_pointer;
      return;
    }

    else {
      // Copy board.
      for (int i = 0; i < 64; ++i) {
        row = i / 8 + 1;
        col = i % 8 + 1;

        if ((green_board & 1L << i) != 0)
          board[row][col] = GREENSQ;
        else if ((red_board & 1L << i) != 0)
          board[row][col] = REDSQ;
        else
          board[row][col] = EMPTY;
      }

      --stack_pointer;
      --disks_played;

      // Debug
      long green = toBitBoard(GREEN);
      long red = toBitBoard(RED);
      int stonesSet = Long.bitCount(green) + Long.bitCount(red);

      if (stonesSet != disks_played + 4) {
        throw new RuntimeException("disks_played is not equal to stones set so far.");
      }

      return;
    }
  }

  /**
   * 
   * Pushes the current GameBoard onto the stack.
   * 
   * @param type
   *          The type, PASS or NORMAL_MOVE.
   */
  public void pushCurrentBoard(int type) {
    if (type == NORMAL_MOVE) {
      move_stack[stack_pointer][0] = toBitBoard(GREEN);
      move_stack[stack_pointer][1] = toBitBoard(RED);
    } else if (type == PASS) {
      move_stack[stack_pointer][0] = 0;
      move_stack[stack_pointer][1] = 0;
    }
    ++stack_pointer;
  }

  /**
   * 
   * @param player
   * @return The number of stones player has.
   */
  public int countStones(int player) {
    int sum = 2;
    if (player == RED) {
      for (int i = 0; i < disks_played; ++i)
        sum += gain_red[i];
    } else {
      for (int i = 0; i < disks_played; ++i)
        sum += gain_green[i];
    }
    return sum;
  }

  /**
   * 
   * @param player
   *          The current player.
   * 
   *          Generates all moves available for this position, for this player.
   */
  public void generate_all(int player) {
    int count = 0;
    int curr_move;

    // If the game board is full no more moves are available.
    if (disks_played == 60) {
      move_count[disks_played] = 0;
    }

    // Reset the number of moves generated so far, otherwise we won't be able to
    // generate new ones.
    move_number[disks_played] = 0;

    curr_move = generate_move(player);
    while (curr_move != ILLEGAL) {
      move_list[disks_played][count] = curr_move;
      ++count;
      curr_move = generate_move(player);
    }
    move_count[disks_played] = count;
  }

  /**
   * 
   * @param player
   *          The player to generate a move for.
   * @return Returns the next move or ILLEGAL if there was no move available.
   */
  private int generate_move(int player) {
    int move;
    int move_index;

    move_index = move_number[disks_played];
    while (move_index < MOVE_ORDER_SIZE) {
      move = move_check_order[move_index];
      if ((board[move / 10][move % 10] == EMPTY) && is_valid_Move(move, player)) {
        move_number[disks_played] = move_index + 1;
        return move;
      } else
        move_index++;
    }

    // If move_index >= MOVE_ORDER_SIZE there was no valid move.
    move_number[disks_played] = move_index;
    return ILLEGAL;
  }

  /**
   * 
   * @param move
   *          The move to check whether it's valid, e.g. it flips something.
   * @param player
   *          The player that makes the move.
   * @return
   */
  private boolean is_valid_Move(int square, int player) {
    int index = 0;
    int dir = flip_direction[square][index];

    do {
      if (AnyDrctnlFlips(square, dir, player, oppOf(player)))
        return true;
      ++index;
      dir = flip_direction[square][index];
    } while (flip_direction[square][index] != 0); // While there are directions
                                                  // to check left.

    return false; // If no direction resulted in a flip this move is invalid.
  }

  /**
   * 
   * @param player
   * @return Returns the opponent. Check GameConstants for correct
   *         implementation.
   */
  private int oppOf(int player) {
    return 2 - player;
  }

  /**
   * 
   * @param square
   *          The square to play to.
   * @param inc
   *          in
   * @param player
   * @param opp
   * @return
   */
  private boolean AnyDrctnlFlips(int square, int dir, int player, int opp) {
    int pos = square;

    do {
      pos = pos + dir;
    } while (board[pos / 10][pos % 10] == opp);

    if (board[pos / 10][pos % 10] == player && pos != (square + dir)) {
      return true;
    } else
      return false;
  }

  /**
   * 
   * @param square
   *          The square to play to.
   * @param player
   *          The player who plays.
   */
  public void setStones(int square, int player) {
    int flipped = 0;
    int index = 0;
    int dir = flip_direction[square][index];

    board[square / 10][square % 10] = player;
    do {
      flipped += DrctnlFlips(square, dir, player, oppOf(player));
      ++index;
      dir = flip_direction[square][index];
    } while (flip_direction[square][index] != 0); // While there are directions
                                                  // to check left.

    if (player == RED) {
      gain_red[disks_played] = 1 + flipped;
      gain_green[disks_played] = -flipped;
    } else {
      gain_green[disks_played] = 1 + flipped;
      gain_red[disks_played] = -flipped;
    }
  }

  /**
   * 
   * @param square
   *          The square we played.
   * @param dir
   *          The direction to check.
   * @param player
   * @param opp
   * @return Returns the number of stones flipped.
   */
  private int DrctnlFlips(int square, int dir, int player, int opp) {
    int flipped = 0;
    int pos = square + dir;

    while (board[pos / 10][pos % 10] == opp)
      pos += dir;

    if (board[pos / 10][pos % 10] == player) {
      do {
        if (board[pos / 10][pos % 10] == opp) {
          board[pos / 10][pos % 10] = player;
          ++flipped;
        }
        pos -= dir;
      } while (board[pos / 10][pos % 10] != player);
      return flipped;
    } else
      return 0;
  }

  /**
   * Initializes the flip_direction table for each field of the GameBoard.
   */
  private void init_flip_direction() {

    // Iterate through every field on the board.
    for (int pos = 0; pos < GameConstants.dir_mask.length; ++pos) {
      for (int k = 0, valid = 0; k < 8; ++k)
        if ((GameConstants.dir_mask[pos] & (1 << k)) > 0) {
          flip_direction[pos][valid] = GameConstants.move_offset[k];
          ++valid;
        }
    }
  }
}