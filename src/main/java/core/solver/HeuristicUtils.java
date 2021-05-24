package core.solver;

import java.util.ArrayList;
import java.util.Random;

/**
 * @version 1.0
 * @date 2021/5/21 02:51
 */
public class HeuristicUtils {
    private static final Random RANDOM = new Random();
    private static final int HEURISTIC_NUM = 8;

    private final int n;
    private final int sum;
    private final int[] order;
    private int count;

    private int[] sumLine;
    private int[] sumColumn;
    private int sumDiagonal;
    private int sumBackDiagonal;

    private static class Pair {
        int x;
        int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public HeuristicUtils(int n, int sum) {
        this.n = n;
        this.sum = sum;
        this.sumLine = new int[n];
        this.sumColumn = new int[n];
        this.count = -1;

        this.order = new int[HEURISTIC_NUM];
        for (int i = 0; i < HEURISTIC_NUM; i++) {
            order[i] = i + 1;
        }
        for (int i = 0; i < HEURISTIC_NUM; i++) {
            int r = RANDOM.nextInt(HEURISTIC_NUM);
            int temp = order[i];
            order[i] = order[r];
            order[r] = temp;
        }
    }

    public int[][] getNextBoard(int[][] square, int[] sumLine, int[] sumColumn, int sumDiagonal, int sumBackDiagonal) {
        this.sumLine = sumLine.clone();
        this.sumColumn = sumColumn.clone();
        this.sumDiagonal = sumDiagonal;
        this.sumBackDiagonal = sumBackDiagonal;
        count = count == HEURISTIC_NUM - 1 ? 0 : count + 1;
        return useHeuristic(order[count], square);
    }

    private int[][] useHeuristic(int num, int[][] square) {
        switch (num) {
            case 1:
                return LLH1(square, false);
            case 2:
                return LLH2(square);
            case 3:
                return LLH3(square);
            case 4:
                return LLH1(square, true);
            case 5:
                return LLH5(square);
            case 6:
                return LLH6(square);
            case 7:
                return LLH7(square);
            case 8:
                return LLH9(square);
//            case 9: return LLH9(square);
            default:
                throw new IllegalArgumentException();
        }
    }

