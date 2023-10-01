package java16code.RecipesCode;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MaxUsingReduce {

  public static void main (String... args)
  {
    Stream<Integer> maxNum = Stream.of(1, 9, 3, 11, 7, 5, 8);
    Integer max1 = maxNum.peek(num -> System.out.print(num+", ")).reduce(Integer.MIN_VALUE, Integer::max);
    System.out.println("\nThe Max value in first stream is: " + max1 + "\n");

    Random rand = new Random();
    List<Integer> numList = rand.ints(7, 0, 99)
        .boxed()
        .collect(Collectors.toList());
    System.out.println(numList);
    Integer max2 = numList.stream()
        .reduce(Integer.MIN_VALUE, Integer::max);
    System.out.println("The Max value in second stream is: " + max2);
  }
}
