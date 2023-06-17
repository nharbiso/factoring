package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Term;
import version3.utilities.Functions;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * Class utilized to factor a trinomial as a quadratic.
 * @author Nathan Harbison
 */
public class FactoringQuadratics {
   /**
    * Factors a given quadratic expression, or one similar in form.
    * @param exp the expression to be factored
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   public static List<Expression> factor(Expression exp) {
      // find two numbers whose product is the product of the first and last
      // terms' coefficients (target) and whose sum is the middle term's coefficient
      BigInteger target = exp.getCoeff(0).multiply(exp.getCoeff(2));
      boolean findSum = target.compareTo(BigInteger.ZERO) > 0;

      BigInteger[] splitMid = new BigInteger[2];
      List<BigInteger> factors = Functions.findFactors(target.abs());
      for(BigInteger factor1 : factors.subList(0, factors.size() / 2)) { // only need to check first half
         BigInteger factor2 = target.abs().divide(factor1);
         if(findSum && factor1.add(factor2).equals(exp.getCoeff(1).abs())) {
            if(exp.getCoeff(1).compareTo(BigInteger.ZERO) > 0) {
               splitMid[0] = factor1;
               splitMid[1] = factor2;
            } else {
               splitMid[0] = factor1.negate();
               splitMid[1] = factor2.negate();
            }
            break;
         }
         else if(!findSum && factor1.subtract(factor2).equals(exp.getCoeff(1))) {
            splitMid[0] = factor1;
            splitMid[1] = factor2.negate();
            break;
         } else if(!findSum && factor2.subtract(factor1).equals(exp.getCoeff(1))) {
            splitMid[0] = factor1.negate();
            splitMid[1] = factor2;
            break;
         }
      }

      if(splitMid[0] == null) {
         return factorSpecial(exp);
      }
   
      Expression exp1 = new Expression();
      exp1.addTerm(new Term(exp.getCoeff(0), exp.getVarPowers(0)));
      exp1.addTerm(new Term(splitMid[0], exp.getVarPowers(1)));

      Expression exp2 = new Expression();
      exp2.addTerm(new Term(splitMid[1], exp.getVarPowers(1)));
      exp2.addTerm(new Term(exp.getCoeff(2), exp.getVarPowers(2)));

      Expression factor = new Expression();
      factor.addTerm(exp1.getFactor());
      factor.addTerm(exp2.getFactor());
      if(exp1.equals(exp2)) {
         List<Expression> factored = FactoringBinomials.factor(exp1);
         factored.addAll(FactoringBinomials.factor(factor));
         return factored;
      }

      return new ArrayList<>(List.of(exp));
   }

   /**
    * Factors special case of trinomial, where
    * (ax^(2^n) + bx^(2^(n-1)) + c)(ax^(2^n) - bx^(2^(n-1)) + c) =
    * (a^2x^(2^(n + 1)) + (2ac - b^2)x^(2^n) + c^2.
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   public static List<Expression> factorSpecial(Expression exp) {
      Set<Character> allVars = exp.getAllVars();
      for(char var : allVars) {
         if(!Functions.isPowerOf2(exp.getPower(0, var)) || exp.getPower(0, var) <= 2
            || exp.getPower(0, var) / 2 != exp.getPower(1, var)) {
            return new ArrayList<>(List.of(exp));
         }
      }

      if(Functions.isNthPower(exp.getCoeff(0), 2) && Functions.isNthPower(exp.getCoeff(2), 2)) {
         BigInteger a = Functions.nthRoot(exp.getCoeff(0), 2), b = null, c = Functions.nthRoot(exp.getCoeff(2), 2);
         BigInteger bIfPosC = a.multiply(c).multiply(BigInteger.valueOf(2)).subtract(exp.getCoeff(1));
         BigInteger bIfNegC = a.multiply(c).multiply(BigInteger.valueOf(2).negate()).subtract(exp.getCoeff(1));
         if (Functions.isNthPower(bIfPosC, 2))
            b = Functions.nthRoot(bIfPosC, 2);
         else if (Functions.isNthPower(bIfNegC, 2)) {
            b = Functions.nthRoot(bIfNegC, 2);
            c = c.negate();
         }

         if (b != null) { // factorable
            Expression exp1 = new Expression();
            Expression exp2 = new Expression();
            BigInteger[] coefs1 = new BigInteger[]{a, b, c};
            BigInteger[] coefs2 = new BigInteger[]{a, b.negate(), c};
            for (int i = 0; i < exp1.size(); i++) {
               Term term = exp.getTerm(i);

               Map<Character, Integer> pows = new HashMap<>();
               for(char var : term.getVariables()) {
                  pows.put(var, term.getPower(var) / 2);
               }
               exp1.addTerm(new Term(coefs1[i], pows));
               exp2.addTerm(new Term(coefs2[i], pows));
            }

            if (exp.getAllVars().size() == 1) {
               List<Expression> factored = FactoringPolynomials.factor(exp1);
               factored.addAll(FactoringPolynomials.factor(exp2));
               return factored;
            }
            return new ArrayList<>(List.of(exp1, exp2));
         }
      }
      return new ArrayList<>(List.of(exp));
   }
}