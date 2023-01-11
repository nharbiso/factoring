import java.util.Arrays;
import java.math.BigInteger;
import javax.swing.JOptionPane;

public class FactoringQuadratics
{
   public static final Fraction[] nullInt = {new Fraction()};
   public static String factor(Fraction[] coefficients, String[] var, int[][] pow, String factor, boolean printNotFactorable)
   {    
      Fraction target = coefficients[0].multiply(coefficients[2]);
      boolean sum = target.compareTo(Fraction.ZERO) > 0, neg = coefficients[1].compareTo(Fraction.ZERO) < 0;
      Fraction[] mid = nullInt;
      for(Fraction x = Fraction.ONE; x.compareTo(target.abs()) <= 0; x = x.add(new Fraction(BigInteger.ONE, target.getDenom())))
      {
         if(sum && (target.divide(x).add(x).equals(coefficients[1].abs())))
         {
            mid = new Fraction[] {x, target.divide(x)};
            if(neg)
            {
               mid[0] = mid[0].multiply(Fraction.NEGONE);
               mid[1] = mid[1].multiply(Fraction.NEGONE);
            }
            break;
         }
         else if(!sum && target.abs().divide(x).subtract(x).equals(coefficients[1]))
         {
            mid = new Fraction[] {x.multiply(Fraction.NEGONE), target.abs().divide(x)};
            break;
         }
      }
      if(Arrays.equals(mid, nullInt))
         if(coefficients[0].isSq() && coefficients[2].isSq() && var.length == 1 && pow[0][0] == 4)
         {
            Fraction a = coefficients[0].sqrt(), b, c = coefficients[2].sqrt();
            Fraction ifPos = a.multiply(c).multiply(new Fraction("2/1")).subtract(coefficients[1]);
            Fraction ifNeg = a.multiply(c).multiply(new Fraction("-2/1")).subtract(coefficients[1]);
            if(ifPos.isSq())
               b = ifPos.sqrt();
            else if(ifNeg.isSq())
            {
               b = ifNeg.sqrt();
               c = c.multiply(Fraction.NEGONE);
            }
            else
            {
               if(printNotFactorable && factor.equals(""))
                  return "Could not be factored.";
               else
                  return factor+"("+Factoring.factorString(coefficients[0], false)+Factoring.getOneVariable(var[0], 4)+Factoring.midString(coefficients[1], var[0], 2, true)+Factoring.constantString(coefficients[2], true)+")";
            }  
            return factor/*+FactoringQuartics.printQuad(a, b, c, var[0])+FactoringQuartics.printQuad(a, b * -1, c, var[0])*/;
         }
         else if(printNotFactorable && factor.equals(""))
            return "Could not be factored.";
         else
            return factor+"("+Factoring.factorString(coefficients[0], false)+Factoring.getVariable(var, pow[0])+Factoring.midString(coefficients[1], var, pow[1], true)+Factoring.midString(coefficients[2], var, pow[2], true)+")";
      
      Fraction[] factors1 = {coefficients[0], mid[0]};
      Fraction[] factors2 = {mid[1], coefficients[2]};
      Fraction div1 = Factoring.getNumFactor(factors1);
      Fraction div2 = Factoring.getNumFactor(factors2);
      if(!(factors1[0].equals(factors2[0]) && factors1[1].equals(factors2[1])))
         return "Could not be factored.";
      Fraction[] binomial1 = factors1;
      Fraction[] binomial2 = {div1, div2};
      pow = toBinomial(pow);
      return FactoringBinomials.factor(binomial1, var, pow, factor, false) + FactoringBinomials.factor(binomial2, var, pow, "", false);
   }
   private static int[][] toBinomial(int[][] y)
   {
      y = FactoringBinomials.divide(y, 2);
      int[][] z = new int[2][y.length];
      z[0] = y[0];
      z[1] = y[2];
      return z;
   }
}