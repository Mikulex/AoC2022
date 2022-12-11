package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day10.class.getClassLoader().getResource("input11_11.txt").toURI());
        solve1(path);
        solve2(path);
    }

    private static void solve1(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String currentLine;
            List<Monkey> monkeyList = new ArrayList<>();

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith("Monkey ")) {
                    Monkey monkey = new Monkey();
                    monkey.setMonkeyList(monkeyList);

                    setHoldingItems(reader, monkey);
                    setWorryLevelModifierPart1(reader, monkey);
                    setDivisor(reader, monkey);
                    setTargetMonkeys(reader, monkey);
                    monkeyList.add(monkey);
                }
            }

            monkeyList.forEach(monkey -> monkey.setModulo(monkeyList.stream().mapToInt(Monkey::getDivisor).reduce(1, (a,b) -> a*b)));

            // part 1
            for (int i = 0; i < 20; i++) {
                monkeyList.forEach(Monkey::startTurn);
            }

            BigDecimal monkeyBusiness = calculateMonkeyBusiness(monkeyList);

            System.out.println(monkeyBusiness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void solve2(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String currentLine;
            List<Monkey> monkeyList = new ArrayList<>();

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith("Monkey ")) {
                    Monkey monkey = new Monkey();
                    monkey.setMonkeyList(monkeyList);

                    setHoldingItems(reader, monkey);
                    setWorryLevelModifierPart2(reader, monkey);
                    setDivisor(reader, monkey);
                    setTargetMonkeys(reader, monkey);
                    monkeyList.add(monkey);
                }
            }

            monkeyList.forEach(monkey -> monkey.setModulo(monkeyList.stream().mapToInt(Monkey::getDivisor).reduce(1, (a,b) -> a*b)));


            for (int i = 0; i < 10000; i++) {
                monkeyList.forEach(Monkey::startTurn);
            }

            BigDecimal monkeyBusiness = calculateMonkeyBusiness(monkeyList);

            System.out.println(monkeyBusiness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BigDecimal calculateMonkeyBusiness(List<Monkey> monkeyList) {
        return monkeyList.stream()
                .map(Monkey::getInspectedCount)
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .limit(2)
                .mapToObj(BigDecimal::new)
                .reduce(BigDecimal.ONE, BigDecimal::multiply);
    }

    private static void setTargetMonkeys(BufferedReader reader, Monkey monkey) throws IOException {
        Pattern pattern = Pattern.compile("\\d+");

        Matcher matcher = pattern.matcher(reader.readLine());
        if (matcher.find()) {
            monkey.setTargetIfTrue(Integer.parseInt(matcher.group()));
        }

        matcher = pattern.matcher(reader.readLine());
        if (matcher.find()) {
            monkey.setTargetIfFalse(Integer.parseInt(matcher.group()));
        }

    }

    private static void setDivisor(BufferedReader reader, Monkey monkey) throws IOException {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(reader.readLine());
        if (matcher.find()) {
            monkey.setDivisor(Integer.parseInt(matcher.group()));
        }
    }

    private static void setWorryLevelModifierPart1(BufferedReader reader, Monkey monkey) throws IOException {
        Matcher matcher = Pattern.compile("[+*]+\\W\\d+").matcher(reader.readLine());
        if (matcher.find()) {
            String[] operationParts = matcher.group().split(" ");
            int operand = Integer.parseInt(operationParts[1]);
            if (operationParts[0].equals("+")) {
                monkey.setWorryLevelOperation(level -> (level + operand) / 3 );
            } else {
                monkey.setWorryLevelOperation(level -> (level * operand) / 3);
            }
        } else {
            monkey.setWorryLevelOperation(level -> (level * level) / 3 );
        }
    }

    private static void setWorryLevelModifierPart2(BufferedReader reader, Monkey monkey) throws IOException {
        Matcher matcher = Pattern.compile("[+*]+\\W\\d+").matcher(reader.readLine());
        if (matcher.find()) {
            String[] operationParts = matcher.group().split(" ");
            int operand = Integer.parseInt(operationParts[1]);
            if (operationParts[0].equals("+")) {
                monkey.setWorryLevelOperation(level -> (level + operand));
            } else {
                monkey.setWorryLevelOperation(level -> (level * operand));
            }
        } else {
            monkey.setWorryLevelOperation(level -> (level * level));
        }
    }

    private static void setHoldingItems(BufferedReader reader, Monkey monkey) throws IOException {
        String[] parsedItems = reader.readLine().strip()
                .replace("Starting items: ", "")
                .replace(",", "")
                .split(" ");
        Deque<Long> holdingItems = Arrays.stream(parsedItems)
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));
        monkey.setHoldingItems(holdingItems);
    }

    private static class Monkey {
        private int inspectedCount;
        private Deque<Long> holdingItems;
        private UnaryOperator<Long> worryLevelOperation;

        private List<Monkey> monkeyList;

        private int modulo;

        private int divisor;
        private int targetIfTrue;
        private int targetIfFalse;

        public void startTurn() {
            while (!holdingItems.isEmpty()) {
                long currentItem = worryLevelOperation.apply(holdingItems.removeFirst()) % modulo;

                Monkey targetMonkey = monkeyList.get(currentItem % divisor == 0 ? targetIfTrue : targetIfFalse);
                targetMonkey.holdingItems.addLast(currentItem);

                inspectedCount++;
            }
        }

        public int getInspectedCount() {
            return inspectedCount;
        }

        public void setHoldingItems(Deque<Long> holdingItems) {
            this.holdingItems = holdingItems;
        }

        public void setWorryLevelOperation(UnaryOperator<Long> worryLevelOperation) {
            this.worryLevelOperation = worryLevelOperation;
        }

        public void setTargetIfTrue(int targetIfTrue) {
            this.targetIfTrue = targetIfTrue;
        }

        public void setTargetIfFalse(int targetIfFalse) {
            this.targetIfFalse = targetIfFalse;
        }

        public void setDivisor(int divisor) {
            this.divisor = divisor;
        }

        public void setMonkeyList(List<Monkey> monkeyList) {
            this.monkeyList = monkeyList;
        }

        public void setModulo(int modulo) {
            this.modulo = modulo;
        }

        public int getDivisor() {
            return this.divisor;
        }
    }


}
