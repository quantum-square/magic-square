package web.task;

import io.javalin.websocket.WsContext;
import web.model.TaskState;
import web.model.TaskType;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @date 2021/5/15 17:24
 */
public class TaskManager {

    private final Map<Long, Task> taskGroup = new HashMap<>();

    public long create(int[][] board, TaskType type) {
        Task task;
        if (type == TaskType.sdk) {
            task = new SudokuTask(board);
        } else if (type == TaskType.ms) {
            task = new MagicSquareTask(board);
        } else {
            return 0;
        }
        task.setTaskState(TaskState.NEW);
        taskGroup.put(task.getId(), task);

        return task.getId();
    }

    public boolean start(long id) {
        Task task = taskGroup.get(id);
        if (task != null && task.getState() == Thread.State.NEW) {
            task.start();
            task.setTaskState(TaskState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean suspend(long id) {
        Task task = taskGroup.get(id);
        if (task != null && task.getState() != Thread.State.TERMINATED) {
            task.suspend();
            task.setTaskState(TaskState.SUSPEND);
            return true;
        }
        return false;
    }

    public boolean resume(long id) {
        Task task = taskGroup.get(id);
        if (task != null && task.getState() != Thread.State.TERMINATED) {
            task.resume();
            task.setTaskState(TaskState.RUNNING);
            return true;
        }
        return false;
    }

    public boolean stop(long id) {
        Task task = taskGroup.get(id);
        if (task != null && task.getState() != Thread.State.TERMINATED) {
//            task.interrupt();
            task.stop();
            task.setTaskState(TaskState.TERMINAL);
            return true;
        }
        return false;
    }

    public TaskState state(Long id) {
        Task task = taskGroup.get(id);
        if (task != null) {
            return task.getTaskState();
        }
        return TaskState.EMPTY;
    }

    public boolean syncBoard(Long id, WsContext wsContext) {
        Task task = taskGroup.get(id);
        if (task != null) {
            task.setWsContext(wsContext);
            if (task.getTaskState() != TaskState.RUNNING) {
                task.sendBoardState();
            }
            return true;
        }
        return false;
    }

}
