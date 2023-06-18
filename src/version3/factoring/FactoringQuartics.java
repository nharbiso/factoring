package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.util.*;
import java.math.BigInteger;

/**
 * Class utilized to factor a quartic polynomial into two unfactorable quadratics.
 * Formulas and algorithm obtained from
 * https://www.maa.org/sites/default/files/Brookfield2007-103574.pdf
 * @author Nathan Harbison
 */
public class FactoringQuartics {
   /**
    * Factors a given quartic polynomial into two unfactorable quadratics, if possible.
    * @param exp the expression to be factored.
    * @return a list consisting of the factors of the expression, or just the
    *     * given expression if unfactorable.
    */
   public static List<Expression> factor(Expression exp) {
      Set<Character> allVars = exp.getAllVars();
      if(allVars.size() != 1)
         throw new IllegalArgumentException("Error: not a polynomial expression with one variable");
      char var = Functions.getItemFromSet(allVars);

      exp.addZeroes();

      if(exp.getPower(0, var) != 4)
         throw new IllegalArgumentException("Error: not a quartic polynomial.");
      
      // convert quartic from form of f(x) = a'x^4 + b'x^3 + c'x^2 + d'x + e' -> g(x) = x^4 + cx^2 + dx + e
      // g(x) = f(x - b' / 4a') / a'
      Fraction[] coeffs = new Fraction[5];
      for(int x = 0; x < exp.size(); x++)
         coeffs[x] = new Fraction(exp.getCoeff(x), exp.getCoeff(0));

      Fraction[] rCoeffs = new Fraction[4];
      rCoeffs[0] = coeffs[0];
      rCoeffs[1] = coeffs[2].subtract(coeffs[1].pow(2).multiply(new Fraction("3/8")));
      rCoeffs[2] = coeffs[3].add(coeffs[1].pow(3).multiply(new Fraction("1/8")))
                        .subtract(coeffs[1].multiply(coeffs[2]).multiply(new Fraction("1/2")));
      rCoeffs[3] = coeffs[4].subtract(coeffs[1].pow(4).multiply(new Fraction("3/256")))
                        .add(coeffs[1].pow(2).multiply(coeffs[2]).multiply(new Fraction("1/16")))
                        .subtract(coeffs[1].multiply(coeffs[3]).multiply(new Fraction("1/4")));
      
      // resolvent = z^3 + 2cz^2 + (c^2 - 4e)z - d^2
      // if the resolvent has a rational root that is a perfect square, then
      // the quartic is factorable, and we can then use that root to find
      // the quadratic factors of the quartic
      Fraction[] resolvent = {Fraction.ONE,
                              rCoeffs[1].multiply(new Fraction(2)),
                              rCoeffs[1].pow(2).subtract(rCoeffs[3].multiply(new Fraction(4))),
                              rCoeffs[2].pow(2).inverse()};
      Optional<Fraction> sqFactor = findResolvSqFactor(resolvent);
      if(sqFactor.isPresent()) {
         List<List<Fraction>> quads = findQuadFactors(sqFactor.get(), rCoeffs, resolvent);
         List<Expression> factors = new ArrayList<>();
         for(List<Fraction> quad : quads)
            factors.add(new Expression(transformBack(quad, coeffs), var));
         return factors;
      }
      
      exp.removeZeroes();
      return new ArrayList<>(List.of(exp));
   }

