package MagicSquareSolver;

import java.util.Random;

public class HeuristicUtils {
    private static final Random random = new Random();
    private static final int HEURISTIC_NUM = 9;

    private int n;
    private int sum;
    private int[] order;
    private int count;

    public HeuristicUtils(int n, int sum){
        this.n = n;
        this.sum = sum;
        this.count = -1;

        this.order = new int[HEURISTIC_NUM];
        for (int i = 0; i < HEURISTIC_NUM; i++) {
            order[i] = i+1;
        }
        for (int i = 0; i < HEURISTIC_NUM; i++) {
            int r = random.nextInt(HEURISTIC_NUM);
            int temp = order[i];
            order[i] = order[r];
            order[r] = temp;
        }
    }

    public int[][] getNextBoard(int[][] square, int[] sumLine, int[] sumColumn) {
        count = count == HEURISTIC_NUM-1 ? 0 : count+1;
//        return useHeuristic(order[count], square);
        return useHeuristic(2, square, sumLine, sumColumn);
    }

    private int[][] useHeuristic(int num, int[][] square, int[] sumLine, int[] sumColumn){
        switch (num){
            case 1: return LLH1(square);
            case 2: return LLH2(square);
            case 3: return LLH3(square);
            case 4: return LLH4(square);
            case 5: return LLH5(square);
            case 6: return LLH6(square);
            case 7: return LLH7(square);
            case 8: return LLH8(square);
            case 9: return LLH9(square);
            default: throw new IllegalArgumentException();
        }
    }

    private int[][] LLH1(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

//        while(true){
//            int i = random.nextInt(n);
//            int j = random.nextInt(n);
//
//        }

        return newBoard;
    }

    private int[][] LLH2(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int decision1 = random.nextInt(2 * n + 2);
        if (decision1 < n) {
            int row1 = random.nextInt(n);
            int row2 = random.nextInt(n);
            int[] tmp = newBoard[row1];
            newBoard[row1] = newBoard[row2];
            newBoard[row2] = tmp;
        } else if (decision1 < 2*n) {
            int col1 = random.nextInt(n);
            int col2 = random.nextInt(n);
            for (int i = 0; i < n; i++) {
                int temp = newBoard[i][col1];
                newBoard[i][col1] = newBoard[i][col2];
                newBoard[i][col2] = temp;
            }
        } else {
            for (int i = 0; i < n; i++) {
                int temp = newBoard[i][i];
                newBoard[i][i] = newBoard[i][n - 1 - i];
                newBoard[i][n - 1 - i] = temp;
            }
        }

        return newBoard;
    }

    private int[][] LLH3(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH4(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH5(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH6(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH7(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH8(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH9(int[][] square){
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++){
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }
}
