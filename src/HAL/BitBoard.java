package HAL;
import reversi.OutOfBoundsException;

public class BitBoard {
  
  // Constants
  private final int MOVE_ORDER_SIZE;
  private final int MAX_SEARCH_DEPTH;
  private final int PLAYER;
  private final int OPPONENT;
  private final int OPPONENT_REVERSI;
  
  // Game-Constants
  private final int ILLEGAL;
  private final int GREENSQ;
  private final int EMPTY;
  private final int REDSQ;
  private final int OUTSIDE;
  private final int GREEN;
  private final int RED;
 
  // Patterns&Tables
  private int move_number[];                // The number of moves requested so far for a certain stage of the game.
  private final int move_check_order[];     // The order in which GameBoard fields are checked whether they offer a valid move.
  private final int flip_direction[][];     // Gives information about which directions we have to check for flips. For each field.
  
  // Game-Tables
  private final int move_list[][];          // Holds all possible moves for every stage of the game.
  private int move_count[];                 // Holds the number of possible moves for every stage of the game.
  private long move_stack[][];              // Stack holding all moves done.
  private int gain_red[];                   // Contains the number of gained stones of this color in every stage of the game.
  private int gain_green[];
  
  // Table information
  private int stack_pointer;
  private int move_pointer;
  
  // Game information
  private int disks_played;
  private int board[];
  
  
  /**
   * Constructor
   */
  public BitBoard(int player, int player_reversi, int opponent_reversi) {
    
    // Constants
    MOVE_ORDER_SIZE = GameConstants.MOVE_ORDER_SIZE;
    MAX_SEARCH_DEPTH = GameConstants.MAX_SEARCH_DEPTH;
    PLAYER = player;
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
    
    // Patterns&Tables
    move_number = new int[MOVE_ORDER_SIZE];
    move_list = new int[MAX_SEARCH_DEPTH][MOVE_ORDER_SIZE];
    move_count = new int[MAX_SEARCH_DEPTH];
    move_stack = new long[MAX_SEARCH_DEPTH][2];
    move_check_order = new int[GameConstants.position_list.length];
    for(int i = 0; i < GameConstants.position_list.length; ++i)
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
    board = new int[100];
    board_init();
    
    //If we are Green e.g. we start second, we have to initialize move_list & move_count for update_opp_move().
    if(PLAYER == GREEN)
      generate_all(RED);
  }
  
  
  /**
   * Initializes the GameBoard.
   */
  private void board_init()
  {
    int pos;
    
    for (int i = 0; i < 10; ++i) {
      for (int j = 0; j < 10; ++j) {
        pos = 10 * i + j;
        if ( (i == 0) || (i == 9) || (j == 0) || (j == 9) )
          board[pos] = OUTSIDE;
        else
          board[pos] = EMPTY;
      }
    }
    
    board[44] = board[45] = REDSQ;
    board[54] = board[55] = GREENSQ;
  }
  
  
  /**
   * Updates the GameBoard after a move of our opponent.
   */
  public void update_opp_move(reversi.GameBoard gb){
    
    // We already knew the set of possibles moves, we just need to find the correct one.
    for(int i = 0; i < move_count[disks_played]; ++i){
      try{
        if( gb.getOccupation( ArraytoCoordinate( move_list[disks_played][i] ) ) == OPPONENT_REVERSI){
          if(OPPONENT == RED)
            make_move(move_list[disks_played][i], RED);
          else
            make_move(move_list[disks_played][i], GREEN);
        }
      }
      catch(OutOfBoundsException e){
        System.out.println("Fuck it!");
      }
    }
    
    printBoard();
  }
  
  
  /**
   * 
   * @param square
   * @return Returns a Coordinates object equivalent to square.
   */
  protected reversi.Coordinates ArraytoCoordinate(int square){
    int column = (square % 10);
    int row = (square / 10);
    return new reversi.Coordinates(row, column);
  }
  
  
  /**
   * 
   * @param player
   * @return Returns the current board as bit board. For player.
   */
  private long toBitBoard(int player)
  {
    long BitBoard = 0;
    for(int row = 8; row >= 1; --row)
      for(int col = 8; col >= 1; --col){
        int square = 10 * row + col;
        if( board[square] == player ){
          BitBoard += 1;
          if(square == 11)
            break;
          BitBoard *= 2;
        } 
      }
    return BitBoard;
  }

  
  /**
   * 
   * @param square The square to play.
   * @param player The player who plays.
   */
  public void make_move(int square, int player){
    pushCurrentBoard();
    setStones(square, player);
    ++disks_played;
  }
  
  
  /**
   * 
   * @param player The player who plays.
   */
  public void pass(int player){
    pushCurrentBoard();
  }
  
  
  /**
   * 
   * @return Returns the next move, -1 if there was no move available.
   */
  private int getNextMove(){
    if(move_pointer >= move_count[disks_played])
      return -1;
    
    int move = move_list[disks_played][move_pointer];
    ++move_pointer;
    return move;
  }
  
  
  public void printBoard(){
    System.out.println("Board is: ");
    for(int i = 1; i <= 8; ++i){
      for(int j = 1; j <=8; ++j){
        int pos = 10*i + j;
        System.out.printf("%3d", board[pos]);
      }
      System.out.println();
    }
    System.out.println();
  }
  
  
  /**
   * 
   * @return Returns an array containing all possible moves.
   */
  public int[] getAllMoves(){
    int[] moves = new int[move_count[disks_played]];
    for(int i = 0; i < move_count[disks_played]; ++i) 
    	moves[i] = move_list[disks_played][i];
    return moves;
  }
  
  
  /**
   * 
   * @param square The square to to check.
   * @return The occupation of this square.
   */
  public int getOccupation(int square){
    return board[square];
  }
	  
  
  /**
   * Undo last move;
   */
  public void undo_move()
  {
    int pos;
    long green_board = move_stack[stack_pointer][0];
    long red_board = move_stack[stack_pointer][1];
    
    // Copy board.
    for(int i = 0; i < 64; ++i)
    {
      pos = (((i/8) + 1) * 10) + ((i%8) + 1);
      
      if( (green_board & 1L) > 0 )
        board[pos] = GREENSQ;
      else if( (red_board & 1L) > 0 )
        board[pos] = REDSQ;
      else
        board[pos] = EMPTY;
    }
    
   --stack_pointer;
   --disks_played;   
  }


