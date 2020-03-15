import Constant.Constant;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestPinMessage;
import podChat.requestobject.RequestPinThread;
import sun.misc.Request;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PinUnPinMessage implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);
    private long messageId = 5461;


    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Order(1)
    void connect() throws InterruptedException {
        try {
            chatController = new ChatController(chatContract);

            RequestConnect requestConnect = new RequestConnect
                    .Builder(new ArrayList<String>() {{
                add(Constant.socketAddress);
            }},
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
    void pin() throws InterruptedException {

        RequestPinMessage requestPinMessage = new RequestPinMessage
                .Builder(messageId)
                .build();

        chatController.pinMessage(requestPinMessage);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onPinMessage(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertFalse(chatResponse.hasError());
    }

    @Test
    @Order(3)
    void unPin() throws InterruptedException {

        RequestPinMessage requestPinMessage = new RequestPinMessage
                .Builder(messageId)
                .build();

        chatController.unPinMessage(requestPinMessage);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onUnPinMessage(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertFalse(chatResponse.hasError());
    }

}
