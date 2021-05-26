package web.exception;

/**
 * @version 1.0
 * @date 2021/5/26 9:10
 */
public class ClientSideException extends RuntimeException {

    public ClientSideException() {
    }

    public ClientSideException(String message) {
        super(message);
    }

    public ClientSideException(String message, Throwable cause) {
        super(message, cause);
    }

}
