package exmaple;

import com.google.gson.Gson;
import podAsync.model.ClientMessage;
import podChat.mainmodel.Invitee;
import podChat.model.ChatResponse;
import podChat.model.ResultThread;
import podChat.model.ResultThreads;
import podChat.requestobject.RequestMessage;
import podChat.requestobject.RequestThread;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    static String platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/";
    static String token = "a39ea4dd877d40f890aacdbac19ccf85";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";
    private ChatController chatController;


    void init() {
        chatController = new ChatController(this);
        chatController.connect("", "", serverName, token, ssoHost, platformHost, fileServer, "default");

    }

    @Override
    public void onGetUserInfo() {
        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(5);
        invitee.setId(521);

        invitees[0] = invitee;

        chatController.createThread(0, invitees, "sendMessage", "", "", "");
    }


    @Override
    public void onCreateThread(String content, ChatResponse<ResultThread> outPutThread) {
        Gson gson = new Gson();
        System.out.println(content);
        ClientMessage clientMessage = gson.fromJson(content, ClientMessage.class);


        RequestMessage requestThread = new RequestMessage
                .Builder("Hello", 2942)
                .build();

        chatController.sendTextMessage(requestThread, null);
    }

    @Override
    public void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {
    }


    @Override
    public void onSentMessage() {
        RequestThread requestThread = new RequestThread.Builder().build();

        chatController.getThreads(requestThread, null);
    }
}
