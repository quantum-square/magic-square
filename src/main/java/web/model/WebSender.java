package web.model;

/**
 * @version 1.0
 * @date 2021/5/24 0:14
 */
public interface WebSender {

    /**
     * Set sender
     * @param sender web sender
     */
    void setSender(Object sender);

    /**
     * Send data by sender
     */
    void sendData();

}
