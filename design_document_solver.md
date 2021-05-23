# Design Document of the solvers

## 1 Magic Square solver

I have to say that this is a really exciting part. The main idea is from Ahmed and Ender [1], 
using hyper-heuristics.

### 1.1 Representations

- `n`: the size of the magic square

- `sum`: magic value, to be specific, the sum of each row, column or diagonal

- `LLH`: low level heuristics, chosen by the hyper heuristic which to be executed

### 1.2 Origin Methodology

In [1], two sets of hyper-heuristic methods are mentioned, one for small squares with `n <= 23`, 
and another for large squares with `25 <= n <= 2600`. The main requirement of this project is to
solve squares of max size 20, so only the first hyper-heuristic method will be talked here.

Selection hyper-heuristic means "the heuristic to choose heuristic". The 

### 1.3 Our Improvement



### 1.4 Performance

We performed our experiments on an i7-10870H CPU at 2.20 GHz with a memory of 16.00 GB and each 
one is repeated for 30 trials.

|  n  | average time |
| --- | ------------ |
|  3  |   73 ms |
|  5  |  788 ms |
|  10 |   47 ms |
|  15 |   98 ms |
|  20 |  238 ms |
|  30 | 1324 ms |
|  40 | 3794 ms |

## 2 Sudoku solver

## Reference

[1] *Ahmed Kheiri, Ender Özcan, Constructing Constrained-Version of Magic Squares Using 
Selection Hyper-heuristics, The Computer Journal, Volume 57, Issue 3, March 2014, Pages 469–479, 
https://doi.org/10.1093/comjnl/bxt130*