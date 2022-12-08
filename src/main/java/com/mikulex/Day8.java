package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Day8 {
    public static int MAX = 99;

    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day8.class.getClassLoader().getResource("input8_8.txt").toURI());

        solve(path);
    }

    public static void solve(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            Tree[][] trees = new Tree[MAX][MAX];
            Set<Tree> visibleTrees = new HashSet<>();
            parseFile(reader, trees);

            checkHorizontalVisibility(trees, visibleTrees);
            checkVerticalVisibility(trees, visibleTrees);
            System.out.println(visibleTrees.size());

            int max = Stream.of(trees)
                    .flatMap(Stream::of)
                    .mapToInt(tree -> getScenicScore(tree, trees))
                    .max().orElse(0);

            System.out.println(max);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static int getScenicScore(Tree tree, Tree[][] trees) {
        if (isAtBorder(tree)) {
            return 0;
        }
        return countTreesFrom(tree, trees, Direction.LEFT)
                * countTreesFrom(tree, trees, Direction.RIGHT)
                * countTreesFrom(tree, trees, Direction.UP)
                * countTreesFrom(tree, trees, Direction.DOWN);
    }

    private static int countTreesFrom(Tree tree, Tree[][] trees, Direction direction) {
        int count = 0;
        switch (direction) {
            case LEFT -> {
                for (int i = tree.y - 1; i >= 0; i--) {
                    if (trees[tree.x][i].height < tree.height) {
                        count++;
                    } else {
                        return count + 1;
                    }
                }
            }
            case RIGHT -> {
                for (int i = tree.y + 1; i < MAX; i++) {
                    if (trees[tree.x][i].height < tree.height) {
                        count++;
                    } else {
                        return count + 1;
                    }
                }
            }
            case UP -> {
                for (int i = tree.x - 1; i >= 0; i--) {
                    if (trees[i][tree.y].height < tree.height) {
                        count++;
                    } else {
                        return count + 1;
                    }
                }
            }
            case DOWN -> {
                for (int i = tree.x + 1; i < MAX; i++) {
                    if (trees[i][tree.y].height < tree.height) {
                        count++;
                    } else {
                        return count + 1;
                    }
                }
            }
        }
        return count;

    }

    private static void checkHorizontalVisibility(Tree[][] trees, Set<Tree> visibleTrees) {
        for (var horizontalLine : trees) {
            int lineMax = 0;
            for (int y = 0; y < MAX; y++) {
                if (isAtBorder(horizontalLine[y]) || horizontalLine[y].height > lineMax) {
                    visibleTrees.add(horizontalLine[y]);
                    lineMax = Math.max(horizontalLine[y].height, lineMax);
                }
            }
            int currentMax = 0;
            for (int y = MAX - 1; y >= 0 && currentMax < lineMax; y--) {
                if (isAtBorder(horizontalLine[y]) || horizontalLine[y].height > currentMax) {
                    visibleTrees.add(horizontalLine[y]);
                    currentMax = Math.max(horizontalLine[y].height, currentMax);
                }
            }
        }
    }

    private static boolean isAtBorder(Tree tree) {
        return tree.x == 0 || tree.y == 0 || tree.x == MAX - 1 || tree.y == MAX - 1;
    }

    private static void checkVerticalVisibility(Tree[][] trees, Set<Tree> visibleTrees) {
        for (int y = 0; y < MAX; y++) {
            int lineMax = 0;
            for (int x = 0; x < MAX; x++) {
                if (isAtBorder(trees[x][y]) || trees[x][y].height > lineMax) {
                    visibleTrees.add(trees[x][y]);
                    lineMax = Math.max(trees[x][y].height, lineMax);
                }
            }
            int currentMax = 0;
            for (int x = MAX - 1; x >= 0 && currentMax < lineMax; x--) {
                if (isAtBorder(trees[x][y]) || trees[x][y].height > currentMax) {
                    visibleTrees.add(trees[x][y]);
                    currentMax = Math.max(trees[x][y].height, currentMax);
                }
            }
        }
    }

    private static void parseFile(BufferedReader reader, Tree[][] trees) throws IOException {
        for (int x = 0; x < MAX; x++) {
            char[] chars = reader.readLine().toCharArray();

            for (int y = 0; y < MAX; y++) {
                trees[x][y] = new Tree(x, y, Integer.parseInt(String.valueOf(chars[y])));
            }
        }
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private static class Tree {
        int x;
        int y;

        int height;

        public Tree(int x, int y, int height) {
            this.x = x;
            this.y = y;
            this.height = height;
        }
    }

}
