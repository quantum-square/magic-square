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
        initializeCurrentBoard();
        int f0 = calculateFitness(curBoard);
        int[] queue = new int[L];
        for (int i = 0; i < L; i++) {
            queue[i] = f0;
        }

        for (int i = 0; !hasFoundSolution; i++) {
            int c = i % L;
            int[][] newBoard = generateCandidateSolution();
            int f = calculateFitness(newBoard);
            if (f == 0) {
                hasFoundSolution = true;
            }
            if (f <= queue[c]) {
                curBoard = newBoard;
                queue[c] = f;
            }
            System.out.println("Current fitness:    " + f);
            System.out.println("Best fitness:       " + queue[c]);
            System.out.println("Current generation: " + i);
            printCurrentBoard();
            System.out.println("--------------------------");
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

        MagicSquareHeuristic msh = new MagicSquareHeuristic(3, 1);
        msh.heuristicSolver();

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }

}