  /**
  * Pushes the current GameBoard onto the stack.
  */
  private void pushCurrentBoard(){
    move_stack[stack_pointer][0] = toBitBoard(GREEN);
    move_stack[stack_pointer][1] = toBitBoard(RED);
    ++stack_pointer;
  }
  
  
  /**
   * 
   * @param player
   * @return The number of stones player has.
   */
  public int countStones(int player)
  {
    int sum = 2;
    if(player == RED){
      for(int i = 0; i < disks_played; ++i)
        sum += gain_red[i];
    }
    else{
      for(int i = 0; i < disks_played; ++i)
        sum += gain_green[i];
    }
    return sum;
  }
  

  /**
   * 
   * @param player The current player.
   * 
   * Generates all moves available for this position, for this player.
   */
  public void generate_all(int player){
    int count = 0;
    int curr_move;
 
    curr_move = generate_move(player);
    while(curr_move != ILLEGAL) {
      move_list[disks_played][count] = curr_move;
      ++count;
      curr_move = generate_move(player);
    }

    move_count[disks_played] = count;
  }
  
  /**
   * 
   * @param player The player to generate a move for.
   * @return Returns the next move or ILLEGAL if there was no move available.
   */ 
  private int generate_move(int player)
  {
    int move;
    int move_index;

    move_index = move_number[disks_played];
    while ( move_index < MOVE_ORDER_SIZE)
    {
      move = move_check_order[move_index];
      if( (board[move] == EMPTY) && is_valid_Move(move, player) ){
        move_number[disks_played] = move_index + 1;
        return move;
      }
      else
        move_index++;
    }

    // If move_index >= MOVE_ORDER_SIZE there was no valid move.
    move_number[disks_played] = move_index;
    return ILLEGAL;
  }
  
  
  /**
   * 
   * @param move The move to check whether it's valid, e.g. it flips something.
   * @param player The player that makes the move.
   * @return
   */
  private boolean is_valid_Move(int square, int player)
  {  
    int index = 0;
    int dir = flip_direction[square][index];
        
    do 
    {
      if( AnyDrctnlFlips(square, dir, player, oppOf(player)) )
        return true;
      ++index;
      dir = flip_direction[square][index];
    } while(flip_direction[square][index] != 0); // While there are directions to check left.
    
    return false; // If no direction resulted in a flip this move is invalid.
  }
  
  
  /**
   * 
   * @param player
   * @return Returns the opponent. Check GameConstants for correct implementation.
   */
  private int oppOf(int player)
  {
    return 2 - player;
  }

    
  /**
   * 
   * @param square The square to play to.
   * @param inc in
   * @param player
   * @param opp
   * @return
   */
  private boolean AnyDrctnlFlips(int square, int dir, int player, int opp)
  {
    int pos = square;
    
    do
    {
      pos = pos + dir;
    } while( board[pos] == opp );
    
    if( board[pos] == player && pos != (square + dir)){
      return true;
    }
    else
      return false;
  }
  
  
  /**
   * 
   * @param square The square to play to.
   * @param player The player who plays.
   */
  public void setStones(int square, int player)
  {
    int flipped = 0;
    int index = 0;
    int dir = flip_direction[square][index];
    
    board[square] = player;
    do 
    {
      flipped += DrctnlFlips(square, dir, player, oppOf(player));
      ++index;
      dir = flip_direction[square][index];
    } while(flip_direction[square][index] != 0);    // While there are directions to check left.
 
    if(player == RED){
      gain_red[disks_played] = 1 + flipped;
      gain_green[disks_played] = -flipped;
    }
    else{
      gain_green[disks_played] = 1 + flipped;
      gain_red[disks_played] = -flipped;
    }
  }
  
  
  /**
   * 
   * @param square The square we played.
   * @param dir    The direction to check.
   * @param player  
   * @param opp
   * @return Returns the number of stones flipped.
   */
  private int DrctnlFlips(int square, int dir, int player, int opp)
  {
    int flipped = 0;
    int pos = square + dir;
    
    while( board[pos] == opp )
      pos += dir;
    
    if( board[pos] == player ){
      do{
        pos -= dir;
        if( board[pos] == opp ){
          board[pos] = player;
          ++flipped;
        }
      } while(board[pos] != player );
      return flipped;
    }
    else
      return 0;
  }
  
  protected boolean isEmpty(){
	  int pos;
	    for (int i = 0; i < 10; ++i) {
	      for (int j = 0; j < 10; ++j) {
	        pos = 10 * i + j;
	        if(board[pos] != EMPTY)
	        	return false;
	      }
	    }
	    return true;
  }
 
  /**
   * Initializes the flip_direction table for each field of the GameBoard.
   */
  private void init_flip_direction(){
    
    // Iterate through every field on the board.
    for (int pos = 0; pos < GameConstants.dir_mask.length; ++pos){
        for (int k = 0, valid = 0; k < 8; ++k)         
          if ( (GameConstants.dir_mask[pos] & (1 << k) ) > 0){      
            flip_direction[pos][valid] = GameConstants.move_offset[k];
            ++valid;
          }
    }
  }
}