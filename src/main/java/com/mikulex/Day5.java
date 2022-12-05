package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day5 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day5.class.getClassLoader().getResource("input5_5.txt").toURI());

        part1(path);
        part2(path);
    }

    private static void part1(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<Stack<String>> stacks = createStacks(reader);

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("move")) {
                    moveStack(currentLine, stacks, extractCommands(currentLine));
                }
            }
            //stacks.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void part2(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<Stack<String>> stacks = createStacks(reader);

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("move")) {
                    liftStack(currentLine, stacks, extractCommands(currentLine));
                }
            }
            stacks.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void moveStack(String currentLine, List<Stack<String>> stacks, List<Integer> commands) {
        // stack source and destination are being counted from one instead of zero :(
        Stack<String> source = stacks.get(commands.get(1) - 1);
        Stack<String> destination = stacks.get(commands.get(2) - 1);

        for (int amount = commands.get(0); amount > 0; amount--) {
            destination.push(source.pop());
        }
    }

    private static void liftStack(String currentLine, List<Stack<String>> stacks, List<Integer> commands) {
        // stack source and destination are being counted from one instead of zero :(
        Stack<String> source = stacks.get(commands.get(1) - 1);
        Stack<String> destination = stacks.get(commands.get(2) - 1);
        int head = destination.size();
        for (int amount = commands.get(0); amount > 0; amount--) {
            destination.insertElementAt(source.pop(), head);
        }
    }

    private static List<Integer> extractCommands(String currentLine) {
        return Arrays.stream(currentLine.split(" "))
                .filter(Pattern.compile("\\d+").asMatchPredicate())
                .map(Integer::parseInt)
                .toList();
    }

    private static List<Stack<String>> createStacks(BufferedReader reader) throws IOException {

        String currentLine;
        List<Stack<String>> stacks = Stream.generate(Stack<String>::new).limit(9).toList();

        while ((currentLine = reader.readLine()).contains("[")) {
            String currentStack = currentLine.substring(0, 3);

            for (int i = 0; i < 9; i++) {
                currentStack = currentLine.substring(i * 4, i * 4 + 3);
                if (currentStack.contains("[")) {
                    stacks.get(i).insertElementAt(currentStack, 0);
                }
            }
        }
        return stacks;
    }
}
