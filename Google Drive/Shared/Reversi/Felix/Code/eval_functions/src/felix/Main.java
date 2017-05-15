package felix;

public class Main {

  public static void main(String[] args) {
  }
  
  private static void testMillis(){ 
    long a = System.currentTimeMillis();
    long b;
    long begin = a;
    
    for(int i = 0; i < 10000000; ++i){
      b = System.currentTimeMillis();
      if(b < a)
        System.out.println("Error");
      a = b;
    }

    System.out.println("This took: " + (System.currentTimeMillis()-begin) + "ms");
    
  }
}
