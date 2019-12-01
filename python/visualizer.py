import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
import statistics

mpl.rcParams['font.family'] = 'Arial'

# ucfirst = lambda string: string[0].upper() + string[1:]

def make_graph(language, results_path_func, demos=('simultaneous', 'asymmetric', 'waiter'), sharey=True, logy=False, verbose=False):
    fig, axs = plt.subplots(1, len(demos), sharey=('row' if sharey else 'none'), figsize=(12, 4), dpi=300)
    if len(demos) == 1:
        axs = (axs,)
    fig.tight_layout(rect=[0.04, 0.03, 0.98, 0.95])
    axs[0].set_ylabel('Waiting time [s]')

    for demo_name, ax in zip(demos, axs):
        with open(results_path_func(demo_name), 'r') as f:
            ax.set_facecolor('#f6f6f6')
            ax.set_title(f'{language}, {demo_name}')
            ax.set_xlabel('Number of eaten meals')
            ax.yaxis.grid(color='gray', linewidth=0.15)
            ax.set_axisbelow(True)
            if logy:
                ax.set_yscale('log')

            data = [[float(time) / 1e9 for time in line.strip().rstrip(';').split(';')] for line in f.readlines()]
            if data:
                ax.boxplot(data, flierprops={'marker': '.'}, widths=0.25) # whis='range'
                ax.violinplot(data, showmeans=False, showmedians=False, showextrema=False, widths=0.75)
                ax.set_xticklabels(map(len, data))

                if verbose:
                    print(f'\ndemo: {demo_name} ({len(data)} philosophers)')
                    for i, times in enumerate(data, 1):
                        avg_time = statistics.mean(times)
                        print(f'philosopher #{i}: {len(times)} meals eaten, average time {avg_time:f} s')

    return fig

if __name__ == '__main__':
    for language, img_path, logy, results_path_func in (
        ('Java', 'java.png', False, lambda name: f'../java/zad1/resources/results/java_{name}.log'),
        ('Node.js', 'nodejs.png', True, lambda name: f'../nodejs/results/nodejs_{name}.log'),
        ('C', 'c.png', False, lambda name: f'../c/results/c_{name}.log')
    ):
        make_graph(language, results_path_func, logy=logy, verbose=True).savefig(img_path)

    #make_graph('Node.js', lambda name: f'../nodejs/results/nodejs_{name}.log', demos=('simultaneous', 'asymmetric', 'waiter'), sharey=False,verbose=True).savefig('nodejs.png')
