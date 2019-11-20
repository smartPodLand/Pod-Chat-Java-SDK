import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestDeliveredMessageList;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class GetDeliveredList implements ChatContract.view {
    long messageId = 52347;

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
    void getDeliveredList() throws InterruptedException {

        RequestDeliveredMessageList requestDeliveredMessageList = new RequestDeliveredMessageList
                .Builder(messageId)
                .build();

        chatController.deliveredMessageList(requestDeliveredMessageList);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).OnDeliveredMessageList(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

    @Test
    @Order(2)
    void getDeliveredListWithInvalidMessageId() throws InterruptedException {

        RequestDeliveredMessageList requestDeliveredMessageList = new RequestDeliveredMessageList
                .Builder(123)
                .build();

        chatController.deliveredMessageList(requestDeliveredMessageList);

        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        ErrorOutPut errorOutPut = argument.getValue();

        Assertions.assertTrue(errorOutPut.isHasError());
    }

}
