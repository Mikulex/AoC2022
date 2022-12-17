package com.mikulex;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.lang.Long.signum;

public class Day15 {

    static final long ROW_OF_INTEREST = 2_000_000; // part 1
    static final long RANGE_MAX = 4_000_000; // part 2
    // static final long RANGE_MAX = 20; // part 2 TEST

    public static void main(String[] args) throws URISyntaxException {
        Path path = Path.of(Day15.class.getClassLoader().getResource("input15_15.txt").toURI());


        try (BufferedReader reader = Files.newBufferedReader(path)) {
            final var dataPoints = reader.lines()
                    .map(DataPoint::fromString)
                    .toList();
            final var sensors = dataPoints.stream()
                    .map(Sensor::fromDataPoint)
                    .toList();
            final Set<Long> xPositionsOfBeaconsOnRow = getXPositionsOfBeaconsOnRow(ROW_OF_INTEREST, dataPoints);

            // part 1
            Range range = calculateCoverage(sensors, ROW_OF_INTEREST);
            long result = range.size() - xPositionsOfBeaconsOnRow.stream()
                    .filter(range::contains)
                    .count();
            System.out.println("Result for part 1: " + result);

            // part 2
            LongStream.range(0, RANGE_MAX + 1)
                    .forEach(index -> calculateCoverage(sensors, index));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<Long> getXPositionsOfBeaconsOnRow(long rowOfInterest, List<DataPoint> dataPoints) {
        return dataPoints.stream()
                .filter(dp -> dp.beaconPos.y() == rowOfInterest)
                .map(dp -> dp.beaconPos.x())
                .collect(Collectors.toSet());
    }

    private static Range calculateCoverage(List<Sensor> dataPoints, long row) {
        try {
            return dataPoints.stream()
                    .filter(sensor -> sensor.coversRow(row))
                    .map(sensor -> sensor.coveredXCoordinatesOn(row))
                    .sorted((left, right) -> signum(left.min() - right.min()))
                    .reduce(Range::union)
                    .orElseThrow();
        } catch (DisjointRangeException e) {
            throw new RuntimeException("missing tuning frequency: " + computeTuningFrequency(e.missingIndex, row));
        }
    }

    private static long computeTuningFrequency(long x, long y) {
        return x * 4_000_000 + y;
    }
}

class DataPoint {
    Position sensorPos;
    Position beaconPos;

    public DataPoint(Position sensorPos, Position beaconPos) {
        this.sensorPos = sensorPos;
        this.beaconPos = beaconPos;
    }

    static DataPoint fromString(String string) {
        Matcher matcher = Pattern.compile("-?\\d+,\\sy=-?\\d+").matcher(string);
        List<Position> data = matcher.results()
                .map(MatchResult::group)
                .map(match -> match.split(",\\sy="))
                .map(split -> new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1])))
                .toList();

        return new DataPoint(data.get(0), data.get(1));
    }
}

record Position(long x, long y) {
    long manhattan_distance(Position other) {
        return Math.abs(this.x() - other.x()) + Math.abs(this.y - other.y);
    }
}

class Sensor {
    Position pos;
    long radius;

    static Sensor fromDataPoint(DataPoint dataPoint) {
        final var sensor = new Sensor();
        sensor.pos = dataPoint.sensorPos;
        sensor.radius = sensor.pos.manhattan_distance(dataPoint.beaconPos);
        return sensor;
    }

    boolean coversRow(long row) {
        return pos.y() - radius <= row && row <= pos.y() + radius;

    }

    Range coveredXCoordinatesOn(long row) {
        final var distanceToLine = Math.abs(this.pos.y() - row);
        final var minX = this.pos.x() - (radius - distanceToLine);
        final var maxX = this.pos.x() + (radius - distanceToLine);
        return new Range(minX, maxX);
    }

}

record Range(long min, long max) {

    public Range clamp(long min, long max) {
        return new Range(Math.max(this.min, min), Math.min(this.max, max));
    }

    public Range union(Range other) {
        throwIfDisjointWith(other);
        return new Range(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
    }

    private void throwIfDisjointWith(Range other) {
        // here, we assume that there will always be at most 1 square between the ranges, because the task states that
        // there is only a single square missing on the entire map

        final var thisLeftOfOther = this.max() < other.min() && Math.abs(this.max() - other.min()) > 1;

        final var otherLeftOfThis = other.max() < this.min() && Math.abs(other.max() - this.min()) > 1;

        if (thisLeftOfOther) {
            throw new DisjointRangeException(this.max + 1);
        } else if (otherLeftOfThis) {
            throw new DisjointRangeException(other.max + 1);
        }
    }

    long size() {
        return max - min + 1;
    }

    boolean contains(long data) {
        return data >= min() && data <= max();
    }
}

class DisjointRangeException extends RuntimeException {
    long missingIndex;

    DisjointRangeException(long missingIndex) {
        this.missingIndex = missingIndex;
    }
}