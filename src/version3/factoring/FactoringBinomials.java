package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.util.*;
/**
*  Class utilized to factor a binomial expression
*  @author Nathan Harbison
*/
public class FactoringBinomials
{
   /**
   *  Factors a given binomial expression
   *  
   *  @param exp the expression to be factored
   *  @return a string representing the factored expression, or the given expression if unfactorable
   */
   public static List<Expression> factor(Expression exp)
   {
      Set<Character> allVars = exp.getAllVars();

      if(isDifOfSqs(exp))
      {
         Expression exp1 = new Expression();
         exp1.addReducedTerm((int) Math.sqrt(exp.getCoefficient(0)), exp.getVars(0), new Fraction("1/2"));
         exp1.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(1))), exp.getVars(1), new Fraction("1/2"));
         Expression exp2 = new Expression();
         exp2.addReducedTerm(exp1.getCoefficient(0), exp.getVars(0), new Fraction("1/2"));
         exp2.addReducedTerm(exp1.getCoefficient(1) * -1, exp.getVars(1), new Fraction("1/2"));

         List<Expression> factors = factor(exp1);
         factors.addAll(factor(exp2));
         return factors;
      }
      else if(areCubes(exp))
      {
         Expression exp1 = new Expression();
         exp1.addReducedTerm((int) Math.cbrt(exp.getCoefficient(0)), exp.getVars(0), new Fraction("1/3"));
         exp1.addReducedTerm((int) Math.cbrt(exp.getCoefficient(1)), exp.getVars(1), new Fraction("1/3"));
         Expression exp2 = new Expression();
         exp2.addReducedTerm((int) Math.pow(exp1.getCoefficient(0), 2), exp.getVars(0), new Fraction("2/3"));
         Map<Character, Integer> combined = new HashMap<Character, Integer>(exp.getVars(0));
         combined.putAll(exp.getVars(1));
         exp2.addReducedTerm(exp1.getCoefficient(0) * exp1.getCoefficient(1) * -1, combined, new Fraction("1/3"));
         exp2.addReducedTerm((int) Math.pow(exp1.getCoefficient(1), 2), exp.getVars(1), new Fraction("2/3"));

         List<Expression> factors = factor(exp1);

         if(allVars.size() == 1) {
            exp2.addZeroes();
            factors.addAll(FactoringPolynomials.factor(exp2));
         } else {
            factors.add(exp2);
         }

         return factors;
      }
      if(allVars.size() == 1)
      {
         exp.addZeroes();
         return FactoringPolynomials.factor(exp);
      }
      return new ArrayList<>(List.of(exp));
   }
   /**
   *  Determines if a given expression is a difference of squares
   *  @param exp the expression to be processed
   *  @return whether or not the given expression is difference of squares
   */
   public static boolean isDifOfSqs(Expression exp)
   {
      for(int x = 0; x < exp.size(); x++)
         for(char c : exp.getVars(x).keySet())
            if(exp.getPower(x, c) % 2 != 0)
               return false;
      return Functions.isPerfectSq(exp.getCoefficient(0)) && Functions.isPerfectSq(Math.abs(exp.getCoefficient(1))) && exp.getCoefficient(1) < 0;
   }
   /**
   *  Determines if a given expression consists of cubes
   *  @param exp the expression to be processed
   *  @return whether or not the given expression contains all cubes
   */
   public static boolean areCubes(Expression exp)
   {
      for(int x = 0; x < exp.size(); x++)
         for(char c : exp.getVars(x).keySet())
            if(exp.getPower(x, c) % 3 != 0)
               return false;
      return Functions.isPerfectCube(exp.getCoefficient(0)) && Functions.isPerfectCube(exp.getCoefficient(1));
   }
}