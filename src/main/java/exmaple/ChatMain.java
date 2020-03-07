package exmaple;

import com.google.gson.Gson;
import exception.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import podAsync.Async;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.MessageVO;
import podChat.mainmodel.RequestSearchContact;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.model.*;
import podChat.requestobject.*;
import podChat.util.InviteType;
import podChat.util.RoleType;
import podChat.util.TextMessageType;
import podChat.util.ThreadType;

import javax.print.attribute.standard.RequestingUserName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By Khojasteh on 7/27/2019
 */
public class ChatMain implements ChatContract.view {
//    public static String platformHost = "https://sandbox.pod.ir:8043";
//    public static String token = "c0ec3f4e05cc4f54b81513881af1c827";
//    public static String ssoHost = "https://accounts.pod.ir";
//    public static String fileServer = "https://core.pod.ir";
//    public static String serverName = "chat-server";
//    public static String queueServer = "10.56.16.25";
//    public static String queuePort = "61616";
//    public static String queueInput = "queue-in-amjadi-stomp";
//    public static String queueOutput = "queue-out-amjadi-stomp";
//    public static String queueUserName = "root";
//    public static String queuePassword = "zalzalak";
//    public static Long chatId = 4101L;

    public static String platformHost = "https://sandbox.pod.ir:8043";
    public static String token = "bebc31c4ead6458c90b607496dae25c6";
    public static String ssoHost = "https://accounts.pod.ir";
    public static String fileServer = "https://core.pod.ir";
    public static String serverName = "chatlocal";
    public static String queueServer = "192.168.112.23";
    public static String queuePort = "61616";
    public static String queueInput = "queue-in-integration";
    public static String queueOutput = "queue-out-integration";
    public static String queueUserName = "root";
    public static String queuePassword = "j]Bm0RU8gLhbPUG";
    public static Long chatId = 4101L;
    static ChatController chatController;


    private static Logger logger = LogManager.getLogger(Async.class);
    Gson gson = new Gson();

    void init() {
        chatController = new ChatController(this);
        try {

            RequestConnect requestConnect = new RequestConnect
                    .Builder(queueServer,
                    queuePort,
                    queueInput,
                    queueOutput,
                    queueUserName,
                    queuePassword,
                    serverName,
                    token,
                    ssoHost,
                    platformHost,
                    fileServer,
                    4101L)
                    .build();

            chatController.connect(requestConnect);

//            addContact();
//            Thread.sleep(2000);
//            getcontact();
//            Thread.sleep(2000);
//            removeContact();
//            Thread.sleep(2000);
//            getcontact();
//            updateContact();
//            Thread.sleep(2000);
//            getcontact();
//            searchContact();


//            createThread();
//            getThreads();
//            sendMessage();

//            deleteMultipleMessage();
//            deleteMessage();
//            editMessage();
//            forwardMessage();

//            addParticipant();
//            removeParticipant();
//            Thread.sleep(2000);
//            getParticipant();
//chatController.getUserInfo();
//            createThreadWithMessage();
//            createThreadWithFileMessage();
            createPublicGroupOrChannelThread();
//            isNameAvailable();

//            leaveThread();
//            replyMessage();
//            replyFileMessage(); /// check it

//            Thread.sleep(2000);

//            getDeliveryList();
//            getSeenList();

//            mute();
//            Thread.sleep(2000);
//            unmute();

//            getHistory();
//            clearHistory();

//            block();
//            unblock();
//            Thread.sleep(2000);
//            getBlockList();


/*
            addAdmin();
            Thread.sleep(2000);
            getAdmin();
            Thread.sleep(2000);
            removeAdmin();
            Thread.sleep(2000);
            getAdmin();*/


//            pinThread();
//            Thread.sleep(2000);
//            getThreads();


//            unPinThread();
//            Thread.sleep(2000);


//            chatController.setToke("e1559e7981b94917a053b32ef36c334a");
//            getcontact();
//
//
//            chatController.setToke("e92a45d5abb54194bfe8f6e6d915189a");
//            getcontact();
        /*    addAuditor();
            Thread.sleep(2000);
            getParticipant();
            Thread.sleep(2000);
            removeAuditor();
            Thread.sleep(2000);
            getParticipant();*/

//            interactiveMessage();

//            uploadImage();  //checkit
//            uploadFile();    ///checkit

//            spam();

//            Thread.sleep(2000);
//            setAuditorRole();
//            getParticipant();

//            pinMessage();
//            Thread.sleep(2000);
//            getThreads();
            Thread.sleep(2000);
//            unPinMessage();
//            chatController.getUserInfo();

//            getCurrentUserRoles();

//            getMentionedList();

//            updateProfile();


//            sendFileMessage();


        } catch (ConnectionException | InterruptedException e) {
            System.out.println(e);
        }


    }

