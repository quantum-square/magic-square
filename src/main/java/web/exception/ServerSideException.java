package web.exception;

/**
 * @version 1.0
 * @date 2021/5/26 9:11
 */
public class ServerSideException extends RuntimeException {

    public ServerSideException() {
    }

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }

}
