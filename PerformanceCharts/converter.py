from pathlib import Path
import pandas as pd

HEADER_MAPPING = {
    # Constructors
    "testDefaultConstructor": "Constructor()",
    "testCapacityConstructor": "Constructor(int)",
    "testComparatorConstructor": "Constructor(Comparator)",
    "testCapacityAndComparatorConstructor": "Constructor(int, Comparator)",
    "testCollectionConstructor": "Constructor(Collection)",
    "testCollectionAndComparatorConstructor": "Constructor(Collection, Comparator)",
    "testPriorityQueueConstructor": "Constructor(PriorityQueue)",
    "testSortedSetConstructor": "Constructor(SortedSet)",

    # Operations
    "testAdd": "add(E)",
    "testAddAll": "addAll(Collection)",
    "testClear": "clear()",
    "testComparator": "comparator()",
    "testContains": "contains(Object)",
    "testContainsAll": "containsAll(Collection)",
    "testElement": "element()",
    "testIsEmpty": "isEmpty()",
    "testIterator": "iterator()",
    "testOffer": "offer(E)",
    "testPeek": "peek()",
    "testPoll": "poll()",
    "testRemoveHead": "remove()",
    "testRemoveObject": "remove(Object)",
    "testRemoveAll": "removeAll(Collection)",
    "testRetainAll": "retainAll(Collection)",
    "testSize": "size()",
    "testToArray": "toArray()",
    "testToArrayTyped": "toArray(T[])"
}

COLUMN_ORDER = [
    "Constructor()", "Constructor(int)", "Constructor(Comparator)",
    "Constructor(int, Comparator)", "Constructor(Collection)",
    "Constructor(Collection, Comparator)", "Constructor(PriorityQueue)",
    "Constructor(SortedSet)", "add(E)", "addAll(Collection)", "clear()",
    "comparator()", "contains(Object)", "containsAll(Collection)",
    "element()", "isEmpty()", "iterator()", "offer(E)", "peek()", "poll()",
    "remove()", "remove(Object)", "removeAll(Collection)", "retainAll(Collection)",
    "size()", "toArray()", "toArray(T[])"
]


def convert_to_wide_matrix(input_file: str | Path, output_file: str | Path) -> None:
    input_path = Path(input_file)
    output_path = Path(output_file)

    if not input_path.exists():
        print(f"Skipping '{input_path}' (file not found).")
        return

    df = pd.read_csv(input_path, sep=';')

    # Extract short method name from fully-qualified package string (e.g., 'com.pkg.Benchmark.testAdd' -> 'testAdd')
    clean_benchmark = df['Benchmark'].astype(str).str.strip('"\t ')
    method_names = clean_benchmark.str.split('.').str[-1]

    # Map method names using HEADER_MAPPING with raw name fallback
    df['Metric'] = method_names.map(HEADER_MAPPING).fillna(method_names)

    # Pivot table and aggregate scores safely using mean
    pivot_df = df.pivot_table(
        index='Size',
        columns='Metric',
        values='Score (ns/op)',
        aggfunc='mean'
    ).round()

    # Safely convert to nullable Int64 to avoid crashes on missing/NaN benchmark cells
    pivot_df = pivot_df.astype('Int64')

    # Reorder according to COLUMN_ORDER while appending any unmapped extra columns
    available_columns = [col for col in COLUMN_ORDER if col in pivot_df.columns]
    extra_columns = [col for col in pivot_df.columns if col not in available_columns]
    pivot_df = pivot_df.reindex(columns=available_columns + extra_columns)

    # Export wide matrix CSV
    pivot_df.to_csv(output_path, sep=';')
    print(f"Wide matrix CSV created: {output_path}")


if __name__ == "__main__":
    convert_to_wide_matrix("CustomPriorityQueue_jmh_performance.csv", "CustomPriorityQueue_wide_matrix.csv")
    convert_to_wide_matrix("PriorityQueue_jmh_performance.csv", "PriorityQueue_wide_matrix.csv")