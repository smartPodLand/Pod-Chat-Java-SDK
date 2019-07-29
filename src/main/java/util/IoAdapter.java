package util;

import java.io.IOException;

/**
 * Created By Khojasteh on 7/27/2019
 */
public interface IoAdapter {
    public void onReceiveMessage(String message) throws IOException;
    public void onSendError();
    public void onReceiveError();
    public void onSessionCloseError();
}
