package java16code.RecipesCode;

import java.util.stream.IntStream;

class StreamsPeekDebug {

  public static void main (String[] args) {

    int sum = sumDoubleDivisibleBy3(3,6);

    System.out.println("\nSum of filtered: " + sum);
  }

  public static int  sumDoubleDivisibleBy3 (int start, int end) {

    return IntStream.rangeClosed(start, end)
      .peek(n -> System.out.printf("original: %d%n", n))
      .map(n -> n * 2)
      .peek(n -> System.out.printf("doubled: %d%n", n))
      .filter(n -> n % 3 == 0)
      .peek(n -> System.out.printf("filtered: %d%n", n))
      .sum();
}

}
