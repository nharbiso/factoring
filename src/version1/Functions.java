package version1;

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
   public static BigInteger gcd(BigInteger x, BigInteger y)
   {
      if(x.compareTo(BigInteger.ZERO) == 0)
         return y;
      if(y.compareTo(BigInteger.ZERO) == 0)
         return x;
      if(x.compareTo(y) > 0)
         return gcd(y, x.remainder(y));
      return gcd(x, y.remainder(x));
   }
}