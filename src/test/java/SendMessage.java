import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultMessage;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestMessage;
import podChat.util.Util;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class SendMessage implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "4979b53a7bf64dd8a9a6b7d461fba92e";
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

    //Check error... The thread id does not exist....
    @Test
    @Order(2)
    void sendMessageError() throws InterruptedException {

        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5462)
                .build();

        chatController.sendTextMessage(requestThread, null);

        Thread.sleep(5000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        Assertions.assertTrue(argument.getValue().isHasError());

    }


    //Only if client is online, the test will passed
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
    }


}
