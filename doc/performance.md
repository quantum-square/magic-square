# Performance

## Magic Square

We performed our experiments on an `intel i7 CPU @ 2.20 GHz with 16.00 GB memory` and each one is repeated for 30 trials. The last one is using constraint by fixing a 3*3 submatrix to number 1 to 9. The result is shown in the table below:

|  n  | average time |
| --- | ------------ |
|  3 |   73 ms |
|  5 |  742 ms |
| 10 |   47 ms |
| 15 |   98 ms |
| 20 |  194 ms |
| 30 | 1117 ms |
| 40 | 3794 ms |
| 20 (c) | 216 ms |

When doubling the size, it is harder to converge. From 10 to 20, it is 3 times bigger. From 20 to 40, it is about 15 times bigger.

From the experiments we do, if we use the same algorithm to solve large squares, the running time may grow **exponentially**.

## Sudoku

We performed our experiments on an `intel i7 CPU @ 2.20 GHz with 16.00 GB memory` and each one is repeated for 30 trials with about 50% of the grids fixed. The result is shown in the table below:

|  n  | average time |
| --- | ------------ |
|  3  |    31 ms     |

`50% fixed` is the hardest ratio to find the solution. When we achieve good performance on this, then the other ratio should be easy to solve in a satisfying time.

However, it is hard to solve larger sudoku because simulated annealing is a random algorithm but sudoku has more constraints rather than magic square. So maybe larger sudoku is hard to solve by our algorithm.
