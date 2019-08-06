import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestThread;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class GetThreadTest implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "b9ee4c528c284cb8a56356b7ee45d068";
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


        Mockito.verify(chatContract).onState("OPEN");
        Mockito.verify(chatContract).onState("ASYNC_READY");

        java.lang.Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertTrue(!argument.getValue().hasError());

    }

    @Test
    @Order(2)
    void getThreadList() throws InterruptedException {
        RequestThread requestThread = new RequestThread.Builder().build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetThreadList(argument.capture());

        ChatResponse chatResponse = (ChatResponse) argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    @BeforeAll
    void reset() {
        Mockito.reset();
    }

    @Test
    @Order(2)
    void getThreadListWithPagination() throws InterruptedException {
        RequestThread requestThread = new RequestThread
                .Builder()
                .offset(0)
                .count(10)
                .build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetThreadList(argument.capture());

        ChatResponse chatResponse = (ChatResponse) argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }
}
