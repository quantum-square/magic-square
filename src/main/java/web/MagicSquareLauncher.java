package web;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import web.model.BoardParam;
import web.model.TaskParam;
import web.task.TaskManager;
import web.model.TaskType;

import java.util.function.Consumer;

/**
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 17:51
 */
public class MagicSquareLauncher {
    private static final Logger logger = LoggerFactory.getLogger(MagicSquareLauncher.class);
    private final static int PORT = 8000;
    private final static String TASK_ID_JSON_FORMAT = "{\"taskId\":%d}";


    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Javalin app = Javalin.create(initConfig()).start(PORT);


        app.get("/", ctx -> ctx.render("index.html"));
//        app.get("/", ctx -> ctx.result("Hello world!"));

        app.post("/:taskType/create", ctx -> {
            TaskType taskType = TaskType.valueOf(ctx.pathParam("taskType"));
            BoardParam boardParam = ctx.bodyAsClass(BoardParam.class);
            long taskId = taskManager.create(boardParam.getBoard(), taskType);
            ctx.result(String.format(TASK_ID_JSON_FORMAT, taskId));
        });

        app.post("/start", ctx -> {
            TaskParam taskParam = ctx.bodyAsClass(TaskParam.class);
            boolean result = taskManager.start(taskParam.getTaskId());
            if (!result) {
                ctx.status(304);
            }
        });

        app.post("/suspend", ctx -> {
            TaskParam taskParam = ctx.bodyAsClass(TaskParam.class);
            boolean result = taskManager.suspend(taskParam.getTaskId());
            if (!result) {
                ctx.status(304);
            }
        });

        app.post("/resume", ctx -> {
            TaskParam taskParam = ctx.bodyAsClass(TaskParam.class);
            boolean result = taskManager.resume(taskParam.getTaskId());
            if (!result) {
                ctx.status(304);
            }
        });

        app.post("/stop", ctx -> {
            TaskParam taskParam = ctx.bodyAsClass(TaskParam.class);
            boolean result = taskManager.stop(taskParam.getTaskId());
            if (!result) {
                ctx.status(304);
            }
        });

        app.ws("/syncBoard/:taskId", ws -> {
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

        });
    }


    private static Consumer<JavalinConfig> initConfig() {
        return config -> {
            config.requestLogger((ctx, ms) -> {
                logger.debug("METHOD: [{}], URL: [{}], HOST: {}, STATUS: [{}] USAGE: [{}] ms",
                        ctx.req.getMethod(), ctx.url(), ctx.req.getRemoteHost(), ctx.status(), ms);
            });

            config.wsLogger(ws -> {
                ws.onConnect(ctx -> {
                    logger.debug("METHOD: [WebSocket], SYNC BOARD [taskId={}], HOST: [{}], STATE: CONNECTED",
                            ctx.pathParam("taskId"), ctx.header("Host"));
                });
                ws.onMessage(ctx -> {
                    logger.debug("METHOD: [WebSocket], RECEIVED MSG: [STRING], HOST: [{}], DATA: {}",
                            ctx.header("Host"), ctx.message());
                });

                ws.onBinaryMessage(ctx -> {
                    logger.debug("METHOD: [WebSocket], RECEIVED MSG: [BINARY], HOST: [{}], DATA: {}",
                            ctx.header("Host"), ctx.data());
                });

                ws.onClose(ctx -> {
                    logger.debug("METHOD: [WebSocket], SYNC BOARD [taskId={}], HOST: [{}], STATE: CLOSED",
                            ctx.pathParam("taskId"), ctx.header("Host"));
                });

                ws.onError(ctx -> {
                    Throwable error = ctx.error();
                    if (error != null) {
                        logger.debug("METHOD: [WebSocket], ERROR OCCURRED, HOST: [{}], ERROR: {}",
                                ctx.header("Host"), error.getMessage());
                    }
                });

            });
        };
    }

}