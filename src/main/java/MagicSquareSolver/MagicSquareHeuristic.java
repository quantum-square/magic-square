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
    private boolean hasFoundSolution;

    private final HeuristicUtils heuristicUtils;

    public MagicSquareHeuristic(int n) {
        this(n, new int[n][n]);
    }

    public MagicSquareHeuristic(int n, int[][] board) {
        this.n = n;
        this.board = board;
        this.sum = (1 + n * n) * n / 2;
        heuristicUtils = new HeuristicUtils(n, sum);
        this.sumLine = new int[n];
        this.sumColumn = new int[n];
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

    /**
     * Main Approach: Late Acceptance Hill-Climbing
     * @param L the queue to maintain the history of solution function values
     */
    public void heuristicSolver(int L){
        initializeCurrentBoard();
        int f0 = calculateFitness(curBoard);
        int[] queue = new int[L];
        for (int i = 0; i < L; i++) {
            queue[i] = f0;
        }

        for (int i = 0; !hasFoundSolution; i++) {
            int[][] newBoard = generateCandidateSolution();
            int f = calculateFitness(newBoard);
            if (f == 0) {
                hasFoundSolution = true;
            }
            int c = i % L;
            if (f <= queue[c]) {
                curBoard = newBoard;
            }
            queue[c] = f;
            System.out.println("-----");
            printCurrentBoard();
        }
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

    private void renewSum(int[][] square) {
        for (int i = 0; i < n; i++) {
            sumLine[i] = 0;
            for (int j = 0; j < n; j++)
                sumLine[i] += square[i][j];
            sumLine[i] = sumLine[i] - this.sum;
        }

        for (int i = 0; i < n; i++) {
            sumColumn[i] = 0;
            for (int j = 0; j < n; j++)
                sumColumn[i] += square[j][i];
            sumColumn[i] += sumColumn[i] - this.sum;
        }

        sumDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumDiagonal += square[i][i];
        sumDiagonal = sumDiagonal - this.sum;

        sumBackDiagonal = 0;
        for (int i = 0; i < n; i++)
            sumBackDiagonal += square[i][n-1-i];
        sumBackDiagonal = sumBackDiagonal - this.sum;
    }

    private int calculateFitness(int[][] square) {
        renewSum(square);
        int fit = 0;
        for (int i = 0; i < n; i++) {
            fit += Math.abs(sumLine[i]);
            fit += Math.abs(sumColumn[i]);
        }
        fit += Math.abs(sumDiagonal);
        fit += Math.abs(sumBackDiagonal);
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
        return heuristicUtils.getNextBoard(curBoard, sumLine, sumColumn);
    }

    private void printCurrentBoard(){
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(curBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MagicSquareHeuristic msh = new MagicSquareHeuristic(3);
        msh.heuristicSolver(1000);
        msh.printCurrentBoard();
    }

}
