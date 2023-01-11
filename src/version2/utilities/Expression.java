package version2.utilities;

import version2.utilities.Term;

import java.util.*;
import java.math.BigInteger;

/**
*  Class representing an expression
*  @author Nathan Harbison
*/
public class Expression
{
   /**
   *  A list of terms representing a given expression
   */
   private List<Term> terms;
   /**
   *  Instantiates a blank expression
   */
   public Expression()
   {
      terms = new ArrayList<Term>();
   }
   /**
   *  Adds a term to the expression, with a given coefficient
   *  and variables
   *  @param t the term to be added
   */
   public void addTerm(Term t)
   {
      terms.add(t);
   }
   /**
   *  Adds a term to the expression, with a given coefficient
   *  and variables at the specified index
   *  @param i the index where the term is to be added
   *  @param t the term to be added
   */
   public void addTerm(int i, Term t)
   {
      terms.add(i, t);
   }
   /**
   *  Adds a term to the expression, with a given coefficient
   *  and variables, with their powers multiplied by a given scalar.
   *  @param c coefficient of the term
   *  @param v unmodified variables of the term
   *  @param scalar modifying scalar of the variables' powers
   */
   public void addTerm(int c, Map<Character, Integer> v, Fraction scalar)
   {
      Map<Character, Integer> copy = new HashMap<Character, Integer>(v);
      for(char ch : copy.keySet())
         copy.put(ch, scalar.multiply(new BigInteger(copy.get(ch)+"")).getNum().intValue());
      addTerm(new Term(c, copy));
   }
   /**
   *  Adds all the terms of given expression to the end of the expression
   *  @param exp the expression containing the terms to be added
   */
   public void addExpression(Expression exp)
   {
      for(Term t : exp.terms)
         addTerm(t);
   }
   /**
   *  Removes the term from the specified index and returns it
   *  @param i the index of the term to be removed
   *  @return the removed term
   */
   public Term removeTerm(int i)
   {
      return terms.remove(i);
   }
   /**
   *  Returns the list of terms consisting the expression
   *  @return the list of terms of the expression
   */
   public List<Term> getTerms()
   {
      return terms;
   }
   /**
   *  Returns the coefficient for a given term, denoted by its index
   *  @param x the index of the term
   *  @return the coefficient of the term
   */
   public int getCoef(int x)
   {
      return terms.get(x).getCoef();
   }
   /**
   *  Returns a list of coefficients in order of the terms' indices
   *  @return a list of all coefficients
   */
   public List<Integer> getCoefs()
   {
      List<Integer> coefs = new ArrayList<Integer>();
      for(Term t : terms)
         coefs.add(t.getCoef());
      return coefs;
   }
   /**
   *  Finds and returns a map describing the variables and their powers
   *  for a specific term, denoted by its index in the expression
   *  @param x the index of the term
   *  @return a map representing the variables and their respective powers for the desired term
   */
   public Map<Character, Integer> getVars(int x)
   {
      Map<Character, Integer> map = new HashMap<Character, Integer>();
      map.putAll(terms.get(x).getVariables());
      return map;
   }
   /**
   *  Returns the power of a variable in a given term, denoted by its index
   *  @param x the index of the term
   *  @param v the variable whose power is desired
   *  @return the coefficient of the term
   */
   public Integer getPow(int x, char v)
   {
      return terms.get(x).getPow(v);
   }
   /**
   *  Multiplies all coefficients by a given scalar
   *  @param scalar the scalar multiplier of the expression
   */
   public void multiply(int scalar)
   {
      for(Term t : terms)
         t.setCoefficient(t.getCoef() * scalar);
   }
   /**
   *  For a polynomial expression, adds all terms for variables with a power
   *  less than the maximum in the expression that do not exist, each with 
   *  a coefficient of zero. This method will also order the terms by decreasing power.
   *  @param var the sole variable in the polynomial expression
   */
   public void addZeroes(char var)
   {
      if(terms.size() < getPow(0, var) + 1)
      {
         for(int i = 0; i < terms.size() - 1; i++)
            if(getPow(i, var) - getPow(i + 1, var) > 1)
            {
               Map<Character, Integer> temp = new HashMap<Character, Integer>();
               temp.put(var, getPow(i, var) - 1);
               terms.add(i + 1, new Term(0, temp));
            }
      }
   }
   /**
   *  Removes all terms with coefficients of zero in the expression
   */
   public void removeZeroes()
   {
      for(int i = terms.size() - 1; i >= 0; i--)
         if(getCoef(i) == 0)
            terms.remove(i);
   }
   /**
   *  Returns the number of terms in the expression
   *  @return the size of the expression
   */
   public int size()
   {
      return terms.size();
   }
   /**
   *  Finds and returns the given factor of an expression,
   *  consisting of the numerical and variable factor
   *  @return the factor of the expression, as a term object
   */
   public Term getFactor()
   {
      if(terms.size() == 1)
         return new Term(0, "");
      
      //if only one variable, reorder in decreasing power
      List<Character> allVar = getAllVar();
      if(allVar.size() == 1)
         for(int i = 0; i < terms.size() - 1; i++)
         {
            int max = i;
            for(int j = i + 1; j < terms.size(); j++)
               if(getPow(j, allVar.get(0)) > getPow(max, allVar.get(0)))
                  max = j;
            Term t = terms.get(i);
            terms.set(i, terms.get(max));
            terms.set(max, t);
         }
      
      //find numerical factor
      int numGCD = Math.abs(terms.get(0).getCoef());
      for(int x = 1; x < terms.size(); x++)
         numGCD = Functions.gcd(numGCD, Math.abs(terms.get(x).getCoef()));
      if(terms.get(0).getCoef() < 0)
         numGCD *= -1;
      for(Term t : terms)
         t.setCoefficient(t.getCoef() / numGCD);
      
      //find variable(s) factor
      List<Character> varInAll = new ArrayList<Character>();
      outer: for(Character c : allVar)
         if(!varInAll.contains(c))
         {
            for(Term t : terms)
               if(t.getPow(c) == 0)
                  continue outer;
            varInAll.add(c);
         }
      Map<Character, Integer> varToPow = new HashMap<Character, Integer>();
      for(Character var : varInAll)
      {
         int min = terms.get(0).getPow(var);
         for(int x = 1; x < terms.size(); x++)
            min = Math.min(min, terms.get(x).getPow(var));
         for(Term t : terms)
         {
            int newPow = t.getPow(var) - min;
            if(newPow > 0)
               t.getVariables().put(var, t.getPow(var) - min);
            else
               t.getVariables().remove(var);
         }
         varToPow.put(var, min);
      }
      return new Term(numGCD, varToPow);
   }
   /**
   *  Creates and returns a list containing all unique
   *  variables in the expression
   *  @return a list of characters, each representing a unique variable in the given expression
   */
   public List<Character> getAllVar()
   {
      Set<Character> set = new HashSet<Character>();
      for(int i = 0; i < terms.size(); i++)
         set.addAll(terms.get(i).getVariables().keySet());
      return new ArrayList<Character>(set);
   }
   /**
   *  Returns a copy of the expression object
   *  @return a copy of this expression
   */ 
   public Expression getCopy()
   {
      Expression copy = new Expression();
      for(int i = 0; i < terms.size(); i++)
         copy.addTerm(new Term(getCoef(i), getVars(i)));
      return copy;
   }
   
