package java8code;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ListOperations
{

  public static void main (String args[])
  {
    System.out.println("List Operations on Java");
  
    List<String> myList = new ArrayList<>();
    myList.add("first");
    myList.add("Second");

    System.out.print(myList.toString()+"\n");
   
    List<String> fruits = new ArrayList<String>();
    fruits.add("Manzana");
    fruits.add("Banano");
    fruits.add("Fresa");
    fruits.add("Mango");
    fruits.add("Coco");
    fruits.add("Mandarina");
    fruits.add("Frambuesa");

    //Filtrando valores en la lista
    List<String> filteredFruitsList = new ArrayList<>();
    //filteredFruitsList = fruits.stream().filter(p -> !"Mandarina".equals(p)).collect(Collectors.toList());
    //System.out.print(filteredFruitsList.toString()+"\n");

    filteredFruitsList = fruits.stream().filter(p -> !p.contains("Man")).collect(Collectors.toList());
    System.out.print(filteredFruitsList.toString()+"\n");

  }
}

