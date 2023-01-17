package version1;

public class FactoringPolynomials
{
   private static Fraction[] div;
   public static String factor(Fraction[] coefficients, String factor, int power, String var, boolean printNotFactorable)
   {
      if(power <= 2)
      {
         if(coefficients[1].equals(Fraction.ZERO) && coefficients.length == 3)
         {
            coefficients = new Fraction[] {coefficients[0], coefficients[2]};
            return FactoringBinomials.factor(coefficients, new String[] {var}, new int[][] {{power}, {0}}, factor, false);
         }
         else if(coefficients.length == 3 && power == 2)
            return FactoringQuadratics.factor(coefficients, new String[] {var}, new int[][] {{2}, {1}, {0}}, factor, false);
         else
            return FactoringBinomials.factor(coefficients, new String[] {var}, new int[][] {{power}, {0}}, factor, false);
      }
      else
      {
         Fraction[] posFactors = findRationalFact(coefficients[0].abs().findFactors(), coefficients[coefficients.length - 1].abs().findFactors());
         for(int x = 0; x < posFactors.length; x++)
         {
            Fraction sum = Fraction.ZERO;
            for(int y = 0; y <= power; y++)
               sum = sum.add(coefficients[y].multiply(posFactors[x].pow(power - y)));
            if(sum.equals(Fraction.ZERO))
            {
               Fraction[] divided = synDivide(coefficients, posFactors[x]);
               if(!divideCube(divided, posFactors[x]))
                  return factor(divided, factor+getVar(var, posFactors[x]), power - 1, var, false);
               else
                  return factor(div, factor+getVar(var, posFactors[x])+getCube(posFactors[x], var), power - 3, var, false);
            }
         }
         String s;
         if(power == 4)
            s = FactoringQuartics.factor(coefficients, factor, var, printNotFactorable);
         else
         {
            s = Factoring.factorString(coefficients[0], false)+Factoring.getOneVariable(var, power);
            if(!printNotFactorable || !factor.equals(""))
               s = factor+"("+s;
            else
               s = factor+s;
            for(int x = power - 1; x >= 0; x--)
               if(coefficients[power - x].compareTo(Fraction.ZERO) != 0 && x != 0)
                  s += Factoring.midString(coefficients[power - x], var, x, true);
               else if(x == 0)
                  s += Factoring.constantString(coefficients[power-x], true);
            if(!printNotFactorable || !factor.equals(""))
               s += ")";
         }
         return s;
      }
   }
   public static Fraction[] findRationalFact(Fraction[] f, Fraction[] s)
   {
      Fraction[] z = new Fraction[f.length * s.length];
      int count = 0;
      for(int x = 0; x < f.length; x++)
         for(int y = 0; y < s.length; y++)
         {
            Fraction posFact = s[y].divide(f[x]);
            if(notRepeated(posFact, z, count))
            {
               z[count] = posFact;
               count++;
            }
         }
      Fraction[] posFacts = new Fraction[count * 2];
      for(int x = 0; x < posFacts.length; x += 2)
      {
         posFacts[x] = z[x / 2];
         posFacts[x+1] = z[x / 2].multiply(Fraction.NEGONE);
      }
      return posFacts;
   }
   private static boolean notRepeated(Fraction ps, Fraction[] arr, int arrLength)
   {
      for(int x = 0; x < arrLength; x++)
         if(arr[x].equals(ps))
            return false;
      return true;
   }
   private static Fraction[] synDivide(Fraction[] z, Fraction y)
   {
      Fraction[] quotient = new Fraction[z.length - 1];
      quotient[0] = z[0];
      for(int x = 1; x < quotient.length; x++)
         quotient[x] = quotient[x - 1].multiply(y).add(z[x]);
      for(int x = 0; x < quotient.length; x++)
         quotient[x] = quotient[x].divide(new Fraction(y.getDenom(), Fraction.ONE));
      return quotient;
   }
   private static boolean divideCube(Fraction[] d, Fraction root)
   {
      Fraction[] dividend = new Fraction[d.length];
      for(int x = 0; x < dividend.length; x++)
         dividend[x] = d[x];
      Fraction[] divisor = notDecimal(root);
      Fraction[] result = new Fraction[dividend.length-divisor.length+1];
      for(int x = 0; x < result.length; x++)
      {
         result[x] = dividend[x].divide(divisor[0]); 
         for(int y = x; y < x + 3; y++)
            dividend[y] = dividend[y].subtract(result[x].multiply(divisor[y-x]));
      }
      div = result;
      return dividend[dividend.length-1].equals(Fraction.ZERO) && dividend[dividend.length-2].equals(Fraction.ZERO);
   }
   private static Fraction[] notDecimal(Fraction n)
   {
      Fraction x = new Fraction(n.getDenom(), Fraction.ONE);
      Fraction y = n.multiply(x); 
      return new Fraction[] {x.pow(2), x.multiply(y), y.pow(2)};
   }
   private static String getVar(String var, Fraction x)
   {
      Fraction y = new Fraction(x.getDenom(), Fraction.ONE);
      return "("+Factoring.factorString(y, false)+var+Factoring.constantString(x.multiply(y).multiply(Fraction.NEGONE), true)+")";
   }
   private static String getCube(Fraction n, String var)
   {
      Fraction[] cube = notDecimal(n);
      return "("+Factoring.factorString(cube[0], false)+Factoring.getOneVariable(var, 2)+Factoring.midString(cube[1], var, 1, true)+Factoring.constantString(cube[2], true)+")";
   }
}