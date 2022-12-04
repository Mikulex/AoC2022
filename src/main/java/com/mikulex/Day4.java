package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day4 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Paths.get(Day4.class.getClassLoader().getResource("input4_4.txt").toURI());

        part1(path);
        part2(path);

    }

    private static void part1(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            long result = reader.lines().map(string -> string.split(","))
                    .map(Day4::convertToRanges)
                    .filter(Day4::rangesAreContained)
                    .count();
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void part2(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            long result = reader.lines().map(string -> string.split(","))
                    .map(Day4::convertToRanges)
                    .filter(Day4::rangesAreOverlapped)
                    .count();
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean rangesAreOverlapped(List<Range> ranges) {
        return ranges.get(0).overlaps(ranges.get(1)) ||
               ranges.get(1).overlaps(ranges.get(0));
    }

    private static boolean rangesAreContained(List<Range> ranges) {
        return ranges.get(0).isContainedIn(ranges.get(1)) ||
               ranges.get(1).isContainedIn(ranges.get(0));
    }

    private static List<Range> convertToRanges(String[] strings) {
        return Arrays.stream(strings)
                .map(string -> string.split("-"))
                .map(rooms -> new Range(Integer.parseInt(rooms[0]), Integer.parseInt(rooms[1])))
                .collect(Collectors.toList());
    }

    private record Range(int min, int max) {

        public boolean isContainedIn(Range other) {
            return this.min >= other.min && this.max <= other.max;
        }

        public boolean overlaps(Range other) {
            return this.isContainedIn(other)
                    || (this.max >= other.min && this.max <= other.max)
                    || (this.min >= other.min && this.min <= other.max);
        }
    }

}
