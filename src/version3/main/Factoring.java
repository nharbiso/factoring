package version3.main;

import version3.factoring.*;
import version3.utilities.*;

import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.util.*;

/**
 * Factors any given simplified expression into separate terms.
 * @author Nathan Harbison
 */
public class Factoring {
   /**
    * String used in parsing expression input, to keep delimiters at start of the succeeding token.
    */
   private static final String WITH_DELIMITER = "(?=%1$s)";
   public static void main(String[] args) {
      while(true) {
         String expStr = JOptionPane.showInputDialog("Enter the expression you would like to factor. (Hit enter to quit)");
         if(expStr.isEmpty())
            break;
         try {
            expStr = expStr.replaceAll("\\s+", ""); // clean up spacing - remove all white space
            String factored = factor(expStr);
            boolean factorable = !expStr.equals(factored);

            expStr = expStr.charAt(0) + expStr.substring(1).replaceAll("\\+", " + ").replaceAll("-", " - ");
            expStr = Scripts.superscriptNum(expStr);
            factored = Scripts.superscriptNum(factored);

            if(!factorable) {
               JOptionPane.showMessageDialog(null, "The expression " + expStr + " is not factorable.");
            } else {
               JOptionPane.showMessageDialog(null, expStr + " factored is:\n" + factored);
            }

         } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Could not interpret the given expression, received:\n" + e.getMessage());
         }
      }
   }

   /**
    * Factors a given expression and expresses it in a string.
    * @param expStr string of expression to be factored.
    * @return the string representation of factored expression.
    * @throws Exception will throw an exception if the parameters are null or invalidly formatted.
    */
   public static String factor(String expStr) throws Exception {
      String[] terms = expStr.split(String.format(WITH_DELIMITER, "\\+|-")); // split expression into its component terms

      // finds the substring of each term that represents the variables and their powers
      String[] variables = new String[terms.length];
      for (int i = 0; i < variables.length; i++)
         variables[i] = Functions.getVariable(terms[i]);

      // parse the coefficient from each term
      Fraction[] coefficients = new Fraction[terms.length];
      for (int i = 0; i < coefficients.length; i++)
         coefficients[i] = Functions.parseCoefficient(terms[i], variables[i]);

      // find factor by which to multiply all coefficients to make them whole
      // and use it to make all coefficient whole
      BigInteger fracLCM = getLCM(coefficients);

      // create expression object from coefficients and variable strings
      Expression exp = new Expression();
      for (int i = 0; i < coefficients.length; i++)
         exp.addTerm(new Term(coefficients[i].getNum(), variables[i]));

      // factor out any common numerical factors and variables
      Term factorTerm = exp.getFactor();

      List<Expression> factoredExp = List.of(exp);
      if (exp.size() == 3 && Functions.canBeQuadFactored(exp)) {
         // factor as a quadratic
         factoredExp = FactoringQuadratics.factor(exp);
      } else if (exp.size() == 2) {
         // factor as a binomial
         factoredExp = FactoringBinomials.factor(exp);
      } else if(exp.getAllVars().size() == 1) {
         // factor as a polynomial
         factoredExp = FactoringPolynomials.factor(exp);
      } else if(exp.size() == 4) {
         // factor by grouping
         factoredExp = FactoringByGrouping.factor(exp);
      }
      
      if(factoredExp.size() == 1 && factorTerm.isConstant() && factorTerm.getCoeff().equals(BigInteger.ONE))
         return expStr;

      // group duplicate terms together
      Map<Expression, Integer> freqs = new HashMap<>();
      for(Expression factorExp : factoredExp) {
         freqs.putIfAbsent(factorExp, 0);
         freqs.put(factorExp, freqs.get(factorExp) + 1);
      }

      // start with initial factored out term, perhaps with rational coefficient
      String factorCoeff = new Fraction(factorTerm.getCoeff(), fracLCM).toString();
      if (factorCoeff.equals("-1") || factorCoeff.equals("1"))
         factorCoeff = factorCoeff.replaceAll("1", "");
      String factored = factorCoeff + factorTerm.getVarStr();

      // add other factored out expressions to string
      for(Expression factorExp : factoredExp) {
         if(freqs.containsKey(factorExp)) {
            int freq = freqs.get(factorExp);
            factored += "(" + factorExp.toString() + ")" + (freq == 1 ? "" : "^" + freq);
            freqs.remove(factorExp);
         }
      }
      return factored;
   }

   /**
    * Finds common (positive) factor to multiply all fractional coefficients by to
    * make them whole numbers, and alters the coefficients accordingly by multiplying
    * them by this common factor.
    * @param coeffs array representing the coefficient of each term.
    * @return a string representation of the multiplying scalar that makes all
    * coefficients whole when multiplied. If 1, returns nothing; otherwise returns
    * the string representation of the scalar.
    */
   private static BigInteger getLCM(Fraction[] coeffs) {
      List<BigInteger> denoms = new ArrayList<>();
      for(Fraction coeff : coeffs)
         denoms.add(coeff.getDenom());
      BigInteger lcm = Functions.lcm(denoms);

      for(int i = 0; i < coeffs.length; i++)
         coeffs[i] = coeffs[i].multiply(lcm);

      return lcm;
   }
}