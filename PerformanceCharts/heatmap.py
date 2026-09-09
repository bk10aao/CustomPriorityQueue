#!/usr/bin/env python3
"""
Generate a performance comparison matrix heatmap between CustomPriorityQueue
and JDK PriorityQueue implementations.
Dynamically extracts all benchmark methods from wide-format JMH CSV files
and applies standard operation display labels.
"""

import io
import os
import sys
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_HEATMAP_PATH = os.path.join(SCRIPT_DIR, 'heatmap.png')

DEFAULT_JDK_CSV = os.path.join(SCRIPT_DIR, 'PriorityQueue_wide_matrix.csv')
DEFAULT_CUSTOM_CSV = os.path.join(SCRIPT_DIR, 'CustomPriorityQueue_wide_matrix.csv')

ALT_JDK_CSVS = ['PriorityQueue_wide_matrix.csv']
ALT_CUSTOM_CSVS = ['CustomPriorityQueue_wide_matrix.csv']

DISPLAY_NAME_MAP = {
    'Constructor()': 'Constructor()',
    'Constructor(int)': 'Constructor(int)',
    'Constructor(Comparator)': 'Constructor(Comparator)',
    'Constructor(int, Comparator)': 'Constructor(int, Comparator)',
    'Constructor(Collection)': 'Constructor(Collection)',
    'Constructor(Collection, Comparator)': 'Constructor(Collection, Comparator)',
    'add(E)': 'add(E)',
    'addAll(Collection)': 'addAll(Collection)',
    'clear()': 'clear()',
    'comparator()': 'comparator()',
    'contains(Object)': 'contains(Object)',
    'containsAll(Collection)': 'containsAll(Collection)',
    'element()': 'element()',
    'isEmpty()': 'isEmpty()',
    'iterator()': 'iterator()',
    'offer(E)': 'offer(E)',
    'peek()': 'peek()',
    'poll()': 'poll()',
    'remove(Object)': 'remove(Object)',
    'removeAll(Collection)': 'removeAll(Collection)',
    'retainAll(Collection)': 'retainAll(Collection)',
    'size()': 'size()',
    'toArray()': 'toArray()',
    'toArray(T[])': 'toArray(T[])',
}


def resolve_file_path(default_path, alt_filenames):
    """Resolve file path checking default path and script directory fallbacks."""
    if os.path.exists(default_path):
        return default_path
    for alt in alt_filenames:
        alt_path = os.path.join(SCRIPT_DIR, alt)
        if os.path.exists(alt_path):
            return alt_path
    return default_path


def load_wide_jmh_csv(filepath):
    """Load wide-format JMH CSV robustly regardless of delimiter."""
    with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
        lines = [line.strip() for line in f if line.strip()]
    sample_line = lines[1] if len(lines) > 1 else lines[0]
    sep = ';' if ';' in sample_line else ('\t' if '\t' in sample_line else ',')
    df = pd.read_csv(io.StringIO('\n'.join(lines)), sep=sep)
    df.columns = [c.strip().replace('"', '') for c in df.columns]
    return df


def get_canonical_name(col_name):
    """Normalize raw CSV column name into clean benchmark display title."""
    return DISPLAY_NAME_MAP.get(col_name, col_name)


def build_column_mappings(df1, df2):
    """Dynamically map all benchmark method columns across both datasets."""
    map1, map2 = {}, {}

    size_col_1 = next((c for c in df1.columns if c.lower().replace('param:', '').strip(' ()_') == 'size'), df1.columns[0])
    size_col_2 = next((c for c in df2.columns if c.lower().replace('param:', '').strip(' ()_') == 'size'), df2.columns[0])

    cols1 = [c for c in df1.columns if c != size_col_1]
    cols2 = [c for c in df2.columns if c != size_col_2]

    for c in cols1:
        canonical = get_canonical_name(c)
        map1[canonical] = c

    for c in cols2:
        canonical = get_canonical_name(c)
        map2[canonical] = c

    all_keys = list(dict.fromkeys(list(map1.keys()) + [k for k in map2.keys() if k not in map1]))
    return map1, map2, all_keys, size_col_1, size_col_2


