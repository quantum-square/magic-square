package core.model;

/**
 * @version 2.0
 * @date 2021/5/23 23:36
 */
public enum SolverType {
    /**
     * Sudoku Solver and MagicSquare Solver.
     */
    sdk("Sudoku"),
    ms("MagicSquare");

    String name;

    SolverType(String name) {
        this.name = name;
    }
}
