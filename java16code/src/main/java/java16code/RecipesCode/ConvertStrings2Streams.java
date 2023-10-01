package java16code.RecipesCode;

class ConvertStrings2Streams {
  public static void main(String[] args) {
    String aSentence = "Madam in Eden, Im Adam";
    System.out.println("Input Sentence:\n\t" + aSentence);
    System.out.println("Is palindrome?: " + isPalindrone(aSentence));

  }

  public static boolean isPalindrone(String txtIn) {
    String text = txtIn.toLowerCase().codePoints()
        .filter(Character::isLetterOrDigit)
        .collect(StringBuilder::new,
            StringBuilder::appendCodePoint,
            StringBuilder::append)
        .toString();

    String textInverted = new StringBuilder(text)
        .reverse()
        .toString();

    return text.equals(textInverted);
  }
}