    private int[][] LLH1(int[][] square, boolean mustSatisfy) {
        ArrayList<Pair> badPairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (sumLine[i] != 0 || sumColumn[j] != 0) {
                    badPairs.add(new Pair(i, j));
                } else if ((i == j && sumDiagonal != 0)
                        || (i == n - j - 1 && sumBackDiagonal != 0)) {
                    badPairs.add(new Pair(i, j));
                }
            }
        }
        if (badPairs.size() <= 1) {
            return square;
        }

        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        if (mustSatisfy) {
            int r = RANDOM.nextInt(badPairs.size());
            Pair p = badPairs.get(r);
            Pair otherPair = new Pair(0, 0);
            if (sumLine[p.x] != 0) {
                int expect = newBoard[p.x][p.y] + sum - sumLine[p.x];
                int minDiff = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (i == p.x) {
                        continue;
                    }
                    for (int j = 0; j < n; j++) {
                        int diff = Math.abs(newBoard[i][j] - expect);
                        if (diff < minDiff) {
                            minDiff = diff;
                            otherPair.x = i;
                            otherPair.y = j;
                        }
                    }
                }
            } else if (sumColumn[p.y] != 0) {
                int expect = newBoard[p.x][p.y] + sum - sumColumn[p.y];
                int minDiff = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (i == p.y) {
                            continue;
                        }
                        int diff = Math.abs(newBoard[i][j] - expect);
                        if (diff < minDiff) {
                            minDiff = diff;
                            otherPair.x = i;
                            otherPair.y = j;
                        }
                    }
                }
            } else if (sumDiagonal != 0) {
                int expect = newBoard[p.x][p.y] + sum - sumDiagonal;
                int minDiff = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (i == j) {
                            continue;
                        }
                        int diff = Math.abs(newBoard[i][j] - expect);
                        if (diff < minDiff) {
                            minDiff = diff;
                            otherPair.x = i;
                            otherPair.y = j;
                        }
                    }
                }
            } else if (sumBackDiagonal != 0) {
                int expect = newBoard[p.x][p.y] + sum - sumBackDiagonal;
                int minDiff = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (i == n - 1 - j) {
                            continue;
                        }
                        int diff = Math.abs(newBoard[i][j] - expect);
                        if (diff < minDiff) {
                            minDiff = diff;
                            otherPair.x = i;
                            otherPair.y = j;
                        }
                    }
                }
            }
            int temp = newBoard[p.x][p.y];
            newBoard[p.x][p.y] = newBoard[otherPair.x][otherPair.y];
            newBoard[otherPair.x][otherPair.y] = temp;
        } else {
            int r1 = 0, r2 = 0;
            while (r1 == r2) {
                r1 = RANDOM.nextInt(badPairs.size());
                r2 = RANDOM.nextInt(badPairs.size());
            }
            Pair p1 = badPairs.get(r1);
            Pair p2 = badPairs.get(r2);
            int temp = newBoard[p1.x][p1.y];
            newBoard[p1.x][p1.y] = newBoard[p2.x][p2.y];
            newBoard[p2.x][p2.y] = temp;
        }

        return newBoard;
    }

    private int[][] LLH2(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int decision1 = RANDOM.nextInt(2 * n + 2);
        if (decision1 < n) {
            int row1 = RANDOM.nextInt(n);
            int row2 = RANDOM.nextInt(n);
            int[] tmp = newBoard[row1];
            newBoard[row1] = newBoard[row2];
            newBoard[row2] = tmp;
        } else if (decision1 < 2 * n) {
            int col1 = RANDOM.nextInt(n);
            int col2 = RANDOM.nextInt(n);
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

    private int[][] LLH3(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int minIndex = 0;
        int minSum = Integer.MAX_VALUE;
        int maxIndex = 0;
        int maxSum = Integer.MIN_VALUE;
        int minFlag = 0; // 1 for row, 2 for col, 3 for diagonal, 4 for back
        int maxFlag = 0; // 1 for row, 2 for col, 3 for diagonal, 4 for back
        for (int i = 0; i < n; i++) {
            if (minSum > sumLine[i]) {
                minSum = sumLine[i];
                minIndex = i;
                minFlag = 1;
            }
            if (maxSum < sumLine[i]) {
                maxSum = sumLine[i];
                maxIndex = i;
                maxFlag = 1;
            }
        }

        for (int i = 0; i < n; i++) {
            if (minSum > sumLine[i]) {
                minSum = sumLine[i];
                minIndex = i;
                minFlag = 2;
            }
            if (maxSum < sumLine[i]) {
                maxSum = sumLine[i];
                maxIndex = i;
                maxFlag = 2;
            }
        }

        if (minSum > sumDiagonal) {
            minSum = sumDiagonal;
            minFlag = 3;
        }
        if (maxSum < sumDiagonal) {
            maxSum = sumDiagonal;
            maxFlag = 3;
        }

        if (minSum > sumBackDiagonal) {
            minSum = sumBackDiagonal;
            minFlag = 4;
        }
        if (maxSum < sumBackDiagonal) {
            maxSum = sumBackDiagonal;
            maxFlag = 4;
        }

        Pair minPair, maxPair;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int minI = 0;
        int maxI = 0;
        switch (minFlag) {
            case 1:
                for (int i = 0; i < n; i++) {
                    if (min > newBoard[minIndex][i]) {
                        minI = i;
                        min = newBoard[minIndex][i];
                    }
                }
                minPair = new Pair(minIndex, minI);
                break;
            case 2:
                for (int i = 0; i < n; i++) {
                    if (min > newBoard[i][minIndex]) {
                        minI = i;
                        min = newBoard[i][minIndex];
                    }
                }
                minPair = new Pair(minI, minIndex);
                break;
            case 3:
                for (int i = 0; i < n; i++) {
                    if (min > newBoard[i][i]) {
                        minI = i;
                        min = newBoard[i][i];
                    }
                }
                minPair = new Pair(minI, minI);
                break;
            case 4:
                for (int i = 0; i < n; i++) {
                    if (min > newBoard[i][n - 1 - i]) {
                        minI = i;
                        min = newBoard[i][n - 1 - i];
                    }
                }
                minPair = new Pair(minI, n - 1 - minI);
                break;
            default:
                throw new IllegalArgumentException();
        }

        switch (maxFlag) {
            case 1:
                for (int i = 0; i < n; i++) {
                    if (max < newBoard[maxIndex][i]) {
                        maxI = i;
                        max = newBoard[maxIndex][i];
                    }
                }
                maxPair = new Pair(maxIndex, maxI);
                break;
            case 2:
                for (int i = 0; i < n; i++) {
                    if (max < newBoard[i][maxIndex]) {
                        maxI = i;
                        max = newBoard[i][maxIndex];
                    }
                }
                maxPair = new Pair(maxI, maxIndex);
                break;
            case 3:
                for (int i = 0; i < n; i++) {
                    if (max < newBoard[i][i]) {
                        maxI = i;
                        max = newBoard[i][i];
                    }
                }
                maxPair = new Pair(maxI, maxI);
                break;
            case 4:
                for (int i = 0; i < n; i++) {
                    if (max < newBoard[i][n - 1 - i]) {
                        maxI = i;
                        max = newBoard[i][n - 1 - i];
                    }
                }
                maxPair = new Pair(maxI, n - 1 - maxI);
                break;
            default:
                throw new IllegalArgumentException();
        }

        int temp = newBoard[maxPair.x][maxPair.y];
        newBoard[maxPair.x][maxPair.y] = newBoard[minPair.x][minPair.y];
        newBoard[minPair.x][minPair.y] = temp;

        return newBoard;
    }

    private int[][] LLH5(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int k = 0, l = 0;
        while (k == l) {
            k = RANDOM.nextInt(n);
            l = RANDOM.nextInt(n);
        }
        if (sumLine[k] + sumLine[l] == 0) {
            for (int i = 0; i < n; i++) {
                if (sumLine[k] == newBoard[k][i] - newBoard[l][i]) {
                    int temp = newBoard[k][i];
                    newBoard[k][i] = newBoard[l][i];
                    newBoard[l][i] = temp;
                    break;
                }
            }
        }

        k = 0;
        l = 0;
        while (k == l) {
            k = RANDOM.nextInt(n);
            l = RANDOM.nextInt(n);
        }
        if (sumColumn[k] + sumColumn[l] == 0) {
            for (int i = 0; i < n; i++) {
                if (sumColumn[k] == newBoard[i][k] - newBoard[i][l]) {
                    int temp = newBoard[i][k];
                    newBoard[i][k] = newBoard[i][l];
                    newBoard[i][l] = temp;
                    break;
                }
            }
        }

        return newBoard;
    }

    private int[][] LLH6(int[][] square) {
        ArrayList<Pair> badPairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (sumLine[i] == 0) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (sumColumn[j] != 0) {
                    if (i == j && sumDiagonal == 0) {
                        continue;
                    }
                    if (i == n - j - 1 && sumBackDiagonal == 0) {
                        continue;
                    }
                    badPairs.add(new Pair(i, j));
                }
            }
        }
        if (badPairs.size() <= 1) {
            return square;
        }

        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int r1 = 0, r2 = 0;
        while (r1 == r2) {
            r1 = RANDOM.nextInt(badPairs.size());
            r2 = RANDOM.nextInt(badPairs.size());
        }
        Pair p1 = badPairs.get(r1);
        Pair p2 = badPairs.get(r2);
        int temp = newBoard[p1.x][p1.y];
        newBoard[p1.x][p1.y] = newBoard[p2.x][p2.y];
        newBoard[p2.x][p2.y] = temp;

        return newBoard;
    }

    private int[][] LLH7(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int k = 0, l = 0;
        while (k == l) {
            k = RANDOM.nextInt(n);
            l = RANDOM.nextInt(n);
        }
        if (sumLine[k] + sumLine[l] == 0) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (sumLine[k] == newBoard[k][i] - newBoard[l][i]
                            + newBoard[k][j] - newBoard[l][j]) {
                        int temp = newBoard[k][i];
                        newBoard[k][i] = newBoard[l][i];
                        newBoard[l][i] = temp;

                        temp = newBoard[k][j];
                        newBoard[k][j] = newBoard[l][j];
                        newBoard[l][j] = temp;
                        break;
                    }
                }
            }
        }

        k = 0;
        l = 0;
        while (k == l) {
            k = RANDOM.nextInt(n);
            l = RANDOM.nextInt(n);
        }
        if (sumColumn[k] + sumColumn[l] == 0) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (sumColumn[k] == newBoard[i][k] - newBoard[i][l]
                            + newBoard[j][k] - newBoard[j][l]) {
                        int temp = newBoard[i][k];
                        newBoard[i][k] = newBoard[i][l];
                        newBoard[i][l] = temp;

                        temp = newBoard[j][k];
                        newBoard[j][k] = newBoard[j][l];
                        newBoard[j][l] = temp;
                        break;
                    }
                }
            }
        }

        return newBoard;
    }

    private int[][] LLH8(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        return newBoard;
    }

    private int[][] LLH9(int[][] square) {
        int[][] newBoard = new int[n][];
        for (int i = 0; i < n; i++) {
            newBoard[i] = square[i].clone();
        }

        int minIndex = 0;
        int minSum = Integer.MAX_VALUE;
        int maxIndex = 0;
        int maxSum = Integer.MIN_VALUE;
        int minFlag = 0; // 1 for row, 2 for col, 3 for diagonal, 4 for back
        int maxFlag = 0; // 1 for row, 2 for col, 3 for diagonal, 4 for back
        for (int i = 0; i < n; i++) {
            if (minSum > sumLine[i]) {
                minSum = sumLine[i];
                minIndex = i;
                minFlag = 1;
            }
            if (maxSum < sumLine[i]) {
                maxSum = sumLine[i];
                maxIndex = i;
                maxFlag = 1;
            }
        }

        for (int i = 0; i < n; i++) {
            if (minSum > sumLine[i]) {
                minSum = sumLine[i];
                minIndex = i;
                minFlag = 2;
            }
            if (maxSum < sumLine[i]) {
                maxSum = sumLine[i];
                maxIndex = i;
                maxFlag = 2;
            }
        }

        if (minSum > sumDiagonal) {
            minSum = sumDiagonal;
            minFlag = 3;
        }
        if (maxSum < sumDiagonal) {
            maxSum = sumDiagonal;
            maxFlag = 3;
        }

        if (minSum > sumBackDiagonal) {
            minSum = sumBackDiagonal;
            minFlag = 4;
        }
        if (maxSum < sumBackDiagonal) {
            maxSum = sumBackDiagonal;
            maxFlag = 4;
        }

        Pair start1, start2;
        Pair displace1, displace2;
        if (minFlag == 1) {
            start1 = new Pair(minIndex, 0);
            displace1 = new Pair(0, 1);
        } else if (minFlag == 2) {
            start1 = new Pair(0, minIndex);
            displace1 = new Pair(0, 1);
        } else if (minFlag == 3) {
            start1 = new Pair(0, 0);
            displace1 = new Pair(1, 1);
        } else {
            start1 = new Pair(0, n - 1);
            displace1 = new Pair(1, -1);
        }

        if (maxFlag == 1) {
            start2 = new Pair(maxIndex, 0);
            displace2 = new Pair(0, 1);
        } else if (maxFlag == 2) {
            start2 = new Pair(0, maxIndex);
            displace2 = new Pair(0, 1);
        } else if (maxFlag == 3) {
            start2 = new Pair(0, 0);
            displace2 = new Pair(1, 1);
        } else {
            start2 = new Pair(0, n - 1);
            displace2 = new Pair(1, -1);
        }

        for (int i = 0; i < n; i++) {
            if (RANDOM.nextDouble() < 0.5) {
                int temp = newBoard[start1.x][start1.y];
                newBoard[start1.x][start1.y] = newBoard[start2.x][start2.y];
                newBoard[start2.x][start2.y] = temp;
            }
            start1.x += displace1.x;
            start1.y += displace1.y;
            start2.x += displace2.x;
            start2.y += displace2.y;
        }

        return newBoard;
    }
}
