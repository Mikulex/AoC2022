package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day13.class.getClassLoader().getResource("input14_14.txt").toURI());
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Set<Pixel> pixels = parseSolids(reader);

            SandEmitter emitter = new SandEmitter("+", 500, 0);
            pixels.add(emitter);
            int maxX = pixels.stream().max(Comparator.comparing(Pixel::getX)).map(Pixel::getX).orElse(0);
            int minX = pixels.stream().min(Comparator.comparing(Pixel::getX)).map(Pixel::getX).orElse(0);
            int maxY = pixels.stream().max(Comparator.comparing(Pixel::getY)).map(Pixel::getY).orElse(0);

            Pixel[][] map = prepareMap(pixels, maxX, minX, maxY);

            // part 1
            int sum = 0;
            while (letSandFall(map, emitter, minX, maxX, maxY)) {
                sum++;
            }
            printMap(map);
            System.out.println(sum);

            // part 2
            maxY = maxY + 2;
            map = prepareMap(pixels, emitter.getX() + maxY , emitter.getX() - maxY, maxY);
            addGround(maxY, map);

            sum = 0;
            while (letSandFall2(map, emitter, emitter.getX() - maxY, emitter.getX() + maxY, maxY)) {
                sum++;
            }
            printMap(map);
            System.out.println(sum  + 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addGround(int maxY, Pixel[][] map) {
        for (int i = 0; i < map[maxY].length; i++) {
            map[maxY][i] = new Solid("#", i, maxY);
        }
    }

    private static Pixel[][] prepareMap(Set<Pixel> pixels, int maxX, int minX, int maxY) {
        Pixel[][] map = new Pixel[maxY +1][maxX - minX +1];

        for (Pixel pixel : pixels) {
            map[pixel.getY()][pixel.getX() - minX] = pixel;
        }
        return map;
    }

    private static boolean letSandFall(Pixel[][] map, SandEmitter emitter, int minX, int maxX, int maxY) {
        int x = emitter.getX() - minX;
        int y = emitter.getY();

        maxX -= minX;
        minX = 0;

        while (x >= minX && x <= maxX && y <= maxY) {
            if (y + 1 > maxY) {
                return false;
            }

            // check below
            if (!(map[y + 1][x] instanceof Blocking)) {
                y++;
                continue;
            }

            // check to the left
            if (x - 1 < minX) {
                return false;
            }

            if (!(map[y + 1][x - 1] instanceof Blocking)) {
                x--;
                y++;
                continue;
            }

            // check to the right
            if (x + 1 > maxX) {
                return false;
            }

            if (!(map[y + 1][x + 1] instanceof Blocking)) {
                x++;
                y++;
                continue;
            }
            map[y][x] = new Sand("o", x, y);
            return true;
        }
        return false;
    }

    private static boolean letSandFall2(Pixel[][] map, SandEmitter emitter, int minX, int maxX, int maxY) {
        int x = emitter.getX() - minX;
        int y = emitter.getY();

        maxX -= minX;
        minX = 0;

        while (x >= minX && x <= maxX && y <= maxY) {
            if (y + 1 > maxY) {
                return false;
            }

            // check below
            if (!(map[y + 1][x] instanceof Blocking)) {
                y++;
                continue;
            }

            // check to the left
            if (x - 1 < minX) {
                return false;
            }

            if (!(map[y + 1][x - 1] instanceof Blocking)) {
                x--;
                y++;
                continue;
            }

            // check to the right
            if (x + 1 > maxX) {
                return false;
            }

            if (!(map[y + 1][x + 1] instanceof Blocking)) {
                x++;
                y++;
                continue;
            }
            if (map[y][x] instanceof SandEmitter) {
                return false;
            }
            map[y][x] = new Sand("o", x, y);
            return true;
        }
        return false;
    }


    private static void printMap(Pixel[][] map) {
        Arrays.stream(map).forEach(arr -> {
            Arrays.stream(arr)
                    .map(pixel -> Optional.ofNullable(pixel)
                            .map(Pixel::getContent)
                            .orElse("."))
                    .forEach(System.out::print);
            System.out.println();
        });
    }

    private static Set<Pixel> parseSolids(BufferedReader reader) {
        return reader.lines()
                .map(line -> line.split(" -> "))
                .map(Day14::createSolids)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private static Set<Solid> createSolids(String[] coords) {
        List<Solid> corners = Stream.of(coords)
                .map(pair -> pair.split(","))
                .map(pair -> new Solid("#", Integer.parseInt(pair[0]), Integer.parseInt(pair[1])))
                .toList();

        Set<Solid> betweens = new HashSet<>();

        for (int i = 0; i < corners.size() - 1; i++) {
            Solid current = corners.get(i);
            Solid next = corners.get(i+1);
            if (current.getX() == next.getX()) {
                int min = Math.min(current.getY(), next.getY());
                int max = Math.max(current.getY(), next.getY());
                for (int j = min; j < max; j++) {
                    betweens.add(new Solid("#", current.getX(), j));
                }
            } else {
                int min = Math.min(current.getX(), next.getX());
                int max = Math.max(current.getX(), next.getX());
                for (int j = min; j < max; j++) {
                    betweens.add(new Solid("#", j, current.getY()));
                }
            }
        }
        betweens.addAll(corners);
        return betweens;
    }

    private interface Pixel {
        String getContent();

        int getX();
        int getY();

    }

    private record SandEmitter(String content, int x, int y) implements Pixel {
        @Override
        public String getContent() {
            return content;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return content;
        }
    }


    private static class Sand implements Pixel, Blocking{
        private final String content;
        private int x;
        private int y;

        public Sand(String content, int x, int y) {
            this.content = content;
            this.x = x;
            this.y = y;
        }

        public String getContent() {
            return content;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return content;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Pixel pixel) {
                return this.x == pixel.getX() && this.y == pixel.getY();
            }
            return false;
        }
    }

    private record Solid(String content, int x, int y) implements Pixel, Blocking {

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    private interface Blocking {
    }
}
