package version3.utilities;

import java.util.*;
import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * Class representing a mathematical expression consisting of added terms, each
 * composed of one or more variables to integer powers and an integer coefficient.
 * @author Nathan Harbison
 */
public class Expression {
   /** A list of terms representing a given expression. */
   private List<Term> terms;

   // ---------------------------------------------------------------------------------------
   // Constructors

   /**
    * Instantiates a blank expression with no terms.
    */
   public Expression() {
      this.terms = new ArrayList<>();
   }

   /**
    * Instantiates an expression with the given list of terms,
    * in the given order.
    */
   public Expression(List<Term> terms) {
      this.terms = new ArrayList<>();
      for(Term term : terms)
         this.terms.add(new Term(term));
   }

   /**
    * Instantiates a copy of the given expression.
    */
   public Expression(Expression exp) {
      this(exp.terms);
   }

   /**
    * Instantiates a polynomial of the given variable, with terms of
    * decreasing power and with the given coefficients as in their given order.
    * @param coeffs an ordered list of the polynomial's coefficients.
    * @param var the variable in the expression.
    */
   public Expression(List<BigInteger> coeffs, char var) {
      this.terms = new ArrayList<>();
      for(int ind = 0; ind < coeffs.size(); ind++) {
         int power = coeffs.size() - ind - 1;
         Map<Character, Integer> termVars = new HashMap<>();
         if(power > 0)
            termVars.put(var, power);
         this.terms.add(new Term(coeffs.get(ind), termVars));
      }
   }

   /**
    * Instantiates a new expression by parsing the given string
    * representation of one.
    * @param expStr a string representation of an expression.
    */
   public Expression(String expStr) {
      expStr = expStr.replaceAll("\\s+", "");
      String[] terms = expStr.split(String.format("(?=%1$s)", "\\+|-"));

      String[] variables = new String[terms.length];
      for(int i = 0; i < variables.length; i++)
         variables[i] = Functions.getVariable(terms[i]);

      // parse the coefficient from each term
      Fraction[] coefficients = new Fraction[terms.length];
      for(int i = 0; i < coefficients.length; i++)
         coefficients[i] = Functions.parseCoefficient(terms[i], variables[i]);

      this.terms = new ArrayList<>();
      for(int i = 0; i < coefficients.length; i++) {
         if(!coefficients[i].isWhole())
            throw new IllegalArgumentException("Error: non-integer coefficients not supported");
         this.terms.add(new Term(coefficients[i].getNum(), variables[i]));
      }
   }

   // ---------------------------------------------------------------------------------------
   // Modifiers

   /**
    * Adds a term to the end of expression, with a given
    * coefficient and variables.
    * @param term the term to be added.
    */
   public void addTerm(Term term) {
      this.terms.add(term);
   }

   /**
    * Adds a term to the expression at the specified index
    * in the ordered list of the expression's terms.
    * @param ind the index where the term is to be added.
    * @param term the term to be added.
    */
   public void addTerm(int ind, Term term) {
      this.terms.add(ind, term);
   }

   /**
    * Adds a term to the end of the expression, with a given coefficient
    * and variables, with their powers multiplied by a given scalar.
    * @param coeff the coefficient of the term.
    * @param vars a map of the unmodified term's variables to their powers.
    * @param scalar modifying scalar of the variables' powers.
    */
   public void addReducedTerm(BigInteger coeff, Map<Character, Integer> vars, Fraction scalar) {
      Map<Character, Integer> modVars = new HashMap<>(vars);
      for(char var : modVars.keySet()) {
         int power = modVars.get(var);
         int newPower = scalar.multiply(BigInteger.valueOf(power)).intValue();
         modVars.put(var, newPower);
      }
      this.terms.add(new Term(coeff, modVars));
   }

   /**
    * Adds a term at the specified index in the expression, with a given coefficient
    * and variables, with their powers multiplied by a given scalar.
    * @param ind the index in the expression where the term is to be added.
    * @param coeff the coefficient of the term.
    * @param vars a map of the unmodified term's variables to their powers.
    * @param scalar modifying scalar of the variables' powers.
    */
   public void addReducedTerm(int ind, BigInteger coeff, Map<Character, Integer> vars, Fraction scalar) {
      Map<Character, Integer> modVars = new HashMap<>(vars);
      for(char var : modVars.keySet()) {
         int power = modVars.get(var);
         int newPower = scalar.multiply(BigInteger.valueOf(power)).intValue();
         modVars.put(var, newPower);
      }
      this.terms.add(ind, new Term(coeff, modVars));
   }

