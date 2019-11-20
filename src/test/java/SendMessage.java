import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestMessage;

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
                    .Builder(Constant.queueServer,
                    Constant.queuePort,
                    Constant.queueInput,
                    Constant.queueOutput,
                    Constant.queueUserName,
                    Constant.queuePassword,
                    Constant.serverName,
                    Constant.token,
                    Constant.ssoHost,
                    Constant.platformHost,
                    Constant.fileServer,
                    Constant.chatId)
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
    void sendMessage() throws InterruptedException {

        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5461L)
                .build();

        chatController.sendTextMessage(requestThread);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.times(1)).onSentMessage(argument.capture());
        Mockito.verify(chatContract, Mockito.times(1)).onNewMessage(argument.capture());


    }

    //Check error... The thread id does not exist....
    @Test
    @Order(2)
    void sendMessageError() throws InterruptedException {

        RequestMessage requestThread = new RequestMessage
                .Builder("this is final test", 5462)
                .build();

        chatController.sendTextMessage(requestThread);

        Thread.sleep(5000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        Assertions.assertTrue(argument.getValue().isHasError());

    }


  /*  //Only if client is online, the test will passed
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
