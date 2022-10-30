import javax.swing.JOptionPane;
public class Combinations
{
   public static void main(String[] args)
   {
      int n = 0, k = 0;
      try {
         n = Integer.parseInt(JOptionPane.showInputDialog("Input n."));
         k = Integer.parseInt(JOptionPane.showInputDialog("Input k."));
      } catch(NumberFormatException e) {
         System.out.println("Error: Could not interpret input as a number.");
         System.exit(0); 
      }
      if(k > n)
      {
         System.out.println("Error: k is greater than n");
         System.exit(0);
      }
      String s = "";
      for(int x = 1; x <= n; x++)
         s += x+"";
      printComb(s, k, "");
   }
   public static void printComb(String f, int n, String curr)
   {
      if(n != 0)
         for(int x = 1; x <= f.length(); x++)
            printComb(f.substring(x, f.length()), n - 1, curr+f.charAt(x - 1));
      else
         System.out.println(curr);
   }
}