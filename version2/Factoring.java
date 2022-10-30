import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.util.*;

/**
* Factors any given simplified expression into
* separate terms.
* @author Nathan Harbison
*/
public class Factoring
{
   /**
   *  String used in parsing expression input, to keep delimiters
   */
   private static final String WITH_DELIMITER = "(?=%1$s)";
   public static void main(String[] args)
   {
      while(true)
      {
         String quad = JOptionPane.showInputDialog("Enter the expression you would like to factor. (-1 to quit)");
         try {
            if(quad.equals("-1"))
               throw new NullPointerException();
         } catch(NullPointerException e) {
            break;
         }
         try {
            factor(quad);
         } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Could not interpret the given expression.");
         }
      }
   }
   /**
   *  Factors a given expression and expresses it in a string
   *  @param ex string of expression to be factored
   *  @return string of factored expression
   *  @throws Exception will throw an exception if the parameters are null or invalidly formatted
   */
   public static String factor(String ex) throws Exception
   {
      String[] terms = ex.split(String.format(WITH_DELIMITER, "\\+|-"));
      ex = ex.substring(0, 1) + ex.substring(1, ex.length()).replaceAll("\\+", " + ").replaceAll("-", " - ");
      
      String[] variables = new String[terms.length];
      for(int x = 0; x < variables.length; x++)
         variables[x] = getVariable(terms[x]);
      
      Fraction[] coefficients = new Fraction[terms.length];
      for(int x = 0; x < coefficients.length; x++)
         coefficients[x] = coefficients(terms[x], variables[x]);
      
      String fracLCM = getLCM(coefficients);
      
      Expression exp = new Expression();
      for(int x = 0; x < coefficients.length; x++)
         exp.addTerm(new Term(coefficients[x].toInt(), variables[x]));
      String factor = exp.getFactor().toString();
      if((factor.contains("-1") && factor.length() < 3) || (factor.contains("1") && factor.length() < 2))
         factor = factor.replaceAll("1", "");
      String withFrac = ex;
      ex = exp.toString();
      
      List<Character> allVars = exp.getAllVar();
      
      String print = "(" + ex + ")";
      if(exp.size() == 3 && canBeQuadFactored(exp, allVars))
         print = FactoringQuadratics.factor(exp, allVars);
      else if(exp.size() == 2)
         print = FactoringBinomials.factor(exp, allVars);
      else if(allVars.size() == 1)
      {
         exp.addZeroes(allVars.get(0));
         print = FactoringPolynomials.factor(exp, allVars);
      }
      else if(exp.size() == 4)
         print = FactoringByGrouping.factor(exp);
      
      if(print.substring(1, print.length() - 1).equals(ex) && factor.isEmpty())
         JOptionPane.showMessageDialog(null, "The expression "+Scripts.superscript(withFrac)+" is not factorable.");
      else 
      {
         if(!fracLCM.equals(""))
            withFrac = fracLCM+"("+withFrac+")";
         String[] factors = print.split(String.format(WITH_DELIMITER, "\\("));
         for(int i = 0; i < factors.length; i++)
         {
            int count = 1;
            for(int j = i + 1; j < factors.length; j++)
               if(factors[i].equals(factors[j]))
                  count++;
            if(count > 1)
            {
               print = factors[i] + "^" + count + print.replace(factors[i], "");
               factors = print.split(String.format(WITH_DELIMITER, "\\("));
            }
         }
         JOptionPane.showMessageDialog(null, Scripts.superscript(withFrac)+" factored is:\n"+Scripts.superscript(factor + print));
      }
      return factor + print;
   }
   /**
   *  Finds and returns the variable portion (or lack thereof) in a given term
   *  @param term the term to be analyzed
   *  @return string representing variable and its power or ' ' if none is found
   */
   private static String getVariable(String term)
   {
      int loc = 0;
      try {
         if(term.charAt(loc) == '+' || term.charAt(loc) == '-')
            loc++;
         while(Character.isDigit(term.charAt(loc)) || term.charAt(loc) == '/')
            loc++;
      } catch(StringIndexOutOfBoundsException e) {
         return "";
      }
      return term.substring(loc, term.length());
   }
   /**
   *  Finds and returns the coefficient in a given term
   *  @param term the term to be analyzed
   *  @param var the variable contained in the term
   *  @return coefficient of the term
   */
   private static Fraction coefficients(String term, String var)
   {
      if(!var.equals(""))
         term = term.substring(0, term.indexOf(var)).replaceAll("\\+", "");
      if(term.equals(""))
         return Fraction.ONE;
      if(term.equals("-"))
         return Fraction.NEGONE;
      return new Fraction(term);
   }
   /**
   *  Finds common factor to multiply all coefficients by,
   *  if there are non-integer coefficients, and alters the coefficients
   *  according by multiplying them by the common factor
   *  @param coef array representing the coef of each term
   *  @return string representing a scalar to make all coefficients whole
   */
   private static String getLCM(Fraction[] coef)
   {
      BigInteger lcm = BigInteger.ONE;
      for(Fraction c : coef)
         lcm = lcm.multiply(c.getDenom()).divide(Functions.gcd(lcm, c.getDenom()));
      for(int x = 0; x < coef.length; x++)
         coef[x] = coef[x].multiply(lcm);
      if(lcm.compareTo(BigInteger.ONE) == 0)
         return "";
      return lcm.toString();
   }
   
   /**
   *  Determines if an expression can be factored similarly to the method
   *  utilized for a quadratic polynomial
   *  @param exp the expression
   *  @param allVars list of each unique variable in the expression
   *  @return whether or not the expression can be factored like a quadratic
   */
   private static boolean canBeQuadFactored(Expression exp, List<Character> allVars)
   {
      for(char c : allVars)
      {
         if(!(exp.getPow(0, c) == 0 && exp.getPow(1, c) == 0) && (double) exp.getPow(0, c) / 2 != exp.getPow(1, c) &&
            !(exp.getPow(2, c) == 0 && exp.getPow(1, c) == 0) && (double) exp.getPow(2, c) / 2 != exp.getPow(1, c))
            return false;
      }
      return true;
   }
}