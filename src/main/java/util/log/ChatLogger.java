package util.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatLogger {

    private static Logger logger = LogManager.getLogger(ChatLogger.class);
    public static Boolean isLoggable = false;

    public ChatLogger(Boolean isLoggable) {
        this.isLoggable = isLoggable;
    }

    public void setIsLogNeeded(Boolean isLogNeeded) {
        this.isLoggable = isLogNeeded;
    }

    public static void info(String msg) {
        if (isLoggable) logger.info(msg);
    }

    public static void info(String msg, String msg2) {
        if (isLoggable) logger.info(msg,msg2);
    }


    public static void error(String msg) {
        if (isLoggable) logger.error(msg);
    }

    public static void error(String msg, String msg2) {
        if (isLoggable) logger.error(msg, msg2);
    }

    public static void error(Object object) {
        if (isLoggable) logger.error(object);
    }

    public static void warn(String msg) {
        if (isLoggable) logger.warn(msg);
    }

}
