package java8code;

/* Changing case of a speciﬁc character within an ASCII string
 * Steps:
 1- Declare a string.
 2- Input the string.
 3- Convert the string into a character array.
 4- Input the character that is to be searched.
 5- Search for the character into the character array.
 6- If found,check if the character is lowercase or uppercase.
     If Uppercase, add 32 to the ASCII code of the character.
     If Lowercase, subtract 32 from the ASCII code of the character.
 7- Change the original character from the Character array.
 8- Convert the character array back into the string.
 Great!, the Case of the character is changed.
*/

import java.util.Scanner;

public class charChange {
  public static void main (String[] args) 
  {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter a target string of text:");
    String text = scanner.nextLine();
    char[] charArray = text.toCharArray();
    System.out.println("Enter character you are looking for: ");
    //System.out.println(text);
    String searchCh = scanner.next();
    char ch= searchCh.charAt(0);

    char newCh = ch;
    for (int i = 0; i <= text.length()-1; i++)
    {
      if(charArray[i] == ch)
      {
        if (ch >= 'a' && ch <= 'z') { newCh -= 32; }
        else if (ch >= 'A' && ch <= 'Z') { newCh += 32; }
        charArray[i] = newCh;
        newCh = ch;
        //break;
      }
    }
    System.out.println("Result:");
    text = String.valueOf(charArray);
    System.out.println(text);
  }
}
