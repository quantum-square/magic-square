package web.model;

/**
 * Task Type.
 *
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 22:05
 */
public enum TaskType {
    /**
     * Sudoku task and MagicSquare task.
     */
    sdk("Sudoku"),
    ms("MagicSquare");

    String name;

    TaskType(String name) {
        this.name = name;
    }
}
