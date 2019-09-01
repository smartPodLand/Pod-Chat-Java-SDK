package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.chat.ChatHandler;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.MessageVO;
import podChat.mainmodel.RequestSearchContact;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.model.ChatResponse;
import podChat.model.ResultHistory;
import podChat.model.ResultNewMessage;
import podChat.model.ResultUserInfo;
import podChat.requestobject.*;
import podChat.util.InviteType;
import podChat.util.ThreadType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
    private static Logger logger = LogManager.getLogger(Async.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "323ff4dc07234f7a8642fb8fdb50b3de";
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


    @Override
    public void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {

//        leaveThread();
//        removeParticipant();
//        addParticipant();
//        getParticipant();
//        replyFileMessage();
//        sendFileMessage();
//        uploadImage();
//        uploadFile();
//        addContact();
        //    getHistory();
//        getcontact();
//        createThread();
//        getParticipant();
//        replyMessage();
//        editMessage();
//        forwardMessage();
//        deleteMultipleMessage();
//        getHistory();
//        clearHistory();
//        getHistory();
//        sendMessage();
//        sendMessage();
        createThreadWithMessage();
//        createThreadWithMessage();
//        deleteMessage();
//        updateContact();
//        removeContact();
//        clearHistory();
//        searchContact();
    }


    /******************************************************************
     *                           CONTACT                              *
     * ****************************************************************/

    /**
     * add contact
     */
    void addContact() {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .lastName("مظلوم")
                .build();
        chatController.addContact(requestAddContact);

    }

    /**
     * remove contact
     */
    private void removeContact() {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(15141)
                .build();

        chatController.removeContact(requestRemoveContact);
    }

    /**
     * update contact
     */
    private void updateContact() {
        RequestUpdateContact requestUpdateContact = new RequestUpdateContact
                .Builder(13882, "زهرا", "مظلوم", "09156452709", "gdimi@gmail.com")
                .build();

        chatController.updateContact(requestUpdateContact);
    }

    /**
     * search contact
     */
    private void searchContact() {
        RequestSearchContact searchContact = new RequestSearchContact
                .Builder()
                .cellphoneNumber("09156452709")
                .build();

        chatController.searchContact(searchContact);
    }

    /**
     * get contact
     */
    private void getcontact() {
        RequestGetContact requestGetContact = new RequestGetContact
                .Builder()
                .build();
        chatController.getContact(requestGetContact, null);
    }

    /******************************************************************
     *                           HISTORY                              *
     * ****************************************************************/

    /**
     * clear history
     */
    private void clearHistory() {
        RequestClearHistory requestClearHistory = new RequestClearHistory
                .Builder(5461)
                .build();

        chatController.clearHistory(requestClearHistory);
    }

    /**
     * get history
     */
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

    /******************************************************************
     *                           THREAD                               *
     * ****************************************************************/

    /**
     * leave thread
     */
    private void leaveThread() {
        RequestLeaveThread leaveThread = new RequestLeaveThread
                .Builder(5781)
                .build();

        chatController.leaveThread(leaveThread, null);
    }

    /**
     * delete message
     */
    private void deleteMessage() {
        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(47617L);
        }})
                .build();

        chatController.deleteMessage(deleteMessage, null);
    }

    /**
     * create thread with message
     */
    private void createThreadWithMessage() {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("hello world")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(4781);
        invitee.setIdType(InviteType.TO_BE_USER_ID);

//        Invitee invitee1 = new Invitee();
//        invitee1.setId(1181);
//        invitee1.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.NORMAL, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);

    }

    /**
     * edit message
     */
    private void editMessage() {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hiii", 47602)
                .build();
        chatController.editMessage(requestEditMessage, null);
    }

    /**
     * send message
     */
    private void sendMessage() {
        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread, null);
    }

    /**
     * get thread
     */
    private void getThreads() {
        RequestThread requestThread = new RequestThread
                .Builder()
                .threadIds(new ArrayList<Integer>() {{
                    add(5461);
                }})
                .build();

        chatController.getThreads(requestThread, null);
    }

    /**
     * delete multiple message
     */
    private void deleteMultipleMessage() {
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(49444l);
            add(49443l);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMultipleMessage(requestDeleteMessage, null);
    }

    /**
     * forward message
     */
    private void forwardMessage() {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(3042, new ArrayList<Long>() {{
            add(47602l);
            add(47601l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);
    }

    /**
     * reply message
     */
    private void replyMessage() {
        RequestReplyMessage requestReplyMessage = new RequestReplyMessage
                .Builder("hi", 5461, 47601)
                .build();

        chatController.replyMessage(requestReplyMessage, null);
    }

    /**
     * create thread
     */
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

    /******************************************************************
     *                           PARTICIPANT                          *
     * ****************************************************************/

    /**
     * remove participant
     */
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

    /**
     * get participant
     */
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

    /**
     * add participant
     */
    private void addParticipant() {
        RequestAddParticipants addParticipants = new RequestAddParticipants
                .Builder(5821, new ArrayList<Long>() {{
            add(15141l);
        }})
                .build();

        chatController.addParticipants(addParticipants, null);
    }

    /******************************************************************
     *                           FIlE                                 *
     * ****************************************************************/

    /**
     * send file message
     */
    private void sendFileMessage() {
        RequestFileMessage requestFileMessage = new RequestFileMessage
                .Builder(5461, "C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .description("this is test image")
                .xC(0)
                .yC(0)
                .hC(100)
                .wC(200)
                .build();

        chatController.uploadFileMessage(requestFileMessage, null);
    }

    /**
     * reply file message
     */
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

    /**
     * upload image
     */

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

    /**
     * upload file
     */
    private void uploadFile() {
        RequestUploadFile requestUploadFile = new RequestUploadFile
                .Builder("F:\\models.txt")
                .build();

        chatController.uploadFile(requestUploadFile);


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
    public void onGetThreadHistory(ChatResponse<ResultHistory> history) {
        List<MessageVO> messageVOS = history.getResult().getHistory();

        for (MessageVO messageVO : messageVOS) {
            if (!messageVO.isSeen() && messageVO.getParticipant().getId() != 4101) {

                chatController.seenMessage(messageVO.getId(), messageVO.getParticipant().getId(), new ChatHandler() {
                    @Override
                    public void onSeen(String uniqueId) {
                        super.onSeen(uniqueId);
                    }
                });
            }
        }

    }
}
