import java.math.BigDecimal;
public class test
{
   public static void main(String[] args)
   {
      BigDecimal c = BigDecimal.valueOf(43.6);
      for(int x = 2; x <= 24; x++)
         c = c.add(new BigDecimal(40)).multiply(BigDecimal.valueOf(1.0009));
      System.out.println(c.divide(BigDecimal.valueOf(1.0009)));
   }
}