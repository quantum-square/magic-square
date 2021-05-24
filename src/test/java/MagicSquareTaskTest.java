import web.task.MagicSquareTask;

/**
 * @version 1.0
 * @date 2021/5/24 8:40
 */
public class MagicSquareTaskTest {

    public static void main(String[] args) {
        testNormal();
        testConstraint();
    }

    /**
     * Run 30 times with no constraint and get the average.
     */
    public static int testNormal() {
        int sum = 0;
        final int N = 30; // run N time

        for (int i = 0; i < N; i++) {
            long start = System.currentTimeMillis();

            MagicSquareTask mss = new MagicSquareTask(new int[10][10]);
            mss.run();

            long end = System.currentTimeMillis();
            sum += end - start;

//            msh.printCurrentBoard();
//            if(msh.checkValid(msh.curBoard)){
//                System.out.println("Congratulation!");
//            }

            System.out.println(end - start + " ms");
        }

        System.out.println("Average: " + sum / N + " ms");
        return sum / N; // return the average
    }

    /**
     * Run 30 times with some places fixed and get the average.
     */
    public static int testConstraint() {

        int sum = 0;
        final int N = 30; // run N time

        for (int i = 0; i < N; i++) {

            int[][] constraintBoard = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 4, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };

            long start = System.currentTimeMillis();

            MagicSquareTask mss = new MagicSquareTask(constraintBoard);
            mss.run();


            long end = System.currentTimeMillis();
            sum += end - start;

            printCurrentBoard(mss.getCurBoard());
            if (checkValid(mss.getCurBoard())) {
                System.out.println("Congratulation!");
            }

            System.out.println(end - start + " ms");
        }

        System.out.println("Average: " + sum / N + " ms");
        return sum / N; // return the average
    }

    private static void printCurrentBoard(int[][] board) {
        for (int[] ints : board) {
            for (int anInt : ints) {
                System.out.printf("%3d ", anInt);
            }
            System.out.println();
        }
    }

    private static boolean checkValid(int[][] square) {
        int n = square.length;
        int sum = (1 + n * n) * n / 2;
        for (int i = 0; i < n; i++) {
            int curSum = 0;
            for (int j = 0; j < n; j++) {
                curSum += square[i][j];
            }
            if (curSum != sum) return false;
        }

        for (int i = 0; i < n; i++) {
            int curSum = 0;
            for (int j = 0; j < n; j++) {
                curSum += square[j][i];
            }
            if (curSum != sum) return false;
        }

        int curSum = 0;
        for (int i = 0; i < n; i++) {
            curSum += square[i][i];
        }
        if (curSum != sum) return false;

        curSum = 0;
        for (int i = 0; i < n; i++) {
            curSum += square[i][n - 1 - i];
        }
        if (curSum != sum) return false;

        return true;
    }
}
