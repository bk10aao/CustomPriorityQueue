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

import static java.util.Objects.requireNonNull;

public class CustomPriorityQueue<E> implements Queue<E>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<E> array;

    private final Comparator<? super E> comparator;

    private transient int modCount = 0;

    public CustomPriorityQueue() {
        this(11, null);
    }

    public CustomPriorityQueue(final int initialCapacity) {
        this(initialCapacity, null);
    }

    public CustomPriorityQueue(final Comparator<? super E> comparator) {
        this(11, comparator);
    }

    public CustomPriorityQueue(final int initialCapacity, Comparator<? super E> comparator) {
        this.array = new ArrayList<>(initialCapacity);
        this.comparator = comparator;
    }

    public CustomPriorityQueue(final Collection<? extends E> c) {
        this.array = new ArrayList<>(c);
        this.comparator = null;
        buildHeap();
    }

    public CustomPriorityQueue(final Collection<? extends E> c, Comparator<? super E> comparator) {
        this.array = new ArrayList<>(c);
        this.comparator = comparator;
        buildHeap();
    }

    public boolean add(final E item) {
        requireNonNull(item);
        modCount++;
        array.add(item);
        pushUp(array, array.size());
        return true;
    }

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

    public void clear() {
        modCount++;
        array.clear();
    }

    public Comparator<? super E> comparator() {
        return comparator;
    }

    public boolean contains(final Object o) {
        if (o == null)
            return false;
        return array.contains(o);
    }

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

    public E element() {
        E item = peek();
        if (item == null)
            throw new NoSuchElementException();
        return item;
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

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
                E moved = removeAt(lastRet);
                expectedModCount = modCount;
                if (lastRet < cursor)
                    cursor--;
                if (lastRet < array.size() && moved != array.get(lastRet)) {
                    if (forgetMeNot == null)
                        forgetMeNot = new ArrayList<>();
                    forgetMeNot.add(moved);
                }
                lastRet = -1;
            }

            private void checkForConcurrentModification() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    public boolean offer(final E item) {
        return add(item);
    }

    public E peek() {
        return array.isEmpty() ? null : array.getFirst();
    }

    public E poll() {
        return array.isEmpty() ? null : removeAt(0);
    }

    public E remove() {
        E item = poll();
        if (item == null)
            throw new NoSuchElementException();
        return item;
    }

    public boolean remove(final Object o) {
        if (o == null || array.isEmpty())
            return false;
        int index = array.indexOf(o);
        if (index == -1)
            return false;
        removeAt(index);
        return true;
    }

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

    public int size() {
        return array.size();
    }

    public Object[] toArray() {
        return array.toArray();
    }

    public <T> T[] toArray(final T[] a) {
        return array.toArray(a);
    }

    private E removeAt(final int index) {
        modCount++;
        int lastIndex = array.size() - 1;
        if (index == lastIndex)
            return array.remove(lastIndex);
        E moved = array.remove(lastIndex);
        array.set(index, moved);
        int oneBasedIndex = index + 1;
        E currentAtPos = array.get(index);
        pushDown(array, oneBasedIndex);
        if (array.get(index) == currentAtPos)
            pushUp(array, oneBasedIndex);
        return moved;
    }

    private void buildHeap() {
        for (int i = array.size() / 2; i >= 1; i--)
            pushDown(array, i);
    }

    private int compare(final E e1, final E e2) {
        if (comparator != null)
            return comparator.compare(e1, e2);
        return ((Comparable<? super E>) e1).compareTo(e2);
    }

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
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    private int getGrandparentIndex(int i) {
        return i / 4;
    }

    private int getIndexOfLargestChildOrGrandchild(final List<E> h, final int i) {
        int[] candidates = getChildrenAndGrandchildrenIndices(i, h.size());
        int largest = candidates[0];
        for (int idx : candidates)
            if (compare(h.get(idx - 1), h.get(largest - 1)) > 0)
                largest = idx;
        return largest;
    }

    private int getIndexOfSmallestChildOrGrandchild(final List<E> h, final int i) {
        int[] candidates = getChildrenAndGrandchildrenIndices(i, h.size());
        int smallest = candidates[0];
        for (int index : candidates)
            if (compare(h.get(index - 1), h.get(smallest - 1)) < 0)
                smallest = index;
        return smallest;
    }

    private int getLeftChildIndex(final int i) {
        return 2 * i;
    }

    private int getParentIndex(final int i) {
        return i / 2;
    }

    private int getRightChildIndex(final int i) {
        return 2 * i + 1;
    }

    private boolean hasGrandParent(final int i) {
        return i >= 4;
    }

    private boolean isEven(final int i) {
        int level = 31 - Integer.numberOfLeadingZeros(i);
        return level % 2 == 0;
    }

    private boolean isGrandchild(final int parent, final int child) {
        return child >= 4 && getParentIndex(getParentIndex(child)) == parent;
    }

    private void pushDown(final List<E> h, final int i) {
        if (isEven(i))
            pushDownMin(h, i);
        else
            pushDownMax(h, i);
    }

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
                } else {
                    break;
                }
            else {
                if (compare(h.get(m - 1), h.get(i - 1)) > 0)
                    swap(i - 1, m - 1, h);
                break;
            }
        }
    }

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
                } else {
                    break;
                }
            else {
                if (compare(h.get(m - 1), h.get(i - 1)) < 0)
                    swap(i - 1, m - 1, h);
                break;
            }
        }
    }

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

    private void swap(final int i, final int j, final List<E> list) {
        E temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}