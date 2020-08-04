package nl.uva.alexandria.logic.comparison;

import nl.uva.alexandria.logic.exceptions.FileException;
import nl.uva.alexandria.model.comparison.LibraryComparison;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static nl.uva.alexandria.model.comparison.LibraryComparisonFactory.createLibraryComparisonFromValues;

@Component
public class AnalysisComparator {

    public void compare(String pathToFile) {
        // Open file.
        File file = new File(pathToFile);

        Set<LibraryComparison> libraryComparisonSet;
        try {
            libraryComparisonSet = createLibraryComparisonSet(file);
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }

        doRequests(libraryComparisonSet);
        // For each library
        // Do analysis request
        // Convert request result and set
        // Do comparison
    }

    private Set<LibraryComparison> createLibraryComparisonSet(File file) throws IOException {
        Set<LibraryComparison> libraryComparisonSet = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine(); // Ignore the headers
            line = bufferedReader.readLine();

            while (line != null) {
                // Read the line
                String[] values = line.split("\t");
                LibraryComparison newLibraryComparison = createLibraryComparisonFromValues(values);
                libraryComparisonSet.add(newLibraryComparison);
                line = bufferedReader.readLine();
            }
        }

        return libraryComparisonSet;
    }

    private void doRequests(Set<LibraryComparison> libraryComparisonSet) {
        // To implement
    }
}