   /**
    * Removes the term from the specified index and returns it
    * @param ind the index of the term to be removed.
    * @return the removed term.
    */
   public Term removeTerm(int ind) {
      return this.terms.remove(ind);
   }

   /**
    * Multiplies all coefficients by a given scalar.
    * @param scalar the scalar multiplier of the expression.
    */
   public void multiply(int scalar) {
      for(Term term : this.terms)
         term.setCoeff(term.getCoeff().multiply(BigInteger.valueOf(scalar)));
   }

   /**
    * For a polynomial (i.e. with one variable), adds terms with a coefficient of
    * zero for all variables with a power less than the maximum in the expression that do not exist.
    * The polynomial is also sorted by decreasing variable power. If the expression is not a polynomial,
    * no operation is completed.
    */
   public void addZeroes() {
      Set<Character> vars = this.getAllVars();
      if(vars.size() != 1)
         return;

      char var = Functions.getItemFromSet(vars);

      this.orderPolynomial();

      if(this.terms.size() < getPower(0, var) + 1) { // terms need to be added
         for(int i = 0; i < this.terms.size(); i++) {
            int power = getPower(0, var) - i;
            if(getPower(i, var) != power) {
               Map<Character, Integer> varToPow = new HashMap<>();
               varToPow.put(var, power);
               this.terms.add(i, new Term(0, varToPow));
            }
         }
      }
   }

   /**
    * Removes all terms with coefficients of zero in the expression.
    */
   public void removeZeroes() {
      List<Term> newTerms = new ArrayList<>();
      for(Term term : this.terms)
         if(!term.getCoeff().equals(BigInteger.ZERO))
            newTerms.add(term);
      this.terms = newTerms;
   }

   /**
    * For a polynomial (i.e. with one variable), orders the expression by decreasing
    * power of the variable. If the expression is not a polynomial, no operation is completed.
    */
   public void orderPolynomial() {
      Set<Character> vars = this.getAllVars();
      if(vars.size() != 1)
         return;
      char var = Functions.getItemFromSet(vars);

      // sort in reverse order
      this.terms.sort(Comparator.comparingInt(term -> -1 * term.getPower(var)));
   }

   /**
    * Evaluates the expression using the given integer values for each variable,
    * as given by the map.
    * @param values a map of each variable to its integer value for substitution.
    * @return the evaluation of the expression after substituting the given integer values
    * for each variable.
    * @throws IllegalArgumentException if values does not contain an entry for each variable
    * in the expression.
    */
   public BigInteger evaluateExpression(Map<Character, Integer> values) {
      BigInteger eval = BigInteger.ZERO;
      for(Term term : this.terms) {
         BigInteger termVal = term.getCoeff();
         for(char var : term.getVariables()) {
            if(!values.containsKey(var)) {
               throw new IllegalArgumentException("Error: given map does not contain variable '" + var + "' contained in expression.");
            }
            termVal = termVal.multiply(BigInteger.valueOf((long) Math.pow(values.get(var), term.getPower(var))));
         }
         eval = eval.add(termVal);
      }
      return eval;
   }

   // ---------------------------------------------------------------------------------------
   // Accessors

   /**
    * Returns the number of terms in the expression.
    * @return the size of the expression.
    */
   public int size() {
      return terms.size();
   }

   /**
    * Returns the number of non-zero terms in the expression.
    * @return the number of non-zero terms in the expression.
    */
   public int nonZeroTerms() {
      int terms = 0;
      for(Term term : this.terms)
         if(!term.getCoeff().equals(BigInteger.ZERO))
            terms++;
      return terms;
   }

   /**
    * Returns the term at the given index within the expression.
    * @return the term at the given index.
    */
   public Term getTerm(int ind) {
      return new Term(this.terms.get(ind));
   }

   /**
    * Returns the coefficient for a given term at the given index
    * in the expression.
    * @param ind the index of the term.
    * @return the coefficient of the term.
    */
   public BigInteger getCoeff(int ind) {
      return this.terms.get(ind).getCoeff();
   }

   /**
    * Returns a list of coefficients in order of the terms' placement
    * in the expression.
    * @return a list of all coefficients.
    */
   public List<BigInteger> getCoeffs() {
      List<BigInteger> coeffs = new ArrayList<>();
      for(Term term : this.terms)
         coeffs.add(term.getCoeff());
      return coeffs;
   }