   /**
    * Finds a factor of the polynomial resolvent that is a perfect square.
    * @param resolvent an array containing the resolvent's coefficients.
    * @return a perfect square factor of the resolvent.
    */
   private static Optional<Fraction> findResolvSqFactor(Fraction[] resolvent) {
      BigInteger firstCoeff = Fraction.commonDenom(Arrays.asList(resolvent)).abs();
      BigInteger lastCoeff = resolvent[3].multiply(firstCoeff).getNum().abs();
      if(!Functions.isNthPower(firstCoeff, 2) || !Functions.isNthPower(lastCoeff, 2))
         return Optional.empty();

      List<BigInteger> firstFactors = Functions.findFactors(Functions.nthRoot(firstCoeff, 2));
      List<BigInteger> lastFactors = Functions.findFactors(Functions.nthRoot(lastCoeff, 2));
      for(BigInteger firstFactor : firstFactors)
         for(BigInteger lastFactor : lastFactors) {
            Fraction sqFactor = new Fraction(lastFactor.pow(2), firstFactor.pow(2));
            Fraction eval = sqFactor.pow(3).multiply(resolvent[0])
                              .add(sqFactor.pow(2).multiply(resolvent[1]))
                              .add(sqFactor.multiply(resolvent[2]))
                              .add(resolvent[3]);
            if(eval.compareTo(Fraction.ZERO) == 0)
               return Optional.of(sqFactor);
         }
      return Optional.empty();
   }
   
   /**
    * Finds the irreducible quadratic factors of the quartic polynomial utilizing the given
    * root of the resolvent that is a perfect square.
    * @param root the root of the resolvent that is a perfect square.
    * @param rCoeffs the coefficients of the reduced quartic.
    * @param resolvent the coefficients of the resolvent of the quartic.
    * @return a list containing the lists of rational coefficients for each quadratic factor of the quartic.
    */
   private static List<List<Fraction>> findQuadFactors(Fraction root, Fraction[] rCoeffs, Fraction[] resolvent) {
      Fraction h = root.nthRoot(2).get();
      Fraction k1, k2;
      // h = sqrt(factor), h' = -h
      if(h.compareTo(Fraction.ZERO) != 0) {
         // k = 1/2h(h^3 + ch + d), k' = 1/2h(h^2 + ch + d)
         Fraction twoH = h.multiply(new Fraction(2)).reciprocal();
         k1 = twoH.multiply(h.pow(3).add(h.multiply(rCoeffs[1])).subtract(rCoeffs[2]));
         k2 = twoH.multiply(h.pow(3).add(h.multiply(rCoeffs[1])).add(rCoeffs[2]));
      } else {
         // resolvent[2] = c^2 - 4e = s^2 for some s in Q
         // k = (c + s) / 2, k' = (c - s) / 2
         Fraction s = resolvent[2].nthRoot(2).get();
         k1 = rCoeffs[1].add(s).multiply(new Fraction("1/2"));
         k2 = rCoeffs[1].subtract(s).multiply(new Fraction("1/2"));
      }

      List<Fraction> factor1 = Arrays.asList(Fraction.ONE, h, k1);
      List<Fraction> factor2 = Arrays.asList(Fraction.ONE, h.inverse(), k2);

      return new ArrayList<>(List.of(factor1, factor2));
   }

   /**
    * Transforms a quadratic expression to its correct form, given
    * that the quartic was reduced.
    * @param quad the quadratic expression to be transformed.
    * @param coeffs the coefficients of the original quartic.
    * @return the transformed quadratic expression.
    */
   private static List<BigInteger> transformBack(List<Fraction> quad, Fraction[] coeffs) {
      // transforms the quadratic via the substitution (f + b'/4a')
      Fraction b = coeffs[1]; // = b/a
      List<Fraction> transformed = new ArrayList<>();
      transformed.add(quad.get(0));
      transformed.add(b.multiply(new Fraction("1/2")).add(quad.get(1)));
      transformed.add(b.pow(2).multiply(new Fraction("1/16"))
                           .add(b.multiply(quad.get(1)).multiply(new Fraction("1/4")))
                           .add(quad.get(2)));

      // multiply by one of the factors of coeff[0] (a'), which we had divided out,
      // to make the coefficients whole again
      BigInteger fracLCM = Fraction.commonDenom(transformed);
      return transformed.stream().map(coeff -> coeff.multiply(fracLCM).getNum()).toList();
   }
}