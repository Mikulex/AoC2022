package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Day13 {
    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day13.class.getClassLoader().getResource("input13_13.txt").toURI());
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<Packet> packets = new ArrayList<>(reader.lines()
                                                           .filter(s -> !s.isBlank())
                                                           .map(Day13::parsePacket)
                                                           .toList());

            // part 1
            int sum = 0;
            for (int i = 0; i < packets.size(); i+= 2) {
                sum += packets.get(i).compareTo(packets.get(i + 1)) <= 0 ? (i/2)+1 : 0;
            }
            System.out.println(sum);
            Packet divider1 = prepareDividerPacket(2);
            Packet divider2 = prepareDividerPacket(6);
            packets.add(divider1);
            packets.add(divider2);

            packets.sort(Packet::compareTo);
            System.out.println((packets.indexOf(divider1) + 1) * (packets.indexOf(divider2) + 1));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Packet prepareDividerPacket(int content) {
        Packet divider = new Packet();
        Entry<List<Entry<Integer>>> dividerEntry = new Entry<>();
        Entry<Integer> dividerInteger = new Entry<>();
        dividerInteger.setContent(content);
        dividerEntry.setContent(List.of(dividerInteger));
        divider.entries.add(dividerEntry);
        return divider;
    }

    private static Packet parsePacket(String s) {
        List<Entry<?>> packetEntries = new ArrayList<>();
        s = s.substring(1, s.length() - 1); // remove brackets around the whole packet

        parseEntries(s, packetEntries);

        Packet packet = new Packet();
        packet.setEntries(packetEntries);
        return packet;
    }


    private static void parseEntries(String packetString, List<Entry<?>> packetEntries) {
        Stack<Entry<List<Entry<?>>>> entryStack = new Stack<>();
        for (int i = 0; i < packetString.length(); i++) {
            char currentChar = packetString.charAt(i);
            if (currentChar == '[') {
                pushToStack(entryStack);
            } else if (currentChar == ']') {
                addFinishedSublistToEntryOrPacket(packetEntries, entryStack);
            } else if (currentChar != ',') {
                i = parseInteger(packetString, packetEntries, entryStack, i);
            }
        }
    }

    private static int parseInteger(
            String packetString, List<Entry<?>> packetEntries, Stack<Entry<List<Entry<?>>>> entryStack, int i) {
        Entry<Integer> entry = new Entry<>();
        int intEnd = i;
        while (intEnd < packetString.length() && Character.isDigit(packetString.charAt(intEnd))) {
            intEnd++;
        }
        entry.setContent(Integer.parseInt(packetString.substring(i, intEnd)));
        i = intEnd - 1;
        if (entryStack.isEmpty()) {
            packetEntries.add(entry);
        } else {
            entryStack.peek().getContent().add(entry);
        }
        return i;
    }

    private static void addFinishedSublistToEntryOrPacket(List<Entry<?>> packetEntries, Stack<Entry<List<Entry<?>>>> entryStack) {
        Entry<List<Entry<?>>> currentEntry = entryStack.pop();
        if (entryStack.isEmpty()) {
            packetEntries.add(currentEntry);
        } else {
            entryStack.peek().getContent().add(currentEntry);
        }
    }

    private static void pushToStack(Stack<Entry<List<Entry<?>>>> entryStack) {
        Entry<List<Entry<?>>> entry = new Entry<>();
        entry.setContent(new ArrayList<>());
        entryStack.push(entry);
    }

    private static class Packet implements Comparable<Packet> {
        private List<Entry<?>> entries = new ArrayList<>();

        public List<Entry<?>> getEntries() {
            return entries;
        }

        public void setEntries(List<Entry<?>> entries) {
            this.entries = entries;
        }

        @Override
        public String toString() {
            return entries.toString();
        }

        @Override
        public int compareTo(Packet other) {
            int result = 0;

            for (int i = 0, j = 0; i < this.entries.size() && j < other.entries.size() && result == 0; i++, j++) {
                result = this.entries.get(i).compareTo(other.entries.get(i));
            }
            if (result != 0) {
                return result;
            } else {
                return this.entries.size() - other.entries.size();
            }
        }
    }

    private static class Entry<T> implements Comparable<Entry<?>>{
        private T content;

        private T getContent() {
            return this.content;
        }

        public void setContent(T content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content.toString();
        }

        @Override
        public int compareTo(Entry<?> other) {
            if (this.content instanceof Integer) {
                if (other.content instanceof Integer) {
                    return ((Integer) this.content).compareTo((Integer) other.content);
                } else if (other.content instanceof List) {
                    Entry<List<Entry<Integer>>> test = new Entry<>();
                    test.content = new ArrayList<>();
                    test.content.add((Entry<Integer>) this);
                    return test.compareTo(other);
                }
            } else if (this.content instanceof List){
                if (other.content instanceof Integer) {
                    return other.compareTo(this) * -1;
                } else if (other.content instanceof List) {
                    int result = 0;
                    List<Entry<?>> thisContentList = (List<Entry<?>>) this.content;
                    List<Entry<?>> otherContentList = (List<Entry<?>>) other.content;
                    for (int i = 0, j = 0; i < thisContentList.size() && j < otherContentList.size() && result == 0; i++, j++) {
                        result = thisContentList.get(i).compareTo(otherContentList.get(i));
                    }
                    if (result != 0) {
                        return result;
                    } else {
                        return thisContentList.size() - otherContentList.size();
                    }
                }
            }
            return 0;
        }
    }
}
