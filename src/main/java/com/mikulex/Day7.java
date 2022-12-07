package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day7 {
    public static final int THRESHOLD = 100000;
    public static final int MAX_SIZE = 70000000;
    public static final int NEEDED_SIZE = 30000000;


    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day7.class.getClassLoader().getResource("input7_7.txt").toURI());

        solve(path);
    }

    public static void solve(Path path) {
        Context context = new Context();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            reader.lines().forEach(line -> {
                COMMAND cmd = parseCommand(line);

                switch (cmd) {
                    case LS -> { // do nothing and read following line
                    }
                    case CD -> changeDirectory(context, line);
                    case NONE -> parseFile(context, line);
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        context.currentDir = context.root;
        // part1
        List<Integer> dirsUnderThreshold = visit(context.root, THRESHOLD);
        System.out.println(dirsUnderThreshold.stream().mapToInt(Integer::intValue).sum());

        // part2
        int unusedSpace = MAX_SIZE - collectSize(context.root);
        int toDelete = NEEDED_SIZE - unusedSpace;
        List<Integer> candidates = visit(context.root, MAX_SIZE);
        System.out.println(candidates.stream()
                                   .sorted(Integer::compareTo)
                                   .filter(size -> size > toDelete)
                                   .findFirst());
    }

    private static List<Integer> visit(File currentFile, int limit) {
        List<Integer> result = new ArrayList<>();
        int dirSize = collect(currentFile);
        if (dirSize < limit) {
            result.add(dirSize);
        }

        result.addAll(currentFile.filesInDir.values().stream()
                              .filter(file -> file.isDir)
                              .map(file -> visit(file, limit))
                              .flatMap(Collection::stream).toList());
        return result;
    }

    private static int collect(File currentFile) {
        return currentFile.filesInDir.values().stream().mapToInt(Day7::collectSize).sum();
    }

    private static int collectSize(File file) {
        return file.isDir
                ? file.filesInDir.values().stream().mapToInt(Day7::collectSize).sum()
                : file.size;
    }

    private static void parseFile(Context context, String line) {
        String[] split = line.split(" ");
        if (split[0].equals("dir")) {
            context.currentDir.filesInDir.put(split[1], new File(true, 0, context.currentDir, new HashMap<>()));
        } else {
            context.currentDir.filesInDir.put(split[1], new File(false, Integer.parseInt(split[0]), context.currentDir, null));
        }
    }

    private static COMMAND parseCommand(String line) {
        if (line.startsWith("$ cd")) {
            return COMMAND.CD;
        } else if (line.startsWith("$ ls")) {
            return COMMAND.LS;
        } else {
            return COMMAND.NONE;
        }
    }

    private static void changeDirectory(Context context, String line) {
        String dir = line.replace("$ cd ", "");
        if (dir.equals("/")) {
            context.currentDir = context.root;
        } else if (dir.equals("..")) {
            context.currentDir = context.currentDir.parent;
        } else {
            context.currentDir = context.currentDir.filesInDir.get(dir);
        }
    }

    private enum COMMAND {
        LS, CD, NONE
    }

    private static class Context {
        File root = new File();
        File currentDir = root;
    }

    private static class File {
        boolean isDir;
        int size;
        File parent;

        Map<String, File> filesInDir;

        public File(boolean isDir, int size, File parent, Map<String, File> filesInDir) {
            this.isDir = isDir;
            this.size = size;
            this.parent = parent;
            this.filesInDir = filesInDir;
        }

        File() {
            isDir = true;
            filesInDir = new HashMap<>();
        }
    }

}
