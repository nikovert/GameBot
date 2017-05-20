package HALclean;

public class OutOfTimeException extends RuntimeException {

  int movecounter;
  
  public OutOfTimeException(){}
  
  public OutOfTimeException(int movecounter){
    this.movecounter = movecounter;
  }
}
