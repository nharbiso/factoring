package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Term;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.util.*;
import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * Class utilized to factor a polynomial expression using synthetic division.
 * @author Nathan Harbison
 */
public class FactoringPolynomials {
   /**
    * Factors a given polynomial expression.
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    * given expression if unfactorable.
    */
   public static List<Expression> factor(Expression exp) {
      Set<Character> allVars = exp.getAllVars();
      if(allVars.size() != 1)
         throw new IllegalArgumentException("Error: not a polynomial expression with one variable");
      char var = Functions.getItemFromSet(allVars);

      exp.addZeroes();

      if(exp.size() <= 2) {
         exp.removeZeroes();
         return new ArrayList<>(List.of(exp));
      }

      // finds all possible rational roots of the polynomial
      List<Fraction> posFactors = findRationalFact(exp.getCoeff(0), exp.getCoeff(exp.size() - 1));
      for(Fraction posFactor : posFactors) {
         // perform synthetic division on the factor
         Optional<DivisionResult> resOpt = synDivide(exp, posFactor, var);
         // check if remainder is 0
         if(resOpt.isPresent() && resOpt.get().remainder().equals(new Expression())) {
            Expression quotient = resOpt.get().quotient();
            Expression factor = getLinExp(var, posFactor);

            // determine if we can also pull out a sum/difference of cubes
            // (i.e. if ax+b is a factor, see if a^3x^3+b^3 = (ax+b)(a^2x^2-abx+b^2x^2)
            // is a factor too, by dividing out by a^2x^2-abx+b^2x^2)
            List<BigInteger> coeffs = new ArrayList<>();
            coeffs.add(posFactor.getDenom().multiply(posFactor.getDenom()));
            coeffs.add(posFactor.getDenom().multiply(posFactor.getNum()));
            coeffs.add(posFactor.getNum().multiply(posFactor.getNum()));
            Expression possCube = new Expression(coeffs, var);
            Optional<DivisionResult> cbResOpt = divide(quotient, possCube, var);

            List<Expression> factored = new ArrayList<>();
            factored.add(factor);
            if (cbResOpt.isPresent() && cbResOpt.get().remainder().equals(new Expression())) {
               factored.add(possCube);
               Expression cbQuotient = cbResOpt.get().quotient();
               if(!cbQuotient.equals(new Expression("1")))
                  factored.addAll(factor(cbQuotient));
            } else {
               factored.addAll(factor(quotient));
            }
            return factored;
         }
      }

      if(exp.nonZeroTerms() == 3 && Functions.isPowerOf2(exp.getPower(0, var))) {
         exp.removeZeroes();
         if(Functions.canBeQuadFactored(exp))
            return FactoringQuadratics.factor(exp);
      } else if(exp.getPower(0, var) == 4)
         return FactoringQuartics.factor(exp);
      else
         exp.removeZeroes();

      return new ArrayList<>(List.of(exp));
   }

   /**
    * Finds all possible rational factors of the polynomial in the form of p/q, where
    * p is a factor of the coefficient of last term in the polynomial, and q is
    * the factor of the coefficient of the first term in the polynomial.
    * @param firstCoeff the coefficient of the first term in the polynomial.
    * @param lastCoeff the coefficient of the last term in the polynomial.
    * @return a list of all possible rational factors of the function.
    */
   public static List<Fraction> findRationalFact(BigInteger firstCoeff, BigInteger lastCoeff)
   {
      List<BigInteger> firstFactors = Functions.findFactors(firstCoeff.abs());
      List<BigInteger> lastFactors = Functions.findFactors(lastCoeff.abs());
      Set<Fraction> factors = new HashSet<>();
      for(BigInteger firstFactor : firstFactors)
         for(BigInteger lastFactor : lastFactors) {
            factors.add(new Fraction(lastFactor, firstFactor));
            factors.add(new Fraction(lastFactor.negate(), firstFactor));
         }
      return new ArrayList<>(factors);
   }

