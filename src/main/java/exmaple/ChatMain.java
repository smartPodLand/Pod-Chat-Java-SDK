package exmaple;

import podChat.chat.Chat;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain {
    static String platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/";
    static String token = "c0b06ed0036b489caebc086997ff2a24";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";


    public static void main(String[] args) {
        init();
    }

    private static void init() {
        Chat.init(false, true)
                .connect("", "", serverName, token, ssoHost, platformHost, fileServer,"");
    }
}
