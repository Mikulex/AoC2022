package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Day9 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day9.class.getClassLoader().getResource("input9_9.txt").toURI());


        List<Node> part1Tail = new ArrayList<>();
        part1Tail.add(new Node(0, 0));

        List<Node> part2Tail = new ArrayList<>();
        IntStream.range(0,9).forEach(i -> part2Tail.add(new Node(0,0)));
        solve(path, part1Tail);
        solve(path, part2Tail);
    }

    public static void solve(Path path, List<Node> tails) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Set<Node> visited = new HashSet<>();
            visited.add(new Node(0, 0));

            Node head = new Node(0, 0);

            reader.lines()
                    .map(line -> line.split(" "))
                    .forEach(split -> {
                        move(split, head, tails, visited);
                    });
            System.out.println(visited.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void move(String[] split, Node head, List<Node> tails, Set<Node> visited) {
        int steps = Integer.parseInt(split[1]);
        switch (split[0]) {
            case "L" -> {
                updateHead(h -> h.setX(h.getX() - 1), steps, head, tails, visited);
            }
            case "R" -> {
                updateHead(h -> h.setX(h.getX() + 1), steps, head, tails, visited);
            }
            case "U" -> {
                updateHead(h -> h.setY(h.getY() + 1), steps, head, tails, visited);
            }
            case "D" -> {
                updateHead(h -> h.setY(h.getY() - 1), steps, head, tails, visited);
            }
        }

    }

    public static void updateHead(Consumer<Node> update, int steps, Node head, List<Node> tails, Set<Node> visited) {
        for (int i = 0; i < steps; i++) {
            update.accept(head);
            updateTails(head, tails);
            visited.add(new Node(tails.get(tails.size()-1).getX(), tails.get(tails.size()-1).getY()));
        }
    }

    public static void updateTails(Node head, List<Node> tails) {
        if (head.manhattanDistanceTo(tails.get(0)) == 2) {
            updateInLine(head, tails.get(0));
        } else if (head.manhattanDistanceTo(tails.get(0)) == 3) {
            updateDiagonally(head, tails.get(0));
        }
        for (int i = 0; i < tails.size()-1; i++) {
            if (tails.get(i).manhattanDistanceTo(tails.get(i+1)) == 2) {
                updateInLine(tails.get(i), tails.get(i+1));
            } else if (tails.get(i).manhattanDistanceTo(tails.get(i+1)) >= 3) {
                updateDiagonally(tails.get(i), tails.get(i+1));
            }
        }
    }

    private static void updateDiagonally(Node head, Node tail) {
        if (head.getX() > tail.getX()) {
            tail.setX(tail.getX() + 1);

        } else {
            tail.setX(tail.getX() - 1);

        }
        if (head.getY() > tail.getY()) {
            tail.setY(tail.getY() + 1);
        } else {
            tail.setY(tail.getY() - 1);
        }
    }

    private static void updateInLine(Node head, Node tail) {
        if (head.getX() == tail.getX()) {
            if (head.getY() > tail.getY()) {
                tail.setY(tail.getY() + 1);
            } else {
                tail.setY(tail.getY() - 1);
            }
        } else if (head.getY() == tail.getY()) {
            if (head.getX() > tail.getX()) {
                tail.setX(tail.getX() + 1);
            } else {
                tail.setX(tail.getX() - 1);
            }
        }
    }

    public static final class Node{
        private int x;
        private int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int manhattanDistanceTo(Node other) {
                return Math.abs(other.x - this.x) + Math.abs(other.y - this.y);
            }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node node = (Node) o;

            if (x != node.x) {
                return false;
            }
            return y == node.y;
        }

        @Override
        public int hashCode() {
            return 31 * x*x*x*x*x + y ;
        }
    }
}
