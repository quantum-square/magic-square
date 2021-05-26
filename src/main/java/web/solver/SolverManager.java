package web.solver;

import io.javalin.websocket.WsContext;
import core.solver.MagicSquareSolver;
import core.solver.MatrixSolver;
import core.model.SolverState;
import core.solver.SudokuSolver;
import core.model.SolverType;
import web.model.WebSender;


import java.util.HashMap;
import java.util.Map;

/**
 * @version 2.0
 * @date 2021/5/15 17:24
 */
public class SolverManager {

    private final Map<Long, MatrixSolver> solverGroup = new HashMap<>();

    public Long create(int[][] board, SolverType type) {
        MatrixSolver solver;
        if (type == SolverType.sdk) {
            solver = new SudokuSolver(board);
        } else if (type == SolverType.ms) {
            solver = new MagicSquareSolver(board);
        } else {
            return null;
        }
        solver.setSolverState(SolverState.NEW);
        solverGroup.put(solver.getSolverId(), solver);

        return solver.getId();
    }

    public boolean chgSendFreq(int freq, SolverType type) {
        if (freq <= 0) {
            return false;
        }
        switch (type) {
            case sdk:
                SudokuSolver.setSendFreq(freq);
                break;
            case ms:
                MagicSquareSolver.setSendFreq(freq);
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean start(long id) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null && solver.getSolverState() == SolverState.NEW) {
            solver.start();
            solver.setSolverState(SolverState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean suspend(long id) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null && solver.getSolverState() != SolverState.TERMINATED) {
            solver.suspend();
            solver.setSolverState(SolverState.SUSPEND);
            solver.setPauseTimestamp(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public boolean resume(long id) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null && solver.getSolverState() != SolverState.TERMINATED) {
            if (solver.getSolverState() != SolverState.NEW) {
                solver.addPauseTimes(System.currentTimeMillis() - solver.getPauseTimestamp());
            }
            solver.resume();
            solver.setSolverState(SolverState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean stop(long id) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null && solver.getSolverState() != SolverState.TERMINATED) {
//            task.interrupt();
            solver.stop();
            solver.setSolverState(SolverState.TERMINATED);
            return true;
        }
        return false;
    }

    public SolverState state(Long id) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null) {
            return solver.getSolverState();
        }
        return SolverState.EMPTY;
    }

    public boolean syncBoard(Long id, WsContext wsContext) {
        MatrixSolver solver = solverGroup.get(id);
        if (solver != null) {
            if (solver instanceof WebSender) {
                WebSender webSender = (WebSender) solver;
                webSender.setSender(wsContext);
                if (solver.getSolverState() != SolverState.RUNNING) {
                    webSender.sendData();
                }
            }
            return true;
        }
        return false;
    }

}
