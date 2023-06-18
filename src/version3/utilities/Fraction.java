package version3.utilities;

import java.math.*;
import java.text.NumberFormat;
import java.util.*;

/**
 * A class based, immutable representation of a rational number, or fraction.
 * @author Nathan Harbison
 */
public class Fraction extends Number implements Comparable<Fraction> {
   /** The numerator and denominator of the fraction. */
   private BigInteger num, denom;
   /** A fractional representation of the number 1. */
   public static final Fraction ONE = new Fraction(1, 1);
   /** A fractional representation of the number -1. */
   public static final Fraction NEG_ONE = new Fraction(-1, 1);
   /** A fractional representation of the number 0. */
   public static final Fraction ZERO = new Fraction(0, 1);

   /**
    * Instantiates a fraction with an integer value given by number.
    * @param number the value of the fraction.
    */
   public Fraction(int number) {
      this(BigInteger.valueOf(number), BigInteger.ONE);
   }

   /**
    * Instantiates a fraction with an integer value given by number.
    * @param number the value of the fraction.
    */
   public Fraction(BigInteger number) {
      this(number, BigInteger.ONE);
   }

   /**
    * Instantiates a fraction with the given numerator and denominator.
    * @param num the numerator of the fraction.
    * @param denom the denominator of the fraction.
    */
   public Fraction(int num, int denom) {
      this(BigInteger.valueOf(num), BigInteger.valueOf(denom));
   }

   /**
    * Instantiates a fraction with the given numerator and denominator.
    * @param num the numerator of the fraction.
    * @param denom the denominator of the fraction.
    */
   public Fraction(BigInteger num, BigInteger denom) {
      this.num = num;
      this.denom = denom;
      simplify();
   }

   /**
    * Instantiates a fraction given an appropriately formatted string
    * representation of one, of the form "<numerator>/<denominator>" or
    * "<number>" for an integer.
    * @param frac the string representation of the fraction.
    * @throws NumberFormatException if the string is formatted incorrectly.
    */
   public Fraction(String frac) throws NumberFormatException {
      try {
         if (frac.contains("/")) {
            int slashInd = frac.indexOf("/");
            this.num = new BigInteger(frac.substring(0, slashInd));
            this.denom = new BigInteger(frac.substring(slashInd + 1));
         } else {
            this.num = new BigInteger(frac);
            this.denom = BigInteger.ONE;
         }
         simplify();
      } catch(NumberFormatException ex) {
         throw new NumberFormatException("Error: Incorrectly formatted fraction \"" + frac + "\"");
      }
   }

   /**
    * Returns the numerator of the fraction.
    * @return the numerator of the fraction.
    */
   public BigInteger getNum() {
      return this.num;
   }

   /**
    * Returns the denominator of the fraction.
    * @return the denominator of the fraction.
    */
   public BigInteger getDenom() {
      return this.denom;
   }

   // ------------------------------------------------------------------------------
   // Methods for operating on fractions

   /**
    * Computes the addition of this fraction and the given fraction and returns the sum
    * as a new fraction.
    * @param frac the addend of the operation.
    * @return a new fraction representing the sum.
    */
   public Fraction add(Fraction frac) {
      return new Fraction(this.num.multiply(frac.denom).add(frac.num.multiply(this.denom)), this.denom.multiply(frac.denom));
   }

   /**
    * Computes the subtraction of this fraction and the given fraction and returns the difference
    * as a new fraction.
    * @param frac the subtrahend of the operation.
    * @return a new fraction representing the difference.
    */
   public Fraction subtract(Fraction frac) {
      return new Fraction(this.num.multiply(frac.denom).subtract(frac.num.multiply(this.denom)), this.denom.multiply(frac.denom));
   }

   /**
    * Computes the multiplication of this fraction and the given fraction and returns the product
    * as a new fraction.
    * @param frac the multiplicand of the operation.
    * @return a new fraction representing the product.
    */
   public Fraction multiply(Fraction frac) {
      return new Fraction(this.num.multiply(frac.num), this.denom.multiply(frac.denom));
   }

   /**
    * Computes the multiplication of this fraction and the given integer and returns the product
    * as a new fraction.
    * @param number the integer multiplicand of the operation.
    * @return a new fraction representing the product.
    */
   public Fraction multiply(BigInteger number) {
      return new Fraction(this.num.multiply(number), this.denom);
   }

   /**
    * Computes the division of this fraction and the given fraction and returns the quotient
    * as a new fraction.
    * @param frac the divisor of the operation.
    * @return a new fraction representing the quotient.
    */
   public Fraction divide(Fraction frac) {
      return new Fraction(this.num.multiply(frac.denom), this.denom.multiply(frac.num));
   }

   /**
    * Computes the division of this fraction and the given integer and returns the quotient
    * as a new fraction.
    * @param number the integer divisor of the operation.
    * @return a new fraction representing the quotient.
    */
   public Fraction divide(BigInteger number) {
      return new Fraction(this.num, this.denom.multiply(number));
   }

   /**
    * Computes and returns the additive inverse of the given fraction.
    * @return a new fraction representing the additive inverse.
    */
   public Fraction inverse() {
      return new Fraction(this.num.multiply(new BigInteger("-1")), this.denom);
   }

   /**
    * Computes and returns the reciprocal of the given fraction.
    * @return a new fraction representing the reciprocal.
    */
   public Fraction reciprocal() {
      return new Fraction(this.denom, this.num);
   }