   /**
    * Divides the expression by the given rational factor p/q using synthetic
    * division (i.e. division the expression by qx-p). Returns the quotient and
    * remainder, or nothing if the result has fractional coefficients.
    * @param exp the expression to be divided.
    * @param factor the factor of the expression.
    * @param var the sole variable in the expression.
    * @return the quotient and remainder of the synthetic division, or nothing
    * if either result has fractional coefficients.
    */
   private static Optional<DivisionResult> synDivide(Expression exp, Fraction factor, char var) {
      // divide out by x - p/q
      List<Fraction> coeffs = new ArrayList<>();
      coeffs.add(new Fraction(exp.getCoeff(0), BigInteger.ONE));
      for(int i = 1; i < exp.size(); i++)
         coeffs.add(factor.multiply(coeffs.get(i - 1)).add(new Fraction(exp.getCoeff(i))));
      // divide quotient by q (so that we therefore divided in total by qx-p)
      coeffs.replaceAll(coeff -> coeff.divide(factor.getDenom()));

      boolean allWhole = coeffs.stream().allMatch(Fraction::isWhole);
      if(!allWhole)
         return Optional.empty();

      List<BigInteger> coeffsInt = coeffs.stream().map(Fraction::getNum).toList();
      Expression quotient = new Expression(coeffsInt.subList(0, coeffs.size() - 1), var);
      Expression remainder = new Expression(List.of(coeffsInt.get(coeffs.size() - 1)), var);
      return Optional.of(new DivisionResult(quotient, remainder));
   }

   /**
    * Divides a polynomial expression by another and returns the result.
    * @param dividend the polynomial expression to be divided.
    * @param divisor the polynomial dividing the dividend.
    * @param var the variable in the polynomial.
    * @return the result of the division, or nothing if the resulting
    * polynomial has fractional coefficients.
    */
   private static Optional<DivisionResult> divide(Expression dividend, Expression divisor, char var) {
      List<Fraction> expCoeffs = dividend.getCoeffs().stream()
              .map(Fraction::new).collect(Collectors.toList());
      List<BigInteger> divCoeffs = divisor.getCoeffs();

      // divide by the divisor (expCoeffs stores the dividends coefficients during the process)
      List<Fraction> quotient = new ArrayList<>();
      for(int i = 0; i < expCoeffs.size() - divCoeffs.size() + 1; i++) {
         quotient.add(expCoeffs.get(i).divide(divCoeffs.get(0)));
         for(int j = i; j < i + divCoeffs.size(); j++)
            expCoeffs.set(j, expCoeffs.get(j).subtract(quotient.get(i).multiply(new Fraction(divCoeffs.get(j-i)))));
      }

      // entries past quotient.size() in expCoeffs now store the remaining
      // coefficients, i.e. the remainder

      boolean quotientWhole = quotient.stream().allMatch(Fraction::isWhole);
      boolean remainderWhole = expCoeffs.stream().allMatch(Fraction::isWhole);
      if(!quotientWhole || !remainderWhole)
         return Optional.empty();

      List<BigInteger> quotInt = quotient.stream().map(Fraction::getNum).toList();
      List<BigInteger> remInt = expCoeffs.subList(quotient.size() + 1, expCoeffs.size())
              .stream().map(Fraction::getNum).toList();
      return Optional.of(new DivisionResult(new Expression(quotInt, var), new Expression(remInt, var)));
   }

   /**
    * Returns the binomial linear polynomial with the given rational
    * number as its singular root.
    * @param var the sole variable in the expression.
    * @param root the root of the expression.
    * @return the binomial linear expression.
    */
   private static Expression getLinExp(char var, Fraction root) {
      Expression exp = new Expression();
      Map<Character, Integer> varPow = new HashMap<>();
      varPow.put(var, 1);
      exp.addTerm(new Term(root.getDenom(), varPow));
      exp.addTerm(new Term(root.getNum().negate(), new HashMap<>()));
      return exp;
   }


   /**
    * Wrapper class that stores the result of expression division,
    * i.e. the quotient and remainder.
    * @param quotient  The quotient of the division.
    * @param remainder The remainder of the division.
    */
   private record DivisionResult(Expression quotient, Expression remainder) {}
}