   /**
   *  Converts a list of coefficients into a polynomial of decreasing power
   *  @param coef a list of the polynomial's coefficients
   *  @param var the variable in the expression
   *  @return an expression containing said coefficients
   */
   public static Expression coefToExp(List<Integer> coef, char var)
   {
      Expression exp  = new Expression();
      for(int pow = coef.size() - 1; pow >= 0; pow--)
      {
         Map<Character, Integer> map = new HashMap<Character, Integer>();
         map.put(var, pow);
         exp.addTerm(new Term(coef.get(coef.size() - pow - 1), map));
      }
      return exp;
   }
   /**
   *  Determines whether or not the expression is equal to another expression (if
   *  the object given is not an expression, the method will return false)
   *  @return if the expressions are exactly alike
   */
   public boolean equals(Object o)
   {
      if(o instanceof Expression)
      {
         Expression exp = (Expression) o;
         if(size() != exp.size())
            return false;
         for(Term t : terms)
            if(!exp.terms.contains(t))
               return false;
         return true;
      }
      return false;
   }
   /**
   *  Returns a string representing the expression
   *  @return a string representation of the expression
   */
   public String toString()
   {
      if(size() > 0)
      {
         String s = Term.frontString(terms.get(0));
         for(int x = 1; x < terms.size(); x++)
            s += Term.midString(terms.get(x));
         return s;
      }
      return "";
   }
}