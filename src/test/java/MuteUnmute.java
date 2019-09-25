import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestMuteThread;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MuteUnmute implements ChatContract.view {
    long threadId = 5461;


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
    void mute() throws InterruptedException {

        RequestMuteThread requestMuteThread = new RequestMuteThread
                .Builder(threadId)
                .build();

        chatController.muteThread(requestMuteThread);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onMuteThread(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

    @Test
    @Order(3)
    void unmute() throws InterruptedException {

        RequestMuteThread requestMuteThread = new RequestMuteThread
                .Builder(threadId)
                .build();

        chatController.unMuteThread(requestMuteThread);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onUnMuteThread(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

}
