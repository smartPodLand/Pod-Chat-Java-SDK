import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultRemoveContact;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestGetContact;
import podChat.requestobject.RequestRemoveContact;

/**
 * Created By Khojasteh on 7/27/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatMain implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "a7e05beb7a6741359349a35dea54f2da";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";

    Gson gson = new Gson();

    @BeforeAll
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Order(1)
    public void connect() throws InterruptedException {
        try {
            chatController = new ChatController(chatContract);
            chatController.connect("", "", serverName, token, ssoHost, platformHost, fileServer, "default");
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(4101);
        userInfo.setName("فاطمه خجسته");
        userInfo.setCellphoneNumber("09151242904");
        userInfo.setSendEnable(true);
        userInfo.setReceiveEnable(true);


        Mockito.verify(chatContract).onState("OPEN");
        Mockito.verify(chatContract).onState("ASYNC_READY");

        java.lang.Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    /*@Test
    @Order(2)
    void createThread() throws InterruptedException {

        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId(9156452709l);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onCreateThread(argument.capture());

        ResultThread resultThread = (ResultThread) argument.getValue().getResult();

        Assertions.assertEquals(5461L, resultThread.getThread().getId());


    }*/

/*
    @Test
    @Order(3)
    void sendMessage() throws InterruptedException {

        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread, null);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onSentMessage(argument.capture());

        ResultMessage resultMessage = (ResultMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultMessage.getMessageId()));

    }

    @Test
    @Order(4)
    void seenMessage() throws InterruptedException {
        Thread.sleep(3000);
        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetDeliverMessage(argument.capture());

        ResultMessage resultMessage = (ResultMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultMessage.getMessageId()));
    }

    @Test
    @Order(4)
    void deliveredMessage() throws InterruptedException {
        Thread.sleep(3000);
        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetSeenMessage(argument.capture());

        ResultMessage resultMessage = (ResultMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultMessage.getMessageId()));
    }

    @Test
    @Order(2)
    void getThreadList() throws InterruptedException {
        RequestThread requestThread = new RequestThread.Builder().build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetThreadList(argument.capture());

        ChatResponse chatResponse = (ChatResponse) argument.getValue();

        Assertions.assertTrue(!chatResponse.isHasError());

    }

    @Test
    @Order(2)
    void addContact() throws InterruptedException {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .lastName("مظلوم")
                .build();
        chatController.addContact(requestAddContact);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onAddContact(argument.capture());

        ChatResponse chatResponse = (ChatResponse) argument.getValue();

        Assertions.assertTrue(!chatResponse.isHasError());

    }*/

    @Test
    @Order(3)
    void getContact() throws InterruptedException {
        RequestGetContact requestGetContact = new RequestGetContact
                .Builder()
                .count(3)
                .offset(0)
                .build();
        chatController.getContact(requestGetContact, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetContacts(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.isHasError());

    }

    @Test
    @Order(2)
    void removeContact() throws InterruptedException {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(13812)
                .build();

        chatController.removeContact(requestRemoveContact);

        Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onRemoveContact(argument.capture());

        ResultRemoveContact resultRemoveContact = (ResultRemoveContact) argument.getValue().getResult();

        Assertions.assertTrue(resultRemoveContact.isResult());
    }

   /* @Test
    @Order(2)
    void removeContactError() throws InterruptedException {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(4101)
                .build();

        chatController.removeContact(requestRemoveContact);

        Thread.sleep(3000);


        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        ErrorOutPut errorOutPut = argument.getValue();

        Assertions.assertTrue(errorOutPut.isHasError());
    }*/
}
