package org.example;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class Main {

    static ArrayList<String> str = new ArrayList<>();
    static Scanner sc;
    static Map<String, Set<Integer>> map = new HashMap<>();

    public static void main(String[] args) {


        str = new ArrayList<>();
        map = new HashMap<>();
        sc = new Scanner(System.in);

        String fileName = "text.txt";

        for (int i = 0; i < args.length; i++) {
            if ("--data".equals(args[i]) && i + 1 < args.length) {
                fileName = args[i + 1];
            }
        }

        try {
            str = new ArrayList<>(Files.readAllLines(Path.of(fileName)));
        } catch (IOException e) {
            System.out.println("Cannot read file!");
            return;
        }

        invertedIndex();

        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");

            if (!sc.hasNextLine()) break;

            String menu = sc.nextLine();
            int meval;

            try {
                meval = Integer.parseInt(menu);
            } catch (Exception e) {
                System.out.println("Incorrect option! Try again.");
                continue;
            }

            if (meval == 1) searchPeople();
            else if (meval == 2) displayPeople();
            else if (meval == 0) {
                System.out.println("Bye!");
                break;
            } else {
                System.out.println("Incorrect option! Try again.");
            }
        }

        sc.close();
    }

    public static void invertedIndex() {
        for (int i = 0; i < str.size(); i++) {
            String sentence = str.get(i).toLowerCase();
            mapping(sentence, i);
        }
    }

    public static void mapping(String sentence, int idx) {
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            word = word.trim().replaceAll("[\\r\\n\\t]+", "");
            map.computeIfAbsent(word, k -> new HashSet<>()).add(idx);
        }
    }

    public static void searchPeople() {

        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        if (!sc.hasNextLine()) return;
        String matchingTypes = sc.nextLine().trim().toUpperCase();

        System.out.println("Enter a name or email to search all suitable people.");
        if (!sc.hasNextLine()) return;
        String input = sc.nextLine().trim().toLowerCase();

        String[] words = input.split("\\s+");
        Set<Integer> result = new HashSet<>();

        if (matchingTypes.equals("ALL")) {

            boolean first = true;
            for (String word : words) {
                Set<Integer> temp = map.getOrDefault(word, new HashSet<>());
                if (first) {
                    result.addAll(temp);
                    first = false;
                } else {
                    result.retainAll(temp);
                }
            }

        } else if (matchingTypes.equals("ANY")) {

            for (String word : words) {
                result.addAll(map.getOrDefault(word, new HashSet<>()));
            }

        } else if (matchingTypes.equals("NONE")) {

            for (int i = 0; i < str.size(); i++) result.add(i);
            for (String word : words) {
                result.removeAll(map.getOrDefault(word, new HashSet<>()));
            }

        } else {

            System.out.println("Incorrect strategy! Using ANY.");
            for (String word : words) {
                result.addAll(map.getOrDefault(word, new HashSet<>()));
            }
        }

        if (result.isEmpty()) {
            System.out.println("No matching people found.");
        } else {
            System.out.println(result.size() + " persons found:");
            for (Integer index : result) {
                System.out.println(str.get(index));
            }
        }
    }

    public static void displayPeople() {
        System.out.println("=== List of people ===");
        for (String s : str) {
            System.out.println(s);
        }
    }
}
