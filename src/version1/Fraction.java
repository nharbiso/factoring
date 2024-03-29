package version1;

import java.math.*;
public class Fraction
{
   private BigInteger num, denom;
   private static Fraction[] nullArr = {new Fraction()};
   public static Fraction ONE = new Fraction(1, 1);
   public static Fraction NEGONE = new Fraction(-1, 1);
   public static Fraction ZERO = new Fraction(0, 1);
   public Fraction()
   {
      num = BigInteger.ZERO;
      denom = BigInteger.ZERO;
   }
   public Fraction(BigInteger x, BigInteger y)
   {
      BigInteger gcd = Functions.gcd(x.abs(), y.abs());
      if(y.compareTo(BigInteger.ZERO) < 0)
         gcd = gcd.multiply(new BigInteger("-1"));
      num = x.divide(gcd);
      denom = y.divide(gcd);
   }
   public Fraction(int xx, int yy)
   {
      this(new BigInteger(""+xx), new BigInteger(""+yy));
   }
   public Fraction(BigInteger[] x)
   {
      this(x[0], x[1]);
   }
   public Fraction(String str) throws NumberFormatException
   {
      BigInteger x, y;
      if(str.contains("/"))
      {
         x = new BigInteger(str.substring(0, str.indexOf("/")));
         y = new BigInteger(str.substring(str.indexOf("/") + 1, str.length()));
      }
      else
      {
         x = new BigInteger(str);
         y = BigInteger.ONE;
      }
      BigInteger gcd = Functions.gcd(x.abs(), y.abs());
      num = x.divide(gcd);
      denom = y.divide(gcd);
   }
   public Fraction(BigInteger x, Fraction f)
   {
      this(x.multiply(f.denom), f.num);
   }
   public Fraction(Fraction f, BigInteger x)
   {
      this(f.num, x.multiply(f.denom));
   }
   public Fraction(Fraction f1, Fraction f2)
   {
      this(f1.num.multiply(f2.denom), f2.num.multiply(f1.denom));
   }
   public BigInteger getNum()
   {
      return num;
   }
   public BigInteger getDenom()
   {
      return denom;
   }
   public void setNum(BigInteger x)
   {
      num = x;
   }
   public void setDenom(BigInteger x)
   {
      denom = x;
   }
   public String toString()
   {
      if(denom.compareTo(BigInteger.ONE) != 0)
         return num.toString()+"/"+denom.toString();
      return num.toString();
   }
   public double toDecimal()
   {
      return new BigDecimal(num).divide(new BigDecimal(denom), 100, RoundingMode.HALF_UP).doubleValue();
   }
   
   public Fraction add(Fraction f)
   {
      BigInteger newDenom = denom.multiply(f.denom).divide(Functions.gcd(denom, f.denom));
      return new Fraction(num.multiply(newDenom.divide(denom)).add(f.num.multiply(newDenom.divide(f.denom))), newDenom);
   }
   public Fraction subtract(Fraction f)
   {
      return add(f.inverse());
   }
   public Fraction multiply(Fraction f)
   {
      return new Fraction(num.multiply(f.num), denom.multiply(f.denom));
   }
   public Fraction divide(Fraction f)
   {
      return multiply(f.reciprocal());
   }
   public Fraction inverse()
   {
      return new Fraction(num.multiply(new BigInteger("-1")), denom);
   }
   public Fraction reciprocal()
   {
      return new Fraction(denom, num);
   }
   public Fraction pow(int pow)
   {
      return new Fraction(num.pow(pow), denom.pow(pow));
   } 
   public Fraction sqrt()
   {
      if(isSq())
         return new Fraction(Functions.sqrt(num), Functions.sqrt(denom));
      return new Fraction();
   }
   public Fraction cbrt()
   {
      if(isCb())
         return new Fraction(Functions.cbrt(num), Functions.cbrt(denom));
      return new Fraction();
   }
   public Fraction abs()
   {
      return new Fraction(num.abs(), denom.abs());
   }
   public boolean isSq()
   {
      return Functions.isSq(num.abs()) && Functions.isSq(denom);
   }
   public boolean isCb()
   {
      return Functions.isCb(num.abs()) && Functions.isCb(denom);
   }
   public boolean equals(Fraction f)
   {
      return num.compareTo(f.num) == 0 && denom.compareTo(f.denom) == 0;
   }
   public int compareTo(Fraction f)
   {
      double d = subtract(f).toDecimal();
      if(d < 0)
         return -1;
      if(d > 0)
         return 1;
      return 0;
   }
   
   public Fraction[] findFactors()
   {
      return toArray(Functions.findFactors(num.abs()), Functions.findFactors(denom));
   }
   public Fraction[] findSqFactors()
   {
      if(!isSq())
         return nullArr;
      return toArray(square(Functions.findFactors(Functions.sqrt(num.abs()))), square(Functions.findFactors(Functions.sqrt(denom))));
   }
   public boolean isFactor(Fraction f)
   {
      if(f.compareTo(Fraction.ONE) == 0)
         return true;
      return divisible(num, f.num) || divisible(denom, f.denom);
   }
   
   
   private boolean divisible(BigInteger b, BigInteger c)
   {
      return new BigDecimal(b).divide(new BigDecimal(c), 2, RoundingMode.HALF_UP).stripTrailingZeros().scale() == 0;
   }
   private BigInteger[] square(BigInteger[] b)
   {
      for(int x = 0; x < b.length; x++)
         b[x] = b[x].pow(2);
      return b;
   }
   private Fraction[] toArray(BigInteger[] x, BigInteger[] y)
   {
      Fraction[] f = new Fraction[x.length * y.length];
      int length = 0;
      for(int s = 0; s < x.length; s++)
         for(int u = 0; u < y.length; u++)
         {
            Fraction tmp = new Fraction(x[s], y[u]);
            if(notPresent(f, tmp, length))
            {
               f[length] = tmp;
               length++;
            }            
         }
      for(int j = 0; j < length - 1; j++)
      {
         int maxPos = 0;
         for(int i = 0; i < length - j; i++)
            if(f[i].compareTo(f[maxPos]) > 0)
               maxPos = i;
         swap(f, maxPos, length - j - 1);
      }
      Fraction[] fr = new Fraction[length];
      for(int m = 0; m < fr.length; m++)
         fr[m] = f[m];
      return fr;
   }
   private void swap(Fraction[] f, int a, int b)
   {
      Fraction tmp = f[a];
      f[a] = f[b];
      f[b] = tmp;
   }
   private boolean notPresent(Fraction[] f, Fraction d, int length)
   {
      for(int m = 0; m < length; m++)
         if(f[m].equals(d))
            return false;
      return true;
   }
}