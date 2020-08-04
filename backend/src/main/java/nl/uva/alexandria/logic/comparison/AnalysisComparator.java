package nl.uva.alexandria.logic.comparison;

import nl.uva.alexandria.model.comparison.LibraryComparison;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class AnalysisComparator {

    public void compare(String pathToFile) {
        // Open file.
        File file = new File(pathToFile);

        try {
            Set<LibraryComparison> libraryComparisonSet = createLibraryComparisonSet(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // For each library
        // Do analysis request
        // Convert request result and set
        // Do comparison
    }

    private Set<LibraryComparison> createLibraryComparisonSet(File file) throws IOException {
        Set<LibraryComparison> libraryComparisonSet = new HashSet<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        bufferedReader.readLine(); // Ignore the headers
        String line = bufferedReader.readLine();

        while (line != null) {
            // Read the line
            String[] values = line.split("\t");
            // LibraryComparison newLibraryComparison = createLibraryComparisonFromValues(values);
            // libraryComparisonSet.add(newLibraryComparison);
            line = bufferedReader.readLine();
        }

        return libraryComparisonSet;
    }
}
