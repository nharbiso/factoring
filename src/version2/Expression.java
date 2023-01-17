package version2;

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

/**
 * Class representing a term of an expression
 * @author Nathan Harbison
 */
class Term
{
    /**
     * Coefficient of the term
     */
    private int coefficient;
    /**
     *  Map that maps each variable in the term to its power
     */
    private Map<Character, Integer> vars;
    /**
     *  Creates a new Term object with a given coefficient and variables
     *  expressed in a String object
     *  @param c coefficient of the term
     *  @param v variables of the term
     */
    public Term(int c, String v)
    {
        coefficient = c;
        char[] arr = v.toCharArray();
        vars = new HashMap<Character, Integer>();
        for(int x = 0; x < arr.length; x++)
            if(Character.isLetter(arr[x]))
            {
                if(x + 1 == arr.length || arr[x + 1] != '^')
                    vars.put(arr[x], 1);
                else
                {
                    int y = x + 2;
                    while(y < arr.length && Character.isDigit(arr[y]))
                        y++;
                    vars.put(arr[x], Integer.parseInt(v.substring(x + 2, y)));
                    x = y - 1;
                }
            }
    }
    /**
     *  Creates a new Term object with a given coefficient and variables
     *  @param c coefficient of the term
     *  @param v variables of the term
     */
    public Term(int c, Map<Character, Integer> v)
    {
        coefficient = c;
        vars = v;
    }
    /**
     *  Modifies the term's coefficient to the given value
     *  @param x the new coefficient
     */
    public void setCoefficient(int x)
    {
        coefficient = x;
    }
    /**
     *  Gets and returns the term's coefficient
     *  @return the coefficient of the term
     */
    public int getCoef()
    {
        return coefficient;
    }
    /**
     *  Finds and returns the power for a given variable
     *  @param v the variable whose power is being found
     *  @return the power of the given variable
     */
    public int getPow(Character v)
    {
        try {
            return vars.get(v);
        } catch(NullPointerException e) {
            return 0;
        }
    }
    /**
     *  Sets the power for a specific variable in the term
     *  @param v the character whose power is being changed
     *  @param pow the new power of the variable
     */
    public void setPow(Character v, int pow)
    {
        vars.put(v, pow);
    }
    /**
     *  Gets and returns the term's map of variables to their powers
     *  @return the variables, mapped to their powers
     */
    public Map<Character, Integer> getVariables()
    {
        return vars;
    }
    /**
     *  Returns the string expression of a collection of variables based on a
     *  given map connecting each variable to its power
     *  @param vars map representing each variable and its power
     *  @return string expression based on the given map
     */
    public static String getVariables(Map<Character, Integer> vars)
    {
        String fin = "";
        for(char c : vars.keySet())
            fin += getOneVariable(c, vars.get(c));
        return fin;
    }
    /**
     *  Returns the string expression of a variable and its power
     *  @param var character representing the variable
     *  @param pow the power to which the variable is raised
     *  @return string expression of the variable and its power
     */
    public static String getOneVariable(char var, int pow)
    {
        if(pow == 1)
            return var + "";
        if(pow == 0)
            return "";
        return var + "^" + pow;
    }
    /**
     *  Returns the string expression of a given term to
     *  to be placed at the beginning of a string expression
     *  @param t the term to be interpreted
     *  @return a string representation of the term
     */
    public static String frontString(Term t)
    {
        return t.toString();
    }
    /**
     *  Returns the string expression of a given term to
     *  to be placed in the middle or end of a string expression
     *  @param t the term to be interpreted
     *  @return a string representation of the term
     */
    public static String midString(Term t)
    {
        if(t.getCoef() == 0)
            return "";
        String coeff, vars = getVariables(t.getVariables());
        if(t.getCoef() > 0)
            coeff = " + " + t.getCoef();
        else
            coeff = " - " + Math.abs(t.getCoef());
        if(Math.abs(t.getCoef()) == 1 && !vars.isEmpty())
            coeff = coeff.replaceAll("1", "");
        return coeff + vars;
    }
    /**
     *  Determines whether or not the term is equal to another term (if
     *  the object given is not an term, the method will return false)
     *  @return if the terms are exactly alike
     */
    public boolean equals(Object o)
    {
        if(o instanceof Term)
            return toString().equals(((Term) o).toString());
        return false;
    }
    /**
     *  Returns a string value of this term, represented by
     *  its coefficient and the map of its variables to their powers
     *  @return a string expression of this term
     */
    public String toString()
    {
        if(getCoef() == 0)
            return "";
        String s = coefficient + getVariables(vars);
        if(Math.abs(coefficient) == 1 && !s.equals("1") && !s.equals("-1"))
            s = s.replaceAll("1", "");
        return s;
    }
}