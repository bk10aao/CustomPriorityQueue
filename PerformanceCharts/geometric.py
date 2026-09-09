import os
import sys
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import gmean

file_jdk = 'PriorityQueue_wide_matrix.csv'
file_custom = 'CustomPriorityQueue_wide_matrix.csv'

df_jdk = pd.read_csv(file_jdk, sep=';')
df_custom = pd.read_csv(file_custom, sep=';')

v1_df = df_jdk.copy()      # JDK PriorityQueue
v2_df = df_custom.copy()   # Custom PriorityQueue

v1_df['Size'] = pd.to_numeric(v1_df['Size'])
v2_df['Size'] = pd.to_numeric(v2_df['Size'])

v1_pivot = v1_df.set_index('Size')
v2_pivot = v2_df.set_index('Size')

common_sizes = sorted(list(set(v1_pivot.index).intersection(set(v2_pivot.index))))
v1_pivot = v1_pivot.loc[common_sizes]
v2_pivot = v2_pivot.loc[common_sizes]

benchmarks = [b for b in v1_pivot.columns if b in v2_pivot.columns and b != 'Size']

v1_fixed = v1_pivot.copy()
v2_fixed = v2_pivot.copy()

for b in benchmarks:
    v1_fixed[b] = pd.to_numeric(v1_fixed[b], errors='coerce').fillna(1).replace(0, 1)
    v2_fixed[b] = pd.to_numeric(v2_fixed[b], errors='coerce').fillna(1).replace(0, 1)

jdk_win_color = '#E53E3E'    # Red for JDK Faster
custom_win_color = '#3B82F6' # Blue for Custom Faster
tie_color = '#888888'        # Gray for Tie

ratios = []
labels = []
colors = []

for b in benchmarks:
    v1_vals = v1_fixed[b].dropna()
    v2_vals = v2_fixed[b].dropna()
    if v1_vals.empty or v2_vals.empty:
        continue

    # Ratio: Custom / JDK execution time
    per_size_ratios = v2_vals / v1_vals
    g_ratio = gmean(per_size_ratios)

    if abs(g_ratio - 1.0) < 1e-4:
        ratios.append(0.0)
        colors.append(tie_color)
    elif g_ratio < 1.0:
        # Custom PriorityQueue is faster
        speedup = 1.0 / g_ratio
        ratios.append(speedup - 1)
        colors.append(custom_win_color)
    else:
        # JDK PriorityQueue is faster
        ratios.append(-(g_ratio - 1))
        colors.append(jdk_win_color)

    labels.append(b)

sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

TEXT_COLOR = '#ffffff'

fig_height = max(7.0, len(sorted_labels) * 0.45)

fig, ax = plt.subplots(figsize=(12, fig_height), facecolor='none')
ax.set_facecolor('none')

bars = ax.barh(
    range(len(sorted_labels)),
    sorted_ratios,
    color=sorted_colors,
    alpha=0.9,
    height=0.65,
)

ax.axvline(x=0, color=TEXT_COLOR, linewidth=1.4, zorder=1)

min_r = min(sorted_ratios) if sorted_ratios else -0.3
max_r = max(sorted_ratios) if sorted_ratios else 0.1

left_limit = min(-0.35, min_r - 0.08)
right_limit = max(0.18, max_r + 0.05)
ax.set_xlim(left_limit, right_limit)

ticks = [-0.3, -0.2, -0.1, 0.0, 0.1]
tick_labels = ['1.30x', '1.20x', '1.10x', 'Tie', '1.10x']

ax.set_xticks(ticks)
ax.set_xticklabels(tick_labels, color=TEXT_COLOR, fontsize=10, fontweight='bold')
ax.set_ylim(-0.5, len(sorted_labels) - 0.5)
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color=TEXT_COLOR, fontsize=10)

for idx, r in enumerate(sorted_ratios):
    val = abs(r)
    text_str = 'Tie' if val < 0.002 else f'{val + 1:.2f}x'

    if r < -0.002:
        ax.text(r - 0.005, idx, f'{text_str} ', va='center', ha='right', color=TEXT_COLOR, fontsize=9, fontweight='bold')
    elif r > 0.002:
        ax.text(r + 0.005, idx, f' {text_str}', va='center', ha='left', color=TEXT_COLOR, fontsize=9, fontweight='bold')
    else:
        ax.text(0.005, idx, f' {text_str}', va='center', ha='left', color=TEXT_COLOR, fontsize=9, fontweight='bold')

ax.set_title(
    'Overall Relative Performance Comparison (JDK PriorityQueue vs CustomPriorityQueue)\n'
    '(Geometric Mean Speedup Factor Across All Sizes)',
    fontsize=13, fontweight='bold', pad=15, color=TEXT_COLOR
)

ax.set_xlabel(
    '← JDK PriorityQueue Faster [Red]   |   Relative Speedup Factor   |   Custom PriorityQueue Faster [Blue] →',
    fontsize=11, labelpad=10, color=TEXT_COLOR, fontweight='bold'
)

ax.grid(False)
ax.tick_params(colors=TEXT_COLOR, which='both', length=0)

for spine in ax.spines.values():
    spine.set_edgecolor('#555555')
    spine.set_linewidth(0.8)

plt.tight_layout()
plt.savefig('transparent_test.png', dpi=300, bbox_inches='tight', transparent=True)
plt.close()

print("Saved transparent_test.png successfully")