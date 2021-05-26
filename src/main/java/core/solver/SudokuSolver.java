package core.solver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.model.SolverInfoDTO;
import core.model.SolverState;
import io.javalin.websocket.WsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.model.WebSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @version 2.0
 * @date 2021/5/23 22:55
 */
public class SudokuSolver extends MatrixSolver implements WebSender {
    private static final Logger logger = LoggerFactory.getLogger(SudokuSolver.class);
    private static int sendFreq = 1000;
    private static final int NOT_FIXED = 0;
    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper();
    private WsContext sender;

    private final int nSquare;

    public SudokuSolver(int[][] board) {
        super();
        this.board = board;
        this.nSquare = board.length;
        this.n = (int) Math.round(Math.sqrt(nSquare));
        this.fixed = new boolean[nSquare][nSquare];
        for (int i = 0; i < nSquare; i++) {
            for (int j = 0; j < nSquare; j++) {
                if (board[i][j] != NOT_FIXED) {
                    fixed[i][j] = true;
                }
            }
        }
        this.curBoard = board;
        this.solverId = getId();
    }

    @Override
    public void run() {
        super.run();
        while (solverState != SolverState.FINISHED) {
            initialize();
            simulatedAnnealingSolver();
            //printBoard();
        }
        sendData();
    }

    @Override
    public void setSender(Object sender) {
        if (sender instanceof WsContext) {
            this.sender = (WsContext) sender;
        }
    }

    @Override
    public void sendData() {
        if (sender != null) {
            try {
                long timeCost;
                switch (solverState) {
                    case NEW:
                        timeCost = 0;
                        break;
                    case FINISHED:
                        timeCost = endTimestamp - startTimestamp - pauseTimes;
                        break;
                    default:
                        timeCost = System.currentTimeMillis() - startTimestamp - pauseTimes;
                        break;

                }
                sender.send(mapper.writeValueAsString(new SolverInfoDTO(solverId, solverState, timeCost, curBoard)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getSendFreq() {
        return sendFreq;
    }

    public static void setSendFreq(int sendFreq) {
        logger.info("Change Sudoku Solver send freq from {} to {}.", SudokuSolver.sendFreq, sendFreq);
        SudokuSolver.sendFreq = sendFreq;
    }

    private void initialize() {
        curBoard = new int[board.length][board.length];
        for (int i = 0; i < nSquare; i++) {
            System.arraycopy(board[i], 0, curBoard[i], 0, nSquare);
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

    private void simulatedAnnealingSolver() {
        int noNew = 0;
        // t -> time
        // T -> Temperature
        for (int t = 0; t < Integer.MAX_VALUE; t++) {
            double T = schedule(t);
            if (noNew >= 2000) {
                //logger.trace("Best Fitness: {}\n", calculateFitness(curBoard));
                t = 0;
                noNew = 0;
            }
            int[][] neighbor = expand();
            int fitnessNow = calculateFitness(curBoard);
            int fitnessNext = calculateFitness(neighbor);
            //logger.trace("T: {}, Time: {}, FitnessNow: {}, FitnessNext: {}", T,t,fitnessNow,fitnessNext);
            if (fitnessNext == 0) {
                solverState = SolverState.FINISHED;
                curBoard = neighbor;
                break;
            }
            int delta = fitnessNow - fitnessNext;
            if (delta > 0 || probability(Math.exp(delta / T))) {
                curBoard = neighbor;
                noNew = 0;
            } else {
                noNew++;
            }

            if (t % sendFreq == 0) {
                sendData();
            }
        }
    }

    private static double schedule(int t) {
//        return (200 * Math.exp(-0.0001 * t));
        return (40 * Math.pow(0.99, t));
    }

    private int[][] expand() {
        int[][] neighbor = new int[curBoard.length][curBoard.length];
        for (int i = 0; i < curBoard.length; i++) {
            System.arraycopy(curBoard[i], 0, neighbor[i], 0, curBoard.length);
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
                if (!fixed[x1][y1]) {
                    isModified1 = true;
                    break;
                }
            } while (count <= 10);

            count = 0;
            do {
                x2 = random.nextInt(n) + x;
                y2 = random.nextInt(n) + y;
                count++;
                if (!fixed[x2][y2] && !(x2 == x1 && y2 == y1)) {
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

    private int calculateFitness(int[][] board) {
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

}
