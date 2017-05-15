package evaluation;

public class Evaluation {

  // Class that implements the evaluation function used by the Othello-Bot Iago from 1981.
  // NICHT FERTIG!!!
  public class Iago {
    
    /**
     *
     * Bmkg: Siehe "A world-championship-level Othello program.pdf" für die Implementierung.
     *
     * @param pos The current Game-Board representation.
     * @return The approximate value of this position, assuming we are unable to reach the end of the game.
     */
    public float eval_Iago(Position pos) {
      int MoveNumber = getNumberOfMoves(pos);
      float eval =  ESAC(MoveNumber)*EdgeStability + 36*InternalStability +
                  CMAC(MoveNumber)*CurrentMobility + 99*PotentialMobility;
      return eval;
    }
    
    private float ESAC(int MoveNumber){
      return 312 + 6.24*MoveNumber;
    }
    
    private float CMAC(int MoveNumber){
      if(currentMove < 26)
        return 50 + 2*MoveNumber;
      else
        return 75 + MoveNumber;
    }
  }
}
