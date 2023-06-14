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
public class Factoring
{
   /**
    * String used in parsing expression input, to keep delimiters.
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

            expStr = Scripts.superscriptNum(expStr);
            factored = Scripts.superscriptNum(factored);

            expStr = expStr.charAt(0) + expStr.substring(1).replaceAll("\\+", " + ").replaceAll("-", " - ");
            if(expStr.equals(factored)) {
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
         variables[i] = getVariable(terms[i]);

      // parse the coefficient from each term
      Fraction[] coefficients = new Fraction[terms.length];
      for (int i = 0; i < coefficients.length; i++)
         coefficients[i] = parseCoefficient(terms[i], variables[i]);

      // find factor by which to multiply all coefficients to make them whole
      // and use it to make all coefficient whole
      BigInteger fracLCM = getLCM(coefficients);

      // create expression object from coefficients and variable strings
      Expression exp = new Expression();
      for (int i = 0; i < coefficients.length; i++)
         exp.addTerm(new Term(coefficients[i].intValue(), variables[i]));
      Set<Character> allVars = exp.getAllVars();

      // factor out any common numerical factors and variables
      Term factorTerm = exp.getFactor();

      List<Expression> factoredExp = List.of(exp);
      if (exp.size() == 3 && canBeQuadFactored(exp, allVars)) {
         // factor as a quadratic
         factoredExp = FactoringQuadratics.factor(exp);
      } else if (exp.size() == 2) {
         // factor as a binomial
         factoredExp = FactoringBinomials.factor(exp);
      } else if(allVars.size() == 1) {
         // factor as a polynomial
         exp.addZeroes();
         factoredExp = FactoringPolynomials.factor(exp);
      } else if(exp.size() == 4) {
         // factor by grouping
         factoredExp = FactoringByGrouping.factor(exp);
      }
      
      if(factoredExp.size() == 1 && factorTerm.isConstant() && factorTerm.getCoefficient() == 1)
         return expStr;

      // group duplicate terms together
      Map<Expression, Integer> freqs = new HashMap<>();
      for(Expression factorExp : factoredExp) {
         freqs.putIfAbsent(factorExp, 0);
         freqs.put(factorExp, freqs.get(factorExp) + 1);
      }

      // start with initial factored out term, perhaps with rational coefficient
      String factorCoeff = new Fraction(new BigInteger(factorTerm.getCoefficient()+""), fracLCM).toString();
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
    * Finds and returns the variable portion (or lack thereof) in a given term.
    * @param term the term to be analyzed.
    * @return string representing variable and its power or "" if none is found.
    */
   private static String getVariable(String term) {
      int loc = 0;
      try {
         if(term.charAt(loc) == '+' || term.charAt(loc) == '-') // ignore sign of coefficient
            loc++;
         while(Character.isDigit(term.charAt(loc)) || term.charAt(loc) == '/') // ignore (fractional) coefficient
            loc++;
      } catch(StringIndexOutOfBoundsException e) {
         return "";
      }
      return term.substring(loc);
   }

   /**
    * Finds, parses, and returns the coefficient in a given term.
    * @param term the term to be analyzed.
    * @param vars the variable(s) contained in the term.
    * @return the coefficient of the term.
    */
   private static Fraction parseCoefficient(String term, String vars) {
      String coefStr = term;
      if(!vars.equals(""))
         coefStr = coefStr.substring(0, coefStr.indexOf(vars));

      if(coefStr.equals("") || coefStr.equals("+"))
         return Fraction.ONE;
      if(coefStr.equals("-"))
         return Fraction.NEG_ONE;
      return new Fraction(coefStr);
   }

   /**
    * Finds common (positive) factor to multiply all fractional coefficients by to
    * make them whole numbers, and alters the coefficients accordingly by multiplying
    * them by this common factor.
    * @param coefs array representing the coefficient of each term.
    * @return a string representation of the multiplying scalar that makes all
    * coefficients whole when multiplied. If 1, returns nothing; otherwise returns
    * the string representation of the scalar.
    */
   private static BigInteger getLCM(Fraction[] coefs) {
      List<BigInteger> denoms = new ArrayList<>();
      for(Fraction coef : coefs)
         denoms.add(coef.getDenom());
      BigInteger lcm = Functions.lcm(denoms);

      for(int i = 0; i < coefs.length; i++)
         coefs[i] = coefs[i].multiply(lcm);

      return lcm;
   }
   
   /**
    * Determines if an expression composed of 3 terms can be factored
    * similarly to the method utilized for a quadratic polynomial.
    * @param exp the expression to be tested.
    * @param allVars set of all unique variables in the expression.
    * @return whether the expression can be factored like a quadratic.
    */
   private static boolean canBeQuadFactored(Expression exp, Set<Character> allVars) {
      // assumption that expression is 3 terms long

      // test for all permutations of the terms
      int[][] perms = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
      for(int[] perm : perms) {
         boolean factorable = true;
         for (char var : allVars) {
            // to be factored like a quadratic, for each variable:
            //  - the variable's power in the middle term must be half of that in the
            //    first term or its power in the first term must be zero.
            if (exp.getPower(perm[0], var) != 0
                    && (double) exp.getPower(perm[0], var) / 2 != exp.getPower(perm[1], var)) {
               factorable = false;
               break;
            }
            //  - the variable's power in the middle term must be half of that in the
            //    last term, if both powers are non-zero, or its power in the last
            //    term must be zero.
            if (exp.getPower(perm[2], var) != 0
                    && (double) exp.getPower(perm[2], var) / 2 != exp.getPower(perm[1], var)) {
               factorable = false;
               break;
            }
         }

         if(factorable) {
            // rearrange terms according to this permutation
            List<Term> terms = new ArrayList<>();
            while (exp.size() != 0) {
               terms.add(exp.removeTerm(0));
            }
            for (int ind : perm) {
               exp.addTerm(terms.get(ind));
            }
            return true;
         }
      }
      return false;
   }
}