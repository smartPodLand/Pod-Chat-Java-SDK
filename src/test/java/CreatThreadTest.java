import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultUserInfo;
import podChat.util.InviteType;
import podChat.util.ThreadType;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class CreatThreadTest implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "f19edb43427f4946a40a960828224d9e";
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

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void createThreadWithPhone() throws InterruptedException {

        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CELLPHONE_NUMBER);
        invitee.setId(9156967335L);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());

        Assertions.assertTrue(!argument.getValue().hasError());


    }

    @Test
    @Order(2)
    void createThreadWithUserId() throws InterruptedException {
        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_ID);
        invitee.setId(1181);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());

        /// ResultThread resultThread = (ResultThread) argument.getValue().getResult();

        // Assertions.assertEquals(5461L, resultThread.getThread().getId());

        Assertions.assertTrue(!argument.getValue().hasError());

    }

    @Test
    @Order(2)
    void createThreadWithBadUserId() throws InterruptedException {
        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_SSO_ID);
        invitee.setId(1111111111);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        /// ResultThread resultThread = (ResultThread) argument.getValue().getResult();

        Assertions.assertTrue(argument.getValue().isHasError());


    }

    @Test
    @Order(2)
    void createThreadWithContactId() throws InterruptedException {
        Invitee[] invitees = new Invitee[1];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee.setId(13882);

        invitees[0] = invitee;
        chatController.createThread(0, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());

        Assertions.assertTrue(!argument.getValue().hasError());

    }

    @Test
    @Order(2)
    void createChannelGroup() throws InterruptedException {
        Invitee[] invitees = new Invitee[2];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee.setId(13882);

        Invitee invitee2 = new Invitee();
        invitee2.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee2.setId(13812);


        invitees[0] = invitee;
        invitees[1] = invitee2;

        chatController.createThread(ThreadType.OWNER_GROUP, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());

        Assertions.assertTrue(!argument.getValue().hasError());

    }


    @Test
    @Order(2)
    void createPublicGroup() throws InterruptedException {
        Invitee[] invitees = new Invitee[2];
        Invitee invitee = new Invitee();
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee.setId(13882);

        Invitee invitee2 = new Invitee();
        invitee2.setIdType(InviteType.TO_BE_USER_CONTACT_ID);
        invitee2.setId(13812);


        invitees[0] = invitee;
        invitees[1] = invitee2;

        chatController.createThread(ThreadType.PUBLIC_GROUP, invitees, "sendMessage", "", "", "");

        java.lang.Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());

        Assertions.assertTrue(!argument.getValue().hasError());

    }

}
