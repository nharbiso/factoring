package version1;

public class FactoringBinomials
{
   public static String factor(Fraction[] coefficients, String[] var, int[][] pow, String factor, boolean printNotFactorable)
   {
      if(Factoring.canDivide(pow[0], 2) && Factoring.canDivide(pow[1], 2) && isDifOfSqs(coefficients))
      {
         Fraction[] co = {coefficients[0].abs().sqrt(), coefficients[1].abs().sqrt()};
         return factor(co, var, divide(pow, 2), factor, false) + factor(neg(co), var, divide(pow, 2), "", false);
      }
      else if(Factoring.canDivide(pow[0], 3) && Factoring.canDivide(pow[1], 3) && areCubes(coefficients))
      {
         Fraction[] co = {coefficients[0].cbrt(), coefficients[1].cbrt()};
         return factor(co, var, divide(pow, 3), factor, false)+"("+Factoring.midString(co[0].multiply(co[0]), var, multiply(divide(pow[0], 3), 2), false)+
            Factoring.midString(co[0].multiply(co[1]).multiply(Fraction.NEGONE), var, divide(toArray(pow), 3), true)+Factoring.midString(co[1].multiply(co[1]), var, multiply(divide(pow[1], 3), 2), true)+")";
      }
      else if(printNotFactorable && factor.equals(""))
         return "Could not be factored.";
      else
         return factor+"("+Factoring.midString(coefficients[0], var, pow[0], false)+Factoring.midString(coefficients[1], var, pow[1], true)+")";
   }
   private static boolean isDifOfSqs(Fraction[] x)
   {
      return x[0].abs().isSq() && x[1].abs().isSq() && x[1].compareTo(Fraction.ZERO) < 0;
   }
   private static boolean areCubes(Fraction[] x)
   {
      return x[0].isCb() && x[1].isCb();
   }
   
   private static Fraction[] neg(Fraction[] x)
   {
      x[1] = x[1].multiply(Fraction.NEGONE);
      return x;
   }
   public static int[][] divide(int[][] z, int div)
   {
      int[][] s = new int[z.length][z[0].length];
      for(int x = 0; x < s.length; x++)
         for(int y = 0; y < s[0].length; y++)
            s[x][y] = z[x][y] / div;
      return s;
   }
   public static int[] divide(int[] z, int div)
   {
      int[] y = new int[z.length];
      for(int x = 0; x < z.length; x++)
         y[x] = z[x] / div;
      return y;
   }
   private static int[] multiply(int[] z, int div)
   {
      int[] y = new int[z.length];
      for(int x = 0; x < z.length; x++)
         y[x] = z[x] * div;
      return y;
   }
   private static int[] toArray(int[][] s)
   {
      int[] z = new int[s[0].length];
      int l = 0;
      for(int x = 0; x < s.length; x++)
         for(int y = 0; y < s[0].length; y++)
         {
            if(s[x][y] != 0)
               z[y] = s[x][y];
            l++;
         }
      return z;
   }
}