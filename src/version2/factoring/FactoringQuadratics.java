package version2.factoring;

import version2.utilities.Expression;
import version2.utilities.Term;
import version2.utilities.Functions;

import java.util.*;

/**
*  Class utilized to factor an expression similarly to a quadratic
*  @author Nathan Harbison
*/
public class FactoringQuadratics
{
   /**
   *  Factors a given quadratic expression, or one similar in form
   *  
   *  @param exp the expression to be factored
   *  @return a string representing the factored expression, or the given expression if unfactorable
   */
   public static String factor(Expression exp, List<Character> allVars)
   {
      int target = exp.getCoef(0) * exp.getCoef(2);
      boolean findSum = target > 0, neg = exp.getCoef(1) < 0;
      int[] mid = new int[2];
      for(int x = 1; x <= Math.abs(target); x++)
      {
         if(findSum && (double) target / x + x == Math.abs(exp.getCoef(1)))
         {
            mid = new int[] {x, target / x};
            if(neg)
            {
               mid[0] *= -1;
               mid[1] *= -1;
            }
            break;
         }
         else if(!findSum && (double) Math.abs(target) / x - x == exp.getCoef(1))
         {
            mid = new int[] {x * -1, Math.abs(target) / x};
            break;
         }
      }
      if(mid[0] == 0 && Functions.isPowerOf2(exp.getPow(0, allVars.get(0))) && 
         Math.log(exp.getPow(0, allVars.get(0))) / Math.log(2) > 1 && 
         Functions.isPerfectSq(exp.getCoef(0)) && Functions.isPerfectSq(exp.getCoef(2)))
      {
         int a = (int) Math.sqrt(exp.getCoef(0)), b = 0, c = (int) Math.sqrt(exp.getCoef(2));
         int ifPos = a * c * 2 - exp.getCoef(1);
         int ifNeg = a * c * -2 - exp.getCoef(1);
         if(Functions.isPerfectSq(ifPos))
            b = (int) Math.sqrt(ifPos);
         else if(Functions.isPerfectSq(ifNeg))
         {
            b = (int) Math.sqrt(ifNeg);
            c *= -1;
         }
         if(b != 0)
         {
            Expression exp1 = exp.getCopy();
            Expression exp2 = exp.getCopy();
            int[] coefs1 = new int[] {a, b, c};
            int[] coefs2 = new int[] {a, b * -1, c};
            for(int i = 0; i < exp1.getTerms().size(); i++)
            {
               Term t1 = exp1.getTerms().get(i);
               Term t2 = exp2.getTerms().get(i);
               t1.setCoefficient(coefs1[i]);
               t2.setCoefficient(coefs2[i]);
               for(char var : t1.getVariables().keySet())
               {
                  t1.setPow(var, t1.getPow(var) / 2);
                  t2.setPow(var, t2.getPow(var) / 2);
               }
            }
            if(allVars.size() == 1)
            {
               exp1.addZeroes(allVars.get(0));
               exp2.addZeroes(allVars.get(0));
               return FactoringPolynomials.factor(exp1, allVars) + FactoringPolynomials.factor(exp2, allVars);
            }
            return "(" + exp1.toString() + ")(" + exp2.toString() + ")";
         }
      }
   
      Expression bi1 = new Expression();
      bi1.addTerm(new Term(exp.getCoef(0), exp.getVars(0)));
      bi1.addTerm(new Term(mid[0], exp.getVars(1)));
      Expression bi2 = new Expression();
      bi2.addTerm(new Term(mid[1], exp.getVars(1)));
      bi2.addTerm(new Term(exp.getCoef(2), exp.getVars(2)));
      Expression fac = new Expression();
      fac.addTerm(bi1.getFactor());
      fac.addTerm(bi2.getFactor());
      if(!bi1.equals(bi2))
         return "(" + exp.toString() + ")";
      return FactoringBinomials.factor(bi1, allVars) + FactoringBinomials.factor(fac, allVars);
   }
}