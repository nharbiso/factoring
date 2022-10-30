import java.util.*;
import java.math.BigInteger;
/**
*  Class utilized to factor an quartic expression. 
*  Formulas and algorithm obtained from
*  https://www.maa.org/sites/default/files/Brookfield2007-103574.pdf
*  @author Nathan Harbison
*/
public class FactoringQuartics
{
   /**
   *  Factors a given quartic expression into two unfactorable quadratics if possible
   *  
   *  @param exp the expression to be factored
   *  @param var the sole variable in the expression
   *  @return a string representing the factored expression, or the given expression if unfactorable
   */
   public static String factor(Expression exp, char var)
   {
      int power = 4; //exp.getPow(0, var)
      
      Fraction[] coef = new Fraction[5];
      for(int x = 0; x < exp.size(); x++)
         coef[x] = new Fraction(exp.getCoef(x), exp.getCoef(0));  
      
      //converts quartic from form of f(x) = ax^4 + bx^3 + cx^2 + dx + e -> g(x) = x^4 + cx^2 + dx + e
      //g(x) = f(x - b / 4a) / a
      boolean reduced = false;
      Fraction rCoef[] = new Fraction[4];
      rCoef[0] = coef[0];
      if(coef[1].getNum().compareTo(BigInteger.ZERO) != 0)
      {
         rCoef[1] = coef[2].subtract(coef[1].pow(2).multiply(new Fraction("3/8")));
         rCoef[2] = coef[3].add(coef[1].pow(3).multiply(new Fraction("1/8"))).subtract(coef[1].multiply(coef[2]).multiply(new Fraction("1/2")));
         rCoef[3] = coef[4].subtract(coef[1].pow(4).multiply(new Fraction("3/256"))).add(coef[1].pow(2).multiply(coef[2]).multiply(new Fraction("1/16"))).subtract(coef[1].multiply(coef[3]).multiply(new Fraction("1/4")));
         reduced = true;
      }
      else if(coef[1].getNum().compareTo(BigInteger.ZERO) == 0)
         System.arraycopy(coef, 2, rCoef, 1, 3);
      
      //resolvent = z^3 + 2cz^2 + (c^2 - 4e)z - d^2
      Fraction[] resolvent = {new Fraction(1, 1), rCoef[1].multiply(new Fraction(2)), rCoef[1].pow(2).subtract(rCoef[3].multiply(new Fraction(4))), 
                              rCoef[2].pow(2).inverse()};
      Fraction sqFactor = findResolvSqFactor(resolvent);
      if(!sqFactor.equals(new Fraction()))
         return quadFactors(sqFactor, coef, rCoef, resolvent, reduced, var);
      
      exp.removeZeroes();
      return "(" + exp.toString() + ")";
   }
   /**
   *  Finds a factor of the polynomial resolvent that is a perfect square
   *  @param resolvent an array containing the resolvent's coefficients
   *  @return a perfect square factor of the resolvent
   */
   private static Fraction findResolvSqFactor(Fraction[] resolvent)
   {
      Fraction[] f = resolvent[3].findSqFactors();
      for(int x = 0; x < f.length; x++)
      {
         Fraction sum = f[x].pow(3).add(f[x].pow(2).multiply(resolvent[1])).add(f[x].multiply(resolvent[2])).add(resolvent[3]);
         if(sum.getNum().compareTo(BigInteger.ZERO) == 0)
            return f[x];
      }
      return new Fraction();
   }
   
   /**
   *  Finds and returns a string containing the quadratic factors of the polynomial
   *  @param fac the perfect square factor of the resolvent
   *  @param coef the coefficients of the original quartic
   *  @param rCoef the coefficients of the reduced quartic
   *  @param resolvent the coefficients of the resolvent of the quartic
   *  @param reduced whether or not the quartic was reduced
   *  @param var the sole variable in the polynomial expression
   *  @return a string containing the quadratic factors of the quartic
   */
   private static String quadFactors(Fraction fac, Fraction[] coef, Fraction[] rCoef, Fraction[] resolvent, boolean reduced, char var)
   {
      Fraction h = fac.sqrt();
      Fraction k, kpr;
      if(h.getNum().compareTo(BigInteger.ZERO) != 0)
      {
         k = new Fraction(BigInteger.ONE, h.multiply(new Fraction(2))).multiply(h.pow(3).add(h.multiply(rCoef[1])).subtract(rCoef[2]));
         kpr = new Fraction(BigInteger.ONE, h.multiply(new Fraction(2))).multiply(h.pow(3).add(h.multiply(rCoef[1])).add(rCoef[2]));
      }
      else
      {
         k = rCoef[1].add(resolvent[2].sqrt()).multiply(new Fraction("1/2"));
         kpr = rCoef[1].subtract(resolvent[2].sqrt()).multiply(new Fraction("1/2"));
      }
      List<Fraction> factor1 = Arrays.asList(new Fraction[] {new Fraction(1, 1), h, k});
      List<Fraction> factor2 = Arrays.asList(new Fraction[] {new Fraction(1, 1), h.inverse(), kpr});
      if(reduced)
      {
         factor1 = transformBack(factor1, coef);
         factor2 = transformBack(factor2, coef);
      }
      return "(" + Expression.coefToExp(toInt(factor1), var).toString() + ")(" + Expression.coefToExp(toInt(factor2), var) + ")";
   }
   /**
   *  Transforms a quadratic expression to its correct form, given
   *  that the quartic was reduced
   *  @param quad the quadratic expression to be transformed
   *  @param coef the coefficients of the original quartic
   *  @return a quadratic expression 
   */
   private static List<Fraction> transformBack(List<Fraction> quad, Fraction[] coef)
   {
      Fraction b = coef[1];
      List<Fraction> transformed = new ArrayList<Fraction>();
      transformed.add(quad.get(0));
      transformed.add(b.multiply(new Fraction("1/2")).add(quad.get(1)));
      transformed.add(b.pow(2).multiply(new Fraction("1/16")).add(b.multiply(quad.get(1)).multiply(new Fraction("1/4"))).add(quad.get(2)));
      
      BigInteger denom = Functions.lcm(transformed.get(1).getDenom(), transformed.get(2).getDenom());
      for(int x = 0; x < transformed.size(); x++)
         transformed.set(x, transformed.get(x).multiply(new Fraction(denom)));
      return transformed;
   }
   /**
   *  Transforms a list of whole fractions to integers
   *  @param frac a list of fractions to be processed
   *  @return a list of integers derived from the given list
   */
   private static List<Integer> toInt(List<Fraction> frac)
   {
      List<Integer> asInt = new ArrayList<Integer>();
      for(int x = 0; x < frac.size(); x++)
         asInt.add(frac.get(x).toInt());
      return asInt;
   }
}