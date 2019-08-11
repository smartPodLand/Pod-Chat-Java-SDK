package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;
import podChat.requestobject.RequestAddContact;
import podChat.requestobject.RequestDeleteMessage;
import podChat.requestobject.RequestForwardMessage;
import podChat.requestobject.RequestGetContact;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);
    static boolean temp = false;

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "389305d75ebb4277a877f1dab67a793d";
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
      /*  RequestThreadParticipant threadParticipant =  new RequestThreadParticipant
                .Builder(3042)
                .build();

        chatController.getThreadParticipant(threadParticipant,null);*/

      /*  RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(5461, new ArrayList<Long>() {{
            add(47403l);
            add(47402l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);*/

     /*  RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
                    add(47403l);
                    add(47402l);
                }})
                .threadId(3042)
                .build();

        chatController.deleteMessage(requestDeleteMessage, null);*/

        /*RequestThread requestThread = new RequestThread
                .Builder()
                .partnerCoreContactId(13882)
                .build();

        chatController.getThreads(requestThread, null);*/


       /* RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread, null);*/


        /*RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .forwardedMessageIds(new ArrayList<Long>() {{
                    add(47241l);
                    add(47242l);
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

       /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


     /*   try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


//        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
//                .Builder(new ArrayList<Long>() {{
//            add(46981L);
//        }})
//                .build();
//
//        chatController.deleteMessage(deleteMessage, null);

     /*   RequestMessage requestThread = new RequestMessage
                .Builder("this is final test1", 3042)
                .build();

        chatController.sendTextMessage(requestThread, null);

        RequestMessage requestThread2 = new RequestMessage
                .Builder("this is final test2", 3042)
                .build();

        chatController.sendTextMessage(requestThread2, null);*/

        //   logger.info("GET USER INFO :   " + gson.toJson(outPutUserInfo));
//
     /*   Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId(9156967335L);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");*/
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
    public void onGetThreadParticipant() {

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
        // logger.info("SEEN MESSAGE:  " + gson.toJson(response));
    }

    @Override
    public void onGetDeliverMessage(ChatResponse<ResultMessage> chatResponse) {
        //  logger.info("DELIVERED MESSAGE:  " + gson.toJson(chatResponse));

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
