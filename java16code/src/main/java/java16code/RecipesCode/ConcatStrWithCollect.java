package java16code.RecipesCode;

import java.util.logging.Logger;
import java.util.stream.Stream;

class ConcatStrWithCollect {
  static Logger log = Logger.getLogger("ConcatStrWithCollect.class");
  public static void main(String... args) {
    Stream<String> streamWords = Stream.of("this", "is", "a", "list", "of", "strings");

    var result1 = concatUsingLambdas(streamWords);
    log.info(result1);

    Stream<String> streamWords2 = Stream.of("other","strings","joined","here!");

    var result2 = concatUsingMethodReference(streamWords2);
    log.info(result2);
  }

  static String concatUsingLambdas(Stream<String> inWords) {
    String resultStr = inWords
        .collect(StringBuilder::new,
            (sb, str) -> sb.append(str).append(".."),
            StringBuilder::append)
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
