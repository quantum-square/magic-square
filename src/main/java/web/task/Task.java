package web.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import web.model.TaskInfo;
import web.model.TaskState;

/**
 * Task base class.
 *
 * @version 1.0
 * @date 2021/5/15 17:24
 */
public abstract class Task extends Thread {

    /**
     * dimension
     */
    int n;
    int[][] board;
    int[][] curBoard;
    boolean[][] fixed;

    TaskState taskState = TaskState.EMPTY;

    private ObjectMapper mapper = new ObjectMapper();

    private WsContext wsContext;

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public int[][] getCurBoard() {
        return curBoard;
    }

    public void setWsContext(WsContext wsContext) {
        this.wsContext = wsContext;
    }

    public void sendBoardState() {
        if (wsContext != null) {
            try {
                wsContext.send(mapper.writeValueAsString(new TaskInfo(getId(), taskState, curBoard)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
