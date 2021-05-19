package MagicSquareSolver;

import java.util.ArrayList;
import java.util.Collections;

public class MagicSquareHeuristic {

    private static final int NOT_FIXED = 0;

    private int n; // dimension
    private int sum; // magic value
    private int[][] board;
    private int[][] curBoard;
    private boolean[][] fixed;
    private ArrayList<Integer> notFixedNumbers;
    private boolean hasFoundSolution;

    private final HeuristicUtils heuristicUtils;
    private final int L;

    public MagicSquareHeuristic(int n, int L) {
        this(n, new int[n][n], L);
    }

    public MagicSquareHeuristic(int n, int[][] board, int L) {
        this.n = n;
        this.board = board;
        this.L = L;
        this.sum = (1 + n * n) * n / 2;
        heuristicUtils = new HeuristicUtils(n, sum);
        notFixedNumbers = new ArrayList<>();
        curBoard = new int[n][n];
        for (int i = 1; i <= n*n; i++) {
            notFixedNumbers.add(i);
        }

        this.fixed = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != NOT_FIXED) {
                    fixed[i][j] = true;
                    if (!notFixedNumbers.remove((Integer) board[i][j])){
                        System.err.println("wrong board from frontend");
                    }
                }
            }
        }
    }

    /**
     * Main Approach: Late Acceptance Hill-Climbing
     */
    public void heuristicSolver(){
        while(!hasFoundSolution) {
            initializeCurrentBoard();

            int f0 = calculateFitness(curBoard);
            int[] queue = new int[L];
            for (int i = 0; i < L; i++) {
                queue[i] = f0;
            }

//            int iIdle = 0;
            for (int i = 0;
//                 (i < n * 100000 || iIdle <= i * 0.02) &&
                         !hasFoundSolution; i++) {
//                int c = i % L;
                int[][] newBoard = generateCandidateSolution();
                int fNew = calculateFitness(newBoard);
                if (fNew == 0) {
                    hasFoundSolution = true;
                }
//                int fOld = calculateFitness(curBoard);
//                if (fNew >= fOld) {
//                    iIdle++;
//                } else {
//                    iIdle = 0;
//                }
//                if (fNew <= queue[c] || fNew <= fOld) {
//                    curBoard = newBoard;
//                }
//                if (Math.min(fNew, fOld) < queue[c])
//                    queue[c] = Math.min(fNew, fOld);
                if (fNew <= queue[0]) {
                    curBoard = newBoard;
                    queue[0] = fNew;
                }
                else if(Math.random() < 0.000007) {
                    curBoard = newBoard;
                    queue[0] = fNew;
                }

                System.out.println("Current fitness:    " + fNew);
                System.out.println("Best fitness:       " + queue[0]);
                System.out.println("Current generation: " + i);
//            printCurrentBoard();
                System.out.println("--------------------------");
            }
            printCurrentBoard();
        }
    }

    private void initializeCurrentBoard() {
        Collections.shuffle(notFixedNumbers);

        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == NOT_FIXED) {
                    curBoard[i][j] = notFixedNumbers.get(count);
                    count++;
                } else {
                    curBoard[i][j] = board[i][j];
                }
            }
        }
    }

    private int calculateFitness(int[][] square) {
        int fit = 0;

        for (int i = 0; i < n; i++) {
            int sumLine = 0;
            for (int j = 0; j < n; j++) {
                sumLine += square[i][j];
            }
            fit += Math.abs(sumLine - this.sum);
        }

        for (int i = 0; i < n; i++) {
            int sumColumn = 0;
            for (int j = 0; j < n; j++) {
                sumColumn += square[j][i];
            }
            fit += Math.abs(sumColumn - this.sum);
        }

        int sumDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumDiagonal += square[i][i];
        fit += Math.abs(sumDiagonal - this.sum);

        sumDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumDiagonal += square[i][n-i-1];
        fit += Math.abs(sumDiagonal - this.sum);

        return fit;
    }

    /**
     * Hyper-heuristic: Random Permutation(RP)
     *
     * This generates a permutation of low-level heuristics randomly,
     * and applies a low-level heuristic in the provided order sequentially
     * @return new board
     */
    private int[][] generateCandidateSolution(){
        return heuristicUtils.getNextBoard(curBoard);
    }

    private void printCurrentBoard(){
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%3d ", curBoard[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        MagicSquareHeuristic msh = new MagicSquareHeuristic(20, 1);
        msh.heuristicSolver();

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

}
