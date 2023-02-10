package java16code.Recipes;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MaxUsingReduce {

  public static void main (String... args)
  {
    Random rand = new Random();
    //Integer max = Stream.of(1, 9, 3, 11, 7, 5, 8) 
    List<Integer> numList = rand.ints(7, 0, 99)
      .boxed()
      .collect(Collectors.toList());
    System.out.println(numList);
    Integer max = numList.stream()
      .reduce(Integer.MIN_VALUE, Integer::max);

    System.out.println("The Max value is: " + max);
  }
}
