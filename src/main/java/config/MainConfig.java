package config;

import util.log.ChatLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class MainConfig {
    public static String sentryUrl;
    public static String contactApiUrl;

    public void setConfig() {
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties prop = new Properties();
            prop.load(input);

            sentryUrl = prop.getProperty("sentry.config");
            contactApiUrl = prop.getProperty("contact.api.url");


        } catch (IOException ex) {
            ChatLogger.error(ex);
        }
    }
}
