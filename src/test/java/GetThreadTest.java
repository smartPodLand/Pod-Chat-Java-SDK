import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestThread;

import java.util.ArrayList;

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
    static String token = "ea3ad8b4101e42ddb6c513025a77d345";
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

    //Get Thread with Name
    @Test
    @Order(2)
    void getThreatWithName() throws InterruptedException {
        RequestThread requestThread = new RequestThread
                .Builder()
                .threadName("sendMessage")
                .build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetThreadList(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    //Get Thread with id
    @Test
    @Order(2)
    void getThreatWithId() throws InterruptedException {
        RequestThread requestThread = new RequestThread
                .Builder()
                .threadIds(new ArrayList<Integer>() {{
                    add(5462);
                }})
                .build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetThreadList(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    //Get Thread with partner's contact id
    @Test
    @Order(2)
    void getThreatWithPartnerContactId() throws InterruptedException {
        RequestThread requestThread = new RequestThread
                .Builder()
                .partnerCoreContactId(1181)
                .build();

        chatController.getThreads(requestThread, null);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetThreadList(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }
}
