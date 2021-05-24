package web.model;

import core.model.SolverState;

/**
 * @version 2.0
 * @date 2021/5/23 23:04
 */
public class SolverInfoDTO {

    Long solverId;
    SolverState solverState;
    int[][] board;

    public SolverInfoDTO(Long solverId, SolverState solverState, int[][] board) {
        this.solverId = solverId;
        this.solverState = solverState;
        this.board = board;
    }

    public Long getSolverId() {
        return this.solverId;
    }

    public void setSolverId(Long solverId) {
        this.solverId = solverId;
    }

    public SolverState getSolverState() {
        return this.solverState;
    }

    public void setSolverState(SolverState solverState) {
        this.solverState = solverState;
    }

    public int[][] getBoard() {
        return this.board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

}
