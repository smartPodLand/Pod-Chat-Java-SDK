package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.chat.ChatHandler;
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

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "82ce9787c7334bdb9c1d8fe1945fe2b7";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";
    static String queueServer = "172.16.0.248";
    static String queuePort = "61616";
    static String queueInput = "queue-in-amjadi-stomp";
    static String queueOutput = "queue-out-amjadi-stomp";
    static String queueUserName = "root";
    static String queuePassword = "zalzalak";
    static ChatController chatController;
    Gson gson = new Gson();

    void init() {
        chatController = new ChatController(this);
        try {

            RequestConnect requestConnect = new RequestConnect
                    .Builder(queueServer, queuePort, queueInput, queueOutput, queueUserName, queuePassword, serverName, token, ssoHost, platformHost, fileServer)
                    .typeCode("default")
                    .build();

            chatController.connect(requestConnect);

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
        //leaveThread();
        // removeParticipant();
        //addParticipant();
        // getParticipant();
        // replyFileMessage();
//        sendFileMessage();
        //    uploadImage();
        //   uploadFile();
        // addContact();
        getHistory();
        // getcontact();

        // createThread();


        // getParticipant();
     /*   replyMessage();
        editMessage();
        forwardMessage();

        deleteMultipleMessage();*/

        //getThreads();

        //  sendMessage();
        // sendMessage();


        //   createThreadWithMessage();
        //createThreadWithMessage();

        //  deleteMessage();

        // updateContact();
        // removeContact();


    }

    private void leaveThread() {
        RequestLeaveThread leaveThread = new RequestLeaveThread
                .Builder(5781)
                .build();

        chatController.leaveThread(leaveThread, null);
    }

    private void removeParticipant() {
        RequestRemoveParticipants requestRemoveParticipants = new RequestRemoveParticipants
                .Builder(5781, new ArrayList<Long>() {{
            add(4781l);
        }})
                .build();

        chatController.removeParticipants(requestRemoveParticipants, new ChatHandler() {
            @Override
            public void onRemoveParticipants(String uniqueId) {
                super.onRemoveParticipants("remove participant: " + uniqueId);

                System.out.println(uniqueId);
            }
        });
    }

    private void sendFileMessage() {
        RequestFileMessage requestFileMessage = new RequestFileMessage
                .Builder(5461, "C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .description("this is test image")
                .xC(0)
                .yC(0)
                .hC(100)
                .wC(200)
                .build();
//
//        RequestFileMessage requestFileMessage = new RequestFileMessage
//                .Builder(5461, "F:\\models.txt")
//                .description("this is test image")
//                .build();

        chatController.uploadFileMessage(requestFileMessage, null);
    }


    private void replyFileMessage() {
  /*      RequestReplyFileMessage requestReplyFileMessage = new RequestReplyFileMessage
                .Builder("this is test", 5461, 47921, "C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .xC(0)
                .yC(0)
                .hC(100)
                .wC(200)
                .build();*/


        RequestReplyFileMessage requestReplyFileMessage = new RequestReplyFileMessage
                .Builder("this is test", 5461, 47921, "F:\\models.txt")
                .xC(0)
                .yC(0)
                .hC(100)
                .wC(200)
                .build();
        chatController.replyFileMessage(requestReplyFileMessage, null);
    }

    private void uploadImage() {
        RequestUploadImage requestUploadImage = new RequestUploadImage
                .Builder("C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .xC(0)
                .yC(0)
                .hC(200)
                .wC(100)
                .build();

        chatController.uploadImage(requestUploadImage);
    }

    private void uploadFile() {
        RequestUploadFile requestUploadFile = new RequestUploadFile
                .Builder("F:\\models.txt")
                .build();

        chatController.uploadFile(requestUploadFile);


    }

    private void getHistory() {
        RequestGetHistory requestGetHistory = new RequestGetHistory
                .Builder(5461)
                .build();

        chatController.getHistory(requestGetHistory, null);

     /*   RequestGetHistory requestGetHistory1 = new RequestGetHistory
                .Builder(5461)
                .build();

        chatController.getHistory(requestGetHistory1, null);*/
    }

    private void removeContact() {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(15141)
                .build();

        chatController.removeContact(requestRemoveContact);
    }

    private void updateContact() {
        RequestUpdateContact requestUpdateContact = new RequestUpdateContact
                .Builder(13882, "زهرا", "مظلوم", "09156452709", "gdimi@gmail.com")
                .build();

        chatController.updateContact(requestUpdateContact);
    }

    private void deleteMessage() {
        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(47617L);
        }})
                .build();

        chatController.deleteMessage(deleteMessage, null);
    }

    private void createThreadWithMessage() {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("hello world")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(4781);
        invitee.setIdType(InviteType.TO_BE_USER_ID);

        Invitee invitee1 = new Invitee();
        invitee1.setId(1181);
        invitee1.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.NORMAL, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);

      /*  RequestThreadInnerMessage requestThreadInnerMessage1 = new RequestThreadInnerMessage
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
    }

    private void editMessage() {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hiii", 47602)
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
                .threadIds(new ArrayList<Integer>() {{
                    add(4982);
                }})
                .build();

        chatController.getThreads(requestThread, null);
    }

    private void deleteMultipleMessage() {
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(47616l);
            add(47617l);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMultipleMessage(requestDeleteMessage, null);
    }

    private void forwardMessage() {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(3042, new ArrayList<Long>() {{
            add(47602l);
            add(47601l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);
    }

    private void getParticipant() {
        RequestThreadParticipant threadParticipant = new RequestThreadParticipant
                .Builder(5781)
                .build();

        chatController.getThreadParticipant(threadParticipant, new ChatHandler() {
            @Override
            public void onGetThreadParticipant(String uniqueId) {
                super.onGetThreadParticipant(uniqueId);
                System.out.println("get participant: " + uniqueId);
            }
        });
    }

    private void addParticipant() {
        RequestAddParticipants addParticipants = new RequestAddParticipants
                .Builder(5821, new ArrayList<Long>() {{
            add(15141l);
        }})
                .build();

        chatController.addParticipants(addParticipants, null);
    }

    private void replyMessage() {
        RequestReplyMessage requestReplyMessage = new RequestReplyMessage
                .Builder("hi", 5461, 47601)
                .build();

        chatController.replyMessage(requestReplyMessage, null);
    }

    private void createThread() {
       /* Invitee[] invitees = new Invitee[2];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee.setId(13812);

        Invitee invitee1 = new Invitee();
        invitee1.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee1.setId(13882);

        invitees[0] = invitee;
        invitees[1] = invitee1;

        chatController.createThread(ThreadType.PUBLIC_GROUP, invitees, "sendMessage", "", "", "");*/

        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_ID);
        invitee.setId(4781);

        invitees[0] = invitee;

        chatController.createThread(ThreadType.NORMAL, invitees, "sendMessage", "", "", "");
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
    public void onGetThreadParticipant(ChatResponse<ResultParticipant> response) {

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
