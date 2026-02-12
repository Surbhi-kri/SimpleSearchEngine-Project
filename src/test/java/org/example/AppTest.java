package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        Main.str = new ArrayList<>();
        Main.map = new HashMap<>();

        // Prepare sample data
        Main.str.add("John Doe");
        Main.str.add("Jane Smith");
        Main.str.add("Alice Johnson");

        Main.invertedIndex();

        // Capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    private void setInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Main.sc = new Scanner(System.in); // IMPORTANT
    }
    private Path createTempFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, content);
        return tempFile;
    }
    @Test
    void testSearchAll() {
        setInput("ALL\nJohn\n");

        Main.searchPeople();

        String output = outputStream.toString();

        assertTrue(output.contains("1 persons found:"));
        assertTrue(output.contains("John Doe"));
    }
    @Test
    void testSearchAny() {
        setInput("ANY\nJohn Jane\n");

        Main.searchPeople();

        String output = outputStream.toString();

        assertTrue(output.contains("2 persons found:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("Jane Smith"));
    }
    @Test
    void testSearchNone() {
        setInput("NONE\nJohn\n");

        Main.searchPeople();

        String output = outputStream.toString();

        assertTrue(output.contains("2 persons found:"));
        assertTrue(output.contains("Jane Smith"));
        assertTrue(output.contains("Alice Johnson"));
    }
    @Test
    void testIncorrectStrategyDefaultsToAny() {
        setInput("WRONG\nJane\n");

        Main.searchPeople();

        String output = outputStream.toString();

        assertTrue(output.contains("Incorrect strategy! Using ANY."));
        assertTrue(output.contains("Jane Smith"));
    }
    @Test
    void testNoMatchFound() {
        setInput("ANY\nMichael\n");

        Main.searchPeople();

        String output = outputStream.toString();

        assertTrue(output.contains("No matching people found."));
    }

    @Test
    void testExitImmediately() throws Exception {
        Path file = createTempFile("John Doe");

        System.setIn(new ByteArrayInputStream("0\n".getBytes()));

        Main.main(new String[]{"--data", file.toString()});

        String output = outputStream.toString();

        assertTrue(output.contains("=== Menu ==="));
        assertTrue(output.contains("Bye!"));
    }





    @Test
    void testIncorrectMenuOption() throws Exception {
        Path file = createTempFile("John Doe");

        System.setIn(new ByteArrayInputStream("abc\n0\n".getBytes()));

        Main.main(new String[]{"--data", file.toString()});

        String output = outputStream.toString();

        assertTrue(output.contains("Incorrect option! Try again."));
    }

    @Test
    void testFileNotFound() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));

        Main.main(new String[]{"--data", "nonexistent.txt"});

        String output = outputStream.toString();

        assertTrue(output.contains("Cannot read file!"));
    }

    @Test
    void testInvertedIndexMapping() {
        assertTrue(Main.map.containsKey("john"));
        assertTrue(Main.map.containsKey("jane"));
        assertTrue(Main.map.containsKey("alice"));

        assertTrue(Main.map.get("john").contains(0));
        assertTrue(Main.map.get("jane").contains(1));
        assertTrue(Main.map.get("alice").contains(2));
    }

    @Test
    void testSearchPeopleAll() {
        // ALL strategy
        Set<Integer> result = new HashSet<>();
        String[] words = {"john", "doe"};
        boolean first = true;
        for (String word : words) {
            Set<Integer> temp = Main.map.getOrDefault(word, new HashSet<>());
            if (first) {
                result.addAll(temp);
                first = false;
            } else {
                result.retainAll(temp);
            }
        }
        assertEquals(1, result.size());
        assertTrue(result.contains(0));
    }

    @Test
    void testSearchPeopleAny() {
        // ANY strategy
        Set<Integer> result = new HashSet<>();
        String[] words = {"john", "jane"};
        for (String word : words) {
            result.addAll(Main.map.getOrDefault(word, new HashSet<>()));
        }
        assertEquals(2, result.size());
        assertTrue(result.contains(0));
        assertTrue(result.contains(1));
    }

    @Test
    void testSearchPeopleNone() {
        // NONE strategy
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < Main.str.size(); i++) {
            result.add(i);
        }
        for (String word : new String[]{"john"}) {
            result.removeAll(Main.map.getOrDefault(word, new HashSet<>()));
        }
        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
    }

    @Test
    void testSearchPeopleInvalidStrategy() {
        // Invalid strategy triggers default ANY
        Set<Integer> result = new HashSet<>();
        String[] words = {"alice"};
        for (String word : words) {
            result.addAll(Main.map.getOrDefault(word, new HashSet<>()));
        }
        assertEquals(1, result.size());
        assertTrue(result.contains(2));
    }

    @Test
    void testDisplayPeople() {
        // Just call it to cover the method
        Main.displayPeople();  // Should print 3 people
    }

    @Test
    void testMappingAddsIndices() {
        // Test mapping directly
        Main.map.clear();
        Main.mapping("hello world", 0);
        assertTrue(Main.map.containsKey("hello"));
        assertTrue(Main.map.containsKey("world"));
        assertTrue(Main.map.get("hello").contains(0));
        assertTrue(Main.map.get("world").contains(0));
    }

    @Test
    void testInvertedIndexWithMultipleLines() {
        Main.str.add("Bob Marley bob@example.com");
        Main.invertedIndex();
        assertTrue(Main.map.containsKey("bob"));
        assertTrue(Main.map.get("bob").contains(3));
    }
}
