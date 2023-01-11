package version2.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a term of an expression,
 * with integer coefficients and variables
 * @author Nathan Harbison
 */
public class Term
{
    /** Coefficient of the term */
    private int coefficient;
    /** Map between each variable in the term and its power */
    private final Map<Character, Integer> vars;
    /**
     *  Creates a new Term object with a given coefficient and variables
     *  expressed in a String object
     *  @param coeff coefficient of the term
     *  @param varStr the string written representation of the term's variables and powers
     */
    public Term(int coeff, String varStr) {
        this.coefficient = coeff;
        char[] chars = varStr.toCharArray();
        this.vars = new HashMap<>();
        for(int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                // power of one - no caret character
                if (i + 1 == chars.length || chars[i + 1] != '^')
                    this.vars.put(chars[i], 1);
                // caret character present - any greater integer power
                else {
                    int endOfPower = i + 2; // find end of the integer power in string
                    while (endOfPower < chars.length && Character.isDigit(chars[endOfPower]))
                        endOfPower++;
                    try {
                        int power = Integer.parseInt(varStr.substring(i + 2, endOfPower));
                        this.vars.put(chars[i], power);
                    } catch(NumberFormatException ex) {
                        throw new IllegalArgumentException("Error: Non-integer power given as a variable");
                    }

                    i = endOfPower - 1; // for loop addition will increase i to endOfPower
                }
            } else
                throw new IllegalArgumentException("Error: Non-alphabetic character used as a variable");
        }
    }
    /**
     *  Creates a new Term object with a given coefficient and variables
     *  @param coeff coefficient of the term
     *  @param vars variables of the term
     */
    public Term(int coeff, Map<Character, Integer> vars)
    {
        this.coefficient = coeff;
        this.vars = vars;
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
