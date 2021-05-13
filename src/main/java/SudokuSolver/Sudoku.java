package SudokuSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Sudoku {

    int[][] board;
    int[][] curBoard;
    boolean[][] isFixed;
    boolean solutionFound;
    int n;
    int sum;

    public Sudoku(int n, int[][] board){
        this.board = board;
        this.n = n;
    }

    public static void main(String[] args) {
        int[][] board = {
                {0, 2, 4, 0, 0, 7, 0, 0, 0},
                {6, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 6, 8, 0, 4, 1, 5},
                {4, 3, 1, 0, 0, 5, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 0, 3, 2},
                {7, 9, 0, 0, 0, 0, 0, 6, 0},
                {2, 0, 9, 7, 1, 0, 8, 0, 0},
                {0, 4, 0, 0, 9, 3, 0, 0, 0},
                {3, 1, 0, 0, 0, 4, 7, 5, 0}
        };
        Sudoku sudoku = new Sudoku(3, board);
        sudoku.readBoard();
        long t1 = System.currentTimeMillis();
        while(!sudoku.solutionFound) {
            sudoku.initialize();
            sudoku.simulatedAnnealingSolver();
            sudoku.printBoard();
        }
        System.out.println((System.currentTimeMillis()-t1) + "ms");
    }

    public void initialize(){
        curBoard = new int[board.length][board.length];
        for (int i = 0; i < n*n; i++)
            for (int j = 0; j < n*n; j++)
                curBoard[i][j] = board[i][j];
        for (int i = 0; i < n*n; i+=n)
            for (int j = 0; j < n*n; j+=n)
                initializeSmallSquare(i, j, i+n, j+n);
    }

    private void initializeSmallSquare(int x1, int y1, int x2, int y2){
        boolean[] hasNumber = new boolean[n*n+1];
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                hasNumber[curBoard[i][j]] = true;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n*n; i++)
            if(!hasNumber[i])
                list.add(i);
        Collections.shuffle(list);
        int cnt = 0;
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                if (curBoard[i][j] == 0) {
                    curBoard[i][j] = list.get(cnt);
                    hasNumber[list.get(cnt)] = true;
                    cnt++;
                }
            }
        }
    }

    // t -> time
    // T -> Temperature
    public void simulatedAnnealingSolver(){
        int noNew = 0;
        for (int t = 0; t < Integer.MAX_VALUE; t++) {
            double T = schedule(t);
            if(noNew >= 2000) {
                System.out.printf("Best Fitness: %2d\n", calculateFitness(curBoard));
//                return;
                t = 0;
                noNew = 0;
            }
            int[][] neighbor = expand();
            int fitnessNow = calculateFitness(curBoard);
            int fitnessNext = calculateFitness(neighbor);
//            System.out.println("T: " + T);
//            System.out.println("Time: " + t);
//            System.out.println("FitnessNow: " + fitnessNow);
//            System.out.println("fitnessNext: " + fitnessNext);
            if (fitnessNext == 0){
                solutionFound = true;
                curBoard = neighbor;
                return;
            }
            int delta = fitnessNow - fitnessNext;
            if(delta > 0 || probability(Math.exp(delta / T))){
                curBoard = neighbor;
                noNew = 0;
            }
            else
                noNew++;
        }
    }

    private static double schedule(int t){
//        return (200 * Math.exp(-0.0001 * t));
        return (40 * Math.pow(0.99, t));
    }

    private int[][] expand(){
        Random random = new Random();
        int[][] neighbor = new int[curBoard.length][curBoard.length];
        for (int i = 0; i < curBoard.length; i++) {
            for (int j = 0; j < curBoard.length; j++) {
                neighbor[i][j] = curBoard[i][j];
            }
        }
        boolean isModified1 = false;
        boolean isModified2 = false;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        while (!isModified1 || !isModified2) {
            int x = random.nextInt(n) * n;
            int y = random.nextInt(n) * n;
            isModified1 = false;
            isModified2 = false;
            int count = 0;

            do {
                x1 = random.nextInt(n) + x;
                y1 = random.nextInt(n) + y;
                count++;
                if (!isFixed[x1][y1]) {
                    isModified1 = true;
                    break;
                }
            } while (count <= 10);

            count = 0;
            do {
                x2 = random.nextInt(n) + x;
                y2 = random.nextInt(n) + y;
                count++;
                if (!isFixed[x2][y2] && !(x2 == x1 && y2 == y1)) {
                    isModified2 = true;
                    break;
                }
            } while (count <= 10);
        }
        int temp = neighbor[x1][y1];
        neighbor[x1][y1] = neighbor[x2][y2];
        neighbor[x2][y2] = temp;
        return neighbor;
    }

    private static boolean probability(double p){
        return p > Math.random();
    }

    public int calculateFitness(int[][] board){
        int fit = 0;
        int length = board.length;

        for (int i = 0; i < length; i++) {
            boolean[] hasOccur = new boolean[n*n];
            for (int j = 0; j < length; j++) {
                if(!hasOccur[board[i][j]-1])
                    hasOccur[board[i][j]-1] = true;
                else
                    fit++;
            }
        }

        for (int i = 0; i < length; i++) {
            boolean[] hasOccur = new boolean[n*n];
            for (int j = 0; j < length; j++) {
                if(!hasOccur[board[j][i]-1])
                    hasOccur[board[j][i]-1] = true;
                else
                    fit++;
            }
        }

        return fit;
    }

    public void readBoard(){
        sum = (1 + n*n) * n*n / 2;
        board = new int[n*n][n*n];
        isFixed = new boolean[n*n][n*n];
        for (int i = 0; i < n*n; i++) {
            for (int j = 0; j < n * n; j++) {
                if(board[i][j] != 0)
                    isFixed[i][j] = true;
            }
        }
    }

    public void printBoard(){
        for (int i = 0; i < n*n; i++) {
            for (int j = 0; j < n*n; j++) {
                System.out.printf("%2d ", curBoard[i][j]);
            }
            System.out.println();
        }
    }
}
