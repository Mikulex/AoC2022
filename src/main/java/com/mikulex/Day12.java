package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class Day12 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day12.class.getClassLoader().getResource("input12_12.txt").toURI());
        Node[][] heightMap;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
             heightMap = parseHeightMap(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Node start = null;
        Node end = null;

        for (int x = 0; x < heightMap.length && (start == null || end == null); x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                if (heightMap[x][y].character == 'S') {
                    start = heightMap[x][y];
                }
                if (heightMap[x][y].character == 'E') {
                    end = heightMap[x][y];
                }
            }
        }

        // part 1
        int pathLength = searchShortestPath(start, end, heightMap);
        System.out.println(pathLength);

    }

    private static int searchShortestPath(Node start, Node end, Node[][] heightMap) {
        Set<Node> closedSet = new HashSet<>();
        Queue<Node> openQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.currentCost));

        openQueue.add(start);
        start.currentCost = 0;

        while (!openQueue.isEmpty()) {
            Node currentNode = openQueue.poll();

            if (Objects.equals(currentNode, end)) {
                return getPathSize(end);
            }

            closedSet.add(currentNode);
            assert currentNode != null;
            checkNeighbours(currentNode, openQueue, closedSet, heightMap);

        }
        return -1;
    }

    private static int getPathSize(Node end) {
        int length = 0;
        while (end.best != null) {
            length++;
            end = end.best;
        }
        return length;
    }

    private static void checkNeighbours(Node currentNode, Queue<Node> openQueue, Set<Node> closedSet, Node[][] heightMap) {
        if (currentNode.x > 0) {
            Node testNode = heightMap[currentNode.x - 1][currentNode.y];
            if (!closedSet.contains(testNode)) {
                addToQueue(currentNode, openQueue, testNode);;
            }
        }
        if (currentNode.x < heightMap.length - 1) {
            Node testNode = heightMap[currentNode.x + 1][currentNode.y];
            if (!closedSet.contains(testNode)) {
                addToQueue(currentNode, openQueue, testNode);
            }
        }
        if (currentNode.y > 0) {
            Node testNode = heightMap[currentNode.x][currentNode.y - 1];
            if (!closedSet.contains(testNode)) {
                addToQueue(currentNode, openQueue, testNode);;
            }
        }
        if (currentNode.y < heightMap[0].length - 1) {
            Node testNode = heightMap[currentNode.x][currentNode.y + 1];
            if (!closedSet.contains(testNode)) {
                addToQueue(currentNode, openQueue, testNode);
            }
        }
    }

    private static void addToQueue(Node currentNode, Queue<Node> openQueue, Node testNode) {
        int diff = Math.max(testNode.size - currentNode.size, 1);
        if (diff > 1) {
            return;
        }

        int newCost = currentNode.currentCost + 1 + diff;
        if (openQueue.contains(testNode) && newCost > testNode.currentCost) {
            return;
        }
        testNode.best = currentNode;
        testNode.currentCost = newCost;
        if (!openQueue.contains(testNode)) {
            openQueue.add(testNode);
        }
    }

    private static Node[][] parseHeightMap(BufferedReader reader) throws IOException {
        Node[][] heightMap = reader.lines()
                .map(line -> line.chars()
                        .mapToObj(c -> (char) c)
                        .map(Node::new)
                        .toArray(Node[]::new))
                .toArray(Node[][]::new);

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                heightMap[x][y].x = x;
                heightMap[x][y].y = y;
            }
        }
        return heightMap;
    }

    private static class Node {
        int size;

        int x;
        int y;
        int currentCost;

        Character character;

        Node best;

        Node(Character c) {
            this.character = c;
            if (c >= 'a') {
                size = c - 'a';
            } else if (c == 'E') {
                size = 25;
            } else {
                size = 0;
            }

        }
    }
}
