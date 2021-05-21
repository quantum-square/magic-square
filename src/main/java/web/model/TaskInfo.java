package web.model;

/**
 * Used to synchronize the board status between the front and back ends.
 *
 * @version 1.0
 * @date 2021/5/15 20:40
 */
public class TaskInfo {

    Long taskId;
    TaskState taskState;
    int[][] board;

    public TaskInfo(Long taskId, TaskState taskState, int[][] board) {
        this.taskId = taskId;
        this.taskState = taskState;
        this.board = board;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

}
