package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podChat.chat.Chat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created By Khojasteh on 7/24/2019
 */
public class QueueConfig {
    private static Logger logger = LogManager.getLogger(QueueConfig.class);

    public static String queueInput;
    public static String queueOutput;
    public static String queueServer;
    public static String queuePort;
    public static String queueUserName;
    public static String queuePassword;
    public static String sendMessageTimeOut;
    public static int queueReconnectTime;

    public void setConfig() {

        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties prop = new Properties();
            prop.load(input);

            queueInput = prop.getProperty("queue.input.sandbox");
            queueOutput = prop.getProperty("queue.output.sandbox");
            queuePassword = prop.getProperty("queue.password.sandbox");
            queuePort = prop.getProperty("queue.port.sandbox");
            sendMessageTimeOut = prop.getProperty("queue.reconnect.timer.sandbox");
            queueReconnectTime = Integer.parseInt(prop.getProperty("queue.reconnect.timer.sandbox"));
            queueServer = prop.getProperty("queue.server.sandbox");
            queueUserName = prop.getProperty("queue.username.sandbox");


        } catch (IOException ex) {
            if (Chat.isLoggable) logger.error(ex);
        }
    }
}
