package custompriorityqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomPriorityQueueTest {

    @Test
    @DisplayName("Default constructor creates an empty queue")
    void testDefaultConstructor() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("Initial capacity constructor creates an empty queue")
    void testCapacityConstructor() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(50);
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    @Test
    @DisplayName("add() updates size and sets root element")
    void testAddSingleElement() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.add(42));
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());
        assertEquals(42, queue.peek());
    }

    @Test
    @DisplayName("add() and poll() maintain correct heap order across multiple inserts")
    void testAddAndPollMultipleElements() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        List<Integer> items = Arrays.asList(30, 8, 40, 15, 2, 80, 50, 1, 90, 20);

        for (Integer item : items) {
            queue.add(item);
        }

        assertEquals(items.size(), queue.size());
        assertEquals(1, queue.peek()); // Minimum element sits at root

        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty()) {
            extracted.add(queue.poll());
        }

        assertEquals(Arrays.asList(1, 2, 8, 15, 20, 30, 40, 50, 80, 90), extracted);
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("Collection constructor builds a valid heap that drains in sorted order")
    void testCollectionConstructor() {
        List<Integer> input = Arrays.asList(45, 12, 89, 3, 27, 6, 71, 100, 1, 18);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(input);

        assertEquals(input.size(), queue.size());
        assertEquals(1, queue.peek());

        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty()) {
            extracted.add(queue.poll());
        }

        assertEquals(Arrays.asList(1, 3, 6, 12, 18, 27, 45, 71, 89, 100), extracted);
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("Handles duplicate values and negative integers correctly")
    void testDuplicatesAndNegativeNumbers() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        List<Integer> items = Arrays.asList(-5, 10, -5, 0, 20, -15, 10);

        for (Integer item : items) {
            queue.offer(item);
        }

        assertEquals(items.size(), queue.size());
        assertEquals(-15, queue.peek());

        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty()) {
            extracted.add(queue.poll());
        }

        assertEquals(Arrays.asList(-15, -5, -5, 0, 10, 10, 20), extracted);
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("clear() empties the queue completely")
    void testClear() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertFalse(queue.isEmpty());

        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.poll());
    }
}