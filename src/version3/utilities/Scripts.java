package version3.utilities;

/**
 * Class providing utilized functions for string formatting utilized
 * by the factoring program.
 * @author Nathan Harbison
 */
public class Scripts
{
   /**
    * Replaces all consecutive numbers following a caret character (i.e.
    * satisfying the regular expression \^\d+) in the given string with
    * their superscript representation in Unicode.
    * @param str the string to be reformatted
    * @return the string with all consecutive numbers following a caret
    * replaced with their superscript representation
    */
   public static String superscriptNum(String str)
   {
      int caret = str.indexOf("^");
      while(caret != -1)
      {
         int endOfDigits = caret + 1; // find end of digits after caret character
         while(endOfDigits < str.length() && Character.isDigit(str.charAt(endOfDigits)))
            endOfDigits++;

         String withSup = str.substring(caret + 1, endOfDigits)
                 .replaceAll("0", "\u2070")
                 .replaceAll("1", "\u00B9")
                 .replaceAll("2", "\u00B2")
                 .replaceAll("3", "\u00B3")
                 .replaceAll("4", "\u2074")
                 .replaceAll("5", "\u2075")
                 .replaceAll("6", "\u2076")
                 .replaceAll("7", "\u2077")
                 .replaceAll("8", "\u2078")
                 .replaceAll("9", "\u2079");
         str = str.substring(0, caret) + withSup + str.substring(endOfDigits);
         
         caret = str.indexOf("^");
      }
      return str;
   }

   /**
    * Replaces all consecutive numbers following an underscore character (i.e.
    * satisfying the regular expression _\d+) in the given string with
    * their subscript representation in Unicode.
    * @param str the string to be reformatted
    * @return the string with all consecutive numbers following an underscore
    * replaced with their superscript representation
    */
   public static String subscriptNum(String str)
   {
      int underscore = str.indexOf("_");
      while(underscore != -1)
      {
         int endOfDigits = underscore + 1; // find end of digits after underscore character
         while(endOfDigits < str.length() && Character.isDigit(str.charAt(endOfDigits)))
            endOfDigits++;

         String withSup = str.substring(underscore + 1, endOfDigits)
                 .replaceAll("0", "\u2080")
                 .replaceAll("1", "\u2081")
                 .replaceAll("2", "\u2082")
                 .replaceAll("3", "\u2083")
                 .replaceAll("4", "\u2084")
                 .replaceAll("5", "\u2085")
                 .replaceAll("6", "\u2086")
                 .replaceAll("7", "\u2088")
                 .replaceAll("8", "\u2088")
                 .replaceAll("9", "\u2089");
         str = str.substring(0, underscore) + withSup + str.substring(endOfDigits);

         underscore = str.indexOf("_");
      }
      return str;
   }
}