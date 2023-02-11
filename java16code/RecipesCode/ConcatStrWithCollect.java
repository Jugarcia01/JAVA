package java16code.RecipesCode;

import java.util.stream.Stream;
import java.util.stream.Collectors;

class ConcatStrWithCollect {

  public static void main(String... args) {
    Stream<String> streamWords = Stream.of("this", "is", "a", "list", "of", "strings");

    var result1 = concatUsingLambdas(streamWords);
    
    Stream<String> streamWords2 = Stream.of("other","strings","joined","here!");

    var result2 = concatUsingMethodReference(streamWords2);
  }

  static String concatUsingLambdas(Stream<String> inWords) {
    String resultStr = inWords
      .collect(() -> new StringBuilder(),
          (sb, str) -> sb.append(str + ".."),
          (sb1, sb2) -> sb1.append(sb2))
      .toString();
    System.out.println(resultStr);
    return resultStr;
  }

  static String concatUsingMethodReference(Stream<String> inWords) {
  String resultStr = inWords
    .collect(StringBuilder::new,
        StringBuilder::append,
        StringBuilder::append)
    .toString();
  System.out.println(resultStr);
  return resultStr;
  }

}
