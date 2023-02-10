package java16code.RecipesCode;

import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.lang.System.Logger;
import java.util.Arrays;
import java.util.Comparator;

class InvokeMultiArgsFromClassRef {
  //public static final Logger Log =

  public static void main(String... args) {
    List<String> myList = Arrays.asList("this","is","a","list","of","strings");

    List<String> myListSorted = myList.stream()
      .sorted((s1,s2) -> s1.compareTo(s2))
      .collect(Collectors.toList());
    
    //myListSorted.stream().forEach(System.out::println); // Print each word in a line in alph sorted
    //System.out.println(myListSorted); //Imprime palab de myList separadas por coma ordenadas en forma alfaetica.
  
    System.out.println("\nPalabras ordenadas por longitud en forma descendente:");
  myList.sort(Comparator
      .comparingInt(String::length).reversed());
  myList.stream()
      .forEach(s -> System.out.println(s));


    //myList.stream()
    //.sorted(Integer.compare(s1.length, s2.length)) >> ERROR
    //.forEach(x -> System.out.print(x + " "));

      //myList.stream().sorted((s1,s2) -> s1.length().compareTo(s2.length())) >> ERROR!!!
      //.forEach(System.out::println);
  }

}

