package java16code.RecipesCode;

import java.util.stream.*;
import java.util.Optional;

class StreamsFind {

    public static void main (String[] args) {
        // Finding the first even integer
        Optional<Integer> firstEven = Stream.of(3, 1, 4, 1, 5, 7, 8, 6, 9, 2, 5)
            .filter(n -> n % 2 == 0)
            .findFirst();

        System.out.println("First even: " + firstEven);

    }


}
