package HALclean;


public class StaticAnalysis {

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
    /**
     * @param the last made move
     * @return the value of the move, based on a static analysis
     */
	protected int staticAnalysis(int square) {
		int col = (square % 10);
		int row = (square / 10);
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
