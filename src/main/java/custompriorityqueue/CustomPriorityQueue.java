package custompriorityqueue;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

/**
 * An array-backed priority queue implemented as a min-max heap.
 * <p>
 * Provides {@code O(1)} retrieval of the minimum element and {@code O(log n)}
 * insertion and removal. Iterators are fail-fast and do not guarantee sorted order.
 * <p>
 * <strong>Note: This implementation is not thread-safe.</strong>
 * @param <E> the type of elements held in this queue
 *
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomPriorityQueue">Repository</a>
 */
public class CustomPriorityQueue<E> implements Queue<E>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The backing array storing the min-max heap.
     */
    private List<E> array;

    /**
     * The comparator used to order elements, or {@code null} for natural ordering.
     */
    private final Comparator<? super E> comparator;

    /**
     * Count of structural modifications for iterator fail-fast checks.
     */
    private transient int modCount = 0;

    /**
     * Constructs an empty priority queue with default capacity (11) and natural ordering.
     */
    public CustomPriorityQueue() {
        this(11, null);
    }

    /**
     * Constructs an empty priority queue with the given initial capacity and natural ordering.
     *
     * @param initialCapacity initial capacity of the backing array
     */
    public CustomPriorityQueue(final int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Constructs an empty priority queue with default capacity (11) and the given comparator.
     *
     * @param comparator comparator to order elements, or {@code null} for natural ordering
     */
    public CustomPriorityQueue(final Comparator<? super E> comparator) {
        this(11, comparator);
    }

    /**
     * Constructs an empty priority queue with the given capacity and comparator.
     *
     * @param initialCapacity initial capacity of the backing array
     * @param comparator      comparator to order elements, or {@code null} for natural ordering
     */
    public CustomPriorityQueue(final int initialCapacity, Comparator<? super E> comparator) {
        this.array = new ArrayList<>(initialCapacity);
        this.comparator = comparator;
    }

    /**
     * Constructs a priority queue containing the elements of the given collection in natural order.
     * Heap construction runs in {@code O(n)} time via {@link #buildHeap()}.
     *
     * @param c collection whose elements are to be placed in this queue
     */
    public CustomPriorityQueue(final Collection<? extends E> c) {
        this.array = new ArrayList<>(c);
        this.comparator = null;
        buildHeap();
    }

    /**
     * Constructs a priority queue containing the elements of the given collection and comparator.
     * Heap construction runs in {@code O(n)} time via {@link #buildHeap()}.
     *
     * @param c          collection whose elements are to be placed in this queue
     * @param comparator comparator to order elements, or {@code null} for natural ordering
     */
    public CustomPriorityQueue(final Collection<? extends E> c, Comparator<? super E> comparator) {
        this.array = new ArrayList<>(c);
        this.comparator = comparator;
        buildHeap();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if specified element is {@code null}
     */
    public boolean add(final E item) {
        requireNonNull(item);
        modCount++;
        array.add(item);
        pushUp(array, array.size());
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     if {@code c} or any element within it is {@code null}
     * @throws IllegalArgumentException if {@code c} is this queue
     */
    public boolean addAll(final Collection<? extends E> c) {
        requireNonNull(c);
        if (c == this)
            throw new IllegalArgumentException();
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        modCount++;
        array.clear();
    }

    /**
     * Returns the comparator used to order elements, or {@code null} if using natural ordering.
     *
     * @return comparator or {@code null}
     */
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        if (o == null)
            return false;
        return array.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(final Collection<?> c) {
        requireNonNull(c);
        if (c.isEmpty())
            return true;
        Collection<?> targets = (c instanceof Set<?>) ? c : new HashSet<>(c);
        Set<Object> values = new HashSet<>(array);
        for (Object e : targets)
            if (!values.contains(e))
                return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public E element() {
        E item = peek();
        if (item == null)
            throw new NoSuchElementException();
        return item;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return array.isEmpty();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Elements are returned in arbitrary order. The iterator is fail-fast and tracks
     * relocated elements to ensure complete traversal across concurrent removals.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int cursor = 0;
            private int lastRet = -1;
            private int expectedModCount = modCount;
            private List<E> forgetMeNot = null;

            @Override
            public boolean hasNext() {
                return cursor < array.size() || (forgetMeNot != null && !forgetMeNot.isEmpty());
            }

            @Override
            public E next() {
                checkForConcurrentModification();
                if (cursor < array.size()) {
                    lastRet = cursor;
                    return array.get(cursor++);
                }
                if (forgetMeNot != null && !forgetMeNot.isEmpty()) {
                    lastRet = -1;
                    return forgetMeNot.removeLast();
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                checkForConcurrentModification();
                if (lastRet < 0)
                    throw new IllegalStateException();
                E lastElem = array.getLast();
                removeAt(lastRet);
                expectedModCount = modCount;
                if (lastRet < cursor)
                    cursor--;
                if (lastRet < array.size() && array.get(lastRet) != lastElem) {
                    boolean shiftedUp = true;
                    for (int i = lastRet; i < array.size(); i++)
                        if (array.get(i) == lastElem) {
                            shiftedUp = false;
                            break;
                        }
                    if (shiftedUp) {
                        if (forgetMeNot == null)
                            forgetMeNot = new ArrayList<>();
                        forgetMeNot.add(lastElem);
                    }
                }
                lastRet = -1;
            }

            private void checkForConcurrentModification() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean offer(final E item) {
        return add(item);
    }

    /**
     * {@inheritDoc}
     */
    public E peek() {
        return array.isEmpty() ? null : array.getFirst();
    }

    /**
     * {@inheritDoc}
     */
    public E poll() {
        return array.isEmpty() ? null : removeAt(0);
    }

    /**
     * {@inheritDoc}
     */
    public E remove() {
        E item = poll();
        if (item == null)
            throw new NoSuchElementException();
        return item;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(final Object o) {
        if (o == null || array.isEmpty())
            return false;
        int index = array.indexOf(o);
        if (index == -1)
            return false;
        removeAt(index);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(final Collection<?> c) {
        requireNonNull(c);
        Set<?> removeSet = (c instanceof Set<?>) ? (Set<?>) c : new HashSet<>(c);
        List<E> kept = new ArrayList<>();
        for (E e : array)
            if (!removeSet.contains(e))
                kept.add(e);
        if (kept.size() == array.size())
            return false;
        modCount++;
        array = kept;
        buildHeap();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(final Collection<?> c) {
        requireNonNull(c);
        Set<?> retainSet = (c instanceof Set<?>) ? (Set<?>) c : new HashSet<>(c);
        List<E> retained = new ArrayList<>();
        for (E e : array)
            if (retainSet.contains(e))
                retained.add(e);
        if (retained.size() == array.size())
            return false;
        modCount++;
        array = retained;
        buildHeap();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return array.size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return array.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(final T[] a) {
        return array.toArray(a);
    }

    /**
     * Establishes the min-max heap property over the entire backing array in
     * linear time, by pushing down every non-leaf node in reverse (bottom-up)
     * one-based index order.
     */
    private void buildHeap() {
        for (int i = array.size() / 2; i >= 1; i--)
            pushDown(array, i);
    }

    /**
     * Compares two elements using this queue's {@link #comparator}, or their
     * {@linkplain Comparable natural ordering} if no comparator was supplied.
     *
     * @param e1 the first element to compare
     * @param e2 the second element to compare
     * @return a negative integer, zero, or a positive integer as {@code e1}
     *         is less than, equal to, or greater than {@code e2}
     */
    private int compare(final E e1, final E e2) {
        if (comparator != null)
            return comparator.compare(e1, e2);
        return ((Comparable<? super E>) e1).compareTo(e2);
    }

    /**
     * Collects the one-based indices of all children and grandchildren of the
     * node at one-based index {@code i} that fall within the heap of the
     * given {@code size}, in the fixed order: left child, right child, then
     * the four grandchildren (left-of-left, right-of-left, left-of-right,
     * right-of-right), skipping any that lie beyond {@code size}.
     *
     * @param i    the one-based index of the node whose descendants are sought
     * @param size the current size of the heap (i.e. the highest valid
     *             one-based index)
     * @return an array containing the one-based indices of {@code i}'s
     *         in-bounds children and grandchildren, in the order described above
     */
    private int[] getChildrenAndGrandchildrenIndices(final int i, final int size) {
        int[] temp = new int[6];
        int count = 0;
        int left = getLeftChildIndex(i);
        int right = getRightChildIndex(i);
        if (left <= size)
            temp[count++] = left;
        if (right <= size)
            temp[count++] = right;
        int g1 = getLeftChildIndex(left);
        int g2 = getRightChildIndex(left);
        int g3 = getLeftChildIndex(right);
        int g4 = getRightChildIndex(right);
        if (g1 <= size)
            temp[count++] = g1;
        if (g2 <= size)
            temp[count++] = g2;
        if (g3 <= size)
            temp[count++] = g3;
        if (g4 <= size)
            temp[count++] = g4;
        int[] result = new int[count];
        arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /**
     * Computes the one-based index of the grandparent of the node at
     * one-based index {@code i}.
     *
     * @param i the one-based index of the node whose grandparent is sought
     * @return the one-based index of {@code i}'s grandparent
     */
    private int getGrandparentIndex(int i) {
        return i / 4;
    }

    /**
     * Finds the one-based index, among the children and grandchildren of the
     * node at one-based index {@code i}, of the element that compares
     * greatest according to {@link #compare(Object, Object)}.
     *
     * @param h the backing list of the heap
     * @param i the one-based index of the node whose children/grandchildren are compared
     * @return the one-based index of the largest child or grandchild of {@code i}
     */
    private int getIndexOfLargestChildOrGrandchild(final List<E> h, final int i) {
        int[] candidates = getChildrenAndGrandchildrenIndices(i, h.size());
        int largest = candidates[0];
        for (int idx : candidates)
            if (compare(h.get(idx - 1), h.get(largest - 1)) > 0)
                largest = idx;
        return largest;
    }

    /**
     * Finds the one-based index, among the children and grandchildren of the
     * node at one-based index {@code i}, of the element that compares
     * smallest according to {@link #compare(Object, Object)}.
     *
     * @param h the backing list of the heap
     * @param i the one-based index of the node whose children/grandchildren are compared
     * @return the one-based index of the smallest child or grandchild of {@code i}
     */
    private int getIndexOfSmallestChildOrGrandchild(final List<E> h, final int i) {
        int[] candidates = getChildrenAndGrandchildrenIndices(i, h.size());
        int smallest = candidates[0];
        for (int index : candidates)
            if (compare(h.get(index - 1), h.get(smallest - 1)) < 0)
                smallest = index;
        return smallest;
    }

    /**
     * Computes the one-based index of the left child of the node at one-based
     * index {@code i}.
     *
     * @param i the one-based index of the parent node
     * @return the one-based index of {@code i}'s left child
     */
    private int getLeftChildIndex(final int i) {
        return 2 * i;
    }

    /**
     * Computes the one-based index of the parent of the node at one-based
     * index {@code i}.
     *
     * @param i the one-based index of the child node
     * @return the one-based index of {@code i}'s parent
     */
    private int getParentIndex(final int i) {
        return i / 2;
    }

    /**
     * Computes the one-based index of the right child of the node at
     * one-based index {@code i}.
     *
     * @param i the one-based index of the parent node
     * @return the one-based index of {@code i}'s right child
     */
    private int getRightChildIndex(final int i) {
        return 2 * i + 1;
    }

    /**
     * Returns whether the node at one-based index {@code i} has a
     * grandparent, i.e. whether it lies at heap depth 2 or greater.
     *
     * @param i the one-based index of the node to check
     * @return {@code true} if {@code i} has a grandparent, {@code false} otherwise
     */
    private boolean hasGrandParent(final int i) {
        return i >= 4;
    }

    /**
     * Determines whether the level of the node at one-based index {@code i}
     * within the heap is a "min level" (as opposed to a "max level"), where
     * the root (level 0) is a min level and levels alternate min/max going
     * down the tree.
     * <p>
     * The level of a node is derived from the position of the highest set
     * bit of its one-based index (i.e. {@code floor(log2(i))}); despite the
     * method's name, it returns {@code true} exactly when that level is even,
     * which corresponds to a min level.
     *
     * @param i the one-based index of the node to check
     * @return {@code true} if {@code i} lies on a min level, {@code false} if
     *         it lies on a max level
     */
    private boolean isEven(final int i) {
        return (31 - Integer.numberOfLeadingZeros(i)) % 2 == 0;
    }

    /**
     * Returns whether {@code child} is a grandchild (rather than a direct
     * child) of {@code parent}, both given as one-based indices.
     *
     * @param parent the one-based index of the candidate ancestor
     * @param child  the one-based index of the candidate descendant
     * @return {@code true} if {@code child} is exactly two levels below
     *         {@code parent} in the heap, {@code false} otherwise
     */
    private boolean isGrandchild(final int parent, final int child) {
        return child >= 4 && getParentIndex(getParentIndex(child)) == parent;
    }

    /**
     * Restores the min-max heap property downward from the node at one-based
     * index {@code i}, dispatching to {@link #pushDownMin(List, int)} or
     * {@link #pushDownMax(List, int)} depending on whether {@code i} lies on
     * a min level or a max level.
     *
     * @param h the backing list of the heap
     * @param i the one-based index of the node to push down
     */
    private void pushDown(final List<E> h, final int i) {
        if (isEven(i))
            pushDownMin(h, i);
        else
            pushDownMax(h, i);
    }

    /**
     * Sinks the element currently at one-based index {@code i} (assumed to
     * lie on a max level) downward until the max-level heap property holds:
     * repeatedly locates the largest child or grandchild of the current
     * position; if that largest descendant is a grandchild and exceeds the
     * current node, swaps them (and additionally swaps the moved element
     * with its new parent if it now exceeds that parent, to preserve the
     * min-level property one level up), then continues from the
     * grandchild's original position. If the largest descendant is a direct
     * child, at most one swap is performed and the process terminates.
     *
     * @param h the backing list of the heap
     * @param i the one-based index at which to begin pushing down (mutated locally as the descent proceeds)
     */
    private void pushDownMax(final List<E> h, int i) {
        while (getLeftChildIndex(i) <= h.size()) {
            int m = getIndexOfLargestChildOrGrandchild(h, i);
            if (isGrandchild(i, m))
                if (compare(h.get(m - 1), h.get(i - 1)) > 0) {
                    swap(i - 1, m - 1, h);
                    int parentOfM = getParentIndex(m);
                    if (compare(h.get(m - 1), h.get(parentOfM - 1)) < 0)
                        swap(m - 1, parentOfM - 1, h);
                    i = m;
                } else
                    break;
            else {
                if (compare(h.get(m - 1), h.get(i - 1)) > 0)
                    swap(i - 1, m - 1, h);
                break;
            }
        }
    }

    /**
     * Sinks the element currently at one-based index {@code i} (assumed to
     * lie on a min level) downward until the min-level heap property holds:
     * repeatedly locates the smallest child or grandchild of the current
     * position; if that smallest descendant is a grandchild and is less than
     * the current node, swaps them (and additionally swaps the moved element
     * with its new parent if it is now greater than that parent, to preserve
     * the max-level property one level up), then continues from the
     * grandchild's original position. If the smallest descendant is a direct
     * child, at most one swap is performed and the process terminates.
     *
     * @param h the backing list of the heap
     * @param i the one-based index at which to begin pushing down (mutated locally as the descent proceeds)
     */
    private void pushDownMin(final List<E> h, int i) {
        while (getLeftChildIndex(i) <= h.size()) {
            int m = getIndexOfSmallestChildOrGrandchild(h, i);
            if (isGrandchild(i, m))
                if (compare(h.get(m - 1), h.get(i - 1)) < 0) {
                    swap(i - 1, m - 1, h);
                    int parentOfM = getParentIndex(m);
                    if (compare(h.get(m - 1), h.get(parentOfM - 1)) > 0)
                        swap(m - 1, parentOfM - 1, h);
                    i = m;
                } else
                    break;
            else {
                if (compare(h.get(m - 1), h.get(i - 1)) < 0)
                    swap(i - 1, m - 1, h);
                break;
            }
        }
    }

    /**
     * Restores the min-max heap property upward from the newly inserted or
     * relocated element at one-based index {@code i}.
     * <p>
     * If {@code i} is the root ({@code i <= 1}), nothing needs to be done.
     * Otherwise, the element is first compared against its parent: if it
     * violates the parent's level ordering, it is swapped with the parent and
     * the appropriate same-type ascent ({@link #pushUpMax(List, int)} for a
     * max-level parent, {@link #pushUpMin(List, int)} for a min-level parent)
     * continues from the parent's position. If it does not violate its
     * parent's ordering, it instead ascends within its own level type
     * ({@link #pushUpMin(List, int)} if {@code i} is on a min level,
     * {@link #pushUpMax(List, int)} if on a max level) to check against
     * grandparents.
     *
     * @param h the backing list of the heap
     * @param i the one-based index of the element to push up
     */
    private void pushUp(final List<E> h, final int i) {
        if (i <= 1) return;
        int parent = getParentIndex(i);
        if (isEven(i))
            if (compare(h.get(i - 1), h.get(parent - 1)) > 0) {
                swap(i - 1, parent - 1, h);
                pushUpMax(h, parent);
            } else
                pushUpMin(h, i);
        else
            if (compare(h.get(i - 1), h.get(parent - 1)) < 0) {
                swap(i - 1, parent - 1, h);
                pushUpMin(h, parent);
            } else
                pushUpMax(h, i);
    }

    /**
     * Continues bubbling the element at one-based index {@code i} upward
     * through successive max-level ancestors (i.e. comparing against
     * grandparents, great-great-grandparents, and so on), swapping and
     * ascending as long as the element exceeds its current grandparent.
     *
     * @param h the backing list of the heap
     * @param i the one-based index at which to begin ascending among max levels
     */
    private void pushUpMax(final List<E> h, int i) {
        while (hasGrandParent(i)) {
            int gp = getGrandparentIndex(i);
            if (compare(h.get(i - 1), h.get(gp - 1)) > 0) {
                swap(i - 1, gp - 1, h);
                i = gp;
            } else
                break;
        }
    }

    /**
     * Continues bubbling the element at one-based index {@code i} upward
     * through successive min-level ancestors (i.e. comparing against
     * grandparents, great-great-grandparents, and so on), swapping and
     * ascending as long as the element is less than its current grandparent.
     *
     * @param h the backing list of the heap
     * @param i the one-based index at which to begin ascending among min levels
     */
    private void pushUpMin(final List<E> h, int i) {
        while (hasGrandParent(i)) {
            int gp = getGrandparentIndex(i);
            if (compare(h.get(i - 1), h.get(gp - 1)) < 0) {
                swap(i - 1, gp - 1, h);
                i = gp;
            } else
                break;
        }
    }


    /**
     * Removes and returns the element at the given zero-based array position,
     * restoring the min-max heap property afterward.
     * <p>
     * The element at {@code index} is removed by moving the last element of
     * the backing array into its place (unless {@code index} already refers
     * to the last position), then re-heapifying: first attempting to push the
     * relocated element down toward its correct position, and if it did not
     * move (meaning it was already too small to sink), attempting to push it
     * up instead.
     *
     * @param index the zero-based array position of the element to remove
     * @return the element that previously occupied the last position of the
     *         array and was moved to fill the vacated slot (or, if
     *         {@code index} was already the last position, the element
     *         removed from that position)
     */
    private E removeAt(final int index) {
        modCount++;
        int lastIndex = array.size() - 1;
        if (index == lastIndex)
            return array.remove(lastIndex);
        E removed = array.get(index);
        E moved = array.remove(lastIndex);
        array.set(index, moved);
        int oneBasedIndex = index + 1;
        E currentAtPos = array.get(index);
        pushDown(array, oneBasedIndex);
        if (array.get(index) == currentAtPos)
            pushUp(array, oneBasedIndex);
        return removed;
    }

    /**
     * Swaps the elements at the given zero-based positions within the given list.
     *
     * @param i    the zero-based index of the first element
     * @param j    the zero-based index of the second element
     * @param list the list in which to perform the swap
     */
    private void swap(final int i, final int j, final List<E> list) {
        E temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}