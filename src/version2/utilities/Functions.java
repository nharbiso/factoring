package version2.utilities;

import java.util.*;
import java.math.*;

public class Functions
{
   public static boolean isSq(BigInteger b)
   {
      BigInteger sqrt = sqrt(b);
      return b.equals(sqrt.multiply(sqrt));
   }
   public static BigInteger sqrt(BigInteger A) 
   {
      BigInteger a = BigInteger.ONE, b = A.shiftRight(5).add(BigInteger.valueOf(8));
      while((b.compareTo(a)) >= 0)
      {
         BigInteger mid = a.add(b).shiftRight(1);
         if(mid.multiply(mid).compareTo(A)>0)
            b = mid.subtract(BigInteger.ONE);
         else
            a = mid.add(BigInteger.ONE);
      }
      return a.subtract(BigInteger.ONE);
   }
   public static boolean isCb(BigInteger b)
   {
      BigInteger cbrt = rt(3, b);
      return b.equals(cbrt.pow(3));
   }
   public static BigInteger cbrt(BigInteger b)
   {
      BigInteger x = rt(3, b);
      return rt(3, b);
   }
   public static BigInteger rt(int n, BigInteger b)
   {
      BigInteger k = BigInteger.ZERO;
      if(b.compareTo(BigInteger.ZERO) > 0)
         while(k.pow(n).compareTo(b) < 0)
            k = k.add(BigInteger.ONE);
      else if(b.compareTo(BigInteger.ZERO) < 0)
         while(k.pow(n).compareTo(b) > 0)
            k = k.subtract(BigInteger.ONE);
      return k;
   }
   public static BigInteger[] findFactors(BigInteger d)
   {
      BigInteger[] f = new BigInteger[100000];
      f[0] = BigInteger.ONE;
      f[1] = d;
      int count = 2;
      for(BigInteger i = new BigInteger("2"); i.compareTo(sqrt(d)) <= 0; i = i.add(BigInteger.ONE))
      {
         if(d.remainder(i).compareTo(BigInteger.ZERO) == 0)
         {
            f[count] = d.divide(i);
            f[count + 1] = d.divide(f[count]);
            count += 2;
         }
      }
      BigInteger[] factors = new BigInteger[count];
      for(int x = 0; x < count; x++)
         factors[x] = f[x];
      return factors;
   }
   public static List<Integer> findFactors(int n)
   {
      List<Integer> factors = new ArrayList<Integer>();
      factors.add(1);
      factors.add(n);
      for(int i = 2; i <= Math.sqrt(n); i++)
      {
         if(n % i == 0)
         {
            factors.add(n / i);
            factors.add(i);
         }
      }
      return factors;
   }

   public static int gcd(int x, int y)
   {
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

   public static int gcd(List<Integer> nums) {
      if(nums.size() == 0)
         throw new IllegalArgumentException("Error: cannot find the gcd of an empty list.");
      int gcd = 0;
      for(int num : nums) {
         gcd = gcd(num, gcd);
      }
      return gcd;
   }

   public static int lcm(int x, int y)
   {
      return x * y / gcd(x, y);
   }

   /**
   *  Determines if an integer is a perfect square
   *  @param x the integer to be processed
   *  @return whether or not the given number is a perfect square
   */
   public static boolean isPerfectSq(int x)
   {
      double input = (double) x;
      return input == Math.pow((int)Math.sqrt(input), 2);
   }

   /**
   *  Determines if an integer is a perfect cube
   *  @param x the integer to be processed
   *  @return whether or not the given number is a perfect cube
   */
   public static boolean isPerfectCube(int x)
   {
      double input = (double) x;
      return input == Math.pow((int)Math.cbrt(input), 3);
   }

   /**
   * Determines if an integer is a power of 2, or
   * can be written as 2^n for some integer n
   * @param x the integer to be processed
   * @return whether or not the given number is a power of 2
   */
   public static boolean isPowerOf2(int x)
   {
      return (x != 0) && ((x & (x-1)) == 0);
   }
}