package version1;

public class Scripts
{
   public static String superscript(String str)
   {       
      int index = str.indexOf("^");
      while(index != -1)
      {
         int tmp = index + 1;
         while(Character.isDigit(str.charAt(tmp)))
            tmp++;
         
         String[] s = {str.substring(0, index), str.substring(index + 1, tmp), str.substring(tmp, str.length())};
         s[1] = replaceSup(s[1]);
         str = s[0]+s[1]+s[2];
         
         index = str.indexOf("^");
      }
      return str;
   }
   private static String replaceSup(String str)
   {
      return str.replaceAll("0", "\u2070").replaceAll("1", "\u00B9").replaceAll("2", "\u00B2").replaceAll("3", "\u00B3").replaceAll("4", "\u2074").replaceAll("5", "\u2075").replaceAll("6", "\u2076").replaceAll("7", "\u2077").replaceAll("8", "\u2078").replaceAll("9", "\u2079");
   }
   public static String subscript(String str)
   {
      return str.replaceAll("0", "\u2080").replaceAll("1", "\u2081").replaceAll("2", "\u2082").replaceAll("3", "\u2083").replaceAll("4", "\u2084").replaceAll("5", "\u2085").replaceAll("6", "\u2086").replaceAll("7", "\u2088").replaceAll("8", "\u2088").replaceAll("9", "\u2089");
   }
}