package web.task;

import web.model.TaskInfo;
import web.model.TaskState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 22:01
 */
public class SudokuTask extends Task {

    private static final int NOT_FIXED = 0;

    private int[][] board;
    private int[][] curBoard;
    private boolean[][] isFixed;
    int n, nSquare;
    int sum;

    public SudokuTask(int[][] board) {
        this.board = board;
        this.nSquare = board.length;
        this.n = (int) Math.round(Math.sqrt(nSquare));
        this.sum = (1 + nSquare) * nSquare / 2;
        this.isFixed = new boolean[nSquare][nSquare];
        for (int i = 0; i < nSquare; i++) {
            for (int j = 0; j < nSquare; j++) {
                if (board[i][j] != NOT_FIXED) {
                    isFixed[i][j] = true;
                }
            }
        }
    }

    @Override
    public TaskInfo getBoardState() {
        return new TaskInfo(getId(), taskState, curBoard);
    }

    @Override
    public void run() {
        super.run();
        while (taskState != TaskState.FINISHED) {
            initialize();
            simulatedAnnealingSolver();
            printBoard();
        }
    }

    public void initialize() {
        curBoard = new int[board.length][board.length];
        for (int i = 0; i < nSquare; i++) {
            for (int j = 0; j < nSquare; j++) {
                curBoard[i][j] = board[i][j];
            }
        }
        for (int i = 0; i < nSquare; i += n) {
            for (int j = 0; j < nSquare; j += n) {
                initializeSmallSquare(i, j, i + n, j + n);
            }
        }
    }

    private void initializeSmallSquare(int x1, int y1, int x2, int y2) {
        boolean[] hasNumber = new boolean[nSquare + 1];
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                hasNumber[curBoard[i][j]] = true;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 1; i <= nSquare; i++) {
            if (!hasNumber[i]) {
                list.add(i);
            }
        }
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
    public void simulatedAnnealingSolver() {
        int noNew = 0;
        for (int t = 0; t < Integer.MAX_VALUE; t++) {
            double T = schedule(t);
            if (noNew >= 2000) {
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
            if (fitnessNext == 0) {
                taskState = TaskState.FINISHED;
                curBoard = neighbor;
                return;
            }
            int delta = fitnessNow - fitnessNext;
            if (delta > 0 || probability(Math.exp(delta / T))) {
                curBoard = neighbor;
                noNew = 0;
            } else {
                noNew++;
            }

            if (t % 100 == 0) {
                sendBoardState();
            }
        }
    }

    private static double schedule(int t) {
//        return (200 * Math.exp(-0.0001 * t));
        return (40 * Math.pow(0.99, t));
    }

    private int[][] expand() {
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

    private static boolean probability(double p) {
        return p > Math.random();
    }

    public int calculateFitness(int[][] board) {
        int fit = 0;
        int length = board.length;

        for (int i = 0; i < length; i++) {
            boolean[] hasOccur = new boolean[nSquare];
            for (int j = 0; j < length; j++) {
                if (!hasOccur[board[i][j] - 1]) {
                    hasOccur[board[i][j] - 1] = true;
                } else {
                    fit++;
                }
            }
        }

        for (int i = 0; i < length; i++) {
            boolean[] hasOccur = new boolean[nSquare];
            for (int j = 0; j < length; j++) {
                if (!hasOccur[board[j][i] - 1]) {
                    hasOccur[board[j][i] - 1] = true;
                } else {
                    fit++;
                }
            }
        }

        return fit;
    }


    public void printBoard() {
        for (int i = 0; i < nSquare; i++) {
            for (int j = 0; j < nSquare; j++) {
                System.out.printf("%2d ", curBoard[i][j]);
            }
            System.out.println();
        }
    }
}
