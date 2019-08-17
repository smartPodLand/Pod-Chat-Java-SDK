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
import podChat.requestobject.*;
import podChat.util.InviteType;
import podChat.util.ThreadType;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);
    static boolean temp = false;

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "ac8ee3b9df8b4a498927fcbc3ef6d591";
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

        // getcontact();

        // createThread();

        //addParticipant();

        // getParticipant();

        //  forwardMessage();

        //deleteMultipleMessage();

        //getThreads();

        sendMessage();
        sendMessage();

        //editMessage();


        //createThreadWithMessage();

        //deleteMessage();


    }

    private void deleteMessage() {
        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(46981L);
        }})
                .build();

        chatController.deleteMessage(deleteMessage, null);
    }

    private void createThreadWithMessage() {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
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
        chatController.createThreadWithMessage(requestCreateThread11);
    }

    private void editMessage() {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hi", 47542)
                .build();
        chatController.editMessage(requestEditMessage, null);
    }

    private void sendMessage() {
        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread, null);
    }

    private void getThreads() {
        RequestThread requestThread = new RequestThread
                .Builder()
                .partnerCoreContactId(13882)
                .build();

        chatController.getThreads(requestThread, null);
    }

    private void deleteMultipleMessage() {
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(47561l);
            add(47562l);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMessage(requestDeleteMessage, null);
    }

    private void forwardMessage() {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(5461, new ArrayList<Long>() {{
            add(47403l);
            add(47402l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);
    }

    private void getParticipant() {
        RequestThreadParticipant threadParticipant = new RequestThreadParticipant
                .Builder(5781)
                .build();

        chatController.getThreadParticipant(threadParticipant, null);
    }

    private void addParticipant() {
        RequestAddParticipants addParticipants = new RequestAddParticipants
                .Builder(5781, new ArrayList<Long>() {{
            add(15141l);
        }})
                .build();

        chatController.addParticipants(addParticipants, null);
    }

    private void createThread() {
        Invitee[] invitees = new Invitee[2];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee.setId(13812);

        Invitee invitee1 = new Invitee();
        invitee1.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee1.setId(13882);

        invitees[0] = invitee;
        invitees[1] = invitee1;

        chatController.createThread(ThreadType.PUBLIC_GROUP, invitees, "sendMessage", "", "", "");
    }

    private void getcontact() {
        RequestGetContact requestGetContact = new RequestGetContact
                .Builder()
                .build();
        chatController.getContact(requestGetContact, null);
    }

    @Override
    public void onDeleteMessage(ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        System.out.println("in delete");
    }

    @Override
    public void onEditMessage(ChatResponse<ResultNewMessage> response) {

    }

    @Override
    public void onCreateThread(ChatResponse<ResultThread> outPutThread) {

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
    }

    @Override
    public void onGetSeenMessage(ChatResponse<ResultMessage> response) {

    }

    @Override
    public void onGetDeliverMessage(ChatResponse<ResultMessage> chatResponse) {

    }

    @Override
    public void onAddContact(ChatResponse<ResultAddContact> chatResponse) {

    }

    @Override
    public void onGetContacts(ChatResponse<ResultContact> response) {


    }

    @Override
    public void onError(ErrorOutPut error) {
        System.out.println(error);
    }


}
