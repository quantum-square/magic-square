package web.solver;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.javalin.websocket.WsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.model.SolverState;
import web.model.BoardDTO;
import web.model.SolverIdDTO;
import core.model.SolverType;

import java.util.function.Consumer;

/**
 * @version 2.0
 * @date 2021/5/17 9:17
 */
public class SolverController {

    private static final Logger logger = LoggerFactory.getLogger(SolverController.class);

    SolverManager solverManager = new SolverManager();

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
            path = "/:solverType/create",
            method = HttpMethod.POST,
            pathParams = @OpenApiParam(name = "solverType", type = SolverType.class),
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = BoardDTO.class),
                    description = "Two dimensional array."),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = SolverIdDTO.class),
                            description = "Solver created successfully")
            }
    )
    public final Handler create = ctx -> {
        SolverType solverType = SolverType.valueOf(ctx.pathParam("solverType"));
        BoardDTO boardDTO = ctx.bodyAsClass(BoardDTO.class);
        Long solverId = solverManager.create(boardDTO.getBoard(), solverType);
        ctx.json(new SolverIdDTO(solverId));
    };

    @OpenApi(
            path = "/start",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SolverIdDTO.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Solver started successfully."),
                    @OpenApiResponse(status = "304", description = "The core.solver does not exist or has started.")
            }
    )
    public final Handler start = ctx -> {
        SolverIdDTO solverIdDTO = ctx.bodyAsClass(SolverIdDTO.class);
        boolean result = solverManager.start(solverIdDTO.getSolverId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/suspend",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SolverIdDTO.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Solver suspended successfully."),
                    @OpenApiResponse(status = "304", description = "Solver does not exist or terminated.")
            }
    )
    public final Handler suspend = ctx -> {
        SolverIdDTO solverIdDTO = ctx.bodyAsClass(SolverIdDTO.class);
        boolean result = solverManager.suspend(solverIdDTO.getSolverId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/resume",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SolverIdDTO.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Solver resumed successfully."),
                    @OpenApiResponse(status = "304", description = "Solver does not exist or terminated.")
            }
    )
    public final Handler resume = ctx -> {
        SolverIdDTO solverIdDTO = ctx.bodyAsClass(SolverIdDTO.class);
        boolean result = solverManager.resume(solverIdDTO.getSolverId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/stop",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SolverIdDTO.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Solver stopped successfully."),
                    @OpenApiResponse(status = "304", description = "Solver does not exist or terminated.")
            }
    )
    public final Handler stop = ctx -> {
        SolverIdDTO solverIdDTO = ctx.bodyAsClass(SolverIdDTO.class);
        boolean result = solverManager.stop(solverIdDTO.getSolverId());
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/state/:solverId",
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content =  @OpenApiContent(from = SolverState.class),
                            description = "EMPTY means the core.solver does not exist")
            }
    )
    public final Handler state = ctx -> {
        Long solverId = ctx.pathParam("solverId", Long.class).getOrNull();
        ctx.json(solverManager.state(solverId));
    };

    public final Consumer<WsHandler> acceptWs = ws -> {
        ws.onConnect(ctx -> {
            Long solverId = ctx.pathParam("solverId", Long.class).getOrNull();
            boolean result = solverManager.syncBoard(solverId, ctx);
            if (!result) {
                ctx.send("Solver not exist!");
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
