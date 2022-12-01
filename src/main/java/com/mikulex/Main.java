package com.mikulex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        Main main = new Main();
        URL resource = main.getClass().getClassLoader().getResource("input1_1.txt");

        Path path = Paths.get(resource.toURI());

        calculateMostCalories(path);
        calculateTop3Calories(path);
    }

    private static void calculateMostCalories(Path path) {
        int currentSum = 0;
        int maxSum = 0;

        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isBlank()) {
                    maxSum = Math.max(currentSum, maxSum);
                    currentSum = 0;
                } else {
                    currentSum += Integer.parseInt(currentLine);
                }
            }
            System.out.println(Math.max(currentSum, maxSum));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void calculateTop3Calories(Path path) {
        List<Integer> calories = new ArrayList<>();

        int currentSum = 0;

        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isBlank()) {
                    calories.add(currentSum);
                    currentSum = 0;
                } else {
                    currentSum += Integer.parseInt(currentLine);
                }
            }
            System.out.println(calories.stream().sorted(Comparator.reverseOrder()).limit(3).reduce(Integer::sum));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}