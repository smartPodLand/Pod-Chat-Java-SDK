import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.model.ChatResponse;
import podChat.model.ResultNewMessage;
import podChat.requestobject.RequestConnect;
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


    Gson gson = new Gson();

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @Order(1)
    public void connect() throws InterruptedException {
        try {
            chatController = new ChatController(chatContract);
            RequestConnect requestConnect = new RequestConnect
                    .Builder(Constant.queueServer, Constant.queuePort, Constant.queueInput, Constant.queueOutput, Constant.queueUserName, Constant.queuePassword, Constant.serverName, Constant.token, Constant.ssoHost, Constant.platformHost, Constant.fileServer)
                    .typeCode("default")
                    .build();

            chatController.connect(requestConnect);

            Thread.sleep(2000);

        } catch (ConnectionException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(2)
    void createThreadWithMessageUserId() throws InterruptedException {
        RequestThreadInnerMessage requestThreadInnerMessage = new RequestThreadInnerMessage
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
        chatController.createThreadWithMessage(requestCreateThread);

        Thread.sleep(10000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.times(1)).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.times(2)).onSentMessage(argument.capture());
        Mockito.verify(chatContract, Mockito.times(2)).onNewMessage(argument.capture());

    }


    @Test
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

        Mockito.verify(chatContract, Mockito.times(1)).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.times(1)).onSentMessage(argument.capture());
        Mockito.verify(chatContract, Mockito.times(1)).onNewMessage(argument.capture());

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

        Mockito.verify(chatContract, Mockito.times(1)).onCreateThread(argument.capture());
        Mockito.verify(chatContract, Mockito.times(1)).onSentMessage(argument.capture());
        Mockito.verify(chatContract, Mockito.times(1)).onNewMessage(argument.capture());


    }


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
