# Custom Priority Queue

An implementation of a Java Priority Queue.

All methods implemented are identical to those found in the Java Queue interface.

# Build and Test

To build and test the project run command `./gradlew clean build`

To test the project run command `./gradlew test`

# Time Complexity

| Method                                |        JDK         |       Custom       | Winner  |
|:--------------------------------------|:------------------:|:------------------:|:-------:|
| `Constructor()`                       |       $O(1)$       |       $O(1)$       | **Tie** |
| `Constructor(int)`                    |       $O(1)$       |       $O(1)$       | **Tie** |
| `Constructor(Comparator)`             |       $O(1)$       |       $O(1)$       | **Tie** |
| `Constructor(int, Comparator)`        |       $O(1)$       |       $O(1)$       | **Tie** |
| `Constructor(Collection)`             |       $O(n)$       |       $O(n)$       | **Tie** |
| `Constructor(Collection, Comparator)` |       $O(n)$       |       $O(n)$       | **Tie** |
| `add(E)`                              |    $O(\log n)$     |    $O(\log n)$     | **Tie** |
| `addAll(Collection)`                  | $O(k \log(n + k))$ | $O(k \log(n + k))$ | **Tie** |
| `clear()`                             |       $O(n)$       |       $O(n)$       | **Tie** |
| `comparator()`                        |       $O(1)$       |       $O(1)$       | **Tie** |
| `contains(Object)`                    |       $O(n)$       |       $O(n)$       | **Tie** |
| `containsAll(Collection)`             |  $O(k \times n)$   |  $O(k \times n)$   | **Tie** |
| `element()`                           |       $O(1)$       |       $O(1)$       | **Tie** |
| `isEmpty()`                           |       $O(1)$       |       $O(1)$       | **Tie** |
| `iterator()`                          |       $O(1)$       |       $O(1)$       | **Tie** |
| `offer(E)`                            |    $O(\log n)$     |    $O(\log n)$     | **Tie** |
| `peek()`                              |       $O(1)$       |       $O(1)$       | **Tie** |
| `poll()`                              |    $O(\log n)$     |    $O(\log n)$     | **Tie** |
| `remove(Object)`                      |       $O(n)$       |       $O(n)$       | **Tie** |
| `removeAll(Collection)`               |  $O(n \times k)$   |  $O(n \times k)$   | **Tie** |
| `retainAll(Collection)`               |  $O(n \times k)$   |  $O(n \times k)$   | **Tie** |
| `size()`                              |       $O(1)$       |       $O(1)$       | **Tie** |
| `toArray()`                           |       $O(n)$       |       $O(n)$       | **Tie** |
| `toArray(T[])`                        |       $O(n)$       |       $O(n)$       | **Tie** |

# Space Complexity

| Method                                |  JDK   | Custom | Winner  |
|:--------------------------------------|:------:|:------:|:-------:|
| `Constructor()`                       | $O(1)$ | $O(1)$ | **Tie** |
| `Constructor(int)`                    | $O(1)$ | $O(1)$ | **Tie** |
| `Constructor(Comparator)`             | $O(1)$ | $O(1)$ | **Tie** |
| `Constructor(int, Comparator)`        | $O(1)$ | $O(1)$ | **Tie** |
| `Constructor(Collection)`             | $O(n)$ | $O(n)$ | **Tie** |
| `Constructor(Collection, Comparator)` | $O(n)$ | $O(n)$ | **Tie** |
| `add(E)`                              | $O(1)$ | $O(1)$ | **Tie** |
| `addAll(Collection)`                  | $O(1)$ | $O(1)$ | **Tie** |
| `clear()`                             | $O(1)$ | $O(1)$ | **Tie** |
| `comparator()`                        | $O(1)$ | $O(1)$ | **Tie** |
| `contains(Object)`                    | $O(1)$ | $O(1)$ | **Tie** |
| `containsAll(Collection)`             | $O(1)$ | $O(1)$ | **Tie** |
| `element()`                           | $O(1)$ | $O(1)$ | **Tie** |
| `isEmpty()`                           | $O(1)$ | $O(1)$ | **Tie** |
| `iterator()`                          | $O(1)$ | $O(1)$ | **Tie** |
| `offer(E)`                            | $O(1)$ | $O(1)$ | **Tie** |
| `peek()`                              | $O(1)$ | $O(1)$ | **Tie** |
| `poll()`                              | $O(1)$ | $O(1)$ | **Tie** |
| `remove(Object)`                      | $O(1)$ | $O(1)$ | **Tie** |
| `removeAll(Collection)`               | $O(1)$ | $O(1)$ | **Tie** |
| `retainAll(Collection)`               | $O(1)$ | $O(1)$ | **Tie** |
| `size()`                              | $O(1)$ | $O(1)$ | **Tie** |
| `toArray()`                           | $O(n)$ | $O(n)$ | **Tie** |
| `toArray(T[])`                        | $O(n)$ | $O(n)$ | **Tie** |
- Overall Structure Space Complexity: $O(n)$
- $n$ = current number of elements in the priority queue
- $k$ = number of elements in the specified collection parameter

