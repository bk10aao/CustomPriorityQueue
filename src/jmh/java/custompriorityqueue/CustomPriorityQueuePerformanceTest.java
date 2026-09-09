package custompriorityqueue;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class CustomPriorityQueuePerformanceTest {

    @Param({"10000", "20000", "30000", "40000", "50000", "60000", "70000", "80000", "90000", "100000"})
    private int size;

    private Integer[] elements;
    private List<Integer> populatedList;
    private CustomPriorityQueue<Integer> customQueue;
    private Comparator<Integer> reverseComp;

    private int index;

    @Setup(Level.Trial)
    public void setupTrial() {
        elements = new Integer[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            elements[i] = random.nextInt();
        }

        populatedList = new ArrayList<>(size);
        Collections.addAll(populatedList, elements);

        reverseComp = Comparator.reverseOrder();
        index = 0;
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        customQueue = new CustomPriorityQueue<>(populatedList);
    }

    private int getNextElement() {
        return elements[Math.abs(index++ % size)];
    }


    @Benchmark
    public void testDefaultConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<Integer>());
    }

    @Benchmark
    public void testCapacityConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<Integer>(size));
    }

    @Benchmark
    public void testComparatorConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<>(reverseComp));
    }

    @Benchmark
    public void testCapacityAndComparatorConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<>(size, reverseComp));
    }

    @Benchmark
    public void testCollectionAndComparatorConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<>(populatedList, reverseComp));
    }

    @Benchmark
    public void testCollectionConstructor(Blackhole bh) {
        bh.consume(new CustomPriorityQueue<>(populatedList));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        bh.consume(customQueue.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        bh.consume(customQueue.isEmpty());
    }

    @Benchmark
    public void testPeek(Blackhole bh) {
        bh.consume(customQueue.peek());
    }

    @Benchmark
    public void testElement(Blackhole bh) {
        bh.consume(customQueue.element());
    }

    @Benchmark
    public void testOffer(Blackhole bh) {
        bh.consume(customQueue.offer(getNextElement()));
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        bh.consume(customQueue.add(getNextElement()));
    }

    @Benchmark
    public void testPoll(Blackhole bh) {
        bh.consume(customQueue.poll());
    }

    @Benchmark
    public void testRemoveHead(Blackhole bh) {
        bh.consume(customQueue.remove());
    }

    @Benchmark
    public void testRemoveObject(Blackhole bh) {
        bh.consume(customQueue.remove(getNextElement()));
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        bh.consume(customQueue.contains(getNextElement()));
    }

    @Benchmark
    public void testContainsAll(Blackhole bh) {
        bh.consume(customQueue.containsAll(populatedList.subList(0, Math.min(10, size))));
    }

    @Benchmark
    public void testAddAll(Blackhole bh) {
        CustomPriorityQueue<Integer> target = new CustomPriorityQueue<>();
        target.addAll(populatedList);
        bh.consume(target);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        customQueue.clear();
        bh.consume(customQueue);
    }

    @Benchmark
    public void testComparator(Blackhole bh) {
        bh.consume(customQueue.comparator());
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        bh.consume(customQueue.toArray());
    }

    @Benchmark
    public void testToArrayTyped(Blackhole bh) {
        bh.consume(customQueue.toArray(new Integer[0]));
    }

    @Benchmark
    public void testIterator(Blackhole bh) {
        Iterator<Integer> it = customQueue.iterator();
        while (it.hasNext()) {
            bh.consume(it.next());
        }
    }

    @Benchmark
    public void testRemoveAll(Blackhole bh) {
        CustomPriorityQueue<Integer> q = new CustomPriorityQueue<>(populatedList);
        bh.consume(q.removeAll(populatedList.subList(0, size / 2)));
    }

    @Benchmark
    public void testRetainAll(Blackhole bh) {
        CustomPriorityQueue<Integer> q = new CustomPriorityQueue<>(populatedList);
        bh.consume(q.retainAll(populatedList.subList(0, size / 2)));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CustomPriorityQueuePerformanceTest.class.getSimpleName())
                .measurementIterations(3)
                .warmupIterations(2)
                .forks(1)
                .result("CustomPriorityQueue_performance_results.csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        writeCustomCsv(results);
    }

    private static void writeCustomCsv(Collection<RunResult> results) {
        try (FileWriter writer = new FileWriter("CustomPriorityQueue_jmh_performance.csv")) {
            writer.write("Benchmark;Size;Score (ns/op)\n");
            for (RunResult result : results) {
                String benchmarkName = result.getParams().getBenchmark();
                String shortName = benchmarkName.substring(benchmarkName.lastIndexOf('.') + 1);

                double score = result.getPrimaryResult().getScore();
                String sizeVal = result.getParams().getParam("size");

                writer.write("\"" + shortName + "\";" + (sizeVal != null ? sizeVal : "N/A") + ";" + score + "\n");
            }
            System.out.println("JMH Performance report saved: CustomPriorityQueue_jmh_performance.csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}