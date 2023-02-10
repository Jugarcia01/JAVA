package java16code.RecipesCode;

import java.lang.System.*;
import java.util.*;
import java.util.stream.Stream;

class LambdaExpAndMethodRef {

    public static void main (String[] args) {
      // Stream of finite seq of data
      // Using a method reference to access a existing method 'print'
      Stream.of(1, 3, 6, 5, 2)
        .forEach(x -> System.out.print(x + ", "));
        // Stream of limited infinite seq of data
        // Using a lambda expression to acces a method
      System.out.println();
      Stream.generate(Math::random)
        .limit(10)
        .forEach(System.out::println);

  }

}
