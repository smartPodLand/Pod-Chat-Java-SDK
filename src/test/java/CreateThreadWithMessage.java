import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ResultNewMessage;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestCreateThread;
import podChat.util.InviteType;
import podChat.util.ThreadType;
import podChat.util.Util;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class CreateThreadWithMessage implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;

    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "a2925769a1dc487d81b49a40e5eddd53";
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

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void createThreadWithMessageUserId() throws InterruptedException {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(8508);
        invitee.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(0, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.atLeastOnce()).onNewMessage(argument.capture());

        ResultNewMessage resultNewMessage = (ResultNewMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultNewMessage.getThreadId()));
    }


    /*@Test
    @Order(3)
    void createThreadWithMessageContactId() throws InterruptedException {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(13882);
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(0, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.atLeastOnce()).onNewMessage(argument.capture());

        ResultNewMessage resultNewMessage = (ResultNewMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultNewMessage.getThreadId()));

    }

    @Test
    @Order(4)
    void createThreadWithMessageOwnerGroup() throws InterruptedException {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(13882);
        invitee.setId(13812);
        invitee.setIdType(InviteType.TO_BE_USER_CONTACT_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.OWNER_GROUP, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();
        chatController.createThreadWithMessage(requestCreateThread);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.atLeastOnce()).onNewMessage(argument.capture());

        ResultNewMessage resultNewMessage = (ResultNewMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultNewMessage.getThreadId()));

    }

    @Test
    @Order(5)
    void createThreadWithMessagePublicGroup() throws InterruptedException {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
                .Builder()
                .message("Hello zahraaaa")
                .build();

        Invitee invitee = new Invitee();
        invitee.setId(4781);
        invitee.setIdType(InviteType.TO_BE_USER_ID);

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.PUBLIC_GROUP, new ArrayList<Invitee>() {{
            add(invitee);
        }})
                .message(requestThreadInnerMessage)
                .build();

        chatController.createThreadWithMessage(requestCreateThread);

        Thread.sleep(3000);
        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.atLeastOnce()).onNewMessage(argument.capture());

        ResultNewMessage resultNewMessage = (ResultNewMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultNewMessage.getThreadId()));

    }*/

   /* //Only if client is online, the test will passed
    @Test
    @Order(3)
    void seenMessage() throws InterruptedException {
        Thread.sleep(3000);
        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetDeliverMessage(argument.capture());

        ResultMessage resultMessage = (ResultMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultMessage.getMessageId()));
    }

    //Only if client is online, the test will passed
    @Test
    @Order(3)
    void deliveredMessage() throws InterruptedException {
        Thread.sleep(3000);
        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetSeenMessage(argument.capture());

        ResultMessage resultMessage = (ResultMessage) argument.getValue().getResult();

        Assertions.assertTrue(!Util.isNullOrEmpty(resultMessage.getMessageId()));
    }*/


}
