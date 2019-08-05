package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.mainmodel.Invitee;
import podChat.model.*;
import podChat.requestobject.RequestAddContact;
import podChat.requestobject.RequestGetContact;
import podChat.requestobject.RequestThread;
import podChat.util.InviteType;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "cf7e7f0495794f438e1dd919af10b7c5";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";
    public static ChatController chatController;
    Gson gson = new Gson();

    void init() {
        chatController = new ChatController(this);
        try {
            chatController.connect("", "", serverName, token, ssoHost, platformHost, fileServer, "default");

        } catch (ConnectionException e) {
            System.out.println(e);
        }

    }

    void addContact() {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .lastName("مظلوم")
                .build();
        chatController.addContact(requestAddContact);

    }

    @Override
    public void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {
        logger.info("GET USER INFO :   " + gson.toJson(outPutUserInfo));

        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId(9156452709l);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");
    }


    @Override
    public void onCreateThread(ChatResponse<ResultThread> outPutThread) {
        logger.info("CREATE THREAD:   " + gson.toJson(outPutThread));

        Gson gson = new Gson();

//        ChatResponse<ResultThread> chatResponse = gson.fromJson(content, new TypeToken<ChatResponse<ResultThread>>() {
//        }.getType());
//
//        RequestMessage requestThread = new RequestMessage
//                .Builder("this is final test", chatResponse.getResult().getThread().getId())
//                .build();
//
//        chatController.sendTextMessage(requestThread, null);
    }

    @Override
    public void onGetThreadList(ChatResponse<ResultThreads> thread) {
        logger.info("THREAD LIST:  " + gson.toJson(thread));

    }


    @Override
    public void onSentMessage(ChatResponse<ResultMessage> chatResponse) {
        logger.info("SENT MESSAGE:  " + gson.toJson(chatResponse));

        RequestThread requestThread = new RequestThread.Builder().build();

        chatController.getThreads(requestThread, null);
    }

    @Override
    public void onGetSeenMessage(ChatResponse<ResultMessage> response) {
        logger.info("SEEN MESSAGE:  " + gson.toJson(response));
    }

    @Override
    public void onGetDeliverMessage(ChatResponse<ResultMessage> chatResponse) {
        logger.info("DELIVERED MESSAGE:  " + gson.toJson(chatResponse));

        addContact();
    }

    @Override
    public void onAddContact(ChatResponse<ResultAddContact> chatResponse) {
        logger.info("ADD CONTACT:  " + gson.toJson(chatResponse));

        RequestGetContact requestGetContact = new RequestGetContact
                .Builder()
                .build();
        chatController.getContact(requestGetContact, null);
    }

    @Override
    public void onGetContacts(ChatResponse<ResultContact> response) {
        logger.info("GET CONTACT:  " + gson.toJson(response));


    }

    @Override
    public void onError(ErrorOutPut error) {
        System.out.println(error);
    }


}