# Performance

Below performance is a comparison made at 100,000 operations per method.

| Method                                | Custom (ns) | JDK (ns)  |            Winner            | Margin |
|:--------------------------------------|:-----------:|:---------:|:----------------------------:|:------:|
| `Constructor()`                       |      5      |     5     | **Statistically Equivalent** | 1.00x  |
| `Constructor(int)`                    |    4,487    |   4,435   | **Statistically Equivalent** | 1.01x  |
| `Constructor(Comparator)`             |      5      |     4     | **Statistically Equivalent** | 1.25x  |
| `Constructor(int, Comparator)`        |    4,607    |   4,443   | **Statistically Equivalent** | 1.04x  |
| `Constructor(Collection)`             |  2,154,960  | 2,063,983 | **Statistically Equivalent** | 1.04x  |
| `Constructor(Collection, Comparator)` |  1,874,421  | 1,889,059 | **Statistically Equivalent** | 1.01x  |
| `add(E)`                              |     44      |    50     |          **Custom**          | 1.14x  |
| `addAll(Collection)`                  |  2,140,600  | 2,463,814 |          **Custom**          | 1.15x  |
| `clear()`                             |      2      |     2     | **Statistically Equivalent** | 1.00x  |
| `comparator()`                        |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `contains(Object)`                    |   27,021    |  26,901   | **Statistically Equivalent** | 1.00x  |
| `containsAll(Collection)`             |  1,381,584  | 1,257,559 | **Statistically Equivalent** | 1.10x  |
| `element()`                           |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `isEmpty()`                           |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `iterator()`                          |   13,370    |  13,337   | **Statistically Equivalent** | 1.00x  |
| `offer(E)`                            |     47      |    44     | **Statistically Equivalent** | 1.07x  |
| `peek()`                              |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `poll()`                              |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `remove(Object)`                      |   27,362    |  27,148   | **Statistically Equivalent** | 1.01x  |
| `removeAll(Collection)`               |  5,188,346  | 5,188,071 | **Statistically Equivalent** | 1.00x  |
| `retainAll(Collection)`               |  5,268,904  | 5,258,133 | **Statistically Equivalent** | 1.00x  |
| `size()`                              |      1      |     1     | **Statistically Equivalent** | 1.00x  |
| `toArray()`                           |   11,114    |  11,136   | **Statistically Equivalent** | 1.00x  |
| `toArray(T[])`                        |   56,091    |  59,775   | **Statistically Equivalent** | 1.07x  |

# Performance Charts

#### Note: The following performance charts are designed to be viewed in dark mode.
![geometric_performance.png](PerformanceCharts/geometric.png)
![heatmap](PerformanceCharts/heatmap.png)
![constructor.png](PerformanceCharts/constructor.png)
![constructor_capacity.png](PerformanceCharts/constructor_capacity.png)
![constructor_comparator.png](PerformanceCharts/constructor_comparator.png)
![constructor_capacity_comparator.png](PerformanceCharts/constructor_capacity_comparator.png)
![constructor_collection.png](PerformanceCharts/constructor_collection.png)
![constructor_collection_comparator.png](PerformanceCharts/constructor_collection_comparator.png)
![add.png](PerformanceCharts/add.png)
![add_all.png](PerformanceCharts/add_all.png)
![clear.png](PerformanceCharts/clear.png)
![comparator.png](PerformanceCharts/comparator.png)
![contains.png](PerformanceCharts/contains.png)
![contains_all.png](PerformanceCharts/contains_all.png)
![element.png](PerformanceCharts/element.png)
![is)empty.png](PerformanceCharts/is_empty.png)
![iterator.png](PerformanceCharts/iterator.png)
![offer.png](PerformanceCharts/offer.png)
![peek.png](PerformanceCharts/peek.png)
![poll.png](PerformanceCharts/poll.png)
![remove_object.png](PerformanceCharts/remove_object.png)
![remove_all.png](PerformanceCharts/remove_all.png)
![retain_all.png](PerformanceCharts/retain_all.png)
![size.png](PerformanceCharts/size.png)
![to_array.png](PerformanceCharts/to_array.png)
![to_array_typed.png](PerformanceCharts/to_array_typed.png)