package config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podChat.chat.Chat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Created By Khojasteh on 7/27/2019
 */
public class MainConfig {
    private static Logger logger = LogManager.getLogger(MainConfig.class);
    public static String sentryUrl;

    public void setConfig() {
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties prop = new Properties();
            prop.load(input);

            sentryUrl = prop.getProperty("sentry.config");


        } catch (IOException ex) {
            if (Chat.isLoggable) logger.error(ex);
        }
    }
}
