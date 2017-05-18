package HALtests;


import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;

public class HAL implements ReversiPlayer {
	//implements alpha beta and depth calculation by time
	
	//greater weighting of the total amount of tiles

		/**
		 * Die Farbe des Spielers.
		 */
		//protected static int player = 0;
		//protected static int opponent = 0;
		private long timeout;
		protected int maxdepth;
		protected int traveldepth;
		protected final static int passvalue = -64;
		
		// Game information
		protected static int player;
		protected static int opponent;
		private BitBoard board;
		private Eval eval;
		/**
		 * Konstruktor, der bei der Gründung eines Bots eine Meldung auf den
		 * Bildschirm ausgibt.
		 */
		public HAL()
		{
			System.out.println("A.I. erstellt.");
		}

		/**
		 * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
		 * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
		 * 
		 * @see reversi.ReversiPlayer
		 */
		public void initialize(int player, long timeout) {
		    // Opponent player
		    if (player == reversi.GameBoard.RED)
		      opponent = reversi.GameBoard.GREEN;
		    else
		      opponent = reversi.GameBoard.RED;

		    // Timeout
		    this.timeout = timeout;
		    if (timeout > 5000)
		      this.timeout = Long.MAX_VALUE;
		    // traveldepth
		    traveldepth = 7;
		    // Player
		    if (player == reversi.GameBoard.RED) {
		      System.out.println("HAL ist Spieler RED.");
		      this.player = GameConstants.RED;
		    } else {
		      System.out.println("HAL ist Spieler GREEN.");
		      this.player = GameConstants.GREEN;
		    }

		    // Initialize Eval
		    eval = new Eval();
		    
		    // Initialize GameBoard
		    board = new BitBoard(this.player, player, opponent);
		  }
		
		/**
		 * 
		 * @param BitBoard gb
		 * @return a list of all possible moves
		 */
		/*
		private static ArrayList<Coordinates> possibleMoves(GameBoard gb, int moveplayer){
			ArrayList<Coordinates> possible = new ArrayList<Coordinates>();
			for(int i=1; i<9; i++){ //loop for Column and for row
				Coordinates result;
				for(int j=1; j<9; j++){
					result = new Coordinates(i, j);
					if(gb.checkMove(moveplayer, result)){
						possible.add(result); //return the result, no need to keep checking
					}
				}
			}
			return possible;
		}
		*/
			
		
		
		
		/**
		 * 
		 * @param the Gameboard
		 * @param the start time
		 * @param alpha value
		 * @param beta value
		 * @param depth the depth (used to see how deep we go)
		 * @param last (the last made move)
		 * @return
		 */
		protected int min(long start, int alpha, int beta, int depth, int lastCoordinate, int lastmovescount) throws RuntimeException{
			long timediff = System.currentTimeMillis()-start;
			if(timediff > timeout-500){
				throw new RuntimeException("time ran out");
			}

			if(depth == traveldepth){
				return eval.getWeight(board, lastCoordinate, depth, lastmovescount); //go back up from here
			}
			
			board.generate_all(opponent);
			int[] possible = board.getAllMoves(); //possibleMoves(gb, opponent);
			
			if(possible.length == 0){
				return passvalue;
			}

			//GameBoard test;
			int min = beta, at = 0, tmp = 0;
			
			for(int i = 0; i < possible.length; i++){
				//should we prune
				if(eval.checkPrune(board, possible[i], (board.countStones(player)+board.countStones(opponent)), opponent)){
					//System.out.println("Pruning!");
					if(++i >= possible.length){
						//System.out.println("Pruning and breaking!");
						break;
					}
				}
				
				board.make_move(possible[i], opponent); //make the move
				tmp = max(start, alpha, min, depth+1, possible[i], possible.length);
				
				tmp = tmp%1000;
				if(tmp < min){ // see if the move is good
					min = tmp; //save the move if it is
					at = i; 
					if (min <= alpha){
						break;
					}
				}
				board.undo_move();
			}
			min = min%1000;
			if(min < 0){
				min = min*(-1);
				min = at*1000 + min; //add at to the number
				min = min*(-1);
			}
			else{
				min = at*1000 + min; //add at to the number
			}
			
			return min;
		}
		

