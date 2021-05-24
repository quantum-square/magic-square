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
}
