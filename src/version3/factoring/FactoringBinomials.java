package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.math.BigInteger;
import java.util.*;
/**
 * Class utilized to factor a binomial expression as either a difference
 * of squares or a sum/difference of cubes.
 * @author Nathan Harbison
 */
public class FactoringBinomials {
   /**
    * Factors a given binomial expression as either a difference
    * of squares or a sum/difference of cubes.
    *
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   public static List<Expression> factor(Expression exp) {
      Set<Character> allVars = exp.getAllVars();

      if(isDifOfSqs(exp)) {
         BigInteger coeff1Root = Functions.nthRoot(exp.getCoeff(0), 2);
         BigInteger coeff2Root = Functions.nthRoot(exp.getCoeff(1).abs(), 2);

         Expression factor1 = new Expression();
         factor1.addReducedTerm(coeff1Root, exp.getVarPowers(0), new Fraction("1/2"));
         factor1.addReducedTerm(coeff2Root, exp.getVarPowers(1), new Fraction("1/2"));

         Expression factor2 = new Expression();
         factor2.addReducedTerm(coeff1Root, exp.getVarPowers(0), new Fraction("1/2"));
         factor2.addReducedTerm(coeff2Root.negate(), exp.getVarPowers(1), new Fraction("1/2"));

         List<Expression> factors = factor(factor1);
         factors.addAll(factor(factor2));
         return factors;
      } else if(allCubes(exp)) {
         BigInteger coeff1Root = Functions.nthRoot(exp.getCoeff(0), 3);
         BigInteger coeff2Root = Functions.nthRoot(exp.getCoeff(1), 3);

         Expression exp1 = new Expression();
         exp1.addReducedTerm(coeff1Root, exp.getVarPowers(0), new Fraction("1/3"));
         exp1.addReducedTerm(coeff2Root, exp.getVarPowers(1), new Fraction("1/3"));

         Expression exp2 = new Expression();
         exp2.addReducedTerm(coeff1Root.pow(2), exp.getVarPowers(0), new Fraction("2/3"));
         Map<Character, Integer> combined = new HashMap<>(exp.getVarPowers(0));
         combined.putAll(exp.getVarPowers(1));
         exp2.addReducedTerm(coeff1Root.multiply(coeff2Root).negate(), combined, new Fraction("1/3"));
         exp2.addReducedTerm(coeff2Root.pow(2), exp.getVarPowers(1), new Fraction("2/3"));

         List<Expression> factors = factor(exp1);
         // note: it is impossible to factor the secondary expression here as a quadratic
         // but can pull out rational factors if it is a polynomial
         if(allVars.size() == 1)
            factors.addAll(FactoringPolynomials.factor(exp2));
         else
            factors.add(exp2);
         return factors;
      }
      if(allVars.size() == 1)
         return FactoringPolynomials.factor(exp);
      return new ArrayList<>(List.of(exp));
   }

   /**
    * Determines if a given binomial expression is a difference of squares.
    * @param exp the binomial expression to be processed.
    * @return whether the given expression is difference of squares.
    */
   public static boolean isDifOfSqs(Expression exp) {
      for(char var : exp.getAllVars())
         for(int i = 0; i < exp.size(); i++)
            if(exp.getPower(i, var) % 2 != 0)
               return false;
      // first term should be positive (no abs), from calling getFactor
      return Functions.isNthPower(exp.getCoeff(0), 2)
              && Functions.isNthPower(exp.getCoeff(1).abs(), 2)
              && exp.getCoeff(1).compareTo(BigInteger.ZERO) < 0;
   }

   /**
    * Determines if each term in the given binomial expression are perfect cubes.
    * @param exp the binomial expression to be processed.
    * @return whether the given expression contains all cubes.
    */
   public static boolean allCubes(Expression exp) {
      for(char var : exp.getAllVars())
         for(int i = 0; i < exp.size(); i++)
            if(exp.getPower(i, var) % 3 != 0)
               return false;
      return Functions.isNthPower(exp.getCoeff(0), 3)
              && Functions.isNthPower(exp.getCoeff(1).abs(), 3);
   }
}