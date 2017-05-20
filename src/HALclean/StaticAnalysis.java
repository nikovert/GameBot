package HALclean;


public class StaticAnalysis {
	
    static final int GREENSQ = GameConstants.GREENSQ;
    static final int EMPTY = GameConstants.EMPTY;
    static final int REDSQ = GameConstants.REDSQ;
    private int opponent;

	/**
	 * May be used by the evaluation function to get a value how the advantage of the entire board
	 * @param board
	 * @param player
	 * @return the advantage of the player
	 */
	 protected int staticAnalysis(BitBoard board, int player){
		 int advantage = 0, opponentadvantage = 0;
		 int discs = 0, opponentdiscs = 0; //counts the disc owned by a player
		 
		 if(player == REDSQ)
			 opponent = GREENSQ;
		 else
			 opponent = REDSQ;
		 
		  for(int i = 1; i <= 8; i++){
			  for(int j = 1; j <= 8; j++){
				  if(board.board[i][j] == player){
					  advantage += getValue(i,j, player);
					  discs++;
				  }/*
				  else
					  if(board.board[i][j] != EMPTY){
						  opponentadvantage += getValue(i,j, opponent);
						  opponentdiscs++;
					  }
				   */
			  }
	 		}
		  advantage = advantage/discs;
		  //opponentadvantage = opponentadvantage/opponentdiscs;
		  
		  return advantage;
	  }

	 /*weight of fields:
	 100  -8  8  6  6  8  -8 100
	 -8  -24 -4 -3 -3 -4 -24 -8
	  8   -4  7  4  4  7  -4  8
	  6   -3  4  0  0  4  -3  6
	  6   -3  4  0  0  4  -3  6
	  8   -4  7  4  4  7  -4  8
	 -8  -24 -4 -3 -3 -4 -24 -8
	 100   -8  8  6  6  8  -8 100
	*/
	private int getValue(int col, int row, int player) {
		switch (col){
			case 1: col = 8;
			case 8: 
				if(row == 1 || row == 8){
					//Corner
					return 100;
				}else {
					if(row == 2 || row == 7){
						//C Square
						return -8;
					}else {
						if(row == 3 || row == 6){
							//A Square
							return 8;
						}else{
							//B Square
							return 6;
						}
					}
				}
			case 2: col = 7;
			case 7: 
				if(row == 1 || row == 8){
					//C Square
					return -8;
				}else {
					if(row == 2 || row == 7){
						//X Square
						return -24;
					}else {
						if(row == 3 || row == 6)
							return -4;
						else
							return -3;
					}
				}
			case 3: col = 6;
			case 6: 
				if(row == 1 || row == 8){
					//A Square
					return 8;
				}else{
					if(row == 2 || row == 7)
						return -4;
					else {
						if(row == 3 || row == 6)
							return 7;
						else
							return 4;
					}
				}
			case 4: col = 5;
			case 5: 
				if(row == 1 || row == 8){
					//B Square
					return 6;
				}else{
					if(row == 2 || row == 7)
						return -3;
					else {
						if(row == 3 || row == 6)
							return 4;
						else
							return 0;
					}
				}
			default:
				System.err.println("weight not found for given Coorsinate");
		}
		return 0;
	}
}
