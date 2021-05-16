package web.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import web.model.TaskState;

/**
 * Task base class.
 *
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 17:24
 */
public abstract class Task extends Thread {

    protected boolean finished = false;

    protected ObjectMapper mapper = new ObjectMapper();

    protected WsContext wsContext;

    public void setWsContext(WsContext wsContext) {
        this.wsContext = wsContext;
    }

    /**
     * Get board status.
     * @return TaskState
     */
    public abstract TaskState getBoardState();

    public void sendBoardState(){
        if(wsContext !=null){
            try {
                wsContext.send(mapper.writeValueAsString(getBoardState()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
