package sample;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ritwik Banerjee
 */
public class Main {

    public static void main(String... args) {
        Drug a = new Drug("ibuprofen", 200, Drug.DosageFrequency.AS_NEEDED);
        Drug b = new Drug("ibuprofen", 200, Drug.DosageFrequency.DAILY);
        Drug c = new Drug("ibuprofen", 400, Drug.DosageFrequency.DAILY);
        Drug d = new Drug("ibuprofen", 200, Drug.DosageFrequency.TWICE_A_DAY);
        Drug e = new Drug("aripriprazole", 4, Drug.DosageFrequency.DAILY);
        Drug f = new Drug("glimepiride", 1, Drug.DosageFrequency.DAILY);
        Drug g = new Drug("ibuprofen", 400, Drug.DosageFrequency.DAILY);


        // c and g are complete duplicates, so the tree won't print both of them.
        SortedSet<Drug> pillbox = new TreeSet<>(Stream.of(a, b, c, d, e, f, g).collect(Collectors.toSet()));

        pillbox.forEach(drug -> System.out.println(drug.toString()));
    }
}
