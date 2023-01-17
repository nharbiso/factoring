package version3.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a term of an expression,
 * with integer coefficients and variables
 * @author Nathan Harbison
 */
public class Term {
    /** Coefficient of the term */
    private int coefficient;
    /** Map between each variable in the term and its power */
    private final Map<Character, Integer> vars;

    private final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // ---------------------------------------------------------------------------------------
    // Constructors

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
            if(Character.isLetter(chars[i])) {
                // power of one - no caret character
                if(i + 1 == chars.length || chars[i + 1] != '^')
                    this.vars.put(chars[i], 1);
                // caret character present - any greater integer power
                else {
                    int endOfPower = i + 2; // find end of the integer power in string
                    while (endOfPower < chars.length && Character.isDigit(chars[endOfPower]))
                        endOfPower++;
                    String powerStr = varStr.substring(i + 2, endOfPower);
                    try {
                        int power = Integer.parseInt(powerStr);
                        this.vars.put(chars[i], power);
                    } catch(NumberFormatException ex) {
                        throw new IllegalArgumentException("Error: Non-integer power given as a variable:" + powerStr);
                    }

                    i = endOfPower - 1; // for loop addition will increase i to endOfPower
                }
            } else
                throw new IllegalArgumentException("Error: Non-alphabetic character used as a variable: " + chars[i]);
        }
    }

    /**
     *  Creates a new Term object with a given coefficient and variables
     *  @param coeff coefficient of the term
     *  @param vars variables of the term
     */
    public Term(int coeff, Map<Character, Integer> vars) {
        this.coefficient = coeff;
        this.vars = new HashMap<>(vars);
    }

    /**
     *  Creates a copy of the given Term object
     *  @param term the term to be copied
     */
    public Term(Term term) {
        this(term.coefficient, term.vars);
    }

    // ---------------------------------------------------------------------------------------
    // Accessors and modifiers

    /**
     *  Gets and returns the term's coefficient
     *  @return the coefficient of the term
     */
    public int getCoefficient() {
        return this.coefficient;
    }

    /**
     * Modifies the coefficient to the given value
     * @param coeff the new coefficient of the term
     */
    public void setCoefficient(int coeff) {
        this.coefficient = coeff;
    }

    /**
     *  Finds and returns the power for a given variable
     *  @param var the variable whose power is being found
     *  @return the power of the given variable
     *  @throws IllegalArgumentException if given an invalid non-alphabetic variable
     */
    public int getPower(Character var) {
        if(!Character.isLetter(var))
            throw new IllegalArgumentException("Error: Invalid non-alphabetic variable " + var);
        try {
            return this.vars.get(var);
        } catch(NullPointerException ex) {
            // if variable not in map, it has a power of 0 (e.g. x = xz^0)
            return 0;
        }
    }

    /**
     *  Sets the power for a specific variable in the term
     *  @param var the variable whose power is being changed
     *  @param power the new power of the variable
     */
    public void setPower(char var, int power) {
        this.vars.put(var, power);
    }

    /**
     *  Gets and returns the term's map of variables to their powers. Modifying this map
     *  modifies the corresponding term.
     *  @return the variables, mapped to their powers
     */
    public Map<Character, Integer> getVariables() {
        return this.vars;
    }

    // ---------------------------------------------------------------------------------------
    // To-string and equality methods

    /**
     *  Returns the string expression of the variables in the given term
     *  @return string expression of the term's variables
     */
    public String getVarStr() {
        String varStr = "";
        for(char var : ALPHABET.toCharArray())
            if(this.vars.containsKey(var))
                varStr += getOneVariable(var, this.vars.get(var));
        return varStr;
    }
    /**
     *  Returns the string expression of a variable and its power
     *  @param var character representing the variable
     *  @param pow the power to which the variable is raised
     *  @return string expression of the variable and its power
     *  @throws IllegalArgumentException if given an invalid non-alphabetic variable
     */
    public static String getOneVariable(char var, int pow) {
        if(!Character.isLetter(var))
            throw new IllegalArgumentException("Error: Invalid non-alphabetic variable " + var);

        if(pow == 1)
            return Character.toString(var);
        if(pow == 0)
            return "";
        return var + "^" + pow;
    }
    /**
     *  Returns the string form of the given term for placement at
     *  the beginning of an expression (without a space or plus sign)
     *  @return a string representation of the term
     */
    public String frontStr() {
        if(this.getCoefficient() == 0)
            return "";
        String termStr = this.coefficient + this.getVarStr();
        if(Math.abs(coefficient) == 1 && !termStr.equals("1") && !termStr.equals("-1"))
            termStr = termStr.replaceAll("1", "");
        return termStr;
    }
    /**
     *  Returns the string form of the given term for placement at
     *  the middle or end of an expression (with a space and plus/minus signs)
     *  @return a string representation of the term
     */
    public String middleStr() {
        if(this.getCoefficient() == 0)
            return "";
        String coeff;
        String vars = getVarStr();
        if(getCoefficient() > 0)
            coeff = " + " + getCoefficient();
        else
            coeff = " - " + Math.abs(getCoefficient());
        if(Math.abs(getCoefficient()) == 1 && !vars.isEmpty())
            coeff = coeff.replaceAll("1", "");
        return coeff + vars;
    }

    /**
     *  Determines if equality between the term and another object,
     *  returning true if both are identical terms
     *  @return if the object is an identical term
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj instanceof Term otherTerm) {
            System.out.println("Coeff: " + this.coefficient + ", vars: " + this.vars);
            System.out.println("Coeff: " + otherTerm.coefficient + ", vars: " + otherTerm.vars);
            return this.toString().equals(otherTerm.toString());
        }
        return false;
    }

    /**
     * Hashes the given term
     * @return a hash code for the given term
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.coefficient, this.vars);
    }

    /**
     *  Returns a string value of this term, represented by
     *  its coefficient and the map of its variables to their powers
     *  @return a string expression of this term
     */
    @Override
    public String toString() {
        return this.frontStr();
    }
}
