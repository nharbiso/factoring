package version3.utilities;

import java.util.*;
import java.math.BigInteger;

/**
 *  Class representing an expression
 *  @author Nathan Harbison
 */
public class Expression {
   /** A list of terms representing a given expression */
   private List<Term> terms;

   // ---------------------------------------------------------------------------------------
   // Constructors

   /**
    *  Instantiates a blank expression with no terms
    */
   public Expression() {
      this.terms = new ArrayList<>();
   }

   /**
    * Instantiates an expression with the given list of terms,
    * in the given order.
    */
   public Expression(List<Term> terms) {
      this.terms = new ArrayList<>(terms);
   }

   /**
    * Instantiates a copy of the given expression.
    */
   public Expression(Expression exp) {
      this.terms = new ArrayList<>(exp.terms);
   }

   /**
    *  Instantiates an expression with the order and coefficients of the given
    *  list, as a polynomial of the given variable in decreasing power
    *  @param coeffs a list of the polynomial's coefficients
    *  @param var the variable in the expression
    */
   public Expression(List<Integer> coeffs, char var)
   {
      this.terms = new ArrayList<>();
      for(int ind = 0; ind < coeffs.size(); ind++)
      {
         int power = coeffs.size() - ind - 1;
         Map<Character, Integer> termVars = new HashMap<>();
         termVars.put(var, power);
         this.terms.add(new Term(coeffs.get(ind), termVars));
      }
   }

   // ---------------------------------------------------------------------------------------
   // Modifiers

   /**
    *  Adds a term to the expression, with a given coefficient
    *  and variables
    *  @param term the term to be added
    */
   public void addTerm(Term term) {
      this.terms.add(term);
   }

   /**
    *  Adds a term to the expression, with a given coefficient
    *  and variables at the specified index in the ordered list of
    *  the expression's terms
    *  @param ind the index where the term is to be added
    *  @param term the term to be added
    */
   public void addTerm(int ind, Term term) {
      this.terms.add(ind, term);
   }

   /**
    *  Adds a term to the expression, with a given coefficient
    *  and variables, with their powers multiplied by a given scalar.
    *  @param coeff coefficient of the term
    *  @param vars a map of the unmodified term's variables to their powers
    *  @param scalar modifying scalar of the variables' powers
    */
   public void addReducedTerm(int coeff, Map<Character, Integer> vars, Fraction scalar) {
      Map<Character, Integer> modVars = new HashMap<>(vars);
      for(char var : modVars.keySet()) {
         int power = modVars.get(var);
         int newPower = scalar.multiply(BigInteger.valueOf(power)).toInt();
         modVars.put(var, newPower);
      }
      addTerm(new Term(coeff, modVars));
   }

   /**
    *  Adds all the terms of given expression to the end of the expression
    *  @param exp the expression containing the terms to be added
    */
   public void addExpression(Expression exp) {
      for(Term term : exp.terms)
         addTerm(new Term(term));
   }

   /**
    *  Removes the term from the specified index and returns it
    *  @param ind the index of the term to be removed
    *  @return the removed term
    */
   public Term removeTerm(int ind) {
      return this.terms.remove(ind);
   }

   /**
    *  Multiplies all coefficients by a given scalar
    *  @param scalar the scalar multiplier of the expression
    */
   public void multiply(int scalar) {
      for(Term term : this.terms)
         term.setCoefficient(scalar * term.getCoefficient());
   }

   /**
    *  For a polynomial (i.e. with one variable), adds terms with a coefficient of
    *  zero for all variables with a power less than the maximum in the expression that do not exist.
    *  The polynomial is also sorted by decreasing variable power. If the expression is not a polynomial,
    *  no operation is completed.
    */
   public void addZeroes() {
      Set<Character> vars = this.getAllVars();
      if(vars.size() != 1)
         return;

      char var = Functions.getItem(vars);

      this.orderPolynomial();

      if(this.terms.size() < getPower(0, var) + 1) { // terms need to be added
         for(int i = 0; i < this.terms.size(); i++) {
            int power = this.terms.size() - i - 1;
            if(getPower(i, var) != power) {
               Map<Character, Integer> varToPow = new HashMap<>();
               varToPow.put(var, power);
               this.terms.add(i, new Term(0, varToPow));
            }
         }
      }
   }

   /**
    *  Removes all terms with coefficients of zero in the expression
    */
   public void removeZeroes() {
      List<Term> newTerms = new ArrayList<>();
      for(Term term : this.terms)
         if(term.getCoefficient() != 0)
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
      char var = Functions.getItem(vars);

      // sort in reverse order
      this.terms.sort(Comparator.comparingInt(term -> -1 * term.getPower(var)));
   }

