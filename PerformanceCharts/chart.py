#!/usr/bin/env python3
"""
Generate PNG performance benchmark charts comparing CustomPriorityQueue
and JDK PriorityQueue from wide semicolon-delimited CSV matrices.
"""

import sys
from pathlib import Path
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import numpy as np
import pandas as pd

# ──────────────────────────────────────────────────────────────────────────────
# Configuration & Column Mappings
# ──────────────────────────────────────────────────────────────────────────────

SCRIPT_DIR = Path(__file__).resolve().parent
CUSTOM_PQ_CSV = SCRIPT_DIR / "CustomPriorityQueue_wide_matrix.csv"
JDK_PQ_CSV = SCRIPT_DIR / "PriorityQueue_wide_matrix.csv"
OUTPUT_DIR = SCRIPT_DIR

COLORS = {
    'custom': '#4DA6FF',  # Electric Blue
    'jdk': '#9B6EF3',  # Purple
    'grid': '#252525',
}

FIGURE_SIZE = (12, 6.2)
DPI = 150

OPERATIONS = {
    # Constructors
    'constructor_default': {
        'title': 'Constructor()',
        'cols': ['Constructor()', 'defaultConstructor']
    },
    'constructor_capacity': {
        'title': 'Constructor(int)',
        'cols': ['Constructor(int)', 'capacityConstructor']
    },
    'constructor_comparator': {
        'title': 'Constructor(Comparator)',
        'cols': ['Constructor(Comparator)', 'comparatorConstructor']
    },
    'constructor_capacity_comparator': {
        'title': 'Constructor(int, Comparator)',
        'cols': ['Constructor(int, Comparator)', 'capacityAndComparatorConstructor']
    },
    'constructor_collection': {
        'title': 'Constructor(Collection)',
        'cols': ['Constructor(Collection)', 'collectionConstructor']
    },
    'constructor_collection_comparator': {
        'title': 'Constructor(Collection, Comparator)',
        'cols': ['Constructor(Collection, Comparator)', 'collectionAndComparatorConstructor']
    },

    # Queue & Collection Methods
    'add': {
        'title': 'add(E)',
        'cols': ['add(E)', 'testAdd', 'add']
    },
    'add_all': {
        'title': 'addAll(Collection)',
        'cols': ['addAll(Collection)', 'testAddAll', 'addAll']
    },
    'clear': {
        'title': 'clear()',
        'cols': ['clear()', 'testClear', 'clear']
    },
    'comparator': {
        'title': 'comparator()',
        'cols': ['comparator()', 'testComparator', 'comparator']
    },
    'contains': {
        'title': 'contains(Object)',
        'cols': ['contains(Object)', 'testContains', 'contains']
    },
    'contains_all': {
        'title': 'containsAll(Collection)',
        'cols': ['containsAll(Collection)', 'containsAll']
    },
    'element': {
        'title': 'element()',
        'cols': ['element()', 'testElement', 'element']
    },
    'is_empty': {
        'title': 'isEmpty()',
        'cols': ['isEmpty()', 'testIsEmpty', 'isEmpty']
    },
    'iterator': {
        'title': 'iterator()',
        'cols': ['iterator()', 'testIterator', 'iterator']
    },
    'offer': {
        'title': 'offer(E)',
        'cols': ['offer(E)', 'testOffer', 'offer']
    },
    'peek': {
        'title': 'peek()',
        'cols': ['peek()', 'testPeek', 'peek']
    },
    'poll': {
        'title': 'poll()',
        'cols': ['poll()', 'testPoll', 'poll']
    },
    'remove_object': {
        'title': 'remove(Object)',
        'cols': ['remove(Object)', 'testRemoveObject', 'removeObject']
    },
    'remove_all': {
        'title': 'removeAll(Collection)',
        'cols': ['removeAll(Collection)', 'testRemoveAll', 'removeAll']
    },
    'retain_all': {
        'title': 'retainAll(Collection)',
        'cols': ['retainAll(Collection)', 'testRetainAll', 'retainAll']
    },
    'size': {
        'title': 'size()',
        'cols': ['size()', 'testSize', 'size']
    },
    'to_array': {
        'title': 'toArray()',
        'cols': ['toArray()', 'testToArray', 'toArray']
    },
    'to_array_typed': {
        'title': 'toArray(T[])',
        'cols': ['toArray(T[])', 'testToArrayTyped', 'toArrayTyped']
    },
}


