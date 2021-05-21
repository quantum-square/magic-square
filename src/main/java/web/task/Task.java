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

    protected TaskState taskState = TaskState.EMPTY;

    protected ObjectMapper mapper = new ObjectMapper();

    protected WsContext wsContext;

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public void setWsContext(WsContext wsContext) {
        this.wsContext = wsContext;
    }

    /**
     * Get board status.
     * @return TaskState
     */
    public abstract TaskInfo getTaskInfo();

    public void sendBoardState(){
        if(wsContext !=null){
            try {
                wsContext.send(mapper.writeValueAsString(getTaskInfo()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
