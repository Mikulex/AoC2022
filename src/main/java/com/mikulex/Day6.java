package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day6 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day6.class.getClassLoader().getResource("input6_6.txt").toURI());

        calculate(path, 4);
        calculate(path, 14);
    }

    private static void calculate(Path path, int amount) {
        List<Character> recentChars = new LinkedList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int readChar;
            int count = 0;
            boolean notFound = true;
            while ((readChar = reader.read()) != -1 && notFound) {
                count++;
                if (count < amount) {
                    recentChars.add((char) readChar);
                } else {
                    notFound = checkRecentChars(recentChars, (char) readChar, count, amount);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkRecentChars(List<Character> recentChars, char readChar, int count, int amount) {
        boolean notFound;
        recentChars.add(readChar);
        Set<Character> set = new HashSet<>(recentChars);
        if (set.size() == amount) {
            System.out.println(count);
            notFound = false;
        } else {

            recentChars.remove(0);
            notFound = true;
        }
        return notFound;
    }

}