   // ---------------------------------------------------------------------------------------
   // Accessors

   /**
    *  Returns the number of terms in the expression
    *  @return the size of the expression
    */
   public int size() {
      return terms.size();
   }

   /**
    *  Returns the list of terms consisting the expression
    *  @return the list of terms of the expression
    */
   public List<Term> getTerms() {
      return new ArrayList<>(this.terms);
   }

   /**
    *  Returns the coefficient for a given term, denoted by its index
    *  @param ind the index of the term
    *  @return the coefficient of the term
    */
   public int getCoefficient(int ind) {
      return this.terms.get(ind).getCoefficient();
   }

   /**
    *  Returns a list of coefficients in order of the terms' indices
    *  @return a list of all coefficients
    */
   public List<Integer> getCoefficients() {
      List<Integer> coefs = new ArrayList<>();
      for(Term term : this.terms)
         coefs.add(term.getCoefficient());
      return coefs;
   }

   /**
    *  Finds and returns a map describing the variables and their powers
    *  for a specific term, denoted by its index in the expression
    *  @param ind the index of the term
    *  @return a map mapping variables to their respective powers for the desired term
    */
   public Map<Character, Integer> getVars(int ind) {
      return new HashMap<>(this.terms.get(ind).getVariables());
   }

   /**
    *  Returns the power of a variable in a given term, denoted by its index
    *  @param ind the index of the term
    *  @param var the variable whose power is desired
    *  @return the coefficient of the term
    */
   public int getPower(int ind, char var) {
      return this.terms.get(ind).getPower(var);
   }

   /**
    *  For a non-empty expression, factors out the largest possible term, in terms of its
    *  coefficient and variables' powers, from the given expression, and returns said term.
    *  @return the factor of the expression, as a term object
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
      int numGCD = Functions.gcd(this.getCoefficients());
      if(this.terms.get(0).getCoefficient() < 0)
         numGCD *= -1;
      for(Term term : this.terms)
         term.setCoefficient(term.getCoefficient() / numGCD);
      
      // find variables present in all terms
      Set<Character> varsInAll = new HashSet<>();
      outer: for(char var : allVars) {
         for(Term term : terms)
            if (term.getPower(var) == 0)
               continue outer;
         varsInAll.add(var);
      }

      // factor out the highest power of variables present in all terms
      Map<Character, Integer> varToPow = new HashMap<>();
      for(char var : varsInAll) {
         // find the smallest power of the variable
         int minPower = this.terms.get(0).getPower(var);
         for(int i = 1; i < this.terms.size(); i++)
            minPower = Math.min(minPower, this.terms.get(i).getPower(var));

         // divide all terms by the variable to its smallest power present in the expression
         for(Term term : this.terms) {
            // as minPower is the minimum, newPow >= 0
            int newPow = term.getPower(var) - minPower;
            if(newPow > 0)
               term.getVariables().put(var, term.getPower(var) - minPower);
            else
               term.getVariables().remove(var);
         }
         varToPow.put(var, minPower);
      }

      return new Term(numGCD, varToPow);
   }

   /**
    *  Creates and returns a set containing all unique
    *  variables in the expression
    *  @return a set of characters, each representing a unique variable in the given expression
    */
   public Set<Character> getAllVars()
   {
      Set<Character> vars = new HashSet<>();
      for(Term term : this.terms)
         vars.addAll(term.getVariables().keySet());
      return vars;
   }

   // ---------------------------------------------------------------------------------------
   // To-string and equality methods

   /**
    *  Determines if equality between the expression and another object,
    *  returning true if both are identical expressions, ignoring order
    *  @return if the object is an identical expression, ignoring order
    */
   @Override
   public boolean equals(Object obj)
   {
      if(this == obj)
         return true;
      if(obj instanceof Expression otherExp) {
         Set<Term> termsNoOrder1 = new HashSet<>(this.terms);
         Set<Term> termsNoOrder2 = new HashSet<>(otherExp.terms);
         return termsNoOrder1.equals(termsNoOrder2);
      }
      return false;
   }

   /**
    * Hashes the given expression
    * @return a hash code for the given expression
    */
   @Override
   public int hashCode() {
      Set<Term> termsNoOrder = new HashSet<>(this.terms);
      return Objects.hash(termsNoOrder);
   }

   /**
    *  Returns a string representing the expression
    *  @return a string representation of the expression
    */
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      if(size() > 0)
      {
         builder.append(this.terms.get(0).frontStr());
         for(int i = 1; i < terms.size(); i++)
            builder.append(this.terms.get(i).middleStr());
      }
      return builder.toString();
   }
}