   /**
    * Computes and returns the fraction raised to the given power, as new fraction.
    * @param pow the power the fraction is to be raised to.
    * @return a new fraction representing the fraction raised to the given power.
    */
   public Fraction pow(int pow) {
      return new Fraction(this.num.pow(pow), this.denom.pow(pow));
   }

   /**
    * If the fraction is a perfect nth power (i.e. its numerator and denominator are perfect nth powers,
    * when simplified), then returns a new fraction representing the nth root of the fraction. Otherwise,
    * the return value is empty.
    * @return the nth root of the given fraction, if it is a perfect nth power.
    */
   public Optional<Fraction> nthRoot(int n)
   {
      if(isNthPower(n))
         return Optional.of(new Fraction(Functions.nthRoot(this.num, n), Functions.nthRoot(this.denom, n)));
      return Optional.empty();
   }

   /**
    * Determines if the fraction is a perfect nth power (i.e. its numerator and denominator are perfect nth powers,
    * when simplified).
    * @return whether the fraction is a perfect nth power.
    */
   public boolean isNthPower(int n) {
      return Functions.isNthPower(this.num.abs(), n) && Functions.isNthPower(this.denom, n);
   }

   /**
    * Computes and returns the absolute value of the given fraction as a new fraction.
    * @return the absolute value of the given fraction.
    */
   public Fraction abs() {
      return new Fraction(this.num.abs(), this.denom);
   }

   /**
    * Determines if the fraction is an integer (that it has a denominator of 1).
    * @return whether the fraction is an integer.
    */
   public boolean isWhole()
   {
      return this.denom.equals(BigInteger.ONE);
   }

   // ------------------------------------------------------------------------------
   // Static methods

   /**
    * Finds and returns the common denominator of a list of fractions.
    * @param fracs the list of fractions.
    * @return the common denominator of the fractions in the list.
    */
   public static BigInteger commonDenom(List<Fraction> fracs) {
      return Functions.lcm(fracs.stream().map(Fraction::getDenom).toList());
   }

   // ------------------------------------------------------------------------------
   // Methods for string conversion, primitive conversion, and equality

   /**
    * Compares the value of this fraction with the given fraction.
    * @param frac the fraction to be compared.
    * @return -1, 0, 1 if the given fraction is numerically less than, equal to, or greater than frac.
    */
   @Override
   public int compareTo(Fraction frac) {
      BigInteger diff = this.num.multiply(frac.denom).subtract(frac.num.multiply(this.denom));
      if(!diff.equals(BigInteger.ZERO))
         diff = diff.divide(diff.abs());
      return diff.intValue();
   }

   /**
    * Hashes the given fraction.
    * @return a hash code for the given fraction.
    */
   @Override
   public int hashCode() {
      return Objects.hash(this.num, this.denom);
   }

   /**
    * Determines equality between the fraction and another object,
    * returning true if both are identical fractions.
    * @return whether the object is an identical fraction.
    */
   @Override
   public boolean equals(Object obj) {
      if(this == obj)
         return true;
      if(obj instanceof Fraction frac) {
         return this.num.equals(frac.num) && this.denom.equals(frac.denom);
      }
      return false;
   }

   /**
    * Returns a string value of this fraction.
    * @return a string expression of this fraction.
    */
   @Override
   public String toString() {
      if(!this.denom.equals(BigInteger.ONE))
         return this.num.toString() + "/" + this.denom.toString();
      return this.num.toString();
   }

   /**
    * Returns the value of this fraction as an {@code int}, with truncation.
    * @return the numeric value represented by this fraction after conversion
    * to type {@code int}.
    */
   @Override
   public int intValue() {
      if(isWhole())
         return this.num.intValueExact();
      return (int) doubleValue();
   }

   /**
    * Returns the value of this fraction as a {@code long}, with truncation.
    * @return the numeric value represented by this fraction after conversion
    * to type {@code long}.
    */
   @Override
   public long longValue() {
      if(isWhole())
         return this.num.longValueExact();
      return (long) doubleValue();
   }

   /**
    * Returns the value of this fraction as a {@code float}.
    * @return the numeric value represented by this fraction after conversion
    * to type {@code float}.
    */
   @Override
   public float floatValue() {
      return new BigDecimal(this.num).divide(new BigDecimal(this.denom), RoundingMode.HALF_UP).floatValue();
   }

   /**
    * Returns the value of this fraction as a {@code double}.
    * @return the numeric value represented by this fraction after conversion
    * to type {@code double}.
    */
   @Override
   public double doubleValue() {
      return new BigDecimal(this.num).divide(new BigDecimal(this.denom), RoundingMode.HALF_UP).doubleValue();
   }

   // ------------------------------------------------------------------------------
   // Helper methods

   /**
    * Simplifies the fraction by dividing the numerator and denominator by their
    * greatest common divisor and making the denominator positive (so the sign of
    * the fraction is given by the sign of the numerator).
    */
   private void simplify() {
      BigInteger gcd = this.num.abs().gcd(this.denom.abs()); // make num and denom be coprime
      if(this.denom.compareTo(BigInteger.ZERO) < 0) // make denom is positive
         gcd = gcd.multiply(new BigInteger("-1"));
      this.num = this.num.divide(gcd);
      this.denom = this.denom.divide(gcd);
   }
}