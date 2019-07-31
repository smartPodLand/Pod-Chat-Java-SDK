package exmaple;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ConnectionException;
import podChat.mainmodel.Invitee;
import podChat.model.ChatResponse;
import podChat.model.ResultAddContact;
import podChat.model.ResultThread;
import podChat.model.ResultThreads;
import podChat.requestobject.RequestAddContact;
import podChat.requestobject.RequestGetContact;
import podChat.requestobject.RequestMessage;
import podChat.requestobject.RequestThread;
import podChat.util.InviteType;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "b96d39e58881494c917d09d3e375a08b";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";
    public static ChatController chatController;


    void init() {
        chatController = new ChatController(this);
        try {
            chatController.connect("", "", serverName, token, ssoHost, platformHost, fileServer, "default");

        } catch (ConnectionException e) {
            System.out.println(e);
        }

    }

    @Override
    public void onGetUserInfo() {
//        Invitee[] invitees = new Invitee[1];
//        Invitee invitee = new Invitee();
//        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
//        invitee.setId(9156452709L);
//
//        invitees[0] = invitee;
//
//        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .lastName("مظلوم")
                .build();
        chatController.addContact(requestAddContact);

//        RequestGetContact requestGetContact = new RequestGetContact
//                .Builder()
//                .build();
//        chatController.getContact(requestGetContact, null);
    }


    @Override
    public void onCreateThread(String content, ChatResponse<ResultThread> outPutThread) {
        Gson gson = new Gson();

        ChatResponse<ResultThread> chatResponse = gson.fromJson(content, new TypeToken<ChatResponse<ResultThread>>() {
        }.getType());

        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", chatResponse.getResult().getThread().getId())
                .build();

        chatController.sendTextMessage(requestThread, null);
    }

    @Override
    public void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {
        System.out.println(content);

    }


    @Override
    public void onSentMessage() {
        RequestThread requestThread = new RequestThread.Builder().build();

        chatController.getThreads(requestThread, null);
    }

    @Override
    public void onAddContact(String content, ChatResponse<ResultAddContact> chatResponse) {
        RequestGetContact requestGetContact = new RequestGetContact
                .Builder()
                .build();
        chatController.getContact(requestGetContact, null);
    }
}
