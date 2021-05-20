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

    private int[] sumLine;
    private int[] sumColumn;
    private int sumDiagonal;
    private int sumBackDiagonal;
    
    private int[] curSumLine;
    private int[] curSumColumn;
    private int curSumDiagonal;
    private int curSumBackDiagonal;

    public int row1;
    public int col1;
    public int row2;
    public int col2;

    private final HeuristicUtils heuristicUtils;

    public MagicSquareHeuristic(int n) {
        this(n, new int[n][n]);
    }

    public MagicSquareHeuristic(int n, int[][] board) {
        this.n = n;
        this.board = board;
        this.sum = (1 + n * n) * n / 2;
        heuristicUtils = new HeuristicUtils(n, sum);
        notFixedNumbers = new ArrayList<>();
        curBoard = new int[n][n];
        for (int i = 1; i <= n*n; i++) {
            notFixedNumbers.add(i);
        }

        this.sumLine = new int[n];
        this.sumColumn = new int[n];
        this.curSumLine = new int[n];
        this.curSumColumn = new int[n];

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
        System.arraycopy(curSumLine, 0, sumLine, 0, n);
        System.arraycopy(curSumColumn, 0, sumColumn, 0, n);
        sumDiagonal = curSumDiagonal;
        sumBackDiagonal = curSumBackDiagonal;

        double coefficient = n * n;

        while(!hasFoundSolution) {
            int[][] newBoard = curFitness <= coefficient ?
                    generateCandidateSolution() : randomSwap();
//            int[][] newBoard = generateCandidateSolution();
//            int[][] newBoard = randomSwap();

            int fNew = curFitness <= coefficient ?
                    calculateFitness(newBoard) : updateFitness(newBoard, curFitness);

            if (fNew == 0) {
                hasFoundSolution = true;
            }
            if (fNew <= curFitness) {
                curBoard = newBoard;
                curFitness = fNew;
                System.arraycopy(curSumLine, 0, sumLine, 0, n);
                System.arraycopy(curSumColumn, 0, sumColumn, 0, n);
                sumDiagonal = curSumDiagonal;
                sumBackDiagonal = curSumBackDiagonal;
            }
            else if(Math.random() < 0.000038 / n) {
                curBoard = newBoard;
                curFitness = fNew;
                System.arraycopy(curSumLine, 0, sumLine, 0, n);
                System.arraycopy(curSumColumn, 0, sumColumn, 0, n);
                sumDiagonal = curSumDiagonal;
                sumBackDiagonal = curSumBackDiagonal;
            }

//            if (i % 1000000 == 0) {
//                System.out.println("Current fitness:    " + fNew);
//                System.out.println("Best fitness:       " + curFitness);
//                System.out.println("Current generation: " + i);
//                  printCurrentBoard();
//                System.out.println("--------------------------");
//            }
        }
//        printCurrentBoard();
//        System.out.println("------------------------------------------------");
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

    public int updateFitness(int[][] square, int lastFitness) {
        System.arraycopy(sumLine, 0, curSumLine, 0, n);
        System.arraycopy(sumColumn, 0, curSumColumn, 0, n);
        curSumDiagonal = sumDiagonal;
        curSumBackDiagonal = sumBackDiagonal;
        int delta1 = square[row1][col1] - square[row2][col2];
        int delta2 = -delta1;
        lastFitness -= Math.abs(curSumLine[row1]) + Math.abs(curSumLine[row2])
                + Math.abs(curSumColumn[col1]) + Math.abs(curSumColumn[col2]);
        curSumLine[row1] += delta1;
        curSumColumn[col1] += delta1;
        curSumLine[row2] += delta2;
        curSumColumn[col2] += delta2;
        lastFitness += Math.abs(curSumLine[row1]) + Math.abs(curSumLine[row2])
                + Math.abs(curSumColumn[col1]) + Math.abs(curSumColumn[col2]);

        lastFitness -= Math.abs(curSumDiagonal) + Math.abs(curSumBackDiagonal);
        if (row1 == col1) {
            curSumDiagonal += delta1;
        }
        if (row2 == col2) {
            curSumDiagonal += delta2;
        }
        if (row1 == n - col1 - 1) {
            curSumBackDiagonal += delta1;
        }
        if (row2 == n - col2 - 1) {
            curSumBackDiagonal += delta2;
        }
        lastFitness += Math.abs(curSumDiagonal) + Math.abs(curSumBackDiagonal);

        return lastFitness;
    }

    public int calculateFitness(int[][] square) {
        renewSum(square);
        int fit = 0;

//        for (int i = 0; i < n; i++) {
//            int sumLine = 0;
//            for (int j = 0; j < n; j++) {
//                sumLine += square[i][j];
//            }
//            fit += Math.abs(sumLine - this.sum);
//        }
//
//        for (int i = 0; i < n; i++) {
//            int sumColumn = 0;
//            for (int j = 0; j < n; j++) {
//                sumColumn += square[j][i];
//            }
//            fit += Math.abs(sumColumn - this.sum);
//        }
//
//        int sumDiagonal = 0;
//        for (int i = 0; i < n; i++)
//            sumDiagonal += square[i][i];
//        fit += Math.abs(sumDiagonal - this.sum);
//
//        sumDiagonal = 0;
//        for (int i = 0; i < n; i++)
//            sumDiagonal += square[i][n-i-1];
//        fit += Math.abs(sumDiagonal - this.sum);

        for (int i = 0; i < n; i++) {
            fit += Math.abs(curSumLine[i]);
            fit += Math.abs(curSumColumn[i]);
        }

        fit += Math.abs(curSumDiagonal);
        fit += Math.abs(curSumBackDiagonal);

        return fit;
    }

    private void renewSum(int[][] square) {
        for (int i = 0; i < n; i++) {
            curSumLine[i] = 0;
            for (int j = 0; j < n; j++)
                curSumLine[i] += square[i][j];
            curSumLine[i] = curSumLine[i] - this.sum;
        }

        for (int i = 0; i < n; i++) {
            curSumColumn[i] = 0;
            for (int j = 0; j < n; j++)
                curSumColumn[i] += square[j][i];
            curSumColumn[i] = curSumColumn[i] - this.sum;
        }

        curSumDiagonal = 0;
        for (int i = 0; i < n; i++)
            curSumDiagonal += square[i][i];
        curSumDiagonal = curSumDiagonal - this.sum;

        curSumBackDiagonal = 0;
        for (int i = 0; i < n; i++)
            curSumBackDiagonal += square[i][n-1-i];
        curSumBackDiagonal = curSumBackDiagonal - this.sum;
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

        row1 = random.nextInt(n);
        row2 = random.nextInt(n);
        col1 = random.nextInt(n);
        col2 = random.nextInt(n);

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

    /**
     * Run N times and get the average.
     * @param p this is for SA to adjust the parameter (worsen acceptance rate)
     * @param x this is also for SA to adjust the parameter
     */
    public static int test(double x, double p) {
        int sum = 0;
        final int N = 30; // run N time

        for (int i = 0; i < N; i++) {
            long start = System.currentTimeMillis();

            MagicSquareHeuristic msh = new MagicSquareHeuristic(20);
            msh.heuristicSolver();

            long end = System.currentTimeMillis();
            sum += end - start;

            msh.printCurrentBoard();
            if(msh.checkValid(msh.curBoard)){
                System.out.println("Congratulation!");
            }

            System.out.println(end - start + " ms");
        }

        System.out.println("Average: " + sum / N + " ms");
        return sum / N; // return the average
    }

    private boolean checkValid(int[][] square) {
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += square[i][j];
            }
            if (sum != this.sum) return false;
        }

        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += square[j][i];
            }
            if (sum != this.sum) return false;
        }

        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += square[i][i];
        }
        if (sum != this.sum) return false;

        sum = 0;
        for (int i = 0; i < n; i++) {
            sum += square[i][n-1-i];
        }
        if (sum != this.sum) return false;

        return true;
    }

    public static void main(String[] args) { test(1, 1); }

}
