package util.activeMq;

import java.io.IOException;

/**
 * Created By Khojasteh on 7/27/2019
 */
public interface IoAdapter {
    void onReceiveMessage(String message) throws IOException;

    void onSendError();

    void onReceiveError();

    void onSessionCloseError();
}
