package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Paths.get(Day3.class.getClassLoader().getResource("input3_3.txt").toURI());

        part1(path);
        part2(path);
    }

    private static void part1(Path path) {
        int result = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            result += reader.lines()
                    .map(Day3::splitToCompartments)
                    .map(Day3::extractDuplicates)
                    .mapToInt(Day3::calculatePriority)
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
    }

    private static void part2(Path path) {
        int result;
        int groupIndex = -1;
        int lineIndex = 0;
        String currentLine;

        // List of groups of 3 Character sets
        List<List<Set<Character>>> groups = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            while ((currentLine = reader.readLine()) != null) {
                if (lineIndex % 3 == 0) {
                    groupIndex++;
                    groups.add(new ArrayList<>());
                }
                groups.get(groupIndex).add(getCharacterSet(currentLine));
                lineIndex++;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        result = groups.stream()
                .map(Day3::extractDuplicates)
                .mapToInt(Day3::calculatePriority)
                .sum();
        System.out.println(result);
    }

    private static int calculatePriority(Set<Character> characters) {
        return characters.stream()
                /*
                 * lowercase: a-z 97 - 122
                 * uppercase: A-Z 65 - 90
                  */
                .mapToInt(character -> character - (isUppercase(character) ? 'A' : 'a') + (isUppercase(character) ? 27 : 1))
                .sum();
    }

    private static boolean isUppercase(Character character) {
        return character < 'a';
    }

    private static Set<Character> extractDuplicates(List<Set<Character>> compartments) {
        Set<Character> intersection = compartments.get(0);

        compartments.forEach(intersection::retainAll);
        return intersection;
    }

    private static List<Set<Character>> splitToCompartments(String line) {
        return Stream.of((line.substring(0, line.length() / 2)), line.substring(line.length() / 2))
                .map(Day3::getCharacterSet)
                .collect(Collectors.toList());
    }

    private static Set<Character> getCharacterSet(String subString) {
        return subString.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    }

}
