package web;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.plugin.openapi.InitialConfigurationCreator;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.task.TaskController;

import java.util.function.Consumer;

/**
 * @author ruan
 * @version 1.0
 * @date 2021/5/15 17:51
 */
public class MagicSquareLauncher {
    private static final Logger logger = LoggerFactory.getLogger(MagicSquareLauncher.class);
    public final static String HOST = "localhost";
    private final static int PORT = 8000;

    public static void main(String[] args) {
        MagicSquareLauncher launcher = new MagicSquareLauncher();
        launcher.start();
    }

    private void start() {

        Javalin app = Javalin.create(initConfig()).start(PORT);

        TaskController controller = new TaskController();

        app.get("/", controller.index);
//        app.get("/", ctx -> ctx.result("Hello world!"));

        app.post("/:taskType/create", controller.create);

        app.post("/start", controller.start);

        app.post("/suspend", controller.suspend);

        app.post("/resume", controller.resume);

        app.post("/stop", controller.stop);

        app.get("/state/:taskId", controller.state);

        app.ws("/syncBoard/:taskId", controller.acceptWs);

    }

    private OpenApiOptions getOpenApiOptions() {
        InitialConfigurationCreator initialConfigurationCreator = () -> new OpenAPI()
                .info(new Info().version("1.0").description("My Application"))
                .addServersItem(new Server().url("http://"+HOST+":"+PORT).description("Demo"));
        return new OpenApiOptions((initialConfigurationCreator)).path("/swagger-docs")
                .swagger(new SwaggerOptions("/docs").title("APIs Documentation"));
    }

    private Consumer<JavalinConfig> initConfig() {
        return config -> {

            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));

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