# ──────────────────────────────────────────────────────────────────────────────
# File Parsing & Data Handling
# ──────────────────────────────────────────────────────────────────────────────

def load_jmh_csv(filepath: Path) -> dict[int, dict[str, float]]:
    """Parse wide/transposed/long JMH CSV files into {size: {operation_name: value}}."""
    if not filepath.exists():
        return {}

    with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
        first_line = f.readline()

    sep = ';' if ';' in first_line else ('\t' if '\t' in first_line else ',')

    try:
        df = pd.read_csv(filepath, sep=sep)
    except Exception:
        df = pd.read_csv(filepath, sep=None, engine='python')

    df.columns = df.columns.astype(str).str.strip().str.replace('"', '')

    # Find the size column
    size_col = None
    for col in df.columns:
        c_clean = col.strip()
        if c_clean in ['Size', 'size', 'Param: size', 'N', 'n']:
            size_col = col
            break

    if size_col is None:
        for col in df.columns:
            if '(' not in col and col.lower().replace('param:', '').strip() in ['size', 'n']:
                size_col = col
                break

    if size_col is None and len(df.columns) > 0:
        first_col = df.columns[0]
        try:
            pd.to_numeric(df[first_col])
            size_col = first_col
        except (ValueError, TypeError):
            return {}

    if size_col is None:
        return {}

    data: dict[int, dict[str, float]] = {}
    for _, row in df.iterrows():
        try:
            size_val = int(float(str(row[size_col]).replace(',', '').strip()))
        except (ValueError, KeyError, TypeError):
            continue

        data[size_val] = {}
        for col in df.columns:
            if col != size_col:
                try:
                    raw_val = str(row[col]).replace(',', '.').strip()
                    val = float(raw_val)
                    data[size_val][col] = val if not np.isnan(val) else np.nan
                except (ValueError, TypeError):
                    data[size_val][col] = np.nan

    return data


def resolve_path(default_path: Path, patterns: list[str]) -> Path:
    """Resolve explicit path or locate best matching file in current directory."""
    if default_path.exists():
        return default_path
    for pat in patterns:
        matches = list(SCRIPT_DIR.glob(pat))
        if matches:
            return matches[0]
    return default_path


def match_exact_column(available_cols: set[str], candidates: list[str]) -> str | None:
    """Match candidates to available columns with exact signature priority."""
    # 1. Exact match
    for cand in candidates:
        if cand in available_cols:
            return cand

    # 2. Case-insensitive exact match
    avail_lower = {c.lower(): c for c in available_cols}
    for cand in candidates:
        if cand.lower() in avail_lower:
            return avail_lower[cand.lower()]

    # 3. Method prefix/suffix match avoiding cross-collisions
    for cand in candidates:
        cand_clean = cand.lower().replace('()', '').split('(')[0].strip()
        for col in sorted(available_cols):
            col_lower = col.lower()
            if cand_clean in col_lower:
                # Prevent matching 'add' to 'addAll' or 'remove' to 'removeAll'
                idx = col_lower.find(cand_clean)
                after = col_lower[idx + len(cand_clean):]
                if cand_clean == 'add' and after.startswith('all'):
                    continue
                if cand_clean == 'remove' and (after.startswith('all') or after.startswith('object')):
                    continue
                if cand_clean == 'contains' and after.startswith('all'):
                    continue
                if cand_clean == 'toarray' and after.startswith('t'):
                    continue
                return col
    return None


# ──────────────────────────────────────────────────────────────────────────────
# Chart Rendering
# ──────────────────────────────────────────────────────────────────────────────

