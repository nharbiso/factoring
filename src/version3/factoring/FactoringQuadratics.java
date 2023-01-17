package version3.factoring;

import version3.utilities.Expression;
import version3.utilities.Term;
import version3.utilities.Functions;

import java.util.Set;

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
   public static String factor(Expression exp)
   {
      Set<Character> allVars = exp.getAllVars();

      int target = exp.getCoefficient(0) * exp.getCoefficient(2);
      boolean findSum = target > 0, neg = exp.getCoefficient(1) < 0;
      int[] mid = new int[2];
      for(int x = 1; x <= Math.abs(target); x++)
      {
         if(findSum && (double) target / x + x == Math.abs(exp.getCoefficient(1)))
         {
            mid = new int[] {x, target / x};
            if(neg)
            {
               mid[0] *= -1;
               mid[1] *= -1;
            }
            break;
         }
         else if(!findSum && (double) Math.abs(target) / x - x == exp.getCoefficient(1))
         {
            mid = new int[] {x * -1, Math.abs(target) / x};
            break;
         }
      }

      char randVar = Functions.getItem(allVars);

      if(mid[0] == 0 && Functions.isPowerOf2(exp.getPower(0, randVar)) &&
         Math.log(exp.getPower(0, randVar)) / Math.log(2) > 1 &&
         Functions.isPerfectSq(exp.getCoefficient(0)) && Functions.isPerfectSq(exp.getCoefficient(2)))
      {
         int a = (int) Math.sqrt(exp.getCoefficient(0)), b = 0, c = (int) Math.sqrt(exp.getCoefficient(2));
         int ifPos = a * c * 2 - exp.getCoefficient(1);
         int ifNeg = a * c * -2 - exp.getCoefficient(1);
         if(Functions.isPerfectSq(ifPos))
            b = (int) Math.sqrt(ifPos);
         else if(Functions.isPerfectSq(ifNeg))
         {
            b = (int) Math.sqrt(ifNeg);
            c *= -1;
         }
         if(b != 0)
         {
            Expression exp1 = new Expression(exp);
            Expression exp2 = new Expression(exp);
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
                  t1.setPower(var, t1.getPower(var) / 2);
                  t2.setPower(var, t2.getPower(var) / 2);
               }
            }
            if(allVars.size() == 1)
            {
               exp1.addZeroes();
               exp2.addZeroes();
               return FactoringPolynomials.factor(exp1) + FactoringPolynomials.factor(exp2);
            }
            return "(" + exp1.toString() + ")(" + exp2.toString() + ")";
         }
      }
   
      Expression bi1 = new Expression();
      bi1.addTerm(new Term(exp.getCoefficient(0), exp.getVars(0)));
      bi1.addTerm(new Term(mid[0], exp.getVars(1)));
      Expression bi2 = new Expression();
      bi2.addTerm(new Term(mid[1], exp.getVars(1)));
      bi2.addTerm(new Term(exp.getCoefficient(2), exp.getVars(2)));
      Expression fac = new Expression();
      fac.addTerm(bi1.getFactor());
      fac.addTerm(bi2.getFactor());
      if(!bi1.equals(bi2))
         return "(" + exp.toString() + ")";
      return FactoringBinomials.factor(bi1) + FactoringBinomials.factor(fac);
   }
}