def main():
    jdk_csv_path = resolve_file_path(DEFAULT_JDK_CSV, ALT_JDK_CSVS)
    custom_csv_path = resolve_file_path(DEFAULT_CUSTOM_CSV, ALT_CUSTOM_CSVS)

    if not os.path.exists(jdk_csv_path):
        print(f"Error: JDK PriorityQueue CSV not found at '{jdk_csv_path}'.")
        sys.exit(1)
    if not os.path.exists(custom_csv_path):
        print(f"Error: Custom PriorityQueue CSV not found at '{custom_csv_path}'.")
        sys.exit(1)

    jdk_df = load_wide_jmh_csv(jdk_csv_path)
    custom_df = load_wide_jmh_csv(custom_csv_path)

    map_jdk, map_custom, method_keys, size_col_jdk, size_col_custom = build_column_mappings(jdk_df, custom_df)

    jdk_df[size_col_jdk] = pd.to_numeric(jdk_df[size_col_jdk])
    custom_df[size_col_custom] = pd.to_numeric(custom_df[size_col_custom])

    sizes = sorted(list(set(jdk_df[size_col_jdk]).intersection(set(custom_df[size_col_custom]))))

    heatmap_data = np.zeros((len(method_keys), len(sizes)))
    text_labels = []

    for i, m_key in enumerate(method_keys):
        row_labels = []
        jdk_col = map_jdk.get(m_key)
        custom_col = map_custom.get(m_key)

        for j, size in enumerate(sizes):
            jdk_vals = jdk_df.loc[jdk_df[size_col_jdk] == size, jdk_col].values if jdk_col and jdk_col in jdk_df.columns else []
            custom_vals = custom_df.loc[custom_df[size_col_custom] == size, custom_col].values if custom_col and custom_col in custom_df.columns else []

            jdk_val = float(jdk_vals[0]) if len(jdk_vals) > 0 and pd.notna(jdk_vals[0]) else 1.0
            custom_val = float(custom_vals[0]) if len(custom_vals) > 0 and pd.notna(custom_vals[0]) else 1.0

            if jdk_val <= 0: jdk_val = 1.0
            if custom_val <= 0: custom_val = 1.0

            # Log2 ratio: positive -> Custom PriorityQueue is faster; negative -> JDK is faster
            ratio = np.log2(jdk_val / custom_val)
            heatmap_data[i, j] = ratio

            if jdk_val >= custom_val:
                factor = jdk_val / custom_val
                row_labels.append(f"+{factor:.1f}x" if factor < 100 else f"+{factor:.0f}x")
            else:
                factor = custom_val / jdk_val
                row_labels.append(f"-{factor:.1f}x" if factor < 100 else f"-{factor:.0f}x")
        text_labels.append(row_labels)

    text_labels = np.array(text_labels)

    # Sort methods by average performance ratio
    avg_ratios = np.mean(heatmap_data, axis=1)
    sorted_idx = np.argsort(avg_ratios)
    heatmap_data = heatmap_data[sorted_idx]
    text_labels = text_labels[sorted_idx]
    sorted_methods = [method_keys[idx] for idx in sorted_idx]

    fig, ax = plt.subplots(figsize=(16, max(8, len(sorted_methods) * 0.7)), facecolor='none')
    ax.set_facecolor('none')

    clipped_data = np.clip(heatmap_data, -4.0, 4.0)
    cmap = sns.diverging_palette(15, 240, as_cmap=True)

    sns.heatmap(
        clipped_data,
        annot=text_labels,
        fmt="",
        cmap=cmap,
        center=0,
        xticklabels=[f'{s:,}' for s in sizes],
        yticklabels=sorted_methods,
        ax=ax,
        cbar_kws={
            'label': '← JDK Faster  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster →'
        },
        linewidths=0.6,
        linecolor='#444444',
        annot_kws={'size': 9, 'weight': 'bold'}
    )

    ax.set_title(
        'JDK vs Custom PriorityQueue\n'
        'Performance Comparison Matrix Heatmap\n'
        '(Blue/Positive = Custom Faster, Red/Negative = JDK Faster)',
        color='#ffffff', fontsize=15, fontweight='bold', pad=20
    )
    ax.set_ylabel('PriorityQueue Interface Operations', color='#ffffff', fontsize=12, labelpad=10)
    ax.set_xlabel('Collection Size (Elements)', color='#ffffff', fontsize=12, labelpad=10)

    ax.tick_params(colors='#ffffff', labelsize=10)
    plt.xticks(rotation=45, ha='right')
    plt.yticks(rotation=0)

    cbar = ax.collections[0].colorbar
    cbar.ax.tick_params(colors='#ffffff', labelsize=10)
    cbar.ax.yaxis.label.set_color('#ffffff')
    cbar.ax.yaxis.label.set_fontsize(11)

    plt.tight_layout()
    plt.savefig(OUTPUT_HEATMAP_PATH, dpi=300, transparent=True)
    plt.close()


if __name__ == '__main__':
    main()