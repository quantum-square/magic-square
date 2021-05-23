package web.task;

import MagicSquareSolver.HeuristicUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.model.TaskInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @version 1.0
 * @date 2021/5/15 22:01
 */
public class MagicSquareTask extends Task {

    private static final Logger logger = LoggerFactory.getLogger(MagicSquareTask.class);
    private static final int NOT_FIXED = 0;
    private static final Random RANDOM = new Random();
    private static final int INF = Integer.MAX_VALUE;

    /**
     * dimension
     */
    private final int n;
    /**
     * magic value
     */
    private final int sum;
    private final int[][] board;
    private int[][] curBoard;
    private final boolean[][] fixed;
    private final ArrayList<Integer> notFixedNumbers;
    private boolean hasFoundSolution;

    private final int[] curSumRow;
    private final int[] curSumColumn;
    private int curSumDiagonal;
    private int curSumBackDiagonal;

    private final int[] sumRow;
    private final int[] sumColumn;
    private int sumDiagonal;
    private int sumBackDiagonal;

    private int row1;
    private int col1;
    private int row2;
    private int col2;

    private final HeuristicUtils heuristicUtils;

    public MagicSquareTask(int[][] board) {
        this.n = board.length;
        this.board = board;
        this.sum = (1 + n * n) * n / 2;
        heuristicUtils = new HeuristicUtils(n, sum);
        notFixedNumbers = new ArrayList<>();
        curBoard = new int[n][n];
        for (int i = 1; i <= n * n; i++) {
            notFixedNumbers.add(i);
        }

        this.curSumRow = new int[n];
        this.curSumColumn = new int[n];
        this.sumRow = new int[n];
        this.sumColumn = new int[n];

        this.fixed = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != NOT_FIXED) {
                    fixed[i][j] = true;
                    if (!notFixedNumbers.remove((Integer) board[i][j])) {
                        logger.error("Wrong board from frontend, duplicate fixed numbers appeared: {}", board[i][j]);
                    }
                }
            }
        }
    }


    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo(getId(), taskState, curBoard);
    }

    @Override
    public void run() {
        super.run();

        initializeCurrentBoard();
        int curFitness = calculateFitness(curBoard);
        System.arraycopy(sumRow, 0, curSumRow, 0, n);
        System.arraycopy(sumColumn, 0, curSumColumn, 0, n);
        curSumDiagonal = sumDiagonal;
        curSumBackDiagonal = sumBackDiagonal;

        double coefficient = 0.75 * n * n;
        int count = 0;

        while (!hasFoundSolution) {
            int[][] newBoard;
            int fNew;

            if (curFitness > coefficient) {
                newBoard = randomSwap();
                fNew = updateFitness(newBoard, curFitness);
            } else {
                newBoard = generateCandidateSolution();
                fNew = calculateFitness(newBoard);
            }

            if (fNew == 0) {
                hasFoundSolution = true;
            } else if (fNew == INF) {
                continue;
            }

            if (fNew <= curFitness || n * Math.random() < 0.000038) {
                curBoard = newBoard;
                curFitness = fNew;
                System.arraycopy(sumRow, 0, curSumRow, 0, n);
                System.arraycopy(sumColumn, 0, curSumColumn, 0, n);
                curSumDiagonal = sumDiagonal;
                curSumBackDiagonal = sumBackDiagonal;
            }

            if (count % 100 == 0) {
                sendBoardState();
                ++count;
            }
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

    public int updateFitness(int[][] square, int lastFitness) {
        System.arraycopy(curSumRow, 0, sumRow, 0, n);
        System.arraycopy(curSumColumn, 0, sumColumn, 0, n);
        sumDiagonal = curSumDiagonal;
        sumBackDiagonal = curSumBackDiagonal;
        int delta1 = square[row1][col1] - square[row2][col2];
        int delta2 = -delta1;
        lastFitness -= Math.abs(sumRow[row1]) + Math.abs(sumRow[row2])
                + Math.abs(sumColumn[col1]) + Math.abs(sumColumn[col2]);
        sumRow[row1] += delta1;
        sumColumn[col1] += delta1;
        sumRow[row2] += delta2;
        sumColumn[col2] += delta2;
        lastFitness += Math.abs(sumRow[row1]) + Math.abs(sumRow[row2])
                + Math.abs(sumColumn[col1]) + Math.abs(sumColumn[col2]);

        lastFitness -= Math.abs(sumDiagonal) + Math.abs(sumBackDiagonal);
        if (row1 == col1) {
            sumDiagonal += delta1;
        }
        if (row2 == col2) {
            sumDiagonal += delta2;
        }
        if (row1 == n - col1 - 1) {
            sumBackDiagonal += delta1;
        }
        if (row2 == n - col2 - 1) {
            sumBackDiagonal += delta2;
        }
        lastFitness += Math.abs(sumDiagonal) + Math.abs(sumBackDiagonal);

        return lastFitness;
    }

    public int calculateFitness(int[][] square) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (fixed[i][j] && square[i][j] != board[i][j]) {
                    return INF;
                }
            }
        }
        renewSum(square);
        int fit = Math.abs(sumDiagonal) + Math.abs(sumBackDiagonal);

        for (int i = 0; i < n; i++) {
            fit += Math.abs(sumRow[i]);
            fit += Math.abs(sumColumn[i]);
        }

        return fit;
    }

    private void renewSum(int[][] square) {
        sumDiagonal = -this.sum;
        sumBackDiagonal = -this.sum;
        for (int i = 0; i < n; i++) {
            sumDiagonal += square[i][i];
            sumBackDiagonal += square[i][n - 1 - i];
            sumRow[i] = -this.sum;
            sumColumn[i] = -this.sum;
            for (int j = 0; j < n; j++) {
                sumRow[i] += square[i][j];
                sumColumn[i] += square[j][i];
            }
        }
    }

    /**
     * Hyper-heuristic: Random Permutation(RP)
     * <p>
     * This generates a permutation of low-level heuristics randomly,
     * and applies a low-level heuristic in the provided order sequentially
     *
     * @return new board
     */
    private int[][] generateCandidateSolution() {
        return heuristicUtils.getNextBoard(curBoard, sumRow, sumColumn, sumDiagonal, sumBackDiagonal);
    }

    private int[][] randomSwap() {
        int[][] newBoard = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(curBoard[i], 0, newBoard[i], 0, n);
        }

        boolean hasChanged = false;
        while (!hasChanged || fixed[row1][col1] || fixed[row2][col2]) {
            row1 = RANDOM.nextInt(n);
            row2 = RANDOM.nextInt(n);
            col1 = RANDOM.nextInt(n);
            col2 = RANDOM.nextInt(n);
            hasChanged = true;
        }

        int temp = newBoard[row1][col1];
        newBoard[row1][col1] = newBoard[row2][col2];
        newBoard[row2][col2] = temp;

        return newBoard;
    }

    public void printCurrentBoard() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%3d ", curBoard[i][j]);
            }
            System.out.println();
        }
    }

    public boolean checkValid() {
        int[][] square = curBoard;
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
}
