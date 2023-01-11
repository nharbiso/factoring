import java.util.Arrays;
public class FactoringByGrouping
{
   private static int[] nullArr = {0};
   public static String factor(Fraction[] co, String[] var, int[][] pow, String fac)
   {
      String s = factorByGrouping(co, var, pow, fac);
      if(!s.equals("Could not be factored"))
         return s;
      
      int[] indexs = findSquares(co, pow);
      int[] pos = findSq(co, indexs, true);
      int[] neg = findSq(co, indexs, false);
       
      for(int x = 0; x < pos.length; x++)  
         for(int y = 0; y < neg.length; y++)
         {
            String tmp = factorBySquares(pos[x], neg[y], co, var, pow, fac);
            if(tmp != "not factorable")
               return tmp;
         }
      
      return "Could not be factored.";
   }
   //factoring by grouping
   private static String factorByGrouping(Fraction[] co, String[] var, int[][] pow, String fac)
   {
      String c1 = compare(new int[] {0, 1, 2, 3}, Arrays.copyOf(co, co.length), Arrays.copyOf(var, var.length), copyOf(pow), fac);
      if(!c1.equals(""))
         return c1;
      String c2 = compare(new int[] {0, 2, 1, 3}, Arrays.copyOf(co, co.length), Arrays.copyOf(var, var.length), copyOf(pow), fac);
      if(!c2.equals(""))
         return c2;
      String c3 = compare(new int[] {0, 3, 1, 2}, Arrays.copyOf(co, co.length), Arrays.copyOf(var, var.length), copyOf(pow), fac);
      if(!c3.equals(""))
         return c3;
      return "Could not be factored";
   }
   private static int[][] copyOf(int[][] z)
   {
      int[][] y = new int[z.length][z[0].length];
      for(int x = 0; x < y.length; x++)
         y[x] = Arrays.copyOf(z[x], z[x].length);
      return y;
   }
   private static String compare(int[] pos, Fraction[] co, String[] var, int[][] pow, String fac)
   {
      //co.length = 4, var.length = ?, pow.length = 4, pow[0].length = var.length
      Fraction[] co1 = {co[pos[0]], co[pos[1]]}; //rearranging of the coefficients
      Fraction[] co2 = {co[pos[2]], co[pos[3]]};
      int[][] pow1 = {pow[pos[0]], pow[pos[1]]}; // and pows
      int[][] pow2 = {pow[pos[2]], pow[pos[3]]};
      
      Fraction coefficient1 = Factoring.getNumFactor(co1); //common factors of first part
      Fraction coefficient2 = Factoring.getNumFactor(co2); //common factors of second part
      Fraction[] coefficients = {coefficient1, coefficient2}; //coefficients of first factor
      int[] var1 = findCommonPow(pow1); //common variables of first part (does dividing)
      int[] var2 = findCommonPow(pow2); //common variables of second part
      
      if(!compare(pow1, pow2, co1, co2, coefficients))
         return "";
        
      return FactoringBinomials.factor(coefficients, var, new int[][] {var1, var2}, fac, false)
         +FactoringBinomials.factor(co1, var, pow1, "", false);
   }
   private static int[] findCommonPow(int[][] p)
   {
      int[] z = new int[p[0].length];
      for(int x = 0; x < z.length; x++)
         if(p[0][x] != 0 && p[1][x] != 0)
         {
            if(p[0][x] > p[1][x])
            {
               z[x] = p[1][x];
               p[0][x] -= p[1][x];
               p[1][x] = 0;
            }
            else if(p[1][x] > p[0][x])
            {
               z[x] = p[0][x];
               p[1][x] -= p[0][x];
               p[0][x] = 0;
            }
            else
            {
               z[x] = p[0][x];
               p[0][x] = p[1][x] = 0;
            }
         }
      return z;
   }
   private static boolean compare(int[][] p1, int[][] p2, Fraction[] co1, Fraction[] co2, Fraction[] c)
   {
      if(Arrays.equals(p1[0], p2[0]) && Arrays.equals(p1[1], p2[1]) && equal(co1, co2))
         return true;
      if(flipped(p1, p2))
      {
         if(co2[1].compareTo(Fraction.ZERO) < 0)
         {
            co2 = new Fraction[] {co2[0].divide(Fraction.NEGONE), co2[1].divide(Fraction.NEGONE)};
            c[1].multiply(Fraction.NEGONE);
         }
         co2 = new Fraction[] {co2[1], co2[0]};
         if(Arrays.deepEquals(co1, co2))
            return true;
      }
      
      return false;
   }
   private static boolean equal(Fraction[] o1, Fraction[] o2)
   {
      if(o1.length != o2.length)
         return false;
      for(int x = 0; x < o1.length; x++)
         if(!o1[x].equals(o2[x]))
            return false;
      return true;
   }
   private static boolean flipped(int[][] p1, int[][] p2)
   {
      int[] tmp = p2[0];
      p2[0] = p2[1];
      p2[1] = tmp;
      return Arrays.equals(p1[0], p2[0]) && Arrays.equals(p1[1], p2[1]);
   }
   //other factoring methods
   private static int[] findSquares(Fraction[] co, int[][] pow)
   {
      int[] z = new int[4];
      int l = 0;
      for(int x = 0; x < pow.length; x++)
         for(int y = 0; y < pow[x].length; y++)
         {
            if(pow[x][y] != 0 && pow[x][y] % 2 != 0)
               break;
            if(y == pow[x].length - 1 && co[x].abs().isSq())
            {
               z[l] = x;
               l++;
            }
         }
      int[] f = new int[l];
      for(int x = 0; x < f.length; x++)
         f[x] = z[x];
      return f;
   }
   private static int[] findSq(Fraction[] co, int[] i, boolean pos)
   {
      int[] z = new int[i.length];
      int l = 0;
      for(int x = 0; x < i.length; x++)
      {
         if(pos && co[i[x]].compareTo(Fraction.ZERO) > 0)
         {
            z[l] = i[x];
            l++;
         }
         else if(!pos && co[i[x]].compareTo(Fraction.ZERO) < 0)
         {
            z[l] = i[x];
            l++;
         }
      }
      int[] fin = new int[l];
      for(int x = 0; x < fin.length; x++)
         fin[x] = z[x];
      return fin;
   }
   private static String factorBySquares(int pos, int neg, Fraction[] c, String[] var, int[][] p, String fac)
   {
      int[][] pow = copyOf(p);
      Fraction[] co = Arrays.copyOf(c, c.length);
      Fraction[] co1 = {co[pos].sqrt(), co[neg].abs().sqrt()};
      Fraction[] co2 = {co[pos].sqrt(), co[neg].abs().sqrt().multiply(Fraction.NEGONE)};
      int[][] pow1 = {FactoringBinomials.divide(pow[pos], 2), FactoringBinomials.divide(pow[neg], 2)};
      int[][] pow2 = {Arrays.copyOf(pow1[0], pow1[0].length), Arrays.copyOf(pow1[1], pow1[1].length)};
      int[] otheri = {-1, -1};
      for(int x = 0; x < 4; x++)
         if(x != pos && x != neg)
         {
            if(otheri[0] == -1)
               otheri[0] = x;
            else
               otheri[1] = x;
         }
      Fraction[] co3 = {co[otheri[0]], co[otheri[1]]};
      int[][] pow3 = {pow[otheri[0]], pow[otheri[1]]};
      int[][] tmp1 = {Arrays.copyOf(pow3[0], pow3[0].length), Arrays.copyOf(pow3[1], pow3[1].length)};
      Fraction[] factor = {Fraction.ZERO, Factoring.getNumFactor(co3)};
      Fraction[] tmp2 = Arrays.copyOf(factor, factor.length);
      int[] var3 = findCommonPow(pow3);
      
      if(compare(pow1, pow3, co1, co3, factor))
         return FactoringBinomials.factor(co1, var, pow1, fac, false)+"("+Factoring.midString(co2[0], var, pow2[0], false)
            +Factoring.midString(co2[1], var, pow2[1], true)+Factoring.midString(factor[1], var, var3, true)+")";
      else if(compare(pow2, tmp1, co2, co3, tmp2))
         return FactoringBinomials.factor(co2, var, pow2, fac, false)+"("+Factoring.midString(co1[0], var, pow1[0], false)
            +Factoring.midString(co1[1], var, pow1[1], true)+Factoring.midString(tmp2[1], var, var3, true)+")";
       
      //try to obtain a square using the other indices (otheri)
      pow = copyOf(p);
      co = Arrays.copyOf(c, c.length);
      boolean[] oiSq = {false, false};
      for(int x = 0; x < otheri.length; x++)
         for(int y = 0; y < pow[x].length; y++)
         {
            if(pow[otheri[x]][y] != 0 && pow[otheri[x]][y] % 2 != 0)
               break;
            if(y == pow[otheri[x]].length - 1 && co[otheri[x]].abs().isSq())
               oiSq[x] = true;
         } 
      for(int x = 0; x < otheri.length; x++)
         if(oiSq[x])
         {
            int tmp = otheri[(x + 1) % 2];
            if(co[otheri[x]].compareTo(Fraction.ZERO) > 0 && formsPerfectSq(pow, co, pos, tmp, otheri[x]))
            {
               String s = "("+Factoring.midString(co[pos].sqrt(), var, FactoringBinomials.divide(pow[pos], 2), false)+
                  Factoring.midString(co[otheri[x]].sqrt().multiply(signOf(co[tmp])), var, FactoringBinomials.divide(pow[otheri[x]], 2), true);
               return s+Factoring.midString(co[neg].abs().sqrt(), var, FactoringBinomials.divide(pow[neg], 2), true)+")"+s
                  +Factoring.midString(co[neg].abs().sqrt().multiply(Fraction.NEGONE), var, FactoringBinomials.divide(pow[neg], 2), true)+")";
            }
            else if(co[otheri[x]].compareTo(Fraction.ZERO) < 0 && formsPerfectSq(pow, co, neg, tmp, otheri[x]))
            {
               String s = "("+Factoring.midString(co[pos].sqrt(), var, FactoringBinomials.divide(pow[pos], 2), false);
               return s+Factoring.midString(co[neg].abs().sqrt(), var, FactoringBinomials.divide(pow[neg], 2), true)
                  +Factoring.midString(co[otheri[x]].abs().sqrt().multiply(signOf(co[tmp].multiply(Fraction.NEGONE))), var, FactoringBinomials.divide(pow[otheri[x]], 2), true)+")"+s
                  +Factoring.midString(co[neg].abs().sqrt().multiply(Fraction.NEGONE), var, FactoringBinomials.divide(pow[neg], 2), true)
                  +Factoring.midString(co[otheri[x]].abs().sqrt().multiply(signOf(co[tmp].multiply(Fraction.NEGONE))).multiply(Fraction.NEGONE), var, FactoringBinomials.divide(pow[otheri[x]], 2), true)+")";
            }
         }
      
      return "not factorable";           
   }
   private static boolean formsPerfectSq(int[][] pow, Fraction[] co, int f, int s, int t)
   {
      if(co[f].abs().sqrt().multiply(co[t].abs().sqrt()).multiply(new Fraction("2/1")).compareTo(co[s].abs()) != 0)
         return false;
      for(int x = 0; x < pow[0].length * 2; x++)
      {
         if(x < pow[0].length)
         {
            if(pow[f][x] != 0 && pow[s][x] != 0 && pow[f][x] / 2.0 != pow[s][x])
               return false;
         }
         else 
         {
            if(pow[t][x - pow[0].length] != 0 && pow[s][x - pow[0].length] != 0 && pow[t][x - pow[0].length] / 2.0 != pow[s][x - pow[0].length])
               return false;
         }
      }
      return true;
   }
   private static Fraction signOf(Fraction x)
   {
      if(x.compareTo(Fraction.ZERO) > 0)
         return Fraction.ONE;
      if(x.compareTo(Fraction.ZERO) < 0)
         return Fraction.NEGONE;
      return Fraction.ZERO;
   }
}