   /**
    * Finds and returns a map describing the variables and their powers
    * for a specific term, as given by its index in the expression.
    * @param ind the index of the term.
    * @return a map mapping variables to their respective powers for the desired term.
    */
   public Map<Character, Integer> getVarPowers(int ind) {
      Term term = this.terms.get(ind);
      Map<Character, Integer> powers = new HashMap<>();
      for(char var : term.getVariables()) {
         powers.put(var, term.getPower(var));
      }
      return powers;
   }

   /**
    * Returns the power of a variable in a given term, as given its index
    * in the expression.
    * @param ind the index of the term.
    * @param var the variable whose power is desired.
    * @return the coefficient of the term.
    */
   public int getPower(int ind, char var) {
      return this.terms.get(ind).getPower(var);
   }

   /**
    * For a non-empty expression, factors out the largest possible term, in terms of its
    * coefficient and variables' powers, from the given expression, and returns said term.
    * @return the factor of the expression, as a term object.
    * @throws IllegalStateException if the expression is empty.
    */
   public Term getFactor() {
      if(this.terms.size() == 0)
         throw new IllegalStateException("Error: cannot factor an empty expression.");

      if(this.terms.size() == 1)
         return new Term(1, new HashMap<>());
      
      // if only one variable, reorder in decreasing power
      Set<Character> allVars = this.getAllVars();
      if(allVars.size() == 1)
         this.orderPolynomial();
      
      // find numerical factor
      BigInteger numGCD = Functions.gcd(this.getCoeffs());
      if(this.terms.get(0).getCoeff().compareTo(BigInteger.ZERO) < 0)
         numGCD = numGCD.negate();
      for(Term term : this.terms)
         term.setCoeff(term.getCoeff().divide(numGCD));
      
      // find variables present in all terms
      Set<Character> varsInAll = new HashSet<>();
      for(Term term : terms)
         varsInAll.addAll(term.getVariables());

      // factor out the highest power of variables present in all terms
      Map<Character, Integer> varToPow = new HashMap<>();
      for(char var : varsInAll) {
         // find the smallest power of the variable
         int minPower = this.terms.get(0).getPower(var);
         for(int i = 1; i < this.terms.size(); i++)
            minPower = Math.min(minPower, this.terms.get(i).getPower(var));

         if(minPower == 0)
            continue;

         // divide all terms by the variable to its smallest power present in the expression
         for(Term term : this.terms) {
            term.setPower(var, term.getPower(var) - minPower);
         }
         varToPow.put(var, minPower);
      }

      return new Term(numGCD, varToPow);
   }

   /**
    * Creates and returns a set containing all unique
    * variables in the expression.
    * @return a set of characters, each representing a unique variable in the given expression.
    */
   public Set<Character> getAllVars()
   {
      Set<Character> vars = new HashSet<>();
      for(Term term : this.terms)
         vars.addAll(term.getVariables());
      return vars;
   }

   // ---------------------------------------------------------------------------------------
   // To-string and equality methods

   /**
    * Determines equality between the expression and another object,
    * returning true if both are identical expressions, ignoring order
    * and terms with coefficients of 0.
    * @return if the object is an identical expression.
    */
   @Override
   public boolean equals(Object obj)
   {
      if(this == obj)
         return true;
      if(obj instanceof Expression otherExp) {
         Set<Term> termsNoOrder1 = this.terms.stream()
                 .filter(term -> !term.getCoeff().equals(BigInteger.ZERO)).collect(Collectors.toSet());
         Set<Term> termsNoOrder2 = otherExp.terms.stream()
                 .filter(term -> !term.getCoeff().equals(BigInteger.ZERO)).collect(Collectors.toSet());
         return termsNoOrder1.equals(termsNoOrder2);
      }
      return false;
   }

   /**
    * Hashes the given expression.
    * @return a hash code for the given expression.
    */
   @Override
   public int hashCode() {
      Set<Term> termsNoOrder = new HashSet<>(this.terms);
      return Objects.hash(termsNoOrder);
   }

   /**
    * Returns a string representing the expression.
    * @return a string representation of the expression.
    */
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      if(this.size() > 0)
      {
         builder.append(this.terms.get(0).frontStr());
         for(int i = 1; i < terms.size(); i++)
            builder.append(this.terms.get(i).middleStr());
      }
      return builder.toString();
   }
}