package custompriorityqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomPriorityQueueTest {

    @Test
    @DisplayName("Default constructor creates an empty queue")
    public void testDefaultConstructor() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("Initial capacity constructor creates an empty queue")
    public void testCapacityConstructor() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(50);
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
    }

    @Test
    @DisplayName("add() updates size and sets root element")
    public void testAddSingleElement() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.add(42));
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());
        assertEquals(42, queue.peek());
    }

    @Test
    @DisplayName("add() and poll() maintain correct heap order across multiple inserts")
    public void testAddAndPollMultipleElements() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        List<Integer> items = Arrays.asList(30, 8, 40, 15, 2, 80, 50, 1, 90, 20);
        for (Integer item : items)
            queue.add(item);
        assertEquals(items.size(), queue.size());
        assertEquals(1, queue.peek());
        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty())
            extracted.add(queue.poll());
        assertEquals(Arrays.asList(1, 2, 8, 15, 20, 30, 40, 50, 80, 90), extracted);
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("Collection constructor builds a valid heap that drains in sorted order")
    public void testCollectionConstructor() {
        List<Integer> input = Arrays.asList(45, 12, 89, 3, 27, 6, 71, 100, 1, 18);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(input);
        assertEquals(input.size(), queue.size());
        assertEquals(1, queue.peek());
        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty())
            extracted.add(queue.poll());
        assertEquals(Arrays.asList(1, 3, 6, 12, 18, 27, 45, 71, 89, 100), extracted);
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("Handles duplicate values and negative integers correctly")
    public void testDuplicatesAndNegativeNumbers() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        List<Integer> items = Arrays.asList(-5, 10, -5, 0, 20, -15, 10);
        for (Integer item : items) {
            queue.offer(item);
        }
        assertEquals(items.size(), queue.size());
        assertEquals(-15, queue.peek());
        List<Integer> extracted = new ArrayList<>();
        while (!queue.isEmpty())
            extracted.add(queue.poll());
        assertEquals(Arrays.asList(-15, -5, -5, 0, 10, 10, 20), extracted);
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("clear() empties the queue completely")
    public void testClear() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertFalse(queue.isEmpty());

        queue.clear();
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("remove(Object) on empty queue returns false")
    public void onRemove_fromEmptyQueue_returnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.remove(3));
    }

    @Test
    @DisplayName("remove(Object) when item is not present returns false")
    public void onRemove_itemNotPresent_returns_false() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertFalse(queue.remove(1_000_000));
    }

    @Test
    @DisplayName("remove(null) returns false")
    public void onRemove_null_returns_false() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertFalse(queue.remove(null));
    }

    @Test
    @DisplayName("remove(Object) when item is present returns true")
    public void onRemove_presentItem_returnsTrue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertTrue(queue.remove(3));
    }

    @Test
    @DisplayName("remove() on empty queue throws NoSuchElementException")
    public void onRemoveFirst_onEmptyQueue_throwsNoSuchElementException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertThrows(NoSuchElementException.class, queue::remove);
    }

    @Test
    @DisplayName("remove() on queue containing [1, 2, 3] returns 1")
    public void onRemove_queueOf_1_2_3_returns_1() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertEquals(1, queue.remove());
    }

    @Test
    @DisplayName("removeAll(null) throws NullPointerException")
    public void onRemoveAll_nullCollection_throwsNullPointerException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Arrays.asList(1, 2, 3));
        assertThrows(NullPointerException.class, () -> queue.removeAll(null));
    }

    @Test
    @DisplayName("removeAll(null) throws NullPointerException")
    public void testRemoveAll_NullCollection_ThrowsNullPointerException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertThrows(NullPointerException.class, () -> queue.removeAll(null));
    }

    @Test
    @DisplayName("removeAll on empty queue returns false")
    public void testRemoveAll_EmptyQueue_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.removeAll(List.of(10, 20, 30)));
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("removeAll with empty collection returns false and leaves queue untouched")
    public void testRemoveAll_EmptyCollection_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 10, 20));
        assertFalse(queue.removeAll(Collections.emptyList()));
        assertEquals(3, queue.size());
        assertEquals(10, queue.peek());
    }

    @Test
    @DisplayName("removeAll with no matching elements returns false and preserves heap")
    public void testRemoveAll_NoMatches_ReturnsFalseAndPreservesOrder() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(40, 15, 2, 80, 50));
        assertFalse(queue.removeAll(List.of(99, 100, 200)));
        assertEquals(5, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(2, 15, 40, 50, 80), polled);
    }

    @Test
    @DisplayName("removeAll removes subset of elements and rebuilds heap correctly")
    public void testRemoveAll_PartialMatches_RemovesElementsAndMaintainsHeapOrder() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 8, 40, 15, 2, 80, 50, 1, 90, 20));
        assertTrue(queue.removeAll(List.of(8, 2, 50, 999)));
        assertEquals(7, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(1, 15, 20, 30, 40, 80, 90), polled);
    }

    @Test
    @DisplayName("removeAll removes all occurrences of duplicate elements")
    public void testRemoveAll_WithDuplicates_RemovesAllOccurrences() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 5, 10, 20, 5, 30, 10));
        assertTrue(queue.removeAll(Set.of(10, 5)));
        assertEquals(2, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(20, 30), polled);
    }

    @Test
    @DisplayName("removeAll matching all queue elements empties queue")
    public void testRemoveAll_AllMatches_EmptiesQueue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30, 40));
        assertTrue(queue.removeAll(List.of(10, 20, 30, 40)));
        assertTrue(queue.isEmpty());
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("removeAll works identically whether input is a Set or a List")
    public void testRemoveAll_HandlesSetAndListInputs() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(50, 10, 30, 20, 40));
        Set<Integer> setTarget = Set.of(10, 40);
        assertTrue(queue.removeAll(setTarget));
        assertTrue(queue.removeAll(List.of(20)));
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(30, 50), polled);
    }

    @Test
    @DisplayName("removeAll modifies modCount and causes active iterator to throw ConcurrentModificationException")
    public void testRemoveAll_TriggersFailFastIterator() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30, 40));
        Iterator<Integer> it = queue.iterator();
        assertTrue(it.hasNext());
        queue.removeAll(List.of(40));
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("addAll(null) throws NullPointerException")
    public void testAddAll_NullCollection_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CustomPriorityQueue<>().addAll(null));
    }

    @Test
    @DisplayName("addAll(this) throws IllegalArgumentException")
    public void testAddAll_SelfAddition_ThrowsIllegalArgumentException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertThrows(IllegalArgumentException.class, () -> queue.addAll(queue));
    }

    @Test
    @DisplayName("addAll with empty collection returns false and leaves queue unchanged")
    public void testAddAll_EmptyCollection_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.addAll(Collections.emptyList()));
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("addAll with elements returns true and expands size correctly")
    public void testAddAll_ValidElements_ReturnsTrueAndUpdatesSize() {
        List<Integer> items = List.of(30, 10, 20);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.addAll(items));
        assertEquals(3, queue.size());
        assertEquals(10, queue.peek());
    }

    @Test
    @DisplayName("addAll supports subtype collections through upper-bounded wildcard (? extends E)")
    public void testAddAll_SubtypeCollection_SuccessfullyAdds() {
        List<Integer> integers = List.of(40, 15, 2, 80);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.addAll(integers));
        assertEquals(4, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(2, 15, 40, 80), polled);
    }

    @Test
    @DisplayName("addAll preserves min-max heap order after bulk insertions")
    public void testAddAll_MaintainsHeapOrder() {
        CustomPriorityQueue<Integer> intQueue = new CustomPriorityQueue<>();
        intQueue.add(25);
        List<Integer> unorderedBatch = List.of(30, 8, 40, 15, 2, 80, 50, 1, 90, 20);
        assertTrue(intQueue.addAll(unorderedBatch));
        assertEquals(11, intQueue.size());
        List<Integer> polled = drainQueue(intQueue);
        assertEquals(List.of(1, 2, 8, 15, 20, 25, 30, 40, 50, 80, 90), polled);
    }

    @Test
    @DisplayName("addAll handles duplicate items correctly")
    public void testAddAll_WithDuplicates_AddsAllOccurrences() {
        List<Integer> duplicates = List.of(10, 5, 10, 5, 20);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.addAll(duplicates));
        assertEquals(5, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(5, 5, 10, 10, 20), polled);
    }

    @Test
    @DisplayName("addAll from a Set populates elements into queue correctly")
    public void testAddAll_FromSetInput_AddsAllElements() {
        Set<Integer> set = Set.of(100, 20, 50);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.addAll(set));
        assertEquals(3, queue.size());

        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(20, 50, 100), polled);
    }

    @Test
    @DisplayName("addAll modifies modCount and triggers ConcurrentModificationException on active iterator")
    public void testAddAll_TriggersFailFastIterator() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20));
        Iterator<Integer> it = queue.iterator();

        assertTrue(it.hasNext());
        queue.addAll(List.of(30, 40));

        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("contains(null) returns false without throwing NullPointerException")
    public void testContains_NullInput_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.contains(null));
        queue.addAll(List.of(10, 20, 30));
        assertFalse(queue.contains(null));
    }

    @Test
    @DisplayName("contains on empty queue returns false")
    public void testContains_EmptyQueue_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.contains(10));
    }

    @Test
    @DisplayName("contains returns true for existing elements regardless of position in heap")
    public void testContains_ExistingElements_ReturnsTrue() {
        List<Integer> items = List.of(30, 8, 40, 15, 2, 80, 50, 1, 90, 20);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(items);
        for (Integer item : items)
            assertTrue(queue.contains(item), "Queue should contain element: " + item);
    }

    @Test
    @DisplayName("contains returns false for non-existent elements")
    public void testContains_NonExistentElement_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertFalse(queue.contains(5));
        assertFalse(queue.contains(15));
        assertFalse(queue.contains(100));
    }

    @Test
    @DisplayName("contains returns true when duplicate instances exist in queue")
    public void testContains_WithDuplicates_ReturnsTrue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(5, 10, 5, 20));
        assertTrue(queue.contains(5));
        assertTrue(queue.contains(10));
    }

    @Test
    @DisplayName("contains with incompatible object type returns false gracefully")
    public void testContains_IncompatibleType_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertFalse(queue.contains("String Object"));
        assertFalse(queue.contains(10.5));
    }

    @Test
    @DisplayName("contains returns false after element is polled from queue")
    public void testContains_AfterElementPolled_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertEquals(10, queue.poll());
        assertFalse(queue.contains(10));
        assertTrue(queue.contains(20));
        assertTrue(queue.contains(30));
    }

    @Test
    @DisplayName("containsAll(null) throws NullPointerException")
    public void testContainsAll_NullCollection_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CustomPriorityQueue<Integer>().containsAll(null));
    }

    @Test
    @DisplayName("containsAll with empty target collection returns true regardless of queue state")
    public void testContainsAll_EmptyTargetCollection_ReturnsTrue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertTrue(queue.containsAll(Collections.emptyList()));
        queue.addAll(List.of(10, 20, 30));
        assertTrue(queue.containsAll(Collections.emptySet()));
    }

    @Test
    @DisplayName("containsAll on empty queue with non-empty collection returns false")
    public void testContainsAll_EmptyQueueWithNonEmptyTarget_ReturnsFalse() {
        assertFalse(new CustomPriorityQueue<Integer>().containsAll(List.of(10, 20)));
    }

    @Test
    @DisplayName("containsAll returns true when all target elements exist in queue")
    public void testContainsAll_AllElementsPresent_ReturnsTrue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 8, 40, 15, 2, 80));

        assertTrue(queue.containsAll(List.of(8, 40, 2)));
        assertTrue(queue.containsAll(List.of(30, 8, 40, 15, 2, 80)));
    }

    @Test
    @DisplayName("containsAll returns false when at least one element is missing")
    public void testContainsAll_PartialElementsPresent_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30, 40));
        assertFalse(queue.containsAll(List.of(10, 20, 99)));
        assertFalse(queue.containsAll(List.of(100, 200)));
    }

    @Test
    @DisplayName("containsAll executes correctly for both Set and List inputs")
    public void testContainsAll_SetAndListInputs() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(50, 10, 30, 20));
        Set<Integer> setTarget = Set.of(10, 30);
        assertTrue(queue.containsAll(setTarget));
        List<Integer> listTarget = List.of(20, 50);
        assertTrue(queue.containsAll(listTarget));
    }

    @Test
    @DisplayName("containsAll returns true when target collection contains duplicate elements present in queue")
    public void testContainsAll_DuplicatesInTargetCollection_ReturnsTrue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(5, 10, 15));
        assertTrue(queue.containsAll(List.of(5, 5, 10, 10, 10)));
    }

    @Test
    @DisplayName("containsAll with target containing null element returns false gracefully")
    public void testContainsAll_TargetContainsNull_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertFalse(queue.containsAll(Collections.singletonList(null)));
    }

    @Test
    @DisplayName("containsAll with incompatible element types returns false gracefully")
    public void testContainsAll_IncompatibleTypes_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertFalse(queue.containsAll(List.of("10", "20")));
    }

    @Test
    @DisplayName("element() on empty queue throws NoSuchElementException")
    public void testElement_EmptyQueue_ThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> new CustomPriorityQueue<Integer>().element());
    }

    @Test
    @DisplayName("element() returns top element without removing it from queue")
    public void testElement_NonEmptyQueue_ReturnsTopElementWithoutRemoving() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 8, 40, 15, 2, 80));
        assertEquals(2, queue.element());
        assertEquals(6, queue.size());
    }

    @Test
    @DisplayName("consecutive calls to element() return the same item and preserve queue state")
    public void testElement_ConsecutiveCalls_ReturnsSameItem() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(15, 5, 20));
        assertEquals(5, queue.element());
        assertEquals(5, queue.element());
        assertEquals(5, queue.element());
        assertEquals(3, queue.size());
    }

    @Test
    @DisplayName("element() updates to new head after poll() removes prior root")
    public void testElement_UpdatesAfterPoll() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 5));
        assertEquals(5, queue.element());
        assertEquals(5, queue.poll());
        assertEquals(10, queue.element());
        assertEquals(10, queue.poll());
        assertEquals(20, queue.element());
        assertEquals(20, queue.poll());
        assertThrows(NoSuchElementException.class, queue::element);
    }

    @Test
    @DisplayName("element() respects custom comparator ordering")
    public void testElement_WithCustomComparator_ReturnsRootBasedOnComparator() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(Comparator.reverseOrder());
        queue.addAll(List.of(10, 50, 30));
        assertEquals(50, queue.element());
    }

    @Test
    @DisplayName("Constructor throws NullPointerException when input collection is null")
    public void testConstructor_NullCollection_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CustomPriorityQueue<Integer>(null, Comparator.naturalOrder()));
    }

    @Test
    @DisplayName("Constructor allows null comparator and defaults to natural ordering")
    public void testConstructor_NullComparator_UsesNaturalOrder() {
        List<Integer> items = List.of(30, 8, 40, 15, 2, 80);
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(items, null);
        assertNull(queue.comparator());
        assertEquals(6, queue.size());
        assertEquals(2, queue.peek());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(2, 8, 15, 30, 40, 80), polled);
    }

    @Test
    @DisplayName("Constructor initializes empty queue when passed empty collection")
    public void testConstructor_EmptyCollection_InitializesEmptyQueue() {
        CustomPriorityQueue<String> queue = new CustomPriorityQueue<>(Collections.emptyList(), String.CASE_INSENSITIVE_ORDER);
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
        assertEquals(String.CASE_INSENSITIVE_ORDER, queue.comparator());
    }

    @Test
    @DisplayName("Constructor heapifies unsorted collection according to custom comparator")
    public void testConstructor_CustomComparator_HeapifiesCorrectly() {
        List<Integer> items = List.of(30, 8, 40, 15, 2, 80);
        Comparator<Integer> reverseOrder = Comparator.reverseOrder();
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(items, reverseOrder);
        assertEquals(80, queue.peek());
        assertEquals(reverseOrder, queue.comparator());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(80, 40, 30, 15, 8, 2), polled);
    }

    @Test
    @DisplayName("Constructor makes defensive copy of source collection")
    public void testConstructor_DefensiveCopy_SourceCollectionMutationsDoNotAffectQueue() {
        List<Integer> sourceList = new ArrayList<>(List.of(10, 20, 30));
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(sourceList, Comparator.naturalOrder());
        sourceList.add(40);
        sourceList.removeFirst();
        assertEquals(3, queue.size());
        assertEquals(10, queue.peek());
    }

    @Test
    @DisplayName("Constructor accepts subtype collections (? extends E) and supertype comparators (? super E)")
    public void testConstructor_GenericsWildcards_SupportsSubtypesAndSuperTypeComparator() {
        List<Integer> integers = List.of(50, 10, 30);
        Comparator<Number> numberComparator = Comparator.comparingDouble(Number::doubleValue);
        CustomPriorityQueue<Number> queue = new CustomPriorityQueue<>(integers, numberComparator);
        assertEquals(3, queue.size());
        assertEquals(10, queue.peek());
        List<Number> polled = drainQueue(queue);
        assertEquals(List.of(10, 30, 50), polled);
    }

    @Test
    @DisplayName("retainAll(null) throws NullPointerException")
    public void testRetainAll_NullCollection_ThrowsNullPointerException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertThrows(NullPointerException.class, () -> queue.retainAll(null));
    }

    @Test
    @DisplayName("retainAll on empty queue returns false")
    public void testRetainAll_EmptyQueue_ReturnsFalse() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertFalse(queue.retainAll(List.of(10, 20, 30)));
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("retainAll returns false when queue already contains only target elements")
    public void testRetainAll_AllElementsRetained_ReturnsFalseAndPreservesHeap() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 10, 20));
        assertFalse(queue.retainAll(List.of(10, 20, 30, 40)));
        assertEquals(3, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(10, 20, 30), polled);
    }

    @Test
    @DisplayName("retainAll removes non-matching elements and rebuilds heap correctly")
    public void testRetainAll_PartialMatches_RemovesUnmatchedAndRebuildsHeap() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(30, 8, 40, 15, 2, 80, 50, 1, 90, 20));
        assertTrue(queue.retainAll(List.of(8, 2, 80, 1, 999)));
        assertEquals(4, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(1, 2, 8, 80), polled);
    }

    @Test
    @DisplayName("retainAll with disjoint target collection empties queue")
    public void testRetainAll_NoMatches_EmptiesQueue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        assertTrue(queue.retainAll(List.of(40, 50, 60)));
        assertTrue(queue.isEmpty());
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("retainAll retains all instances of duplicate elements")
    public void testRetainAll_WithDuplicates_RetainsAllOccurrences() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 5, 10, 20, 5, 30));
        assertTrue(queue.retainAll(Set.of(5, 10)));
        assertEquals(4, queue.size());
        List<Integer> polled = drainQueue(queue);
        assertEquals(List.of(5, 5, 10, 10), polled);
    }

    @Test
    @DisplayName("retainAll works identically whether input is a Set or a List")
    public void testRetainAll_HandlesSetAndListInputs() {
        CustomPriorityQueue<Integer> setQueue = new CustomPriorityQueue<>(List.of(50, 10, 30, 20, 40));
        Set<Integer> setTarget = Set.of(10, 30, 40);
        assertTrue(setQueue.retainAll(setTarget));
        assertEquals(List.of(10, 30, 40), drainQueue(setQueue));
        CustomPriorityQueue<Integer> listQueue = new CustomPriorityQueue<>(List.of(50, 10, 30, 20, 40));
        List<Integer> listTarget = List.of(10, 30, 40);
        assertTrue(listQueue.retainAll(listTarget));
        assertEquals(List.of(10, 30, 40), drainQueue(listQueue));
    }

    @Test
    @DisplayName("retainAll modifies modCount and triggers ConcurrentModificationException on active iterator")
    public void testRetainAll_TriggersFailFastIterator() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30, 40));
        Iterator<Integer> it = queue.iterator();
        assertTrue(it.hasNext());
        queue.retainAll(List.of(10, 20));
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("toArray on empty queue returns an empty Object array")
    public void testToArray_EmptyQueue_ReturnsEmptyObjectArray() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        Object[] result = queue.toArray();
        assertNotNull(result);
        assertEquals(0, result.length);
        assertEquals(Object[].class, result.getClass());
    }

    @Test
    @DisplayName("toArray returns array containing all queue elements")
    public void testToArray_NonEmptyQueue_ContainsAllElements() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        List<Integer> items = List.of(30, 8, 40, 15, 2);
        queue.addAll(items);
        Object[] result = queue.toArray();
        assertEquals(items.size(), result.length);
        assertTrue(List.of(result).containsAll(items));
    }

    @Test
    @DisplayName("toArray returns runtime type Object[] regardless of generic element type")
    public void testToArray_ReturnsObjectArrayType() {
        CustomPriorityQueue<String> queue = new CustomPriorityQueue<>(List.of("apple", "banana", "cherry"));
        Object[] result = queue.toArray();
        assertEquals(Object[].class, result.getClass());
    }

    @Test
    @DisplayName("Modifying returned array does not affect internal queue state")
    public void testToArray_MutatingReturnedArray_DoesNotAffectQueue() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        Object[] result = queue.toArray();
        result[0] = 999;
        assertEquals(3, queue.size());
        assertFalse(queue.contains(999));
    }

    @Test
    @DisplayName("Modifying queue after calling toArray does not alter previously returned array")
    public void testToArray_MutatingQueue_DoesNotAffectReturnedArray() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 20, 30));
        Object[] snapshot = queue.toArray();
        queue.poll();
        queue.add(40);
        assertEquals(3, snapshot.length);
        assertFalse(List.of(snapshot).contains(40));
    }

    @Test
    @DisplayName("toArray includes duplicate elements when present in queue")
    public void testToArray_WithDuplicates_ContainsAllDuplicateOccurrences() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>(List.of(10, 5, 10, 5));
        Object[] result = queue.toArray();
        assertEquals(4, result.length);
        long fiveCount = Arrays.stream(result).filter(e -> e.equals(5)).count();
        long tenCount = Arrays.stream(result).filter(e -> e.equals(10)).count();
        assertEquals(2, fiveCount);
        assertEquals(2, tenCount);
    }

    @Test
    @DisplayName("toArray(null) throws NullPointerException")
    public void testToArray_NullArray_ThrowsNullPointerException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        assertThrows(NullPointerException.class, () -> queue.toArray((Integer[]) null));
    }

    @Test
    @DisplayName("toArray with undersized array allocates new array of correct type and size")
    public void testToArray_UndersizedArray_AllocatesNewArray() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Integer[] input = new Integer[0];
        Integer[] result = queue.toArray(input);

        assertNotSame(input, result);
        assertEquals(3, result.length);
        assertEquals(Integer[].class, result.getClass());
        assertTrue(List.of(result).containsAll(List.of(10, 20, 30)));
    }

    @Test
    @DisplayName("toArray with exact-sized array populates and returns same instance")
    public void testToArray_ExactSizedArray_FillsAndReturnsSameInstance() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Integer[] input = new Integer[3];
        Integer[] result = queue.toArray(input);

        assertSame(input, result);
        assertTrue(List.of(result).containsAll(List.of(10, 20, 30)));
    }

    @Test
    @DisplayName("toArray with oversized array sets element at index size to null sentinel")
    public void testToArray_OversizedArray_SetsNullSentinel() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20));

        Integer[] input = new Integer[]{99, 99, 99, 99, 99};
        Integer[] result = queue.toArray(input);

        assertSame(input, result);
        assertNull(result[2]);
        assertEquals(99, result[3]);
    }

    @Test
    @DisplayName("toArray on empty queue with zero-length array returns input array")
    public void testToArray_EmptyQueue_ReturnsEmptyArray() {
        CustomPriorityQueue<String> queue = new CustomPriorityQueue<>();

        String[] input = new String[0];
        String[] result = queue.toArray(input);

        assertSame(input, result);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("toArray with supertype array populates correctly")
    public void testToArray_SupertypeArray_PopulatesCorrectly() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20));

        Number[] input = new Number[2];
        Number[] result = queue.toArray(input);

        assertSame(input, result);
        assertTrue(List.of(result).containsAll(List.of(10, 20)));
    }

    @Test
    @DisplayName("toArray with incompatible array type throws ArrayStoreException")
    public void testToArray_IncompatibleArrayType_ThrowsArrayStoreException() {
        CustomPriorityQueue<Object> queue = new CustomPriorityQueue<>();
        queue.add("String element");

        assertThrows(ArrayStoreException.class, () -> queue.toArray(new Integer[1]));
    }

    @Test
    @DisplayName("next() sequentially iterates through all elements in the underlying array")
    public void testNext_SequentiallyTraversesElements() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Iterator<Integer> it = queue.iterator();
        List<Integer> iterated = new ArrayList<>();

        while (it.hasNext()) {
            iterated.add(it.next());
        }

        assertEquals(3, iterated.size());
        assertTrue(iterated.containsAll(List.of(10, 20, 30)));
    }

    @Test
    @DisplayName("next() throws NoSuchElementException when iteration is complete")
    public void testNext_PastEnd_ThrowsNoSuchElementException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.add(10);

        Iterator<Integer> it = queue.iterator();
        it.next();

        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    @DisplayName("next() throws ConcurrentModificationException when queue modified externally")
    public void testNext_ExternalModification_ThrowsConcurrentModificationException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Iterator<Integer> it = queue.iterator();
        it.next();

        queue.add(40);

        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("remove() throws IllegalStateException when called before next()")
    public void testRemove_BeforeNext_ThrowsIllegalStateException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Iterator<Integer> it = queue.iterator();

        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    @DisplayName("remove() throws IllegalStateException when called twice consecutively")
    public void testRemove_CalledTwice_ThrowsIllegalStateException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Iterator<Integer> it = queue.iterator();
        it.next();
        it.remove();

        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    @DisplayName("remove() deletes current element, syncs expectedModCount, and allows continued iteration")
    public void testRemove_ValidCall_RemovesElementAndAllowsContinuedIteration() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30, 40));

        Iterator<Integer> it = queue.iterator();
        while (it.hasNext()) {
            Integer item = it.next();
            if (item.equals(20)) {
                it.remove();
            }
        }

        assertEquals(3, queue.size());
        assertFalse(queue.contains(20));
    }

    @Test
    @DisplayName("remove() throws ConcurrentModificationException if queue modified externally prior to removal")
    public void testRemove_ExternalModification_ThrowsConcurrentModificationException() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(10, 20, 30));

        Iterator<Integer> it = queue.iterator();
        it.next();

        queue.remove(30);

        assertThrows(ConcurrentModificationException.class, it::remove);
    }

    @Test
    @DisplayName("remove() adds sifted-up elements to forgetMeNot and returns them in subsequent next() calls")
    public void testRemove_SiftUpRevisitedViaForgetMeNot() {
        CustomPriorityQueue<Integer> queue = new CustomPriorityQueue<>();
        queue.addAll(List.of(1, 10, 5, 20, 30, 15, 8));
        Iterator<Integer> it = queue.iterator();
        List<Integer> visited = new ArrayList<>();
        while (it.hasNext()) {
            Integer val = it.next();
            visited.add(val);
            if (val.equals(10)) {
                it.remove();
            }
        }
        assertEquals(6, queue.size());
        assertFalse(queue.contains(10));
        assertEquals(7, visited.size());
        assertTrue(visited.containsAll(List.of(1, 10, 5, 20, 30, 15, 8)));
    }

    private <T> List<T> drainQueue(CustomPriorityQueue<T> q) {
        List<T> result = new ArrayList<>();
        while (!q.isEmpty())
            result.add(q.poll());
        return result;
    }
}