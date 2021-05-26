package web.solver;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.javalin.websocket.WsHandler;
import kotlin.jvm.functions.Function1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.model.SolverState;
import web.exception.ClientSideException;
import web.exception.ServerSideException;
import web.model.BoardDTO;
import web.model.SendFreqDTO;
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
                            description = "Solver created successfully"),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = String.class),
                            description = "Bad Request"),
                    @OpenApiResponse(status = "500", content = @OpenApiContent(from = String.class),
                            description = "Internal Server Error")
            }
    )
    public final Handler create = ctx -> {
        SolverType solverType = SolverType.valueOf(ctx.pathParam("solverType"));
        try {
            BoardDTO boardDTO = ctx.bodyValidator(BoardDTO.class).check("board", boardDTOCheck -> {
                if (boardDTOCheck == null) {
                    return Boolean.FALSE;
                } else {
                    int[][] board = boardDTOCheck.getBoard();
                    if (board == null || board.length == 0 || board[0].length != board.length) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }).get();
            Long solverId = solverManager.create(boardDTO.getBoard(), solverType);
            ctx.json(new SolverIdDTO(solverId));
        } catch (BadRequestResponse ex) {
            throw new ClientSideException("Invalid board parameter! " + ex.getMessage());
        } catch (Exception ex) {
            throw new ServerSideException("Internal Server Error");
        }
    };

    @OpenApi(
            path = "/:solverType/chgSendFreq",
            method = HttpMethod.POST,
            pathParams = @OpenApiParam(name = "solverType", type = SolverType.class),
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SendFreqDTO.class),
                    description = "Websocket information sending frequency, once every [sendFreq]."),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = SolverIdDTO.class),
                            description = "Solver send freq set successfully"),
                    @OpenApiResponse(status = "304", description = "The solver does not exist or illegal [sendFreq].")
            }
    )
    public final Handler chgSendFreq = ctx -> {
        SolverType solverType = SolverType.valueOf(ctx.pathParam("solverType"));
        SendFreqDTO sendFreq = ctx.bodyAsClass(SendFreqDTO.class);
        boolean result = solverManager.chgSendFreq(sendFreq.getSendFreq(), solverType);
        if (!result) {
            ctx.status(304);
        }
    };

    @OpenApi(
            path = "/start",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = SolverIdDTO.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Solver started successfully."),
                    @OpenApiResponse(status = "304", description = "The solver does not exist or has started.")
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
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = SolverState.class),
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
