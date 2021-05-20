package MagicSquareSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MagicSquareHeuristic {

    private static final int NOT_FIXED = 0;
    private static final Random random = new Random();

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
     * Main Approach: Naive move acceptance
     */
    public void heuristicSolver(){
        initializeCurrentBoard();

        int curFitness = calculateFitness(curBoard);

        while(!hasFoundSolution) {
            int[][] newBoard = curFitness <= n*n ?
                    generateCandidateSolution() : randomSwap();
//            int[][] newBoard = generateCandidateSolution();
//            int[][] newBoard = randomSwap();
            int fNew = calculateFitness(newBoard);
            if (fNew == 0) {
                hasFoundSolution = true;
            }
            if (fNew <= curFitness) {
                curBoard = newBoard;
                curFitness = fNew;
            }
            else if(Math.random() < 0.000038 / n) {
                curBoard = newBoard;
                curFitness = fNew;
            }

//            if (i % 1000000 == 0) {
//                System.out.println("Current fitness:    " + fNew);
//                System.out.println("Best fitness:       " + curFitness);
//                System.out.println("Current generation: " + i);
//                  printCurrentBoard();
//                System.out.println("--------------------------");
//            }
        }
        printCurrentBoard();
        System.out.println("------------------------------------------------");
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

    private int[][] randomSwap() {
        int[][] newBoard = new int[n][n];
        for (int i = 0; i < n; i++){
//            newBoard[i] = curBoard[i].clone();
            System.arraycopy(curBoard[i], 0, newBoard[i], 0, n);
        }

        int row1 = random.nextInt(n);
        int row2 = random.nextInt(n);
        int col1 = random.nextInt(n);
        int col2 = random.nextInt(n);

        int temp = newBoard[row1][col1];
        newBoard[row1][col1] = newBoard[row2][col2];
        newBoard[row2][col2] = temp;

        return newBoard;
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
        int sum = 0;
        final int N = 5;

        for (int i = 0; i < N; i++) {
            long start = System.currentTimeMillis();

            MagicSquareHeuristic msh = new MagicSquareHeuristic(20, 1);
            msh.heuristicSolver();

            long end = System.currentTimeMillis();
            sum += end - start;

            System.out.println(end - start + " ms");
        }

        System.out.println(sum / N + " ms");
    }

}
