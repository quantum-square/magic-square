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
    private int[] sumLine;
    private int[] sumColumn;
    private int sumDiagonal;
    private int sumBackDiagonal;

    public MagicSquareHeuristic(int n, int[][] board) {
        this.n = n;
        this.board = board;
        this.sum = (1 + n * n) * n / 2;
        notFixedNumbers = new ArrayList<>();
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

    // Main Approach: Late Acceptance Hill-Climbing
    public void heuristicSolver(){
        initializeCurrentBoard();
    }

    private void initializeCurrentBoard() {
        curBoard = new int[n][n];
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

        //TODO: add renew

        for (int i = 0; i < n; i++) {
            int sumLine = 0;
            for (int j = 0; j < n; j++)
                sumLine += square[i][j];
            fit += Math.abs(sumLine - this.sum);
        }

        for (int i = 0; i < n; i++) {
            int sumColumn = 0;
            for (int j = 0; j < n; j++)
                sumColumn += square[j][i];
            fit += Math.abs(sumColumn - this.sum);
        }

        int sumDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumDiagonal += square[i][i];
        fit += Math.abs(sumDiagonal - this.sum);

        int sumBackDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumBackDiagonal += square[i][n-1-i];
        fit += Math.abs(sumBackDiagonal - this.sum);

        return fit;
    }

    public static void main(String[] args) {

    }

}
