package java16code.RecipesCode;

import java.util.stream.Stream;
import java.util.stream.Collectors;

class ConcatStrWithCollect {

  public static void main(String... args) {
    Stream<String> streamWords = Stream.of("this", "is", "a", "list", "of", "strings");

    var result1 = concatUsingLambdas(Stream inputWords);

  }

  static String concatUsingLambdas(Stream inWords) {
    String resultStr = inWords
      .collect(() -> new StringBuilder(),
          (sb, str) -> sb.append(str),
          (sb1, sb2) -> sb1.append(sb2))
      .toString();
      
    return resultStr;
  }

}
