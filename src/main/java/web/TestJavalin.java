package web;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class TestJavalin {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config ->
                config.addStaticFiles("/",
                        "src/files",
                        Location.EXTERNAL))
                .start(7000);
        app.wsBefore("/websocket/:path", ws -> System.out.println("before"));
        app.ws("/websocket/:path", ws -> {
            ws.onConnect(ctx -> System.out.println("Connected"));
            ws.onMessage(ctx -> {
//                User user = ctx.message(User.class); // convert from json
//                ctx.send(user); // convert to json and send back
            });
            ws.onBinaryMessage(ctx -> System.out.println("Message"));
            ws.onClose(ctx -> System.out.println("Closed"));
            ws.onError(ctx -> System.out.println("Errored"));
        });
        app.get("/test", ctx -> ctx.result("Hello World"));
        app.get("/hello/:name", ctx -> {
           ctx.result("Hello: " + ctx.pathParam("name"));
        });
    }

}
