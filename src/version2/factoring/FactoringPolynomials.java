package version2.factoring;

import version2.utilities.Expression;
import version2.utilities.Term;
import version2.utilities.Fraction;
import version2.utilities.Functions;

import java.util.*;
import java.math.BigInteger;

/**
*  Class utilized to factor a polynomial expression
*  @author Nathan Harbison
*/
public class FactoringPolynomials
{
   /**
   *  Factors a given polynomial expression
   *  
   *  @param exp the expression to be factored
   *  @return a string representing the factored expression, or the given expression if unfactorable
   */
   public static String factor(Expression exp, List<Character> allVars)
   {
      if(exp.size() <= 2)
         return "(" + exp.toString() + ")";
      if(exp.size() == 3 && exp.getCoef(1) == 0)
      {
         exp.removeZeroes();
         if(FactoringBinomials.isDifOfSqs(exp) || FactoringBinomials.areCubes(exp))
            return FactoringBinomials.factor(exp, allVars);
      }
      else if(exp.size() == 3)
         return FactoringQuadratics.factor(exp, allVars);
   
      List<Fraction> posFactors = findRationalFact(exp.getCoef(0), exp.getCoef(exp.size() - 1));
      for(Fraction posFactor : posFactors)
      {
         Fraction sum = Fraction.ZERO;
         for(int i = 0; i < exp.size(); i++)
            sum = sum.add(posFactor.pow(exp.getPow(i, allVars.get(0))).multiply(new Fraction(exp.getCoef(i))));
         if(sum.equals(Fraction.ZERO))
         {
            Expression divided = synDivide(exp, posFactor, allVars.get(0));
            List<Integer> coef = new ArrayList<Integer>();
            coef.add(posFactor.getDenom().multiply(posFactor.getDenom()).intValue());
            coef.add(posFactor.getDenom().multiply(posFactor.getNum()).intValue());
            coef.add(posFactor.getNum().multiply(posFactor.getNum()).intValue());
            Expression possCube = Expression.coefToExp(coef, allVars.get(0));
            Expression newDiv = divide(divided, possCube, allVars.get(0));
            if(newDiv.size() == 0)
               return getVar(allVars.get(0), posFactor) + factor(divided, allVars);
            else
               return getVar(allVars.get(0), posFactor) + "(" + possCube.toString() + ")" + factor(newDiv, allVars); 
         }
      }
      if(exp.getPow(0, allVars.get(0)) == 4 && exp.getCoef(1) == 0 && exp.getCoef(3) == 0)
      {
         exp.removeZeroes();
         return FactoringQuadratics.factor(exp, allVars);
      }
      else if(exp.getPow(0, allVars.get(0)) == 4)
         return FactoringQuartics.factor(exp, allVars.get(0));
      else
      {
         exp.removeZeroes();
         return "(" + exp.toString() + ")";
      }
   }
   /**
   *  Finds all possible rational factors of the polynomial in the form of p/q, where
   *  p is a factor of an integer, the last coefficient in the polynomial, and q is
   *  the factor of another integer, the first coefficient of said polynomial
   *  @param c1 the first coefficient of the polynomial
   *  @param c2 the last coefficient of the polynomial 
   *  @return a list of all possible rational factors of the function
   */
   public static List<Fraction> findRationalFact(int c1, int c2)
   {
      List<Integer> f1 = Functions.findFactors(Math.abs(c1));
      List<Integer> f2 = Functions.findFactors(Math.abs(c2));
      Set<Fraction> set = new HashSet<Fraction>();
      for(int x = 0; x < f1.size(); x++)
         for(int y = 0; y < f2.size(); y++)
         {
            set.add(new Fraction(f2.get(y), f1.get(x)));
            set.add(new Fraction(f2.get(y) * -1, f1.get(x)));
         }
      return new ArrayList<Fraction>(set);
   }
   /**
   *  Divides the expression by the given rational factor using synthetic
   *  division
   *  @param exp the expression to be divided
   *  @param factor the factor of the expression
   *  @param var the sole variable in the expression
   *  @return the quotient of the synthetic division
   */
   private static Expression synDivide(Expression exp, Fraction factor, char var)
   {
      List<Integer> coef = new ArrayList<Integer>();
      coef.add(exp.getCoef(0) / factor.getDenom().intValue());
      for(int x = 1; x < exp.size() - 1; x++)
         coef.add(factor.multiply(new Fraction(coef.get(x - 1) * factor.getDenom().intValue())).add(new Fraction(exp.getCoef(x))).divide(factor.getDenom()).getNum().intValue());
      return Expression.coefToExp(coef, var);
   }
   /**
   *  Divides a polynomial expression by another and returns the result
   *  @param dividend the polynomial expression to be divided
   *  @param divisor the polynomial dividing the dividend
   *  @param var the variable in the polynomial
   *  @return the result of the division (blank if there is a remainder or 
   *  the resulting polynomial has fractional coefficients)
   */
   private static Expression divide(Expression dividend, Expression divisor, char var)
   {
      List<Integer> dendInt = dividend.getCoefs();
      List<Fraction> dend = new ArrayList<Fraction>();
      for(int x = 0; x < dendInt.size(); x++)
         dend.add(new Fraction(dendInt.get(x)));
      List<Integer> sor = divisor.getCoefs();
      List<Fraction> quotient = new ArrayList<Fraction>();
      for(int x = 0; x < dend.size() - sor.size() + 1; x++)
      {
         quotient.add(new Fraction(dend.get(x), new BigInteger(sor.get(0) + ""))); 
         for(int y = x; y < x + sor.size(); y++)
            dend.set(y, dend.get(y).subtract(quotient.get(x).multiply(new Fraction(sor.get(y-x)))));
      }
      
      List<Integer> toInt = new ArrayList<Integer>();
      for(int x = 0; x < quotient.size(); x++)
         if(quotient.get(x).isWhole())
            toInt.add(quotient.get(x).toInt());
         else
            return new Expression();
      for(int x = quotient.size(); x < dend.size(); x++)
         if(!dend.get(x).equals(Fraction.ZERO))
            return new Expression();
      return Expression.coefToExp(toInt, var);
   }
   /**
   *  Returns a string represntation of binomial linear factor
   *  of the polynomial
   *  @param var the sole variable in the expression
   *  @param factor the factor of the expression
   *  @return a string representation of the factor
   */
   private static String getVar(char var, Fraction factor)
   {
      Expression exp = new Expression();
      Map<Character, Integer> map = new HashMap<Character, Integer>();
      map.put(var, 1);
      exp.addTerm(new Term(factor.getDenom().intValue(), map));
      exp.addTerm(new Term(factor.getNum().intValue() * -1, new HashMap<Character, Integer>()));
      return "(" + exp.toString() + ")";
   }
}