def create_chart(
        title: str,
        candidates: list[str],
        custom_data: dict[int, dict[str, float]],
        jdk_data: dict[int, dict[str, float]],
        canonical_sizes: list[int],
        output_path: Path
) -> bool:
    all_cols = set()
    for s in canonical_sizes:
        if s in custom_data:
            all_cols.update(custom_data[s].keys())
        if s in jdk_data:
            all_cols.update(jdk_data[s].keys())

    matched_col = match_exact_column(all_cols, candidates)
    if not matched_col:
        return False

    custom_series = [
        custom_data.get(s, {}).get(matched_col, np.nan) for s in canonical_sizes
    ]
    jdk_series = [
        jdk_data.get(s, {}).get(matched_col, np.nan) for s in canonical_sizes
    ]

    if np.all(np.isnan(custom_series)) and np.all(np.isnan(jdk_series)):
        return False

    fig, ax = plt.subplots(figsize=FIGURE_SIZE, dpi=DPI)
    fig.patch.set_alpha(0)
    ax.set_facecolor('none')

    x_positions = list(range(len(canonical_sizes)))

    # Plot Series
    ax.plot(
        x_positions, custom_series,
        color=COLORS['custom'], linewidth=1.5, marker='o', markersize=6,
        markeredgecolor=COLORS['custom'], markeredgewidth=1.5, zorder=3
    )
    ax.plot(
        x_positions, jdk_series,
        color=COLORS['jdk'], linewidth=1.5, marker='o', markersize=6,
        markeredgecolor=COLORS['jdk'], markeredgewidth=1.5, zorder=3
    )

    # Grid & Axes
    ax.grid(True, color=COLORS['grid'], linewidth=0.8, linestyle='-', zorder=0)
    ax.set_axisbelow(True)

    ax.set_xticks(x_positions)
    ax.set_xticklabels([f'{s:,}' for s in canonical_sizes], color='white', fontsize=10)
    ax.tick_params(axis='x', colors='white', length=0, pad=8)
    ax.set_xlim(-0.5, len(canonical_sizes) - 0.5)

    ax.set_ylim(bottom=0)
    ax.yaxis.set_major_locator(mticker.MaxNLocator(integer=True))
    ax.yaxis.set_major_formatter(mticker.FuncFormatter(lambda x, _: f'{int(round(x)):,}'))

    if ax.yaxis.get_offset_text():
        ax.yaxis.get_offset_text().set_color('white')

    ax.tick_params(axis='y', colors='white', length=0, pad=8)
    for label in ax.get_yticklabels():
        label.set_color('white')
        label.set_fontsize(10)

    for spine in ax.spines.values():
        spine.set_visible(False)

    ax.set_xlabel('Size', color='white', fontsize=12, labelpad=12)
    ax.set_ylabel('Time (ns/op)', color='white', fontsize=11, labelpad=10)
    ax.set_title(title, color='white', fontsize=15, fontweight='bold', pad=14)

    # Legend with pure circle markers
    legend_elements = [
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['custom'],
               markeredgecolor=COLORS['custom'], markeredgewidth=1.5, markersize=8,
               label='Custom', linestyle='none'),
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['jdk'],
               markeredgecolor=COLORS['jdk'], markeredgewidth=1.5, markersize=8,
               label='JDK', linestyle='none'),
    ]

    leg = ax.legend(
        handles=legend_elements,
        loc='upper center',
        bbox_to_anchor=(0.5, -0.22),
        ncol=2,
        frameon=False,
        fontsize=12,
        handlelength=1.5,
        columnspacing=2.0
    )
    for text in leg.get_texts():
        text.set_color('white')

    fig.savefig(output_path, dpi=DPI, transparent=True, bbox_inches='tight', facecolor='none', edgecolor='none')
    plt.close(fig)
    return True


# ──────────────────────────────────────────────────────────────────────────────
# Main Execution
# ──────────────────────────────────────────────────────────────────────────────

def main():
    custom_arg = Path(sys.argv[1]) if len(sys.argv) > 1 else CUSTOM_PQ_CSV
    jdk_arg = Path(sys.argv[2]) if len(sys.argv) > 2 else JDK_PQ_CSV

    custom_path = resolve_path(custom_arg, ["CustomPriorityQueue*.csv", "*Custom*.csv"])
    jdk_path = resolve_path(jdk_arg, ["PriorityQueue*.csv", "*JDK*.csv"])

    print(f"Loading Custom: {custom_path.name}")
    custom_data = load_jmh_csv(custom_path)

    print(f"Loading JDK:    {jdk_path.name}")
    jdk_data = load_jmh_csv(jdk_path)

    canonical_sizes = sorted(list(set(custom_data.keys()) | set(jdk_data.keys())))

    if not canonical_sizes:
        print("Error: Could not parse benchmarking sizes or data rows.")
        sys.exit(1)

    generated = 0
    for file_slug, config in OPERATIONS.items():
        title = config['title']
        output_path = OUTPUT_DIR / f"{file_slug}.png"
        if create_chart(title, config['cols'], custom_data, jdk_data, canonical_sizes, output_path):
            print(f"  ✓ Saved {file_slug}.png ({title})")
            generated += 1

    print(f"\nSuccessfully generated {generated} performance chart(s).")


if __name__ == '__main__':
    main()