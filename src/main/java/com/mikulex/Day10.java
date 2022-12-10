package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day10 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day10.class.getClassLoader().getResource("input10_10.txt").toURI());

        solve(path);

    }

    private static void solve(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String currentInstruction = "";
            int cycle = 0;
            int register = 1;
            int temp = 0;
            int sum = 0;
            boolean instructionFinished = true;
            StringBuilder output = new StringBuilder();

            while (true) {
                cycle++;
                sum = logStrength(cycle, register, sum); // part 1
                if (instructionFinished) {
                    currentInstruction = reader.readLine();
                    if (currentInstruction == null) {
                        break;
                    }
                    String[] split = currentInstruction.split(" ");

                    if (split[0].equals("addx")) {
                        temp = register + Integer.parseInt(split[1]);
                        instructionFinished = false;
                    }
                    drawPixel(cycle, register, output);
                } else {
                    drawPixel(cycle, register, output);
                    register = temp;
                    instructionFinished = true;


                }
            }

            System.out.println(sum);
            System.out.println(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawPixel(int cycle, int register, StringBuilder output) {
        int position = (cycle - 1) % 40 ;
        boolean isSeen = position == register || position == register - 1 || position == register + 1;
        if (position == 0) {
            output.append("\n");
        }
        output.append(isSeen ? "#" : ".");

    }

    private static int logStrength(int cycle, int register, int sum) {
        if (cycle == 20 || (cycle -20) % 40 == 0) {
            System.out.println("cycle " + cycle + " strength " + register * cycle);
            sum += register * cycle;
        }
        return sum;
    }
}
