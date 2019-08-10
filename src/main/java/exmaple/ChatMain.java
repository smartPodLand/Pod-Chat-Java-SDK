package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;
import podChat.requestobject.RequestAddContact;
import podChat.requestobject.RequestCreateThread;
import podChat.requestobject.RequestGetContact;
import podChat.requestobject.RequestMessage;
import podChat.util.InviteType;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);
    static boolean temp = false;

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "5c0b12f7c00e4e32b4dfbccfe8687f3f";
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
        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread, null);


        /*RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .forwardedMessageIds(new ArrayList<Long>() {{
                    add(47181L);
                    add(47160L);
                }})
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(4781);
        invitee.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(0, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);*/

//        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
//                .Builder(new ArrayList<Long>() {{
//            add(46981L);
//        }})
//                .build();
//
//        chatController.deleteMessage(deleteMessage, null);

//        RequestMessage requestThread = new RequestMessage
//                .Builder("this is final test", 5461)
//                .build();
//
//        chatController.sendTextMessage(requestThread, null);

        //   logger.info("GET USER INFO :   " + gson.toJson(outPutUserInfo));
//
//        Invitee[] invitees = new Invitee[1];
//        Invitee invitee = new Invitee();
//        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
//        invitee.setId(9156452709l);
//
//        invitees[0] = invitee;
//        chatController.createThread(0, invitees, "sendMessage", "", "", "");
    }

    @Override
    public void onDeleteMessage(ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        System.out.println("in delete");
    }

    @Override
    public void onCreateThread(ChatResponse<ResultThread> outPutThread) {
        //logger.info("CREATE THREAD:   " + gson.toJson(outPutThread));

        Gson gson = new Gson();

//        ChatResponse<ResultThread> chatResponse = gson.fromJson(content, new TypeToken<ChatResponse<ResultThread>>() {
//        }.getType());
//

    }

    @Override
    public void onGetThreadList(ChatResponse<ResultThreads> thread) {
        logger.info("THREAD LIST:  " + gson.toJson(thread));

    }

    @Override
    public void onNewMessage(ChatResponse<ResultNewMessage> chatResponse) {
        ResultNewMessage resultNewMessage = chatResponse.getResult();
//        if (!temp) {
//            long threadId = resultNewMessage.getThreadId();
//
//            MessageVO messageVO = resultNewMessage.getMessageVO();
//
//            long messageId = messageVO.getId();
//
//            RequestReplyMessage requestReplyMessage = new RequestReplyMessage
//                    .Builder("HELLOOOO", threadId, messageId)
//                    .build();
//
//            chatController.replyMessage(requestReplyMessage, null);
//            temp = true;
//        }
    }


    @Override
    public void onSentMessage(ChatResponse<ResultMessage> chatResponse) {
        //logger.info("SENT MESSAGE:  " + gson.toJson(chatResponse));

//        RequestThread requestThread = new RequestThread.Builder().build();
//
//        chatController.getThreads(requestThread, null);
    }

    @Override
    public void onGetSeenMessage(ChatResponse<ResultMessage> response) {
        //     logger.info("SEEN MESSAGE:  " + gson.toJson(response));
    }

    @Override
    public void onGetDeliverMessage(ChatResponse<ResultMessage> chatResponse) {
        //logger.info("DELIVERED MESSAGE:  " + gson.toJson(chatResponse));

        //addContact();
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
