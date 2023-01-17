package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Term;
import version3.utilities.Fraction;
import version3.utilities.Functions;

import java.util.*;

/**
*  Class utilized to factor an expression by grouping
*  @author Nathan Harbison
*/
public class FactoringByGrouping
{
   /**
   *  Factors a given expression by grouping or a difference of squares
   *  
   *  @param exp the expression to be factored
   *  @return a string representing the factored expression, or the given expression if unfactorable
   */
   public static String factor(Expression exp)
   {
      String factored = factorByGrouping(exp);
      if(!factored.equals(exp.toString()))
         return factored;
      
      factored = factorBySquares(exp);
      if(!factored.equals(exp.toString()))
         return factored;
         
      return "(" + exp.toString() + ")";
   }
   /**
   *  Factors a given expression by grouping, and returns a string
   *  containing the factored form, or the expression if unfactorable
   *  @param exp the expression to be factored
   *  @return the factored expression, or the given expression if unfactorable
   */
   private static String factorByGrouping(Expression exp)
   {
      int[][] positions = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1}};
      for(int[] pos : positions)
      {
         Expression e1 = new Expression();
         e1.addTerm(new Term(exp.getCoefficient(pos[0]), exp.getVars(pos[0])));
         e1.addTerm(new Term(exp.getCoefficient(pos[1]), exp.getVars(pos[1])));
      
         Expression e2 = new Expression();
         e2.addTerm(new Term(exp.getCoefficient(pos[2]), exp.getVars(pos[2])));
         e2.addTerm(new Term(exp.getCoefficient(pos[3]), exp.getVars(pos[3])));
      
         Expression fac = new Expression();
         fac.addTerm(e1.getFactor());
         fac.addTerm(e2.getFactor());
      
         if(e1.equals(e2))      
            return FactoringBinomials.factor(e1) + FactoringBinomials.factor(fac);
      }
      return exp.toString();
   }
   /**
   *  Factors a given expression by finding and factoring
   *  differences of squares, and returns the factored form
   *  contained in a string, or the given expression if unfactorable
   *  @param exp the expression to be factored
   *  @return the factored expression, or the given expression if unfactorable
   */
   private static String factorBySquares(Expression exp)
   {
      List<Integer> posSqInd = new ArrayList<Integer>();
      List<Integer> negSqInd = new ArrayList<Integer>();
      loop: for(int i = 0; i < exp.size(); i++)
      {
         if(!Functions.isPerfectSq(Math.abs(exp.getCoefficient(i))))
            continue loop;
         for(char var : exp.getVars(i).keySet())
            if(exp.getPower(i, var) % 2 != 0)
               continue loop;
         if(exp.getCoefficient(i) > 0)
            posSqInd.add(i);
         else
            negSqInd.add(i);
      }
      for(int posInd : posSqInd)
         for(int negInd : negSqInd)
         {
            String factored = factorSquares(exp, posInd, negInd);
            if(!factored.equals(exp.toString()))
               return factored;
         }
      return exp.toString();
   }
   /**
   *  A helper method that factors a given expression utilizing differences of squares
   *  and the given indices of certain squares
   *  @param exp the expression to be factored
   *  @param posInd the index of a perfect square term with a positive coefficient
   *  @param negInd the index of perfect square term with a negative coefficient
   *  @return the factored expression, or the given expression if unfactorable
   */
   private static String factorSquares(Expression exp, int posInd, int negInd)
   {
      Expression exp1 = new Expression();
      exp1.addReducedTerm((int) Math.sqrt(exp.getCoefficient(posInd)), exp.getVars(posInd), new Fraction("1/2"));
      exp1.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(negInd))), exp.getVars(negInd), new Fraction("1/2"));
      Expression exp2 = new Expression();
      exp2.addReducedTerm((int) Math.sqrt(exp.getCoefficient(posInd)), exp.getVars(posInd), new Fraction("1/2"));
      exp2.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(negInd))) * -1, exp.getVars(negInd), new Fraction("1/2"));
      Expression exp3 = new Expression();
      for(int i = 0; i < exp.size(); i++)
         if(i != posInd && i != negInd)
            exp3.addTerm(new Term(exp.getCoefficient(i), exp.getVars(i)));
      if(!exp3.getVars(0).keySet().equals(exp.getVars(posInd).keySet()) && !exp3.getVars(1).keySet().equals(exp.getVars(negInd).keySet()))
         exp3 = backwards(exp3);
      Term fac = exp3.getFactor();
      if(exp3.equals(exp1))
      {
         exp2.addTerm(fac);
         return FactoringBinomials.factor(exp1) + "(" + exp2.toString() + ")";
      }
      else if(exp3.equals(exp2))
      {
         exp1.addTerm(fac);
         return FactoringBinomials.factor(exp2) + "(" + exp1.toString() + ")";
      }
     
      List<Integer> otherIndices = new ArrayList<Integer>();
      for(int i = 0; i < exp.size(); i++)
         if(i != posInd && i != negInd)
            otherIndices.add(i);
      
      for(int x = 0; x < otherIndices.size(); x++)
      {
         Expression sqrt = sqrt(exp, posInd, otherIndices.get((x + 1) % 2), otherIndices.get(x));
         if(sqrt != null && Functions.isPerfectSq(Math.abs(exp.getCoefficient(otherIndices.get(x)))))
         {
            Expression sqrt2 = new Expression(sqrt);
            sqrt.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(negInd))), exp.getVars(negInd), new Fraction("1/2"));
            sqrt2.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(negInd))) * -1, exp.getVars(negInd), new Fraction("1/2"));
            return "(" + sqrt.toString() + ")(" + sqrt2.toString() + ")";
         }
      }
      for(int x = 0; x < otherIndices.size(); x++)
      {
         Expression sqrt = sqrt(exp, negInd, otherIndices.get((x + 1) % 2), otherIndices.get(x));
         if(sqrt != null && Functions.isPerfectSq(Math.abs(exp.getCoefficient(otherIndices.get(x)))))
         {
            exp1 = new Expression();
            exp1.addReducedTerm((int) Math.sqrt(exp.getCoefficient(posInd)), exp.getVars(posInd), new Fraction("1/2"));
            exp1.addExpression(sqrt);
            exp2 = new Expression();
            exp2.addReducedTerm((int) Math.sqrt(exp.getCoefficient(posInd)), exp.getVars(posInd), new Fraction("1/2"));
            sqrt.multiply(-1);
            exp2.addExpression(sqrt);
            return "(" + exp1.toString() + ")(" + exp2.toString() + ")";
         }
      }
      
      return exp.toString();
   }
   /**
   *  Returns a copy of given binomial expression in backwards order
   *  @param exp the expression to be manipulated
   *  @return the new backwards expression
   */
   private static Expression backwards(Expression exp)
   {
      Expression e = new Expression();
      e.addTerm(new Term(exp.getCoefficient(1), exp.getVars(1)));
      e.addTerm(new Term(exp.getCoefficient(0), exp.getVars(0)));
      return e;
   }
   /**
   *  Determines if three terms in an expression form a perfect square
   *  and returns the square root of said terms
   *  @param exp the expression being analyzed
   *  @param a the index of the first term
   *  @param b the index of the second term
   *  @param c the index of the third term
   *  @return the square root of the three terms, or null if they do not form a perfect square
   */
   private static Expression sqrt(Expression exp, int a, int b, int c)
   {
      if((int) Math.sqrt(Math.abs(exp.getCoefficient(a))) * (int) Math.sqrt(Math.abs(exp.getCoefficient(c))) * 2 != Math.abs(exp.getCoefficient(b)))
         return null;
      Map<Character, Integer> compareVars = new HashMap<Character, Integer>();
      compareVars.putAll(exp.getVars(a));
      compareVars.putAll(exp.getVars(c));
      for(char var : compareVars.keySet())
         if(compareVars.get(var) % 2 != 0)
            return null;
         else
            compareVars.put(var, compareVars.get(var) / 2);
      if(!compareVars.equals(exp.getVars(b)))
         return null;
      Expression sqrt = new Expression();
      sqrt.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(a))), exp.getVars(a), new Fraction("1/2"));
      sqrt.addReducedTerm((int) Math.sqrt(Math.abs(exp.getCoefficient(c))) * exp.getCoefficient(b) / Math.abs(exp.getCoefficient(b)) * exp.getCoefficient(a) / Math.abs(exp.getCoefficient(a)), exp.getVars(c), new Fraction("1/2"));
      return sqrt;
   }
}