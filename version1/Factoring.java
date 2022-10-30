import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.util.Arrays;

/**
* Factors a given expression
* @author Nathan Harbison
*/

public class Factoring
{
   public static final String[] NULLSTR = {""};
   private static final String WITH_DELIMITER = "(?=%1$s)";
   private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
   private static final String numbers = "1234567890";
   
   private static int[][] pow;
   private static String[] fullArr;
   private static String fracLCM = "";
   public static void main(String[] args)
   {
      while(true)
      {
         String quad = JOptionPane.showInputDialog("Enter the expression you would like to factor. (-1 to quit)");
         try {
            if(quad.equals("-1"))
               throw new NullPointerException();
         } catch(NullPointerException e) {
           break;
         }
         try {
            factor(quad);
         } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Could not interpret the given expression.");
         }
      }
   }
   public static void factor(String quad) throws Exception
   {
      String[] s = quad.split(String.format(WITH_DELIMITER, "\\+|-"));
      quad = quad.replaceAll("\\+", " + ").replaceAll("-", " - ");
      
      String[] variables = new String[s.length];
      for(int x = 0; x < variables.length; x++)
         variables[x] = getVariable(s[x]);
      
      Fraction[] coefficients = new Fraction[s.length];
      for(int x = 0; x < coefficients.length; x++)
         coefficients[x] = coefficients(s[x], variables[x]);
      
      String factor = getFactor(coefficients, variables);
      System.out.println(Arrays.toString(fullArr));
      
      String print = quad;
      //based on what the variables are, determine what should be used for factoring
      if(variables.length == 3)
      {
         if(canBeQuadFactored())
            print = FactoringQuadratics.factor(coefficients, fullArr, pow, factor, true);
         else if(fullArr.length == 1)
         {
            coefficients = addZeroes(coefficients);
            print = FactoringPolynomials.factor(coefficients, factor, pow[0][0], fullArr[0], true);
         }
         else
            print = toString(factor, coefficients);
      }
      else if(variables.length == 2)
         print = FactoringBinomials.factor(coefficients, fullArr, pow, factor, true);
      else if(fullArr.length == 1)
      {
         coefficients = addZeroes(coefficients);
         print = FactoringPolynomials.factor(coefficients, factor, pow[0][0], fullArr[0], true);
      }
      else if(variables.length == 4)
         print = FactoringByGrouping.factor(coefficients, fullArr, pow, factor);
      else
         print = toString(factor, coefficients);
      
      if(print.equals("Could not be factored.") || print.equals(quad))
         JOptionPane.showMessageDialog(null, "The polynomial "+Scripts.superscript(quad)+" is not factorable.");
      else
      {
         if(!fracLCM.equals(""))
            quad = fracLCM+"("+quad+")";
         s = print.split(String.format(WITH_DELIMITER, "\\("));
         for(int x = 0; x < s.length; x++)
         {
            int count = 1;
            for(int y = x + 1; y < s.length; y++)
               if(s[x].equals(s[y]))
                  count++;
            if(count > 1)
            {
               print = print.replace(s[x], "");
               if(print.contains("-") || print.contains("\\+"))
                  print = s[x]+"^"+count+print;
               else
                  print += s[x]+"^"+count;
               s = print.split(String.format(WITH_DELIMITER, "\\("));
            }
         }
         System.out.println(print);
         JOptionPane.showMessageDialog(null, Scripts.superscript(quad)+" factored is:\n"+Scripts.superscript(print));
      }
   }
   private static String getVariable(String s)
   {
      int loc = 0;
      try {
         if(s.charAt(loc) == '+' || s.charAt(loc) == '-')
            loc++;
         while(indexIsNum(s, loc) || s.charAt(loc) == '/')
            loc++;
      } catch(StringIndexOutOfBoundsException e) {
         return "";
      }
      if(loc == s.length())
         return "";
      return s.substring(loc, s.length());
   }
   private static Fraction coefficients(String s, String var)
   {
      if(!var.equals(""))
         s = s.substring(0, s.indexOf(var)).replaceAll("\\+", "");
      if(s.equals(""))
         return Fraction.ONE;
      if(s.equals("-"))
         return Fraction.NEGONE;
      return new Fraction(s);
   }
   private static String getFactor(Fraction[] num, String[] varPow) throws Exception
   {      
      String numFac = factorString(getNumFactor(num), false);
      String[][] varNoPow = createVarNoPow(varPow);
      String[] varInAll = findCommonVar(varNoPow);
      
      if(varInAll.length == 0)
         return numFac;
      
      String fac = "";
      for(int x = 0; x < varInAll.length; x++)
      {
         int i = 0;
         for(int y = 0; y < fullArr.length; y++)
            if(varInAll[x].equals(fullArr[y]))
            {
               i = y;
               break;
            }
         
         int lowPow = pow[0][i];
         for(int y = 1; y < pow.length; y++)
            if(pow[y][i] < lowPow)
               lowPow = pow[y][i];
         for(int y = 0; y < pow.length; y++)
            pow[y][i] -= lowPow;
         fac += getOneVariable(fullArr[i], lowPow);
      }
      
      return numFac+fac;
   }
   private static String[][] createVarNoPow(String[] s) throws Exception
   {
      String[][] k = new String[s.length][10];
      int[][] p = new int[s.length][10];
      for(int x = 0; x < k.length; x++)
      {
         int l = 0;
         for(int y = 0; y < s[x].length(); y++)
         {
            try {
               if(s[x].charAt(y+1) == '^')
               {
                  k[x][l] = s[x].substring(y, y + 1);
                  int i = y + 3;
                  while(!indexIsLetter(s[x], i) && indexExists(s[x], i))
                     i++;
                  p[x][l] = Integer.parseInt(s[x].substring(y + 2, i));
                  y = i - 1;
               }
               else
               {
                  k[x][l] = s[x].substring(y, y + 1);
                  p[x][l] = 1;
               }
            } catch(StringIndexOutOfBoundsException e) {
               k[x][l] = s[x].substring(y, y + 1);
               p[x][l] = 1;}
            l++;
         }
      }
      createFullArr(k);
      
      String[][] varNoPow = new String[k.length][fullArr.length];
      pow = new int[k.length][fullArr.length];
      for(int x = 0; x < varNoPow.length; x++)
         for(int y = 0; y < varNoPow[0].length; y++)
            for(int z = 0; z < fullArr.length; z++)
               try {
                  if(fullArr[z].equals(k[x][y]))
                  {
                     varNoPow[x][z] = k[x][y];
                     pow[x][z] = p[x][y];
                     break;
                  }
               } catch(NullPointerException e) {
                  break;}
      return varNoPow;
   }
   private static void createFullArr(String[][] s)
   {
      String[] q = new String[s.length * s[0].length];
      int l = 0;
      for(int x = 0; x < s[0].length; x++)
         if(s[0][x] != null)
         {
            q[x] = s[0][x];
            l++;
         }
         else
            break;
      for(int x = 1; x < s.length; x++)
         for(int y = 0; y < getLength(s[x]); y++)
         {
            if(l != 0)
            {
               for(int z = 0; z < l; z++)
               {
                  try {
                     if(s[x][y].equals(q[z]))
                        break;
                     else if(z == l - 1)
                     {
                        q[l] = s[x][y];
                        l++;
                     }
                  } catch(NullPointerException e) {
                     break;}
               }
            }
            else if(s[x][y] != null)
            {  
               q[l] = s[x][y];
               l++;
            }
         }
      fullArr = new String[l];
      for(int x = 0; x < fullArr.length; x++)
         fullArr[x] = q[x];
   }
   private static String[] removeNull(String[] f)
   {
      String[] s = new String[getLength(f)];
      for(int x = 0; x < s.length; x++)
         s[x] = f[x];
      return s;
   }
   private static int getLength(String[] s)
   {
      for(int x = 0; x < s.length; x++)
         if(s[x] == null)
            return x;
      return s.length;
   }
   private static String[] findCommonVar(String[][] varNoPow)
   {
      String[] s = new String[fullArr.length];
      int length = 0;
      for(int x = 0; x < fullArr.length; x++)
      {
         for(int y = 0; y < varNoPow.length; y++)
         {
            try {
               varNoPow[y][x].charAt(0);
            } catch(NullPointerException e) {
               break;}
            if(y == varNoPow.length - 1)
            {
               s[length] = fullArr[x];
               length++;
            }
         }
      }
      
      String[] fin = new String[length];
      for(int x = 0; x < fin.length; x++)
         fin[x] = s[x];
      return fin;
   }
   public static Fraction getNumFactor(Fraction[] num)
   {      
      BigInteger[] d = new BigInteger[num.length];
      for(int x = 0; x < d.length; x++)
         d[x] = num[x].getDenom();
      
      BigInteger[] denoms = Arrays.copyOf(d, d.length);
      
      for(int x = 0; x < denoms.length; x++)
      {
         if(denoms[x].compareTo(BigInteger.ONE) != 0)
            break;
         if(x == denoms.length - 1)
            return getWholeFactor(num);
      } 
      
      //find lcm to make all fractions whole
      BigInteger lcm = BigInteger.ONE;
      BigInteger divisor = new BigInteger("2");
         
      while(true) 
      {
         int counter = 0;
         boolean divisible = false;
             
         for(int i = 0; i < denoms.length; i++)
         {         
            if(denoms[i].compareTo(BigInteger.ZERO) < 0)
               denoms[i] = denoms[i].abs();
            if (denoms[i].compareTo(BigInteger.ONE) == 0)
               counter++;
            
            if(denoms[i].remainder(divisor).compareTo(BigInteger.ZERO) == 0)
            {
               divisible = true;
               denoms[i] = denoms[i].divide(divisor);
            }
         }
         if(divisible)
            lcm = lcm.multiply(divisor);
         else
            divisor = divisor.add(BigInteger.ONE);
      
         if(counter == denoms.length)
            break;
      }
      for(int x = 0; x < denoms.length; x++)
      {
         BigInteger m = lcm.divide(d[x]);
         num[x] = new Fraction(m.multiply(num[x].getNum()), BigInteger.ONE);
      }
      fracLCM = lcm.toString();
      
      return getWholeFactor(num);
   }
   public static Fraction getWholeFactor(Fraction[] num)
   {
      Fraction[] n = new Fraction[num.length];
      for(int x = 0; x < n.length; x++)
         n[x] = num[x].abs();
   
      Fraction co = Fraction.ONE;
      if(num[0].compareTo(Fraction.ZERO) < 0)
         co = Fraction.NEGONE;
      
      int minPos = 0;
      for(int x = 1; x < n.length; x++)
         if(n[x].compareTo(n[minPos]) < 0)
            minPos = x;
      
      Fraction[] f = n[minPos].findFactors();
      Fraction fac = Fraction.ONE;
      loop: for(int x = f.length - 1; x >= 0; x--)
      {
         for(int y = 0; y < n.length; y++)
         {
            if(n[y].divide(f[x]).getDenom().compareTo(BigInteger.ONE) != 0)
               break;
            if(y == n.length - 1)
            {
               fac = f[x];
               break loop;
            }
         }
      }
      fac = fac.multiply(co);
      for(int x = 0; x < num.length; x++)
         num[x] = num[x].divide(fac);
      return fac;
   }
   public static boolean canDivide(int[] n, int divisor)
   {
      for(int x = 0; x < n.length; x++)
         if(n[x] % divisor != 0 && n[x] != 0)
            return false;
      return true;
   }
   
   private static boolean canBeQuadFactored()
   {
      for(int x = 0; x < pow[0].length; x++)
         if(pow[0][x] != 0 && pow[1][x] != 0 && (double) pow[0][x] / 2 != pow[1][x])
            return false;
      for(int x = 0; x < pow[2].length; x++)
         if(pow[2][x] != 0 && pow[1][x] != 0 && (double) pow[2][x] / 2 != pow[1][x])
            return false;
      return true;
   }
   
   //helper methods used by this and other classes
   public static String getVariable(String[] var, int[] pow)
   {
      String fin = "";
      for(int x = 0; x < var.length; x++)
         fin += getOneVariable(var[x], pow[x]);
      return fin;
   }
   public static String getOneVariable(String var, int pow)
   {
      if(pow == 1)
         return var;
      if(pow == 0)
         return "";
      return var+"^"+pow;
   }
   public static String factorString(Fraction n, boolean plusSign)
   {
      if(n.equals(Fraction.NEGONE))
         return "-";
      if(n.equals(Fraction.ONE) && plusSign)
         return "\\+";
      if(n.equals(Fraction.ONE))
         return "";
      if(n.compareTo(Fraction.ZERO) > 0 && plusSign)
         return "\\+"+n;
      return n.toString();
   }
   public static String midString(Fraction num, String[] var, int[] pow, boolean plus)
   {
      String co = num.toString();
      String v = getVariable(var, pow);
      if((num.equals(Fraction.NEGONE) || num.equals(Fraction.ONE)) && !v.equals(""))
         co = co.replaceAll("1", "");
      if(num.compareTo(Fraction.ZERO) < 0)
         return " - "+num.abs().toString()+v;
      if(num.equals(Fraction.ZERO))
         return "";
      if(plus)
         return " + "+co+v;
      return co+v;
   }
   public static String midString(Fraction num, String var, int pow, boolean plus)
   {
      String co = num.toString();
      if(num.equals(Fraction.NEGONE) || num.equals(Fraction.ONE))
         co = co.replaceAll("1", "");
      if(num.compareTo(Fraction.ZERO) < 0)
         return " - "+co.replaceAll("-","")+getOneVariable(var, pow);
      if(num.equals(Fraction.ZERO))
         return "";
      if(plus)
         return " + "+co+getOneVariable(var, pow);
      return co+getOneVariable(var, pow);
   }
   public static String constantString(Fraction n, boolean space)
   {
      if(n.compareTo(Fraction.ZERO) < 0 && space)
         return " - "+n.multiply(Fraction.NEGONE);
      if(n.compareTo(Fraction.ZERO) > 0 && space)
         return " + "+n.toString();
      if(n.compareTo(Fraction.ZERO) > 0)
         return "\\+"+n.toString();
      return n.toString();
   }
   public static boolean indexIsNum(String s, int loc)
   {
      for(int x = 0; x < numbers.length(); x++)
         try {
            if(s.charAt(loc) == numbers.charAt(x))
               return true;
         } catch(StringIndexOutOfBoundsException e) {
            break;}
      return false;
   } 
   private static boolean indexIsLetter(String s, int loc)
   {
      for(int x = 0; x < alphabet.length(); x++)
         try {
            if(s.charAt(loc) == alphabet.charAt(x))
               return true;
         } catch(StringIndexOutOfBoundsException e) {
            break;}
      return false;
   }
   private static boolean indexExists(String s, int loc)
   {
      try {
         s.charAt(loc);
      } catch(StringIndexOutOfBoundsException e) {
         return false;}
      return true;
   }
   
   private static Fraction[] addZeroes(Fraction[] z)
   {
      Fraction[] y = new Fraction[pow[0][0] + 1]; 
      int offset = 0;
      for(int x = 0; x < y.length; x++)
      {
         if(pow[x-offset][0] == y.length - x - 1)
            y[x] = z[x-offset];
         else
         {
            y[x] = Fraction.ZERO;
            offset++;
         }
      }
      return y;
   }
   public static String toString(String factor, Fraction[] c)
   {
      String s = factorString(c[0], false)+getVariable(fullArr, pow[0]);
      if(!factor.equals(""))
         s = factor+"("+s;
      for(int x = 1; x < c.length; x++)
         s += midString(c[x], fullArr, pow[x], true);
      if(!factor.equals(""))
         s += ")";
      return s;
   }
}