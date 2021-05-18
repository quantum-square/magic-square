package web.model;

/**
 * Task parameters, front-end and back-end data transmission objects.
 *
 * @author ruan
 * @version 1.0
 * @date 2021/5/16 9:25
 */
public class TaskId {

    Long taskId;

    public TaskId() {

    }

    public TaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