    /*********************************************************************
     *                             PROFILE                                 *
     *********************************************************************/

    void updateProfile() {
        RequestUpdateProfile requestUpdateProfile = new RequestUpdateProfile
                .Builder()
                .metadata("test2")
                .bio("hello")
                .build();

        chatController.updateProfile(requestUpdateProfile);

        chatController.getUserInfo();
    }

    /*********************************************************************
     *                             ADMIN                                 *
     *********************************************************************/

    /**
     * set role
     */


    void addAdmin() {
        RequestRole requestRole = new RequestRole();
        requestRole.setId(4781);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.THREAD_ADMIN);
        }});


        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAdmin requestSetAdmin = new RequestSetAdmin
                .Builder(5941, requestRoleArrayList)
                .build();

        chatController.addAdmin(requestSetAdmin);

    }

    void addAuditor() {
        RequestRole requestRole = new RequestRole();
        requestRole.setId(1181);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.POST_CHANNEL_MESSAGE);
            add(RoleType.READ_THREAD);
        }});

        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAuditor requestSetAuditor = new RequestSetAuditor
                .Builder(5461, requestRoleArrayList)
                .build();

        chatController.addAuditor(requestSetAuditor);

    }


    void getCurrentUserRoles() {
        RequestCurrentUserRoles requestCurrentUserRoles = new RequestCurrentUserRoles
                .Builder(6629)
                .build();


        chatController.getCurrentUserRoles(requestCurrentUserRoles);
    }

    /**
     * delete role
     */
    void removeAdmin() {
        RequestRole requestRole = new RequestRole();
        requestRole.setId(4781);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.THREAD_ADMIN);
        }});

        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAdmin requestSetAdmin = new RequestSetAdmin
                .Builder(5941, requestRoleArrayList)
                .build();

        chatController.removeAdmin(requestSetAdmin);

    }


    void removeAuditor() {
        RequestRole requestRole = new RequestRole();
        requestRole.setId(1181);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.POST_CHANNEL_MESSAGE);
            add(RoleType.READ_THREAD);
        }});

        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAuditor requestSetAuditor = new RequestSetAuditor
                .Builder(5461, requestRoleArrayList)
                .build();


        chatController.removeAuditor(requestSetAuditor);

    }


    void getAdmin() {
        RequestGetAdmin requestGetAdmin = new RequestGetAdmin
                .Builder(5941)
                .build();

        chatController.getAdminList(requestGetAdmin);
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
                .cellphoneNumber("09157770684")
                .lastName("خیرخواه")
                .firstName("فرهاد")
                .build();
        Gson gson = new Gson();
        System.out.println(gson.toJson(requestAddContact));
        chatController.addContact(requestAddContact);
    }

    /**
     * remove contact
     */
    private void removeContact() {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(20714)
                .build();

        chatController.removeContact(requestRemoveContact);
    }

    /**
     * update contact
     */
    private void updateContact() {
        RequestUpdateContact requestUpdateContact = new RequestUpdateContact
                .Builder(13882, "زهرا", "مظلوم", "09156452709", "zahra@gmail.com")
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
        chatController.getContact(requestGetContact);
    }

    /**
     * block
     */
    private void block() {
        RequestBlock requestBlock = new RequestBlock
                .Builder()
                .contactId(13882)
                .build();

        chatController.block(requestBlock);
    }

    /**
     * unblock
     */
    private void unblock() {
        RequestUnBlock requestUnBlock = new RequestUnBlock
                .Builder()
//                (6061)
                .blockId(2222)
                .build();

        chatController.unBlock(requestUnBlock);
    }

    /**
     * block list
     */
    private void getBlockList() {
        RequestBlockList requestBlockList = new RequestBlockList
                .Builder()
                .build();

        chatController.getBlockList(requestBlockList);
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
    /*    RequestGetHistory requestGetHistory = new RequestGetHistory
                .Builder(5461)
                .build();

        chatController.getHistory(requestGetHistory);*/
        RequestGetHistory requestGetHistory2 = new RequestGetHistory
                .Builder(7109)
//                .uniqueIds(new String[]{"a98d00af-6cb7-4174-a82a-a8ec68af0bb1"})
                .build();

        chatController.getHistory(requestGetHistory2);

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
                .Builder(5941)
                .build();

        chatController.leaveThread(leaveThread);
    }

    /**
     * delete message
     */
    private void deleteMessage() {
        RequestDeleteMessage deleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(86368L);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMessage(deleteMessage);
    }

    /**
     * create thread with message
     */
    private void createThreadWithMessage() {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder("hello world")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId("09151242904");
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);

