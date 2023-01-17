package version1;

import java.math.BigInteger;
public class FactoringQuartics
{
   public static String factor(Fraction[] c, String factor, String var, boolean printNotFactorable) 
   {
      int power = 4;
      if(c.length != 5)
      {
         System.out.println("An error occurred");
         System.exit(0);
      }
      
      Fraction[] co = new Fraction[c.length];
      for(int x = 0; x < c.length; x++)
         co[x] = new Fraction(c[x], c[0]);  
      
      boolean wasReduced = false;
      Fraction rco[] = new Fraction[4];
      rco[0] = co[0];
      if(co.length == 5 && co[1].getNum().compareTo(BigInteger.ZERO) != 0)
      {
         rco[1] = co[2].subtract(co[1].pow(2).multiply(new Fraction("3/8")));
         rco[2] = co[3].add(co[1].pow(3).multiply(new Fraction("1/8"))).subtract(co[1].multiply(co[2]).multiply(new Fraction("1/2")));
         rco[3] = co[4].subtract(co[1].pow(4).multiply(new Fraction("3/256"))).add(co[1].pow(2).multiply(co[2]).multiply(new Fraction("1/16"))).subtract(co[1].multiply(co[3]).multiply(new Fraction("1/4")));
         wasReduced = true;
      }
      else if(co[1].getNum().compareTo(BigInteger.ZERO) == 0)
         System.arraycopy(co, 2, rco, 1, 3);
      
      Fraction[] resolvent = {new Fraction(1, 1), rco[1].multiply(new Fraction("2/1")), rco[1].pow(2).subtract(rco[3].multiply(new Fraction("4/1"))), rco[2].pow(2).inverse()};
      Fraction sqFactor = findResolvSqFactor(resolvent);
      if(!sqFactor.equals(new Fraction()))
         return factor+findQuadFactors(sqFactor, co, rco, resolvent, wasReduced, var);
      
      String s = Factoring.factorString(c[0], false)+Factoring.getOneVariable(var, power);
      if(!printNotFactorable || !factor.equals(""))
         s = factor+"("+s;
      else
         s = factor+s;
      for(int x = power - 1; x >= 0; x--)
         if(c[power - x].compareTo(Fraction.ZERO) != 0 && x != 0)
            s += Factoring.midString(c[power - x], var, x, true);
         else if(x == 0)
            s += Factoring.constantString(c[power-x], true);
      if(!printNotFactorable || !factor.equals(""))
         s += ")";
      return s;
   }
   private static Fraction findResolvSqFactor(Fraction[] resolvent)
   {
      Fraction[] f = resolvent[3].findSqFactors();
      for(int x = 0; x < f.length; x++)
      {
         Fraction sum = f[x].pow(3).add(f[x].pow(2).multiply(resolvent[1])).add(f[x].multiply(resolvent[2])).add(resolvent[3]);
         if(sum.getNum().compareTo(BigInteger.ZERO) == 0)
            return f[x];
      }
      return new Fraction();
   }
   
   //after finding the perfect square root of the resolvent
   private static String findQuadFactors(Fraction x, Fraction[] co, Fraction[] rco, Fraction[] resolv, boolean wasReduced, String var)
   {
      Fraction h = x.sqrt();
      Fraction k, kpr;
      if(h.getNum().compareTo(BigInteger.ZERO) != 0)
      {
         k = new Fraction(BigInteger.ONE, h.multiply(new Fraction(2, 1))).multiply(h.pow(3).add(h.multiply(rco[1])).subtract(rco[2]));
         kpr = new Fraction(BigInteger.ONE, h.multiply(new Fraction(2, 1))).multiply(h.pow(3).add(h.multiply(rco[1])).add(rco[2]));
      }
      else
      {
         k = rco[1].add(resolv[2].sqrt()).multiply(new Fraction("1/2"));
         kpr = rco[1].subtract(resolv[2].sqrt()).multiply(new Fraction("1/2"));
      }
      Fraction[] factor1 = {new Fraction(1, 1), h, k};
      Fraction[] factor2 = {new Fraction(1, 1), h.inverse(), kpr};
      if(wasReduced)
      {
         factor1 = transformBack(factor1, co);
         factor2 = transformBack(factor2, co);
      }
      return printQuad(factor1[0], factor1[1], factor1[2], var)+printQuad(factor2[0], factor2[1], factor2[2], var);
   }
   private static Fraction[] transformBack(Fraction[] quad, Fraction[] co)
   {
      Fraction b = co[1];
      Fraction[] y = new Fraction[quad.length];
      y[0] = quad[0];
      y[1] = b.multiply(new Fraction("1/2")).add(quad[1]);
      y[2] = b.pow(2).multiply(new Fraction("1/16")).add(b.multiply(quad[1]).multiply(new Fraction("1/4"))).add(quad[2]);
      
      BigInteger denom = lcm(y[1].getDenom(), y[2].getDenom());
      for(int z = 0; z < y.length; z++)
         y[z] = y[z].multiply(new Fraction(denom, BigInteger.ONE));
      return y;
   }
   private static BigInteger lcm(BigInteger b1, BigInteger b2)
   {
      return b1.multiply(b2).divide(Functions.gcd(b1, b2));
   }
   public static String printQuad(Fraction a, Fraction b, Fraction c, String var)
   {
      return "("+Factoring.factorString(a, false)+Factoring.getOneVariable(var, 2)+Factoring.midString(b, var, 1, true)+Factoring.constantString(c, true)+")";
   }  
   public static void print(Fraction[] f)
   {
      for(int x = 0; x < f.length; x++)
         System.out.print(f[x].toString()+" ");
      System.out.println();
   }
}