package web.model;

/**
 * Used to synchronize the board status between the front and back ends.
 *
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 20:40
 */
public class TaskState {

    Long taskId;
    Boolean finished;
    int[][] board;

    public TaskState(Long taskId, Boolean finished, int[][] board) {
        this.taskId = taskId;
        this.finished = finished;
        this.board = board;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Boolean isFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

}