//        Invitee invitee1 = new Invitee();
//        invitee1.setId("09156967335");
//        invitee1.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
//        Invitee invitee1 = new Invitee();
//        invitee1.setId(1181);
//        invitee1.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThreadWithMessage requestCreateThreadWithMessage = new RequestCreateThreadWithMessage
                .Builder(ThreadType.NORMAL, new ArrayList<Invitee>() {{
            add(invitee);
        }},
                TextMessageType.TEXT)
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThreadWithMessage);

    }

    private void getMentionedList() {
        RequestGetMentionedList requestGetMentionedList = new RequestGetMentionedList
                .Builder(7108)
//                .allMentioned(true)
                .unreadMentioned(true)
                .build();


        chatController.getMentionedList(requestGetMentionedList);
    }


    /**
     * create thread with file message
     */

    private void createThreadWithFileMessage() {
        RequestUploadImage requestUploadFile = new RequestUploadImage
                .Builder("D:\\download.jpg")
                .hC(200)
                .build();

        Invitee invitee = new Invitee();
        invitee.setId("09900449643");
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);

        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder("hellllllllllllllo")
                .build();


        RequestCreateThreadWithFile requestCreateThreadWithFile = new RequestCreateThreadWithFile
                .Builder(ThreadType.NORMAL, new ArrayList<Invitee>() {{
            add(invitee);
        }}, requestUploadFile,
                TextMessageType.PICTURE)
                .message(requestThreadInnerMessage)
                .description("tesssssssssssst")
                .build();


        chatController.createThreadWithFileMessage(requestCreateThreadWithFile);

    }

    /**
     * edit message
     */
    private void editMessage() {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hiii", 72301)
                .build();
        chatController.editMessage(requestEditMessage);
    }

    /**
     * send message
     */
    private void sendMessage() {
        RequestMessage requestThread = new RequestMessage
                .Builder("hi", 1, TextMessageType.TEXT)
                .build();

        chatController.sendTextMessage(requestThread);
    }

    /**
     * get thread
     */
    private void getThreads() {
        RequestThread requestThread = new RequestThread
                .Builder()
//                .newMessages()
                .build();

        chatController.getThreads(requestThread);
    }

    /**
     * delete multiple message
     */
    private void deleteMultipleMessage() {
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(86370L);
            add(86369L);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMultipleMessage(requestDeleteMessage);
    }

    /**
     * forward message
     */
    private void forwardMessage() {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(6362, new ArrayList<Long>() {{
            add(72301L);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);
    }

    /**
     * reply message
     */
    private void replyMessage() {
        RequestReplyMessage requestReplyMessage = new RequestReplyMessage
                .Builder("hi", 6362, 72301, TextMessageType.TEXT)
                .build();

        chatController.replyMessage(requestReplyMessage);
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
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId("13812");

//        Invitee invitee2 = new Invitee();
//        invitee2.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
//        invitee2.setId(13812);

        invitees[0] = invitee;
//        invitees[1] = invitee2;

        chatController.createThread(ThreadType.NORMAL, invitees, "sendMessage", "", "", "", "default");
    }

    /**
     * create public group or channel thread
     */

    private void createPublicGroupOrChannelThread() {
        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId("09151242904");
        invitees[0] = invitee;

        RequestCreatePublicGroupOrChannelThread requestCreateThread = new RequestCreatePublicGroupOrChannelThread
                .Builder(ThreadType.PUBLIC_GROUP, new ArrayList<Invitee>() {{
            add(invitee);
        }}, "test")
                .build();

        chatController.createThread(requestCreateThread);
    }

    /**
     * seen message list
     */
    private void getSeenList() {
        RequestSeenMessageList requestSeenMessageList = new RequestSeenMessageList
                .Builder(55216)
                .build();

        chatController.seenMessageList(requestSeenMessageList);
    }

    /**
     * delivery message list
     */
    private void getDeliveryList() {
        RequestDeliveredMessageList requestDeliveredMessageList = new RequestDeliveredMessageList
                .Builder(55216)
                .build();

        chatController.deliveredMessageList(requestDeliveredMessageList);
    }

    /**
     * mute thread
     */
    private void mute() {
        RequestMuteThread requestMuteThread = new RequestMuteThread
                .Builder(4982)
                .build();

        chatController.muteThread(requestMuteThread);
    }

    /**
     * unmute thread
     */
    private void unmute() {
        RequestMuteThread requestMuteThread = new RequestMuteThread
                .Builder(4982)
                .build();

        chatController.unMuteThread(requestMuteThread);
    }

    /**
     * spam thread
     */

    private void spam() {
        RequestSpam requestSpam = new RequestSpam
                .Builder(6450)
                .build();

        chatController.spam(requestSpam);
    }


    /**
     * bot message
     */

    private void interactiveMessage() {
        RequestInteract requestInteract = new RequestInteract
                .Builder(56249, "OK")
                .build();

        chatController.interactiveMessage(requestInteract);
    }


    private void pinThread() {
        RequestPinThread requestPinThread = new RequestPinThread
                .Builder(5461)
                .build();

        chatController.pinThread(requestPinThread);
    }

    private void unPinThread() {
        RequestPinThread requestPinThread = new RequestPinThread
                .Builder(5461)
                .build();

        chatController.unPinThread(requestPinThread);
    }


    private void pinMessage() {
        RequestPinMessage requestPinMessage = new RequestPinMessage
                .Builder(88287L)
                .build();

        chatController.pinMessage(requestPinMessage);
    }

    private void unPinMessage() {
        RequestPinMessage requestPinMessage = new RequestPinMessage
                .Builder(75293L)
                .build();

        chatController.unPinMessage(requestPinMessage);
    }


    private void isNameAvailable() {
        RequestIsNameAvailable requestIsNameAvailable = new RequestIsNameAvailable
                .Builder("test")
                .build();

        chatController.isNameAvailable(requestIsNameAvailable);
    }

    /******************************************************************
     *                           PARTICIPANT                          *
     * ****************************************************************/

    /**
     * remove participant
     */
    private void removeParticipant() {
        RequestRemoveParticipants requestRemoveParticipants = new RequestRemoveParticipants
                .Builder(5941, new ArrayList<Long>() {{
            add(1181L);
        }})
                .build();

        chatController.removeParticipants(requestRemoveParticipants);
    }

    /**
     * get participant
     */
    private void getParticipant() {
        RequestThreadParticipant threadParticipant = new RequestThreadParticipant
                .Builder(6630)
                .build();

        chatController.getThreadParticipant(threadParticipant);
    }

    /**
     * add participant
     */
    private void addParticipant() {
        RequestAddParticipants addParticipants = RequestAddParticipants
                .newBuilder()
                .threadId(6630L)
                .withUsername("fkheirkhah")
                .build();

        chatController.addParticipants(addParticipants);
    }

    /******************************************************************
     *                           FIlE                                 *
     * ****************************************************************/

    /**
     * send file message
     */
    private void sendFileMessage() {
        RequestFileMessage requestFileMessage = new RequestFileMessage
                .Builder(5461, "D:\\chat.txt", TextMessageType.FILE)
                .description("this is test")
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
                .Builder("this is test",
                5461,
                55202,
                "C:\\Users\\fanap_soft\\Desktop\\chat output\\b.jpg"
                , TextMessageType.PICTURE)
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
                .Builder("D:\\download.jpg")
                .build();
        System.out.println(gson.toJson(requestUploadImage));
        chatController.uploadImage(requestUploadImage);
    }

    /**
     * upload file
     */
    private void uploadFile() {
        RequestUploadFile requestUploadFile = new RequestUploadFile
                .Builder("D:\\Music.rar")
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
            if (!messageVO.getSeen() && messageVO.getParticipant().getId() != 4101) {

                chatController.seenMessage(messageVO.getId(), messageVO.getParticipant().getId());
            }
        }

    }

    @Override
    public void onCreateThread(ChatResponse<ResultThread> outPutThread) {
        System.out.println("");
    }

    @Override
    public void onError(ErrorOutPut error) {
//        if (error.getErrorCode() == 21) {
//            Scanner myObj = new Scanner(System.in);
//            System.out.println("Enter token");
//
//            String token = myObj.nextLine();
//
//            chatController.setToke(token);
//            getThreads();
//
//        }
    }

    @Override
    public void onSearchContact(ChatResponse<ResultContact> chatResponse) {

        System.out.println(gson.toJson(chatResponse));
    }

    @Override
    public void OnInteractMessage(ChatResponse<ResultInteractMessage> chatResponse) {
        System.out.println("helllo");
    }

    @Override
    public void onGetContacts(ChatResponse<ResultContact> response) {
        System.out.println(response);
    }

    @Override
    public void onAddContact(ChatResponse<ResultAddContact> chatResponse) {
        System.out.println(gson.toJson(chatResponse));
    }

    @Override
    public void onSetRole(ChatResponse<ResultSetRole> chatResponse) {
        System.out.println("helllo");
    }


    @Override
    public void onRemoveRole(ChatResponse<ResultSetRole> chatResponse) {
        System.out.println("helllo");
    }

    @Override
    public void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {
        System.out.println("d");
    }

}