		/**
		 * 
		 * @param the Gameboard
		 * @param the start time
		 * @param alpha value
		 * @param beta value
		 * @param depth the depth (used to see how deep we go)
		 * @param last (the last made move)
		 * @return
		 */
		protected int max(long start, int alpha, int beta, int depth, int lastCoordinate, int lastmovescount)  throws RuntimeException{
			long timediff = System.currentTimeMillis()-start;
			if(timediff > timeout-500){
				throw new RuntimeException("time ran out");
			}
			
			if(depth == traveldepth){
				return eval.getWeight(board, lastCoordinate, depth, lastmovescount); //go back up from here
			}

			int[] possible = board.getAllMoves();
			
			if(possible.length == 0){
				return passvalue;
			}
			
			
			int max = alpha, at = 0, tmp = 0;
			
			for(int i = 0; i < possible.length; i++){

				//should We prune
				if(eval.checkPrune(board, possible[i], (board.countStones(player)+board.countStones(opponent)), player)){
					//System.out.println("Pruning!");
					if(++i >= possible[i]){
						//System.out.println("Pruning and breaking!");
						break;
					}
				}
				
				board.make_move(possible[i], player); //make the move
				tmp = min(start, max, beta, depth+1, possible[i], possible.length);
				
				tmp = tmp%1000;
				if(tmp > max){ // see if the move is good
					max = tmp; //save the move if it is
					at = i; // eventuel nur bei depth == 0
					if (max >= beta){
						break;
					}
				}
				board.undo_move();
			}
			max = max%1000;
			if(max < 0){
				max = max*(-1);
				max = at*1000 + max; //add at to the number
				max = max*(-1);
			}
			else{
				max = at*1000 + max; //add at to the number
			}
			
			return max;
		}
		

		/**
		 * 
		 * @param gb
		 * @param alpha
		 * @param beta
		 * @param depth
		 * @param player
		 * @return
		 * @throws RuntimeException
		 */
		/*
		protected int endgamesearch(GameBoard gb, long start, int alpha, int beta, int depth, int player)  throws RuntimeException{
			long timediff = System.currentTimeMillis()-start;
			if(timediff > timeout-500){
				throw new RuntimeException("time ran out");
			}
			int opponent;
			if(player == gb.GREEN) 
				opponent = gb.RED;
			else 
				opponent = gb.GREEN;
			
			
			if((gb.countStones(player)+gb.countStones(opponent)) == 64 || depth == traveldepth){
				if(gb.countStones(opponent) == gb.countStones(player))
					return 0; //draw
				if(gb.countStones(opponent) > gb.countStones(player))//go back up from here
					return 1; //win
				else
					return -1; //lose
			}
			
			GameBoard test = gb.clone();
			ArrayList<Coordinates> possible = possibleMoves(test, player);
			
			boolean firstchild = true;
			int score = alpha; // maybe not alpha;
			
			for(int i = 0; i< possible.size(); i++){
				test.makeMove(player, possible.get(i));
				if(firstchild){
					firstchild = false;
					score = -(endgamesearch(test, start, -beta, -alpha, depth+1, opponent));
				}else{
					score = -(endgamesearch(test, start, -alpha-1, -beta-1, depth+1, opponent));
					if(alpha < score && score < beta)
						score = -(endgamesearch(test, start, -beta, -beta-1, -score, opponent));
				}
				if(score > alpha)
					alpha = score;
				if(alpha >= beta)
					break;
			}
			return alpha;
	}
*/

		/**
		 * 
		 * @return the Coordinates for the best move
		 */
		private Coordinates bestMove(){
			long start = System.currentTimeMillis();
			board.generate_all(player);
			int[] possible = board.getAllMoves(); //possibleMoves(gb, opponent);
			
			if(possible.length == 0){
				return null;
			}
			//check corner
			/*Coordinates corner = eval.checkCorners(board);
			
			if(corner != null){
				return corner;
			}
			*/

			int at = 0;
			/*
			if((gb.countStones(player)+gb.countStones(opponent) + traveldepth) >= 64){
				try{	
					at = endgamesearch(gb.clone(), start, -1, 1, 0, this.player);
				}catch(RuntimeException e){
					traveldepth--;
				}
				at = Math.abs(at);
				at = (at - at%1000)/1000;
				return possible.get(at);
			}
			*/

			
			try{	
				at = max(start, -64, 64, 0, 0, possible.length);
			}catch(RuntimeException e){
				traveldepth--;
			}
			
			long timediff = System.currentTimeMillis() - start;
			int traveldepthtmp = traveldepth;
			int prevat = at;
			
			while(timediff < (timeout-1000)){
				traveldepth++;
				try{
					at = max(start, -64, 64, 0, 0, possible.length);
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
			at = (at - at%1000)/1000;
			return board.ArraytoCoordinate(possible[at]);
		}
		
		

		/**
		 * Macht einen Zug für den HumanPlayer, indem der Benutzer zur Eingabe eines
		 * Zuges aufgefordert wird. Diese Methode wird von {@link reversi.Arena}
		 * abwechselnd aufgerufen.
		 * 
		 * @see reversi.ReversiPlayer
		 * @return Der Zug des HumanPlayers.
		 */
		public Coordinates nextMove(GameBoard gb)
		{
			System.out.print("HAL ");
			if (player == GameConstants.RED)
			{
				System.out.print("(RED) ");
			}
			else if (player == GameConstants.GREEN)
			{
				System.out.print("(GREEN) ");
			}
			board.update_opp_move(gb);
			return bestMove();
		} 

		/**
		 * Used for the System output
		 */
		private static String tab(int node){
			String tmp = "";
			for(int i= 0; i < node; i++)
				tmp +="\t"+"*";
			return tmp;
		}
}

