package web.task;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.javalin.websocket.WsHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import web.model.Board;
import web.model.TaskId;
import web.model.TaskState;
import web.model.TaskType;

import java.util.function.Consumer;

/**
 * @version 1.0
 * @date 2021/5/17 9:17
 */
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    TaskManager taskManager = new TaskManager();

    @OpenApi(
            path = "/", // parameter needed to resolve ambiguity
            method = HttpMethod.GET,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent()),
            responses = {@OpenApiResponse(status = "200")
            }
    )
    public final Handler index = ctx -> {
        ctx.render("index.html");
    };

    @OpenApi(
            path = "/:taskType/create",
            method = HttpMethod.POST,
            pathParams = @OpenApiParam(name = "taskType", type = TaskType.class),
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Board.class),
                    description = "Two dimensional array."),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = TaskId.class),
                            description = "Task created successfully")
            }
    )
    public final Handler create = ctx -> {
        TaskType taskType = TaskType.valueOf(ctx.pathParam("taskType"));
        Board board = ctx.bodyAsClass(Board.class);
        long taskId = taskManager.create(board.getBoard(), taskType);
        ctx.json(new TaskId(taskId));
    };

    @OpenApi(
            path = "/start",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = TaskId.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Task started successfully."),
                    @OpenApiResponse(status = "304", description = "The task does not exist or has started.")
            }
    )
    public final Handler start = ctx -> {
        TaskId taskId = ctx.bodyAsClass(TaskId.class);
        boolean result = taskManager.start(taskId.getTaskId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/suspend",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = TaskId.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Task suspended successfully."),
                    @OpenApiResponse(status = "304", description = "Task does not exist or terminated.")
            }
    )
    public final Handler suspend = ctx -> {
        TaskId taskId = ctx.bodyAsClass(TaskId.class);
        boolean result = taskManager.suspend(taskId.getTaskId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/resume",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = TaskId.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Task resumed successfully."),
                    @OpenApiResponse(status = "304", description = "Task does not exist or terminated.")
            }
    )
    public final Handler resume = ctx -> {
        TaskId taskId = ctx.bodyAsClass(TaskId.class);
        boolean result = taskManager.resume(taskId.getTaskId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/stop",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = TaskId.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Task stopped successfully."),
                    @OpenApiResponse(status = "304", description = "Task does not exist or terminated.")
            }
    )
    public final Handler stop = ctx -> {
        TaskId taskId = ctx.bodyAsClass(TaskId.class);
        boolean result = taskManager.stop(taskId.getTaskId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/state/:taskId",
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content =  @OpenApiContent(from = TaskState.class),
                            description = "EMPTY means the task does not exist")
            }
    )
    public final Handler state = ctx -> {
        Long taskId = ctx.pathParam("taskId", Long.class).getOrNull();
        ctx.json(taskManager.state(taskId));
    };

    public final Consumer<WsHandler> acceptWs = ws -> {
        ws.onConnect(ctx -> {
            Long taskId = ctx.pathParam("taskId", Long.class).getOrNull();
            boolean result = taskManager.syncBoard(taskId, ctx);
            if (!result) {
                ctx.send("Task not exist!");
            }
        });

        ws.onMessage(ctx -> {

        });

        ws.onBinaryMessage(ctx -> {

        });

        ws.onClose(ctx -> {

        });

        ws.onError(ctx -> {

        });

    };
}
