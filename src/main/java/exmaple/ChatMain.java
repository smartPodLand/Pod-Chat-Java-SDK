package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.mainmodel.ResultDeleteMessage;
import podChat.model.*;
import podChat.requestobject.RequestAddContact;
import podChat.requestobject.RequestEditMessage;
import podChat.requestobject.RequestGetContact;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);
    static boolean temp = false;

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "18d4ecd7729441c596b8ed85f881efd3";
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
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hi", 47160)
                .build();
        chatController.editMessage(requestEditMessage, null);

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


       /* RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .forwardedMessageIds(new ArrayList<Long>() {{
                    add(47181l);
                    add(47160l);
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
        chatController.createThreadWithMessage(requestCreateThread);

        RequestThreadInnerMessage requestThreadInnerMessage1 = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .forwardedMessageIds(new ArrayList<Long>() {{
                    add(46862l);
                    add(46965l);
                }})
                .build();

        Invitee invitee1 = new Invitee();
        invitee1.setId(4781);
        invitee1.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread1 = new RequestCreateThread
                .Builder(0, new ArrayList<Invitee>() {{
            add(invitee1);
        }})
                .message(requestThreadInnerMessage1)
                .build();
        chatController.createThreadWithMessage(requestCreateThread1);


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RequestThreadInnerMessage requestThreadInnerMessage11 = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa final")
                .build();

        Invitee invitee11 = new Invitee();
        invitee11.setId(4781);
        invitee11.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread11 = new RequestCreateThread
                .Builder(0, new ArrayList<Invitee>() {{
            add(invitee11);
        }})
                .message(requestThreadInnerMessage11)
                .build();
        chatController.createThreadWithMessage(requestCreateThread11);*/

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
    public void onEditMessage() {

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
