package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class Day2 {
    public static void main(String[] args) throws URISyntaxException {
        Day2 day2 = new Day2();
        Path path = Paths.get(day2.getClass().getClassLoader().getResource("input2_2.txt").toURI());

        part1(day2, path);
        part2(day2, path);
    }

    private static void part1(Day2 day2, Path path) {
        Optional<Integer> result;
        HashMap<String, Choice> choiceMap = prepareChoiceMap();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            result = reader.lines()
                    .map(line -> line.split(" "))
                    .map(split -> choiceMap.get(split[1])
                            .getScore() + day2.getFightScore(choiceMap.get(split[1]), choiceMap.get(split[0])))
                    .reduce(Integer::sum);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
    }

    private static void part2(Day2 day2, Path path) {
        Optional<Integer> result;
        HashMap<String, Choice> choiceMap = prepareChoiceMap();
        HashMap<String, Result> resultMap = prepareResultMap();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            result = reader.lines()
                    .map(line -> line.split(" "))
                    .map(split -> day2.getFightScorePart2(choiceMap.get(split[0]), resultMap.get(split[1])))
                    .reduce(Integer::sum);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
    }

    private static HashMap<String, Choice> prepareChoiceMap() {
        HashMap<String, Choice> map = new HashMap<>();
        map.put("A", Choice.ROCK);
        map.put("B", Choice.PAPER);
        map.put("C", Choice.SCISSORS);
        map.put("X", Choice.ROCK);
        map.put("Y", Choice.PAPER);
        map.put("Z", Choice.SCISSORS);
        return map;
    }

    private static HashMap<String, Result> prepareResultMap() {
        HashMap<String, Result> map = new HashMap<>();
        map.put("X", Result.LOSE);
        map.put("Y", Result.DRAW);
        map.put("Z", Result.WIN);
        return map;
    }

    private Integer getFightScore(Choice playerChoice, Choice opponentChoice) {
        int result = 0;
        if (playerChoice.equals(opponentChoice)) {
            return 3;
        }
        switch (playerChoice) {
            case ROCK -> result = opponentChoice.equals(Choice.PAPER) ? 0 : 6;
            case PAPER -> result = opponentChoice.equals(Choice.SCISSORS) ? 0 : 6;
            case SCISSORS -> result = opponentChoice.equals(Choice.ROCK) ? 0 : 6;
        }
        return result;
    }

    private Integer getFightScorePart2(Choice opponentChoice, Result gameResult) {
        int result = getChoice(opponentChoice, gameResult);

        result += switch (gameResult) {
            case LOSE -> 0;
            case DRAW -> 3;
            case WIN -> 6;
        };

        return result;
    }

    private int getChoice(Choice opponentChoice, Result gameResult) {
        if (gameResult.equals(Result.DRAW)) {
            return opponentChoice.getScore();
        }
        return switch (opponentChoice) {
            case ROCK -> gameResult.equals(Result.WIN) ? Choice.PAPER.getScore() : Choice.SCISSORS.getScore();
            case PAPER -> gameResult.equals(Result.WIN) ? Choice.SCISSORS.getScore() : Choice.ROCK.getScore();
            case SCISSORS -> gameResult.equals(Result.WIN) ? Choice.ROCK.getScore() : Choice.PAPER.getScore();
        };
    }

    private enum Choice {
        ROCK(1), PAPER(2), SCISSORS(3);

        private final int score;

        Choice(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }

    private enum Result {
        LOSE, DRAW, WIN
    }
}


