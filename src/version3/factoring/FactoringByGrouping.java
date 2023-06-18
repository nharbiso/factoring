package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Term;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.math.BigInteger;
import java.util.*;

/**
 * Class utilized to factor a quadrinomial by grouping.
 * @author Nathan Harbison
 */
public class FactoringByGrouping
{
   /**
    * Factors a given quadrinomial by grouping or a difference of squares.
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   public static List<Expression> factor(Expression exp) {
      List<Expression> factored = factorByGrouping(exp);
      if(factored.size() != 1)
         return factored;
      
      factored = factorBySquares(exp);
      return factored;
   }

   /**
    * Tries to factor a given quadrinomial by grouping the expression into two
    * binomials, and pulling out a factor from both.
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   private static List<Expression> factorByGrouping(Expression exp) {
      int[][] perms = Functions.findPerms(4);
      for(int[] perm : perms) {
         // group into two binomials based on ordering from permutation
         Expression exp1 = new Expression();
         exp1.addTerm(new Term(exp.getCoeff(perm[0]), exp.getVarPowers(perm[0])));
         exp1.addTerm(new Term(exp.getCoeff(perm[1]), exp.getVarPowers(perm[1])));
      
         Expression exp2 = new Expression();
         exp2.addTerm(new Term(exp.getCoeff(perm[2]), exp.getVarPowers(perm[2])));
         exp2.addTerm(new Term(exp.getCoeff(perm[3]), exp.getVarPowers(perm[3])));

         // factor out term
         Expression factor = new Expression();
         factor.addTerm(exp1.getFactor());
         factor.addTerm(exp2.getFactor());

         // check if remaining binomials are now equal - then we can factor by "undoing" distribution
         if(exp1.equals(exp2)) {
            List<Expression> factored = FactoringBinomials.factor(exp1);
            factored.addAll(FactoringBinomials.factor(factor));
            return factored;
         }
      }
      return new ArrayList<>(List.of(exp));
   }

   /**
    * Tries to factor a given quadrinomial by finding and factoring
    * differences of squares, i.e. by factoring these two types of expressions:
    * x^2-y^2+2x-2y = (x+y)(x-y) + 2(x-y) = (x+y+2)(x-y) or
    * x^2+2xy+y^2-4z^2 = (x+y)^2-4z^2 = (x+y-2z)(x+y+2z).
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   private static List<Expression> factorBySquares(Expression exp) {
      // find indices of positive/negative square terms in the expression
      // (potential factorable difference of squares)
      List<Integer> posSqInd = new ArrayList<>();
      List<Integer> negSqInd = new ArrayList<>();
      loop: for(int i = 0; i < exp.size(); i++) {
         if(!Functions.isNthPower(exp.getCoeff(i).abs(), 2))
            continue loop;
         for(char var : exp.getAllVars())
            if(exp.getPower(i, var) % 2 != 0)
               continue loop;
         if(exp.getCoeff(i).compareTo(BigInteger.ZERO) > 0)
            posSqInd.add(i);
         else
            negSqInd.add(i);
      }
      for(int posInd : posSqInd)
         for(int negInd : negSqInd) {
            List<Expression> factored = factorSquares(exp, posInd, negInd);
            if(factored.size() != 1)
               return factored;
         }
      return new ArrayList<>(List.of(exp));
   }

   /**
    * A helper method that factors a given expression utilizing differences of squares
    * and the given indices of certain squares.
    * @param exp the expression to be factored.
    * @param posInd the index of a perfect square term with a positive coefficient.
    * @param negInd the index of perfect square term with a negative coefficient.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   private static List<Expression> factorSquares(Expression exp, int posInd, int negInd) {
      // factor two terms given by indices as difference of squares
      BigInteger posSqRoot = Functions.nthRoot(exp.getCoeff(posInd), 2);
      BigInteger negSqRoot = Functions.nthRoot(exp.getCoeff(negInd).abs(), 2);

      Expression sumRt = new Expression();
      sumRt.addReducedTerm(posSqRoot, exp.getVarPowers(posInd), new Fraction("1/2"));
      sumRt.addReducedTerm(negSqRoot, exp.getVarPowers(negInd), new Fraction("1/2"));

      Expression diffRt = new Expression();
      diffRt.addReducedTerm(posSqRoot, exp.getVarPowers(posInd), new Fraction("1/2"));
      diffRt.addReducedTerm(negSqRoot.negate(), exp.getVarPowers(negInd), new Fraction("1/2"));

      // factor out common factor from other two terms
      Expression otherExp = new Expression();
      List<Integer> otherIndices = new ArrayList<>();
      for(int i = 0; i < exp.size(); i++)
         if(i != posInd && i != negInd) {
            otherExp.addTerm(new Term(exp.getCoeff(i), exp.getVarPowers(i)));
            otherIndices.add(i);
         }

      Term fac = otherExp.getFactor();
      // see if remaining binomial matches either factor of the difference of squares
      if(sumRt.equals(otherExp)) {
         diffRt.addTerm(fac);
         List<Expression> factored = FactoringBinomials.factor(sumRt);
         if(Functions.canBeQuadFactored(diffRt))
            factored.addAll(FactoringQuadratics.factor(diffRt));
         else
            factored.add(diffRt);
         return factored;
      } else if(diffRt.equals(otherExp)) {
         sumRt.addTerm(fac);
         List<Expression> factored = FactoringBinomials.factor(diffRt);
         if(Functions.canBeQuadFactored(sumRt))
            factored.addAll(FactoringQuadratics.factor(sumRt));
         else
            factored.add(sumRt);
         return factored;
      }

      // otherwise, see if we can form perfect square of a binomial from three terms, and form
      // a difference of squares with the negative square
      for(int i = 0; i < 2; i++) {
         Optional<Expression> sqrtOpt = sqrt(exp, posInd, otherIndices.get(i), otherIndices.get((i + 1) % 2));
         if(sqrtOpt.isPresent()) {
            Expression factor1 = sqrtOpt.get();
            Expression factor2 = new Expression(factor1);
            factor1.addReducedTerm(negSqRoot, exp.getVarPowers(negInd), new Fraction("1/2"));
            factor2.addReducedTerm(negSqRoot.negate(), exp.getVarPowers(negInd), new Fraction("1/2"));

            List<Expression> factored = new ArrayList<>();
            if(Functions.canBeQuadFactored(factor1))
               factored.addAll(FactoringQuadratics.factor(factor1));
            else
               factored.add(factor1);
            if(Functions.canBeQuadFactored(factor2))
               factored.addAll(FactoringQuadratics.factor(factor2));
            else
               factored.add(factor2);
            return factored;
         }
      }
      for(int i = 0; i < 2; i++) {
         Optional<Expression> sqrtOpt = sqrt(exp, negInd, otherIndices.get(i), otherIndices.get((i + 1) % 2));
         if(sqrtOpt.isPresent()) {
            Expression factor1 = new Expression(sqrtOpt.get());
            factor1.addReducedTerm(0, Functions.nthRoot(exp.getCoeff(posInd), 2), exp.getVarPowers(posInd), new Fraction("1/2"));

            Expression factor2 = new Expression(sqrtOpt.get());
            factor2.multiply(-1);
            factor2.addReducedTerm(0, Functions.nthRoot(exp.getCoeff(posInd), 2), exp.getVarPowers(posInd), new Fraction("1/2"));

            List<Expression> factored = new ArrayList<>();
            if(Functions.canBeQuadFactored(factor1))
               factored.addAll(FactoringQuadratics.factor(factor1));
            else
               factored.add(factor1);
            if(Functions.canBeQuadFactored(factor2))
               factored.addAll(FactoringQuadratics.factor(factor2));
            else
               factored.add(factor2);
            return factored;
         }
      }

      return new ArrayList<>(List.of(exp));
   }

   /**
    * Determines if three terms in an expression form a positive or negative
    * perfect square and returns the square root of said terms.
    * @param exp the expression being analyzed.
    * @param ind1 the index of the first term.
    * @param ind2 the index of the second term.
    * @param ind3 the index of the third term.
    * @return the square root of the three terms, or null if they do not form a perfect square
   */
   private static Optional<Expression> sqrt(Expression exp, int ind1, int ind2, int ind3) {
      // check coefficients
      //   - first and last coefficients should be perfect squares
      //   - middle term should be twice the product of the square roots of the others
      BigInteger absCoeff1 = exp.getCoeff(ind1).abs();
      BigInteger absCoeff2 = exp.getCoeff(ind2).abs();
      BigInteger absCoeff3 = exp.getCoeff(ind3).abs();

      boolean posCoeff1 = exp.getCoeff(ind1).compareTo(BigInteger.ZERO) > 0;
      boolean posCoeff2 = exp.getCoeff(ind2).compareTo(BigInteger.ZERO) > 0;
      boolean posCoeff3 = exp.getCoeff(ind3).compareTo(BigInteger.ZERO) > 0;

      if(!Functions.isNthPower(absCoeff1, 2) || !Functions.isNthPower(absCoeff3, 2))
         return Optional.empty();
      if(posCoeff1 != posCoeff3)
         return Optional.empty();

      BigInteger coeff1Rt = Functions.nthRoot(absCoeff1, 2);
      BigInteger coeff3Rt = Functions.nthRoot(absCoeff3, 2);
      if(!coeff1Rt.multiply(coeff3Rt).multiply(BigInteger.valueOf(2)).equals(absCoeff2))
         return Optional.empty();

      // check variables
      //  - first and last terms should be perfect squares
      //  - middle term should have all variables present in first and last terms to
      //    half the power
      Map<Character, Integer> compareVars = new HashMap<>();
      compareVars.putAll(exp.getVarPowers(ind1));
      compareVars.putAll(exp.getVarPowers(ind3));
      for(char var : compareVars.keySet())
         if(compareVars.get(var) % 2 != 0)
            return Optional.empty();
         else
            compareVars.put(var, compareVars.get(var) / 2);
      if(!compareVars.equals(exp.getVarPowers(ind2)))
         return Optional.empty();

      Expression sqrt = new Expression();
      sqrt.addReducedTerm(coeff1Rt, exp.getVarPowers(ind1), new Fraction("1/2"));
      sqrt.addReducedTerm(posCoeff2 == posCoeff3 ? coeff3Rt : coeff3Rt.negate(),
              exp.getVarPowers(ind3), new Fraction("1/2"));
      return Optional.of(sqrt);
   }
}