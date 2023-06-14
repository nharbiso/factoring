package version3.utilities;

import java.util.*;
import java.math.*;

/**
 * Class containing important helper static methods utilized by the factoring program.
 * @author Nathan Harbison
 */
public class Functions
{
   /**
    * Determines (approximate) nth root of the given number via Newton's method.
    * @param number the number to be operated on.
    * @param n the root of the operation.
    * @return the nth root of the given number.
    */
   public static BigInteger nthRoot(BigInteger number, int n) {
      if (n < 1 || (number.compareTo(BigInteger.ZERO) < 0 && n % 2 == 0)) {
         throw new IllegalArgumentException("Error: root is less than 1 or taking even root of negative number");
      }

      // Make an initial guess, by shifting a bit to the position of floor(log2(number)/n)
      // as root = 2^(log2(number)/n)
      BigInteger guess = BigInteger.ONE;
      BigInteger iter = number;
      while(iter.compareTo(BigInteger.ZERO) > 0) {
         guess = guess.shiftLeft(1);
         iter = iter.shiftRight(n);
      }

      while (true) {
         // new_guess = (guess * (n-1) + number / guess^(n-1)) / n
         BigInteger new_guess = guess.multiply(BigInteger.valueOf(n - 1)).add(number.divide(guess.pow(n - 1))).divide(BigInteger.valueOf(n));
         BigInteger diff = new_guess.subtract(guess).abs();
         if (diff.compareTo(BigInteger.ONE) <= 0) { // converged
            return guess.min(new_guess);
         }
         guess = new_guess;
      }
   }

   /**
    * Determines if the given number is a perfect nth power.
    * @param number the number to test.
    * @param n the exponent to test as the power.
    * @return whether the given number is a perfect nth power.
    */
   public static boolean isNthPower(BigInteger number, int n) {
      return nthRoot(number, n).pow(n).equals(number);
   }

   /**
    *  Determines if an integer is a perfect square.
    *  @param num the integer to be processed.
    *  @return whether the given number is a perfect square.
    */
   public static boolean isPerfectSq(int num) {
      return (double) num == Math.pow((int)Math.sqrt(num), 2);
   }

   /**
    *  Determines if an integer is a perfect cube.
    *  @param num the integer to be processed.
    *  @return whether the given number is a perfect cube.
    */
   public static boolean isPerfectCube(int num) {
      return (double) num == Math.pow((int)Math.cbrt(num), 3);
   }

   /**
    * Determines if an integer is a power of 2, i.e.
    * can be written as 2^n for some integer n.
    * @param num the integer to be processed.
    * @return whether the given number is a power of 2.
    */
   public static boolean isPowerOf2(int num) {
      return (num != 0) && ((num & (num-1)) == 0);
   }

   /**
    * Finds and returns a list of all factors of the given integer,
    * in increasing order.
    * @param num the integer to be factored.
    * @return all factors of the given integer.
    */
   public static List<BigInteger> findFactors(BigInteger num) {
      List<BigInteger> factors = new ArrayList<>();
      for(BigInteger i = BigInteger.ONE; i.compareTo(nthRoot(num, 2)) <= 0; i = i.add(BigInteger.ONE)) {
         if(num.remainder(i).compareTo(BigInteger.ZERO) == 0) { // i divides num
            factors.add(i);
            factors.add(num.divide(i));
         }
      }

      Collections.sort(factors);
      return factors;
   }

   /**
    * Finds and returns a list of all factors of the given integer,
    * in increasing order.
    * @param num the integer to be factored.
    * @return all factors of the given integer.
    */
   public static List<Integer> findFactors(int num) {
      List<Integer> factors = new ArrayList<>();
      for(int i = 1; i <= Math.sqrt(num); i++) {
         if(num % i == 0) {
            factors.add(num / i);
            factors.add(i);
         }
      }

      Collections.sort(factors);
      return factors;
   }

   /**
    * Finds the (positive) gcd of the two given numbers via the Euclidean algorithm.
    * @param x one of the numbers whose gcd is to be computed.
    * @param y one of the numbers whose gcd is to be computed.
    * @return the gcd of the two given integers.
    */
   public static BigInteger gcd(BigInteger x, BigInteger y) {
      x = x.abs();
      y = y.abs();

      if(x.compareTo(BigInteger.ZERO) == 0)
         return y;
      if(y.compareTo(BigInteger.ZERO) == 0)
         return x;
      if(x.compareTo(y) > 0)
         return gcd(y, x.remainder(y));
      return gcd(x, y.remainder(x));
   }

   /**
    * Finds the (positive) gcd of the two given numbers via the Euclidean algorithm.
    * @param x one of the numbers whose gcd is to be computed.
    * @param y one of the numbers whose gcd is to be computed.
    * @return the gcd of the two given integers.
    */
   public static int gcd(int x, int y) {
      x = Math.abs(x);
      y = Math.abs(y);

      if(x == 0)
         return y;
      if(y == 0)
         return x;
      if(x > y)
         return gcd(y, x % y);
      return gcd(x, y % x);
   }

   /**
    * Finds the (positive) gcd of all numbers within the given list via the Euclidean algorithm.
    * @param nums the list of numbers whose gcd is to be computed.
    * @return the gcd of the given list of integers.
    */
   public static int gcd(List<Integer> nums) {
      if(nums.size() == 0)
         throw new IllegalArgumentException("Error: cannot find the gcd of an empty list.");
      int gcd = 0;
      for(int num : nums) {
         gcd = gcd(num, gcd);
      }
      return gcd;
   }

   /**
    * Finds the (positive) lcm of the two given numbers.
    * @param x one of the numbers whose lcm is to be computed.
    * @param y one of the numbers whose lcm is to be computed.
    * @return the lcm of the two given integers.
    */
   public static BigInteger lcm(BigInteger x, BigInteger y) {
      return x.multiply(y).divide(gcd(x, y));
   }

   /**
    * Finds the (positive) lcm of the two given numbers.
    * @param x one of the numbers whose lcm is to be computed.
    * @param y one of the numbers whose lcm is to be computed.
    * @return the lcm of the two given integers.
    */
   public static int lcm(int x, int y) {
      return x * y / gcd(x, y);
   }

   /**
    * Finds the (positive) lcm of all numbers within the given list.
    * @param nums the list of numbers whose lcm is to be computed.
    * @return the lcm of the given list of integers.
    */
   public static BigInteger lcm(List<BigInteger> nums) {
      if(nums.size() == 0)
         throw new IllegalArgumentException("Error: cannot find the gcd of an empty list.");
      BigInteger lcm = BigInteger.ONE;
      for(BigInteger num : nums) {
         lcm = lcm(num, lcm);
      }
      return lcm;
   }

   /**
    * Obtains a random item from the given non-empty set.
    * @param set the set to be processed.
    * @return a random item from the set.
    * @param <T> the class of the items within the given set.
    */
   public static <T> T getItemFromSet(Set<T> set) {
      Iterator<T> iter = set.iterator();
      if(!iter.hasNext())
         throw new IllegalArgumentException("Error: cannot get an item from an empty set.");
      return iter.next();
   }

   /**
    * Concatenates the two lists into a new list.
    * @param list1 the list to be concatenated at the front of the new list.
    * @param list2 the list to be concatenated at the end of the new list.
    * @return the concatenation of the two lists.
    * @param <T> the class of items within the given lists.
    */
   public static <T> List<T> concatLists(List<T> list1, List<T> list2) {
      List<T> list = new ArrayList<>(list1);
      list.addAll(list2);
      return list;
   }
}