package version3.utilities;

import java.util.*;
import java.math.*;

/**
 * Class containing important helper static methods utilized by the factoring program.
 * @author Nathan Harbison
 */
public class Functions {
   //-------------------------------------------------------------------------------------------------
   // Numeric operations (roots, finding factors, gcd/lcm)

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
    * Determines if an integer is a power of 2, i.e.
    * can be written as 2^n for some integer n.
    * @param num the integer to be processed.
    * @return whether the given number is a power of 2.
    */
   public static boolean isPowerOf2(int num) {
      return (num != 0) && ((num & (num-1)) == 0);
   }

   /**
    * Finds and returns a list of all factors of the given integer.
    * @param num the integer to be factored.
    * @return all factors of the given integer.
    */
   public static List<BigInteger> findFactors(BigInteger num) {
      List<BigInteger> factors = new ArrayList<>();
      BigInteger sqrt = nthRoot(num, 2);
      for(BigInteger i = BigInteger.ONE; i.compareTo(sqrt) <= 0; i = i.add(BigInteger.ONE)) {
         if(num.remainder(i).equals(BigInteger.ZERO)) { // i divides num
            factors.add(i);
            factors.add(num.divide(i));
         }
      }
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
    * @param m one of the numbers whose gcd is to be computed.
    * @param n one of the numbers whose gcd is to be computed.
    * @return the gcd of the two given integers.
    */
   public static BigInteger gcd(BigInteger m, BigInteger n) {
      m = m.abs();
      n = n.abs();

      if(m.equals(BigInteger.ZERO))
         return n;
      if(n.equals(BigInteger.ZERO))
         return m;
      if(m.compareTo(n) > 0)
         return gcd(n, m.remainder(n));
      return gcd(m, n.remainder(m));
   }

   /**
    * Finds the (positive) gcd of all numbers within the given list via the Euclidean algorithm.
    * @param nums the list of numbers whose gcd is to be computed.
    * @return the gcd of the given list of integers.
    */
   public static BigInteger gcd(List<BigInteger> nums) {
      if(nums.size() == 0)
         throw new IllegalArgumentException("Error: cannot find the gcd of an empty list.");
      BigInteger gcd = BigInteger.ZERO;
      for(BigInteger num : nums) {
         gcd = gcd(num, gcd);
      }
      return gcd;
   }

   /**
    * Finds the (positive) lcm of the two given numbers.
    * @param m one of the numbers whose lcm is to be computed.
    * @param n one of the numbers whose lcm is to be computed.
    * @return the lcm of the two given integers.
    */
   public static BigInteger lcm(BigInteger m, BigInteger n) {
      return m.multiply(n).divide(gcd(m, n));
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

   //-------------------------------------------------------------------------------------------------
   // Expression operations/parsing

   /**
    * Finds and returns the variable portion (or lack thereof) in a given term.
    * @param term the term to be analyzed.
    * @return string representing variable and its power or "" if none is found.
    */
   public static String getVariable(String term) {
      int loc = 0;
      try {
         if(term.charAt(loc) == '+' || term.charAt(loc) == '-') // ignore sign of coefficient
            loc++;
         while(Character.isDigit(term.charAt(loc)) || term.charAt(loc) == '/') // ignore (fractional) coefficient
            loc++;
      } catch(StringIndexOutOfBoundsException e) {
         return "";
      }
      return term.substring(loc);
   }

   /**
    * Finds, parses, and returns the coefficient in a given term.
    * @param term the term to be analyzed.
    * @param vars the variable(s) contained in the term.
    * @return the coefficient of the term.
    */
   public static Fraction parseCoefficient(String term, String vars) {
      String coefStr = term;
      if(!vars.equals(""))
         coefStr = coefStr.substring(0, coefStr.indexOf(vars));

      if(coefStr.equals("") || coefStr.equals("+"))
         return Fraction.ONE;
      if(coefStr.equals("-"))
         return Fraction.NEG_ONE;
      return new Fraction(coefStr);
   }

   /**
    * Determines if an expression composed of 3 terms can be factored
    * similarly to the method utilized for a quadratic polynomial.
    * @param exp the expression to be tested.
    * @return whether the expression can be factored like a quadratic.
    */
   public static boolean canBeQuadFactored(Expression exp) {
      Set<Character> allVars = exp.getAllVars();
      // assuming that expression is 3 terms long

      // test for all permutations of the terms
      int[][] perms = Functions.findPerms(3);
      for(int[] perm : perms) {
         boolean factorable = true;
         for (char var : allVars) {
            // to be factored like a quadratic, for each variable:
            //  - the variable's power in the middle term must be half of that in the
            //    first term or its power in the first term must be zero.
            if (exp.getPower(perm[0], var) != 0
                    && (double) exp.getPower(perm[0], var) / 2 != exp.getPower(perm[1], var)) {
               factorable = false;
               break;
            }
            //  - the variable's power in the middle term must be half of that in the
            //    last term, if both powers are non-zero, or its power in the last
            //    term must be zero.
            if (exp.getPower(perm[2], var) != 0
                    && (double) exp.getPower(perm[2], var) / 2 != exp.getPower(perm[1], var)) {
               factorable = false;
               break;
            }
         }

         if(factorable) {
            // rearrange terms according to this permutation
            List<Term> terms = new ArrayList<>();
            while (exp.size() != 0) {
               terms.add(exp.removeTerm(0));
            }
            for (int ind : perm) {
               exp.addTerm(terms.get(ind));
            }
            return true;
         }
      }
      return false;
   }

   //-------------------------------------------------------------------------------------------------
   // Other helper functions

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
    * Finds and returns an array of all permutations of the array
    * {0, 1, ..., n-1}.
    * @param n the length of the array to be permuted.
    * @return an array of all permutations of {0, 1, ..., n-1}.
    */
   public static int[][] findPerms(int n) {
      List<int[]> perms = new ArrayList<>();
      int[] arr = new int[n];
      for(int i = 0; i < n; i++)
         arr[i] = i;

      findPermsHelper(arr, perms, 0);
      return perms.toArray(new int[0][]);
   }

   /**
    * Helper to find all permutation of an array by finding all
    * permutations of arr with the indices 0, ..., start - 1 being
    * fixed. (Essentially finds all permutations of the last size - start
    * entries of the array, where size is the size of the array.)
    * @param arr the array to be permuted.
    * @param perms the list of all current permutations of arr.
    * @param start the starting index for finding permutations.
    */
   private static void findPermsHelper(int[] arr, List<int[]> perms, int start) {
      if(start == arr.length - 1) {
         perms.add(arr.clone());
         return;
      }

      for(int i = start; i < arr.length; i++) {
         swap(arr, start, i);
         findPermsHelper(arr, perms, start + 1);
         swap(arr, start, i);
      }
   }

   /**
    * Swaps the elements at the two given indices in the integer array.
    * @param arr the array with elements to be swapped.
    * @param i the index of the first element to be swapped.
    * @param j the index of the second element to be swapped.
    */
   private static void swap(int[] arr, int i, int j) {
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
   }
}