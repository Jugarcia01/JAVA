package java8code;

// String format example code.
public class StrFormat
{

  public static void main (String[] args)
  {
    //Remove Whitespace from the Beginning and End of a String
    String str1 = "  Cadena de texto nueva!   ";
    String strFmt1 = str1.trim();

    System.out.println(strFmt1);

    //Replacing parts of String

    // -Exact match replace single character with other
    String str2 = "Bocon";
    System.out.println(str2.replace("o", "a"));

    // -Replace sequence of characters with another sequence of characters
    String str3 = "Trabajamos y festejamos";
    System.out.println(str3.replace("jamos", "jo"));

  }

  

}
