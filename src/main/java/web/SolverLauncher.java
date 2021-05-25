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
import web.solver.SolverController;

import java.util.function.Consumer;

/**
 * @version 2.0
 * @date 2021/5/23 23:00
 */
public class SolverLauncher {
    private static final Logger logger = LoggerFactory.getLogger(SolverLauncher.class);
    private final static String HOST = "localhost";
    private final static int PORT = 8000;

    public static void main(String[] args) {
        SolverLauncher launcher = new SolverLauncher();
        launcher.start();
    }

    private void start() {

        Javalin app = Javalin.create(initConfig()).start(PORT);

        SolverController controller = new SolverController();

        app.get("/", controller.index);
//        app.get("/", ctx -> ctx.result("Hello world!"));

        app.post("/:solverType/create", controller.create);

        app.post("/:solverType/chgSendFreq", controller.chgSendFreq);

        app.post("/start", controller.start);

        app.post("/suspend", controller.suspend);

        app.post("/resume", controller.resume);

        app.post("/stop", controller.stop);

        app.get("/state/:solverId", controller.state);

        app.ws("/syncBoard/:solverId", controller.acceptWs);

    }

    private OpenApiOptions getOpenApiOptions() {
        InitialConfigurationCreator initialConfigurationCreator = () -> new OpenAPI()
                .info(new Info().version("1.0").description("My Application"))
                .addServersItem(new Server().url("http://" + HOST + ":" + PORT).description("Demo"));
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
                    logger.debug("METHOD: [WebSocket], SYNC BOARD [solverId={}], HOST: [{}], STATE: CONNECTED",
                            ctx.pathParam("solverId"), ctx.header("Host"));
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
                    logger.debug("METHOD: [WebSocket], SYNC BOARD [solverId={}], HOST: [{}], STATE: CLOSED",
                            ctx.pathParam("solverId"), ctx.header("Host"));
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
