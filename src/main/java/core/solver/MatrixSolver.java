package core.solver;

import core.model.SolverState;

/**
 * @version 2.0
 * @date 2021/5/23 22:54
 */
public abstract class MatrixSolver extends Thread{

    /**
     * dimension
     */
    int n;
    /**
     * input board
     */
    int[][] board;
    int[][] curBoard;
    boolean[][] fixed;
    long solverId;
    SolverState solverState;
    long startTimestamp = 0;
    long endTimestamp = 0;
    long pauseTimestamp = 0;
    long pauseTimes = 0;

    public long getSolverId() {
        return solverId;
    }

    public SolverState getSolverState() {
        return solverState;
    }

    public void setSolverState(SolverState solverState) {
        this.solverState = solverState;
    }

    public int[][] getCurBoard() {
        return curBoard;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getPauseTimestamp() {
        return pauseTimestamp;
    }

    public void setPauseTimestamp(long pauseTimestamp) {
        this.pauseTimestamp = pauseTimestamp;
    }

    public long getPauseTimes() {
        return pauseTimes;
    }

    public void addPauseTimes(long pauseTimes) {
        this.pauseTimes += pauseTimes;
    }
}
